/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.io.IOException;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class AddDeactivationServlet extends HttpServlet
{

   public AddDeactivationServlet()
   {
   }

   private static class Message implements ResultMessageAdapter
   {
      public ResultMessage Report = null;

      public Message(ResultMessage message)
      {
         this.Report = message;
      }

      @Override
      public void setResult(int result)
      {
         this.Report.setResult(result);
         if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to add a deactivation to the product license.");
         }
         else if(result == ResultMessage.Success)
         {
            this.Report.setType(ResultMessage.InfoMessage);
            this.Report.setMessage(
               "A deactivation was successfully added to the product license.");
         }
         else if(result == ResultMessage.DoesNotExist)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The specified product license does not exist.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to add a product license deactivation.<br />" +
               "Please log in as an administrator to do so.");
         }
      }

      @Override
      public void addException(Exception e)
      {
         this.Report.appendToMessage("<br />" + e);
      }

      @Override
      public void addMessage(String extra)
      {
         this.Report.appendToMessage(extra);
      }
   }

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
   {
      // Make sure the character decoding is correct.
      if(request.getCharacterEncoding() == null)
         request.setCharacterEncoding("UTF-8");

      // Get the parameters from the request.
      int licenseId = 0;
      try
      {
         licenseId = Integer.parseInt(request.getParameter("license"));
      }
      catch(Exception e)
      {
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      AddDeactivationServlet.Message message = new AddDeactivationServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin in order to add a new license group.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!user.isAdministrator() && !user.isDistributor())
         message.setResult(ResultMessage.NotAuthorized);
      else if(licenseId < 1)
      {
         message.setResult(ResultMessage.Failure);
         page = "/allproducts.jsp";
      }
      else
      {
         page = "/clientlicense.jsp?license=" + licenseId;
         Database database = new Database();
         try
         {
            database.connect();
            database.statement = database.connection.createStatement();
            try
            {
               // Get the deactivation information.
               int deactivations = 0;
               int maxDeactivations = 0;
               database.results = database.statement.executeQuery(
                  "SELECT deactivations, maxdeactivations FROM licenses " +
                  "WHERE userlicenseid = " + licenseId);
               boolean isValid = database.results.next();
               if(isValid)
               {
                  deactivations = database.results.getInt("deactivations");
                  maxDeactivations = database.results.getInt("maxdeactivations");
               }
               else
                  message.setResult(ResultMessage.DoesNotExist);

               database.results.close();
               if(isValid)
               {
                  // Give the user one more deactivation.
                  if(deactivations > maxDeactivations)
                     maxDeactivations = deactivations + 1;
                  else
                     maxDeactivations += 1;

                  database.statement.executeUpdate(
                     "UPDATE licenses SET maxdeactivations = " + maxDeactivations +
                     " WHERE userlicenseid = " + licenseId);
                  message.setResult(ResultMessage.Success);
               }
            }
            catch(SQLException sqle2)
            {
               message.setResult(ResultMessage.Failure);
               message.addException(sqle2);
            }
         }
         catch(SQLException sqle)
         {
            message.setResult(ResultMessage.ResourceError);
            message.addException(sqle);
         }
         catch(NamingException ne)
         {
            message.setResult(ResultMessage.ResourceError);
            message.addException(ne);
         }

         database.cleanup();
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

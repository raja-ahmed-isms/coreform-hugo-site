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



public class ResetLicenseSignatures extends HttpServlet
{

   public ResetLicenseSignatures()
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
         if(result == ResultMessage.Success)
         {
            this.Report.setType(ResultMessage.InfoMessage);
            this.Report.setMessage(
               "The license signatures have been successfully reset.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to reset the license signatures.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to reset the license signatures.");
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
      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ResetLicenseSignatures.Message message = new ResetLicenseSignatures.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is logged in.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      boolean isAdmin = user.isAdministrator();

      if(!LoginServlet.isUserLoggedIn(session))
         message.setResult(ResultMessage.LoginError);
      else if(!isAdmin)
         message.setResult(ResultMessage.NotAuthorized);
      else
      {
         page = "/allproducts.jsp";
         Database database = new Database();
         try
         {
            database.connect();
            database.statement = database.connection.createStatement();
            try
            {
               // Clear out the signatures for node-locked licenses.
               database.statement.executeUpdate(
                  "DELETE FROM activationfeatures WHERE activationid IN " +
                  "(SELECT activationid FROM licenseactivations WHERE userlicenseid IN" +
                  "(SELECT userlicenseid FROM licenses WHERE licensetype <> 2))");
               message.setResult(ResultMessage.Success);
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

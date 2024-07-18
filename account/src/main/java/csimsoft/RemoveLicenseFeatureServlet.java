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


public class RemoveLicenseFeatureServlet extends HttpServlet
{

   public RemoveLicenseFeatureServlet()
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
               "The license feature has been successfully removed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to remove the license feature.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to remove a license feature.<br />" +
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
      // Get the request parameters.
      int licenseId = 0;
      try
      {
         licenseId = Integer.parseInt(request.getParameter("license"));
      }
      catch(Exception e)
      {
      }

      int featureId = 0;
      try
      {
         featureId = Integer.parseInt(request.getParameter("feature"));
      }
      catch(Exception e)
      {
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      RemoveLicenseFeatureServlet.Message message =
         new RemoveLicenseFeatureServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to remove a feature.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in as an administrator to remove a feature.");
      }
      else if(!user.isAdministrator())
         message.setResult(ResultMessage.NotAuthorized);
      else if(featureId < 1 || licenseId < 1)
      {
         page = "/allproducts.jsp";
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />The given license or feature id is not valid.");
      }
      else
      {
         page = "/licensefeatures.jsp?license=" + licenseId;
         Database database = new Database();
         try
         {
            database.connect();
            database.connection.setAutoCommit(false);
            database.statement = database.connection.createStatement();
            try
            {
               // Remove the license feature from the database.
               database.statement.executeUpdate(
                  "DELETE FROM activationfeatures WHERE featureid = " + featureId +
                  " AND activationid IN (SELECT activationid FROM licenseactivations " +
                  "WHERE userlicenseid IN (SELECT userlicenseid FROM licenses " +
                  "WHERE licenseid = " + licenseId + "))");
               database.statement.executeUpdate(
                  "DELETE FROM userfeatures WHERE featureid = " + featureId +
                  " AND userlicenseid IN (SELECT userlicenseid FROM licenses " +
                  "WHERE licenseid = " + licenseId + ")");
               database.statement.executeUpdate(
                  "DELETE FROM licensefeatures WHERE featureid = " + featureId +
                  " AND licenseid = " + licenseId);

               database.connection.commit();
               message.setResult(ResultMessage.Success);
            }
            catch(SQLException sqle2)
            {
               database.connection.rollback();
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

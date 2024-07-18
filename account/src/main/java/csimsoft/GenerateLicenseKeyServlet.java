/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



public class GenerateLicenseKeyServlet extends HttpServlet
{

   public GenerateLicenseKeyServlet()
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
               "The license key has been successfully generated.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to generate a new license key.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to generate a license key.<br />" +
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

      // Get the parameters.
      int userLicenseId = 0;
      try
      {
         userLicenseId = Integer.parseInt(request.getParameter("license"));
      }
      catch(Exception e)
      {
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      GenerateLicenseKeyServlet.Message message = new GenerateLicenseKeyServlet.Message(
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
      else if(userLicenseId < 0)
         message.setResult(ResultMessage.Failure);
      else
      {
         page = "/clientlicense.jsp?license=" + userLicenseId;
         DatabaseLicenses licenses = new DatabaseLicenses();
         DatabaseOrders orders = new DatabaseOrders(licenses.database);
         try
         {
            licenses.database.connect();
            licenses.database.connection.setAutoCommit(false);
            licenses.database.statement = licenses.database.connection.createStatement();
            try
            {
               // Get the user id and license id.
               int userId = 0;
               int licenseId = 0;
               licenses.database.results = licenses.database.statement.executeQuery(
                  "SELECT userid, licenseid FROM licenses WHERE userlicenseid = " +
                  userLicenseId);
               if(licenses.database.results.next())
               {
                  userId = licenses.database.results.getInt("userid");
                  licenseId = licenses.database.results.getInt("licenseid");
               }

               licenses.database.results.close();

               // Generate a license key.
               String key = null;
               PreparedStatement check = licenses.database.connection.prepareStatement(
                  "SELECT userlicenseid FROM licenses WHERE licensekey = ?");
               licenses.database.addStatement(check);
               for(int i = 0; key == null && i < 3; ++i)
               {
                  key = Database.generateKey();

                  // Make sure the license key isn't a duplicate.
                  if(key != null)
                  {
                     check.setString(1, key);
                     licenses.database.results = check.executeQuery();
                     if(licenses.database.results.next())
                        key = null;

                     licenses.database.results.close();
                  }
               }

               if(key == null)
                  message.setResult(ResultMessage.Failure);
               else
               {
                  // Put the key in the database.
                  PreparedStatement statement =
                     licenses.database.connection.prepareStatement(
                     "UPDATE licenses SET licensekey = ? WHERE userlicenseid = ?");
                  licenses.database.addStatement(statement);

                  statement.setString(1, key);
                  statement.setInt(2, userLicenseId);
                  statement.executeUpdate();

                  // Add the license downloads to the user's list.
                  if(userId > 0 && licenseId > 0)
                     orders.addNewUserDownloads(userId, licenseId, userLicenseId);

                  licenses.database.connection.commit();
                  message.setResult(ResultMessage.Success);
               }
            }
            catch(SQLException sqle2)
            {
               licenses.database.connection.rollback();
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

         licenses.database.cleanup();
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

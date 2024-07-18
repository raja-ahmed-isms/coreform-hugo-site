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


public class ChangeUserLicenseKeyServlet extends HttpServlet
{

   public ChangeUserLicenseKeyServlet()
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
               "The license key has been successfully changed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to change the license key.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The given license key already exists.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to change the license key.");
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
      // Get the parameters.
      int licenseId = 0;
      try
      {
         licenseId = Integer.parseInt(request.getParameter("license"));
      }
      catch(Exception e)
      {
      }

      String licenseKey = request.getParameter("licensekey");
      if(licenseKey == null)
         licenseKey = "";
      else
         licenseKey = licenseKey.trim().replace("-", "");

      boolean isValid = licenseKey.matches("\\p{Alnum}*");

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ChangeUserLicenseKeyServlet.Message message =
         new ChangeUserLicenseKeyServlet.Message(
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
      else if(licenseId < 1)
         message.setResult(ResultMessage.Failure);
      else if(licenseKey.length() > 32)
      {
         page = "/licensekey.jsp?license=" + licenseId;
         message.setResult(ResultMessage.Failure);
         message.addMessage("<br /> The license key is too long.");
      }
      else if(!isValid)
      {
         page = "/licensekey.jsp?license=" + licenseId;
         message.setResult(ResultMessage.Failure);
         message.addMessage("<br /> Only ascii letters and numbers are allowed.");
      }
      else
      {
         page = "/clientlicense.jsp?license=" + licenseId;
         Database database = new Database();
         try
         {
            database.connect();
            PreparedStatement query = database.connection.prepareStatement(
               "SELECT userlicenseid FROM licenses WHERE licensekey = ?");
            database.addStatement(query);
            PreparedStatement update = database.connection.prepareStatement(
               "UPDATE licenses SET licensekey = ? WHERE userlicenseid = ?");
            database.addStatement(update);
            try
            {
               // Make sure the new license key isn't a duplicate.
               boolean duplicate = false;
               boolean noChange = false;
               if(!licenseKey.isEmpty())
               {
                  query.setString(1, licenseKey);
                  database.results = query.executeQuery();
                  duplicate = database.results.next();
                  if(duplicate)
                     noChange = licenseId == database.results.getInt("userlicenseid");

                  database.results.close();
               }

               if(noChange)
                  message.setResult(ResultMessage.Success);
               else if(duplicate)
                  message.setResult(ResultMessage.Exists);
               else
               {
                  // Set the new license key.
                  update.setString(1, licenseKey);
                  update.setInt(2, licenseId);
                  update.executeUpdate();

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

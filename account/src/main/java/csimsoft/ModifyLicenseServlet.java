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



public class ModifyLicenseServlet extends HttpServlet
{

   public ModifyLicenseServlet()
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
               "The license has been successfully modified.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The new license name already exists.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to modify the license.");
         }
         else if(result == ResultMessage.DoesNotExist)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The given license does not exist.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to modify a license.<br />" +
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

      // Get the request parameters.
      String licenseName = request.getParameter("licensename");
      String version = request.getParameter("version");
      int licenseId = 0;
      try
      {
         licenseId = Integer.parseInt(request.getParameter("license"));
      }
      catch(Exception e)
      {
      }

      int processes = -1;
      try
      {
         processes = Integer.parseInt(request.getParameter("pcount"));
      }
      catch(Exception e)
      {
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ModifyLicenseServlet.Message message = new ModifyLicenseServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to modify a license.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in as an administrator to modify a license.");
      }
      else if(!user.isAdministrator())
         message.setResult(ResultMessage.NotAuthorized);
      else if(licenseId < 1 || licenseName == null || licenseName.isEmpty() ||
         version == null || version.isEmpty())
      {
         page = "/allproducts.jsp";
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />Missing required parameters.");
      }
      else
      {
         page = "/allproducts.jsp";
         if(licenseName.length() > 64)
            licenseName = licenseName.substring(0, 64);

         if(version.length() > 16)
            version = version.substring(0, 16);

         DatabaseLicenses licenses = new DatabaseLicenses();
         try
         {
            licenses.database.connect();
            licenses.database.connection.setAutoCommit(false);
            try
            {
               // See if the license name changed.
               String oldName = null;
               String oldVersion = null;
               int oldProcesses = 0;
               licenses.getLicenseInfo(licenseId);
               if(licenses.database.results.next())
               {
                  oldName = licenses.database.results.getString("licensename");
                  oldVersion = licenses.database.results.getString("version");
                  oldProcesses = licenses.database.results.getInt("allowedinst");
               }
               else
                  message.setResult(ResultMessage.DoesNotExist);

               licenses.database.results.close();
               if(oldName != null && !oldName.isEmpty())
               {
                  // Make sure the license name is not a duplicate.
                  if(!oldName.equals(licenseName) &&
                     licenses.isLicenseInDatabase(licenseName))
                  {
                     message.setResult(ResultMessage.Exists);
                     page = "/licenseinfo.jsp?license=" + licenseId;
                  }
                  else
                  {
                     if(processes < 0)
                        processes = oldProcesses;

                     PreparedStatement statement =
                        licenses.database.connection.prepareStatement(
                        "UPDATE licenseproducts SET licensename = ?, version = ?, " +
                        "allowedinst = ? WHERE licenseid = ?");
                     licenses.database.addStatement(statement);
                     statement.setString(1, licenseName);
                     statement.setString(2, version);
                     statement.setInt(3, processes);
                     statement.setInt(4, licenseId);
                     statement.executeUpdate();

                     // If the version changed, discard the license signatures.
                     if(!oldVersion.equals(version))
                        licenses.removeLicenseSignatures(licenseId);

                     message.setResult(ResultMessage.Success);
                  }
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

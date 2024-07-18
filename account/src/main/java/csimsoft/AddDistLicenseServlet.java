/**
 * @(#)AddDistLicenseServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/10/26
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


public class AddDistLicenseServlet extends HttpServlet
{

   public AddDistLicenseServlet()
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
               "The new distributor license has been successfully added.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to add the new distributor license.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.InfoMessage);
            this.Report.setMessage(
               "The distributor already has the specified license.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to add a new license.<br />" +
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
      // Get the result message object for the session.
      HttpSession session = request.getSession();
      AddDistLicenseServlet.Message message = new AddDistLicenseServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin in order to add a new license.
      String page = "/index.jsp";
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!user.isAdministrator())
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Get the parameters from the request.
      int userId = 0;
      int licenseId = 0;
      try
      {
         userId = Integer.parseInt(request.getParameter("user"));
         licenseId = Integer.parseInt(request.getParameter("license"));
      }
      catch(Exception e)
      {
      }

      if(userId < 1 || licenseId < 1)
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      page = "/distlicense.jsp?user=" + userId;
      DatabaseUsers users = new DatabaseUsers();
      DatabaseLicenses licenses = new DatabaseLicenses(users.database);
      try
      {
         users.database.connect();
         users.database.statement = users.database.connection.createStatement();
         try
         {
            // Make sure the user is a distributor.
            if(users.isDistributor(userId))
            {
               // See if the license is already on the list.
               if(licenses.isDistributorLicenseInDatabase(userId, licenseId))
                  message.setResult(ResultMessage.Exists);
               else
               {
                  // Add the license to the distributor's list.
                  users.database.statement.executeUpdate(
                     "INSERT INTO distributorlicenses (userid, licenseid) " +
                     "VALUES (" + userId + ", " + licenseId + ")");
                  message.setResult(ResultMessage.Success);
               }
            }
            else
               message.setResult(ResultMessage.Failure);
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

      users.database.cleanup();
      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

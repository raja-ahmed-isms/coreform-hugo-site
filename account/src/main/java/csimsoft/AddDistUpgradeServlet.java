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


public class AddDistUpgradeServlet extends HttpServlet
{

   public AddDistUpgradeServlet()
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
               "The new distributor upgrade path has been successfully added.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to add the new distributor upgrade path.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.InfoMessage);
            this.Report.setMessage(
               "The distributor already has the specified upgrade path.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to add a new upgrade path.<br />" +
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
      // Get the parameters from the request.
      int userId = 0;
      int upgradeId = 0;
      try
      {
         userId = Integer.parseInt(request.getParameter("user"));
         upgradeId = Integer.parseInt(request.getParameter("upgrade"));
      }
      catch(Exception e)
      {
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      AddDistUpgradeServlet.Message message = new AddDistUpgradeServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin in order to add a new license.
      String page = "/index.jsp";
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session))
         message.setResult(ResultMessage.LoginError);
      else if(!user.isAdministrator())
         message.setResult(ResultMessage.NotAuthorized);
      else if(userId < 1 || upgradeId < 1)
         message.setResult(ResultMessage.Failure);
      else
      {
         page = "/distupgrades.jsp?user=" + userId;
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
                  // See if the upgrade is already on the list.
                  if(licenses.isDistributorUpgradeInDatabase(userId, upgradeId))
                     message.setResult(ResultMessage.Exists);
                  else
                  {
                     // Add the upgrade to the distributor's list.
                     users.database.statement.executeUpdate(
                        "INSERT INTO distributorupgrades (userid, upgradeid) " +
                        "VALUES (" + userId + ", " + upgradeId + ")");
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
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

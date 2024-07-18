/**
 * @(#)ChangeRolesServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2010/8/6
 */

package csimsoft;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class ChangeRolesServlet extends HttpServlet
{

   public ChangeRolesServlet()
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
               "The user's roles have been successfully changed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to change the user's roles.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to change the user's roles." +
               "<br />Please log in as an administrator to do so.");
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
      String idString = request.getParameter("user");

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ChangeRolesServlet.Message message = new ChangeRolesServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is logged in.
      String contextPath = request.getContextPath();
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
         return;
      }

      // Make sure the user is authorized to make the changes.
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!user.isAdministrator())
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
         return;
      }

      // Make sure the user id is valid.
      int userId = 0;
      try
      {
         userId = Integer.parseInt(idString);
      }
      catch(Exception e)
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
         return;
      }

      DatabaseUsers users = new DatabaseUsers();
      try
      {
         users.database.connect();
         users.database.connection.setAutoCommit(false);
         users.database.statement = users.database.connection.createStatement();
         try
         {
            // Set each of the roles on or off.
            if(UserInfo.AdminRoleName.equals(
               request.getParameter(UserInfo.AdminRoleName)))
            {
               if(!users.isAdministrator(userId))
               {
                  AcctLogger.get().info("User " + user.getUserId() + " is giving admin to " + userId);
                  users.database.statement.executeUpdate(
                     "INSERT INTO siteadmins (userid) VALUES (" + userId + ")");
               }
            }
            else
            {
               AcctLogger.get().info("User " + user.getUserId() + " is removing admin from " + userId);
               users.database.statement.executeUpdate(
                  "DELETE FROM siteadmins WHERE userid = " + userId);
            }

			boolean wantReseller = UserInfo.ResellerRoleName.equals(request.getParameter(UserInfo.ResellerRoleName));
			boolean wantDist = UserInfo.DistributorRoleName.equals(request.getParameter(UserInfo.DistributorRoleName));
            if(wantReseller || wantDist)
            {
               if(!users.isDistributor(userId) && !users.isReseller(userId))
               {
                  // Get the current group id from the database.
                  int nextId = users.database.getNextId("distributorcount", "groupid");
                  int resellerVal = wantReseller ? 1 : 0;

                  users.database.statement.executeUpdate(
                     "INSERT INTO distributors (userid, groupid, folder, reseller) VALUES (" +
                     userId + ", " + nextId + ", 0, " + resellerVal + ")");
               } else if(wantReseller && users.isDistributor(userId))
			   {
				   users.database.statement.executeUpdate(
					  "UPDATE distributors SET reseller = 1 WHERE userid = " + userId);
               } else if(wantDist && users.isReseller(userId))
			   {
				   users.database.statement.executeUpdate(
					  "UPDATE distributors SET reseller = 0 WHERE userid = " + userId);
			   }
            }
            else
            {
			   users.database.statement.executeUpdate(
				  "UPDATE users SET groupid = 0 WHERE groupid IN " +
				  "(SELECT groupid FROM distributors WHERE userid = " + userId + ")");
			   users.database.statement.executeUpdate(
				  "DELETE FROM distributorlicenses WHERE userid = " + userId);
			   users.database.statement.executeUpdate(
				  "DELETE FROM distributors WHERE userid = " + userId);
            }

            users.database.connection.commit();
            message.setResult(ResultMessage.Success);
         }
         catch(java.sql.SQLException sqle2)
         {
            users.database.connection.rollback();
            message.setResult(ResultMessage.Failure);
            message.addException(sqle2);
         }
      }
      catch(java.sql.SQLException sqle)
      {
         message.setResult(ResultMessage.ResourceError);
         message.addException(sqle);
      }
      catch(javax.naming.NamingException ne)
      {
         message.setResult(ResultMessage.ResourceError);
         message.addException(ne);
      }

      users.database.cleanup();

      String page = "/clientinfo.jsp?user=" + userId;
      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

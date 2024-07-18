/**
 *
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


public class ChangeUserGroupServlet extends HttpServlet
{

   public ChangeUserGroupServlet()
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
               "The new distributor has been successfully assigned.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to change the distributor.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to set a user group.<br />" +
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
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
   {
      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ChangeUserGroupServlet.Message message = new ChangeUserGroupServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin in order to add a new license.
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!user.isAdministrator())
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
         return;
      }

      // Get the parameters from the request.
      int userId = 0;
      int groupId = 0;
      try
      {
         userId = Integer.parseInt(request.getParameter("user"));
         groupId = Integer.parseInt(request.getParameter("groupid"));
      }
      catch(Exception e)
      {
      }

      if(userId < 1 || groupId < 0)
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
         return;
      }

      String page = "/clientinfo.jsp?user=" + userId;
      try
      {
         int distributorId = Integer.parseInt(request.getParameter("dist"));
         if(distributorId > 0)
            page = "/distusers.jsp?user=" + distributorId;
      }
      catch(Exception e)
      {
      }

      DatabaseUsers users = new DatabaseUsers();
      try
      {
         users.database.connect();
         users.database.statement = users.database.connection.createStatement();
         try
         {
            // Get the user's old group id.
            UserProfileInfo info = new UserProfileInfo();
            if(users.getUserProfile(userId, info))
            {
               //String distributorEmail = users.getDistributorEmail(groupId);
               if(info.getGroupId() != groupId)
               {
                  // Set the user's new group id.
                  users.database.statement.executeUpdate(
                     "UPDATE users SET groupid = " + groupId +
                     " WHERE userid = " + userId);
               }

               message.setResult(ResultMessage.Success);

               // Send an email to the new distributor.
               //if(groupId > 0 && info.getGroupId() != groupId)
               //{
                  //csimsoft.Email messenger = new csimsoft.Email();
                  //messenger.sendClientNotification(userId, info.getEmail(),
                  //   info.getFirstName(), info.getLastName(), distributorEmail);
               //}
            }
            else
            {
               message.setResult(ResultMessage.Failure);
               message.addMessage("<br />The user id is not valid.");
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

      users.database.cleanup();
      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

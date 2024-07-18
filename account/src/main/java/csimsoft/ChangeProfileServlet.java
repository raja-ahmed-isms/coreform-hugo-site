/**
 * @(#)ChangeProfileServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2010/6/22
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


public class ChangeProfileServlet extends HttpServlet
{

   public ChangeProfileServlet()
   {
   }

   private static class Message implements ResultMessageAdapter
   {
      public ResultMessage Report = null;
      public boolean Admin = false;

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
               "The profile has been successfully changed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to change the profile.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to update the user's profile.<br />" +
               "Please log in as an administrator or distributor to do so.");
         }
      }

      @Override
      public void addException(Exception e)
      {
         if(this.Admin)
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
      String idString = request.getParameter("user");
      String first = request.getParameter("first");
      String last = request.getParameter("last");
      String email = request.getParameter("email");

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ChangeProfileServlet.Message message = new ChangeProfileServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is logged in.
      String contextPath = request.getContextPath();
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage("You must log in to change your profile.");
         response.sendRedirect(response.encodeRedirectURL(
            contextPath + "/changeprofile.jsp"));
         return;
      }

      // Make sure the user is authorized to make the changes.
      UserInfo user = LoginServlet.getUserInfo(session);
      int userId = user.getUserId();
      boolean badId = false;
      try
      {
         if(idString != null && !idString.isEmpty())
            userId = Integer.parseInt(idString);
      }
      catch(Exception e)
      {
         badId = true;
      }

      String page = "/index.jsp";
      boolean isAdmin = user.isAdministrator();
      message.Admin = isAdmin;
      boolean isDistributor = user.isDistributor();
      boolean isReseller = user.isReseller();
      if(userId != user.getUserId() && !isAdmin && !isDistributor && !isReseller)
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Make sure all the required fields are not null.
      if(badId || userId < 1 || first == null || first.isEmpty() || last == null ||
         last.isEmpty() || email == null || email.isEmpty())
      {
         response.sendRedirect(response.encodeRedirectURL(
            contextPath + "/changeprofile.jsp"));
         return;
      }

      DatabaseUsers users = new DatabaseUsers();
      try
      {
         users.database.connect();
         try
         {
            // Make sure the distributor can access the user.
            if(isAdmin || userId == user.getUserId() || (isDistributor &&
               users.isDistributor(userId, user.getUserId()))
			   || (isReseller && users.isReseller(userId, user.getUserId())))
            {
               PreparedStatement statement = users.database.connection.prepareStatement(
                  "UPDATE users SET firstname = ?, lastname = ?, email = ? " +
                  "WHERE userid = ?");
               users.database.addStatement(statement);
               statement.setString(1, first);
               statement.setString(2, last);
               statement.setString(3, email);
               statement.setInt(4, userId);
               statement.executeUpdate();
               message.setResult(ResultMessage.Success);
            }
            else
               message.setResult(ResultMessage.NotAuthorized);
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

      if(userId != user.getUserId())
      {
         if(isAdmin)
            page = "/clientinfo.jsp?user=" + userId;
         else if(isDistributor)
            page = "/distclient.jsp?user=" + userId;
         else if(isReseller)
            page = "/resellerclient.jsp?user=" + userId;
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

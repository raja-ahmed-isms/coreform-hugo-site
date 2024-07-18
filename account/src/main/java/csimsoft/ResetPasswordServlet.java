/**
 * @(#)ResetPasswordServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/4/17
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
import org.mindrot.jbcrypt.BCrypt;


public class ResetPasswordServlet extends HttpServlet
{

   public ResetPasswordServlet()
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
               "Your password has been successfully changed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Your password has not been changed due to an internal error.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to change the user's password." +
               "<br />Please log in as an administrator or distributor to do so.");
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

      // Get the request parameters.
      String token = request.getParameter("token");
      String password = request.getParameter("pass");
      String confirm = request.getParameter("confirm");

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ResetPasswordServlet.Message message = new ResetPasswordServlet.Message(
         LoginServlet.getResultMessage(session));
      if(token == null) {
         message.setResult(ResultMessage.NotAuthorized);
         return;
      }

      String contextPath = request.getContextPath();
      String page = "/index.jsp";

      // Make sure the password and confirmation are valid and match.
      if(password == null || password.isEmpty() ||
         confirm == null || !confirm.equals(password))
      {
         response.sendRedirect(response.encodeRedirectURL(
               contextPath + "/resetpass.jsp?token=" + token));
         return;
      }

      DatabaseUsers users = new DatabaseUsers();
      try
      {
         users.database.connect();
         try
         {
            DatabasePassReset passReset = new DatabasePassReset(users.database);
            String email = passReset.getUserEmail(token);
            int userId = users.getUserId(email);
            PreparedStatement statement = users.database.connection.prepareStatement(
               "UPDATE users SET password = ? WHERE userid = ?");
            String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            users.database.addStatement(statement);
            statement.setString(1, hashPassword);
            statement.setInt(2, userId);
            statement.executeUpdate();
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

      users.database.cleanup();

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

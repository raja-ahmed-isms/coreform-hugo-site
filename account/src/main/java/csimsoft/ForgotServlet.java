/**
 * @(#)ForgotServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/9/11
 */

package csimsoft;

import java.io.IOException;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;


public class ForgotServlet extends HttpServlet
{

   public ForgotServlet()
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
               "An email message with a password reset link has been sent to the " +
               "specified email address.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to send an email message to the specified address. " +
               "Please check the address and try again.");
         }
         else if(result == ResultMessage.DoesNotExist)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The specified email address was not found.<br />" +
               "Please check your email address for errors or signup " +
               "for a new account.");
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

      HttpSession session = request.getSession();
      ForgotServlet.Message message = new ForgotServlet.Message(
         LoginServlet.getResultMessage(session));

      String email = request.getParameter("email");
      String contextPath = request.getContextPath();
      if(email == null || email.isEmpty())
      {
         response.sendRedirect(response.encodeRedirectURL(
            contextPath + "/forgot.jsp"));
         return;
      }

      DatabasePassReset database = new DatabasePassReset();
      try
      {
         database.database.connect();
         DatabaseUsers users = new DatabaseUsers(database.database);
         if (users.isUserInDatabase(email)) {
            // Generate token and construct link
            String token = UUID.randomUUID().toString();
            database.addReset(token, email);
            csimsoft.Email messenger = new csimsoft.Email();
            String link = "https://coreform.com/account/resetpass.jsp?token=" + token;
            messenger.sendForgotMessage(message, email, link);
         } 
         else
            message.setResult(ResultMessage.DoesNotExist);
      }
      catch(java.sql.SQLException sqle)
      {
         message.setResult(ResultMessage.ResourceError);
         message.addException(sqle);
      }
      catch(javax.naming.NamingException sqle)
      {
         message.setResult(ResultMessage.ResourceError);
         message.addException(sqle);
      }

      database.database.cleanup();
      response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
   }
}
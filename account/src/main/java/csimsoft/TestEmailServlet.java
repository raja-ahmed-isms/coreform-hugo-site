/**
 * @(#)TestEmailServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/4/15
 */

package csimsoft;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class TestEmailServlet extends HttpServlet
{

   public TestEmailServlet()
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
               "The test email was successfully sent.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to send the test email.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to send a test email.<br />" +
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
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
   {
      // Get the result message object for the session.
      HttpSession session = request.getSession();
      TestEmailServlet.Message message = new TestEmailServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to send a test email.
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session) || !user.isAdministrator())
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
         return;
      }

      // Send a test email.
      try
      {
         Email testemail = new Email();
         javax.mail.Session mailSession = testemail.getMailSession();

         // Set up the message body.
         String body =
            "This is a test of the csimsoft email system. If this were " +
            "an actual email it would say something important.\n";

         // Send the message.
         testemail.sendMessage(mailSession, "info@coreform.com",
            "csimsoft Email Test", body);
         message.setResult(ResultMessage.Success);
      }
      catch(javax.mail.internet.AddressException ae)
      {
         message.setResult(ResultMessage.Failure);
         message.addException(ae);
      }
      catch(javax.mail.MessagingException me)
      {
         message.setResult(ResultMessage.Failure);
         message.addException(me);
      }

      // Redirect the response back to the account page.
      response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
   }
}
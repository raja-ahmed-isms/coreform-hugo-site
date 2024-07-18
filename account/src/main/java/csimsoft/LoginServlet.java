/**
 * @(#)LoginServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/3/19
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


public class LoginServlet extends HttpServlet
{

   public LoginServlet()
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
         if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The email address and/or password you submited are not valid.");
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

      // Get the user name and password from the request.
      String email = request.getParameter("user");
      String password = request.getParameter("pass");
      boolean hasEmail = email != null && !email.isEmpty();
      boolean hasPass = password != null && !password.isEmpty();
      boolean sendingXml = "yes".equalsIgnoreCase(request.getParameter("xml"));

      AcctLogger.get().info("Start login");

      // Store information about the user and state in the session.
      HttpSession session = request.getSession();
      if(hasEmail)
      {
         // Add the email address to the session.
         session.setAttribute("email", email);
      }

      // Set up the result message object.
      LoginServlet.Message message = new LoginServlet.Message(
         LoginServlet.getResultMessage(session));

      String contextPath = request.getContextPath();
      DatabaseUsers users = new DatabaseUsers();
      try
      {
         boolean loginSuccess = false;
         if(hasEmail && hasPass)
         {
            // Look up the user name in the database.
            UserInfo user = null;
            users.database.connect();
            PreparedStatement statement = users.database.connection.prepareStatement(
               "SELECT userid, password FROM users WHERE email = ?");
            users.database.addStatement(statement);
            statement.setString(1, email);
            users.database.results = statement.executeQuery();
            if(users.database.results.next())
            {
               AcctLogger.get().info("Has login result");
               String hashPass = users.database.results.getString("password");
               if (BCrypt.checkpw(password, hashPass)) {
                  // Get the user id from the result set.
                  loginSuccess = true;
                  LoginServlet.loginUser(session);
                  user = LoginServlet.getUserInfo(session);
                  user.setUserId(users.database.results.getInt("userid"));
                  AcctLogger.get().info("Logged in user id: " + user.getUserId());
               }
            }

            users.database.results.close();
            if(loginSuccess)
            {
               // Get the user roles and add them to the session.
               users.getUserRoles(user);
            }
         }

         // If the login was not successful, send back an error message.
         if(!loginSuccess)
         {
            message.setResult(ResultMessage.Failure);
            if(!sendingXml)
            {
               message.addMessage(
                  " If you have forgotten your password, please click " +
                  "<a href=\"" + contextPath + "/forgot.jsp\">here</a> " +
                  "to have a reset link emailed to you.");
            }
         }
         else if(sendingXml)
            message.setResult(ResultMessage.Success);
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

      if(sendingXml)
      {
         message.Report.sendXml(response);
         return;
      }

      // Redirect the response back to the requesting page.
      String page = request.getParameter("page");
      if(page == null || page.isEmpty())
         page = "/index.jsp";

      try
      {
         // Add the order item id if it's present.
         String orderType = (String)session.getAttribute("ordertype");
         String orderItem = (String)session.getAttribute("orderitem");
         String orderDays = null;
         String orderSource = null;
         try
         {
            orderDays = (String)session.getAttribute("orderdays");
         }
         catch(Exception e)
         {
         }

         try
         {
            orderSource = (String)session.getAttribute("ordersource");
         }
         catch(Exception e)
         {
         }

         if(orderType != null && !orderType.isEmpty() &&
            orderItem != null && !orderItem.isEmpty())
         {
            page = "/neworder?" + orderType + "=" + orderItem;
            if(orderDays != null && !orderDays.isEmpty())
               page += "&days=" + orderDays;
            if(orderSource != null && !orderSource.isEmpty())
               page += "&source=" + orderSource;

            session.removeAttribute("ordertype");
            session.removeAttribute("orderitem");
            session.removeAttribute("orderdays");
            session.removeAttribute("ordersource");
         }
      }
      catch(Exception e)
      {
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }

   public static ResultMessage getResultMessage(HttpSession session)
   {
      ResultMessage message = null;
      try
      {
         message = (ResultMessage)session.getAttribute("message");
      }
      catch(ClassCastException cce)
      {
      }

      if(message == null)
      {
         message = new ResultMessage();
         session.setAttribute("message", message);
      }

      return message;
   }

   public static UserInfo getUserInfo(HttpSession session)
   {
      UserInfo user = null;
      try
      {
         user = (UserInfo)session.getAttribute("user");
      }
      catch(ClassCastException cce)
      {
      }

      if(user == null)
      {
         user = new UserInfo();
         session.setAttribute("user", user);
      }

      return user;
   }

   public static void loginUser(HttpSession session)
   {
      session.setAttribute("login", "success");
   }

   public static boolean isUserLoggedIn(HttpSession session)
   {
      return "success".equals(session.getAttribute("login"));
   }
}


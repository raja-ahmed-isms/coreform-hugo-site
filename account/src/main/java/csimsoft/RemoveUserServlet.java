/**
 * @(#)RemoveUserServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/4/18
 */

package csimsoft;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class RemoveUserServlet extends HttpServlet
{

   public RemoveUserServlet()
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
               "The user has been successfully removed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to remove the user.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to remove users.<br />" +
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
      // Get the request parameters.
      int userId = 0;
      try
      {
         userId = Integer.parseInt(request.getParameter("user"));
      }
      catch(Exception e)
      {
      }

      // Get the optional parameters.
      String search = request.getParameter("search");
      String filterRole = request.getParameter("role");
      String pageNumber = request.getParameter("page");
      ParameterBuilder params = new ParameterBuilder();
      params.add("search", search);
      params.add("role", filterRole);
      params.add("page", pageNumber);

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      RemoveUserServlet.Message message = new RemoveUserServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is an administrator. Make sure the user id is valid.
      String contextPath = request.getContextPath();
      String page = request.getParameter("from");
      if(page == null || page.isEmpty())
         page = "/userlist.jsp";

      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in as an administrator to remove users.");
      }
      else if(!user.isAdministrator())
         message.setResult(ResultMessage.NotAuthorized);
      else if(userId < 1)
      {
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />The given user id is not valid.");
      }

      if(message.Report.getResult() != ResultMessage.NoResult)
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page + params));
         return;
      }

      DatabaseProcesses processes = new DatabaseProcesses();
      DatabaseLicenses licenses = new DatabaseLicenses(processes.database);
      DatabaseUniversities universities = new DatabaseUniversities(processes.database);
      try
      {
         processes.database.connect();
         processes.database.connection.setAutoCommit(false);
         try
         {
            AcctLogger.get().info("User " + user.getUserId() + " is deleting user " + userId);
            // Get and execute all the updates for the order process handlers.
            List<OrderProcessHandler> handlers = processes.getOrderHandlers();
            Iterator<OrderProcessHandler> iter = handlers.iterator();
            while(iter.hasNext())
               iter.next().removeUser(processes.database, userId);

            // Remove the user from the standard locations.
            processes.database.statement.executeUpdate(
               "DELETE FROM userdownloads WHERE userid = " + userId);

            processes.database.statement.executeUpdate(
               "DELETE FROM orderproducts WHERE orderid IN " +
               "(SELECT orderid FROM userorders WHERE userid = " + userId + ")");
            processes.database.statement.executeUpdate(
               "DELETE FROM orderlicenses WHERE orderid IN " +
               "(SELECT orderid FROM userorders WHERE userid = " + userId + ")");
            processes.database.statement.executeUpdate(
               "DELETE FROM userorders WHERE userid = " + userId);

            // Remove the user's licenses.
            licenses.removeUserLicenses(userId);

            universities.removeContact(userId);

            processes.database.statement.executeUpdate(
               "UPDATE users SET groupid = 0 WHERE groupid IN " +
               "(SELECT groupid FROM distributors WHERE userid = " + userId + ")");
            processes.database.statement.executeUpdate(
               "DELETE FROM distributorlicenses WHERE userid = " + userId);
            processes.database.statement.executeUpdate(
               "DELETE FROM distributors WHERE userid = " + userId);
            processes.database.statement.executeUpdate(
               "DELETE FROM siteadmins WHERE userid = " + userId);
            processes.database.statement.executeUpdate(
               "DELETE FROM users WHERE userid = " + userId);

            processes.database.connection.commit();
            message.setResult(ResultMessage.Success);
         }
         catch(SQLException sqle2)
         {
            processes.database.connection.rollback();
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

      processes.database.cleanup();
      response.sendRedirect(response.encodeRedirectURL(contextPath + page + params));
   }
}
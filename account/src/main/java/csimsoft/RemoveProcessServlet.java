/**
 * @(#)RemoveProcessServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2010/8/20
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


public class RemoveProcessServlet extends HttpServlet
{

   public RemoveProcessServlet()
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
               "The process has been successfully removed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to remove the process.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to remove processes.<br />" +
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
      int processId = 0;
      try
      {
         processId = Integer.parseInt(request.getParameter("process"));
      }
      catch(Exception nfe)
      {
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      RemoveProcessServlet.Message message = new RemoveProcessServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to remove a process.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in as an administrator to remove processes.");
      }
      else if(!user.isAdministrator())
         message.setResult(ResultMessage.NotAuthorized);
      else if(processId < 1)
      {
         page = "/orderprocess.jsp";
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />The given process id is not valid.");
      }
      else
      {
         page = "/orderprocess.jsp";
         DatabaseProcesses processes = new DatabaseProcesses();
         try
         {
            processes.database.connect();
            processes.database.connection.setAutoCommit(false);
            try
            {
               // Get the handler for the process.
               OrderProcessHandler handler = processes.getProcessHandler(processId);
               if(handler != null)
               {
                  // Execute any handler cleanup statements.
                  handler.cleanup(processes.database, processId);
               }

               // Finish removing the process.
               processes.database.statement.executeUpdate(
                  "UPDATE products SET processid = 0 WHERE processid = " + processId);
               processes.database.statement.executeUpdate(
                  "UPDATE userorders SET processid = 0 WHERE processid = " + processId);
               processes.database.statement.executeUpdate(
                  "DELETE FROM orderprocess WHERE processid = " + processId);

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
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}
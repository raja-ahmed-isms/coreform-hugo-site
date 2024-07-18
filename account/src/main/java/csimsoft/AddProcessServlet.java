/**
 * @(#)AddProcessServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/9/26
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


public class AddProcessServlet extends HttpServlet
{

   public AddProcessServlet()
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
               "The new process has been successfully added.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to add the new process.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The specified process already exists.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to add a new process.<br />" +
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
      // Make sure the character decoding is correct.
      if(request.getCharacterEncoding() == null)
         request.setCharacterEncoding("UTF-8");

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      AddProcessServlet.Message message = new AddProcessServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin in order to add a new process.
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!user.isAdministrator())
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
         return;
      }

      // Get the parameters from the request.
      String processName = request.getParameter("process");
      String handlerName = request.getParameter("handler");

      // Make sure the required parameters are present.
      String page = "/addprocess.jsp";
      if(processName == null || processName.isEmpty() ||
         handlerName == null || handlerName.isEmpty())
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      page = "/orderprocess.jsp";
      DatabaseProcesses processes = new DatabaseProcesses();
      try
      {
         processes.database.connect();
         try
         {
            if(processes.isProcessInDatabase(processName))
            {
               message.setResult(ResultMessage.Exists);
               page = "/addprocess.jsp";
            }
            else
            {
               // Get the current process id from the database.
               int nextId = processes.database.getNextId(
                  "orderprocesscount", "processid");

               PreparedStatement statement =
                  processes.database.connection.prepareStatement(
                  "INSERT INTO orderprocess (processid, processname, handlername) " +
                  "VALUES (?, ?, ?)");
               processes.database.addStatement(statement);
               statement.setInt(1, nextId);
               statement.setString(2, processName);
               statement.setString(3, handlerName);

               statement.executeUpdate();
               message.setResult(ResultMessage.Success);
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

      processes.database.cleanup();
      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}
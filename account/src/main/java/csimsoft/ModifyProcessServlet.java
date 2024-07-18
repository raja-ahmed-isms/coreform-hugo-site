/**
 * @(#)ModifyProcessServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2008/1/3
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


public class ModifyProcessServlet extends HttpServlet
{

   public ModifyProcessServlet()
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
               "The process has been successfully updated.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to modify the process.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to modify a process.<br />" +
               "Please log in as an administrator to do so.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The specified process already exists.");
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
      ModifyProcessServlet.Message message = new ModifyProcessServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin in order to modify a process.
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!user.isAdministrator())
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
         return;
      }

      // Get the parameters from the request.
      int processId = 0;
      String newProcessName = request.getParameter("newname");
      try
      {
         processId = Integer.parseInt(request.getParameter("process"));
      }
      catch(Exception nfe)
      {
      }

      // Make sure the required parameters are present.
      String page = "/orderprocess.jsp";
      if(processId < 1 || newProcessName == null || newProcessName.isEmpty())
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      DatabaseProcesses processes = new DatabaseProcesses();
      try
      {
         processes.database.connect();
         try
         {
            if(processes.isProcessInDatabase(newProcessName))
               message.setResult(ResultMessage.Exists);
            else
            {
               // Change the process name.
               PreparedStatement statement =
                  processes.database.connection.prepareStatement(
                  "UPDATE orderprocess SET processname = ? WHERE processid = ?");
               processes.database.addStatement(statement);
               statement.setString(1, newProcessName);
               statement.setInt(2, processId);

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
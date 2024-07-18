/**
 * @(#)OrderConfigServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/9/28
 */

package csimsoft;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class OrderConfigServlet extends HttpServlet
{

   public OrderConfigServlet()
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
         if(result == ResultMessage.DoesNotExist)
         {
            this.Report.setType(ResultMessage.InfoMessage);
            this.Report.setMessage(
               "The order process requires no configuration.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The order process has not been configured correctly.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to configure a process.<br />" +
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
      OrderConfigServlet.Message message = new OrderConfigServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is an administrator.
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!user.isAdministrator())
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
         return;
      }

      // Get the process name from the request.
      String page = "/orderprocess.jsp";
      int processId = 0;
      try
      {
         processId = Integer.parseInt(request.getParameter("process"));
      }
      catch(Exception nfe)
      {
      }

      if(processId < 1)
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      DatabaseProcesses processes = new DatabaseProcesses();
      try
      {
         // Get the handler process handler.
         processes.database.connect();
         OrderProcessHandler handler = processes.getProcessHandler(processId);
         if(handler == null)
            message.setResult(ResultMessage.Failure);
         else
         {
            // Get the config page from the order process handler.
            page = handler.getConfigurePage(processId);
            if(page == null || page.isEmpty())
            {
               page = "/orderprocess.jsp";
               message.setResult(ResultMessage.DoesNotExist);
            }
         }
      }
      catch(java.sql.SQLException sqle)
      {
         message.setResult(ResultMessage.ResourceError);
         message.addException(sqle);
      }
      catch(javax.naming.NamingException ne)
      {
         message.setResult(ResultMessage.ResourceError);
         message.addException(ne);
      }

      processes.database.cleanup();
      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}
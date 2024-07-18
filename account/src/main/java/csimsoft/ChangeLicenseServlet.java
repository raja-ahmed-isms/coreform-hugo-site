/**
 * @(#)ChangeLicenseServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/10/22
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


public class ChangeLicenseServlet extends HttpServlet
{

   public ChangeLicenseServlet()
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
               "The new process configuration has been saved.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to save the new process configuration.");
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
   public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
   {
      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ChangeLicenseServlet.Message message = new ChangeLicenseServlet.Message(
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

      // Get the parameters from the request.
      String page = "/orderprocess.jsp";
      int productId = 0;
      int processId = 0;
      try
      {
         productId = Integer.parseInt(request.getParameter("product"));
         processId = Integer.parseInt(request.getParameter("process"));
      }
      catch(Exception nfe)
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      Database database = new Database();
      try
      {
         database.connect();
         database.statement = database.connection.createStatement();
         database.results = database.statement.executeQuery(
            "SELECT productid FROM processlicense WHERE processid = " +
            processId);
         boolean isAdded = database.results.next();
         database.results.close();
         try
         {
            if(isAdded)
            {
               database.statement.executeUpdate(
                  "UPDATE processlicense SET productid = " + productId +
                  " WHERE processid = " + processId);
            }
            else
            {
               database.statement.executeUpdate(
                  "INSERT INTO processlicense (processid, productid) " +
                  "VALUES (" + processId + ", " + productId + ")");
            }

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

      database.cleanup();
      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}
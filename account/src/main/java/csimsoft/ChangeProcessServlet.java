/**
 * @(#)ChangeProcessServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/9/28
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


public class ChangeProcessServlet extends HttpServlet
{

   public ChangeProcessServlet()
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
               "The order process handler has been successfully changed.");
         }
         else if(result == ResultMessage.DoesNotExist)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The specified order process does not exist.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to change the order process handler.");
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
      ChangeProcessServlet.Message message = new ChangeProcessServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is an administrator.
      String page = "/index.jsp";
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!user.isAdministrator())
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Get the parameters from the request.
      int processId = 0;
      try
      {
         processId = Integer.parseInt(request.getParameter("process"));
      }
      catch(Exception nfe)
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // The request may be for a product, license, or license group.
      int productId = 0;
      int licenseId = 0;
      try
      {
         productId = Integer.parseInt(request.getParameter("product"));
      }
      catch(Exception nfe)
      {
      }

      try
      {
         licenseId = Integer.parseInt(request.getParameter("license"));
      }
      catch(Exception nfe)
      {
      }

      if(productId > 0)
      {
         page = "/alldownloads.jsp";
         String rootId = request.getParameter("rootid");
         if(rootId != null && !rootId.isEmpty())
            page += "?rootid=" + rootId;
      }
      else if(licenseId > 0)
         page = "/allproducts.jsp";
      else
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      DatabaseProcesses processes = new DatabaseProcesses();
      try
      {
         processes.database.connect();
         processes.database.connection.setAutoCommit(false);
         processes.database.statement = processes.database.connection.createStatement();
         try
         {
            boolean changes = true;
            if(productId > 0)
            {
               // Get the current order process id.
               int oldProcessId = 0;
               processes.database.results = processes.database.statement.executeQuery(
                  "SELECT processid FROM products WHERE productid = " + productId);
               if(processes.database.results.next())
                  oldProcessId = processes.database.results.getInt("processid");

               processes.database.results.close();
               if(processId == oldProcessId)
                  changes = false;
               else
               {
                  // Get the current order process handler and remove the
                  // product from the process.
                  OrderProcessHandler handler =
                     processes.getProcessHandler(oldProcessId);
                  if(handler != null)
                     handler.removeFromProcess(processes.database, productId);

                  // Set the new product process id.
                  processes.database.statement.executeUpdate(
                     "UPDATE products SET processid = " + processId +
                     " WHERE productid = " + productId);

                  // Get the new order process handler and add the
                  // product to the process.
                  handler = processes.getProcessHandler(processId);
                  if(handler != null)
                     handler.addToProcess(processes.database, productId);
               }
            }
            else if(licenseId > 0)
            {
               processes.database.statement.executeUpdate(
                  "UPDATE licenseproducts SET processid = " + processId +
                  " WHERE licenseid = " + licenseId);
            }

            if(changes)
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
      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}
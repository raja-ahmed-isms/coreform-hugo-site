/**
 * @(#)ApproveLicenseServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/11/2
 */

package csimsoft;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class ApproveLicenseServlet extends HttpServlet
{

   public ApproveLicenseServlet()
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
               "The client/license information has been saved.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to save the client/license information.");
         }
         else if(result == ResultMessage.DoesNotExist)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The order process handler does not exist.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to approve a license.<br />" +
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
      ApproveLicenseServlet.Message message = new ApproveLicenseServlet.Message(
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
      String page = "/allorders.jsp";
      int orderId = 0;
      try
      {
         orderId = Integer.parseInt(request.getParameter("orderid"));
      }
      catch(NumberFormatException nfe)
      {
         message.setResult(ResultMessage.Failure);
         message.addException(nfe);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      DatabaseLicenses licenses = new DatabaseLicenses();
      DatabaseOrders orders = new DatabaseOrders(licenses.database);
      DatabaseProcesses processes = new DatabaseProcesses(licenses.database);
      try
      {
         licenses.database.connect();
         licenses.database.connection.setAutoCommit(false);
         try
         {
            OrderInfo order = new OrderInfo(orderId);
            if(orders.getOrderStep(orderId, order))
            {
               // Get the order handler and process license.
               order.setHandler(processes.getProcessHandler(order.getProcessId()));
               int productId = licenses.getProcessLicense(order.getProcessId());
               if(productId < 1)
               {
                  message.setResult(ResultMessage.Failure);
                  message.addMessage(
                     "<br />The process is not configured.");
               }
               else if(order.getHandler() == null)
                  message.setResult(ResultMessage.DoesNotExist);
               else if(licenses.isDownloadApproved(order.getUserId(), productId))
               {
                  message.setResult(ResultMessage.Success);
                  message.addMessage(
                     "<br />The license has already been approved.");
               }
               else
               {
                  // Approve the license.
                  licenses.setDownloadApproval(order.getUserId(), productId,
                     DatabaseLicenses.DownloadApproved);

                  // Increment the order step.
                  order.incrementProcessStep();
                  if(orders.updateOrderStep(order))
                     page = "/orderinfo?orderid=" + orderId;

                  licenses.database.connection.commit();
                  message.setResult(ResultMessage.Success);

                  try
                  {
                     // Send an email to notify the user of the license approval.
                     DatabaseUsers users = new DatabaseUsers(licenses.database);
                     String email = users.getUserEmail(order.getUserId());
                     if(email != null)
                     {
                        csimsoft.Email messenger = new csimsoft.Email();
                        messenger.sendLicenseMessage(message, email, orderId);
                     }
                  }
                  catch(java.sql.SQLException sqle3)
                  {
                     message.addMessage("<br />Unable to send an email to the user.");
                     message.addException(sqle3);
                  }
               }
            }
            else
            {
               message.setResult(ResultMessage.Failure);
               message.addMessage(
                  "<br />The order id is not valid.");
            }
         }
         catch(java.sql.SQLException sqle2)
         {
            licenses.database.connection.rollback();
            message.setResult(ResultMessage.Failure);
            message.addException(sqle2);
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

      licenses.database.cleanup();
      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}
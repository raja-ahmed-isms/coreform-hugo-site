/**
 * @(#)ApprovePaymentServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/5/9
 */
package csimsoft;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class ApprovePaymentServlet extends HttpServlet
{

   public ApprovePaymentServlet()
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
               "The payment information has been saved.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to save the payment information.");
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
               "You are not authorized to approve a payment.<br />" +
               "Please log in as an administrator or distributor to do so.");
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
      ApprovePaymentServlet.Message message = new ApprovePaymentServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is an administrator.
      String page = "/index.jsp";
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      boolean isAdmin = user.isAdministrator();
      boolean isDistributor = user.isDistributor();
      if(!isAdmin && !isDistributor)
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Get the parameters from the request.
      if(isAdmin)
         page = "/allorders.jsp";
      else
         page = "/distorders.jsp";

      int orderId = 0;
      try
      {
         orderId = Integer.parseInt(request.getParameter("orderid"));
      }
      catch(Exception nfe)
      {
         message.setResult(ResultMessage.Failure);
         message.addException(nfe);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Get the expiration date if there is one.
      String expiration = null;
      if("yes".equals(request.getParameter("expiration")))
      {
         try
         {
            int year = Integer.parseInt(request.getParameter("year"));
            int month = Integer.parseInt(request.getParameter("month")) - 1;
            java.util.Calendar date = java.util.Calendar.getInstance();
            date.set(year, month, 1);
            date.set(java.util.Calendar.DAY_OF_MONTH,
               date.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
            expiration = Database.formatDate(date);
         }
         catch(Exception nfe)
         {
            message.setResult(ResultMessage.Failure);
            message.addException(nfe);
            response.sendRedirect(response.encodeRedirectURL(contextPath + page));
            return;
         }
      }

      DatabaseOrders orders = new DatabaseOrders();
      DatabaseLicenses manager = new DatabaseLicenses(orders.database);
      DatabaseProcesses processes = new DatabaseProcesses(orders.database);
      DatabaseUsers users = new DatabaseUsers(orders.database);
      try
      {
         orders.database.connect();
         orders.database.connection.setAutoCommit(false);
         try
         {
            OrderInfo order = new OrderInfo(orderId);
            if(orders.getOrderStep(orderId, order))
            {
               // Get the order process handler. Make sure a
               // distributor can access the order.
               order.setHandler(processes.getProcessHandler(order.getProcessId()));
               if(!isAdmin && !users.isDistributor(order.getUserId(), user.getUserId()))
                  message.setResult(ResultMessage.NotAuthorized);
               else if(order.getHandler() == null)
                  message.setResult(ResultMessage.DoesNotExist);
               else
               {
                  // Get the licenses for the order.

                  // Add each of the licenses to the user's list.

                  order.incrementProcessStep();
                  if(orders.updateOrderStep(order))
                     page = "/orderinfo?orderid=" + orderId;

                  orders.database.connection.commit();
                  message.setResult(ResultMessage.Success);

                  try
                  {
                     // TODO: Customize the email for a distributor.
                     // Send an email to notify the user of the payment approval.
                     String email = users.getUserEmail(order.getUserId());
                     if(email != null)
                     {
                        csimsoft.Email messenger = new csimsoft.Email();
                        messenger.sendPaymentMessage(message, email, orderId);
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
               message.addMessage("The order id is not valid.");
            }
         }
         catch(java.sql.SQLException sqle2)
         {
            orders.database.connection.rollback();
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

      orders.database.cleanup();
      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}
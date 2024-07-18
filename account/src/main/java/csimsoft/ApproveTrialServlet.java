/**
 * @(#)ApproveTrialServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2008/4/29
 */

package csimsoft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class ApproveTrialServlet extends HttpServlet
{

   public ApproveTrialServlet()
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
               "The trial approval information has been saved.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to save the trial approval information.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to approve a trial download.<br />" +
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
      // Get the request parameters.
      int orderId = 0;
      try
      {
         orderId = Integer.parseInt(request.getParameter("orderid"));
      }
      catch(NumberFormatException nfe)
      {
      }

      int approval = DatabaseLicenses.ApprovalUnassigned;
      try
      {
         approval = Integer.parseInt(request.getParameter("approval"));
      }
      catch(NumberFormatException nfe)
      {
      }

      String orderType = request.getParameter("otype");
      boolean isProduct = "product".equalsIgnoreCase(orderType);
      boolean isLicense = "license".equalsIgnoreCase(orderType);
      if("both".equalsIgnoreCase(orderType))
      {
         isProduct = true;
         isLicense = true;
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ApproveTrialServlet.Message message = new ApproveTrialServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is an administrator.
      String page = "/index.jsp";
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      boolean isAdmin = user.isAdministrator();
      boolean isDistributor = user.isDistributor();
      if(!LoginServlet.isUserLoggedIn(session))
         message.setResult(ResultMessage.LoginError);
      else if(!isAdmin && !isDistributor)
         message.setResult(ResultMessage.NotAuthorized);
      else if(orderId < 1 || (!isProduct && !isLicense))
      {
         message.setResult(ResultMessage.Failure);
         message.addMessage("<br />The order id or type is not valid.");
         if(isAdmin)
            page = "/allorders.jsp";
         else
            page = "/distorders.jsp";
      }
      else if(approval < DatabaseLicenses.DownloadRejected ||
         approval > DatabaseLicenses.DownloadApproved)
      {
         page = "/orderinfo?orderid=" + orderId;
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />Invalid approval parameter.");
      }
      else
      {
         if(isAdmin)
            page = "/allorders.jsp";
         else
            page = "/distorders.jsp";

         DatabaseUsers users = new DatabaseUsers();
         DatabaseLicenses licenses = new DatabaseLicenses(users.database);
         DatabaseOrders orders = new DatabaseOrders(users.database);
         DatabaseProcesses processes = new DatabaseProcesses(users.database);
         try
         {
            users.database.connect();
            users.database.connection.setAutoCommit(false);
            try
            {
               OrderInfo order = new OrderInfo(orderId);
               if(!orders.getOrderStep(orderId, order))
               {
                  message.setResult(ResultMessage.Failure);
                  message.addMessage(
                     "<br />The order id is not valid.");
               }
               else if(!isAdmin &&
                  !users.isDistributor(order.getUserId(), user.getUserId()))
               {
                  message.setResult(ResultMessage.NotAuthorized);
               }
               else
               {
                  // Get the order handler and products.
                  order.setHandler(processes.getProcessHandler(order.getProcessId()));
                  if(order.getHandler() == null)
                  {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage(
                        "<br />Unable to find the order process handler.");
                  }
                  else
                  {
                     int count = 0;
                     if(isProduct)
                     {
                        // Set the approval for each of the products.
                        count = licenses.setOrderDownloadApproval(order.getUserId(),
                           orderId, approval);
                     }

                     if(isLicense)
                     {
                        // Set the approval for each of the licenses.
                        count += licenses.setOrderLicenseApproval(order.getUserId(),
                           orderId, approval);
                     }

                     if(count == 0)
                     {
                        message.setResult(ResultMessage.Failure);
                        message.addMessage(
                           "<br />The order does not have any ");
                        if(isLicense && isProduct)
                           message.addMessage("licenses or products.");
                        else if(isLicense)
                           message.addMessage("licenses.");
                        else
                           message.addMessage("products.");
                     }
                     else
                     {
                        if(approval == DatabaseLicenses.DownloadApproved)
                        {
                           // Increment the order step.
                           order.incrementProcessStep();
                           if(orders.updateOrderStep(order))
                              page = "/orderinfo?orderid=" + orderId;
                        }
                        else if(approval == DatabaseLicenses.DownloadRejected)
                        {
                           // Remove the order from the database.
                           List<Integer> licenseIds = orders.removeOrder(orderId);

                           // Remove the user licenses for active orders.
                           Iterator<Integer> iter = licenseIds.iterator();
                           while(iter.hasNext())
                              licenses.removeClientLicense(iter.next().intValue());
                        }

                        users.database.connection.commit();
                        message.setResult(ResultMessage.Success);

                        try
                        {
                           // Get the product and license names for the order.
                           OrderSummary summary = new OrderSummary(orderId);
                           summary.setOrderSummary(orders);
                           ArrayList<String> products = new ArrayList<String>();
                           for(int i = 0; i < summary.getProductCount(); ++i)
                              products.add(summary.getProduct(i).getName());

                           ArrayList<String> keys = new ArrayList<String>();
                           for(int i = 0; i < summary.getLicenseCount(); ++i)
                              keys.add(summary.getLicense(i).getName());

                           // Send an email to notify the user of the trial
                           // approval/rejection.
                           String email = users.getUserEmail(order.getUserId());
                           if(email != null)
                           {
                              csimsoft.Email messenger = new csimsoft.Email();
                              messenger.sendTrialMessage(message, email, orderId,
                                 approval, products, keys);
                           }
                        }
                        catch(java.sql.SQLException sqle3)
                        {
                           message.addMessage(
                              "<br />Unable to send an email to the user.");
                           message.addException(sqle3);
                        }
                     }
                  }
               }
            }
            catch(java.sql.SQLException sqle2)
            {
               users.database.connection.rollback();
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

         users.database.cleanup();
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}
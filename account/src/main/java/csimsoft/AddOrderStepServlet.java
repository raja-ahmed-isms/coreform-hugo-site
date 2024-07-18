/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class AddOrderStepServlet extends HttpServlet
{

   public AddOrderStepServlet()
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
         if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to add to the order step.");
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
               "You are not authorized to add to the order step.<br />" +
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
      // Get the parameters from the request.
      int orderId = 0;
      try
      {
         orderId = Integer.parseInt(request.getParameter("orderid"));
      }
      catch(Exception nfe)
      {
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      AddOrderStepServlet.Message message = new AddOrderStepServlet.Message(
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
      else if(orderId < 1)
      {
         message.setResult(ResultMessage.Failure);
         message.addMessage("The order id is not valid.");
      }
      else
      {
         if(isAdmin)
            page = "/allorders.jsp";
         else
            page = "/distorders.jsp";

         DatabaseOrders orders = new DatabaseOrders();
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
                  // Get the order process handler. Make sure a distributor
                  // can access the order.
                  order.setHandler(processes.getProcessHandler(order.getProcessId()));
                  if(!isAdmin &&
                     !users.isDistributor(order.getUserId(), user.getUserId()))
                  {
                     message.setResult(ResultMessage.NotAuthorized);
                  }
                  else if(order.getHandler() == null)
                     message.setResult(ResultMessage.DoesNotExist);
                  else
                  {
                     // Increment the order step.
                     order.incrementProcessStep();
                     boolean moreSteps = orders.updateOrderStep(order);
                     if(moreSteps)
                        page = "/orderinfo?orderid=" + orderId;

                     orders.database.connection.commit();
                     message.setResult(ResultMessage.Success);

                     if(!moreSteps)
                     {
                        try
                        {
                           // Get the product and license names for the order.
                           OrderSummary summary = new OrderSummary(orderId);
                           summary.setOrderSummary(orders);
                           ArrayList<String> products = new ArrayList<String>();
                           for(int i = 0; i < summary.getProductCount(); ++i)
                              products.add(summary.getProduct(i).getName());

                           ArrayList<String> licenses = new ArrayList<String>();
                           for(int i = 0; i < summary.getLicenseCount(); ++i)
                              licenses.add(summary.getLicense(i).getName());

                           // Send an email to notify the user that the order is
                           // complete.
                           String email = users.getUserEmail(order.getUserId());
                           if(email != null)
                           {
                              csimsoft.Email messenger = new csimsoft.Email();
                              messenger.sendOrderCompleteMessage(message, email,
                                 orderId, products, licenses);
                           }
                        }
                        catch(SQLException sqle3)
                        {
                           message.addMessage(
                              "<br />Unable to send an email to the user.");
                           message.addException(sqle3);
                        }
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
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

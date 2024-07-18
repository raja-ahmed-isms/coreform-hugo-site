/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class SetPurchaseOrderServlet extends HttpServlet
{

   public SetPurchaseOrderServlet()
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
               "The PO has been successfully changed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to change the PO for this order.");
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
               "You are not authorized to change the PO for this order.");
         }
      }

      @Override
      public void addException(Exception e)
      {
         //this.Report.appendToMessage("<br />" + e);
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

      // Get the parameters from the request.
      int orderId = 0;
      try
      {
         orderId = Integer.parseInt(request.getParameter("orderid"));
      }
      catch(Exception nfe)
      {
      }

      String purchaseOrder = request.getParameter("po");
      if(purchaseOrder == null)
         purchaseOrder = "";
      else if(purchaseOrder.length() > 32)
         purchaseOrder = purchaseOrder.substring(0, 32);

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      SetPurchaseOrderServlet.Message message = new SetPurchaseOrderServlet.Message(
         LoginServlet.getResultMessage(session));

      // See if the user is an administrator.
      String page = "/index.jsp";
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      boolean isAdmin = user.isAdministrator();
      boolean isDistributor = user.isDistributor();

      String refNumber = null;
      ParameterList params = new ParameterList();
      params.setParameters(request);
      if((isAdmin || isDistributor) && params.hasParameter("rn"))
      {
         refNumber = request.getParameter("rn");
         if(refNumber == null)
            refNumber = "";
         else if(refNumber.length() > 64)
            refNumber = refNumber.substring(0, 64);
      }

      if(!LoginServlet.isUserLoggedIn(session))
         message.setResult(ResultMessage.LoginError);
      else if(orderId < 1)
      {
         message.setResult(ResultMessage.Failure);
         message.addMessage("The order id is not valid.");
      }
      else
      {
         if(isAdmin)
            page = "/allorders.jsp";
         else if(isDistributor)
            page = "/distorders.jsp";
         else
            page = "/orders.jsp";

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
                  boolean allowed = isAdmin || order.getUserId() == user.getUserId();
                  if(!allowed && isDistributor)
                     allowed = users.isDistributor(order.getUserId(), user.getUserId());

                  if(!allowed)
                     message.setResult(ResultMessage.NotAuthorized);
                  else if(order.getHandler() == null)
                     message.setResult(ResultMessage.DoesNotExist);
                  else
                  {
                     page = "/orderinfo?orderid=" + orderId;

                     // Set the order number.
                     orders.setOrderNumber(orderId, purchaseOrder);

                     // Set the reference number if needed.
                     if(refNumber != null)
                        orders.setReferenceNumber(orderId, refNumber);

                     orders.database.connection.commit();
                     message.setResult(ResultMessage.Success);
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

/**
 * @(#)OrderInfoServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/9/24
 */

package csimsoft;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class OrderInfoServlet extends HttpServlet
{

   public OrderInfoServlet()
   {
   }

   private static class Message implements ResultMessageAdapter
   {
      public ResultMessage Report = null;
      public boolean Admin = false;

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
               "The order process has not been configured correctly.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to view this order.<br />" +
               "Please log in as an administrator or distributor to do so.");
         }
      }

      @Override
      public void addException(Exception e)
      {
         if(this.Admin)
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
      OrderInfoServlet.Message message = new OrderInfoServlet.Message(
         LoginServlet.getResultMessage(session));

      // See if the user is logged in.
      UserInfo user = null;
      boolean isAdmin = false;
      boolean isDistributor = false;
      if(LoginServlet.isUserLoggedIn(session))
      {
         // Get the user from the session.
         user = LoginServlet.getUserInfo(session);
         isAdmin = user.isAdministrator();
         message.Admin = isAdmin;
         isDistributor = user.isDistributor();
      }

      String page = "/orders.jsp";
      String contextPath = request.getContextPath();
      if(isAdmin)
         page = "/allorders.jsp";
      else if(isDistributor)
         page = "/distorders.jsp";

      // Make sure the order id is specified in the request.
      int orderId = 0;
      try
      {
         orderId = Integer.parseInt(request.getParameter("orderid"));
      }
      catch(Exception nfe)
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      DatabaseOrders orders = new DatabaseOrders();
      try
      {
         orders.database.connect();
         orders.database.connection.setAutoCommit(false);
         try
         {
            boolean authorized = true;
            if(user != null)
            {
               // Make sure the user is authorized to view the order.
               if(isAdmin || orders.isOrderOwnedByUser(orderId, user.getUserId()))
               {
                  if(isDistributor)
                     isDistributor = user.getUserId() != orders.getOrderUserId(orderId);
               }
               else
                  authorized = false;
            }

            if(authorized)
            {
               // Get the handler for the order.
               OrderProcessHandler handler = orders.getOrderHandler(orderId);
               if(handler == null)
                  message.setResult(ResultMessage.Failure);
               else if(isAdmin || isDistributor)
                  page = handler.getAdminPage(orders, orderId);
               else
                  page = handler.getOrderPage(orders, orderId);

               if(page == null)
                  page = "/basicorder.jsp?orderid=" + orderId;
            }
            else
               message.setResult(ResultMessage.NotAuthorized);
         }
         catch(SQLException sqle2)
         {
            orders.database.connection.rollback();
            message.setResult(ResultMessage.ResourceError);
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
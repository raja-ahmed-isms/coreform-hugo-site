/**
 * @(#)RemoveOrderServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/12/5
 */

package csimsoft;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class RemoveOrderServlet extends HttpServlet
{

   public RemoveOrderServlet()
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
               "The order has been successfully removed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to remove the order.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to remove an order.<br />" +
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
      RemoveOrderServlet.Message message = new RemoveOrderServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to remove a user.
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

      // Get the optional parameters.
      ParameterBuilder params = new ParameterBuilder();
      String userId = request.getParameter("user");
      if(userId != null && !userId.isEmpty())
      {
         page = "/clientorders.jsp";
         params.add("user", userId);
      }
      else
      {
         String distId = request.getParameter("dist");
         if(isAdmin)
         {
            if(distId == null || distId.isEmpty())
               page = "/allorders.jsp";
            else
            {
               page = "/distorders.jsp";
               params.add("user", distId);
            }
         }
         else
            page = "/distorders.jsp";

         params.add("otype", request.getParameter("otype"));
         params.add("page", request.getParameter("page"));
      }

      DatabaseOrders orders = new DatabaseOrders();
      DatabaseLicenses licenses = new DatabaseLicenses(orders.database);
      try
      {
         // Get the order id and connect to the database.
         int orderId = Integer.parseInt(request.getParameter("orderid"));
         orders.database.connect();
         orders.database.connection.setAutoCommit(false);
         orders.database.statement = orders.database.connection.createStatement();
         try
         {
            // Make sure the distributor has access to the order.
            if(!isAdmin && !orders.isOrderOwnedByUser(orderId, user.getUserId()))
            {
               message.setResult(ResultMessage.NotAuthorized);
               page = "/distorders.jsp";
               params.clear();
            }
            else
            {
               List<Integer> licenseIds = orders.removeOrder(orderId);

               // Remove the user licenses for active orders.
               Iterator<Integer> iter = licenseIds.iterator();
               while(iter.hasNext())
                  licenses.removeClientLicense(iter.next().intValue());

               orders.database.connection.commit();
               message.setResult(ResultMessage.Success);
            }
         }
         catch(SQLException sqle2)
         {
            orders.database.connection.rollback();
            message.setResult(ResultMessage.Failure);
            message.addException(sqle2);
         }
      }
      catch(NumberFormatException nfe)
      {
         message.setResult(ResultMessage.Failure);
         message.addException(nfe);
      }
      catch(NullPointerException npe)
      {
         message.setResult(ResultMessage.Failure);
         message.addException(npe);
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

      orders.database.cleanup();
      response.sendRedirect(response.encodeRedirectURL(contextPath + page + params));
   }
}
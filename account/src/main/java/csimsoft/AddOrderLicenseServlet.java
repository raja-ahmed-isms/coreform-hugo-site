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


public class AddOrderLicenseServlet extends HttpServlet
{

   public AddOrderLicenseServlet()
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
               "The order license was successfully added.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to add the order license.");
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
               "You are not authorized to add order licenses.<br />" +
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

      int licenseId = 0;
      try
      {
         licenseId = Integer.parseInt(request.getParameter("license"));
      }
      catch(Exception nfe)
      {
      }

      int quantity = 1;
      try
      {
         quantity = Integer.parseInt(request.getParameter("quantity"));
      }
      catch(Exception nfe)
      {
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      AddOrderLicenseServlet.Message message = new AddOrderLicenseServlet.Message(
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
      else if(orderId < 1 || licenseId < 1 || quantity < 1)
         message.setResult(ResultMessage.Failure);
      else
      {
         page = "/orderinfo?orderid=" + orderId;

         DatabaseOrders orders = new DatabaseOrders();
         DatabaseUsers users = new DatabaseUsers(orders.database);
         DatabaseLicenses licenses = new DatabaseLicenses(orders.database);
         try
         {
            orders.database.connect();
            orders.database.connection.setAutoCommit(false);
            orders.database.statement = orders.database.connection.createStatement();
            try
            {
               OrderInfo order = new OrderInfo(orderId);
               if(orders.getOrderStep(orderId, order))
               {
                  // Make sure a distributor can access the order.
                  if(!isAdmin &&
                     !users.isDistributor(order.getUserId(), user.getUserId()))
                  {
                     message.setResult(ResultMessage.NotAuthorized);
                  }
                  else
                  {
                     // Set a default expiration date.
                     java.util.Calendar date = java.util.Calendar.getInstance();
                     date.add(java.util.Calendar.YEAR, 1);
                     date.set(java.util.Calendar.DAY_OF_MONTH,
                        date.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
                     String expiration = Database.formatDate(date);
                     for(int i = 0; i < quantity; ++i)
                     {
                        // Create a new user license.
                        int userLicenseId = licenses.addClientLicense(order.getUserId(),
                           licenseId, expiration, 0);

                        // Add the license to the order.
                        orders.database.statement.executeUpdate(
                           "INSERT INTO orderlicenses (orderid, licenseid) VALUES (" +
                           orderId + ", " + userLicenseId + ")");
                     }

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

/**
 * @(#)AddOrderServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/9/28
 */

package csimsoft;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class AddOrderServlet extends HttpServlet {

   public AddOrderServlet() {
   }

   private static class Message implements ResultMessageAdapter {
      public ResultMessage Report = null;

      public Message(ResultMessage message) {
         this.Report = message;
      }

      @Override
      public void setResult(int result) {
         this.Report.setResult(result);
         if (result == ResultMessage.DoesNotExist) {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage("The requested product/license id is not valid.");
         } else if (result == ResultMessage.Failure) {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage("Unable to order the product/license due to an internal error.");
         } else if (result == ResultMessage.NotAuthorized) {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage("You are not authorized to order for this user.");
         }
      }

      @Override
      public void addException(Exception e) {
         this.Report.appendToMessage("<br />" + e);
      }

      @Override
      public void addMessage(String extra) {
         this.Report.appendToMessage(extra);
      }
   }

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
         throws IOException, ServletException {
      doPost(request, response);
   }

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
         throws IOException, ServletException {
      // Get the result message object for the session.
      HttpSession session = request.getSession();
      AddOrderServlet.Message message =
            new AddOrderServlet.Message(LoginServlet.getResultMessage(session));

      String contextPath = request.getContextPath();
      String page = "/index.jsp";

      // TEMP
      /*
       * message.setResult(ResultMessage.ResourceError); message.addMessage(
       * "<br />We're sorry, but the system is temporarily down for maintainance. " +
       * "Please check back in an hour or two.");
       * 
       * response.sendRedirect(response.encodeRedirectURL(contextPath + page));
       */

      int licenseId = 0;
      int productId = 0;
      int numDays = 0;
      String license = request.getParameter("license");
      String product = request.getParameter("product");
      String days = request.getParameter("days");
      String lead = request.getParameter("source");
      try {
         if (license != null && !license.isEmpty()) {
            licenseId = Integer.parseInt(license);
            page = "/order.jsp?license=" + licenseId;
            if (days != null && !days.isEmpty()) {
               numDays = Integer.parseInt(days);
               page += "&days=" + numDays;
            }

            if (lead != null && !lead.isEmpty())
               page += "&source=" + lead;
         } else if (product != null && !product.isEmpty()) {
            productId = Integer.parseInt(product);
            page = "/order.jsp?product=" + productId;
         } else {
            response.sendRedirect(response.encodeRedirectURL(contextPath + page));
            return;
         }
      } catch (Exception nfe) {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // The user must be logged in to order a product.
      UserInfo user = LoginServlet.getUserInfo(session);
      int userId = user.getUserId();

      // An admin or distributor may be ordering for the user.
      boolean isAdmin = user.isAdministrator();
      boolean isDistributor = user.isDistributor();
      if (isAdmin || isDistributor) {
         try {
            String idString = request.getParameter("user");
            if (idString != null && !idString.isEmpty()) {
               userId = Integer.parseInt(idString);
               page = "/clientorders.jsp?user=" + userId;
            }
         } catch (NumberFormatException nfe) {
            response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         }
      }

      DatabaseUsers users = new DatabaseUsers();
      DatabaseProducts products = new DatabaseProducts(users.database);
      DatabaseLicenses licenses = new DatabaseLicenses(users.database);
      try {
         users.database.connect();
         users.database.connection.setAutoCommit(false);
         users.database.statement = users.database.connection.createStatement();
         PreparedStatement update = users.database.connection
               .prepareStatement("INSERT INTO userorders (orderid, orderdate, userid, processid, "
                     + "processstep, srcnumber) VALUES (?, ?, ?, ?, 0, ?)");
         users.database.addStatement(update);
         try {
            OrderInfo order = new OrderInfo();
            checkItem(users.database, message, order, productId, licenseId);
            AcctLogger.get().info("AddOrder after checkItem");
            AcctLogger.get().info("licenseId: " + licenseId);

            // The user must be logged in to order a product.
            if (message.Report.getResult() == ResultMessage.NoResult
                  && (userId < 1 || !LoginServlet.isUserLoggedIn(session))) {
               // If the order is for a trial, send the user to the sign up page.
               message.setResult(ResultMessage.LoginError);
               OrderProcessHandler handler = order.getHandler();

               // Send the user to the sign up page instead of a login page.
               if (handler.isTrial())
                  page = "/signup.jsp";

               // Add the order item to the session.
               if (licenseId > 0) {
                  session.setAttribute("ordertype", "license");
                  session.setAttribute("orderitem", license);
                  if (days != null && !days.isEmpty())
                     session.setAttribute("orderdays", days);
                  if (lead != null && !lead.isEmpty())
                     session.setAttribute("ordersource", lead);
               } else if (productId > 0) {
                  session.setAttribute("ordertype", "product");
                  session.setAttribute("orderitem", product);
               }
            }

            if (message.Report.getResult() == ResultMessage.NoResult) {
               checkOrder(users, message, order, userId, productId, licenseId,
                     isDistributor ? user.getUserId() : 0);
               AcctLogger.get().info("AddOrder after checkOrder");
            }

            if (message.Report.getResult() == ResultMessage.Exists) {
               AcctLogger.get().info("Order exists");
               page = "/orderinfo?orderid=" + order.getId();
            }
            else if (message.Report.getResult() == ResultMessage.NoResult) {
               AcctLogger.get().info("Getting next order id");
               // Get the next order id.
               int orderId = users.database.getNextId("ordercount", "orderid");
               if (lead != null && lead.length() > 32)
                  lead = lead.substring(0, 32);
               AcctLogger.get().info("Got next order id: " + orderId);

               // Add the order to the database.
               Date orderDate = new Date(System.currentTimeMillis());
               update.setInt(1, orderId);
               update.setDate(2, orderDate);
               update.setInt(3, userId);
               update.setInt(4, order.getProcessId());
               update.setString(5, lead);
               update.executeUpdate();
               AcctLogger.get().info("AddOrder after order update");

               // Add the product or license to the order.
               String name = null;
               if (productId > 0) {
                  name = products.getProductNamePath(productId);
                  users.database.statement
                        .executeUpdate("INSERT INTO orderproducts (orderid, productid) VALUES ("
                              + orderId + ", " + productId + ")");
               } else if (licenseId > 0) {
                  name = order.getProcessName();

                  // Set a default expiration.
                  String expiration = null;
                  if (numDays < 1) {
                     java.util.Calendar date = java.util.Calendar.getInstance();
                     date.add(java.util.Calendar.YEAR, 1);
                     date.set(java.util.Calendar.DAY_OF_MONTH,
                           date.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));

                     expiration = Database.formatDate(date);
                  }

                  // Create a new instance of the license.
                  int userLicenseId =
                        licenses.addClientLicense(userId, licenseId, expiration, numDays);
                  AcctLogger.get().info("AddOrder after addClientLicense");

                  // Add the new license to the order.
                  users.database.statement
                        .executeUpdate("INSERT INTO orderlicenses (orderid, licenseid) VALUES ("
                              + orderId + ", " + userLicenseId + ")");
                  AcctLogger.get().info("AddOrder after orderlicenses");
               }

               users.database.connection.commit();
               message.setResult(ResultMessage.Success);

               page = "/orderinfo?orderid=" + orderId;
               if (!isAdmin && !isDistributor) // TEMP
               {
                  String email = Integer.toString(userId);
                  String distEmail = null;

                  // Send the new order notification. Try to get the
                  // user's email address.
                  try {
                     email = users.getUserEmail(userId);
                     if (isDistributor)
                        distEmail = users.getUserEmail(user.getUserId());
                  } catch (Exception e) {
                  }

                  csimsoft.Email messenger = new csimsoft.Email();
                  messenger.sendOrderNotification(distEmail, email, orderId, name, licenseId < 1,
                        isAdmin || isDistributor);
               }
            }
         } catch (SQLException sqle2) {
            users.database.connection.rollback();
            message.setResult(ResultMessage.Failure);
            message.addException(sqle2);
         }
      } catch (SQLException sqle) {
         message.setResult(ResultMessage.ResourceError);
         message.addException(sqle);
      } catch (NamingException ne) {
         message.setResult(ResultMessage.ResourceError);
         message.addException(ne);
      }

      users.database.cleanup();
      if (message.Report.getResult() == ResultMessage.LoginError)
         message.Report.clear(); // Don't show message for login error.

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }

   public static void checkItem(Database database, ResultMessageAdapter message, OrderInfo order,
         int productId, int licenseId) throws SQLException {
      // Make sure the ordered product/license is valid.
      boolean productValid = false;
      if (licenseId > 0) {
         PreparedStatement query = database.connection.prepareStatement(
               "SELECT licensename, processid FROM licenseproducts WHERE licenseid = ?");
         database.addStatement(query);
         query.setInt(1, licenseId);
         database.results = query.executeQuery();
         productValid = database.results.next();
         if (productValid) {
            order.setProcessName(database.results.getString("licensename"));
            order.setProcessId(database.results.getInt("processid"));
         }

         database.results.close();
      } else if (productId > 0) {
         database.results = database.statement.executeQuery(
               "SELECT product, processid FROM products WHERE productid = " + productId);
         productValid = database.results.next();
         if (productValid) {
            order.setProcessName(database.results.getString("product"));
            order.setProcessId(database.results.getInt("processid"));
         }

         database.results.close();
      }

      if (!productValid) {
         // No license/product matching the id in the database.
         message.setResult(ResultMessage.DoesNotExist);
      } else if (order.getProcessId() < 1)
         message.setResult(ResultMessage.Failure);
      else {
         // Get the order handler for the process.
         DatabaseProcesses processes = new DatabaseProcesses(database);
         order.setHandler(processes.getProcessHandler(order.getProcessId()));
         if (order.getHandler() == null)
            message.setResult(ResultMessage.Failure);
      }
   }

   public static void checkOrder(DatabaseUsers users, ResultMessageAdapter message, OrderInfo order,
         int userId, int productId, int licenseId, int distributorId) throws SQLException {
      // Make sure the distributor owns the user.
      if (distributorId > 0 && userId != distributorId
            && !users.isDistributor(userId, distributorId)) {
         message.setResult(ResultMessage.NotAuthorized);
         return;
      }
      AcctLogger.get().info("checkOrder after authorization");

      // See if the user has already ordered the product. For license
      // orders, check if there is an active order. A license can be
      // re-ordered to increase the quantity.
      String condition = " AND orderid IN (SELECT orderid FROM userorders WHERE userid = " + userId;
      if (licenseId > 0)
         condition += " AND processstep > -1";

      condition += ")";
      if (licenseId > 0) {
         users.database.results = users.database.statement
               .executeQuery("SELECT orderid FROM orderlicenses WHERE licenseid IN "
                     + "(SELECT userlicenseid FROM licenses WHERE licenseid = " + licenseId
                     + " AND userid = " + userId + ")" + condition);
         if (users.database.results.next())
            order.setId(users.database.results.getInt("orderid"));

         users.database.results.close();
      } else if (productId > 0) {
         users.database.results = users.database.statement.executeQuery(
               "SELECT orderid FROM orderproducts WHERE productid = " + productId + condition);
         if (users.database.results.next())
            order.setId(users.database.results.getInt("orderid"));

         users.database.results.close();
      }

      if (order.getId() > 0)
         message.setResult(ResultMessage.Exists);
      AcctLogger.get().info("End check order");
   }
}

/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class DatabaseOrders extends DatabaseMethods
{

   public DatabaseOrders()
   {
      super();
   }

   public DatabaseOrders(Database db)
   {
      super(db);
   }

   public static class OrderLicenseItem
   {
      public int UserLicenseId = 0;
      public int LicenseId = 0;

      public OrderLicenseItem()
      {
      }

      public OrderLicenseItem(int userLicenseId, int licenseId)
      {
         this.UserLicenseId = userLicenseId;
         this.LicenseId = licenseId;
      }
   }

   public void getActiveUserOrders(int userId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT orderid, orderdate, ordernumber, distnumber FROM userorders " +
         "WHERE userid = " + userId + " AND processstep > -1 " +
         "ORDER BY orderid");
   }

   public void getCompletedUserOrders(int userId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT orderid, orderdate, ordernumber, distnumber FROM userorders " +
         "WHERE userid = " + userId + " AND processstep = -1 " +
         "ORDER BY orderid DESC");
   }

   public List<Integer> removeOrder(int orderId) throws SQLException
   {
      // If the order is still active, the user licenses need to be
      // deleted too. Return them to the caller to handle.
      ArrayList<Integer> licenses = new ArrayList<Integer>();
      if(this.getOrderStep(orderId) > -1)
      {
         this.database.results = this.database.statement.executeQuery(
            "SELECT licenseid FROM orderlicenses WHERE orderid = " +
            orderId);
         while(this.database.results.next())
            licenses.add(new Integer(this.database.results.getInt("licenseid")));

         this.database.results.close();
      }

      this.database.statement.executeUpdate(
         "DELETE FROM orderproducts WHERE orderid = " + orderId);
      this.database.statement.executeUpdate(
         "DELETE FROM orderlicenses WHERE orderid = " + orderId);
      this.database.statement.executeUpdate(
         "DELETE FROM userorders WHERE orderid = " + orderId);

      return licenses;
   }

   public void getOrderInfo(int orderId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT orderdate, processid, processstep, ordernumber, distnumber, " +
         "srcnumber FROM userorders WHERE orderid = " + orderId);
   }

   public boolean isOrderActive(int orderId) throws SQLException
   {
      boolean active = false;
      this.getOrderInfo(orderId);
      if(this.database.results.next())
      {
         int orderStep = this.database.results.getInt("processstep");
         if(orderStep > -1)
            active = true;
      }

      this.database.results.close();
      return active;
   }

   public boolean isOrderOwnedByUser(int orderId, int userId) throws SQLException
   {
      int orderUserId = this.getOrderUserId(orderId);
      boolean owns = userId == orderUserId;
      if(!owns)
      {
         // See if the user is the distributor for the order.
         this.database.results = this.database.statement.executeQuery(
            "SELECT userid FROM distributors WHERE groupid IN " +
            "(SELECT groupid FROM users WHERE userid = " + orderUserId + ")");
         if(this.database.results.next())
            owns = userId == this.database.results.getInt("userid");

         this.database.results.close();
      }

      return owns;
   }

   public int getOrderUserId(int orderId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      int userId = 0;
      this.database.results = this.database.statement.executeQuery(
         "SELECT userid FROM userorders WHERE orderid = " + orderId);
      if(this.database.results.next())
         userId = this.database.results.getInt("userid");

      this.database.results.close();
      return userId;
   }

   public void getOrderUser(int orderId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT userid, firstname, lastname, email FROM users WHERE userid = " +
         "(SELECT userid FROM userorders WHERE orderid = " + orderId + ")");
   }

   public boolean getOrderUser(int orderId, UserProfileInfo info) throws SQLException
   {
      this.getOrderUser(orderId);
      boolean exists = this.database.results.next();
      if(exists)
      {
         info.setId(this.database.results.getInt("userid"));
         info.setProfile(this.database.results);
      }

      this.database.results.close();
      return exists;
   }

   public int getOrderStep(int orderId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      int step = 0;
      this.database.results = this.database.statement.executeQuery(
         "SELECT processstep FROM userorders WHERE orderid = " + orderId);
      if(this.database.results.next())
         step = this.database.results.getInt("processstep");

      this.database.results.close();
      return step;
   }

   public boolean getOrderStep(int orderId, OrderInfo order) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT userid, processid, processstep FROM userorders WHERE orderid = " +
         orderId);
      boolean exists = this.database.results.next();
      if(exists)
         order.setStep(this.database.results);

      this.database.results.close();
      return exists;
   }

   public void setOrderStep(int orderId, int step) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.statement.executeUpdate(
         "UPDATE userorders SET processstep = " + step +
         " WHERE orderid = " + orderId);
   }

   public void setOrderNumber(int orderId, String orderNumber) throws SQLException
   {
      PreparedStatement statement = this.database.connection.prepareStatement(
         "UPDATE userorders SET ordernumber = ? WHERE orderid = ?");
      this.database.addStatement(statement);
      statement.setString(1, orderNumber);
      statement.setInt(2, orderId);
      statement.executeUpdate();
   }

   public void setReferenceNumber(int orderId, String refNumber) throws SQLException
   {
      PreparedStatement statement = this.database.connection.prepareStatement(
         "UPDATE userorders SET distnumber = ? WHERE orderid = ?");
      this.database.addStatement(statement);
      statement.setString(1, refNumber);
      statement.setInt(2, orderId);
      statement.executeUpdate();
   }

   public List<Integer> getOrderProductIds(int orderId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      ArrayList<Integer> products = new ArrayList<Integer>();
      this.database.results = this.database.statement.executeQuery(
         "SELECT productid FROM orderproducts WHERE orderid = " + orderId);
      while(this.database.results.next())
         products.add(new Integer(this.database.results.getInt("productid")));

      this.database.results.close();
      return products;
   }

   public void getOrderProducts(int orderId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT orderproducts.productid, products.product " +
         "FROM orderproducts INNER JOIN products ON orderproducts.productid = " +
         "products.productid WHERE orderproducts.orderid = " + orderId +
         " ORDER BY products.product");
   }

   public void getOrderProducts(int orderId, List<OrderSummaryDownload> products)
      throws SQLException
   {
      this.getOrderProducts(orderId);
      while(this.database.results.next())
      {
         OrderSummaryDownload product = new OrderSummaryDownload(
            this.database.results.getInt("productid"),
            this.database.results.getString("product"));
         products.add(product);
      }

      this.database.results.close();
   }

   public void getOrderLicenses(int orderId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT userlicenseid, licenseid, licensetype, quantity FROM licenses " +
         "WHERE userlicenseid IN (SELECT licenseid FROM orderlicenses " +
         "WHERE orderid = " + orderId + ") ORDER BY userlicenseid");
   }

   public int getLicenseOrderId(int userId, int licenseId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      int orderId = 0;
      this.database.results = this.database.statement.executeQuery(
         "SELECT orderid FROM userorders WHERE userid = " + userId +
         " AND orderid IN (SELECT orderid FROM orderlicenses " +
         "WHERE licenseid = " + licenseId + ")");
      if(this.database.results.next())
         orderId = this.database.results.getInt("orderid");

      this.database.results.close();
      return orderId;
   }

   public OrderProcessHandler getOrderHandler(int orderId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      String handlerName = null;
      this.database.results = this.database.statement.executeQuery(
         "SELECT handlername FROM orderprocess WHERE processid IN " +
         "(SELECT processid FROM userorders WHERE orderid = " + orderId + ")");
      if(this.database.results.next())
         handlerName = this.database.results.getString("handlername");

      this.database.results.close();
      return DatabaseProcesses.getHandler(handlerName);
   }

   public void completeOrder(int orderId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      int userId = 0;
      this.database.results = this.database.statement.executeQuery(
         "SELECT userid FROM userorders WHERE orderid = " + orderId);
      if(this.database.results.next())
         userId = this.database.results.getInt("userid");

      this.database.results.close();
      if(userId > 0)
         this.completeOrder(orderId, userId);
   }

   public void completeOrder(int orderId, int userId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      // Get the licenses for the order.
      ArrayList<DatabaseOrders.OrderLicenseItem> licenses =
         new ArrayList<DatabaseOrders.OrderLicenseItem>();
      this.database.results = this.database.statement.executeQuery(
         "SELECT userlicenseid, licenseid FROM licenses WHERE userlicenseid IN " +
         "(SELECT licenseid FROM orderlicenses WHERE orderid = " + orderId + ")");
      while(this.database.results.next())
      {
         int userLicenseId = this.database.results.getInt("userlicenseid");
         int licenseId = this.database.results.getInt("licenseid");
         licenses.add(new DatabaseOrders.OrderLicenseItem(userLicenseId, licenseId));
      }

      this.database.results.close();

      // Generate license keys for the order licenses.
      PreparedStatement statement = this.database.connection.prepareStatement(
         "UPDATE licenses SET licensekey = ? WHERE userlicenseid = ?");
      this.database.addStatement(statement);
      PreparedStatement check = this.database.connection.prepareStatement(
         "SELECT userlicenseid FROM licenses WHERE licensekey = ?");
      this.database.addStatement(check);
      Iterator<DatabaseOrders.OrderLicenseItem> iter = licenses.iterator();
      while(iter.hasNext())
      {
         DatabaseOrders.OrderLicenseItem license = iter.next();
         String key = null;
         for(int i = 0; key == null && i < 3; ++i)
         {
            key = Database.generateKey();

            // Make sure the license key isn't a duplicate.
            if(key != null)
            {
               check.setString(1, key);
               this.database.results = check.executeQuery();
               if(this.database.results.next())
                  key = null;

               this.database.results.close();
            }
         }

         if(key != null)
         {
            statement.setString(1, key);
            statement.setInt(2, license.UserLicenseId);
            statement.executeUpdate();
         }
      }

      // Get the products from the user's download list.
      Set<Integer> downloads = this.getUserDownloads(userId);

      // Get the products for the order.
      TreeSet<Integer> products = new TreeSet<Integer>();
      this.database.results = this.database.statement.executeQuery(
         "SELECT productid FROM orderproducts WHERE orderid = " + orderId);
      while(this.database.results.next())
         products.add(new Integer(this.database.results.getInt("productid")));

      this.database.results.close();

      // Get the products from the license platforms.
      iter = licenses.iterator();
      while(iter.hasNext())
      {
         DatabaseOrders.OrderLicenseItem license = iter.next();
         this.getLicenseDownloads(license.LicenseId, license.UserLicenseId, products);
      }

      // Add each of the products to the user's download list.
      this.addNewUserDownloads(downloads, products, userId);

      this.setOrderStep(orderId, -1);
   }

   public Set<Integer> getUserDownloads(int userId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      // Get the user's current list of downloads.
      TreeSet<Integer> downloads = new TreeSet<Integer>();
      this.database.results = this.database.statement.executeQuery(
         "SELECT productid FROM userdownloads WHERE userid = " + userId);
      while(this.database.results.next())
         downloads.add(new Integer(this.database.results.getInt("productid")));

      this.database.results.close();
      return downloads;
   }

   public void getLicenseDownloads(int licenseId, int userLicenseId,
      Set<Integer> products) throws SQLException
   {
      if(products == null)
         return;

      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      // Get the downloads for the given user license platforms.
      this.database.results = this.database.statement.executeQuery(
         "SELECT productid FROM platformdownloads WHERE licenseid = " +
         licenseId + " AND platformid IN " +
         "(SELECT platformid FROM userplatforms WHERE userlicenseid = " +
         userLicenseId + ")");
      while(this.database.results.next())
         products.add(new Integer(this.database.results.getInt("productid")));

      this.database.results.close();
   }

   public void addNewUserDownloads(int userId, int licenseId, int userLicenseId)
      throws SQLException
   {
      Set<Integer> downloads = this.getUserDownloads(userId);
      TreeSet<Integer> products = new TreeSet<Integer>();
      this.getLicenseDownloads(licenseId, userLicenseId, products);
      this.addNewUserDownloads(downloads, products, userId);
   }

   public void addNewUserDownloads(Set<Integer> downloads, Set<Integer> products,
      int userId) throws SQLException
   {
      // Add each of the products to the user's download list.
      Iterator<Integer> jter = products.iterator();
      while(jter.hasNext())
      {
         Integer productId = jter.next();
         if(!downloads.contains(productId))
         {
            this.database.statement.executeUpdate(
               "INSERT INTO userdownloads (userid, productid) VALUES (" +
               userId + ", " + productId.intValue() + ")");
         }
      }
   }

   public boolean updateOrderStep(OrderInfo order) throws SQLException
   {
      return this.updateOrderStep(order.getId(), order.getUserId(),
         order.getProcessStep(), order.getHandler());
   }

   public boolean updateOrderStep(int orderId, int userId, int step,
      OrderProcessHandler handler) throws SQLException
   {
      boolean hasMoreSteps = step < handler.getNumberOfSteps();
      if(hasMoreSteps)
         this.setOrderStep(orderId, step);
      else
         this.completeOrder(orderId, userId);

      return hasMoreSteps;
   }
}

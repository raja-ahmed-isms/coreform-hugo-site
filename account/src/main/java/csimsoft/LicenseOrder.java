/**
 * @(#)LicenseOrder.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/10/22
 */

package csimsoft;

import java.sql.SQLException;


public class LicenseOrder implements OrderProcessHandler
{

   public LicenseOrder()
   {
   }

   @Override
   public void changeUserId(Database database, int oldId, int newId) throws SQLException
   {
   }

   @Override
   public void removeUser(Database database, int userId) throws SQLException
   {
   }

   @Override
   public void changeProductId(Database database, int oldId, int newId)
      throws SQLException
   {
   }

   @Override
   public void removeProduct(Database database, int productId) throws SQLException
   {
   }

   @Override
   public void changeLicenseId(Database database, int oldId, int newId)
      throws SQLException
   {
   }

   @Override
   public void removeLicense(Database database, int licenseId) throws SQLException
   {
   }

   @Override
   public void changeUserLicenseId(Database database, int oldId, int newId)
      throws SQLException
   {
   }

   @Override
   public void removeUserLicense(Database database, int licenseId) throws SQLException
   {
   }

   @Override
   public void changeId(Database database, int oldId, int newId) throws SQLException
   {
   }

   @Override
   public void cleanup(Database database, int processId) throws SQLException
   {
   }

   @Override
   public String getConfigurePage(int processId)
   {
      return null;
   }

   @Override
   public boolean isTrial()
   {
      return false;
   }

   @Override
   public void addToProcess(Database database, int productId) throws SQLException
   {
   }

   @Override
   public void removeFromProcess(Database database, int productId) throws SQLException
   {
   }

   @Override
   public int getNumberOfSteps()
   {
      // 0 -> Get license type and quantity then wait for payment.
      return 1;
   }

   @Override
   public String getOrderPage(DatabaseOrders orders, int orderId) throws SQLException
   {
      OrderInfo order = new OrderInfo(orderId);
      orders.getOrderStep(orderId, order);
      order.setHandler(this);
      String page = null;

      // 0 -> infowait.jsp
      if(order.getProcessStep() == 0)
         page = "/infowait.jsp?orderid=" + orderId;

      return page;
   }

   @Override
   public String getAdminPage(DatabaseOrders orders, int orderId) throws SQLException
   {
      OrderInfo order = new OrderInfo(orderId);
      orders.getOrderStep(orderId, order);
      order.setHandler(this);
      String page = null;

      // 0 -> licenselist.jsp
      if(order.getProcessStep() == 0)
         page = "/licenselist.jsp?orderid=" + orderId;

      return page;
   }
}
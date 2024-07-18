/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.SQLException;


public class ApprovedLicenseOrder implements OrderProcessHandler
{

   public ApprovedLicenseOrder()
   {
   }

   @Override
   public void changeUserId(Database database, int oldId, int newId) throws SQLException
   {
      database.statement.executeUpdate(
         "UPDATE userapprovals SET userid = " + newId + " WHERE userid = " + oldId);
   }

   @Override
   public void removeUser(Database database, int userId) throws SQLException
   {
      database.statement.executeUpdate(
         "DELETE FROM userapprovals WHERE userid = " + userId);
   }

   @Override
   public void changeProductId(Database database, int oldId, int newId)
      throws SQLException
   {
      database.statement.executeUpdate(
         "UPDATE userapprovals SET productid = " + newId +
         " WHERE productid = " + oldId);
   }

   @Override
   public void removeProduct(Database database, int productId) throws SQLException
   {
      database.statement.executeUpdate(
         "DELETE FROM userapprovals WHERE productid = " + productId);
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
      // 0 -> Get product quantities
      // 1 -> Wait for admin or distributor approval
      return 2;
   }

   @Override
   public String getOrderPage(DatabaseOrders orders, int orderId) throws SQLException
   {
      OrderInfo order = new OrderInfo(orderId);
      orders.getOrderStep(orderId, order);
      order.setHandler(this);
      String page = null;

      // 0 -> orderlquantity.jsp
      //if(order.getProcessStep() == 0)
      //   page = "/orderlquantity.jsp?orderid=" + orderId;

      // 1 -> paymentwait.jsp
      if(order.getProcessStep() == 1)
         page = "/paymentwait.jsp?orderid=" + orderId;

      return page;
   }

   @Override
   public String getAdminPage(DatabaseOrders orders, int orderId) throws SQLException
   {
      OrderInfo order = new OrderInfo(orderId);
      orders.getOrderStep(orderId, order);
      order.setHandler(this);
      String page = null;

      // 0 -> orderlquantity.jsp
      //if(order.getProcessStep() == 0)
      //   page = "/orderlquantity.jsp?orderid=" + orderId;

      // 1 -> verifypayment.jsp
      if(order.getProcessStep() == 1)
         page = "/verifypayment.jsp?orderid=" + orderId;

      return page;
   }
}

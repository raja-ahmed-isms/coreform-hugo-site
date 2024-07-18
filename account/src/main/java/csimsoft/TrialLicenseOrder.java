/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.SQLException;


public class TrialLicenseOrder implements OrderProcessHandler
{

   public TrialLicenseOrder()
   {
   }

   @Override
   public void changeUserId(Database database, int oldId, int newId) throws SQLException
   {
      database.statement.executeUpdate(
         "UPDATE licenseapprovals SET userid = " + newId + " WHERE userid = " + oldId);
   }

   @Override
   public void removeUser(Database database, int userId) throws SQLException
   {
      database.statement.executeUpdate(
         "DELETE FROM licenseapprovals WHERE userid = " + userId);
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
      database.statement.executeUpdate(
         "DELETE FROM licenseapprovals WHERE licenseid IN " +
         "(SELECT userlicenseid FROM licenses WHERE licenseid = " + licenseId + ")");
   }

   @Override
   public void changeUserLicenseId(Database database, int oldId, int newId)
      throws SQLException
   {
      database.statement.executeUpdate(
         "UPDATE licenseapprovals SET licenseid = " + newId +
         " WHERE licenseid = " + oldId);
   }

   @Override
   public void removeUserLicense(Database database, int licenseId) throws SQLException
   {
      database.statement.executeUpdate(
         "DELETE FROM licenseapprovals WHERE licenseid = " + licenseId);
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
      return true;
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

      // 0 -> trialwait.jsp
      if(order.getProcessStep() == 0)
         page = "/trialwait.jsp?orderid=" + orderId;

      return page;
   }

   @Override
   public String getAdminPage(DatabaseOrders orders, int orderId) throws SQLException
   {
      OrderInfo order = new OrderInfo(orderId);
      orders.getOrderStep(orderId, order);
      order.setHandler(this);
      String page = null;

      // 0 -> licensetrial.jsp
      if(order.getProcessStep() == 0)
         page = "/licensetrial.jsp?orderid=" + orderId;

      return page;
   }
}

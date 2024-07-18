/**
 * @(#)ApprovedTrialOrder.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2008/3/21
 */

package csimsoft;

import java.sql.SQLException;


public class ApprovedTrialOrder implements OrderProcessHandler
{

   public ApprovedTrialOrder()
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
      return 1;
   }

   @Override
   public String getOrderPage(DatabaseOrders orders, int orderId) throws SQLException
   {
      OrderInfo order = new OrderInfo(orderId);
      orders.getOrderStep(orderId, order);
      if(order.getProcessStep() == 0)
      {
         // If the user has already been approved, skip to the next step.
         DatabaseLicenses licenses = new DatabaseLicenses(orders.database);
         if(licenses.isOrderDownloadApproved(order.getUserId(), orderId))
            order.incrementProcessStep();
         else
         {
            // Return the approval trial wait page.
            return "/trialwait.jsp?orderid=" + orderId;
         }
      }

      if(order.getProcessStep() == getNumberOfSteps())
      {
         orders.completeOrder(orderId, order.getUserId());
         orders.database.connection.commit();
      }

      return null;
   }

   @Override
   public String getAdminPage(DatabaseOrders orders, int orderId) throws SQLException
   {
      int step = orders.getOrderStep(orderId);
      if(step == 0)
      {
         // Return the trial approval page.
         return "/verifytrial.jsp?orderid=" + orderId;
      }

      return null;
   }
}
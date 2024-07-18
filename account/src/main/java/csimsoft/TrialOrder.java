/**
 * @(#)TrialOrder.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/12/24
 */

package csimsoft;

import java.sql.SQLException;


public class TrialOrder implements OrderProcessHandler
{

   public TrialOrder()
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
      return 0;
   }

   @Override
   public String getOrderPage(DatabaseOrders orders, int orderId) throws SQLException
   {
      int step = orders.getOrderStep(orderId);
      if(step == getNumberOfSteps())
      {
         orders.completeOrder(orderId);
         orders.database.connection.commit();
      }

      return null;
   }

   @Override
   public String getAdminPage(DatabaseOrders orders, int orderId) throws SQLException
   {
      return null;
   }
}
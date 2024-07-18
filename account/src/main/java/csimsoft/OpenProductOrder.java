/**
 * @(#)OpenProductOrder.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/3/30
 */

package csimsoft;

import java.sql.SQLException;


public class OpenProductOrder implements OrderProcessHandler
{

   public OpenProductOrder()
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
      database.statement.executeUpdate(
         "UPDATE openproducts SET productid = " + newId +
         " WHERE productid = " + oldId);
   }

   @Override
   public void removeProduct(Database database, int productId) throws SQLException
   {
      database.statement.executeUpdate(
         "DELETE FROM openproducts WHERE productid = " + productId);
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
      database.statement.executeUpdate("DELETE FROM openproducts");
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
      database.results = database.statement.executeQuery(
         "SELECT productid FROM openproducts WHERE productid = " + productId);
      boolean exists = database.results.next();
      database.results.close();
      if(!exists)
      {
         database.statement.executeUpdate(
            "INSERT INTO openproducts (productid) VALUES (" + productId + ")");
      }
   }

   @Override
   public void removeFromProcess(Database database, int productId) throws SQLException
   {
      database.statement.executeUpdate(
         "DELETE FROM openproducts WHERE productid = " + productId);
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
         // Set the step to indicate that the process is complete.
         orders.setOrderStep(orderId, -1);
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
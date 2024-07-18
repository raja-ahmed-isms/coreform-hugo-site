/**
 * @(#)OrderProcessHandler.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/9/24
 */

package csimsoft;

import java.sql.SQLException;


public interface OrderProcessHandler
{
   void changeUserId(Database database, int oldId, int newId) throws SQLException;
   void removeUser(Database database, int userId) throws SQLException;
   void changeProductId(Database database, int oldId, int newId) throws SQLException;
   void removeProduct(Database database, int productId) throws SQLException;
   void changeLicenseId(Database database, int oldId, int newId) throws SQLException;
   void removeLicense(Database database, int licenseId) throws SQLException;
   void changeUserLicenseId(Database database, int oldId, int newId) throws SQLException;
   void removeUserLicense(Database database, int licenseId) throws SQLException;

   void changeId(Database database, int oldId, int newId) throws SQLException;
   void cleanup(Database database, int processId) throws SQLException;

   String getConfigurePage(int processId);
   boolean isTrial();

   void addToProcess(Database database, int productId) throws SQLException;
   void removeFromProcess(Database database, int productId) throws SQLException;

   int getNumberOfSteps();
   String getOrderPage(DatabaseOrders orders, int orderId) throws SQLException;
   String getAdminPage(DatabaseOrders orders, int orderId) throws SQLException;
}
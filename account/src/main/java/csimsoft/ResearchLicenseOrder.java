/**
 * @(#)ResearchLicenseOrder.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/4/7
 */

package csimsoft;

import java.sql.SQLException;


public class ResearchLicenseOrder implements OrderProcessHandler
{

   public ResearchLicenseOrder()
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
         "UPDATE processlicense SET productid = " + newId +
         " WHERE productid = " + oldId);
   }

   @Override
   public void removeProduct(Database database, int productId) throws SQLException
   {
      database.statement.executeUpdate(
         "DELETE FROM processlicense WHERE productid = " + productId);
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
      database.statement.executeUpdate(
         "UPDATE processlicense SET processid = " + newId +
         " WHERE processid = " + oldId);
   }

   @Override
   public void cleanup(Database database, int processId) throws SQLException
   {
      database.statement.executeUpdate(
         "DELETE FROM processlicense WHERE processid = " + processId);
   }

   @Override
   public String getConfigurePage(int processId)
   {
      return "/licenseconfig.jsp?process=" + processId;
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
      // 1 -> Get university info
      // 2 -> Get second contact info
      // 3 -> Send/fax license, wait for admin confirm
      // 4 -> Arrange for payment, wait for admin confirm
      return 5;
   }

   @Override
   public String getOrderPage(DatabaseOrders orders, int orderId) throws SQLException
   {
      OrderInfo order = new OrderInfo(orderId);
      orders.getOrderStep(orderId, order);
      order.setHandler(this);
      int originalStep = order.getProcessStep();
      String page = null;

      // 0 -> orderlquantity.jsp
      //if(order.getProcessStep() == 0)
      //   page = "/orderlquantity.jsp?orderid=" + orderId;

      int universityId = 0;
      if(order.getProcessStep() == 1 || order.getProcessStep() == 2)
      {
         DatabaseUsers users = new DatabaseUsers(orders.database);
         universityId = users.getUniversityId(order.getUserId());
      }

      // 1 -> orderuniversity.jsp
      if(order.getProcessStep() == 1)
      {
         // See if the university is already entered.
         if(universityId > 0)
            order.incrementProcessStep();
         else
            page = "/orderuniversity.jsp?orderid=" + orderId;
      }

      // 2 -> ordercontact.jsp
      if(order.getProcessStep() == 2)
      {
         // See if the contact information is already entered.
         if(this.hasContacts(orders.database, universityId))
            order.incrementProcessStep();
         else
            page = "/ordercontact.jsp?orderid=" + orderId;
      }

      // 3 -> orderlicense.jsp
      if(order.getProcessStep() == 3)
      {
         // If the user's license agreement has already been received,
         // skip to the next step. Otherwise, return the order license
         // page.
         DatabaseLicenses licenses = new DatabaseLicenses(orders.database);
         if(licenses.isProcessLicenseApproved(order.getUserId(), order.getProcessId()))
            order.incrementProcessStep();
         else
            page = "/orderlicense.jsp?orderid=" + orderId;
      }

      // 4 -> paymentwait.jsp
      if(order.getProcessStep() == 4)
         page = "/paymentwait.jsp?orderid=" + orderId;

      // If steps have been skipped, store the new step in the database.
      if(originalStep != order.getProcessStep())
      {
         orders.updateOrderStep(order);
         orders.database.connection.commit();
      }

      return page;
   }

   @Override
   public String getAdminPage(DatabaseOrders orders, int orderId) throws SQLException
   {
      OrderInfo order = new OrderInfo(orderId);
      orders.getOrderStep(orderId, order);
      order.setHandler(this);
      int originalStep = order.getProcessStep();
      String page = null;

      // 0 -> orderlquantity.jsp
      //if(order.getProcessStep() == 0)
      //   page = "/orderlquantity.jsp?orderid=" + orderId;

      int universityId = 0;
      if(order.getProcessStep() == 1 || order.getProcessStep() == 2)
      {
         DatabaseUsers users = new DatabaseUsers(orders.database);
         universityId = users.getUniversityId(order.getUserId());
      }

      // 1 -> orderuniversity.jsp
      if(order.getProcessStep() == 1)
      {
         // See if the university is already entered.
         if(universityId > 0)
            order.incrementProcessStep();
         else
            page = "/orderuniversity.jsp?orderid=" + orderId;
      }

      // 2 -> ordercontact.jsp
      if(order.getProcessStep() == 2)
      {
         // See if the contact information is already entered.
         if(this.hasContacts(orders.database, universityId))
            order.incrementProcessStep();
         else
            page = "/ordercontact.jsp?orderid=" + orderId;
      }

      // 3 -> verifylicense.jsp
      if(order.getProcessStep() == 3)
      {
         // If the user's license agreement has already been received,
         // skip to the next step. Otherwise, return the verify license
         // page.
         DatabaseLicenses licenses = new DatabaseLicenses(orders.database);
         if(licenses.isProcessLicenseApproved(order.getUserId(), order.getProcessId()))
            order.incrementProcessStep();
         else
            page = "/verifylicense.jsp?orderid=" + orderId;
      }

      // 4 -> verifypayment.jsp
      if(order.getProcessStep() == 4)
         page = "/verifypayment.jsp?orderid=" + orderId;

      // If steps have been skipped, store the new step in the database.
      if(originalStep != order.getProcessStep())
      {
         orders.updateOrderStep(order);
         orders.database.connection.commit();
      }

      return page;
   }

   private boolean hasContacts(Database database, int universityId) throws SQLException
   {
      int adminId = 0;
      int techId = 0;
      if(universityId > 0)
      {
         DatabaseUniversities universities = new DatabaseUniversities(database);
         universities.getContacts(universityId);
         if(database.results.next())
         {
            adminId = database.results.getInt("adminid");
            techId = database.results.getInt("techid");
         }

         database.results.close();
      }

      return adminId > 0 && techId > 0;
   }
}
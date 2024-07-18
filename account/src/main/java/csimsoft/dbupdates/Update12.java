/**
 * @(#)Update12.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/5/10
 */

package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;


public class Update12 implements csimsoft.DatabaseUpdate
{

   public Update12()
   {
   }

   public static class InfoItem
   {
      private int UserId = 0;
      private String Company = " ";
      private String Phone = " ";
      private String Address = " ";
      private String Country = " ";

      public InfoItem(int userId)
      {
         this.UserId = userId;
      }

      public int getUserId()
      {
         return this.UserId;
      }

      public String getCompany()
      {
         return this.Company;
      }

      public void setCompany(String company)
      {
         this.Company = company == null ? "" : company;
      }

      public String getPhone()
      {
         return this.Phone;
      }

      public void setPhone(String phone)
      {
         this.Phone = phone == null || phone.isEmpty() ? " " : phone;
      }

      public String getAddress()
      {
         return this.Address;
      }

      public void setAddress(String address)
      {
         this.Address = address == null || address.isEmpty() ? " " : address;
      }

      public String getCountry()
      {
         return this.Country;
      }

      public void setCountry(String country)
      {
         this.Country = country == null || country.isEmpty() ? " " : country;
      }
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Combine the userinfo table with the users table.
      database.statement.executeUpdate(
         "ALTER TABLE users ADD COLUMN company VARCHAR(128)");
      database.statement.executeUpdate(
         "ALTER TABLE users ADD COLUMN phone VARCHAR(32)");
      database.statement.executeUpdate(
         "ALTER TABLE users ADD COLUMN address VARCHAR(512)");
      database.statement.executeUpdate(
         "ALTER TABLE users ADD COLUMN country VARCHAR(32)");

      // Move the data to the user table.
      ArrayList<Update12.InfoItem> items = new ArrayList<Update12.InfoItem>();
      database.results = database.statement.executeQuery(
         "SELECT userid, company, phone, address, country FROM userinfo");
      while(database.results.next())
      {
         Update12.InfoItem item = new Update12.InfoItem(
            database.results.getInt("userid"));
         item.setCompany(database.results.getString("company"));
         item.setPhone(database.results.getString("phone"));
         item.setAddress(database.results.getString("address"));
         item.setCountry(database.results.getString("country"));
         items.add(item);
      }

      database.results.close();
      database.results = database.statement.executeQuery(
         "SELECT userid FROM users WHERE userid NOT IN (SELECT userid FROM userinfo)");
      while(database.results.next())
      {
         Update12.InfoItem item = new Update12.InfoItem(
            database.results.getInt("userid"));
         items.add(item);
      }

      database.results.close();
      Iterator<Update12.InfoItem> iter = items.iterator();
      while(iter.hasNext())
      {
         Update12.InfoItem item = iter.next();
         database.statement.executeUpdate(
            "UPDATE users SET company = '" + item.getCompany() + "', phone = '" +
            item.getPhone() + "', address = '" + item.getAddress() + "', country = '" +
            item.getCountry() + "' WHERE userid = " + item.getUserId());
      }

      // Make the columns required.
      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE users MODIFY COLUMN phone VARCHAR(32) NOT NULL");
         database.statement.executeUpdate(
            "ALTER TABLE users MODIFY COLUMN address VARCHAR(512) NOT NULL");
         database.statement.executeUpdate(
            "ALTER TABLE users MODIFY COLUMN country VARCHAR(32) NOT NULL");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE users ALTER COLUMN phone NOT NULL");
         database.statement.executeUpdate(
            "ALTER TABLE users ALTER COLUMN address NOT NULL");
         database.statement.executeUpdate(
            "ALTER TABLE users ALTER COLUMN country NOT NULL");
      }

      // Remove the userinfo table.
      database.statement.executeUpdate(
         "DELETE FROM userinfo");
      database.statement.executeUpdate(
         "DROP TABLE userinfo");

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}
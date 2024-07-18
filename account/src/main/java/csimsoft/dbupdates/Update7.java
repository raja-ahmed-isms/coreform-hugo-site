/**
 * @(#)Update7.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/1/24
 */

package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update7 implements csimsoft.DatabaseUpdate
{

   public Update7()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      database.statement.executeUpdate(
         "CREATE TABLE licensecount (" +
         "  licenseid INT NOT NULL)");

      database.statement.executeUpdate(
         "CREATE TABLE productlicenses (" +
         "  licenseid INT NOT NULL," +
         "  licensename VARCHAR(64) NOT NULL," +
         "  productid INT NOT NULL," +
         "  groupid INT," +
         "  processid INT," +
         "  PRIMARY KEY (licenseid) )");

      database.statement.executeUpdate(
         "CREATE TABLE licensegroupcount (" +
         "  groupid INT NOT NULL)");

      database.statement.executeUpdate(
         "CREATE TABLE licensegroups (" +
         "  groupid INT NOT NULL," +
         "  groupname VARCHAR(64) NOT NULL," +
         "  processid INT," +
         "  PRIMARY KEY (groupid) )");

      database.statement.executeUpdate(
         "CREATE TABLE orderlicenses (" +
         "  orderid INT NOT NULL, " +
         "  licenseid INT NOT NULL, " +
         "  quantity INT NOT NULL)");

      database.statement.executeUpdate(
         "CREATE TABLE ordergroups (" +
         "  orderid INT NOT NULL, " +
         "  groupid INT NOT NULL, " +
         "  quantity INT NOT NULL)");

      database.statement.executeUpdate(
         "DELETE FROM userlicenses");
      database.statement.executeUpdate(
         "ALTER TABLE userlicenses DROP COLUMN productid");
      database.statement.executeUpdate(
         "ALTER TABLE userlicenses ADD COLUMN licenseid INT NOT NULL");
      database.statement.executeUpdate(
         "ALTER TABLE userlicenses ADD COLUMN quantity INT NOT NULL");
      database.statement.executeUpdate(
         "ALTER TABLE userlicenses ADD COLUMN expiration DATE");

      database.statement.executeUpdate(
         "CREATE TABLE universitycount (" +
         "  universityid INT NOT NULL)");

      database.statement.executeUpdate(
         "CREATE TABLE universities (" +
         "  universityid INT NOT NULL," +
         "  university VARCHAR(128) NOT NULL," +
         "  fax VARCHAR(32)," +
         "  adminid INT NOT NULL," +
         "  techid INT NOT NULL," +
         "  PRIMARY KEY (universityid) )");

      database.statement.executeUpdate(
         "CREATE TABLE contacts (" +
         "  contactid INT NOT NULL," +
         "  email VARCHAR(64) NOT NULL," +
         "  firstname VARCHAR(32) NOT NULL," +
         "  lastname VARCHAR(32) NOT NULL," +
         "  phone VARCHAR(32)," +
         "  address VARCHAR(512)," +
         "  country VARCHAR(32) NOT NULL," +
         "  PRIMARY KEY (contactid) )");

      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}
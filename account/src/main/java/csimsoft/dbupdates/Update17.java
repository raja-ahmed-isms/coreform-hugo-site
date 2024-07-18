/**
 *
 * @author Mark Richardson
 */
package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update17 implements csimsoft.DatabaseUpdate
{

   public Update17()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add the new distributor upgrade table to the database.
      database.statement.executeUpdate(
         "CREATE TABLE distributorupgrades (" +
         "userid INT NOT NULL," +
         "upgradeid INT NOT NULL)");

      // Add the license activation table.
      database.statement.executeUpdate(
         "CREATE TABLE licenseactivations (" +
         "userlicenseid INT NOT NULL," +
         "hostid VARCHAR(32) NOT NULL)");

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

/**
 *
 * @author Mark Richardson
 */
package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update18 implements csimsoft.DatabaseUpdate
{

   public Update18()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add the deactivations column to the license table.
      database.statement.executeUpdate(
         "ALTER TABLE licenses ADD COLUMN deactivations INT");

      // Add the host name column to the activations table.
      database.statement.executeUpdate(
         "ALTER TABLE licenseactivations ADD COLUMN hostname VARCHAR(64)");

      // Fill in the missing info.
      database.statement.executeUpdate(
         "UPDATE licenses SET deactivations = 0");
      database.statement.executeUpdate(
         "UPDATE licenseactivations SET hostname = ''");

      // Constrain the new columns.
      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE licenses MODIFY COLUMN deactivations INT NOT NULL");
         database.statement.executeUpdate(
            "ALTER TABLE licenseactivations MODIFY COLUMN hostname VARCHAR(64) NOT NULL");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE licenses ALTER COLUMN deactivations NOT NULL");
         database.statement.executeUpdate(
            "ALTER TABLE licenseactivations ALTER COLUMN hostname NOT NULL");
      }

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

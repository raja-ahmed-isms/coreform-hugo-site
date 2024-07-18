/**
 *
 * @author Mark Richardson
 */
package csimsoft.dbupdates;

import csimsoft.DatabaseLicenses;
import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update19 implements csimsoft.DatabaseUpdate
{

   public Update19()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add the max deactivations column to the license table.
      database.statement.executeUpdate(
         "ALTER TABLE licenses ADD COLUMN maxdeactivations INT");

      // Fill in the missing info.
      database.statement.executeUpdate(
         "UPDATE licenses SET maxdeactivations = 1 WHERE licensetype = " +
         DatabaseLicenses.NodeLockedLicense);
      database.statement.executeUpdate(
         "UPDATE licenses SET maxdeactivations = 0 WHERE licensetype <> " +
         DatabaseLicenses.NodeLockedLicense);

      // Constrain the new columns.
      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE licenses MODIFY COLUMN maxdeactivations INT NOT NULL");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE licenses ALTER COLUMN maxdeactivations NOT NULL");
      }

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

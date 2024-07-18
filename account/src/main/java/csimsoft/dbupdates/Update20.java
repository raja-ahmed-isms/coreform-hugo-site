/**
 *
 * @author Mark Richardson
 */
package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update20 implements csimsoft.DatabaseUpdate
{

   public Update20()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Change the allowed size of the activation host id.
      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE licenseactivations MODIFY COLUMN hostid VARCHAR(64) NOT NULL");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE licenseactivations " +
            "ALTER COLUMN hostid SET DATA TYPE VARCHAR(64)");
      }

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

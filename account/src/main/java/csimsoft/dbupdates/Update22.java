/**
 *
 * @author Mark Richardson
 */
package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update22 implements csimsoft.DatabaseUpdate
{

   public Update22()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add a default column to the license features and options.
      database.statement.executeUpdate(
         "ALTER TABLE licensefeatures ADD COLUMN autoadd INT");
      database.statement.executeUpdate(
         "ALTER TABLE licenseoptions ADD COLUMN autoadd INT");

      // Set the default column value.
      database.statement.executeUpdate(
         "UPDATE licensefeatures SET autoadd = 0");
      database.statement.executeUpdate(
         "UPDATE licenseoptions SET autoadd = 0");

      // Constrain the new columns.
      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE licensefeatures MODIFY COLUMN autoadd INT NOT NULL");
         database.statement.executeUpdate(
            "ALTER TABLE licenseoptions MODIFY COLUMN autoadd INT NOT NULL");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE licensefeatures ALTER COLUMN autoadd NOT NULL");
         database.statement.executeUpdate(
            "ALTER TABLE licenseoptions ALTER COLUMN autoadd NOT NULL");
      }

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

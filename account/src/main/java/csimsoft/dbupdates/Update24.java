/**
 *
 * @author Mark Richardson
 */
package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;



public class Update24 implements csimsoft.DatabaseUpdate
{

   public Update24()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add a column to the user license table for number of allowed instances.
      database.statement.executeUpdate(
         "ALTER TABLE licenses ADD COLUMN allowedinst INT");

      // Put a default value in the new column for all the licenses.
      database.statement.executeUpdate(
         "UPDATE licenses SET allowedinst = 0");

      // Constrain the new column.
      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE licenses MODIFY COLUMN allowedinst INT NOT NULL");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE licenses ALTER COLUMN allowedinst NOT NULL");
      }

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

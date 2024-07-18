package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update30 implements csimsoft.DatabaseUpdate
{

   public Update30()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add a column to the license table for number of processes.
      database.statement.executeUpdate(
         "ALTER TABLE platformdownloads ADD COLUMN autoadd INT");

      // Put a default value in the new columns for all the licenses.
      database.statement.executeUpdate(
         "UPDATE platformdownloads SET autoadd = 1");

      // Constrain the new column.
      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE platformdownloads MODIFY COLUMN autoadd INT NOT NULL");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE platformdownloads ALTER COLUMN autoadd NOT NULL");
      }

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

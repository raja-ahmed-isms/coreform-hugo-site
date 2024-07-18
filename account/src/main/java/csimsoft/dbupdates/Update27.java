package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update27 implements csimsoft.DatabaseUpdate
{

   public Update27()
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
         "ALTER TABLE licenseproducts ADD COLUMN allowedinst INT");

      // Put a default value in the new columns for all the licenses.
      database.statement.executeUpdate(
         "UPDATE licenseproducts SET allowedinst = 0");

      // Constrain the new column.
      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE licenseproducts MODIFY COLUMN allowedinst INT NOT NULL");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE licenseproducts ALTER COLUMN allowedinst NOT NULL");
      }

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

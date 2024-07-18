/**
 *
 * @author Mark Richardson
 */
package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update25 implements csimsoft.DatabaseUpdate
{

   public Update25()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add a table for the virtual machine signatures.
      database.statement.executeUpdate(
         "CREATE TABLE vmsign (" +
         "  licenseid INT NOT NULL," +
         "  featurechk VARCHAR(64) NOT NULL," +
         "  featuresig VARCHAR(512) NOT NULL)");

      // Add a column to the user license table for virtual machines and
      // remote desktops.
      database.statement.executeUpdate(
         "ALTER TABLE licenses ADD COLUMN vmallowed INT");
      database.statement.executeUpdate(
         "ALTER TABLE licenses ADD COLUMN rdallowed INT");

      // Put a default value in the new columns for all the licenses.
      database.statement.executeUpdate(
         "UPDATE licenses SET vmallowed = 0");
      database.statement.executeUpdate(
         "UPDATE licenses SET rdallowed = 0");

      // Constrain the new column.
      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE licenses MODIFY COLUMN vmallowed INT NOT NULL");
         database.statement.executeUpdate(
            "ALTER TABLE licenses MODIFY COLUMN rdallowed INT NOT NULL");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE licenses ALTER COLUMN vmallowed NOT NULL");
         database.statement.executeUpdate(
            "ALTER TABLE licenses ALTER COLUMN rdallowed NOT NULL");
      }

      // Clear out the signatures for node-locked licenses.
      database.statement.executeUpdate(
         "DELETE FROM activationfeatures WHERE activationid IN " +
         "(SELECT activationid FROM licenseactivations WHERE userlicenseid IN" +
         "(SELECT userlicenseid FROM licenses WHERE licensetype <> 2))");

      // Clean up any leaked license options.
      database.statement.executeUpdate(
         "DELETE FROM licenseoptions WHERE licenseid NOT IN " +
         "(SELECT licenseid FROM licenseproducts)");

      // Clean up the old license tables.
      database.statement.executeUpdate(
         "DELETE FROM userlicenses");
      database.statement.executeUpdate(
         "DELETE FROM ordergroups");
      database.statement.executeUpdate(
         "DELETE FROM distributorgroups");
      database.statement.executeUpdate(
         "DELETE FROM productlicenses");
      database.statement.executeUpdate(
         "DELETE FROM licensegroups");
      database.statement.executeUpdate(
         "DELETE FROM licensegroupcount");

      database.statement.executeUpdate(
         "DROP TABLE userlicenses");
      database.statement.executeUpdate(
         "DROP TABLE ordergroups");
      database.statement.executeUpdate(
         "DROP TABLE distributorgroups");
      database.statement.executeUpdate(
         "DROP TABLE productlicenses");
      database.statement.executeUpdate(
         "ALTER TABLE licensegroups DROP COLUMN processid");
      database.statement.executeUpdate(
         "ALTER TABLE orderlicenses DROP COLUMN quantity");
      database.statement.executeUpdate(
         "ALTER TABLE orderproducts DROP COLUMN quantity");

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

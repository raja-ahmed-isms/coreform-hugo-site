/**
 *
 * @author Mark Richardson
 */
package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update21 implements csimsoft.DatabaseUpdate
{

   public Update21()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add a release date to the downloads.
      database.statement.executeUpdate(
         "ALTER TABLE products ADD COLUMN releasedate DATE");

      // Add a new license approval table.
      database.statement.executeUpdate(
         "CREATE TABLE licenseapprovals (" +
         "userid INT NOT NULL," +
         "licenseid INT NOT NULL," +
         "approval INT NOT NULL)");

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

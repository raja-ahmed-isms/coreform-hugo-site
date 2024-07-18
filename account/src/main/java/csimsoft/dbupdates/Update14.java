/**
 *
 * @author Mark Richardson
 */

package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update14 implements csimsoft.DatabaseUpdate
{

   public Update14()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add the new distributor licenses table.
      database.statement.executeUpdate(
         "CREATE TABLE distributorgroups (" +
         "userid INT NOT NULL," +
         "groupid INT NOT NULL)");

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

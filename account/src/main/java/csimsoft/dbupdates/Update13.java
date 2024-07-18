/**
 * @(#)Update13.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/10/25
 */

package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update13 implements csimsoft.DatabaseUpdate
{

   public Update13()
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
         "CREATE TABLE distributorlicenses (" +
         "userid INT NOT NULL," +
         "licenseid INT NOT NULL)");

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

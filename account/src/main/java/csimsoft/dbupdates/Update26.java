/**
 *
 * @author Mark Richardson
 */
package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update26 implements csimsoft.DatabaseUpdate
{

   public Update26()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add a column to the user order table for the distributor PO number.
      database.statement.executeUpdate(
         "ALTER TABLE userorders ADD COLUMN distnumber VARCHAR(64)");

      // Add a table to assign distributor email templates.
      database.statement.executeUpdate(
         "CREATE TABLE distributoremails (" +
         "  userid INT NOT NULL," +
         "  emailtype INT NOT NULL," +
         "  productid INT NOT NULL)");

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

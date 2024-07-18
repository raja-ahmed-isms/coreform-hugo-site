/**
 * @(#)Update11.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/3/26
 */

package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update11 implements csimsoft.DatabaseUpdate
{

   public Update11()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add a product id for a folder on the server.
      database.statement.executeUpdate(
         "ALTER TABLE distributors ADD COLUMN folder INT");

      database.statement.executeUpdate(
         "UPDATE distributors SET folder = 0");

      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE distributors MODIFY COLUMN folder INT NOT NULL");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE distributors ALTER COLUMN folder NOT NULL");
      }

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}
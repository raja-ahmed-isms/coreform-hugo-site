/**
 * @(#)Update9.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/2/17
 */

package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update9 implements csimsoft.DatabaseUpdate
{

   public Update9()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      database.statement.executeUpdate(
         "ALTER TABLE users ADD COLUMN groupid INT");

      database.statement.executeUpdate(
         "UPDATE users SET groupid = 0");

      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE users MODIFY COLUMN groupid INT NOT NULL");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE users ALTER COLUMN groupid NOT NULL");
      }

      database.statement.executeUpdate(
         "CREATE TABLE distributorcount (" +
         "groupid INT NOT NULL)");

      database.statement.executeUpdate(
         "CREATE TABLE distributors (" +
         "userid INT NOT NULL, " +
         "groupid INT NOT NULL, " +
         "PRIMARY KEY (userid), " +
         "FOREIGN KEY (userid) REFERENCES users (userid))");

      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}
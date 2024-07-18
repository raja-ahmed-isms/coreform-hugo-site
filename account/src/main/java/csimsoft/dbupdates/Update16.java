/**
 *
 * @author Mark Richardson
 */
package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update16 implements csimsoft.DatabaseUpdate
{

   public Update16()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add the new options tables to the database.
      database.statement.executeUpdate(
         "CREATE TABLE optioncount (" +
         "optionid INT NOT NULL)");

      database.statement.executeUpdate(
         "CREATE TABLE options (" +
         "optionid INT NOT NULL," +
         "optionname VARCHAR(64) NOT NULL," +
         "optionkey VARCHAR(32) NOT NULL," +
         "PRIMARY KEY (optionid))");

      database.statement.executeUpdate(
         "CREATE TABLE licenseoptions (" +
         "optionid INT NOT NULL," +
         "licenseid INT NOT NULL)");

      database.statement.executeUpdate(
         "CREATE TABLE useroptions (" +
         "userlicenseid INT NOT NULL," +
         "optionid INT NOT NULL)");

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update28 implements csimsoft.DatabaseUpdate
{

   public Update28()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add a field to track lead generation.
      database.statement.executeUpdate(
         "ALTER TABLE userorders ADD COLUMN srcnumber VARCHAR(32)");

      // Add a start date to the licenses.
      database.statement.executeUpdate(
         "ALTER TABLE licenses ADD COLUMN startdate DATE");

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

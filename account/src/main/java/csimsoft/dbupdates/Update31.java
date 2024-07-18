package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update31 implements csimsoft.DatabaseUpdate
{

   public Update31()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      database.statement.executeUpdate(
         "ALTER TABLE distributors ADD COLUMN reseller INT");

      database.statement.executeUpdate(
         "UPDATE distributors SET reseller = 0");
	  
      // Constrain the new column.
      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE distributors MODIFY COLUMN reseller INT NOT NULL");
         database.statement.executeUpdate(
            "ALTER TABLE distributors MODIFY COLUMN reseller INT NOT NULL");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE distributors ALTER COLUMN reseller NOT NULL");
         database.statement.executeUpdate(
            "ALTER TABLE distributors ALTER COLUMN reseller NOT NULL");
      }

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

/**
 * @(#)Update2.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/1/25
 */

package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update2 implements csimsoft.DatabaseUpdate
{

   public Update2()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      database.statement.executeUpdate(
         "ALTER TABLE users DROP COLUMN role");
      database.statement.executeUpdate(
         "ALTER TABLE userinfo DROP COLUMN email");
      database.statement.executeUpdate(
         "ALTER TABLE userinfo DROP COLUMN status");
      database.statement.executeUpdate(
         "ALTER TABLE userorders DROP COLUMN email");
      database.statement.executeUpdate(
         "ALTER TABLE userdownloads DROP COLUMN email");
      database.statement.executeUpdate(
         "ALTER TABLE userlicenses DROP COLUMN email");
      database.statement.executeUpdate(
         "ALTER TABLE userapprovals DROP COLUMN email");

      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}
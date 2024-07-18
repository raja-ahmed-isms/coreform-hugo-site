/**
 * @(#)Update4.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/1/24
 */

package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update4 implements csimsoft.DatabaseUpdate
{

   public Update4()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      database.statement.executeUpdate(
         "ALTER TABLE products ADD COLUMN processid INT");

      java.util.TreeMap<String, Integer> processes =
         new java.util.TreeMap<String, Integer>();
      database.results = database.statement.executeQuery(
         "SELECT processid, processname FROM orderprocess");
      while(database.results.next())
      {
         processes.put(database.results.getString("processname"),
            new Integer(database.results.getInt("processid")));
      }

      database.results.close();
      java.util.Iterator<java.util.Map.Entry<String, Integer>> iter =
         processes.entrySet().iterator();
      while(iter.hasNext())
      {
         java.util.Map.Entry<String, Integer> process = iter.next();
         database.statement.executeUpdate(
            "UPDATE products SET processid = " + process.getValue().toString() +
            " WHERE processname = '" + process.getKey() + "'");
      }

      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}
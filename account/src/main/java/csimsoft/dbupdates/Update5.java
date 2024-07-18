/**
 * @(#)Update5.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/1/24
 */

package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update5 implements csimsoft.DatabaseUpdate
{

   public Update5()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      database.statement.executeUpdate(
         "ALTER TABLE userorders ADD COLUMN processid INT");

      java.util.TreeMap<Integer, Integer> processes =
         new java.util.TreeMap<Integer, Integer>();
      database.results = database.statement.executeQuery(
         "SELECT processid, productid FROM products");
      while(database.results.next())
      {
         processes.put(new Integer(database.results.getInt("productid")),
            new Integer(database.results.getInt("processid")));
      }

      database.results.close();
      java.util.Iterator<java.util.Map.Entry<Integer, Integer>> iter =
         processes.entrySet().iterator();
      while(iter.hasNext())
      {
         java.util.Map.Entry<Integer, Integer> product = iter.next();
         database.statement.executeUpdate(
            "UPDATE userorders SET processid = " + product.getValue().toString() +
            " WHERE productid = " + product.getKey().toString());
      }

      database.statement.executeUpdate(
         "UPDATE userorders SET processid = 0 WHERE processid IS NULL");

      database.statement.executeUpdate(
         "ALTER TABLE userorders MODIFY COLUMN processid INT NOT NULL");

      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}
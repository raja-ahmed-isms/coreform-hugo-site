/**
 * @(#)Update6.java
 *
 *
 * @author Mark richardson
 * @version 1.00 2012/1/24
 */

package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update6 implements csimsoft.DatabaseUpdate
{

   public Update6()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      database.statement.executeUpdate(
         "ALTER TABLE userorders ADD COLUMN ordernumber VARCHAR(32)");

      database.statement.executeUpdate(
         "CREATE TABLE orderproducts (" +
         " orderid INT NOT NULL," +
         " productid INT NOT NULL," +
         " quantity INT NOT NULL)");

      java.util.TreeMap<Integer, Integer> products =
         new java.util.TreeMap<Integer, Integer>();
      database.results = database.statement.executeQuery(
         "SELECT orderid, productid FROM userorders");
      while(database.results.next())
      {
         products.put(new Integer(database.results.getInt("orderid")),
            new Integer(database.results.getInt("productid")));
      }

      database.results.close();
      java.util.Iterator<java.util.Map.Entry<Integer, Integer>> iter =
         products.entrySet().iterator();
      while(iter.hasNext())
      {
         java.util.Map.Entry<Integer, Integer> product = iter.next();
         database.statement.executeUpdate(
            "INSERT INTO orderproducts (orderid, productid, quantity) VALUES (" +
            product.getKey().toString() + ", " + product.getValue().toString() +
            ", 1)");
      }

      database.statement.executeUpdate(
         "ALTER TABLE userorders DROP COLUMN productid");

      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}
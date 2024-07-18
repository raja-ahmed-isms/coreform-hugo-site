/**
 * @(#)Update10.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/2/22
 */

package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.TreeMap;


public class Update10 implements csimsoft.DatabaseUpdate
{

   public Update10()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add the parent id and depth columns to support the new tree structure.
      database.statement.executeUpdate(
         "ALTER TABLE products ADD COLUMN parentid INT");
      database.statement.executeUpdate(
         "ALTER TABLE products ADD COLUMN depth INT");
      database.statement.executeUpdate(
         "ALTER TABLE products ADD COLUMN folder INT");

      database.statement.executeUpdate(
         "UPDATE products SET parentid = 0, depth = 1, folder = 0");

      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE products MODIFY COLUMN parentid INT NOT NULL");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE products ALTER COLUMN parentid NOT NULL");
      }

      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE products MODIFY COLUMN depth INT NOT NULL");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE products ALTER COLUMN depth NOT NULL");
      }

      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE products MODIFY COLUMN folder INT NOT NULL");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE products ALTER COLUMN folder NOT NULL");
      }

      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE products MODIFY COLUMN groupname VARCHAR(32)");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE products ALTER COLUMN groupname NULL");
      }

      // Get the set of product groups.
      TreeMap<String, String> groups = new TreeMap<String, String>();
      TreeMap<Integer, String> products = new TreeMap<Integer, String>();
      database.results = database.statement.executeQuery(
         "SELECT productid, groupname, location FROM products ORDER BY groupname");
      while(database.results.next())
      {
         Integer productId = new Integer(database.results.getInt("productid"));
         String groupName = database.results.getString("groupname");
         String location = database.results.getString("location");
         String[] path = location.split("/");
         if(groupName != null && !groupName.isEmpty() && path.length > 1)
         {
            groups.put(groupName, path[0]);
            products.put(productId, path[path.length - 1]);
         }
      }

      database.results.close();
      if(!groups.isEmpty())
      {
         // Create directory objects for the product groups.
         int lastId = 0;
         database.results = database.statement.executeQuery(
            "SELECT productid FROM productcount");
         if(database.results.next())
            lastId = database.results.getInt("productid");

         database.results.close();
         int nextId = lastId + 1;
         Iterator<String> iter = groups.navigableKeySet().iterator();
         for( ; iter.hasNext(); ++nextId)
         {
            String groupName = iter.next();
            String location = groups.get(groupName);
            database.statement.executeUpdate(
               "INSERT INTO products (productid, product, location, parentid," +
               " depth, folder) VALUES (" + nextId + ", '" + groupName + "', '" +
               location + "', 0, 0, 1)");
            database.statement.executeUpdate(
               "UPDATE products SET parentid = " + nextId +
               " WHERE groupname = '" + groupName + "'");
         }

         --nextId;
         if(lastId == 0)
         {
            database.statement.executeUpdate(
               "INSERT INTO productcount (productid) VALUES (" + nextId + ")");
         }
         else
         {
            database.statement.executeUpdate(
               "UPDATE productcount SET productid = " + nextId +
               " WHERE productid = " + lastId);
         }
      }

      // Clean up the location for the products.
      Iterator<Integer> jter = products.navigableKeySet().iterator();
      while(jter.hasNext())
      {
         Integer productId = jter.next();
         String location = products.get(productId);
         database.statement.executeUpdate(
            "UPDATE products SET location = '" + location +
            "' WHERE productid = " + productId.intValue());
      }

      // Remove the group name column from the table.
      database.statement.executeUpdate(
         "ALTER TABLE products DROP COLUMN groupname");

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}
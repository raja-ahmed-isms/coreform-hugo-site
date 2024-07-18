package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;


public class Update29 implements csimsoft.DatabaseUpdate
{
   public Update29()
   {
   }

   private static class Product
   {
      public int id = 0;
      public int parentId = 0;
      public boolean folder = true;
      public Product parent = null;
      public ArrayList<Product> items = new ArrayList<Product>();

      public Product(int id)
      {
         this.id = id;
      }

      public void addItem(Product item)
      {
         item.parent = this;
         this.items.add(item);
      }

      public static Product getNext(Product item)
      {
         if(item == null)
            return null;

         if(!item.items.isEmpty())
            return item.items.get(0);

         Product owner = item.parent;
         while(owner != null)
         {
            int index = owner.items.indexOf(item) + 1;
            if(index < owner.items.size())
               return owner.items.get(index);

            item = owner;
            owner = item.parent;
         }

         return null;
      }
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add a table to hold the product paths.
      database.statement.executeUpdate(
         "CREATE TABLE productpaths (" +
         "  productid INT NOT NULL," +
         "  pathorder INT NOT NULL," +
         "  parentid INT NOT NULL)");

      // Add a path for all the products in the database.
      TreeMap<Integer, Update29.Product> groups =
         new TreeMap<Integer, Update29.Product>();
      groups.put(0, new Update29.Product(0));
      database.results = database.statement.executeQuery(
         "SELECT productid, parentid, depth, folder " +
         "FROM products ORDER BY depth, folder DESC");
      while(database.results.next())
      {
         // Get the next product.
         Update29.Product product = new Update29.Product(
            database.results.getInt("productid"));
         product.parentId = database.results.getInt("parentid");
         product.folder = database.results.getInt("folder") > 0;
         groups.put(product.id, product);

         // Find the parent in the map.
         Update29.Product parent = groups.get(product.parentId);
         if(parent != null)
            parent.addItem(product);
      }

      database.results.close();

      // Set up the statement for adding paths.
      PreparedStatement addPath = database.connection.prepareStatement(
         "INSERT INTO productpaths (productid, pathorder, parentid) VALUES (?, ?, ?)");
      database.addStatement(addPath);

      // Loop through the items on the tree.
      Update29.Product next = Update29.Product.getNext(groups.get(0));
      while(next != null)
      {
         // Build the path from the parents.
         ArrayList<Integer> path = new ArrayList<Integer>();
         path.add(next.id);
         Update29.Product owner = next.parent;
         while(owner != null && owner.id > 0)
         {
            path.add(0, owner.id);
            owner = owner.parent;
         }

         // Add the path to the database.
         addPath.setInt(1, next.id);
         for(int i = 0; i < path.size(); ++i)
         {
            addPath.setInt(2, i);
            addPath.setInt(3, path.get(i));
            addPath.executeUpdate();
         }

         next = Update29.Product.getNext(next);
      }

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

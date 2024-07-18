/**
 * @(#)ProductList.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/2/29
 */

package csimsoft;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;


public class ProductList
{
   private ProductListGroup Root = new ProductListGroup();
   private int UserHome = 0;

   public ProductList()
   {
   }

   public ProductList(int userHome)
   {
      this.UserHome = userHome;
   }

   public int getUserHomeId()
   {
      return this.UserHome;
   }

   public void setUserHomeId(int userHome)
   {
      this.UserHome = userHome;
   }

   public ProductListGroup getRoot()
   {
      return this.Root;
   }

   public ProductListGroup[] getRootPath()
   {
      if(this.Root.getId() == this.UserHome)
         return null;

      ArrayList<ProductListGroup> parents = new ArrayList<ProductListGroup>();
      ProductListGroup parent = this.Root.getParent();
      while(parent != null && parent.getId() != this.UserHome)
      {
         parents.add(0, parent);
         parent = parent.getParent();
      }

      if(parents.isEmpty())
         return null;

      ProductListGroup[] path = new ProductListGroup[parents.size()];
      return parents.toArray(path);
   }

   public ProductListGroup getUserHome()
   {
      ProductListGroup home = this.Root;
      while(home != null)
      {
         if(home.getId() == this.UserHome)
            return home;

         home = home.getParent();
      }

      return null;
   }

   public ProductListItem getNextItem(ProductListItem item)
   {
      if(item == null)
         return null;

      // If the item is a group, return the first child.
      if(item instanceof ProductListGroup)
      {
         ProductListGroup group = (ProductListGroup)item;
         if(!group.isEmpty())
            return group.getItem(0);
      }

      // Look up the parent chain for the next item.
      ProductListGroup parent = item.getParent();
      while(parent != null)
      {
         if(item == this.Root)
            break;

         int index = parent.getItemIndex(item) + 1;
         if(index < parent.getItemCount())
            return parent.getItem(index);

         item = parent;
         parent = item.getParent();
      }

      return null;
   }

   public ProductListItem getPreviousItem(ProductListItem item)
   {
      if(item == null)
         return null;

      ProductListGroup parent = item.getParent();
      if(parent == null)
         return null;

      int index = parent.getItemIndex(item);
      if(index == 0)
         return parent == this.Root ? null : parent;

      item = parent.getItem(index - 1);
      if(item instanceof ProductListGroup)
      {
         parent = (ProductListGroup)item;
         item = this.getLastItem(parent);
         return item == null ? parent : item;
      }
      else
         return item;
   }

   public ProductListItem getLastItem()
   {
      return this.getLastItem(this.Root);
   }

   public ProductListItem getLastItem(ProductListGroup parent)
   {
      ProductListItem item = null;
      while(parent != null && !parent.isEmpty())
      {
         item = parent.getItem(parent.getItemCount() - 1);
         if(item instanceof ProductListGroup)
            parent = (ProductListGroup)item;
         else
            parent = null;
      }

      return item;
   }

   public void clear()
   {
      this.Root.clear();
   }

   public void setFolders(Database database, int rootId) throws SQLException
   {
      this.setProducts(database, rootId, 0, true);
   }

   public void setProducts(Database database, int rootId)
      throws SQLException
   {
      this.setProducts(database, rootId, 0, false);
   }

   public void setProducts(Database database, int rootId, int depthLimit)
      throws SQLException
   {
      this.setProducts(database, rootId, depthLimit, false);
   }

   public void setProducts(Database database, int rootId, int depthLimit,
      boolean onlyFolders) throws SQLException
   {
      // Clear the current list of products.
      this.clear();

      if(database.statement == null)
         database.statement = database.connection.createStatement();

      // If there is a root id given, find the location.
      String condition = "";
      ProductListGroup parent = null;
      if(rootId > 0)
      {
         int parentId = 0;
         int depth = 0;
         database.results = database.statement.executeQuery(
            "SELECT product, location, parentid, depth, folder FROM products " +
            "WHERE productid = " + rootId);
         boolean exists = database.results.next();
         if(exists)
         {
            exists = database.results.getInt("folder") > 0;
            if(exists)
            {
               parentId = database.results.getInt("parentid");
               depth = database.results.getInt("depth");
               this.Root.setId(rootId);
               this.Root.setName(database.results.getString("product"));
               this.Root.setLocation(database.results.getString("location"));
            }
         }

         database.results.close();
         if(!exists)
            return;

         // Get the parent chain for the root.
         ProductListGroup current = this.Root;
         while(parentId > 0)
         {
            database.results = database.statement.executeQuery(
               "SELECT product, location, parentid FROM products WHERE productid = " +
               parentId);
            if(database.results.next())
            {
               parent = new ProductListGroup(parentId,
                  database.results.getString("product"));
               parent.setLocation(database.results.getString("location"));
               current.setParent(parent);
               current = parent;
               parentId = database.results.getInt("parentid");
            }
            else
               parentId = 0;

            database.results.close();
         }

         // Set up the depth condition for the query.
         condition = " WHERE depth > " + depth;
         if(depthLimit > 0)
            condition += " AND depth < " + (depth + depthLimit + 1);
      }
      else if(depthLimit > 0)
         condition = " WHERE depth < " + depthLimit;

      // If there is a user folder root, make sure it is in the parent chain.
      if(this.UserHome > 0)
      {
         parent = this.Root;
         boolean found = false;
         while(!found && parent != null)
         {
            found = parent.getId() == this.UserHome;
            parent = parent.getParent();
         }

         if(!found)
         {
            this.clear();
            return;
         }
      }

      // Get the list of files for the given root.
      TreeMap<Integer, ProductListGroup> groups =
         new TreeMap<Integer, ProductListGroup>();
      groups.put(new Integer(rootId), this.Root);
      database.results = database.statement.executeQuery(
         "SELECT productid, product, location, parentid, depth, folder, processid " +
         "FROM products" + condition + " ORDER BY depth, folder DESC, product");
      while(database.results.next())
      {
         // Filter out items if only folders are requested.
         boolean isFolder = database.results.getInt("folder") > 0;
         if(onlyFolders && !isFolder)
            continue;

         // Look up the parent group. If the group doesn't exist,
         // don't add the item.
         Integer parentId = new Integer(database.results.getInt("parentid"));
         parent = groups.get(parentId);
         if(parent == null)
            continue;

         int productId = database.results.getInt("productid");
         String name = database.results.getString("product");
         ProductListItem item = null;
         if(isFolder)
         {
            // Add the group to the map for children to find.
            ProductListGroup group = new ProductListGroup(productId, name);
            groups.put(new Integer(productId), group);
            item = group;
         }
         else
         {
            item = new ProductListItem(productId, name);
            item.setProcessId(database.results.getInt("processid"));
         }

         parent.addItem(item);
         item.setLocation(database.results.getString("location"));
      }

      database.results.close();
   }

   public void addDownloads(DatabaseProducts products, int userId) throws SQLException
   {
      if(products.database.statement == null)
         products.database.statement = products.database.connection.createStatement();

      // Get the list of downloads for the user.
      ProductListGroup group = null;
      products.database.results = products.database.statement.executeQuery(
         "SELECT productid, product FROM products WHERE productid IN " +
         "(SELECT productid FROM userdownloads WHERE userid = " +
         userId + ") ORDER BY product");
      while(products.database.results.next())
      {
         if(group == null)
         {
            group = new ProductListGroup(-1, "My Downloads");
            this.Root.addItem(group);
         }

         group.addItem(new ProductListItem(
            products.database.results.getInt("productid"),
            products.database.results.getString("product")));
      }

      products.database.results.close();
      if(group != null)
      {
         // Get the path for each of the user's downloads.
         for(int i = 0; i < group.getItemCount(); ++i)
         {
            ProductListItem item = group.getItem(i);
            String path = products.getProductNamePath(item.getId());
            if(path != null && !path.isEmpty())
               item.setName(path + "/" + item.getName());
         }
      }
   }

   public int calculateDepths()
   {
      return this.calculateDepths(false);
   }

   public int calculateDepths(boolean showRoot)
   {
      int maxDepth = 0;
      ProductListItem item = this.getNextItem(this.Root);
      while(item != null)
      {
         int depth = item.calculateDepth(this.Root, showRoot);
         if(depth > maxDepth)
            maxDepth = depth;

         item = this.getNextItem(item);
      }

      return maxDepth;
   }
}
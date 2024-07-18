/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DatabaseProducts extends DatabaseMethods
{
   public static final String Distributors = "Distributors";

   public DatabaseProducts()
   {
      super();
   }

   public DatabaseProducts(Database db)
   {
      super(db);
   }

   public String getDownloadDirectory() throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      String directory = null;
      this.database.results = this.database.statement.executeQuery(
         "SELECT directory FROM downloadpath");
      if(this.database.results.next())
         directory = this.database.results.getString("directory");

      this.database.results.close();
      return directory;
   }

   public boolean isProductInDatabase(int parentId, String name, String location)
      throws SQLException
   {
      PreparedStatement statement = this.database.connection.prepareStatement(
         "SELECT productid FROM products WHERE parentid = ? " +
         "AND (product = ? OR location = ?)");
      this.database.addStatement(statement);
      statement.setInt(1, parentId);
      statement.setString(2, name);
      statement.setString(3, location);
      this.database.results = statement.executeQuery();
      boolean exists = this.database.results.next();
      this.database.results.close();
      return exists;
   }

   public int addProduct(ResultMessageAdapter message, int parentId,
      String product, String location, boolean isFolder) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      // Get the depth of the parent folder.
      int depth = 0;
      if(parentId > 0)
      {
         this.database.results = this.database.statement.executeQuery(
            "SELECT depth, folder FROM products WHERE productid = " + parentId);
         boolean parentValid = this.database.results.next();
         if(parentValid)
         {
            parentValid = this.database.results.getInt("folder") > 0;
            if(parentValid)
               depth = this.database.results.getInt("depth");
         }

         this.database.results.close();
         depth++;
         if(!parentValid)
         {
            message.setResult(ResultMessage.Failure);
            message.addMessage("<br />The parent product is not a valid folder.");
            return 0;
         }
      }
      else if(parentId < 0)
         parentId = 0;

      // Make sure the name or location is not duplicated.
      if(this.isProductInDatabase(parentId, product, location))
      {
         message.setResult(ResultMessage.Exists);
         return 0;
      }

      // Get the current product id from the database.
      int nextId = this.database.getNextId("productcount", "productid");

      PreparedStatement statement = this.database.connection.prepareStatement(
         "INSERT INTO products (productid, product, location, parentid, depth, " +
         "folder) VALUES (?, ?, ?, ?, ?, ?)");
      this.database.addStatement(statement);
      statement.setInt(1, nextId);
      statement.setString(2, product);
      statement.setString(3, location);
      statement.setInt(4, parentId);
      statement.setInt(5, depth);
      statement.setInt(6, isFolder ? 1 : 0);
      statement.executeUpdate();

      // Add the path for the new product if needed.
      addProductPath(nextId, parentId);

      return nextId;
   }

   private void addProductPath(int productId, int parentId) throws SQLException
   {
      // Get the parent's path from the database.
      List<Integer> path = getProductIdPath(parentId);

      // Add the new product path to the database.
      int i = 0;
      PreparedStatement addPath = this.database.connection.prepareStatement(
         "INSERT INTO productpaths (productid, pathorder, parentid) VALUES (?, ?, ?)");
      this.database.addStatement(addPath);
      addPath.setInt(1, productId);
      for( ; i < path.size(); ++i)
      {
         addPath.setInt(2, i);
         addPath.setInt(3, path.get(i));
         addPath.executeUpdate();
      }

      addPath.setInt(2, i);
      addPath.setInt(3, productId);
      addPath.executeUpdate();
   }

   public void getProductInfo(int productId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT product, depth, folder FROM products WHERE productid = " + productId);
   }

   public void setProductInfo(int productId, String name, String location)
      throws SQLException
   {
      PreparedStatement statement = this.database.connection.prepareStatement(
         "UPDATE products SET product = ?, location = ? WHERE productid = ?");
      this.database.addStatement(statement);
      statement.setString(1, name);
      statement.setString(2, location);
      statement.setInt(3, productId);
      statement.executeUpdate();
   }

   public void changeProductParent(int productId, int parentId, int depth)
      throws SQLException
   {
      this.database.statement.executeUpdate(
         "UPDATE products SET parentid = " + parentId + ", depth = " + depth +
         " WHERE productid = " + productId);

      // Fix the product path.
      this.database.statement.executeUpdate(
         "DELETE FROM productpaths WHERE productid = " + productId);
      addProductPath(productId, parentId);
   }

   public String getProductName(int productId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      String product = null;
      this.database.results = this.database.statement.executeQuery(
         "SELECT product FROM products WHERE productid = " + productId);
      if(this.database.results.next())
         product = this.database.results.getString("product");

      this.database.results.close();
      return product;
   }

   public void setProductName(int productId, String name) throws SQLException
   {
      PreparedStatement statement = this.database.connection.prepareStatement(
         "UPDATE products SET product = ? WHERE productid = ?");
      this.database.addStatement(statement);
      statement.setString(1, name);
      statement.setInt(2, productId);
      statement.executeUpdate();
   }

   public String getProductNamePath(int productId) throws SQLException
   {
      return this.getProductPath(productId, "product");
   }

   public String getProductLocation(int productId) throws SQLException
   {
      return this.getProductPath(productId, "location");
   }

   public void getProductPaths(ProductInfo product) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      String name = null;
      String location = null;
      this.database.results = this.database.statement.executeQuery(
         "SELECT productpaths.pathorder, products.product, products.location" +
         " FROM productpaths INNER JOIN products ON productpaths.parentid =" +
         " products.productid WHERE productpaths.productid = " + product.getId() +
         " ORDER BY productpaths.pathorder");
      if(this.database.results.next())
      {
         name = this.database.results.getString("product");
         location = this.database.results.getString("location");
         while(this.database.results.next())
         {
            name += "/" + this.database.results.getString("product");
            location += "/" + this.database.results.getString("location");
         }
      }

      this.database.results.close();
      product.setName(name);
      product.setLocation(location);
   }

   private String getProductPath(int productId, String field) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      String path = null;
      this.database.results = this.database.statement.executeQuery(
         "SELECT productpaths.pathorder, products." + field +
         " FROM productpaths INNER JOIN products ON productpaths.parentid =" +
         " products.productid WHERE productpaths.productid = " + productId +
         " ORDER BY productpaths.pathorder");
      if(this.database.results.next())
      {
         path = this.database.results.getString(field);
         while(this.database.results.next())
            path += "/" + this.database.results.getString(field);
      }

      this.database.results.close();
      return path;
   }

   public List<Integer> getProductIdPath(int productId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      ArrayList<Integer> list = new ArrayList<Integer>();
      this.database.results = this.database.statement.executeQuery(
         "SELECT parentid FROM productpaths WHERE productid = " + productId +
         " ORDER BY pathorder");
      while(this.database.results.next())
         list.add(this.database.results.getInt("parentid"));

      this.database.results.close();
      return list;
   }

   public int getProductId(String location)
      throws SQLException
   {
      return this.getProductId(location, "location");
   }

   public int getProductIdFromName(String product)
      throws SQLException
   {
      return this.getProductId(product, "product");
   }

   private int getProductId(String variable, String field)
      throws SQLException
   {
      if(variable == null || variable.isEmpty())
         return 0;

      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      int productId = 0;
      boolean success = true;
      String[] path = variable.split("/");
      for(int depth = 0; success && depth < path.length; ++depth)
      {
         this.database.results = this.database.statement.executeQuery(
            "SELECT productid FROM products WHERE " + field + " = '" + path[depth] +
            "' AND parentid = " + productId + " AND depth = " + depth);
         success = this.database.results.next();
         if(success)
            productId = this.database.results.getInt("productid");

         this.database.results.close();
      }

      return success ? productId : 0;
   }

   public boolean isOpenProduct(int productId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT productid FROM openproducts WHERE productid = " +
         productId);
      boolean isOk = this.database.results.next();
      this.database.results.close();
      return isOk;
   }

   public boolean isDownloadOk(int userId, int productId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT userid FROM userdownloads WHERE userid = " + userId +
         " AND productid = " + productId);
      boolean isOk = this.database.results.next();
      this.database.results.close();
      if(!isOk)
      {
         // See if the user is a distributor and the file is in their folder.
         isOk = this.isDistributorProduct(userId, productId);
      }

      return isOk;
   }

   public boolean isProductInFolder(int productId, int folderId) throws SQLException
   {
      if(productId == folderId)
         return true;

      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT pathorder FROM productpaths WHERE productid = " + productId +
         " AND parentid = " + folderId);
      boolean found = this.database.results.next();
      this.database.results.close();
      return found;
   }

   public int getDistributorFolder(int userId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      int folder = 0;
      this.database.results = this.database.statement.executeQuery(
         "SELECT folder FROM distributors WHERE userid = " + userId);
      if(this.database.results.next())
         folder = this.database.results.getInt("folder");

      this.database.results.close();
      return folder;
   }

   public boolean isDistributorProduct(int userId, int productId) throws SQLException
   {
      boolean distributor = false;
      int folderId = this.getDistributorFolder(userId);
      if(folderId > 0)
         distributor = this.isProductInFolder(productId, folderId);

      return distributor;
   }
}

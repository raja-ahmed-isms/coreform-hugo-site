/**
 * @(#)ClientDownloads.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/3/6
 */

package csimsoft;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;


public class ClientDownloads
{
   private ArrayList<ClientDownload> Downloads = new ArrayList<ClientDownload>();

   public ClientDownloads()
   {
   }

   public boolean isEmpty()
   {
      return this.Downloads.isEmpty();
   }

   public int getDownloadCount()
   {
      return this.Downloads.size();
   }

   public ClientDownload getDownload(int index)
   {
      return this.Downloads.get(index);
   }

   public void clear()
   {
      this.Downloads.clear();
   }

   public void setDownloads(DatabaseProducts products, int userId) throws SQLException
   {
      // Clean up the previous list.
      this.clear();

      if(products.database.statement == null)
         products.database.statement = products.database.connection.createStatement();

      // Get the list of user downloads.
      products.database.results = products.database.statement.executeQuery(
         "SELECT productid, product, location FROM products " +
         "WHERE productid IN (SELECT productid FROM userdownloads " +
         "WHERE userid = " + userId + ") ORDER BY product");
      while(products.database.results.next())
      {
         this.Downloads.add(new ClientDownload(
            products.database.results.getInt("productid"),
            products.database.results.getString("location")));
      }

      products.database.results.close();

      // Fill in the product paths.
      Iterator<ClientDownload> iter = this.Downloads.iterator();
      while(iter.hasNext())
      {
         ClientDownload download = iter.next();
         products.getProductPaths(download);
      }

      // Sort the tree based on the path name.
      java.util.Collections.sort(this.Downloads,
         new java.util.Comparator<ClientDownload>()
         {
            @Override
            public int compare(ClientDownload d1, ClientDownload d2)
            {
               String path1 = d1.getName();
               String path2 = d2.getName();
               if(path1 == null)
                  return path2 == null ? 0 : -1;
               else if(path2 == null)
                  return 1;

               return path1.compareTo(path2);
            }
         });
   }
}
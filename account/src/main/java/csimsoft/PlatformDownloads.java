/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;


public class PlatformDownloads
{
   public static class Download
   {
      private String PlatformName = null;
      private String ProductPath = null;
      private int PlatformId = 0;
      private int ProductId = 0;
      public boolean Autoadd = false;

      public Download()
      {
      }

      public int getPlatformId()
      {
         return this.PlatformId;
      }

      public void setPlatformId(int id)
      {
         this.PlatformId = id;
      }

      public String getPlatformName()
      {
         return this.PlatformName;
      }

      public void setPlatformName(String platformName)
      {
         this.PlatformName = platformName;
      }

      public int getProductId()
      {
         return this.ProductId;
      }

      public void setProductId(int id)
      {
         this.ProductId = id;
      }

      public String getProductPath()
      {
         return this.ProductPath;
      }

      public void setProductPath(String productPath)
      {
         this.ProductPath = productPath;
      }

      public String getChecked()
      {
         return this.Autoadd ? "checked=\"checked\"" : "";
      }
   }

   private ArrayList<PlatformDownloads.Download> Downloads =
      new ArrayList<PlatformDownloads.Download>();

   public PlatformDownloads()
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

   public PlatformDownloads.Download getDownload(int index)
   {
      return this.Downloads.get(index);
   }

   public PlatformDownloads.Download findDownload(int platformId)
   {
      Iterator<PlatformDownloads.Download> iter = this.Downloads.iterator();
      while(iter.hasNext())
      {
         PlatformDownloads.Download download = iter.next();
         if(download.getPlatformId() == platformId)
            return download;
      }

      return null;
   }

   public void setDownloads(int licenseId, DatabaseProducts products) throws SQLException
   {
      this.Downloads.clear();
      if(products.database.statement == null)
         products.database.statement = products.database.connection.createStatement();

      // Get the list of license platforms.
      products.database.results = products.database.statement.executeQuery(
         "SELECT platformid, platformname FROM platforms WHERE platformid IN " +
         "(SELECT platformid FROM platformdownloads WHERE licenseid = " +
         licenseId + ") ORDER BY platformname");
      while(products.database.results.next())
      {
         PlatformDownloads.Download download = new PlatformDownloads.Download();
         download.setPlatformId(products.database.results.getInt("platformid"));
         download.setPlatformName(products.database.results.getString("platformname"));
         this.Downloads.add(download);
      }

      products.database.results.close();
      if(this.Downloads.isEmpty())
         return;

      // Get the list of platform download ids.
      products.database.results = products.database.statement.executeQuery(
         "SELECT platformid, productid, autoadd FROM platformdownloads " +
         "WHERE licenseid = " + licenseId);
      while(products.database.results.next())
      {
         PlatformDownloads.Download download = this.findDownload(
            products.database.results.getInt("platformid"));
         if(download != null)
         {
            download.setProductId(products.database.results.getInt("productid"));
            download.Autoadd = products.database.results.getInt("autoadd") == 1;
         }
      }

      products.database.results.close();

      // Get the download path for each item.
      Iterator<PlatformDownloads.Download> iter = this.Downloads.iterator();
      while(iter.hasNext())
      {
         PlatformDownloads.Download download = iter.next();
         download.setProductPath(products.getProductNamePath(download.getProductId()));
      }
   }
}

/**
 * @(#)ClientDownload.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/3/6
 */

package csimsoft;


public class ClientDownload extends ProductInfo
{
   private String shortLocation = null;

   public ClientDownload()
   {
      super();
   }

   public ClientDownload(int id, String location)
   {
      super(id);
      this.shortLocation = location;
   }

   public String getShortLocation()
   {
      return this.shortLocation;
   }

   public void setShortLocation(String location)
   {
      this.shortLocation = location;
   }
}
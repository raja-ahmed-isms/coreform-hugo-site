/**
 * @(#)OrderSummaryDownload.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/4/1
 */

package csimsoft;


public class OrderSummaryDownload extends OrderSummaryItem
{
   private String Location = null;

   public OrderSummaryDownload()
   {
      super();
   }

   public OrderSummaryDownload(int id, String name)
   {
      super(id, name);
   }

   public String getLocation()
   {
      return this.Location;
   }

   public void setLocation(String location)
   {
      this.Location = location;
   }
}
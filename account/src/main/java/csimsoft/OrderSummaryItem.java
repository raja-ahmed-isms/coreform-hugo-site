/**
 * @(#)OrderSummaryItem.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/4/1
 */

package csimsoft;

import java.sql.ResultSet;
import java.sql.SQLException;


public class OrderSummaryItem
{
   private String ItemName = null;
   private int ItemId = 0;
   private int Quantity = 1;

   public OrderSummaryItem()
   {
   }

   public OrderSummaryItem(int id, String name)
   {
      this.ItemId = id;
      this.ItemName = name;
   }

   public OrderSummaryItem(int id, int quantity)
   {
      this.ItemId = id;
      this.Quantity = quantity;
   }

   public int getId()
   {
      return this.ItemId;
   }

   public void setId(int id)
   {
      this.ItemId = id;
   }

   public String getName()
   {
      if(this.ItemName == null)
         return "";

      return this.ItemName;
   }

   public void setName(String name)
   {
      this.ItemName = name;
   }

   public int getQuantity()
   {
      return this.Quantity;
   }

   public void setQuantity(int quantity)
   {
      this.Quantity = quantity;
   }

   public void setLicense(ResultSet results) throws SQLException
   {
      this.ItemId = results.getInt("licenseid");
      this.setName(results.getString("licensename"));
      this.Quantity = results.getInt("quantity");
   }
}
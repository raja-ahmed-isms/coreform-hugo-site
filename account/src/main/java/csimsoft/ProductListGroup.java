/**
 * @(#)ProductListGroup.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/2/29
 */

package csimsoft;

import java.util.ArrayList;


public class ProductListGroup extends ProductListItem
{
   private ArrayList<ProductListItem> Items = new ArrayList<ProductListItem>();

   public ProductListGroup()
   {
      super();
   }

   public ProductListGroup(int id, String name)
   {
      super(id, name);
   }

   @Override
   public boolean isFolder()
   {
      return true;
   }

   @Override
   public boolean isEmpty()
   {
      return this.Items.isEmpty();
   }

   @Override
   public void clear()
   {
      super.clear();
      this.Items.clear();
   }

   public int getItemCount()
   {
      return this.Items.size();
   }

   public ProductListItem getItem(int index)
   {
      return this.Items.get(index);
   }

   public int getItemIndex(ProductListItem item)
   {
      return this.Items.indexOf(item);
   }

   public void addItem(ProductListItem item)
   {
      if(!this.Items.contains(item))
      {
         this.Items.add(item);
         item.setParent(this);
      }
   }
}
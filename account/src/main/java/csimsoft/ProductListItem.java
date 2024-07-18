/**
 * @(#)ProductListItem.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/2/29
 */

package csimsoft;


public class ProductListItem
{
   private ProductListGroup Parent = null;
   private String ProductName = null;
   private String Location = null;
   private int ProductId = 0;
   private int ProcessId = 0;
   private int Depth = 0;

   public ProductListItem()
   {
   }

   public ProductListItem(int id, String name)
   {
      this.ProductId = id;
      this.ProductName = name;
   }

   public boolean isFolder()
   {
      return false;
   }

   public boolean isEmpty()
   {
      return true;
   }

   public void clear()
   {
      this.Parent = null;
      this.ProductName = null;
      this.Location = null;
      this.ProductId = 0;
   }

   public ProductListGroup getParent()
   {
      return this.Parent;
   }

   public void setParent(ProductListGroup parent)
   {
      this.Parent = parent;
   }

   public int getId()
   {
      return this.ProductId;
   }

   public void setId(int id)
   {
      this.ProductId = id;
   }

   public String getName()
   {
      return this.ProductName;
   }

   public String getJsName()
   {
      return this.ProductName.replace("'", "\\'");
   }

   public void setName(String name)
   {
      this.ProductName = name;
   }

   public String getFullName()
   {
      if(this.ProductName == null)
         return null;

      String fullName = this.ProductName;
      ProductListGroup parent = this.Parent;
      while(parent != null)
      {
         String parentName = parent.getName();
         if(parentName != null)
            fullName = parentName + "/" + fullName;

         parent = parent.getParent();
      }

      return fullName;
   }

   public String getLocation()
   {
      return this.Location;
   }

   public void setLocation(String location)
   {
      this.Location = location;
   }

   public String getFullLocation()
   {
      if(this.Location == null)
         return null;

      String fullLocation = this.Location;
      ProductListGroup parent = this.Parent;
      while(parent != null)
      {
         String parentLocation = parent.getLocation();
         if(parentLocation != null)
            fullLocation = parentLocation + "/" + fullLocation;

         parent = parent.getParent();
      }

      return fullLocation;
   }

   public int getProcessId()
   {
      return this.ProcessId;
   }

   public void setProcessId(int id)
   {
      this.ProcessId = id;
   }

   public int getDepth()
   {
      return this.Depth;
   }

   public void setDepth(int depth)
   {
      this.Depth = depth;
   }

   public int calculateDepth(ProductListGroup root, boolean showRoot)
   {
      this.Depth = showRoot ? 1 : 0;
      if(root == this)
         return this.Depth;

      ProductListGroup parent = this.Parent;
      while(parent != null && parent != root)
      {
         this.Depth++;
         parent = parent.getParent();
      }

      return this.Depth;
   }
}
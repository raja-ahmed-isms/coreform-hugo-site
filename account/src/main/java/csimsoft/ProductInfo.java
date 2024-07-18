package csimsoft;


public class ProductInfo
{
   private int productId = 0;
   private String name = null;
   private String location = null;

   public ProductInfo()
   {
   }

   public ProductInfo(int productId)
   {
      this.productId = productId;
   }

   public int getId()
   {
      return this.productId;
   }

   public void setId(int productId)
   {
      this.productId = productId;
   }

   public String getName()
   {
      return this.name;
   }

   public String getJsName()
   {
      return this.name.replace("'", "\\'");
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getLocation()
   {
      return this.location;
   }

   public void setLocation(String location)
   {
      this.location = location;
   }
}

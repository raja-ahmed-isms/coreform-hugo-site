/**
 *
 * @author Mark Richardson
 */
package csimsoft;


public class UpgradePathItem
{
   private String LicenseName = null;
   private int LicenseId = 0;
   private int UpgradeOrder = 0;

   public UpgradePathItem()
   {
   }

   public int getLicenseId()
   {
      return this.LicenseId;
   }

   public void setLicenseId(int id)
   {
      this.LicenseId = id;
   }

   public String getLicenseName()
   {
      return this.LicenseName;
   }

   public String getJsLicenseName()
   {
      if(this.LicenseName == null)
         return null;

      return this.LicenseName.replace("'", "\\'");
   }

   public void setLicenseName(String name)
   {
      this.LicenseName = name;
   }

   public int getUpgradeOrder()
   {
      return this.UpgradeOrder;
   }

   public void setUpgradeOrder(int order)
   {
      this.UpgradeOrder = order;
   }
}

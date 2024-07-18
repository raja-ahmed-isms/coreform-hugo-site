/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.ResultSet;
import java.sql.SQLException;



public class OrderSummaryLicense extends OrderSummaryItem
{
   private int LicenseId = 0;
   private int LicenseType = DatabaseLicenses.InvalidLicense;

   public OrderSummaryLicense()
   {
      super();
   }

   public OrderSummaryLicense(int id, String name)
   {
      super(id, name);
   }

   public OrderSummaryLicense(int id, int quantity)
   {
      super(id, quantity);
   }

   public int getLicenseId()
   {
      return this.LicenseId;
   }

   public void setLicenseId(int licenseId)
   {
      this.LicenseId = licenseId;
   }

   public int getLicenseType()
   {
      return this.LicenseType;
   }

   public void setLicenseType(int licenseType)
   {
      this.LicenseType = licenseType;
   }

   public String getLicenseTypeName()
   {
      return DatabaseLicenses.getTypeName(this.LicenseType);
   }

   @Override
   public void setLicense(ResultSet results) throws SQLException
   {
      this.setId(results.getInt("userlicenseid"));
      this.LicenseId = results.getInt("licenseid");
      this.LicenseType = results.getInt("licensetype");
      this.setQuantity(results.getInt("quantity"));
   }
}

/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;


public class ClientLicenses
{
   public static class License
   {
      public String Name = "";
      public String RefNumber = "&nbsp;";
      public String Maintenance = "";
      public String Expiration = "";
      public int LicenseId = 0;
      public int LicenseType = DatabaseLicenses.NodeLockedLicense;
      public int Quantity = 0;
      public int UpgradeId = 0;

      public License()
      {
      }

      public String getJsName()
      {
         return this.Name.replace("'", "\\'");
      }

      public String getType()
      {
         return DatabaseLicenses.getTypeName(this.LicenseType);
      }

      public void setInfo(ResultSet results) throws SQLException
      {
         this.LicenseId = results.getInt("userlicenseid");
         this.LicenseType = results.getInt("licensetype");
         this.Quantity = results.getInt("quantity");
         int numDays = results.getInt("numdays");
         this.Expiration = Database.formatDate(results.getDate("expiration"));
         this.UpgradeId = results.getInt("upgradeid");
         String upgradeDate = Database.formatDate(results.getDate("upgradedate"));
         this.Name = results.getString("licensename");

         if(this.Expiration == null && numDays > 0)
            this.Expiration = numDays + " days";

         if(this.Expiration == null)
         {
            if(upgradeDate == null)
               this.Expiration = "&nbsp;";
            else
               this.Expiration = upgradeDate;
         }
         else
            this.Maintenance = "Rental";
      }

      public void setReferenceNumber(String refNumber)
      {
         if(refNumber == null || refNumber.isEmpty())
            this.RefNumber = "&nbsp;";
         else
            this.RefNumber = refNumber;
      }

      public void setMaintenance(String maintenance)
      {
         if(this.Maintenance.isEmpty())
         {
            if(maintenance == null || maintenance.isEmpty())
               this.Maintenance = "None";
            else
               this.Maintenance = maintenance;
         }
      }
   }

   private ArrayList<ClientLicenses.License> Licenses =
      new ArrayList<ClientLicenses.License>();

   public ClientLicenses()
   {
   }

   public boolean isEmpty()
   {
      return this.Licenses.isEmpty();
   }

   public int getLicenseCount()
   {
      return this.Licenses.size();
   }

   public ClientLicenses.License getLicense(int index)
   {
      return this.Licenses.get(index);
   }

   public void clear()
   {
      this.Licenses.clear();
   }

   public void setLicenses(DatabaseLicenses licenses, int userId) throws SQLException
   {
      this.setLicenses(licenses, userId, false);
   }

   public void setLicenses(DatabaseLicenses licenses, int userId, boolean addRef)
      throws SQLException
   {
      TreeMap<Integer, String> upgrades = new TreeMap<Integer, String>();
      TreeMap<Integer, String> refs = new TreeMap<Integer, String>();
      if(addRef)
      {
         // Get the reference numbers for the user. Put them in the
         // upgrades map temporarily.
         licenses.database.results = licenses.database.statement.executeQuery(
            "SELECT orderid, distnumber FROM userorders WHERE userid = " + userId);
         while(licenses.database.results.next())
         {
            int orderId = licenses.database.results.getInt("orderid");
            String refNum = licenses.database.results.getString("distnumber");
            if(refNum != null && !refNum.isEmpty())
               upgrades.put(orderId, refNum);
         }

         licenses.database.results.close();
         if(!upgrades.isEmpty())
         {
            // Get the license ids for the user orders.
            licenses.database.results = licenses.database.statement.executeQuery(
               "SELECT orderid, licenseid FROM orderlicenses WHERE licenseid IN " +
               "(SELECT userlicenseid FROM licenses WHERE userid = " + userId + ")");
            while(licenses.database.results.next())
            {
               int orderId = licenses.database.results.getInt("orderid");
               int licenseId = licenses.database.results.getInt("licenseid");
               String refNum = upgrades.get(orderId);
               if(refNum != null)
                  refs.put(licenseId, refNum);
            }

            licenses.database.results.close();
            upgrades.clear();
         }
      }

      // Get the upgrade path names.
      licenses.getUpgradePaths();
      while(licenses.database.results.next())
      {
         int upgradeId = licenses.database.results.getInt("upgradeid");
         String name = licenses.database.results.getString("upgradename");
         upgrades.put(upgradeId, name);
      }

      licenses.database.results.close();

      // Get the client licenses from the database.
      licenses.getUserLicenses(userId);
      while(licenses.database.results.next())
      {
         ClientLicenses.License license = new ClientLicenses.License();
         license.setInfo(licenses.database.results);
         license.setMaintenance(upgrades.get(license.UpgradeId));
         license.setReferenceNumber(refs.get(license.LicenseId));
         this.Licenses.add(license);
      }

      licenses.database.results.close();
   }
}

/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;


public class ClientLicenseInfo
{
   public static class Feature
   {
      public String featureName = null;
      public int featureId = 0;
      public boolean selected = false;

      public Feature()
      {
      }

      public String getChecked()
      {
         return this.selected ? "checked=\"checked\"" : "";
      }
   }

   public static class Platform
   {
      public String platformName = null;
      public ProductInfo product = new ProductInfo();
      public int platformId = 0;
      public boolean selected = false;

      public Platform()
      {
      }

      public String getJsName()
      {
         return this.platformName.replace("'", "\\'");
      }
   }

   public static class Activation
   {
      public int activationId = 0;
      public String hostId = null;
      public String hostName = null;

      public Activation()
      {
      }

      public String getJsHostId()
      {
         return this.hostId.replace("'", "\\'");
      }

      public String getHostName()
      {
         if(this.hostName == null || this.hostName.isEmpty())
            return "No Name";

         return this.hostName;
      }

      public String getJsHostName()
      {
         return this.hostName.replace("'", "\\'");
      }
   }

   private ArrayList<ClientLicenseInfo.Feature> Features =
      new ArrayList<ClientLicenseInfo.Feature>();
   private ArrayList<ClientLicenseInfo.Feature> Options =
      new ArrayList<ClientLicenseInfo.Feature>();
   private ArrayList<ClientLicenseInfo.Platform> Platforms =
      new ArrayList<ClientLicenseInfo.Platform>();
   private ArrayList<ClientLicenseInfo.Activation> Activations =
      new ArrayList<ClientLicenseInfo.Activation>();
   private String LicenseName = null;
   private String LicenseKey = null;
   private String UpgradeName = null;
   private String Expiration = null;
   private String UpgradeDate = null;
   private String RefNumber = null;
   private String Version = null;
   private int LicenseType = DatabaseLicenses.NodeLockedLicense;
   private int Quantity = 0;
   private int Processes = 0;
   private int Deactivations = 0;
   private int MaxDeactivations = 0;
   private int OrderId = 0;
   private boolean VmAllowed = false;
   private boolean RdAllowed = false;

   public ClientLicenseInfo()
   {
   }

   public String getLicenseName()
   {
      return this.LicenseName;
   }

   public String getVersion()
   {
      return this.Version;
   }

   public int getLicenseType()
   {
      return this.LicenseType;
   }

   public String getLicenseTypeName()
   {
      return DatabaseLicenses.getTypeName(this.LicenseType);
   }

   public String getLicenseKey()
   {
      return this.LicenseKey;
   }

   public int getQuantity()
   {
      return this.Quantity;
   }

   public int getProcesses()
   {
      return this.Processes;
   }

   public String getProcessText()
   {
      if(this.Processes < 1)
         return "Unlimited";
      else if(this.Processes == 1)
         return "Single";
      else
         return Integer.toString(this.Processes);
   }

   public boolean isVirtualMachineAllowed()
   {
      return this.VmAllowed;
   }

   public String getVirtualMachine()
   {
      return this.VmAllowed ? "Enabled" : "Disabled";
   }

   public boolean isRemoteDesktopAllowed()
   {
      return this.RdAllowed;
   }

   public String getRemoteDesktop()
   {
      return this.RdAllowed ? "Enabled" : "Disabled";
   }

   public int getDeactivationCount()
   {
      return this.Deactivations;
   }

   public int getMaxDeactivations()
   {
      return this.MaxDeactivations;
   }

   public String getExpiration()
   {
      return this.Expiration;
   }

   public boolean showUpgrades()
   {
      return this.Expiration == null || "None".equals(this.Expiration);
   }

   public String getUpgradeName()
   {
      return this.UpgradeName;
   }

   public String getUpgradeDate()
   {
      return this.UpgradeDate;
   }

   public String getFeaturesAndOptions()
   {
      String features = "";
      Iterator<ClientLicenseInfo.Feature> iter = this.Features.iterator();
      while(iter.hasNext())
         features += iter.next().featureName + ", ";

      iter = this.Options.iterator();
      while(iter.hasNext())
         features += iter.next().featureName + ", ";

      // Remove the extra comma at the end of the list.
      if(!features.isEmpty())
         features = features.substring(0, features.length() - 2);

      return features;
   }

   public int getFeatureCount()
   {
      return this.Features.size();
   }

   public ClientLicenseInfo.Feature getFeature(int index)
   {
      return this.Features.get(index);
   }

   public int getOptionCount()
   {
      return this.Options.size();
   }

   public ClientLicenseInfo.Feature getOption(int index)
   {
      return this.Options.get(index);
   }

   public int getPlatformCount()
   {
      return this.Platforms.size();
   }

   public ClientLicenseInfo.Platform getPlatform(int index)
   {
      return this.Platforms.get(index);
   }

   public int getActivationCount()
   {
      return this.Activations.size();
   }

   public ClientLicenseInfo.Activation getActivation(int index)
   {
      return this.Activations.get(index);
   }

   public int getOrderId()
   {
      return this.OrderId;
   }

   public boolean hasOrderReference()
   {
      return this.RefNumber != null && !this.RefNumber.isEmpty();
   }

   public String getOrderReference()
   {
      return this.RefNumber;
   }

   public boolean setLicenseInfo(Database database, int userLicenseId)
      throws SQLException
   {
      return this.setLicenseInfo(database, userLicenseId, true);
   }

   public boolean setLicenseInfo(Database database, int userLicenseId,
      boolean onlySelected) throws SQLException
   {
      if(database.statement == null)
         database.statement = database.connection.createStatement();

      int licenseId = 0;
      int upgradeId = 0;
      database.results = database.statement.executeQuery(
         "SELECT licenseid, licensetype, licensekey, quantity, allowedinst, " +
         "vmallowed, rdallowed, deactivations, maxdeactivations, numdays, expiration, " +
         "upgradeid, upgradedate FROM licenses WHERE userlicenseid = " + userLicenseId);
      if(database.results.next())
      {
         licenseId = database.results.getInt("licenseid");
         this.LicenseType = database.results.getInt("licensetype");
         this.LicenseKey = database.results.getString("licensekey");
         if(this.LicenseKey == null)
            this.LicenseKey = "";

         this.Quantity = database.results.getInt("quantity");
         this.Processes = database.results.getInt("allowedinst");
         this.VmAllowed = database.results.getInt("vmallowed") != 0;
         this.RdAllowed = database.results.getInt("rdallowed") != 0;
         this.Deactivations = database.results.getInt("deactivations");
         this.MaxDeactivations = database.results.getInt("maxdeactivations");
         int numDays = database.results.getInt("numdays");
         this.Expiration = Database.formatDate(database.results.getDate("expiration"));
         if(this.Expiration == null || this.Expiration.isEmpty())
         {
            if(numDays > 0)
               this.Expiration = numDays + " Days";
            else
               this.Expiration = "None";
         }

         upgradeId = database.results.getInt("upgradeid");
         this.UpgradeDate = Database.formatDate(database.results.getDate("upgradedate"));
         if(this.UpgradeDate == null)
            this.UpgradeDate = "None";
      }
      database.results.close();
      DatabaseLicenses licenses = new DatabaseLicenses(database);
      this.Version = licenses.getUserLicenseVersion(userLicenseId);

      if(licenseId == 0)
         return false;

      // Get the license name.
      database.results = database.statement.executeQuery(
         "SELECT licensename FROM licenseproducts WHERE licenseid = " + licenseId);
      if(database.results.next())
      {
         this.LicenseName = database.results.getString("licensename");
         if(this.LicenseName == null)
            this.LicenseName = "";
      }

      database.results.close();

      // Format the license key.
      if(!this.LicenseKey.isEmpty())
      {
         int[] sizes = this.getLicenseGroupSizes(this.LicenseKey.length());
         if(sizes != null)
         {
            int first = sizes[0];
            String key = this.LicenseKey.substring(0, first);
            for(int i = 1; i < sizes.length; ++i)
            {
               int last = first + sizes[i];
               key += "-" + this.LicenseKey.substring(first, last);
               first = last;
            }

            this.LicenseKey = key;
         }
      }

      if(upgradeId > 0)
      {
         // Get the upgrade path name.
         database.results = database.statement.executeQuery(
            "SELECT upgradename FROM upgradepaths WHERE upgradeid = " + upgradeId);
         if(database.results.next())
            this.UpgradeName = database.results.getString("upgradename");

         database.results.close();
      }

      if(this.UpgradeName == null || this.UpgradeName.isEmpty())
         this.UpgradeName = "None";

      if(onlySelected)
         this.getSelectedFeatures(database, userLicenseId);
      else
         this.getAllFeatures(database, userLicenseId, licenseId);

      // Get the selected license platforms.
      TreeSet<Integer> selected = new TreeSet<Integer>();
      database.results = database.statement.executeQuery(
         "SELECT platformid FROM userplatforms WHERE userlicenseid = " + userLicenseId);
      while(database.results.next())
         selected.add(database.results.getInt("platformid"));

      database.results.close();

      // Get the license platforms.
      database.results = database.statement.executeQuery(
         "SELECT platformid, platformname FROM platforms WHERE platformid IN " +
         "(SELECT platformid FROM platformdownloads " +
         "WHERE licenseid = " + licenseId + ") ORDER BY platformname");
      while(database.results.next())
      {
         ClientLicenseInfo.Platform platform = new ClientLicenseInfo.Platform();
         platform.platformId = database.results.getInt("platformid");
         platform.platformName = database.results.getString("platformname");
         platform.selected = selected.contains(platform.platformId);
         this.Platforms.add(platform);
      }

      database.results.close();

      // Get the platform downloads.
      DatabaseProducts products = new DatabaseProducts(database);
      Iterator<ClientLicenseInfo.Platform> iter = this.Platforms.iterator();
      while(iter.hasNext())
      {
         ClientLicenseInfo.Platform platform = iter.next();
         database.results = database.statement.executeQuery(
            "SELECT productid FROM platformdownloads WHERE licenseid = " +
            licenseId + " AND platformid = " + platform.platformId);
         if(database.results.next())
            platform.product.setId(database.results.getInt("productid"));

         database.results.close();
         if(platform.product.getId() > 0)
            products.getProductPaths(platform.product);
      }

      // Get the license activations.
      database.results = database.statement.executeQuery(
         "SELECT activationid, hostid, hostname FROM licenseactivations " +
         "WHERE userlicenseid = " + userLicenseId + " ORDER BY hostname");
      while(database.results.next())
      {
         ClientLicenseInfo.Activation activation = new ClientLicenseInfo.Activation();
         activation.activationId = database.results.getInt("activationid");
         activation.hostId = database.results.getString("hostid");
         activation.hostName = database.results.getString("hostname");
         this.Activations.add(activation);
      }

      database.results.close();

      // Get the order id.
      database.results = database.statement.executeQuery(
         "SELECT orderid, distnumber FROM userorders WHERE orderid IN " +
         "(SELECT orderid FROM orderlicenses WHERE licenseid = " + userLicenseId + ")");
      if(database.results.next())
      {
         this.OrderId = database.results.getInt("orderid");
         this.RefNumber = database.results.getString("distnumber");
      }

      database.results.close();

      return true;
   }

   private void getSelectedFeatures(Database database, int userLicenseId)
      throws SQLException
   {
      // Get the license features.
      database.results = database.statement.executeQuery(
         "SELECT featureid, featurename FROM features WHERE featureid IN " +
         "(SELECT featureid FROM userfeatures WHERE userlicenseid = " +
         userLicenseId + ") ORDER BY featurename");
      while(database.results.next())
      {
         ClientLicenseInfo.Feature feature = new ClientLicenseInfo.Feature();
         feature.featureId = database.results.getInt("featureid");
         feature.featureName = database.results.getString("featurename");
         this.Features.add(feature);
      }

      database.results.close();

      // Get the license options.
      database.results = database.statement.executeQuery(
         "SELECT optionid, optionname FROM options WHERE optionid IN " +
         "(SELECT optionid FROM useroptions WHERE userlicenseid = " +
         userLicenseId + ") ORDER BY optionname");
      while(database.results.next())
      {
         ClientLicenseInfo.Feature option = new ClientLicenseInfo.Feature();
         option.featureId = database.results.getInt("optionid");
         option.featureName = database.results.getString("optionname");
         this.Options.add(option);
      }

      database.results.close();
   }

   private void getAllFeatures(Database database, int userLicenseId, int licenseId)
      throws SQLException
   {
      // Get the selected features.
      TreeSet<Integer> indexes = new TreeSet<Integer>();
      database.results = database.statement.executeQuery(
         "SELECT featureid FROM userfeatures WHERE userlicenseid = " + userLicenseId);
      while(database.results.next())
         indexes.add(new Integer(database.results.getInt("featureid")));

      database.results.close();

      // Get the available license features.
      database.results = database.statement.executeQuery(
         "SELECT featureid, featurename FROM features WHERE featureid IN " +
         "(SELECT featureid FROM licensefeatures WHERE licenseid = " +
         licenseId + ") ORDER BY featurename");
      while(database.results.next())
      {
         ClientLicenseInfo.Feature feature = new ClientLicenseInfo.Feature();
         feature.featureId = database.results.getInt("featureid");
         feature.featureName = database.results.getString("featurename");
         feature.selected = indexes.contains(new Integer(feature.featureId));
         this.Features.add(feature);
      }

      database.results.close();

      // Get the selected options.
      indexes.clear();
      database.results = database.statement.executeQuery(
         "SELECT optionid FROM useroptions WHERE userlicenseid = " + userLicenseId);
      while(database.results.next())
         indexes.add(new Integer(database.results.getInt("optionid")));

      database.results.close();

      // Get the license options.
      database.results = database.statement.executeQuery(
         "SELECT optionid, optionname FROM options WHERE optionid IN " +
         "(SELECT optionid FROM licenseoptions WHERE licenseid = " +
         licenseId + ") ORDER BY optionname");
      while(database.results.next())
      {
         ClientLicenseInfo.Feature option = new ClientLicenseInfo.Feature();
         option.featureId = database.results.getInt("optionid");
         option.featureName = database.results.getString("optionname");
         option.selected = indexes.contains(new Integer(option.featureId));
         this.Options.add(option);
      }

      database.results.close();
   }

   private int[] getLicenseGroupSizes(int length)
   {
      if(length < 8)
         return null;

      int numGroups = 0;
      int groupLength = 0;
      if(length < 35)
      {
         if(length < 12)
            numGroups = 2;
         else if(length < 16)
            numGroups = 3;
         else if(length < 25)
            numGroups = 4;
         else
            numGroups = 5;

         groupLength = length / numGroups;
      }
      else
      {
         groupLength = 6;
         numGroups = length / groupLength;
      }

      int[] sizes = new int[numGroups];
      for(int i = 0; i < numGroups; ++i)
         sizes[i] = groupLength;

      int remainder = length - (groupLength * numGroups);
      for(int i = 0; i < remainder; ++i)
      {
         int index = i / 2;
         if((i % 2) > 0)
            index = numGroups - index - 1;

         sizes[index] += 1;
      }

      return sizes;
   }
}

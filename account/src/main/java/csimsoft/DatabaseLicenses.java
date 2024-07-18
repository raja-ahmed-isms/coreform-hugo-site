/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;


public class DatabaseLicenses extends DatabaseMethods {
   public static final int ApprovalUnassigned = -1;
   public static final int DownloadRejected = 0;
   public static final int DownloadApproved = 1;

   public static final int InvalidLicense = 0;
   public static final int NodeLockedLicense = 1;
   public static final int FloatingLicense = 2;
   public static final int UsbLicense = 3;
   public static final int SnLicense = 4;
   public static final int RLMCloudLicense = 5;

   public static final String NodeLockedLicenseName = "Node Locked";
   public static final String FloatingLicenseName = "Floating";
   public static final String UsbLicenseName = "USB Key";
   public static final String SnLicenseName = "Serial Number";
   public static final String RLMCloudLicenseName = "RLM Cloud";

   public static final String[] ProcessTexts = {"Unlimited", "Single", "2", "3", "4", "5"};

   public DatabaseLicenses() {
      super();
   }

   public DatabaseLicenses(Database db) {
      super(db);
   }

   public static class LicensePlatform {
      public int LicenseId = 0;
      public int PlatformId = 0;

      public LicensePlatform() {
      }

      public LicensePlatform(int licenseId, int platformId) {
         this.LicenseId = licenseId;
         this.PlatformId = platformId;
      }
   }

   public static String getTypeName(int licenseType) {
      switch (licenseType) {
         case DatabaseLicenses.FloatingLicense:
            return DatabaseLicenses.FloatingLicenseName;
         case DatabaseLicenses.NodeLockedLicense:
            return DatabaseLicenses.NodeLockedLicenseName;
         case DatabaseLicenses.UsbLicense:
            return DatabaseLicenses.UsbLicenseName;
         case DatabaseLicenses.RLMCloudLicense:
            return DatabaseLicenses.RLMCloudLicenseName;
      }

      return "Invalid";
   }

   public void getProductLicenses() throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT licenseid, licensename, version, allowedinst, processid "
                  + "FROM licenseproducts ORDER BY licensename, version");
   }

   public boolean isLicenseInDatabase(String licenseName) throws SQLException {
      return this.getLicenseId(licenseName) > 0;
   }

   public int getLicenseId(String licenseName) throws SQLException {
      PreparedStatement statement = this.database.connection
            .prepareStatement("SELECT licenseid FROM licenseproducts WHERE licensename = ?");
      this.database.addStatement(statement);
      statement.setString(1, licenseName);
      this.database.results = statement.executeQuery();
      int licenseId = 0;
      if (this.database.results.next())
         licenseId = this.database.results.getInt("licenseid");

      this.database.results.close();
      return licenseId;
   }

   public LicenseNames getProductLicenseMap() throws SQLException {
      this.getProductLicenses();
      LicenseNames names = new LicenseNames();
      while (this.database.results.next()) {
         names.add(this.database.results.getInt("licenseid"),
               this.database.results.getString("licensename"));
      }

      this.database.results.close();
      return names;
   }

   public LicenseNames getProductLicenseMap(int userId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT licenseid, licensename FROM licenseproducts WHERE licenseid IN"
                  + "(SELECT licenseid FROM licenses WHERE userid = " + userId + ")");

      LicenseNames names = new LicenseNames();
      while (this.database.results.next()) {
         names.add(this.database.results.getInt("licenseid"),
               this.database.results.getString("licensename"));
      }

      this.database.results.close();
      return names;
   }

   public void getLicenseInfo(int licenseId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT licensename, version, allowedinst FROM licenseproducts "
                  + "WHERE licenseid = " + licenseId);
   }

   public void getLicenseInfo(String licensename) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
            "SELECT licensename, version, allowedinst, licenseid FROM licenseproducts "
                  + "WHERE licensename = " + licensename);
   }

   //This is used to make sure the same machine doesn't activate a free trial over and over
   public boolean hasHostIdActivatedLicenseBefore(int licenseId, String hostid) throws SQLException {
      this.database.results = this.database.statement.executeQuery(
         "SELECT COUNT(*) FROM (SELECT hostid FROM licenseactivations WHERE userlicenseid IN (SELECT userlicenseid FROM licenses WHERE licenseid = " + licenseId + ")) as trialhostids WHERE hostid = \"" + hostid + "\""
      );
      if ( this.database.results.next() && this.database.results.getInt(1) > 0 ) {
         return true;
      } else {
         return false;
      }
   }

   public String getLicenseName(int licenseId) throws SQLException {
      String name = null;
      this.getLicenseInfo(licenseId);
      if (this.database.results.next())
         name = this.database.results.getString("licensename");

      this.database.results.close();
      return name;
   }

   public void removeLicense(int licenseId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      // Call the order handlers to remove the license.
      DatabaseProcesses processes = new DatabaseProcesses(this.database);
      List<OrderProcessHandler> handlers = processes.getOrderHandlers();
      Iterator<OrderProcessHandler> iter = handlers.iterator();
      while (iter.hasNext())
         iter.next().removeLicense(this.database, licenseId);

      // Remove the license from the database.
      database.statement
            .executeUpdate("DELETE FROM distributorlicenses WHERE licenseid = " + licenseId);

      // Remove the user licenses from the database.
      this.removeUserLicenses(
            "IN (SELECT userlicenseid FROM licenses WHERE licenseid = " + licenseId + ")");
      database.statement.executeUpdate("DELETE FROM licenses WHERE licenseid = " + licenseId);

      database.statement
            .executeUpdate("DELETE FROM licensefeatures WHERE licenseid = " + licenseId);
      database.statement.executeUpdate("DELETE FROM licenseoptions WHERE licenseid = " + licenseId);
      database.statement
            .executeUpdate("DELETE FROM platformdownloads WHERE licenseid = " + licenseId);
      database.statement
            .executeUpdate("DELETE FROM licenseproducts WHERE licenseid = " + licenseId);
   }

   public void getProductFeatures() throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
            "SELECT featureid, featurename, featurekey FROM features " + "ORDER BY featurename");
   }

   public boolean isFeatureInDatabase(String featureName) throws SQLException {
      return this.getFeatureId(featureName) > 0;
   }

   public int getFeatureId(String featureName) throws SQLException {
      int featureId = 0;
      PreparedStatement statement = this.database.connection
            .prepareStatement("SELECT featureid FROM features WHERE featurename = ?");
      this.database.addStatement(statement);
      statement.setString(1, featureName);
      this.database.results = statement.executeQuery();
      if (this.database.results.next())
         featureId = this.database.results.getInt("featureid");

      this.database.results.close();
      return featureId;
   }

   public void getFeatureInfo(int featureId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
            "SELECT featurename, featurekey FROM features WHERE featureid = " + featureId);
   }

   public void getLicenseFeatures(int licenseId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
            "SELECT featureid, featurename, featurekey FROM features WHERE featureid IN "
                  + "(SELECT featureid FROM licensefeatures WHERE licenseid = " + licenseId + ") "
                  + "ORDER BY featurename");
   }

   public void getDefaultFeatures(int licenseId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT featureid FROM licensefeatures WHERE licenseid = " + licenseId
                  + " AND autoadd = 1");
   }

   public boolean isFeatureUsed(int licenseId, int featureId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT featureid FROM licensefeatures WHERE licenseid = " + licenseId
                  + " AND featureid = " + featureId);
      boolean exists = this.database.results.next();
      this.database.results.close();
      return exists;
   }

   public void getProductOptions() throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
            "SELECT optionid, optionname, optionkey FROM options " + "ORDER BY optionname");
   }

   public boolean isOptionInDatabase(String optionName) throws SQLException {
      return this.getOptionId(optionName) > 0;
   }

   public int getOptionId(String optionName) throws SQLException {
      int optionId = 0;
      PreparedStatement statement = this.database.connection
            .prepareStatement("SELECT optionid FROM options WHERE optionname = ?");
      this.database.addStatement(statement);
      statement.setString(1, optionName);
      this.database.results = statement.executeQuery();
      if (this.database.results.next())
         optionId = this.database.results.getInt("optionid");

      this.database.results.close();
      return optionId;
   }

   public void getOptionInfo(int optionId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT optionname, optionkey FROM options WHERE optionid = " + optionId);
   }

   public void getLicenseOptions(int licenseId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT optionid, optionname, optionkey FROM options WHERE optionid IN "
                  + "(SELECT optionid FROM licenseoptions WHERE licenseid = " + licenseId + ")");
   }

   public boolean isOptionUsed(int licenseId, int optionId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT optionid FROM licenseoptions WHERE licenseid = " + licenseId
                  + " AND optionid = " + optionId);
      boolean exists = this.database.results.next();
      this.database.results.close();
      return exists;
   }

   public void getPlatforms() throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
            "SELECT platformid, platformname FROM platforms " + "ORDER BY platformname");
   }

   public boolean isPlatformInDatabase(String platformName) throws SQLException {
      return this.getPlatformId(platformName) > 0;
   }

   public int getPlatformId(String platformName) throws SQLException {
      int platformId = 0;
      PreparedStatement statement = this.database.connection
            .prepareStatement("SELECT platformid FROM platforms WHERE platformname = ?");
      this.database.addStatement(statement);
      statement.setString(1, platformName);
      this.database.results = statement.executeQuery();
      if (this.database.results.next())
         platformId = this.database.results.getInt("platformid");

      this.database.results.close();
      return platformId;
   }

   public String getPlatformName(int platformId) throws SQLException {
      String name = null;
      this.getPlatformInfo(platformId);
      if (this.database.results.next())
         name = this.database.results.getString("platformname");

      this.database.results.close();
      return name;
   }

   public void getPlatformInfo(int platformId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT platformname FROM platforms WHERE platformid = " + platformId);
   }

   public int getPlatformProduct(int licenseId, int platformId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      int productId = 0;
      this.database.results = this.database.statement
            .executeQuery("SELECT productid FROM platformdownloads WHERE platformid = " + platformId
                  + " AND licenseid = " + licenseId);
      if (this.database.results.next())
         productId = this.database.results.getInt("productid");

      this.database.results.close();
      return productId;
   }

   public void getDefaultPlatforms(int licenseId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT platformid FROM platformdownloads WHERE licenseid = " + licenseId
                  + " AND autoadd = 1");
   }

   public boolean isPlatformUsed(int licenseId, int platformId) throws SQLException {
      return this.getPlatformProduct(licenseId, platformId) > 0;
   }

   public boolean isUserPlatformUsed(int licenseId, int platformId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT platformid FROM userplatforms WHERE platformid = " + platformId
                  + " AND userlicenseid = " + licenseId);
      boolean exists = this.database.results.next();
      this.database.results.close();
      return exists;
   }

   public boolean isPlatformDownloadUsed(int userId, int productId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      // Get the list of license platforms using the given product.
      boolean found = false;
      ArrayList<DatabaseLicenses.LicensePlatform> platforms =
            new ArrayList<DatabaseLicenses.LicensePlatform>();
      this.database.results = this.database.statement.executeQuery(
            "SELECT platformid, licenseid FROM platformdownloads WHERE productid = " + productId);
      while (this.database.results.next()) {
         int platformId = this.database.results.getInt("platformid");
         int licenseId = this.database.results.getInt("licenseid");
         platforms.add(new DatabaseLicenses.LicensePlatform(licenseId, platformId));
      }

      this.database.results.close();

      // See if the user is using any of the license platforms.
      Iterator<DatabaseLicenses.LicensePlatform> iter = platforms.iterator();
      while (!found && iter.hasNext()) {
         DatabaseLicenses.LicensePlatform platform = iter.next();
         this.database.results = this.database.statement
               .executeQuery("SELECT platformid FROM userplatforms WHERE userlicenseid IN "
                     + "(SELECT userlicenseid FROM licenses WHERE userid = " + userId
                     + " AND licenseid = " + platform.LicenseId + ")");
         while (!found && this.database.results.next()) {
            int platformId = this.database.results.getInt("platformid");
            found = platformId == platform.PlatformId;
         }

         this.database.results.close();
      }

      return found;
   }

   public void getUpgradePaths() throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
            "SELECT upgradeid, upgradename FROM upgradepaths " + "ORDER BY upgradename");
   }

   public boolean isUpgradeInDatabase(String upgradeName) throws SQLException {
      return this.getUpgradeId(upgradeName) > 0;
   }

   public int getUpgradeId(String upgradeName) throws SQLException {
      int upgradeId = 0;
      PreparedStatement statement = this.database.connection
            .prepareStatement("SELECT upgradeid FROM upgradepaths WHERE upgradename = ?");
      this.database.addStatement(statement);
      statement.setString(1, upgradeName);
      this.database.results = statement.executeQuery();
      if (this.database.results.next())
         upgradeId = this.database.results.getInt("upgradeid");

      this.database.results.close();
      return upgradeId;
   }

   public String getUpgradeName(int upgradeId) throws SQLException {
      String name = null;
      this.getUpgradeInfo(upgradeId);
      if (this.database.results.next())
         name = this.database.results.getString("upgradename");

      this.database.results.close();
      return name;
   }

   public void getUpgradeInfo(int upgradeId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT upgradename FROM upgradepaths WHERE upgradeid = " + upgradeId);
   }

   public void getUpgradePathItems(int upgradeId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results =
            this.database.statement.executeQuery("SELECT licenseid, upgradeorder FROM upgradeitems "
                  + "WHERE upgradeid = " + upgradeId + " ORDER BY upgradeorder DESC");
   }

   public boolean isUpgradeItemUsed(int upgradeId, int licenseId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT upgradeorder FROM upgradeitems WHERE upgradeid = " + upgradeId
                  + " AND licenseid = " + licenseId);
      boolean exists = this.database.results.next();
      this.database.results.close();
      return exists;
   }

   public void getUserLicenseInfo(int licenseId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
            "SELECT licenseid, licensetype, quantity, allowedinst, vmallowed, rdallowed,"
                  + " numdays, expiration, version FROM licenses WHERE userlicenseid = " + licenseId);
   }

   public String getUserUpgradeDate(int licenseId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      String expiration = null;
      this.database.results = this.database.statement
            .executeQuery("SELECT upgradedate FROM licenses WHERE userlicenseid = " + licenseId);
      if (this.database.results.next()) {
         java.sql.Date date = this.database.results.getDate("upgradedate");
         expiration = Database.formatDate(date);
      }

      this.database.results.close();
      return expiration;
   }

   public int getUserLicenseOwner(int licenseId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      int userId = 0;
      this.database.results = this.database.statement
            .executeQuery("SELECT userid FROM licenses WHERE userlicenseid = " + licenseId);
      if (this.database.results.next())
         userId = this.database.results.getInt("userid");

      this.database.results.close();
      return userId;
   }

   public boolean isUserLicenseOwner(int licenseId, int userId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT userlicenseid FROM licenses WHERE userlicenseid = " + licenseId
                  + " AND userid = " + userId);
      boolean exists = this.database.results.next();
      this.database.results.close();
      return exists;
   }

   public boolean isLicenseGroupInDatabase(String groupName) throws SQLException {
      return this.getLicenseGroupId(groupName) > 0;
   }

   public int getLicenseGroupId(String groupName) throws SQLException {
      int groupId = 0;
      PreparedStatement statement = this.database.connection
            .prepareStatement("SELECT groupid FROM licensegroups WHERE groupname = ?");
      this.database.addStatement(statement);
      statement.setString(1, groupName);
      this.database.results = statement.executeQuery();
      if (this.database.results.next())
         groupId = this.database.results.getInt("groupid");

      this.database.results.close();
      return groupId;
   }

   public String getLicenseGroupName(int groupId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      String name = null;
      this.database.results = this.database.statement
            .executeQuery("SELECT groupname FROM licensegroups WHERE groupid = " + groupId);
      if (this.database.results.next())
         name = this.database.results.getString("groupname");

      this.database.results.close();
      return name;
   }

   public void getProductLicenses(int distributorId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT licenseid, licensename, version, allowedinst, processid "
                  + "FROM licenseproducts WHERE licenseid IN (SELECT licenseid "
                  + "FROM distributorlicenses WHERE userid = " + distributorId
                  + ") ORDER BY licensename, version");
   }

   public void getUpgradePaths(int distributorId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT upgradeid, upgradename FROM upgradepaths WHERE upgradeid IN "
                  + "(SELECT upgradeid FROM distributorupgrades WHERE userid = " + distributorId
                  + ") ORDER BY upgradename");
   }

   public boolean isDistributorLicenseInDatabase(int userId, int licenseId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results =
            this.database.statement.executeQuery("SELECT licenseid FROM distributorlicenses "
                  + "WHERE userid = " + userId + " AND licenseid = " + licenseId);
      boolean exists = this.database.results.next();
      this.database.results.close();
      return exists;
   }

   public boolean isDistributorUpgradeInDatabase(int userId, int upgradeId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results =
            this.database.statement.executeQuery("SELECT upgradeid FROM distributorupgrades "
                  + "WHERE userid = " + userId + " AND upgradeid = " + upgradeId);
      boolean exists = this.database.results.next();
      this.database.results.close();
      return exists;
   }

   public void getUserLicenses(int userId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT licenses.userlicenseid, licenses.licensetype, licenses.quantity, "
                  + "licenses.numdays, licenses.expiration, licenses.upgradeid, "
                  + "licenses.upgradedate, licenseproducts.licensename "
                  + "FROM licenses INNER JOIN licenseproducts "
                  + "ON licenses.licenseid = licenseproducts.licenseid " + "WHERE userid = "
                  + userId + " ORDER BY licensename");
   }

   public String getUserLicenseVersion(int userLicenseId) throws SQLException {
      String version = "";
      int licenseid = 0;
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();
      this.database.results = this.database.statement.executeQuery("SELECT version, licenseid FROM licenses WHERE userlicenseid = " + userLicenseId);
      if(this.database.results.next()) {
         licenseid = this.database.results.getInt("licenseid");
         version = this.database.results.getString("version");
         if(this.database.results.wasNull()) {
            version = "";
         }
      }
      if(version.isEmpty()) {
         this.database.results = this.database.statement.executeQuery("SELECT version from licenseproducts WHERE licenseid = " + licenseid);
         if(this.database.results.next()) {
            version = this.database.results.getString("version");
         }
      }
      return version;
   }

   public int addClientLicense(int userId, int licenseId, String expiration) throws SQLException {
      return addClientLicense(userId, licenseId, expiration, 0);
   }

   public int addLearnLicense(int userId) throws SQLException {
      String query = "INSERT INTO licenses (userlicenseid, userid, licenseid, licensetype, "
            + "licensekey, quantity, allowedinst, vmallowed, rdallowed, deactivations, "
            + "maxdeactivations, numdays, expiration, startdate, upgradeid, upgradedate) "
            + "VALUES (?, ?, ?, ?, 'cubitlearn', 1, 1, 1, 1, 0, 0, 0, ?, NULL, 0, NULL)";
      PreparedStatement statement = this.database.connection.prepareStatement(query);
      this.database.addStatement(statement);

      // Expiration date is one year from sign up
      java.util.Calendar date = java.util.Calendar.getInstance();
      date.set(java.util.Calendar.YEAR, date.get(java.util.Calendar.YEAR) + 1);

      String expiration = Database.formatDate(date);

      // Get the license id
      int licenseId = 1;
      getLicenseInfo("'Coreform Cubit Learn'");
      if (this.database.results.next())
         licenseId = this.database.results.getInt("licenseid");

      this.database.results.close();

      // Get the next user license id.
      int nextId = this.database.getNextId("userlicensecount", "userlicenseid");

      // Add the new user license.
      statement.setInt(1, nextId);
      statement.setInt(2, userId);
      statement.setInt(3, licenseId);
      statement.setInt(4, DatabaseLicenses.FloatingLicense);
      statement.setString(5, expiration);
      statement.executeUpdate();

      // Get the default license platforms.
      ArrayList<Integer> platforms = new ArrayList<Integer>();
      ArrayList<Integer> products = new ArrayList<Integer>();
      this.database.results = this.database.statement.executeQuery(
            "SELECT platformid, productid FROM platformdownloads WHERE autoadd = 1 AND licenseid = "
                  + licenseId);
      while (this.database.results.next()) {
         platforms.add(this.database.results.getInt("platformid"));
         products.add(this.database.results.getInt("productid"));
      }

      this.database.results.close();

      // Add the platforms to the user licence.
      Iterator<Integer> iter = platforms.iterator();
      while (iter.hasNext()) {
         int platformId = iter.next();
         this.database.statement
               .executeUpdate("INSERT INTO userplatforms (userlicenseid, platformid) VALUES ("
                     + nextId + ", " + platformId + ")");

      }
      Iterator<Integer> iter_products = products.iterator();
      while (iter_products.hasNext()) {
         int productId = iter_products.next();
         this.database.statement
               .executeUpdate("INSERT INTO userdownloads (productid, userid) VALUES (" + productId
                     + ", " + userId + ")");
      }

      return nextId;
   }

   public int addClientLicense(int userId, int licenseId, String expiration, int numDays )
         throws SQLException {
      AcctLogger.get().info("Start add client license");
      String version = "";
      String licensename = "";
      // Check to see if it's a dynamic license, if it is then generate a version
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();
      this.database.results = this.database.statement
            .executeQuery("SELECT version, licensename FROM licenseproducts WHERE licenseid = " + licenseId);
      if(this.database.results.next()) {
         version = this.database.results.getString("version");
         licensename = this.database.results.getString("licensename");
      }
      // If this is a trial license, calculate an expiration date 30 days away
      if(licensename.contains("Trial") && numDays == 0 ) {
         java.util.Calendar date = java.util.Calendar.getInstance();
         date.add(java.util.Calendar.DATE, 30);
         expiration = Database.formatDate(date);
      }
      if(this.database.results.wasNull() || version.isEmpty() || version.equals("dynamic")) {
         java.util.Calendar today = java.util.Calendar.getInstance();
         int yearAdd = 1;
         int monthAdd = 1;
         if(expiration != null && !expiration.isEmpty()) {
            java.util.Calendar expirationDate = Database.dateFromString(expiration);
            int expirationYearDiff = expirationDate.get(java.util.Calendar.YEAR) - today.get(java.util.Calendar.YEAR);
            int expirationMonthDiff = expirationDate.get(java.util.Calendar.MONTH) - today.get(java.util.Calendar.MONTH);
            if(expirationYearDiff >= yearAdd) {
               yearAdd = expirationYearDiff;
               monthAdd = expirationMonthDiff;
            }
         }
         version = Integer.toString(today.get(java.util.Calendar.YEAR) + yearAdd) + "." + Integer.toString(today.get(java.util.Calendar.MONTH) + monthAdd);
      }
      AcctLogger.get().info("Version: " + version);

      String query = "INSERT INTO licenses (userlicenseid, userid, licenseid, licensetype, "
            + "licensekey, quantity, allowedinst, vmallowed, rdallowed, deactivations, "
            + "maxdeactivations, numdays, expiration, startdate, upgradeid, upgradedate, version) "
            + "VALUES (?, ?, ?, ?, ?, 1, ?, 1, 1, 0, ?, ?, ";
      if (expiration == null || expiration.isEmpty())
         query += "NULL";
      else {
         numDays = 0;
         query += "?";
      }

      query += ", NULL, 0, NULL, ?)";
      PreparedStatement statement = this.database.connection.prepareStatement(query);
      this.database.addStatement(statement);

      // Get the default number of allowed processes.
      int processes = 1;
      getLicenseInfo(licenseId);
      if (this.database.results.next())
         processes = this.database.results.getInt("allowedinst");

      this.database.results.close();

      // Get the next user license id.
      int nextId = this.database.getNextId("userlicensecount", "userlicenseid");

      // Add the new user license.
      statement.setInt(1, nextId);
      statement.setInt(2, userId);
      statement.setInt(3, licenseId);
      statement.setInt(4, DatabaseLicenses.NodeLockedLicense);
      statement.setString(5, Database.generateKey());
      statement.setInt(6, processes);
      statement.setInt(7, 5);
      statement.setInt(8, numDays);
      if (expiration != null && !expiration.isEmpty()) {
         statement.setString(9, expiration);
         statement.setString(10, version);
      }
      else {
         statement.setString(9, version);
      }
      

      statement.executeUpdate();
      AcctLogger.get().info("After insert");

      // Get the default features.
      ArrayList<Integer> features = new ArrayList<Integer>();
      this.database.results = this.database.statement
            .executeQuery("SELECT featureid FROM licensefeatures WHERE licenseid = " + licenseId
                  + " AND autoadd = 1");
      while (this.database.results.next())
         features.add(this.database.results.getInt("featureid"));

      this.database.results.close();

      // Add the features to the user license.
      Iterator<Integer> iter = features.iterator();
      while (iter.hasNext()) {
         int featureId = iter.next();
         this.database.statement
               .executeUpdate("INSERT INTO userfeatures (userlicenseid, featureid) VALUES ("
                     + nextId + ", " + featureId + ")");
      }

      // Get the default license platforms.
      ArrayList<Integer> platforms = new ArrayList<Integer>();
      this.database.results = this.database.statement.executeQuery(
            "SELECT platformid FROM platformdownloads WHERE autoadd = 1 AND licenseid = "
                  + licenseId);
      while (this.database.results.next())
         platforms.add(this.database.results.getInt("platformid"));

      this.database.results.close();

      // Add the platforms to the user licence.
      iter = platforms.iterator();
      while (iter.hasNext()) {
         int platformId = iter.next();
         this.database.statement
               .executeUpdate("INSERT INTO userplatforms (userlicenseid, platformid) VALUES ("
                     + nextId + ", " + platformId + ")");
      }
      AcctLogger.get().info("End addClientLicense");

      return nextId;
   }

   public void removeClientLicense(int licenseId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      // Get and execute all the updates for the order process handlers.
      DatabaseProcesses processes = new DatabaseProcesses(this.database);
      List<OrderProcessHandler> handlers = processes.getOrderHandlers();
      Iterator<OrderProcessHandler> iter = handlers.iterator();
      while (iter.hasNext())
         iter.next().removeUserLicense(processes.database, licenseId);

      this.removeUserLicenses("= " + licenseId);
      database.statement.executeUpdate("DELETE FROM licenses WHERE userlicenseid = " + licenseId);
   }

   public void removeUserLicenses(int userId) throws SQLException {
      this.removeUserLicenses(
            "IN (SELECT userlicenseid FROM licenses WHERE userid = " + userId + ")");
      database.statement.executeUpdate("DELETE FROM licenses WHERE userid = " + userId);
   }

   private void removeUserLicenses(String condition) throws SQLException {
      this.removeUserLicenseSignatures(condition);
      this.removeUserLicenseVmSignatures(condition);
      database.statement
            .executeUpdate("DELETE FROM licenseactivations WHERE userlicenseid " + condition);

      database.statement.executeUpdate("DELETE FROM orderlicenses WHERE licenseid " + condition);

      database.statement
            .executeUpdate("DELETE FROM userplatforms WHERE userlicenseid " + condition);
      database.statement.executeUpdate("DELETE FROM userfeatures WHERE userlicenseid " + condition);
      database.statement.executeUpdate("DELETE FROM useroptions WHERE userlicenseid " + condition);
   }

   public String getUserLicenseKey(int licenseId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      String licenseKey = null;
      this.database.results = this.database.statement
            .executeQuery("SELECT licensekey FROM licenses WHERE userlicenseid = " + licenseId);
      if (this.database.results.next())
         licenseKey = this.database.results.getString("licensekey");

      this.database.results.close();
      return licenseKey;
   }

   public void removeUserLicenseSignatures(int licenseId) throws SQLException {
      this.removeUserLicenseSignatures("= " + licenseId);
   }

   public void removeLicenseSignatures(int licenseId) throws SQLException {
      this.removeUserLicenseSignatures(
            "IN (SELECT userlicenseid FROM licenses WHERE licenseid = " + licenseId + ")");
   }

   private void removeUserLicenseSignatures(String condition) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      /*
       * this.database.statement.executeUpdate(
       * "DELETE FROM activationfeatures WHERE activationid IN " +
       * "(SELECT activationid FROM licenseactivations WHERE userlicenseid " + condition + ")");
       */
      // This should be equivalent, but faster.
      this.database.statement.executeUpdate(
            "DELETE activationfeatures FROM activationfeatures INNER JOIN licenseactivations ON activationfeatures.activationid=licenseactivations.activationid WHERE userlicenseid "
                  + condition);
   }

   public void removeUserLicenseVmSignatures(int licenseId) throws SQLException {
      this.removeUserLicenseVmSignatures("= " + licenseId);
   }

   private void removeUserLicenseVmSignatures(String condition) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.statement.executeUpdate("DELETE FROM vmsign WHERE licenseid " + condition);
   }

   public int getProcessLicense(int processId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      int productId = 0;
      this.database.results = this.database.statement
            .executeQuery("SELECT productid FROM processlicense WHERE processid = " + processId);
      if (this.database.results.next())
         productId = this.database.results.getInt("productid");

      this.database.results.close();
      return productId;
   }

   public int getOrderProcessLicense(int orderId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      int productId = 0;
      this.database.results = this.database.statement
            .executeQuery("SELECT productid FROM processlicense WHERE processid IN "
                  + "(SELECT processid FROM userorders WHERE orderid = " + orderId + ")");
      if (this.database.results.next())
         productId = this.database.results.getInt("productid");

      this.database.results.close();
      return productId;
   }

   public boolean isProcessLicenseApproved(int userId, int processId) throws SQLException {
      int productId = this.getProcessLicense(processId);
      if (productId > 0)
         return this.isDownloadApproved(userId, productId);

      return false;
   }

   public boolean isDownloadApproved(int userId, int productId) throws SQLException {
      return this.getDownloadApproval(userId, productId) == DatabaseLicenses.DownloadApproved;
   }

   public int getDownloadApproval(int userId, int productId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      int approval = DatabaseLicenses.ApprovalUnassigned;
      this.database.results = this.database.statement
            .executeQuery("SELECT approval FROM userapprovals WHERE userid = " + userId
                  + " AND productid = " + productId);
      if (this.database.results.next())
         approval = this.database.results.getInt("approval");

      this.database.results.close();
      return approval;
   }

   public void setDownloadApproval(int userId, int productId, int approval) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT approval FROM userapprovals WHERE userid = " + userId
                  + " AND productid = " + productId);
      boolean valid = this.database.results.next();
      this.database.results.close();
      if (!valid) {
         this.database.statement
               .executeUpdate("INSERT INTO userapprovals (userid, productid, approval) "
                     + "VALUES (" + userId + ", " + productId + ", " + approval + ")");
      } else {
         this.database.statement.executeUpdate("UPDATE userapprovals SET approval = " + approval
               + " WHERE userid = " + userId + " AND productid = " + productId);
      }
   }

   public boolean isOrderDownloadApproved(int userId, int orderId) throws SQLException {
      return this.getOrderDownloadApproval(userId, orderId) == DatabaseLicenses.DownloadApproved;
   }

   public int getOrderDownloadApproval(int orderId) throws SQLException {
      // Get the user for the order.
      DatabaseOrders orders = new DatabaseOrders(this.database);
      int userId = orders.getOrderUserId(orderId);
      if (userId < 1)
         return DatabaseLicenses.ApprovalUnassigned;
      else
         return this.getOrderDownloadApproval(userId, orderId);
   }

   public int getOrderDownloadApproval(int userId, int orderId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      // Get the list of product approvals from the order.
      this.database.results = this.database.statement
            .executeQuery("SELECT approval FROM userapprovals WHERE userid = " + userId
                  + " AND productid IN (SELECT productid FROM orderproducts WHERE orderid = "
                  + orderId + ")");
      return this.getResultsApproval();
   }

   public int setOrderDownloadApproval(int userId, int orderId, int approval) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      // Get the list of products for the order.
      ArrayList<Integer> products = new ArrayList<Integer>();
      this.database.results = this.database.statement
            .executeQuery("SELECT productid FROM orderproducts WHERE orderid = " + orderId);
      while (this.database.results.next())
         products.add(new Integer(this.database.results.getInt("productid")));

      this.database.results.close();
      Iterator<Integer> iter = products.iterator();
      while (iter.hasNext()) {
         int productId = iter.next().intValue();
         this.setDownloadApproval(userId, productId, approval);
      }

      return products.size();
   }

   public boolean isLicenseApproved(int userId, int licenseId) throws SQLException {
      return this.getLicenseApproval(userId, licenseId) == DatabaseLicenses.DownloadApproved;
   }

   public int getLicenseApproval(int userId, int licenseId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      int approval = DatabaseLicenses.ApprovalUnassigned;
      this.database.results = this.database.statement
            .executeQuery("SELECT approval FROM licenseapprovals WHERE userid = " + userId
                  + " AND licenseid = " + licenseId);
      if (this.database.results.next())
         approval = this.database.results.getInt("approval");

      this.database.results.close();
      return approval;
   }

   public void setLicenseApproval(int userId, int licenseId, int approval) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement
            .executeQuery("SELECT approval FROM licenseapprovals WHERE userid = " + userId
                  + " AND licenseid = " + licenseId);
      boolean valid = this.database.results.next();
      this.database.results.close();
      if (!valid) {
         this.database.statement
               .executeUpdate("INSERT INTO licenseapprovals (userid, licenseid, approval) "
                     + "VALUES (" + userId + ", " + licenseId + ", " + approval + ")");
      } else {
         this.database.statement.executeUpdate("UPDATE licenseapprovals SET approval = " + approval
               + " WHERE userid = " + userId + " AND licenseid = " + licenseId);
      }
   }

   public int getOrderLicenseApproval(int orderId) throws SQLException {
      // Get the user for the order.
      DatabaseOrders orders = new DatabaseOrders(this.database);
      int userId = orders.getOrderUserId(orderId);
      if (userId < 1)
         return DatabaseLicenses.ApprovalUnassigned;
      else
         return this.getOrderLicenseApproval(userId, orderId);
   }

   public int getOrderLicenseApproval(int userId, int orderId) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      // Get the list of license approvals from the order.
      this.database.results = this.database.statement
            .executeQuery("SELECT approval FROM licenseapprovals WHERE userid = " + userId
                  + " AND licenseid IN (SELECT licenseid FROM licenses WHERE userlicenseid IN "
                  + "(SELECT licenseid FROM orderlicenses WHERE orderid = " + orderId + "))");
      return this.getResultsApproval();
   }

   public int setOrderLicenseApproval(int userId, int orderId, int approval) throws SQLException {
      if (this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      // Get the list of products for the order.
      TreeSet<Integer> licenses = new TreeSet<Integer>();
      this.database.results = this.database.statement
            .executeQuery("SELECT licenseid FROM licenses WHERE userlicenseid IN "
                  + "(SELECT licenseid FROM orderlicenses WHERE orderid = " + orderId + ")");
      while (this.database.results.next())
         licenses.add(new Integer(this.database.results.getInt("licenseid")));

      this.database.results.close();
      Iterator<Integer> iter = licenses.iterator();
      while (iter.hasNext()) {
         int licenseId = iter.next().intValue();
         this.setLicenseApproval(userId, licenseId, approval);
      }

      return licenses.size();
   }

   private int getResultsApproval() throws SQLException {
      int unset = 0;
      int approved = 0;
      int rejected = 0;
      while (rejected == 0 && this.database.results.next()) {
         int approval = this.database.results.getInt("approval");
         if (approval == DatabaseLicenses.ApprovalUnassigned)
            ++unset;
         else if (approval == DatabaseLicenses.DownloadRejected)
            ++rejected;
         else if (approval == DatabaseLicenses.DownloadApproved)
            ++approved;
      }

      this.database.results.close();
      if (rejected > 0)
         return DatabaseLicenses.DownloadRejected;
      else if (unset > 0)
         return DatabaseLicenses.ApprovalUnassigned;
      else if (approved > 0)
         return DatabaseLicenses.DownloadApproved;

      return DatabaseLicenses.ApprovalUnassigned;
   }
}

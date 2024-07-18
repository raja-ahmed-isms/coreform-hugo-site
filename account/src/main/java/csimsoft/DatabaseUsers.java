/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class DatabaseUsers extends DatabaseMethods
{

   public DatabaseUsers()
   {
      super();
   }

   public DatabaseUsers(Database db)
   {
      super(db);
   }

   public boolean isUserInDatabase(String email) throws SQLException
   {
      return this.getUserId(email) > 0;
   }

   public int getUserId(String email) throws SQLException
   {
      int userId = 0;
      PreparedStatement statement = this.database.connection.prepareStatement(
         "SELECT userid FROM users WHERE email = ?");
      this.database.addStatement(statement);
      statement.setString(1, email);
      this.database.results = statement.executeQuery();
      if(this.database.results.next())
         userId = this.database.results.getInt("userid");

      this.database.results.close();
      return userId;
   }

   public void getUserProfile(int userId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT firstname, lastname, email, groupid FROM users WHERE userid = " +
         userId);
   }

   public boolean getUserProfile(int userId, UserProfileInfo info) throws SQLException
   {
      this.getUserProfile(userId);
      boolean exists = this.database.results.next();
      if(exists)
      {
         info.setProfile(this.database.results);
         info.setGroupId(this.database.results.getInt("groupid"));
      }

      this.database.results.close();
      return exists;
   }

   public void getUserInfo(int userId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT groupid, company, phone, address, country FROM users WHERE userId = " +
         userId);
   }

   public boolean getUserInfo(int userId, UserProfileInfo info) throws SQLException
   {
      this.getUserInfo(userId);
      boolean exists = this.database.results.next();
      if(exists)
      {
         info.setInfo(this.database.results);
         info.setGroupId(this.database.results.getInt("groupid"));
      }

      this.database.results.close();
      return exists;
   }

   public void getUserProfileInfo(int userId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT firstname, lastname, email, groupid, company, phone, address, country " +
         "FROM users WHERE userid = " + userId);
   }

   public boolean getUserProfileInfo(int userId, UserProfileInfo info)
      throws SQLException
   {
      this.getUserProfileInfo(userId);
      boolean exists = this.database.results.next();
      if(exists)
         info.setProfileInfo(this.database.results);

      this.database.results.close();
      return exists;
   }

   public void getUserDistributor(int groupId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT userid, firstname, lastname FROM users WHERE userid IN " +
         "(SELECT userid FROM distributors WHERE reseller = 0 AND groupid = " + groupId + ")");
   }

   public boolean getUserDistributor(int groupId, UserProfileInfo info)
      throws SQLException
   {
      this.getUserDistributor(groupId);
      boolean exists = this.database.results.next();
      if(exists)
         info.setDistributorInfo(this.database.results);

      this.database.results.close();
      return exists;
   }

   public void getUserReseller(int groupId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT userid, firstname, lastname FROM users WHERE userid IN " +
         "(SELECT userid FROM distributors WHERE reseller = 1 AND groupid = " + groupId + ")");
   }

   public boolean getUserReseller(int groupId, UserProfileInfo info)
      throws SQLException
   {
      this.getUserReseller(groupId);
      boolean exists = this.database.results.next();
      if(exists)
         info.setResellerInfo(this.database.results);

      this.database.results.close();
      return exists;
   }

   public String getUserEmail(int userId) throws SQLException
   {
      String email = null;
      this.getUserProfile(userId);
      if(this.database.results.next())
         email = this.database.results.getString("email");

      this.database.results.close();
      return email;
   }

   public int getUserGroup(int userId) throws SQLException
   {
      int groupId = 0;
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT groupid FROM users WHERE userid = " + userId);
      if(this.database.results.next())
         groupId = this.database.results.getInt("groupid");

      this.database.results.close();
      return groupId;
   }

   public String getDistributorEmail(int groupId) throws SQLException
   {
      String email = null;
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT email FROM users WHERE userid IN " +
         "(SELECT userid FROM distributors WHERE groupid = " + groupId + ")");
      if(this.database.results.next())
         email = this.database.results.getString("email");

      this.database.results.close();
      return email;
   }

   public boolean isContactInDatabase(String email) throws SQLException
   {
      return this.getContactId(email) > 0;
   }

   public int getContactId(String email) throws SQLException
   {
      int contactId = 0;
      PreparedStatement statement = this.database.connection.prepareStatement(
         "SELECT contactid FROM contacts WHERE email = ?");
      this.database.addStatement(statement);
      statement.setString(1, email);
      this.database.results = statement.executeQuery();
      if(this.database.results.next())
         contactId = this.database.results.getInt("contactid");

      this.database.results.close();
      return contactId;
   }

   public boolean isContactAccessible(int contactId, int userId)
      throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT universityid FROM universities WHERE (adminid = " + userId +
         " AND techid = " + contactId + ") OR (adminid = " + contactId +
         " AND techid = " + userId + ")");
      boolean accessible = this.database.results.next();
      this.database.results.close();
      return accessible;
   }

   public void getContactInfo(int contactId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT lastname, firstname, email, phone, address, country " +
         "FROM contacts WHERE contactid = " + contactId);
   }

   public boolean getContactInfo(int contactId, UserProfileInfo info) throws SQLException
   {
      this.getContactInfo(contactId);
      boolean exists = this.database.results.next();
      if(exists)
         info.setContactInfo(this.database.results);

      this.database.results.close();
      return exists;
   }

   public void getUserRoles(UserInfo user) throws SQLException
   {
      AcctLogger.get().info("User Roles id: " + user.getUserId());
      // Get the user roles and add them to the info object.
      if(this.isAdministrator(user.getUserId()))
      {
         AcctLogger.get().info("Admin role");
         user.addRole(UserInfo.AdminRoleName);
      }

      if(this.isUniversityUser(user.getUserId()))
         user.addRole(UserInfo.UniversityRoleName);

      if(this.isDistributor(user.getUserId()))
         user.addRole(UserInfo.DistributorRoleName);

      if(this.isReseller(user.getUserId()))
         user.addRole(UserInfo.ResellerRoleName);
   }

   public boolean isAdministrator(int userId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT userid FROM siteadmins WHERE userid = " + userId);
      boolean admin = this.database.results.next();
      this.database.results.close();
      return admin;
   }

   public boolean isDistributor(int userId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT userid FROM distributors WHERE reseller = 0 AND userid = " + userId);
      boolean distributor = this.database.results.next();
      this.database.results.close();
      return distributor;
   }

   public boolean isDistributor(int userId, int distributorId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT userid FROM users WHERE userid = " + userId +
         " AND groupid IN (SELECT groupid FROM distributors WHERE reseller = 0 AND userid = " +
         distributorId + ")");
      boolean distributor = this.database.results.next();
      this.database.results.close();
      return distributor;
   }

   public boolean isReseller(int userId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT userid FROM distributors WHERE reseller = 1 AND userid = " + userId);
      boolean reseller = this.database.results.next();
      this.database.results.close();
      return reseller;
   }

   public boolean isReseller(int userId, int resellerId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT userid FROM users WHERE userid = " + userId +
         " AND groupid IN (SELECT groupid FROM distributors WHERE reseller = 1 AND userid = " +
         resellerId + ")");
      boolean reseller = this.database.results.next();
      this.database.results.close();
      return reseller;
   }

   public int getDistibutorGroup(int userId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT groupid FROM distributors WHERE reseller = 0 AND userid = " + userId);
      int groupId = -1;
      if(this.database.results.next())
         groupId = this.database.results.getInt("groupid");

      this.database.results.close();
      return groupId;
   }

   public int getResellerGroup(int userId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT groupid FROM distributors WHERE reseller = 1 AND userid = " + userId);
      int groupId = -1;
      if(this.database.results.next())
         groupId = this.database.results.getInt("groupid");

      this.database.results.close();
      return groupId;
   }

   public boolean isUniversityUser(int userId) throws SQLException
   {
      return this.getUniversityId(userId) > 0;
   }

   public int getUniversityId(int userId) throws SQLException
   {
      int universityId = 0;
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT universityid FROM universities WHERE adminid = " + userId +
         " OR techid = " + userId);
      if(this.database.results.next())
         universityId = this.database.results.getInt("universityid");

      this.database.results.close();
      return universityId;
   }

   public void removeUnusedContact(int contactId) throws SQLException
   {
      if(!this.isUniversityUser(contactId))
      {
         this.database.statement.executeUpdate(
            "DELETE FROM contacts WHERE contactid = " + contactId);
      }
   }
}

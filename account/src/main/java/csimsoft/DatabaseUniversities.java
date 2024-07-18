/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DatabaseUniversities extends DatabaseMethods
{
   public static final int TechContact = 0x01;
   public static final int AdminContact = 0x02;

   public DatabaseUniversities()
   {
      super();
   }

   public DatabaseUniversities(Database db)
   {
      super(db);
   }

   public void getUniversityInfo(int universityId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT university, fax, adminid, techid FROM universities " +
         "WHERE universityid = " + universityId);
   }

   public boolean getUniversityInfo(int universityId, UniversityInfo info)
      throws SQLException
   {
      this.getUniversityInfo(universityId);
      boolean exists = this.database.results.next();
      if(exists)
      {
         info.setInfo(this.database.results);
         info.setContacts(this.database.results);
      }

      this.database.results.close();
      return exists;
   }

   public String getUniversityName(int universityId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      String name = null;
      this.database.results = this.database.statement.executeQuery(
         "SELECT university FROM universities WHERE universityid = " +
         universityId);
      if(this.database.results.next())
         name = this.database.results.getString("university");

      this.database.results.close();
      return name;
   }

   public int getUniversityId(String university) throws SQLException
   {
      int universityId = 0;
      PreparedStatement statement = this.database.connection.prepareStatement(
         "SELECT universityid FROM universities WHERE university = ?");
      this.database.addStatement(statement);
      statement.setString(1, university);
      this.database.results = statement.executeQuery();
      if(this.database.results.next())
         universityId = this.database.results.getInt("universityid");

      this.database.results.close();
      return universityId;
   }

   public static int getContactType(String contact)
   {
      int contactType = 0;
      if("admin".equals(contact))
         contactType = DatabaseUniversities.AdminContact;
      else if("tech".equals(contact))
         contactType = DatabaseUniversities.TechContact;
      else if("both".equals(contact))
      {
         contactType = DatabaseUniversities.AdminContact |
            DatabaseUniversities.TechContact;
      }

      return contactType;
   }

   public void getContacts(int universityId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT adminid, techid FROM universities WHERE universityid = " +
         universityId);
   }

   public List<Integer> getContact(int universityId, int contactType) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      ArrayList<Integer> contacts = new ArrayList<Integer>();
      this.getContacts(universityId);
      if(this.database.results.next())
      {
         if((contactType & DatabaseUniversities.AdminContact) > 0)
         {
            int adminId = this.database.results.getInt("adminid");
            if(adminId > 0)
               contacts.add(new Integer(adminId));
         }

         if((contactType & DatabaseUniversities.TechContact) > 0)
         {
            int techId = this.database.results.getInt("techid");
            if(techId > 0)
               contacts.add(new Integer(techId));
         }
      }

      this.database.results.close();
      return contacts;
   }

   public List<Integer> setContact(int universityId, int userId, int contactType)
      throws SQLException
   {
      contactType = contactType & (DatabaseUniversities.AdminContact |
         DatabaseUniversities.TechContact);
      if(contactType == 0)
         throw new IllegalArgumentException("Invalid contact type.");

      // Get the previous contact(s).
      List<Integer> previous = this.getContact(universityId, contactType);

      // Set the new contact(s).
      String update = "UPDATE universities SET";
      if((contactType & DatabaseUniversities.AdminContact) > 0)
      {
         update += " adminid = " + userId;
         if((contactType & DatabaseUniversities.TechContact) > 0)
            update += ",";
      }

      if((contactType & DatabaseUniversities.TechContact) > 0)
         update += " techid = " + userId;

      update += " WHERE universityid = " + universityId;
      this.database.statement.executeUpdate(update);

      return previous;
   }

   public void removeContact(int contactId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.statement.executeUpdate(
         "UPDATE universities SET adminid = 0 WHERE adminid = " + contactId);
      this.database.statement.executeUpdate(
         "UPDATE universities SET techid = 0 WHERE techid = " + contactId);
   }
}

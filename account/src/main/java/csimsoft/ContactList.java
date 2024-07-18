/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class ContactList extends DatabasePageList
{

   public ContactList()
   {
      super();
   }

   @Override
   public String getPageBaseLink()
   {
      return "allcontacts.jsp?page=";
   }

   @Override
   public void setResults() throws SQLException
   {
      this.statement = this.connection.createStatement(
           ResultSet.TYPE_SCROLL_INSENSITIVE,
           ResultSet.CONCUR_READ_ONLY);
      this.results = this.statement.executeQuery(
         "SELECT contactid, lastname, firstname, email, phone " +
         "FROM contacts ORDER BY lastname, firstname, email");
   }

   public UserProfileInfo getNext() throws SQLException
   {
      UserProfileInfo info = new UserProfileInfo();
      info.setId(this.results.getInt("contactid"));
      info.setProfile(this.results);
      info.setPhone(this.results.getString("phone"));
      return info;
   }
}

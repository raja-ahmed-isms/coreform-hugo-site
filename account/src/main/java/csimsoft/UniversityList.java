/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class UniversityList extends DatabasePageList
{

   public UniversityList()
   {
      super();
   }

   @Override
   public String getPageBaseLink()
   {
      return "universities.jsp?page=";
   }

   @Override
   public void setResults() throws SQLException
   {
      this.statement = this.connection.createStatement(
         ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      this.results = this.statement.executeQuery(
         "SELECT universityid, university, fax FROM universities " +
         "ORDER BY university");
   }

   public UniversityInfo getNext() throws SQLException
   {
      UniversityInfo info = new UniversityInfo();
      info.setId(this.results.getInt("universityid"));
      info.setInfo(this.results);
      return info;
   }
}

/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DistributorList extends DatabasePageList
{
   private String ExtraParams = null;

   public DistributorList()
   {
      super();
   }

   public String getExtraParameters()
   {
      return this.ExtraParams;
   }

   public void setExtraParameters(String extra)
   {
      this.ExtraParams = extra;
      if(extra != null && !extra.isEmpty() && !extra.startsWith("?"))
         this.ExtraParams = "?" + extra;
   }

   @Override
   public String getPageBaseLink()
   {
      String baseLink = "selectdist.jsp";
      if(this.ExtraParams == null || this.ExtraParams.isEmpty())
         baseLink += "?";
      else
         baseLink += this.ExtraParams + "&";

      baseLink += "page=";
      return baseLink;
   }

   @Override
   public void setResults() throws SQLException
   {
      this.statement = connection.createStatement(
         ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      this.results = this.statement.executeQuery(
         "SELECT distributors.groupid, users.userid, users.lastname, " +
         "users.firstname, users.email FROM users " +
         "INNER JOIN distributors ON (reseller = 0 AND users.userid = distributors.userid) " +
         "ORDER BY users.lastname, users.firstname, users.email");
   }

   public UserProfileInfo getNext() throws SQLException
   {
      UserProfileInfo info = new UserProfileInfo();
      info.setId(this.results.getInt("userid"));
      info.setGroupId(this.results.getInt("groupid"));
      info.setProfile(this.results);
      return info;
   }
}

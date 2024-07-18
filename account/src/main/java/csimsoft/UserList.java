/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserList extends DatabasePageList
{
   public static final int SimpleSearchList = -1;
   public static final int AllUserList = 0;
   public static final int AdminList = 1;
   public static final int DistributorList = 2;
   public static final int ResellerList = 3;

   private String Search = "";
   private String Role = null;
   private int ListType = UserList.AllUserList;

   public UserList()
   {
      super();
   }

   public int getListType()
   {
      return this.ListType;
   }

   public String getSearch()
   {
      return this.Search;
   }

   public String getRole()
   {
      return this.Role;
   }

   public void setSearch(String search, String role)
   {
      this.Search = search;
      this.Role = role;
      this.ListType = UserList.AllUserList;
      if(search != null && !search.isEmpty())
         this.ListType = UserList.SimpleSearchList;
      else if(UserInfo.AdminRoleName.equals(role))
         this.ListType = UserList.AdminList;
      else if(UserInfo.DistributorRoleName.equals(role))
         this.ListType = UserList.DistributorList;
      else if(UserInfo.ResellerRoleName.equals(role))
         this.ListType = UserList.ResellerList;

      if(this.Search == null)
         this.Search = "";
   }

   @Override
   public String getPageBaseLink()
   {
      String baseLink = "userlist.jsp?";
      try
      {
         if(this.ListType == -1)
            baseLink += "search=" + URLEncoder.encode(this.Search, "UTF-8") + "&";
         else if(this.ListType > 0)
            baseLink += "role=" + URLEncoder.encode(this.Role, "UTF-8") + "&";
      }
      catch(UnsupportedEncodingException uee)
      {
      }

      baseLink += "page=";
      return baseLink;
   }

   @Override
   public void setResults() throws SQLException
   {
      if(this.ListType == UserList.SimpleSearchList)
      {
         PreparedStatement prepared = connection.prepareStatement(
            "SELECT userid, lastname, firstname, email FROM users " +
            "WHERE firstname LIKE ? OR lastname LIKE ? OR email LIKE ? " +
            "ORDER BY lastname, firstname, email",
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
         this.statements.add(prepared);
         String search = "%" + this.Search + "%";
         prepared.setString(1, search);
         prepared.setString(2, search);
         prepared.setString(3, search);
         this.results = prepared.executeQuery();
      }
      else
      {
         this.statement = connection.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
         String condition = "";
         if(UserInfo.AdminRoleName.equals(this.Role))
            condition = "WHERE userid IN (SELECT userid FROM siteadmins) ";
         else if(UserInfo.DistributorRoleName.equals(this.Role))
            condition = "WHERE userid IN (SELECT userid FROM distributors WHERE reseller = 0) ";
         else if(UserInfo.ResellerRoleName.equals(this.Role))
            condition = "WHERE userid IN (SELECT userid FROM distributors WHERE reseller = 1) ";

         this.results = this.statement.executeQuery(
            "SELECT userid, lastname, firstname, email FROM users " + condition +
            "ORDER BY lastname, firstname, email");
      }
   }

   public UserProfileInfo getNext() throws SQLException
   {
      UserProfileInfo info = new UserProfileInfo();
      info.setId(this.results.getInt("userid"));
      info.setProfile(this.results);
      return info;
   }
}

/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;


public class UserSearchList extends DatabasePageList
{
   private UserSearch Search = new UserSearch();
   private String Query = null;
   private int UserId = 0;
   private boolean UserValid = true;

   public UserSearchList()
   {
      super();
   }

   public void setDistributorId(int userId)
   {
      this.UserId = userId;
   }

   public String getSearch()
   {
      return this.Query;
   }

   public String getSqlSearch()
   {
      return this.Search.getSearch();
   }

   public boolean setSearch(String search)
   {
      this.Query = search;
      return this.Search.setSearch(search);
   }

   public boolean isDistributorValid()
   {
      return this.UserValid;
   }

   @Override
   public String getPageBaseLink()
   {
      String baseLink = "usersearch.jsp?";
      try
      {
         baseLink += "search=" + URLEncoder.encode(this.Query, "UTF-8") + "&";
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
      if(this.UserId > 0)
      {
         // Get the distributor's group id from the database.
         DatabaseUsers users = new DatabaseUsers(this);
         int groupId = users.getDistibutorGroup(this.UserId);

         // Make sure the distributor can run the query.
         this.UserValid =
            UserSearch.isDistributorValid(this.Query.toLowerCase(), groupId);
      }
      else
         this.UserValid = true;

      if(this.UserValid)
         this.Search.executeQuery(this);
   }

   public UserProfileInfo getNext() throws SQLException
   {
      UserProfileInfo info = new UserProfileInfo();
      info.setId(this.results.getInt("userid"));
      info.setProfile(this.results);
      return info;
   }
}

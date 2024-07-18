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


public class DistributorUsers extends DatabasePageList
{
   private int UserId = 0;
   private String Search = "";
   private UserProfileInfo Info = new UserProfileInfo();
   private int FolderId = 0;
   private boolean IncludeId = false;

   public DistributorUsers()
   {
      super();
   }

   public String getSearch()
   {
      return this.Search;
   }

   public void setSearch(String search)
   {
      this.Search = search;
      if(this.Search == null)
         this.Search = "";
   }

   public int getId()
   {
      return this.UserId;
   }

   public void setId(int id, boolean includeInLink)
   {
      this.UserId = id;
      this.IncludeId = includeInLink;
   }

   public UserProfileInfo getUserInfo()
   {
      return this.Info;
   }

   public int getFolder()
   {
      return this.FolderId;
   }

   @Override
   public String getPageBaseLink()
   {
      String baseLink = "distusers.jsp?";
      if(this.IncludeId)
         baseLink += "user=" + this.UserId + "&";

      try
      {
         if(this.Search != null && !this.Search.isEmpty())
            baseLink += "search=" + URLEncoder.encode(this.Search, "UTF-8") + "&";
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
      DatabaseUsers users = new DatabaseUsers(this);
      DatabaseProducts products = new DatabaseProducts(this);
      if(this.Search == null || this.Search.isEmpty())
      {
         this.statement = connection.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
         if(this.IncludeId)
            users.getUserProfile(this.UserId, this.Info);

         this.FolderId = products.getDistributorFolder(this.UserId);

         this.results = this.statement.executeQuery(
            "SELECT userid, lastname, firstname, email FROM users " +
            "WHERE groupid IN (SELECT groupid FROM distributors WHERE reseller = 0 AND userid = " +
            this.UserId + ") ORDER BY lastname, firstname, email");
      }
      else
      {
         if(this.IncludeId)
            users.getUserProfile(this.UserId, this.Info);
         
         this.FolderId = products.getDistributorFolder(this.UserId);

         PreparedStatement prepared = connection.prepareStatement(
            "SELECT userid, lastname, firstname, email FROM users " +
            "WHERE (firstname LIKE ? OR lastname LIKE ? OR email LIKE ?) " +
            "AND groupid IN (SELECT groupid FROM distributors WHERE reseller = 0 AND userid = ?) " +
            "ORDER BY lastname, firstname, email",
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
         this.statements.add(prepared);
         String search = "%" + this.Search + "%";
         prepared.setString(1, search);
         prepared.setString(2, search);
         prepared.setString(3, search);
         prepared.setInt(4, this.UserId);
         this.results = prepared.executeQuery();
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

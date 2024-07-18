/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.ResultSet;
import java.sql.SQLException;


public class UniversityInfo
{
   private int UniversityId = 0;
   private String University = "";
   private String Fax = "";
   private int AdminId = 0;
   private int TechId = 0;

   public UniversityInfo()
   {
   }

   public UniversityInfo(int universityId)
   {
      this.UniversityId = universityId;
   }

   public int getId()
   {
      return this.UniversityId;
   }

   public void setId(int id)
   {
      this.UniversityId = id;
   }

   public String getName()
   {
      return this.University;
   }

   public String getJsName()
   {
      return this.University.replace("'", "\\'");
   }

   public void setName(String name)
   {
      this.University = name == null ? "" : name;
   }

   public String getFax()
   {
      return this.Fax;
   }

   public void setFax(String fax)
   {
      this.Fax = fax == null ? "" : fax;
   }

   public int getAdminId()
   {
      return this.AdminId;
   }

   public void setAdminId(int id)
   {
      this.AdminId = id;
   }

   public int getTechId()
   {
      return this.TechId;
   }

   public void setTechId(int id)
   {
      this.TechId = id;
   }

   public int getContactId(int index)
   {
      if(index == 0)
         return this.AdminId;
      else if(index == 1)
         return this.TechId;

      return 0;
   }

   public void setInfo(ResultSet results) throws SQLException
   {
      setName(results.getString("university"));
      setFax(results.getString("fax"));
   }

   public void setContacts(ResultSet results) throws SQLException
   {
      setAdminId(results.getInt("adminid"));
      setTechId(results.getInt("techid"));
   }
}

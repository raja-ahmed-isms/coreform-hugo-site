/**
 * @(#)UserProfileInfo.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/5/11
 */

package csimsoft;

import java.sql.ResultSet;
import java.sql.SQLException;


public class UserProfileInfo
{
   private int UserId = 0;
   private int GroupId = 0;
   private String FirstName = "";
   private String LastName = "";
   private String Email = "";
   private String Company = "";
   private String Phone = "";
   private String Address = "";
   private String Country = "";
   private String Industry = "";
   private String EduType= "";
   private String PlannedUse = "";
   private Boolean BlogAllowed = false;

   public UserProfileInfo()
   {
   }

   public int getId()
   {
      return this.UserId;
   }

   public void setId(int id)
   {
      this.UserId = id;
   }

   public int getGroupId()
   {
      return this.GroupId;
   }

   public void setGroupId(int id)
   {
      this.GroupId = id;
   }

   public String getFirstName()
   {
      return this.FirstName;
   }

   public void setFirstName(String firstName)
   {
      this.FirstName = firstName == null ? "" : firstName;
   }

   public String getLastName()
   {
      return this.LastName;
   }

   public void setLastName(String lastName)
   {
      this.LastName = lastName == null ? "" : lastName;
   }

   public String getFullName()
   {
      return this.FirstName + " " + this.LastName;
   }

   public String getLastFirstName()
   {
      return this.LastName + ", " + this.FirstName;
   }

   public String getEmail()
   {
      return this.Email;
   }

   public String getJsEmail()
   {
      return this.Email.replace("'", "\\'");
   }

   public void setEmail(String email)
   {
      this.Email = email == null ? "" : email;
   }

   public String getCompany()
   {
      return this.Company;
   }

   public void setCompany(String company)
   {
      this.Company = company == null ? "" : company;
   }

   public String getPhone()
   {
      return this.Phone;
   }

   public void setPhone(String phone)
   {
      this.Phone = phone == null ? "" : phone;
   }

   public String getAddress()
   {
      return this.Address;
   }

   public String getHtmlAddress()
   {
      return this.Address.replace("\n", "<br />");
   }

   public void setAddress(String address)
   {
      this.Address = address == null ? "" : address;
   }

   public String getCountry()
   {
      return this.Country;
   }

   public void setCountry(String country)
   {
      this.Country = country == null ? "" : country;
   }

   public String getIndustry()
   {
      return this.Industry;
   }

   public void setIndustry(String industry)
   {
      this.Industry = industry;
   }

   public String getEduType()
   {
      return this.EduType;
   }

   public void setEduType(String eduType)
   {
      this.EduType = eduType;
   }

   public String getPlannedUse()
   {
      return this.PlannedUse;
   }

   public void setPlannedUse(String plannedUse)
   {
      this.PlannedUse = plannedUse;
   }

   public Boolean getBlogAllowed()
   {
      return this.BlogAllowed;
   }

   public void setBlogAllowed(Boolean blogAllowed)
   {
      this.BlogAllowed = blogAllowed;
   }

   public void setProfile(ResultSet results) throws SQLException
   {
      this.setFirstName(results.getString("firstname"));
      this.setLastName(results.getString("lastname"));
      this.setEmail(results.getString("email"));
   }

   public void setInfo(ResultSet results) throws SQLException
   {
      this.setCompany(results.getString("company"));
      this.setPhone(results.getString("phone"));
      this.setAddress(results.getString("address"));
      this.setCountry(results.getString("country"));
   }

   public void setProfileInfo(ResultSet results) throws SQLException
   {
      this.setFirstName(results.getString("firstname"));
      this.setLastName(results.getString("lastname"));
      this.setEmail(results.getString("email"));
      this.setGroupId(results.getInt("groupid"));
      this.setCompany(results.getString("company"));
      this.setPhone(results.getString("phone"));
      this.setAddress(results.getString("address"));
      this.setCountry(results.getString("country"));
   }

   public void setContactInfo(ResultSet results) throws SQLException
   {
      this.setFirstName(results.getString("firstname"));
      this.setLastName(results.getString("lastname"));
      this.setEmail(results.getString("email"));
      this.setPhone(results.getString("phone"));
      this.setAddress(results.getString("address"));
      this.setCountry(results.getString("country"));
   }

   public void setDistributorInfo(ResultSet results) throws SQLException
   {
      this.setId(results.getInt("userid"));
      this.setFirstName(results.getString("firstname"));
      this.setLastName(results.getString("lastname"));
   }
   public void setResellerInfo(ResultSet results) throws SQLException
   {
      this.setId(results.getInt("userid"));
      this.setFirstName(results.getString("firstname"));
      this.setLastName(results.getString("lastname"));
   }
}

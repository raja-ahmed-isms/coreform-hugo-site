/**
 * @(#)UserInfo.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2010/6/17
 */

package csimsoft;

import java.util.ArrayList;
import java.util.Iterator;


public class UserInfo
{
   public static final String AdminRoleName = "Admin";
   public static final String DistributorRoleName = "Distributor";
   public static final String ResellerRoleName = "Reseller";
   public static final String UniversityRoleName = "University";

   private int UserId = 0;
   private ArrayList<String> Roles = new ArrayList<String>();

   public UserInfo()
   {
   }

   public UserInfo(int userId)
   {
      this.UserId = userId;
   }

   public int getUserId()
   {
      return this.UserId;
   }

   public void setUserId(int userId)
   {
      this.UserId = userId;
   }

   public int getNumberOfRoles()
   {
      return this.Roles.size();
   }

   public String getRolesString()
   {
      String roles = null;
      Iterator<String> iter = this.Roles.iterator();
      if(iter.hasNext())
      {
         roles = iter.next();
         while(iter.hasNext())
            roles += ", " + iter.next();
      }

      return roles;
   }

   public boolean hasRole(String role)
   {
      return this.Roles.contains(role);
   }

   public void addRole(String role)
   {
      if(!this.Roles.contains(role))
         this.Roles.add(role);
   }

   public void removeRole(String role)
   {
      this.Roles.remove(role);
   }

   public boolean isAdministrator()
   {
      return this.Roles.contains(UserInfo.AdminRoleName);
   }

   public boolean isDistributor()
   {
      return this.Roles.contains(UserInfo.DistributorRoleName);
   }

   public boolean isReseller()
   {
      return this.Roles.contains(UserInfo.ResellerRoleName);
   }

   public boolean isUniversityUser()
   {
      return this.Roles.contains(UserInfo.UniversityRoleName);
   }
}

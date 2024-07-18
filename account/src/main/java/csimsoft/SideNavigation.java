/**
 * @(#)SideNavigation.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/12/7
 */

package csimsoft;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.sql.SQLException;
import javax.naming.NamingException;


public class SideNavigation
{
   private String ContextPath = null;
   private ArrayList<SideNavigationItem> Items = new ArrayList<SideNavigationItem>();

   public SideNavigation()
   {
   }

   public SideNavigation(String contextPath)
   {
      this.ContextPath = contextPath;
   }

   public void addItem(String link, String label)
   {
      this.addItem(link, label, null);
   }

   public void addItem(String link, String label, String tooltip)
   {
      this.Items.add(new SideNavigationItem(link, label, tooltip));
   }

   public String buildMenu(String currentLink)
   {
      if(this.Items.isEmpty())
         return "";

      String menu = "<ul class=\"NavSide\">\n";
      Iterator<SideNavigationItem> iter = this.Items.iterator();
      while(iter.hasNext())
      {
         SideNavigationItem item = iter.next();
         if(item.getLink() == null && item.getLabel() == null)
            continue;

         menu += "  <li class=\"NavSidei\"><a";
         if(item.isLinkCurrent(currentLink))
         {
            menu +=
               " style=\"text-decoration:none; font-weight: bold; color: #1e283c\"";
         }
         else
         {
            menu += " href=\"" + this.ContextPath + item.getLink() + "\"";
            if(item.getTooltip() != null)
               menu += " title=\"" + item.getTooltip() + "\"";
         }

         menu += ">" + item.getLabel() + "</a></li>\n";
      }

      menu += "</ul>\n";
      return menu;
   }

   public class CubitLearnDetails
   {
      public boolean hasLearnLicense;
      public int numDaysRemaining;
      public int learnLicenseId;

      CubitLearnDetails(boolean hasLicense, int numDaysRemaining, int learnLicenseId)
      {
         this.hasLearnLicense = hasLicense;
         this.numDaysRemaining = numDaysRemaining;
         this.learnLicenseId = learnLicenseId;
      }
   }

   public CubitLearnDetails hasCubitLearnLicense(int userId)
   {
		DatabaseUsers users = new DatabaseUsers();
      CubitLearnDetails details = new CubitLearnDetails(false, 0, 0);
      try {
         users.database.connect();
         users.database.statement = users.database.connection.createStatement();
         ArrayList<Integer> licenses = new ArrayList<Integer>();
         ArrayList<Integer> userlicenses = new ArrayList<Integer>();
         ArrayList<java.util.Calendar> expirations = new ArrayList<java.util.Calendar>();
         // Make sure the license id is valid.
         users.database.results = users.database.statement.executeQuery(
            "SELECT userlicenseid, licenseid, expiration FROM licenses WHERE userid = " + userId );
         while(users.database.results.next())
         {
            licenses.add( users.database.results.getInt("licenseid"));
            userlicenses.add( users.database.results.getInt("userlicenseid"));
            java.util.Calendar date = new java.util.GregorianCalendar();
            java.sql.Date sqlDate = users.database.results.getDate("expiration");
            if(sqlDate != null)
            {
               date.setTime(sqlDate);
               expirations.add( date );
            }
            else
            {
               expirations.add( null );
            }
         }
         users.database.results.close();
         for(int i = 0; i < licenses.size(); i++)
         {
            String licensename = "";
            //Make sure it's a Cubit Learn license
            users.database.results = users.database.statement.executeQuery(
               "SELECT licensename FROM licenseproducts WHERE licenseid = " + licenses.get(i));
            if(users.database.results.next())
            {
               licensename = users.database.results.getString("licensename");
            }
            users.database.results.close();
            if(licensename.contains("Cubit Learn")) 
            {
               details.hasLearnLicense = true;
               details.learnLicenseId = userlicenses.get(i);
               java.util.Calendar expiration = expirations.get(i);
               if(expiration != null) {
                  java.util.Calendar today = java.util.Calendar.getInstance();
                  long millidiff = expiration.getTimeInMillis() - today.getTimeInMillis();
                  details.numDaysRemaining = (int)TimeUnit.MILLISECONDS.toDays(millidiff);
               }
               break;
            }
         }
      }
      catch(SQLException sqle)
      {
         // Do nothing
      }
      catch(NamingException ne)
      {
         // Do nothing
      }
      users.database.cleanup();
      return details;
   }
}
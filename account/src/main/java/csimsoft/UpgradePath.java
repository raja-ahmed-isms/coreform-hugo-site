/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class UpgradePath
{
   private String UpgradeName = null;
   private int UpgradeId = 0;
   private ArrayList<UpgradePathItem> Items = new ArrayList<UpgradePathItem>();

   public UpgradePath()
   {
   }

   public UpgradePath(int upgradeId)
   {
      this.UpgradeId = upgradeId;
   }

   public UpgradePath(int upgradeId, String name)
   {
      this.UpgradeName = name;
      this.UpgradeId = upgradeId;
   }

   public int getUpgradeId()
   {
      return this.UpgradeId;
   }

   public void setUpgradeId(int id)
   {
      this.UpgradeId = id;
   }

   public String getUpgradeName()
   {
      return this.UpgradeName;
   }

   public String getJsUpgradeName()
   {
      return this.UpgradeName.replace("'", "\\'");
   }

   public void setUpgradeName(String name)
   {
      this.UpgradeName = name;
   }

   public int getNextOrder()
   {
      int maximum = 0;
      if(!this.Items.isEmpty())
         maximum = this.Items.get(0).getUpgradeOrder() + 1;

      return maximum;
   }

   public int getItemCount()
   {
      return this.Items.size();
   }

   public UpgradePathItem getItem(int index)
   {
      return this.Items.get(index);
   }

   public void addItem(UpgradePathItem item)
   {
      this.Items.add(item);
   }

   public void clearItems()
   {
      this.Items.clear();
   }

   public void setItems(DatabaseLicenses licenses, LicenseNames names)
      throws SQLException
   {
      this.clearItems();
      licenses.getUpgradePathItems(this.UpgradeId);
      while(licenses.database.results.next())
         this.addItem(this.createItem(licenses.database.results, names));

      licenses.database.results.close();
   }

   public UpgradePathItem createItem(ResultSet results, LicenseNames names)
      throws SQLException
   {
      UpgradePathItem item = new UpgradePathItem();
      item.setLicenseId(results.getInt("licenseid"));
      item.setLicenseName(names.find(item.getLicenseId()));
      item.setUpgradeOrder(results.getInt("upgradeorder"));
      return item;
   }
}

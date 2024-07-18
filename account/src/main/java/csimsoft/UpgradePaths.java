/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;


public class UpgradePaths
{
   private ArrayList<UpgradePath> Paths = new ArrayList<UpgradePath>();

   public UpgradePaths()
   {
   }

   public boolean isEmpty()
   {
      return this.Paths.isEmpty();
   }

   public int getPathCount()
   {
      return this.Paths.size();
   }

   public UpgradePath getPath(int index)
   {
      return this.Paths.get(index);
   }

   public void addPath(UpgradePath path)
   {
      this.Paths.add(path);
   }

   public void clearPaths()
   {
      this.Paths.clear();
   }

   public void setPaths(DatabaseLicenses licenses) throws SQLException
   {
      this.clearPaths();

      // Get the list of upgrade paths from the database.
      UpgradePath path = null;
      TreeMap<Integer, UpgradePath> pathMap = new TreeMap<Integer, UpgradePath>();
      licenses.getUpgradePaths();
      while(licenses.database.results.next())
      {
         path = new UpgradePath(licenses.database.results.getInt("upgradeid"));
         path.setUpgradeName(licenses.database.results.getString("upgradename"));
         this.addPath(path);
         pathMap.put(new Integer(path.getUpgradeId()), path);
      }

      licenses.database.results.close();

      // Get the product license names from the database.
      LicenseNames names = licenses.getProductLicenseMap();

      // Get all the upgrade path items from the database.
      licenses.database.results = licenses.database.statement.executeQuery(
         "SELECT upgradeid, licenseid, upgradeorder FROM upgradeitems " +
         "ORDER BY upgradeorder DESC");
      while(licenses.database.results.next())
      {
         int upgradeId = licenses.database.results.getInt("upgradeid");
         if(path == null || path.getUpgradeId() != upgradeId)
            path = pathMap.get(new Integer(upgradeId));

         if(path == null)
            continue;

         path.addItem(path.createItem(licenses.database.results, names));
      }

      licenses.database.results.close();
   }
}

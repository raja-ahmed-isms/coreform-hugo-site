/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;


public class LicenseFeatures
{
   public static class Feature
   {
      public int Id = 0;
      public String Name = "";
      public String Key = "";
      public boolean Autoadd = false;

      public Feature(int featureId)
      {
         this.Id = featureId;
      }

      public String getJsName()
      {
         return this.Name.replace("'", "\\'");
      }

      public String getChecked()
      {
         return this.Autoadd ? "checked=\"checked\"" : "";
      }

      public void setName(String name)
      {
         this.Name = name == null ? "" : name;
      }

      public void setKey(String key)
      {
         this.Key = key == null ? "" : key;
      }
   }

   private ArrayList<LicenseFeatures.Feature> Features =
      new ArrayList<LicenseFeatures.Feature>();

   public LicenseFeatures()
   {
   }

   public boolean isEmpty()
   {
      return this.Features.isEmpty();
   }

   public int getFeatureCount()
   {
      return this.Features.size();
   }

   public LicenseFeatures.Feature getFeature(int index)
   {
      return this.Features.get(index);
   }

   public void clear()
   {
      this.Features.clear();
   }

   public void setFeatures(DatabaseLicenses licenses, int licenseId) throws SQLException
   {
      // Get the default features for the license.
      ArrayList<Integer> defaultFeatures = new ArrayList<Integer>();
      licenses.getDefaultFeatures(licenseId);
      while(licenses.database.results.next())
         defaultFeatures.add(licenses.database.results.getInt("featureid"));

      licenses.database.results.close();
      Collections.sort(defaultFeatures);

      // Get the feature names and keywords for the license.
      licenses.getLicenseFeatures(licenseId);
      while(licenses.database.results.next())
      {
         LicenseFeatures.Feature feature =
            new LicenseFeatures.Feature(licenses.database.results.getInt("featureid"));
         feature.setName(licenses.database.results.getString("featurename"));
         feature.setKey(licenses.database.results.getString("featurekey"));
         feature.Autoadd = Collections.binarySearch(defaultFeatures, feature.Id) >= 0;
         this.Features.add(feature);
      }

      licenses.database.results.close();
   }
}

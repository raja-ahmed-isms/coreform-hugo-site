/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.util.TreeMap;


public class LicenseNames
{
   private TreeMap<Integer, String> Names = new TreeMap<Integer, String>();

   public LicenseNames()
   {
   }

   public String find(int licenseId)
   {
      return this.Names.get(new Integer(licenseId));
   }

   public void add(int licenseId, String licenseName)
   {
      this.Names.put(new Integer(licenseId), licenseName);
   }
}

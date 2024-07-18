/**
 * @(#)ProcessNames.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/3/31
 */
package csimsoft;

import java.util.TreeMap;


public class ProcessNames
{
   private TreeMap<Integer, String> Names = new TreeMap<Integer, String>();

   public ProcessNames()
   {
   }

   public String find(int processId)
   {
      return this.Names.get(new Integer(processId));
   }

   public void add(int processId, String processName)
   {
      this.Names.put(new Integer(processId), processName);
   }
}
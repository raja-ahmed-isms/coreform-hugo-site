/**
 * @(#)ParameterList.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/3/1
 */

package csimsoft;

import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;


public class ParameterList
{
   private ArrayList<String> Parameters = new ArrayList<String>();

   public ParameterList()
   {
   }

   public boolean isEmpty()
   {
      return this.Parameters.isEmpty();
   }

   public int getParameterCount()
   {
      return this.Parameters.size();
   }

   public String getParameterName(int index)
   {
      return this.Parameters.get(index);
   }

   public boolean hasParameter(String name)
   {
      return this.Parameters.contains(name);
   }

   public void setParameters(HttpServletRequest request)
   {
      this.Parameters.clear();
      Enumeration params = request.getParameterNames();
      while(params.hasMoreElements())
      {
         try
         {
            this.Parameters.add((String)params.nextElement());
         }
         catch(ClassCastException cce)
         {
         }
         catch(NullPointerException npe)
         {
         }
      }
   }

   public void removeParameter(String name)
   {
      this.Parameters.remove(name);
   }
}
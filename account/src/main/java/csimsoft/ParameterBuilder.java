/**
 * @(#)ParameterBuilder.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/5/18
 */

package csimsoft;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;


public class ParameterBuilder
{
   private ArrayList<String> Parameters = new ArrayList<String>();

   public ParameterBuilder()
   {
   }

   public void clear()
   {
      this.Parameters.clear();
   }

   public boolean isEmpty()
   {
      return this.Parameters.isEmpty();
   }

   public void add(String parameter, String value)
   {
      try
      {
         if(value != null && !value.isEmpty())
            this.Parameters.add(parameter + "=" + URLEncoder.encode(value, "UTF-8"));
      }
      catch(Exception e)
      {
      }
   }

   public void add(String parameter, int value)
   {
      this.Parameters.add(parameter + "=" + value);
   }

   @Override
   public String toString()
   {
      if(this.Parameters.isEmpty())
         return "";

      return "?" + ParameterBuilder.join(this.Parameters, "&");
   }

   public static String join(Iterable<String> items, String delimiter)
   {
      StringBuilder builder = new StringBuilder();
      Iterator<String> iter = items.iterator();
      if(iter.hasNext())
         builder.append(iter.next());

      while(iter.hasNext())
         builder.append(delimiter).append(iter.next());

      return builder.toString();
   }
}
/**
 * @(#)SideNavigationItem.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/12/7
 */

package csimsoft;


public class SideNavigationItem
{
   private String Link = null;
   private String Label = null;
   private String Tooltip = null;

   public SideNavigationItem()
   {
   }

   public SideNavigationItem(String link, String label, String tooltip)
   {
      this.Link = link;
      this.Label = label;
      this.Tooltip = tooltip;
   }

   public String getLink()
   {
      return this.Link;
   }

   public void setLink(String link)
   {
      this.Link = link;
   }

   public boolean isLinkCurrent(String current)
   {
      if(current == null || current.isEmpty())
         return false;

      String[] tokens = this.Link.split("/");
      if(tokens.length == 0)
         return false;

      return current.equals(tokens[tokens.length - 1]);
   }

   public String getLabel()
   {
      return this.Label;
   }

   public void setLabel(String label)
   {
      this.Label = label;
   }

   public String getTooltip()
   {
      return this.Tooltip;
   }

   public void setTooltip(String tooltip)
   {
      this.Tooltip = tooltip;
   }
}
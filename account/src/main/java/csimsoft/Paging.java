/**
 * @(#)Paging.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/3/1
 */

package csimsoft;


public class Paging
{
   private String PageMenu = null;
   private int PageCount = 1;
   private int Page = 1;
   private int PageRows = 25;
   private int StartRow = 1;

   public Paging()
   {
   }

   public int getPageRows()
   {
      return this.PageRows;
   }

   public void setPageRows(int pageRows)
   {
      this.PageRows = pageRows;
   }

   public int getStartRow()
   {
      return this.StartRow;
   }

   public int getPage()
   {
      return this.Page;
   }

   public void setPage(int page)
   {
      this.Page = page;
   }

   public void setPage(String page)
   {
      this.Page = 1;
      try
      {
         this.Page = Integer.parseInt(page);
         if(this.Page < 1)
            this.Page = 1;
      }
      catch(Exception nfe)
      {
      }
   }

   public int getPages()
   {
      return this.PageCount;
   }

   public void setupPages(int totalRows)
   {
      this.PageCount = totalRows / this.PageRows;
      if(totalRows % this.PageRows > 0)
         this.PageCount++;

      if(this.PageCount > 1)
      {
         if(this.Page > this.PageCount)
            this.Page = this.PageCount;

         this.StartRow = ((this.Page - 1) * this.PageRows) + 1;
      }
      else
         this.StartRow = 1;
   }

   public String getMenu()
   {
      return this.PageMenu;
   }

   public void buildMenu(String baseUrl, int pageNumber, int numPages)
   {
      this.Page = pageNumber;
      this.PageCount = numPages;
      buildMenu(baseUrl);
   }

   public void buildMenu(String baseUrl)
   {
      // Calculate the starting and endinf page.
      int startPage = 1;
      if(this.Page > 5)
         startPage = this.Page - 4;

      int endPage = startPage + 9;
      if(endPage > this.PageCount)
      {
         startPage -= endPage - this.PageCount;
         endPage = this.PageCount;
         if(startPage < 1)
            startPage = 1;
      }

      // Build the paging list.
      this.PageMenu = "<ul class=\"page-menu\">\n";
      if(this.Page > 1)
      {
         this.PageMenu +=
            "  <li class=\"page-menuitem\"><a class=\"page-link\"\n" +
            "    href=\"" + baseUrl + (this.Page - 1) +
            "\"><img src=\"./images/page-previous.png\" /></a></li>\n";
      }

      for(int i = startPage; i <= endPage; i++)
      {
         this.PageMenu +=
            "  <li class=\"page-menuitem\"><a class=\"";
         if(i == this.Page)
            this.PageMenu += "page-linkcurrent";
         else
            this.PageMenu += "page-link";

         this.PageMenu +=
            "\"\n    href=\"" + baseUrl + i + "\">" + i + "</a></li>\n";
      }

      if(this.Page < this.PageCount)
      {
         this.PageMenu +=
            "  <li class=\"page-menuitem\"><a class=\"page-link\"\n" +
            "    href=\"" + baseUrl + (this.Page + 1) +
            "\"><img src=\"./images/page-next.png\" /></a></li>\n";
      }

      this.PageMenu += "</ul>\n";
   }
}
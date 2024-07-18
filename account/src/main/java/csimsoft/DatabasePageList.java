/**
 *
 * @author Mark
 */
package csimsoft;

import java.sql.SQLException;
import javax.naming.NamingException;


public abstract class DatabasePageList extends Database
{
   public Paging pages = new Paging();
   private int RowsShown = 0;

   public DatabasePageList()
   {
      super();
   }

   public boolean isEmpty()
   {
      return this.RowsShown == 0;
   }

   public abstract String getPageBaseLink();

   public abstract void setResults() throws SQLException;

   @Override
   public void connect() throws NamingException, SQLException
   {
      super.connect();

      // set up the results list.
      this.setResults();

      // Set up the paging menu.
      if(this.results != null && this.results.last())
      {
         this.pages.setupPages(this.results.getRow());
         if(this.pages.getPages() > 1)
            this.pages.buildMenu(this.getPageBaseLink());
      }
   }

   public boolean hasNext() throws SQLException
   {
      boolean more = false;
      if(this.RowsShown == 0)
         more = this.results.absolute(this.pages.getStartRow());
      else
         more = this.RowsShown < this.pages.getPageRows() && this.results.next();

      ++this.RowsShown;
      return more;
   }
}

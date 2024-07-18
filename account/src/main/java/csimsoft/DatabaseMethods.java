/**
 *
 * @author Mark Richardson
 */
package csimsoft;


public class DatabaseMethods
{
   public Database database = null;

   public DatabaseMethods()
   {
      this.database = new Database();
   }

   public DatabaseMethods(Database db)
   {
      this.database = db;
   }
}

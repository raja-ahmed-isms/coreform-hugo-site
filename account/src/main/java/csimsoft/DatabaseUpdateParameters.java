/**
 * @(#)DatabaseUpdateParameters.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/3/26
 */

package csimsoft;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class DatabaseUpdateParameters
{
   private ResultMessageAdapter Message = null;
   private Database database = null;
   private HttpSession Session = null;
   private HttpServletRequest Request = null;

   public DatabaseUpdateParameters()
   {
   }

   public ResultMessageAdapter getMessage()
   {
      return this.Message;
   }

   public void setMessage(ResultMessageAdapter message)
   {
      this.Message = message;
   }

   public Database getDatabase()
   {
      return this.database;
   }

   public void setDatabase(Database db)
   {
      this.database = db;
   }

   public HttpSession getSession()
   {
      return this.Session;
   }

   public void setSession(HttpSession session)
   {
      this.Session = session;
   }

   public HttpServletRequest getRequest()
   {
      return this.Request;
   }

   public void setRequest(HttpServletRequest request)
   {
      this.Request = request;
   }
}
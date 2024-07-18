/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DatabaseProcesses extends DatabaseMethods
{
   public DatabaseProcesses()
   {
      super();
   }

   public DatabaseProcesses(Database db)
   {
      super(db);
   }

   public boolean isProcessInDatabase(String processName) throws SQLException
   {
      return this.getProcessId(processName) > 0;
   }

   public int getProcessId(String processName) throws SQLException
   {
      PreparedStatement statement = this.database.connection.prepareStatement(
         "SELECT processid FROM orderprocess WHERE processname = ?");
      this.database.addStatement(statement);
      statement.setString(1, processName);

      int processId = 0;
      this.database.results = statement.executeQuery();
      if(this.database.results.next())
         processId = this.database.results.getInt("processid");

      this.database.results.close();
      return processId;
   }

   public String getProcessName(int processId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      String name = null;
      this.database.results = this.database.statement.executeQuery(
         "SELECT processname FROM orderprocess WHERE processid = " + processId);
      if(this.database.results.next())
         name = this.database.results.getString("processname");

      this.database.results.close();
      return name;
   }

   public void getOrderProcessList() throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      this.database.results = this.database.statement.executeQuery(
         "SELECT processid, processname, handlername FROM orderprocess " +
         "ORDER BY processname");
   }

   public ProcessNames getOrderProcessMap() throws SQLException
   {
      this.getOrderProcessList();
      ProcessNames names = new ProcessNames();
      while(this.database.results.next())
      {
         names.add(this.database.results.getInt("processid"),
            this.database.results.getString("processname"));
      }

      this.database.results.close();
      return names;
   }

   public OrderProcessHandler getProcessHandler(int processId) throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      String handlerName = null;
      this.database.results = this.database.statement.executeQuery(
         "SELECT handlername FROM orderprocess WHERE processid = " + processId);
      if(this.database.results.next())
         handlerName = this.database.results.getString("handlername");

      this.database.results.close();
      return DatabaseProcesses.getHandler(handlerName);
   }

   public static OrderProcessHandler getHandler(String name)
   {
      try
      {
         if(name != null && !name.isEmpty() && !name.equals("null"))
         {
            Class handler = Class.forName(name);
            return (OrderProcessHandler)handler.newInstance();
         }
      }
      catch(ClassNotFoundException cnfe)
      {
      }
      catch(InstantiationException ie)
      {
      }
      catch(IllegalAccessException iae)
      {
      }
      catch(ClassCastException cce)
      {
      }

      return null;
   }

   public List<OrderProcessHandler> getOrderHandlers() throws SQLException
   {
      if(this.database.statement == null)
         this.database.statement = this.database.connection.createStatement();

      ArrayList<OrderProcessHandler> handlers = new ArrayList<OrderProcessHandler>();
      this.database.results = this.database.statement.executeQuery(
         "SELECT handlername FROM orderprocess");
      while(this.database.results.next())
      {
         OrderProcessHandler handler = DatabaseProcesses.getHandler(
            this.database.results.getString("handlername"));
         if(handler != null)
            handlers.add(handler);
      }

      this.database.results.close();
      return handlers;
   }
}

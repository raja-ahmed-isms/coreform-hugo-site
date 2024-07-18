/**
 * @(#)UpdateDatabaseServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2009/4/27
 */
package csimsoft;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class UpdateDatabaseServlet extends HttpServlet
{

   public UpdateDatabaseServlet()
   {
   }

   private static class Message implements ResultMessageAdapter
   {
      public ResultMessage Report = null;

      public Message(ResultMessage message)
      {
         this.Report = message;
      }

      @Override
      public void setResult(int result)
      {
         this.Report.setResult(result);
         if(result == ResultMessage.Success)
         {
            this.Report.setType(ResultMessage.InfoMessage);
            this.Report.setMessage(
               "The database has been successfully updated.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to update the database.");
         }
         else if(result == ResultMessage.DoesNotExist)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to instantiate an update for the database.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to update the database.<br />" +
               "Please log in as an administrator to do so.");
         }
      }

      @Override
      public void addException(Exception e)
      {
         this.Report.appendToMessage("<br />" + e);
      }

      @Override
      public void addMessage(String extra)
      {
         this.Report.appendToMessage(extra);
      }
   }

   public static class UpdateName
   {
      public int Version = 0;
      public String Name = null;

      public UpdateName()
      {
      }

      public UpdateName(int version, String name)
      {
         this.Version = version;
         this.Name = name;
      }

      public int getVersion()
      {
         return this.Version;
      }

      public String getName()
      {
         return this.Name;
      }
   }

   public static class UpdateComparator implements java.util.Comparator<UpdateName>
   {
      public UpdateComparator()
      {
      }

      @Override
      public int compare(UpdateName name1, UpdateName name2)
      {
         if(name1 == null)
            return name2 == null ? 0 : -1;
         else if(name2 == null)
            return 1;

         if(name1.Version == name2.Version)
            return 0;
         else
            return name1.Version < name2.Version ? -1 : 1;
      }
   }

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
         throws IOException, ServletException
   {
      // Get the result message object for the session.
      HttpSession session = request.getSession();
      UpdateDatabaseServlet.Message message = new UpdateDatabaseServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to alter the database.
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session) || !user.isAdministrator())
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
         return;
      }

      Database database = new Database();
      try
      {
         database.connect();
         database.connection.setAutoCommit(false);
         database.statement = database.connection.createStatement();
         int version = 0;
         try
         {
            // See what the current version is.
            database.results = database.statement.executeQuery(
               "SELECT versionid FROM cssversion");
            if(database.results.next())
               version = database.results.getInt("versionid");

            database.results.close();
         }
         catch(SQLException sqle2)
         {
            try
            {
               version = this.initVersion(database);
               database.connection.commit();
            }
            catch(SQLException sqle3)
            {
               database.connection.rollback();
            }
         }

         if(version == 0)
         {
            message.setResult(ResultMessage.Failure);
            message.addMessage("<br />Unable to find the version number.");
         }
         else
         {
            int starting = version;

            // Build the list of updates.
            TreeSet<UpdateDatabaseServlet.UpdateName> allUpdates = this.buildUpdates();
            NavigableSet<UpdateDatabaseServlet.UpdateName> updates =
               allUpdates.tailSet(new UpdateDatabaseServlet.UpdateName(version, ""),
               false);

            DatabaseUpdateParameters params = new DatabaseUpdateParameters();
            params.setMessage(message);
            params.setDatabase(database);
            params.setSession(session);
            params.setRequest(request);

            // Apply the updates.
            Iterator<UpdateDatabaseServlet.UpdateName> iter = updates.iterator();
            while(iter.hasNext())
            {
               UpdateDatabaseServlet.UpdateName name = iter.next();
               DatabaseUpdate update = this.getUpdate(name.getName());
               if(update == null)
               {
                  message.setResult(ResultMessage.DoesNotExist);
                  message.addMessage("<br />Version: " + name.getVersion() +
                     " Class: " + name.getName());
                  break;
               }

               try
               {
                  update.updateDatabase(params);
               }
               catch(SQLException sqle2)
               {
                  message.setResult(ResultMessage.Failure);
                  message.addException(sqle2);
                  database.connection.rollback();
                  break;
               }

               if(message.Report.getResult() != ResultMessage.Success)
                  break;

               version = name.getVersion();
            }

            if(message.Report.getResult() == ResultMessage.NoResult)
               message.setResult(ResultMessage.Success);

            if(version > starting)
            {
               try
               {
                  // Save the new version number.
                  database.statement.executeUpdate(
                     "UPDATE cssversion SET versionid = " + version +
                     " WHERE versionid = " + starting);
                  database.connection.commit();
                  message.addMessage(
                     "<br />The database was upgraded from version " + starting +
                     " to version " + version + ".");
               }
               catch(SQLException sqle2)
               {
                  message.addMessage(
                     "<br />Unable to save the new version: " + version + ".");
                  message.addException(sqle2);
               }
            }
            else
            {
               message.addMessage(
                  "<br />The current database version is " + version + ".");
            }
         }
      }
      catch(SQLException sqle)
      {
         message.setResult(ResultMessage.ResourceError);
         message.addException(sqle);
      }
      catch(javax.naming.NamingException ne)
      {
         message.setResult(ResultMessage.ResourceError);
         message.addException(ne);
      }

      database.cleanup();

      // Redirect the response back to the account page.
      response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
   }

   private int initVersion(Database database) throws SQLException
   {
      database.statement.executeUpdate(
         "CREATE TABLE cssversion (" +
         "  versionid INT NOT NULL)");

      int version = 8;
      database.statement.executeUpdate(
         "INSERT INTO cssversion (versionid) VALUES (" + version + ")");
      return version;
   }

   private TreeSet<UpdateDatabaseServlet.UpdateName> buildUpdates()
   {
      TreeSet<UpdateDatabaseServlet.UpdateName> updates =
         new TreeSet<UpdateDatabaseServlet.UpdateName>(
         new UpdateDatabaseServlet.UpdateComparator());

      // Add the names of the update classes with their version numbers.
      updates.add(
         new UpdateDatabaseServlet.UpdateName(30, "csimsoft.dbupdates.Update30"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(29, "csimsoft.dbupdates.Update29"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(28, "csimsoft.dbupdates.Update28"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(27, "csimsoft.dbupdates.Update27"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(26, "csimsoft.dbupdates.Update26"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(25, "csimsoft.dbupdates.Update25"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(24, "csimsoft.dbupdates.Update24"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(23, "csimsoft.dbupdates.Update23"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(22, "csimsoft.dbupdates.Update22"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(21, "csimsoft.dbupdates.Update21"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(20, "csimsoft.dbupdates.Update20"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(19, "csimsoft.dbupdates.Update19"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(18, "csimsoft.dbupdates.Update18"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(17, "csimsoft.dbupdates.Update17"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(16, "csimsoft.dbupdates.Update16"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(15, "csimsoft.dbupdates.Update15"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(14, "csimsoft.dbupdates.Update14"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(13, "csimsoft.dbupdates.Update13"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(12, "csimsoft.dbupdates.Update12"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(11, "csimsoft.dbupdates.Update11"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(10, "csimsoft.dbupdates.Update10"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(9, "csimsoft.dbupdates.Update9"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(8, "csimsoft.dbupdates.Update8"));
      updates.add(
         new UpdateDatabaseServlet.UpdateName(7, "csimsoft.dbupdates.Update7"));

      return updates;
   }

   private DatabaseUpdate getUpdate(String name)
   {
      try
      {
         if(name != null && !name.isEmpty() && !name.equals("null"))
         {
            Class update = Class.forName(name);
            return (DatabaseUpdate)update.newInstance();
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
}
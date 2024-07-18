/**
 * @(#)Update3.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/1/25
 */

package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update3 implements csimsoft.DatabaseUpdate
{

   public Update3()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();


      // Add the order process count table.
      database.statement.executeUpdate(
         "CREATE TABLE orderprocesscount (processid INT NOT NULL)");

      // Add the process id column.
      database.statement.executeUpdate(
         "ALTER TABLE orderprocess ADD COLUMN processid INT");
      database.statement.executeUpdate(
         "ALTER TABLE processlicense ADD COLUMN processid INT");

      // Fill in the process id column.
      database.results = database.statement.executeQuery(
         "SELECT processname FROM orderprocess");

      java.util.TreeMap<String, Integer> processes =
         new java.util.TreeMap<String, Integer>();
      int nextId = 1;
      for( ; database.results.next(); nextId++)
         processes.put(database.results.getString("processname"), new Integer(nextId));

      database.results.close();

      java.util.Iterator<java.util.Map.Entry<String, Integer>> iter =
         processes.entrySet().iterator();
      while(iter.hasNext())
      {
         java.util.Map.Entry<String, Integer> process = iter.next();
         database.statement.executeUpdate(
            "UPDATE orderprocess SET processid = " +
            process.getValue().toString() + " WHERE processname = '" +
            process.getKey() + "'");
         database.statement.executeUpdate(
            "UPDATE processlicense SET processid = " +
            process.getValue().toString() + " WHERE processname = '" +
            process.getKey() + "'");
      }

      // Set the next process id in the count table.
      database.statement.executeUpdate(
         "INSERT INTO orderprocesscount (processid) VALUES (" + nextId + ")");

      // Change the primary key to the process id.
      database.statement.executeUpdate(
         "ALTER TABLE orderprocess MODIFY COLUMN processid INT NOT NULL");
      database.statement.executeUpdate(
         "ALTER TABLE processlicense MODIFY COLUMN processid INT NOT NULL");
      database.statement.executeUpdate(
         "ALTER TABLE processlicense DROP PRIMARY KEY");
      database.statement.executeUpdate(
         "ALTER TABLE orderprocess DROP PRIMARY KEY");
      database.statement.executeUpdate(
         "ALTER TABLE orderprocess ADD PRIMARY KEY (processid)");
      database.statement.executeUpdate(
         "ALTER TABLE processlicense ADD PRIMARY KEY (processid)");
      database.statement.executeUpdate(
         "ALTER TABLE processlicense ADD FOREIGN KEY (processid) REFERENCES orderprocess (processid)");

      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}
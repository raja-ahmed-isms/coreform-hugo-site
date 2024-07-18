/**
 * @(#)Update1.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/1/25
 */

package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update1 implements csimsoft.DatabaseUpdate
{

   public Update1()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      database.statement.executeUpdate(
         "ALTER TABLE users ADD COLUMN userid INT");
      database.statement.executeUpdate(
         "ALTER TABLE userinfo ADD COLUMN userid INT");
      database.statement.executeUpdate(
         "ALTER TABLE userorders ADD COLUMN userid INT");
      database.statement.executeUpdate(
         "ALTER TABLE userdownloads ADD COLUMN userid INT");
      database.statement.executeUpdate(
         "ALTER TABLE userlicenses ADD COLUMN userid INT");
      database.statement.executeUpdate(
         "ALTER TABLE userapprovals ADD COLUMN userid INT");

      database.statement.executeUpdate(
         "CREATE TABLE usercount (userid INT NOT NULL)");

      database.results = database.statement.executeQuery(
         "SELECT email FROM users");

      java.util.TreeMap<String, Integer> users =
         new java.util.TreeMap<String, Integer>();
      int nextId = 1;
      for( ; database.results.next(); nextId++)
         users.put(database.results.getString("email"), new Integer(nextId));

      database.results.close();

      java.util.Iterator<java.util.Map.Entry<String, Integer> > iter =
         users.entrySet().iterator();
      while(iter.hasNext())
      {
         java.util.Map.Entry<String, Integer> user = iter.next();
         database.statement.executeUpdate(
            "UPDATE users SET userid = " + user.getValue().toString() +
            "WHERE email = '" + user.getKey() + "'");
         database.statement.executeUpdate(
            "UPDATE userinfo SET userid = " + user.getValue().toString() +
            "WHERE email = '" + user.getKey() + "'");
         database.statement.executeUpdate(
            "UPDATE userorders SET userid = " + user.getValue().toString() +
            "WHERE email = '" + user.getKey() + "'");
         database.statement.executeUpdate(
            "UPDATE userdownloads SET userid = " + user.getValue().toString() +
            "WHERE email = '" + user.getKey() + "'");
         database.statement.executeUpdate(
            "UPDATE userlicenses SET userid = " + user.getValue().toString() +
            "WHERE email = '" + user.getKey() + "'");
         database.statement.executeUpdate(
            "UPDATE userapprovals SET userid = " + user.getValue().toString() +
            "WHERE email = '" + user.getKey() + "'");
      }

      database.statement.executeUpdate(
         "INSERT INTO usercount (userid) VALUES (" + nextId + ")");

      database.statement.executeUpdate(
         "ALTER TABLE users MODIFY COLUMN userid INT NOT NULL");
      database.statement.executeUpdate(
         "ALTER TABLE userinfo MODIFY COLUMN userid INT NOT NULL");
      database.statement.executeUpdate(
         "ALTER TABLE userorders MODIFY COLUMN userid INT NOT NULL");
      database.statement.executeUpdate(
         "ALTER TABLE userdownloads MODIFY COLUMN userid INT NOT NULL");
      database.statement.executeUpdate(
         "ALTER TABLE userlicenses MODIFY COLUMN userid INT NOT NULL");
      database.statement.executeUpdate(
         "ALTER TABLE userapprovals MODIFY COLUMN userid INT NOT NULL");

      database.statement.executeUpdate(
         "ALTER TABLE userinfo DROP PRIMARY KEY");
      database.statement.executeUpdate(
         "ALTER TABLE users DROP PRIMARY KEY");
      database.statement.executeUpdate(
         "ALTER TABLE users ADD PRIMARY KEY (userid)");
      database.statement.executeUpdate(
         "ALTER TABLE userinfo ADD PRIMARY KEY (userid)");
      //statement.executeUpdate(
      //   "ALTER TABLE userinfo ADD FOREIGN KEY (userid) REFERENCES users (userid)");

      database.statement.executeUpdate(
         "CREATE TABLE siteadmins (" +
         "userid INT NOT NULL," +
         "PRIMARY KEY (userid)," +
         "FOREIGN KEY (userid) REFERENCES users (userid))");

      java.util.ArrayList<Integer> admins = new java.util.ArrayList<Integer>();
      database.results = database.statement.executeQuery(
         "SELECT userid FROM users WHERE role = 0");
      while(database.results.next())
         admins.add(new Integer(database.results.getInt("userid")));

      database.results.close();

      java.util.Iterator<Integer> jter = admins.iterator();
      while(iter.hasNext())
      {
         database.statement.executeUpdate(
            "INSERT INTO siteadmins (userid) VALUES (" +
            iter.next().toString() + ")");
      }

      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}
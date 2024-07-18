/**
 *
 * @author Mark Richardson
 */
package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;


public class Update23 implements csimsoft.DatabaseUpdate
{

   public Update23()
   {
   }

   private static class Activation
   {
      public int UserLicenseId = 0;
      public String HostId = null;

      public Activation(int userLicenseId, String hostId)
      {
         this.UserLicenseId = userLicenseId;
         this.HostId = hostId;
      }
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add tables to store the activation signatures.
      database.statement.executeUpdate(
         "CREATE TABLE activationfeatures (" +
         "activationid INT NOT NULL, " +
         "featureid INT NOT NULL, " +
         "featurechk VARCHAR(64) NOT NULL, " +
         "featuresig VARCHAR(512) NOT NULL)");
      database.statement.executeUpdate(
         "CREATE TABLE activationcount (" +
         "activationid INT NOT NULL)");
      database.statement.executeUpdate(
         "ALTER TABLE licenseactivations ADD COLUMN activationid INT");

      // Set an id for the existing activations.
      ArrayList<Update23.Activation> activations = new ArrayList<Update23.Activation>();
      database.results = database.statement.executeQuery(
         "SELECT userlicenseid, hostid FROM licenseactivations");
      while(database.results.next())
      {
         activations.add(new Update23.Activation(
            database.results.getInt("userlicenseid"),
            database.results.getString("hostid")));
      }

      database.results.close();

      int nextId = 1;
      PreparedStatement update = database.connection.prepareStatement(
         "UPDATE licenseactivations SET activationid = ? " +
         "WHERE userlicenseid = ? AND hostid = ?");
      database.addStatement(update);
      Iterator<Update23.Activation> iter = activations.iterator();
      for( ; iter.hasNext(); ++nextId)
      {
         Update23.Activation activation = iter.next();
         update.setInt(1, nextId);
         update.setInt(2, activation.UserLicenseId);
         update.setString(3, activation.HostId);
         update.executeUpdate();
      }

      // Set the next available activation id.
      database.statement.executeUpdate(
         "INSERT INTO activationcount (activationid) VALUES (" + nextId + ")");

      // Constrain the new column.
      try
      {
         database.statement.executeUpdate(
            "ALTER TABLE licenseactivations MODIFY COLUMN activationid INT NOT NULL");
      }
      catch(SQLException sqle)
      {
         // Try an alternate form for apache derby.
         database.statement.executeUpdate(
            "ALTER TABLE licenseactivations ALTER COLUMN activationid NOT NULL");
      }

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

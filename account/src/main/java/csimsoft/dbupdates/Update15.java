/**
 *
 * @author Mark Richardson
 */
package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update15 implements csimsoft.DatabaseUpdate
{

   public Update15()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      // Add the new license tables to the database.
      database.statement.executeUpdate(
         "CREATE TABLE featurecount (" +
         "featureid INT NOT NULL)");

      database.statement.executeUpdate(
         "CREATE TABLE features (" +
         "featureid INT NOT NULL," +
         "featurename VARCHAR(64) NOT NULL," +
         "featurekey VARCHAR(32) NOT NULL," +
         "PRIMARY KEY (featureid))");

      database.statement.executeUpdate(
         "CREATE TABLE platformcount (" +
         "platformid INT NOT NULL)");

      database.statement.executeUpdate(
         "CREATE TABLE platforms (" +
         "platformid INT NOT NULL," +
         "platformname VARCHAR(32) NOT NULL," +
         "PRIMARY KEY (platformid))");

      database.statement.executeUpdate(
         "CREATE TABLE licenseproducts (" +
         "licenseid INT NOT NULL," +
         "licensename VARCHAR(64) NOT NULL," +
         "version VARCHAR(16) NOT NULL," +
         "processid INT," +
         "PRIMARY KEY (licenseid))");

      database.statement.executeUpdate(
         "CREATE TABLE licensefeatures (" +
         "featureid INT NOT NULL," +
         "licenseid INT NOT NULL)");

      database.statement.executeUpdate(
         "CREATE TABLE platformdownloads (" +
         "platformid INT NOT NULL," +
         "licenseid INT NOT NULL," +
         "productid INT NOT NULL)");

      database.statement.executeUpdate(
         "CREATE TABLE upgradecount (" +
         "upgradeid INT NOT NULL)");

      database.statement.executeUpdate(
         "CREATE TABLE upgradepaths (" +
         "upgradeid INT NOT NULL," +
         "upgradename VARCHAR(64) NOT NULL," +
         "PRIMARY KEY (upgradeid))");

      database.statement.executeUpdate(
         "CREATE TABLE upgradeitems (" +
         "upgradeid INT NOT NULL," +
         "licenseid INT NOT NULL," +
         "upgradeorder INT NOT NULL)");

      // Add the new user license tables.
      database.statement.executeUpdate(
         "CREATE TABLE userlicensecount (" +
         "userlicenseid INT NOT NULL)");

      database.statement.executeUpdate(
         "CREATE TABLE licenses (" +
         "userlicenseid INT NOT NULL," +
         "userid INT NOT NULL," +
         "licenseid INT NOT NULL," +
         "licensetype INT NOT NULL," +
         "licensekey VARCHAR(32) NOT NULL," +
         "quantity INT NOT NULL," +
         "numdays INT NOT NULL," +
         "expiration DATE," +
         "upgradeid INT NOT NULL," +
         "upgradedate DATE," +
         "PRIMARY KEY (userlicenseid))");

      database.statement.executeUpdate(
         "CREATE TABLE userplatforms (" +
         "userlicenseid INT NOT NULL," +
         "platformid INT NOT NULL)");

      database.statement.executeUpdate(
         "CREATE TABLE userfeatures (" +
         "userlicenseid INT NOT NULL," +
         "featureid INT NOT NULL)");

      // Commit all the changes.
      database.connection.commit();
      message.setResult(ResultMessage.Success);
   }
}

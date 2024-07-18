/**
 * @(#)Database.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/4/11
 */

package csimsoft;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class Database
{
   public Connection connection = null;
   public Statement statement = null;
   public ResultSet results = null;
   protected ArrayList<Statement> statements = new ArrayList<Statement>();

   public Database()
   {
   }

   public void connect() throws NamingException, SQLException
   {
      if(this.connection == null)
      {
         int loops = 180;
         for( int i = 0; i < loops; i++)
         {
            try
            {
               Context context = new InitialContext();
               //context = (Context)context.lookup("java:comp/env");
               DataSource source = (DataSource)context.lookup("jdbc/csimsoftdb");
               source.setLoginTimeout(30);
               this.connection = source.getConnection();
               break;
            }
            catch(Exception e)
            {
               AcctLogger.get().info(e.toString());
            }
            try
            {
               Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
               break;
            }
         }
         if(this.connection == null) 
         {
            throw new SQLException("Could not connect to SQL server");
         }
      }
   }

   public void addStatement(Statement sqlStatement)
   {
      this.statements.add(sqlStatement);
   }

   public void removeStatement(Statement sqlStatement)
   {
      this.statements.remove(sqlStatement);
   }

   public void cleanup()
   {
      try
      {
         this.results.close();
      }
      catch(Exception e)
      {
      }

      this.results = null;
      Iterator<Statement> iter = this.statements.iterator();
      while(iter.hasNext())
      {
         try
         {
            iter.next().close();
         }
         catch(Exception e)
         {
         }
      }

      this.statements.clear();
      try
      {
         this.statement.close();
      }
      catch(Exception e)
      {
      }

      this.statement = null;
      try
      {
         this.connection.close();
      }
      catch(Exception e)
      {
      }

      this.connection = null;
   }

   public int getNextId(String table, String column) throws SQLException
   {
      if(this.statement == null)
         this.statement = this.connection.createStatement();

      // Get the current id from the database.
      int nextId = 1;
      this.results = this.statement.executeQuery(
         "SELECT " + column + " FROM " + table);
      if(this.results.next())
      {
         int lastId = this.results.getInt(column);
         nextId = lastId + 1;

         // Save the new id in place of the old one.
         this.results.close();
         this.statement.executeUpdate(
            "UPDATE " + table + " SET " + column + " = " + nextId +
            " WHERE " + column + " = " + lastId);
      }
      else
      {
         // Add an initial entry for the id.
         this.results.close();
         this.statement.executeUpdate(
            "INSERT INTO " + table + " (" + column + ") VALUES (" + nextId + ")");
      }

      return nextId;
   }

   public static String generateKey()
   {
      // Generate a random UUID. Remove the '-' from the formatted string.
      String key = java.util.UUID.randomUUID().toString().toUpperCase();
      String[] tokens = key.split("-");
      if(tokens.length > 0)
      {
         key = tokens[0];
         for(int i = 1; i < tokens.length; ++i)
            key += tokens[i];
      }

      if(key.length() > 32)
         key = key.substring(0, 32);

      return key;
   }

   public static String formatDate(java.util.Calendar date)
   {
      if(date == null)
         return null;

      return String.format("%tF", date);
   }

   public static java.util.Calendar dateFromString( String date )
   {
      if(date == null) {
         return null;
      }

      String[] splits = date.split("-");
      java.util.Calendar outDate = java.util.Calendar.getInstance();
      outDate.set(java.util.Calendar.YEAR, Integer.parseInt(splits[0]));
      outDate.set(java.util.Calendar.MONTH, Integer.parseInt(splits[1]));
      outDate.set(java.util.Calendar.DAY_OF_MONTH, Integer.parseInt(splits[2]));
      return outDate;
   }

   public static String formatDate(java.sql.Date date)
   {
      if(date == null)
         return null;

      return String.format("%tF", date);
   }

   public boolean needsInitialization() throws SQLException
   {
      if(this.statement == null)
         this.statement = this.connection.createStatement();

      // See if the version number is set.
      int version = 0;
      try
      {
         // See what the current version is.
         this.results = this.statement.executeQuery(
            "SELECT versionid FROM cssversion");
         if(this.results.next())
            version = this.results.getInt("versionid");

         this.results.close();
      }
      catch(SQLException sqle)
      {
      }

      return version == 0;
   }

   public void initializeDb() throws SQLException
   {
      this.statement.executeUpdate(
         "CREATE TABLE cssversion (" +
         "  versionid INT NOT NULL)");
      this.statement.executeUpdate(
         "INSERT INTO cssversion (versionid) VALUES (30)");

      this.statement.executeUpdate(
         "CREATE TABLE users (" +
         "  userid INT NOT NULL," +
         "  email VARCHAR(64) NOT NULL," +
         "  password VARCHAR(64) NOT NULL," +
         "  firstname VARCHAR(32) NOT NULL," +
         "  lastname VARCHAR(32) NOT NULL," +
         "  groupid INT NOT NULL," +
         "  company VARCHAR(128)," +
         "  phone VARCHAR(32) NOT NULL," +
         "  address VARCHAR(512) NOT NULL," +
         "  country VARCHAR(32) NOT NULL," +
         "  PRIMARY KEY (userid))");

      this.statement.executeUpdate(
         "CREATE TABLE passreset (" +
         "  token VARCHAR(64) NOT NULL," +
         "  email VARCHAR(64) NOT NULL," +
         "  timecreated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
         "  PRIMARY KEY (token))");

      this.statement.executeUpdate(
         "CREATE TABLE usercount (" +
         "  userid INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE siteadmins (" +
         "  userid INT NOT NULL," +
         "  PRIMARY KEY (userid)," +
         "  FOREIGN KEY (userid) REFERENCES users (userid))");

      this.statement.executeUpdate(
         "CREATE TABLE distributorcount (" +
         "  groupid INT NOT NULL)");
      this.statement.executeUpdate(
         "CREATE TABLE distributors (" +
         "  userid INT NOT NULL," +
         "  groupid INT NOT NULL," +
         "  folder INT NOT NULL," +
         "  PRIMARY KEY (userid)," +
         "  FOREIGN KEY (userid) REFERENCES users (userid))");

      this.statement.executeUpdate(
         "CREATE TABLE downloadpath (" +
         "  directory VARCHAR(64) NOT NULL," +
         "  PRIMARY KEY (directory))");

      this.statement.executeUpdate(
         "CREATE TABLE productcount (" +
         "  productid INT NOT NULL)");
      this.statement.executeUpdate(
         "CREATE TABLE products (" +
         "  productid INT NOT NULL," +
         "  product VARCHAR(32) NOT NULL," +
         "  location VARCHAR(128) NOT NULL," +
         "  parentid INT NOT NULL," +
         "  depth INT NOT NULL," +
         "  folder INT NOT NULL," +
         "  processid INT," +
         "  releasedate DATE," +
         "  PRIMARY KEY (productid))");

      this.statement.executeUpdate(
         "CREATE TABLE productpaths (" +
         "  productid INT NOT NULL," +
         "  pathorder INT NOT NULL," +
         "  parentid INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE featurecount (" +
         "  featureid INT NOT NULL)");
      this.statement.executeUpdate(
         "CREATE TABLE features (" +
         "  featureid INT NOT NULL," +
         "  featurename VARCHAR(64) NOT NULL," +
         "  featurekey VARCHAR(32) NOT NULL," +
         "  PRIMARY KEY (featureid))");

      this.statement.executeUpdate(
         "CREATE TABLE optioncount (" +
         "  optionid INT NOT NULL)");
      this.statement.executeUpdate(
         "CREATE TABLE options (" +
         "  optionid INT NOT NULL," +
         "  optionname VARCHAR(64) NOT NULL," +
         "  optionkey VARCHAR(32) NOT NULL," +
         "  PRIMARY KEY (optionid))");

      this.statement.executeUpdate(
         "CREATE TABLE platformcount (" +
         "  platformid INT NOT NULL)");
      this.statement.executeUpdate(
         "CREATE TABLE platforms (" +
         "  platformid INT NOT NULL," +
         "  platformname VARCHAR(32) NOT NULL," +
         "  PRIMARY KEY (platformid))");

      this.statement.executeUpdate(
         "CREATE TABLE licensecount (" +
         "  licenseid INT NOT NULL)");
      this.statement.executeUpdate(
         "CREATE TABLE licenseproducts (" +
         "  licenseid INT NOT NULL," +
         "  licensename VARCHAR(64) NOT NULL," +
         "  version VARCHAR(16) NOT NULL," +
         "  allowedinst INT NOT NULL," +
         "  processid INT," +
         "  PRIMARY KEY (licenseid))");

      this.statement.executeUpdate(
         "CREATE TABLE licensefeatures (" +
         "  featureid INT NOT NULL," +
         "  licenseid INT NOT NULL," +
         "  autoadd INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE licenseoptions (" +
         "  optionid INT NOT NULL," +
         "  licenseid INT NOT NULL," +
         "  autoadd INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE platformdownloads (" +
         "  platformid INT NOT NULL," +
         "  licenseid INT NOT NULL," +
         "  productid INT NOT NULL," +
         "  autoadd INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE upgradecount (" +
         "  upgradeid INT NOT NULL)");
      this.statement.executeUpdate(
         "CREATE TABLE upgradepaths (" +
         "  upgradeid INT NOT NULL," +
         "  upgradename VARCHAR(64) NOT NULL," +
         "  PRIMARY KEY (upgradeid))");

      this.statement.executeUpdate(
         "CREATE TABLE upgradeitems (" +
         "  upgradeid INT NOT NULL," +
         "  licenseid INT NOT NULL," +
         "  upgradeorder INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE licensegroupcount (" +
         "  groupid INT NOT NULL)");
      this.statement.executeUpdate(
         "CREATE TABLE licensegroups (" +
         "  groupid INT NOT NULL," +
         "  groupname VARCHAR(64) NOT NULL," +
         "  PRIMARY KEY (groupid))");

      this.statement.executeUpdate(
         "CREATE TABLE openproducts (" +
         "  productid INT NOT NULL," +
         "  PRIMARY KEY (productid)," +
         "  FOREIGN KEY (productid) REFERENCES products (productid))");

      this.statement.executeUpdate(
         "CREATE TABLE orderprocesscount (" +
         "  processid INT NOT NULL)");
      this.statement.executeUpdate(
         "CREATE TABLE orderprocess (" +
         "  processid INT NOT NULL," +
         "  processname VARCHAR(64) NOT NULL," +
         "  handlername VARCHAR(128) NOT NULL," +
         "  PRIMARY KEY (processid))");

      this.statement.executeUpdate(
         "CREATE TABLE ordercount (" +
         "  orderid INT NOT NULL)");
      this.statement.executeUpdate(
         "CREATE TABLE userorders (" +
         "  orderid INT NOT NULL," +
         "  orderdate DATE NOT NULL," +
         "  userid INT NOT NULL," +
         "  processid INT NOT NULL," +
         "  processstep INT NOT NULL," +
         "  ordernumber VARCHAR(32)," +
         "  distnumber VARCHAR(64)," +
         "  srcnumber VARCHAR(32)," +
         "  PRIMARY KEY (orderid))");

      this.statement.executeUpdate(
         "CREATE TABLE orderproducts (" +
         "  orderid INT NOT NULL," +
         "  productid INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE orderlicenses (" +
         "  orderid INT NOT NULL," +
         "  licenseid INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE userdownloads (" +
         "  userid INT NOT NULL," +
         "  productid INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE userlicensecount (" +
         "  userlicenseid INT NOT NULL)");
      this.statement.executeUpdate(
         "CREATE TABLE licenses (" +
         "  userlicenseid INT NOT NULL," +
         "  userid INT NOT NULL," +
         "  licenseid INT NOT NULL," +
         "  licensetype INT NOT NULL," +
         "  licensekey VARCHAR(32) NOT NULL," +
         "  quantity INT NOT NULL," +
         "  allowedinst INT NOT NULL," +
         "  vmallowed INT NOT NULL," +
         "  rdallowed INT NOT NULL," +
         "  deactivations INT NOT NULL," +
         "  maxdeactivations INT NOT NULL," +
         "  numdays INT NOT NULL," +
         "  expiration DATE," +
         "  startdate DATE," +
         "  upgradeid INT NOT NULL," +
         "  upgradedate DATE," +
         "  PRIMARY KEY (userlicenseid))");

      this.statement.executeUpdate(
         "CREATE TABLE userplatforms (" +
         "  userlicenseid INT NOT NULL," +
         "  platformid INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE userfeatures (" +
         "  userlicenseid INT NOT NULL," +
         "  featureid INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE useroptions (" +
         "  userlicenseid INT NOT NULL," +
         "  optionid INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE activationcount (" +
         "  activationid INT NOT NULL)");
      this.statement.executeUpdate(
         "CREATE TABLE licenseactivations (" +
         "  activationid INT NOT NULL," +
         "  userlicenseid INT NOT NULL," +
         "  hostid VARCHAR(64) NOT NULL," +
         "  hostname VARCHAR(64) NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE activationfeatures (" +
         "  activationid INT NOT NULL," +
         "  featureid INT NOT NULL," +
         "  featurechk VARCHAR(64) NOT NULL," +
         "  featuresig VARCHAR(512) NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE vmsign (" +
         "  licenseid INT NOT NULL," +
         "  featurechk VARCHAR(64) NOT NULL," +
         "  featuresig VARCHAR(512) NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE distributorgroups (" +
         "  userid INT NOT NULL," +
         "  groupid INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE distributorlicenses (" +
         "  userid INT NOT NULL," +
         "  licenseid INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE distributorupgrades (" +
         "  userid INT NOT NULL," +
         "  upgradeid INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE distributoremails (" +
         "  userid INT NOT NULL," +
         "  emailtype INT NOT NULL," +
         "  productid INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE processlicense (" +
         "  processid INT NOT NULL," +
         "  productid INT," +
         "  PRIMARY KEY (processid)," +
         "  FOREIGN KEY (processid) REFERENCES orderprocess (processid))");

      this.statement.executeUpdate(
         "CREATE TABLE userapprovals (" +
         "  userid INT NOT NULL," +
         "  productid INT NOT NULL," +
         "  approval INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE licenseapprovals (" +
         "  userid INT NOT NULL," +
         "  licenseid INT NOT NULL," +
         "  approval INT NOT NULL)");

      this.statement.executeUpdate(
         "CREATE TABLE universitycount (" +
         "  universityid INT NOT NULL)");
      this.statement.executeUpdate(
         "CREATE TABLE universities (" +
         "  universityid INT NOT NULL," +
         "  university VARCHAR(128) NOT NULL," +
         "  fax VARCHAR(32)," +
         "  adminid INT NOT NULL," +
         "  techid INT NOT NULL," +
         "  PRIMARY KEY (universityid))");

      this.statement.executeUpdate(
         "CREATE TABLE contacts (" +
         "  contactid INT NOT NULL," +
         "  email VARCHAR(64) NOT NULL," +
         "  firstname VARCHAR(32) NOT NULL," +
         "  lastname VARCHAR(32) NOT NULL," +
         "  phone VARCHAR(32)," +
         "  address VARCHAR(512)," +
         "  country VARCHAR(32) NOT NULL," +
         "  PRIMARY KEY (contactid))");
   }
}


/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class ActivateLicenseServlet extends HttpServlet {

   public ActivateLicenseServlet() {
   }

   private static class Message implements ResultMessageAdapter {
      public ResultMessage Report = null;
      public boolean Admin = false;
      public boolean Distrib = false;

      public Message(ResultMessage message) {
         this.Report = message;
      }

      @Override
      public void setResult(int result) {
         this.Report.setResult(result);
         if (result == ResultMessage.Failure) {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage("Unable to activate the product license.");
         } else if (result == ResultMessage.Success) {
            this.Report.setType(ResultMessage.InfoMessage);
            this.Report.setMessage("The product license was successfully activated.");
         } else if (result == ResultMessage.DoesNotExist) {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage("The specified product license key does not exist.");
         }
      }

      @Override
      public void addException(Exception e) {
         if (this.Admin || this.Distrib)
            this.Report.appendToMessage("<br />" + e);
      }

      @Override
      public void addMessage(String extra) {
         this.Report.appendToMessage(extra);
      }
   }

   public static class ActivationFeature {
      public int FeatureId = 0;
      public String Key = "";
      public String Check = "";
      public String Signature = "";

      public ActivationFeature(int featureId, String featureKey) {
         this.FeatureId = featureId;
         this.Key = featureKey;
         if (featureKey == null)
            this.Key = "";
      }
   }

   public static class LicenseInfo {
      public int LicenseId = 0;
      public int LicenseType = 0;
      public int Processes = 0;
      public boolean VmAllowed = true;
      public boolean RdAllowed = true;
      public int Quantity = 0;
      public int NumDays = 0;
      public String Expiration = null;
      public String Version = "";
      public String Options = "";
      public ArrayList<ActivateLicenseServlet.ActivationFeature> Features =
            new ArrayList<ActivateLicenseServlet.ActivationFeature>();
      public ActivateLicenseServlet.ActivationFeature FloatingVM = null;

      public LicenseInfo() {
      }

      public boolean canActivate(int activations) {
         if (this.LicenseType == DatabaseLicenses.FloatingLicense)
            return activations == 0;
         else
            return activations < this.Quantity;
      }
   }

   public static boolean isTrialLicense(DatabaseLicenses databaseLicenses, int licenseId) throws SQLException {
      if (databaseLicenses.getLicenseName(licenseId).contains("Trial")) {
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
         throws IOException, ServletException {

      AcctLogger.get().info("Start");
      // Make sure the character decoding is correct.
      if (request.getCharacterEncoding() == null)
         request.setCharacterEncoding("UTF-8");

      // Get the parameters from the request.
      String hostId = request.getParameter("hostid");
      String hostName = request.getParameter("hostname");
      String licenseKey = request.getParameter("licensekey");
      if (hostId != null) {
         hostId = hostId.trim();
         if (hostId.startsWith("\"") || hostId.startsWith("'"))
            hostId = hostId.substring(1);

         if (hostId.endsWith("\"") || hostId.endsWith("'"))
            hostId = hostId.substring(0, hostId.length() - 1);

         hostId = hostId.replaceAll("[ \\t]*=[ \\t]*", "=");
      }

      if (licenseKey != null)
         licenseKey = licenseKey.trim().replace("-", "");

      boolean noXml = "no".equalsIgnoreCase(request.getParameter("xml"));

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ActivateLicenseServlet.Message message =
            new ActivateLicenseServlet.Message(LoginServlet.getResultMessage(session));

      // Check if the user is an administrator.
      int userId = 0;
      UserInfo user = null;
      if (LoginServlet.isUserLoggedIn(session)) {
         user = LoginServlet.getUserInfo(session);
         message.Admin = user.isAdministrator();
         message.Distrib = user.isDistributor();
      }
      AcctLogger.get().info("License key: " + licenseKey);

      if (licenseKey.equals("cubitlearn")) {
         if (this.sendLearnLicenseFile(response)) {
            message.setResult(ResultMessage.Success);
            return;
         }

         // Send an xml response back when there is an error.
         message.Report.sendXml(response);
         return;
      }

      ActivateLicenseServlet.LicenseInfo license = new ActivateLicenseServlet.LicenseInfo();
      if (hostId == null || hostId.isEmpty() || licenseKey == null || licenseKey.isEmpty()) {
         message.setResult(ResultMessage.Failure);
         message.addMessage(" The request is missing parameters.");
      } else if (hostId.length() > 64 || licenseKey.length() > 32) {
         message.setResult(ResultMessage.Failure);
         message.addMessage(" The host Id and/or license key are invalid.");
      } else {
         if (hostName == null)
            hostName = "";
         else if (hostName.length() > 64)
            hostName = hostName.substring(0, 64);

         Database database = new Database();
         DatabaseLicenses databaseLicenses = new DatabaseLicenses(database);
         try {
            database.connect();
            database.connection.setAutoCommit(false);
            try {
               // Look for the license in the database.
               int licenseId = 0;
               PreparedStatement query = database.connection.prepareStatement(
                     "SELECT userlicenseid, userid, licenseid, licensetype, quantity, "
                           + "allowedinst, vmallowed, rdallowed, numdays, expiration, version "
                           + "FROM licenses WHERE licensekey = ?");
               database.addStatement(query);
               query.setString(1, licenseKey);
               database.results = query.executeQuery();
               if (database.results.next()) {
                  // Get the license information.
                  license.LicenseId = database.results.getInt("userlicenseid");
                  userId = database.results.getInt("userid");
                  licenseId = database.results.getInt("licenseid");
                  license.LicenseType = database.results.getInt("licensetype");
                  license.Quantity = database.results.getInt("quantity");
                  license.Processes = database.results.getInt("allowedinst");
                  license.VmAllowed = database.results.getInt("vmallowed") != 0;
                  license.RdAllowed = database.results.getInt("rdallowed") != 0;
                  license.NumDays = database.results.getInt("numdays");
                  license.Expiration = this.formatDate(database.results.getDate("expiration"));
                  // If this is a dynamic version license the value will be set here, otherwise it's on the license template
                  license.Version = database.results.getString("version");
                  if(database.results.wasNull()) {
                     license.Version = "";
                  }
                  AcctLogger.get().info("License Type: " + license.LicenseType);
                  AcctLogger.get().info("Quantity: " + license.Quantity);
                  AcctLogger.get().info("Processes: " + license.Processes);
                  AcctLogger.get().info("VmAllowed: " + license.VmAllowed);
                  AcctLogger.get().info("RdAllowed: " + license.RdAllowed);
                  AcctLogger.get().info("NumDays: " + license.NumDays);
                  AcctLogger.get().info("Expiration: " + license.Expiration);
                  AcctLogger.get().info("Version: " + license.Version);
               }

               database.results.close();
               if (license.LicenseId > 0 && licenseId > 0) {

                  // Check the activation table.
                  int activations = 0;
                  int activationId = 0;
                  database.statement = database.connection.createStatement();
                  database.results = database.statement
                        .executeQuery("SELECT hostid, activationid FROM licenseactivations "
                              + "WHERE userlicenseid = " + license.LicenseId);
                  while (database.results.next()) {
                     String otherHostId = database.results.getString("hostid");
                     ++activations;
                     if (hostId.equals(otherHostId))
                        activationId = database.results.getInt("activationid");
                  }

                  database.results.close();

                  // Check the host id for validity.
                  // sn=something : SDK license
                  // 8 char hex number
                  // 12 char hex number : MAC addr license
                  // disksn=WD-WX60AC946860 : HDD SN license
                  // css1=serial-number : USB license
                  boolean isUsb = hostId.matches("css1=\\S*");
                  boolean isUsbShort = isUsb && !hostId.matches("css1=\\S{12,}");
                  boolean isSn = hostId.matches("[Ss][Nn]=([ \\t]*\\S+)+");
                  boolean allowed = hostId.matches("([ \\t]*\\p{XDigit}{8})+")
                        || hostId.matches("([ \\t]*\\p{XDigit}{12})+")
                        || hostId.matches("([ \\t]*[Dd][Ii][Ss][Kk][Ss][Nn]=\\S{12,})+");

                  if (isUsb && license.LicenseType != DatabaseLicenses.UsbLicense) {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage(" USB activation is not allowed for this license.");
                  } else if (isUsbShort) {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage(" The USB serial number is too short.");
                  } else if (!isUsb && license.LicenseType == DatabaseLicenses.UsbLicense) {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage(" This license requires USB activation.");
                  } else if (isSn && license.LicenseType != DatabaseLicenses.SnLicense) {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage(
                           " Serial Number activation is not allowed for this license.");
                  } else if (!isSn && license.LicenseType == DatabaseLicenses.SnLicense) {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage(" This license requires Serial Number activation.");
                  } else if (!isUsb && !isSn && !allowed) {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage(" The host Id is not valid.");
                  /*} else if (!message.Admin && isTrialLicense(databaseLicenses, licenseId) && databaseLicenses.hasHostIdActivatedLicenseBefore(licenseId, hostId)) {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage(" The given host id has already checked out a free trial before.  Please contact support@coreform.com.");*/
                  } else if (activationId > 0 || license.canActivate(activations)) {
                     // Get the rest of the license information.

                     AcctLogger.get().info("Before getLicenseInfo");
                     this.getLicenseInfo(license, licenseId, database);

                     // Set the expiration date if needed.
                     boolean changes = this.setExpiration(license, database);

                     if (activationId == 0) {
                        // Add an entry to the activation table.
                        AcctLogger.get().info("Before addActivation");
                        activationId = this.addActivation(license, hostId, hostName, database);
                        changes = true;
                     }

                     AcctLogger.get().info("Before getSignatures");
                     // Get the feature signatures for the activation.
                     this.getSignatures(license, activationId, database);

                     // Sign any features that need it.
                     AcctLogger.get().info("Before signFeatures");
                     if (this.signFeatures(license, activationId, hostId, database, message)) {
                        changes = true;
                     }

                     if (changes)
                        database.connection.commit();

                     if (message.Report.getResult() == ResultMessage.NoResult)
                        message.setResult(ResultMessage.Success);

                     // If it's a trial license, send emails
                     if (isTrialLicense(databaseLicenses, licenseId)) {
                        DatabaseUsers users = new DatabaseUsers(database);
                        String email = users.getUserEmail(userId);
                        AcctLogger.get()
                              .info("Trial license activated, sending emails to: " + email);
                        MailchimpApi.sendMailchimpActivate(email);
                     }
                  } else {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage(" License limit exceeded.");
                  }
               } else
                  message.setResult(ResultMessage.DoesNotExist);
            } catch (SQLException sqle2) {
               message.setResult(ResultMessage.Failure);
               message.addException(sqle2);
               database.connection.rollback();
            }
         } catch (SQLException sqle) {
            message.setResult(ResultMessage.ResourceError);
            message.addException(sqle);
         } catch (NamingException ne) {
            message.setResult(ResultMessage.ResourceError);
            message.addException(ne);
         }

         database.cleanup();
      }
      AcctLogger.get().info("End");

      if (noXml) {
         String page = request.getContextPath();
         if (user == null)
            page += "/index.jsp";
         else if (userId == user.getUserId()) {
            if (license.LicenseId > 0)
               page += "/licensedetail?license=" + license.LicenseId;
            else
               page += "/index.jsp";
         } else {
            if (license.LicenseId > 0)
               page += "/clientlicense.jsp?license=" + license.LicenseId;
            else
               page += "/allproducts.jsp"; // TODO: redirect to user license list.
         }

         response.sendRedirect(response.encodeRedirectURL(page));
      } else {
         if (this.sendLicenseFile(message, response, license, hostId))
            return;

         // Send an xml response back when there is an error.
         message.Report.sendXml(response);
      }
   }

   private String formatDate(java.sql.Date date) {
      if (date == null)
         return null;

      return String.format("%1$te-%1$tb-%1$tY", date).toLowerCase();
   }

   private String formatDate(java.util.Calendar date) {
      if (date == null)
         return null;

      return String.format("%1$te-%1$tb-%1$tY", date).toLowerCase();
   }

   private void getLicenseInfo(ActivateLicenseServlet.LicenseInfo license, int licenseId,
         Database database) throws SQLException {
     
      // If we haven't gotten the license from the licenses table, pull it from the license template
      if( license.Version.isEmpty()) {
         // Get the license version.
         database.results = database.statement
               .executeQuery("SELECT version FROM licenseproducts WHERE licenseid = " + licenseId);
         if (database.results.next()) {
            license.Version = database.results.getString("version");
         }

         database.results.close();
         if (license.Version.isEmpty())
            license.Version = "1.0";
      }

      // If a floating license allows virtual machines, add a feature for it.
      if (license.LicenseType == DatabaseLicenses.FloatingLicense && license.VmAllowed) {
         license.FloatingVM =
               new ActivateLicenseServlet.ActivationFeature(0, "rlm_server_enable_vm");
      }

      // Get the license features and options.
      database.results = database.statement
            .executeQuery("SELECT featureid, featurekey FROM features WHERE featureid IN "
                  + "(SELECT featureid FROM userfeatures WHERE userlicenseid = " + license.LicenseId
                  + ")");
      while (database.results.next()) {
         license.Features.add(new ActivateLicenseServlet.ActivationFeature(
               database.results.getInt("featureid"), database.results.getString("featurekey")));
      }

      database.results.close();
      database.results =
            database.statement.executeQuery("SELECT optionkey FROM options WHERE optionid IN "
                  + "(SELECT optionid FROM useroptions WHERE userlicenseid = " + license.LicenseId
                  + ")");
      if (database.results.next())
         license.Options = database.results.getString("optionkey");

      while (database.results.next())
         license.Options += "," + database.results.getString("optionkey");

      database.results.close();
   }

   private boolean setExpiration(ActivateLicenseServlet.LicenseInfo license, Database database)
         throws SQLException {
      // If the license has a number of days, calculate the date
      // and save it in the database.
      String dbExpiration = null;
      if (license.Expiration == null && license.NumDays > 0) {
         java.util.Calendar date = java.util.Calendar.getInstance();
         date.add(java.util.Calendar.DAY_OF_MONTH, license.NumDays);
         license.Expiration = this.formatDate(date);
         dbExpiration = Database.formatDate(date);
         if (dbExpiration != null) {
            PreparedStatement fixDate = database.connection.prepareStatement(
                  "UPDATE licenses SET expiration = ?, numdays = 0 " + "WHERE userlicenseid = ?");
            database.addStatement(fixDate);
            fixDate.setString(1, dbExpiration);
            fixDate.setInt(2, license.LicenseId);
            fixDate.executeUpdate();
            return true;
         }
      }

      return false;
   }

   private int addActivation(ActivateLicenseServlet.LicenseInfo license, String hostId,
         String hostName, Database database) throws SQLException {
      int activationId = database.getNextId("activationcount", "activationid");
      PreparedStatement update =
            database.connection.prepareStatement("INSERT INTO licenseactivations (activationid, "
                  + "userlicenseid, hostid, hostname) VALUES (?, ?, ?, ?)");
      database.addStatement(update);
      update.setInt(1, activationId);
      update.setInt(2, license.LicenseId);
      update.setString(3, hostId);
      update.setString(4, hostName);
      update.executeUpdate();

      return activationId;
   }

   private void getSignatures(ActivateLicenseServlet.LicenseInfo license, int activationId,
         Database database) throws SQLException {
      database.results = database.statement
            .executeQuery("SELECT featureid, featurechk, featuresig FROM activationfeatures "
                  + "WHERE activationid = " + activationId);
      while (database.results.next()) {
         int featureId = database.results.getInt("featureid");
         Iterator<ActivateLicenseServlet.ActivationFeature> iter = license.Features.iterator();
         while (iter.hasNext()) {
            ActivateLicenseServlet.ActivationFeature feature = iter.next();
            if (feature.FeatureId == featureId) {
               feature.Check = database.results.getString("featurechk");
               if (feature.Check == null)
                  feature.Check = "";

               feature.Signature = database.results.getString("featuresig");
               if (feature.Signature == null)
                  feature.Signature = "";

               break;
            }
         }
      }

      database.results.close();

      // Get the virtual machine signature if needed.
      if (license.FloatingVM != null) {
         database.results = database.statement.executeQuery(
               "SELECT featurechk, featuresig FROM vmsign WHERE licenseid = " + license.LicenseId);
         if (database.results.next()) {
            license.FloatingVM.Check = database.results.getString("featurechk");
            if (license.FloatingVM.Check == null)
               license.FloatingVM.Check = "";

            license.FloatingVM.Signature = database.results.getString("featuresig");
            if (license.FloatingVM.Signature == null)
               license.FloatingVM.Signature = "";
         }

         database.results.close();
      }
   }

   private boolean signFeatures(ActivateLicenseServlet.LicenseInfo license, int activationId,
         String hostId, Database database, ActivateLicenseServlet.Message message)
         throws SQLException {
      // Find the features with missing signatures.
      ArrayList<ActivateLicenseServlet.ActivationFeature> missing =
            new ArrayList<ActivateLicenseServlet.ActivationFeature>();
      Iterator<ActivateLicenseServlet.ActivationFeature> iter = license.Features.iterator();
      while (iter.hasNext()) {
         ActivateLicenseServlet.ActivationFeature feature = iter.next();
         if (feature.Signature.isEmpty())
            missing.add(feature);
      }

      boolean vmMissing = license.FloatingVM != null && license.FloatingVM.Signature.isEmpty();

      if (missing.isEmpty() && !vmMissing)
         return false;

      // Create a license file with the missing features.
      if (hostId.contains(" "))
         hostId = "\"" + hostId + "\"";

      File file = null;
      BufferedWriter writer = null;
      try {
         // Write the license file.
         file = File.createTempFile("css", "lic");
         writer = new BufferedWriter(new FileWriter(file));
         this.writeHeader(writer, license, hostId);

         String expiration = "permanent";
         if (license.Expiration != null)
            expiration = license.Expiration;

         iter = missing.iterator();
         while (iter.hasNext()) {
            ActivateLicenseServlet.ActivationFeature feature = iter.next();
            this.writeFeature(writer, license, feature, expiration, hostId);
            writer.newLine();
         }

         if (vmMissing)
            this.writeVmFeature(writer, license, expiration);

         writer.close();
      } catch (IOException ioe) {
         message.setResult(ResultMessage.Failure);
         message.addMessage(" Unable to generate the license file.");
         try {
            if (writer != null)
               writer.close();
         } catch (Exception e) {
         }

         if (file != null)
            file.delete();

         return false;
      }

      // Sign the license file.
      try {
         Process signer = Runtime.getRuntime().exec("rlmsign " + file.getAbsolutePath());
         BufferedReader stdout = new BufferedReader(new InputStreamReader(signer.getInputStream()));
         BufferedReader stderr = new BufferedReader(new InputStreamReader(signer.getErrorStream()));
         String line;
         while ((line = stdout.readLine()) != null) {
            AcctLogger.get().info(line);
         }
         while ((line = stderr.readLine()) != null) {
            AcctLogger.get().info(line);
         }
         int result = signer.waitFor();
         if (result != 0) {
            stdout.close();
            stderr.close();
            message.setResult(ResultMessage.Failure);
            message.addMessage(" Unable to sign the license file.");
            file.delete();
            return false;
         }
      } catch (Exception e) {
         message.setResult(ResultMessage.Failure);
         message.addMessage(" Unable to sign the license file.");
         message.addException(e);
         file.delete();
         return false;
      }

      // Read the signatures from the file.
      BufferedReader reader = null;
      try {
         reader = new BufferedReader(new FileReader(file));
         String featureInfo = null;
         String line = reader.readLine();
         while (line != null) {
            if (line.startsWith("LICENSE")) {
               if (featureInfo != null)
                  this.parseFeatureInfo(featureInfo, missing, license.FloatingVM);

               featureInfo = line;
            } else if (line.startsWith("HOST") || line.startsWith("ISV")) {
               if (featureInfo != null) {
                  this.parseFeatureInfo(featureInfo, missing, license.FloatingVM);
                  featureInfo = null;
               }
            } else if (featureInfo != null)
               featureInfo += line;

            line = reader.readLine();
         }

         reader.close();
         if (featureInfo != null)
            this.parseFeatureInfo(featureInfo, missing, license.FloatingVM);
      } catch (IOException ioe) {
         message.setResult(ResultMessage.Failure);
         message.addMessage(" Unable to sign the license file.");
         message.addException(ioe);
         try {
            if (reader != null)
               reader.close();
         } catch (Exception e) {
         }

         file.delete();
         return false;
      }

      // Clean up the temporary file.
      file.delete();

      // Save the signatures in the database.
      int stillMissing = 0;
      iter = missing.iterator();
      PreparedStatement addSig = database.connection.prepareStatement(
            "INSERT INTO activationfeatures (activationid, featureid, featurechk, "
                  + "featuresig) VALUES(?, ?, ?, ?)");
      database.addStatement(addSig);
      while (iter.hasNext()) {
         ActivateLicenseServlet.ActivationFeature feature = iter.next();
         if (feature.Signature.isEmpty())
            ++stillMissing;
         else {
            addSig.setInt(1, activationId);
            addSig.setInt(2, feature.FeatureId);
            addSig.setString(3, feature.Check);
            addSig.setString(4, feature.Signature);
            addSig.executeUpdate();
         }
      }

      if (vmMissing) {
         PreparedStatement addVmSig = database.connection.prepareStatement(
               "INSERT INTO vmsign (licenseid, featurechk, featuresig) VALUES (?, ?, ?)");
         database.addStatement(addVmSig);
         if (license.FloatingVM.Signature.isEmpty())
            ++stillMissing;
         else {
            addVmSig.setInt(1, license.LicenseId);
            addVmSig.setString(2, license.FloatingVM.Check);
            addVmSig.setString(3, license.FloatingVM.Signature);
            addVmSig.executeUpdate();
         }
      }

      if (stillMissing > 0) {
         message.setResult(ResultMessage.Failure);
         message.addMessage(" Unable to sign the license file.");
      }

      return true;
   }

   private void parseFeatureInfo(String featureInfo,
         List<ActivateLicenseServlet.ActivationFeature> features,
         ActivateLicenseServlet.ActivationFeature vmFeature) {
      // Find the feature name.
      int index = featureInfo.indexOf("csimsoft");
      if (index < 0)
         return;

      featureInfo = featureInfo.substring(index + 8).trim();
      index = featureInfo.indexOf(" ");
      if (index < 0)
         return;

      String featureName = featureInfo.substring(0, index);
      ActivateLicenseServlet.ActivationFeature feature = null;
      Iterator<ActivateLicenseServlet.ActivationFeature> iter = features.iterator();
      while (iter.hasNext()) {
         ActivateLicenseServlet.ActivationFeature item = iter.next();
         if (featureName.equals(item.Key)) {
            feature = item;
            break;
         }
      }

      if (feature == null && vmFeature != null && featureName.equals(vmFeature.Key))
         feature = vmFeature;

      if (feature == null)
         return;

      // Find the _ck value.
      index = featureInfo.indexOf("_ck=");
      if (index < 0)
         return;

      index += 4;
      int last = featureInfo.indexOf(" ", index);
      if (last < 0)
         feature.Check = featureInfo.substring(index);
      else
         feature.Check = featureInfo.substring(index, last);

      // Find the sig value.
      index = featureInfo.indexOf("sig=");
      if (index < 0)
         return;

      int quote = featureInfo.indexOf("\"", index);
      index += 4;
      if (index == quote) {
         index += 1;
         last = featureInfo.indexOf("\"", index);
         if (last < 0)
            return;
      } else
         last = featureInfo.indexOf(" ", index);

      if (last < 0)
         feature.Signature = featureInfo.substring(index);
      else
         feature.Signature = featureInfo.substring(index, last);

      feature.Signature = feature.Signature.replace(" ", "");
   }

   private boolean sendLearnLicenseFile(HttpServletResponse response)
         throws IOException {
      ServletOutputStream stream = response.getOutputStream();
      response.setContentType("text/plain");
      response.setHeader("Content-Disposition",
            "attachment; filename=\"cubitlearn.lic\"");

      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
      writer.write("CUSTOMER CubitLearn1 isv=csimsoft server=ls75.rlmcloud.com port=443 password=7s6ndp7q55sg6mxp");
      writer.flush();
      return true;
   }


   private boolean sendLicenseFile(ActivateLicenseServlet.Message message,
         HttpServletResponse response, ActivateLicenseServlet.LicenseInfo license, String hostId)
         throws IOException {
      if (message.Report.getResult() == ResultMessage.Success)
         message.Report.clear();
      else
         return false;

      if (hostId.contains(" "))
         hostId = "\"" + hostId + "\"";

      ServletOutputStream stream = response.getOutputStream();
      response.setContentType("text/plain");
      response.setHeader("Content-Disposition",
            "attachment; filename=\"license-" + license.LicenseId + ".lic\"");

      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
      this.writeHeader(writer, license, hostId);

      String expiration = "permanent";
      if (license.Expiration != null)
         expiration = license.Expiration;

      Iterator<ActivateLicenseServlet.ActivationFeature> iter = license.Features.iterator();
      while (iter.hasNext()) {
         ActivateLicenseServlet.ActivationFeature feature = iter.next();
         if (feature.Signature.isEmpty())
            continue;

         this.writeFeature(writer, license, feature, expiration, hostId);
         if (!feature.Check.isEmpty())
            writer.write(" _ck=" + feature.Check);

         writer.newLine();
         writer.write("  sig=\"" + feature.Signature + "\"");
         writer.newLine();
      }

      if (license.FloatingVM != null && !license.FloatingVM.Signature.isEmpty())
         this.writeVmFeature(writer, license, expiration);

      writer.flush();
      return true;
   }

   private void writeHeader(BufferedWriter writer, ActivateLicenseServlet.LicenseInfo license,
         String hostId) throws IOException {
      if (license.LicenseType == DatabaseLicenses.FloatingLicense)
         writer.write("HOST localhost " + hostId + " 5053");

      writer.newLine();
      writer.write("ISV csimsoft port=5055");
      writer.newLine();
   }

   private void writeFeature(BufferedWriter writer, ActivateLicenseServlet.LicenseInfo license,
         ActivateLicenseServlet.ActivationFeature feature, String expiration, String hostId)
         throws IOException {
      writer.write("LICENSE csimsoft " + feature.Key + " " + license.Version + " " + expiration);
      if (license.LicenseType == DatabaseLicenses.FloatingLicense) {
         if(feature.Key.equals("trelis_sculpt")) { 
            // Sculpt specifically needs to always be uncounted
            writer.write(" uncounted");
         } 
         else {
            writer.write(" " + license.Quantity);
         }
         writer.write(" share=uh");
         if (license.Processes > 0)
            writer.write(":" + license.Processes);

         writer.write(" hostid=ANY");
      } else {
         // Sculpt specifically needs to always be uncounted
         if(feature.Key.equals("trelis_sculpt")) {
            writer.write(" uncounted");
         }
         else {
            if (license.Processes < 1)
               writer.write(" uncounted");
            else if (license.Processes == 1)
               writer.write(" single");
            else
               writer.write(" " + license.Processes);
         }

         writer.write(" hostid=" + hostId);
      }

      if (!license.Options.isEmpty())
         writer.write(" options=" + license.Options);

      if (license.LicenseType != DatabaseLicenses.FloatingLicense) {
         if(!license.VmAllowed || !license.RdAllowed) {
            writer.write(" disable=");
            if (!license.VmAllowed)
               writer.write("\"");

            if (!license.RdAllowed)
               writer.write("TerminalServer");

            if (!license.VmAllowed)
               writer.write(" VM\"");
         }
      }
   }

   private void writeVmFeature(BufferedWriter writer, ActivateLicenseServlet.LicenseInfo license,
         String expiration) throws IOException {
      writer.write("LICENSE csimsoft " + license.FloatingVM.Key + " 1.0 " + expiration + " 1 ");
      if (license.FloatingVM.Signature.isEmpty()) {
         writer.write(" sig=xxx");
         writer.newLine();
      } else {
         if (!license.FloatingVM.Check.isEmpty())
            writer.write(" _ck=" + license.FloatingVM.Check);

         writer.newLine();
         writer.write("  sig=\"" + license.FloatingVM.Signature + "\"");
         writer.newLine();
      }
   }
}

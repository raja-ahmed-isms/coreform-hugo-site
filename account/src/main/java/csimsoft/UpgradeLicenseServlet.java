/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class UpgradeLicenseServlet extends HttpServlet
{

   public UpgradeLicenseServlet()
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
               "The license has been successfully upgraded.");
         }
         else if(result == ResultMessage.DoesNotExist)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The license does not exist.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to upgrade the license.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to upgrade a license.<br />" +
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

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
   {
      // Make sure the character decoding is correct.
      if(request.getCharacterEncoding() == null)
         request.setCharacterEncoding("UTF-8");

      // Get the request parameters.
      int newLicenseId = 0;
      try
      {
         newLicenseId = Integer.parseInt(request.getParameter("license"));
      }
      catch(Exception e)
      {
      }

      int userLicenseId = 0;
      try
      {
         userLicenseId = Integer.parseInt(request.getParameter("userlicense"));
      }
      catch(Exception e)
      {
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      UpgradeLicenseServlet.Message message = new UpgradeLicenseServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to modify a license group.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in as an administrator to upgrade a license.");
      }
      else if(!user.isAdministrator())
         message.setResult(ResultMessage.NotAuthorized);
      else if(userLicenseId < 1 || newLicenseId < 1)
      {
         page = "/allproducts.jsp";
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />Missing required parameters.");
      }
      else
      {
         page = "/clientlicense.jsp?license=" + userLicenseId;
         DatabaseUsers users = new DatabaseUsers();
         DatabaseLicenses licenses = new DatabaseLicenses(users.database);
         DatabaseOrders orders = new DatabaseOrders(users.database);
         try
         {
            users.database.connect();
            users.database.connection.setAutoCommit(false);
            users.database.statement = users.database.connection.createStatement();
            try
            {
               // Make sure the licenses are valid.
               int userId = licenses.getUserLicenseOwner(userLicenseId);
               String licenseName = licenses.getLicenseName(newLicenseId);
               if(userId < 1 || licenseName == null)
                  message.setResult(ResultMessage.DoesNotExist);
               else
               {
                  UpgradeLicenseServlet.upgradeLicense(licenses, orders, userId,
                     userLicenseId, newLicenseId);

                  users.database.connection.commit();
                  message.setResult(ResultMessage.Success);

                  try
                  {
                     // Send an email to notify the user of the upgrade.
                     String email = users.getUserEmail(userId);
                     if(email != null)
                     {
                        csimsoft.Email messenger = new csimsoft.Email();
                        messenger.sendUpgradeMessage(message, email, userLicenseId);
                     }
                  }
                  catch(SQLException sqle3)
                  {
                     message.addMessage("<br />Unable to send an email to the user.");
                     message.addException(sqle3);
                  }
               }
            }
            catch(SQLException sqle2)
            {
               users.database.connection.rollback();
               message.setResult(ResultMessage.Failure);
               message.addException(sqle2);
            }
         }
         catch(SQLException sqle)
         {
            message.setResult(ResultMessage.ResourceError);
            message.addException(sqle);
         }
         catch(NamingException ne)
         {
            message.setResult(ResultMessage.ResourceError);
            message.addException(ne);
         }

         users.database.cleanup();
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }

   public static void upgradeLicense(DatabaseLicenses licenses, DatabaseOrders orders,
      int userId, int userLicenseId, int newLicenseId) throws SQLException
   {
      if(orders.database.statement == null)
         orders.database.statement = orders.database.connection.createStatement();

      // Remove all the signatures for the old license activations.
      licenses.removeUserLicenseSignatures(userLicenseId);
      licenses.removeUserLicenseVmSignatures(userLicenseId);

      // Remove features that aren't in the new license.
      orders.database.statement.executeUpdate(
         "DELETE FROM userfeatures WHERE userlicenseid = " + userLicenseId +
         " AND featureid NOT IN (SELECT featureid FROM licensefeatures " +
         "WHERE licenseid = " + newLicenseId + ")");

      // Remove options that aren't in the new license.
      orders.database.statement.executeUpdate(
         "DELETE FROM useroptions WHERE userlicenseid = " + userLicenseId +
         " AND optionid NOT IN (SELECT optionid FROM licenseoptions " +
         "WHERE licenseid = " + newLicenseId + ")");

      // Remove platforms that aren't in the new license.
      orders.database.statement.executeUpdate(
         "DELETE FROM userplatforms WHERE userlicenseid = " + userLicenseId +
         " AND platformid NOT IN (SELECT platformid FROM platformdownloads " +
         "WHERE licenseid = " + newLicenseId + ")");

      // Get the new default features.
      ArrayList<Integer> features = new ArrayList<Integer>();
      orders.database.results = orders.database.statement.executeQuery(
         "SELECT featureid FROM licensefeatures WHERE autoadd = 1 " +
         "AND licenseid = " + newLicenseId + " AND featureid NOT IN " +
         "(SELECT featureid FROM licensefeatures WHERE licenseid IN " +
         "(SELECT licenseid FROM licenses WHERE userlicenseid = " +
         userLicenseId + "))");
      while(orders.database.results.next())
         features.add(new Integer(orders.database.results.getInt("featureid")));

      orders.database.results.close();

      // Change the license id in the user license.
      orders.database.statement.executeUpdate(
         "UPDATE licenses SET licenseid = " + newLicenseId +
         " WHERE userlicenseid = " + userLicenseId);

      // Add new default features.
      Iterator<Integer> iter = features.iterator();
      while(iter.hasNext())
      {
         int featureId = iter.next().intValue();
         orders.database.statement.executeUpdate(
            "INSERT INTO userfeatures (userlicenseid, featureid) VALUES (" +
            userLicenseId + ", " + featureId + ")");
      }

      // Add the products to the user's download list.
      orders.addNewUserDownloads(userId, newLicenseId, userLicenseId);
   }
}

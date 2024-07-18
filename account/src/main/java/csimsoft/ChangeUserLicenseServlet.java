/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class ChangeUserLicenseServlet extends HttpServlet
{

   public ChangeUserLicenseServlet()
   {
   }

   private static class Message implements ResultMessageAdapter
   {
      public ResultMessage Report = null;
      public boolean Admin = false;

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
               "The license information has been successfully changed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to change the license information.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to change the license information.");
         }
      }

      @Override
      public void addException(Exception e)
      {
         if(this.Admin)
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
      // Get the parameters.
      int licenseId = 0;
      try
      {
         licenseId = Integer.parseInt(request.getParameter("license"));
      }
      catch(Exception e)
      {
      }

      int licenseType = 0;
      try
      {
         licenseType = Integer.parseInt(request.getParameter("ltype"));
      }
      catch(Exception e)
      {
      }

      int processes = -1;
      try
      {
         processes = Integer.parseInt(request.getParameter("pcount"));
      }
      catch(Exception e)
      {
      }

      int quantity = -1;
      try
      {
         quantity = Integer.parseInt(request.getParameter("quantity"));
      }
      catch(Exception e)
      {
      }

      String version = "";
      try{
         version = request.getParameter("version");
      }
      catch(Exception e)
      {
      }
      boolean vmAllowed = "vm".equals(request.getParameter("vm"));
      boolean rdAllowed = "rd".equals(request.getParameter("rd"));

      int numDays = 0;
      String expiration = null;
      boolean expireFailure = false;
      ParameterList params = new ParameterList();
      params.setParameters(request);
      boolean hasExpire = params.hasParameter("expiration");
      try
      {
         String expirationType = request.getParameter("expiration");
         if("numdays".equalsIgnoreCase(expirationType))
         {
            numDays = Integer.parseInt(request.getParameter("numdays"));
            expireFailure = numDays < 0;
         }
         else if("date".equalsIgnoreCase(expirationType))
         {
            int year = Integer.parseInt(request.getParameter("year"));
            int month = Integer.parseInt(request.getParameter("month")) - 1;
            int day = Integer.parseInt(request.getParameter("day"));
            java.util.Calendar date = java.util.Calendar.getInstance();

            date.set(year, month, day);
            expiration = Database.formatDate(date);
            expireFailure = expiration == null || expiration.isEmpty();
         }
      }
      catch(Exception e)
      {
         expireFailure = true;
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ChangeUserLicenseServlet.Message message = new ChangeUserLicenseServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is logged in.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      boolean isAdmin = user.isAdministrator();
      boolean isDistributor = user.isDistributor();
      message.Admin = isAdmin || isDistributor;

      if(!LoginServlet.isUserLoggedIn(session))
         message.setResult(ResultMessage.LoginError);
      else if(licenseId < 1 || quantity < 0 || expireFailure ||
         licenseType < DatabaseLicenses.NodeLockedLicense ||
         licenseType > DatabaseLicenses.RLMCloudLicense)
      {
         message.setResult(ResultMessage.Failure);
      }
      else
      {
         DatabaseUsers users = new DatabaseUsers();
         DatabaseOrders orders = new DatabaseOrders(users.database);
         DatabaseLicenses licenses = new DatabaseLicenses(users.database);
         try
         {
            users.database.connect();
            users.database.connection.setAutoCommit(false);
            users.database.statement = users.database.connection.createStatement();
            try
            {
               // Make sure the license id is valid.
               int userId = 0;
               int oldLicenseType = 0;
               int oldQuantity = 0;
               int oldProcesses = 0;
               boolean oldVmAllowed = false;
               boolean oldRdAllowed = false;
               int maxDeactivations = 0;
               String oldExpiration = null;
               users.database.results = users.database.statement.executeQuery(
                  "SELECT userid, licensetype, quantity, allowedinst, vmallowed, " +
                  "rdallowed, maxdeactivations, expiration FROM licenses " +
                  "WHERE userlicenseid = " + licenseId);
               if(users.database.results.next())
               {
                  userId = users.database.results.getInt("userid");
                  oldLicenseType = users.database.results.getInt("licensetype");
                  oldQuantity = users.database.results.getInt("quantity");
                  oldProcesses = users.database.results.getInt("allowedinst");
                  oldVmAllowed = users.database.results.getInt("vmallowed") != 0;
                  oldRdAllowed = users.database.results.getInt("rdallowed") != 0;
                  maxDeactivations = users.database.results.getInt("maxdeactivations");
                  oldExpiration = Database.formatDate(
                     users.database.results.getDate("expiration"));
               }

               users.database.results.close();
               if(userId < 1)
               {
                  message.setResult(ResultMessage.Failure);
                  message.addMessage(
                     "<br />The given license id is not valid.");
               }
               else
               {
                  // Make sure the distributor or user can access the license.
                  boolean allowed = false;
                  if(isAdmin)
                     allowed = true;
                  else if(isDistributor)
                     allowed = users.isDistributor(userId, user.getUserId());

                  if(!allowed && userId == user.getUserId())
                  {
                     int orderId = orders.getLicenseOrderId(userId, licenseId);
                     if(orderId > 0)
                        allowed = orders.isOrderActive(orderId);
                  }

                  if(allowed)
                  {
                     if(isAdmin || isDistributor)
                        page = "/clientlicense.jsp?license=" + licenseId;
                     else
                        page = "/licensedetail?license=" + licenseId;

                     // Reset the max deactivations when changing the license type.
                     if(licenseType != oldLicenseType)
                     {
                        if(licenseType == DatabaseLicenses.NodeLockedLicense)
                           maxDeactivations = 1;
                        else
                           maxDeactivations = 0;
                     }

                     // Make sure the number of allowed processes is valid.
                     if(processes < 0)
                        processes = oldProcesses;

                     // Make sure the virtual machine and remode desktop setting
                     // are valid.
                     if(!hasExpire)
                     {
                        vmAllowed = oldVmAllowed;
                        rdAllowed = oldRdAllowed;
                     }

                     String update =
                        "UPDATE licenses SET licensetype = ?, quantity = ?, " +
                        "allowedinst = ?, vmallowed = ?, rdallowed = ?, " +
                        "maxdeactivations = ?, version = ?";
                     if(hasExpire)
                     {
                        update += ", numdays = ?, expiration = ";
                        if(expiration == null || expiration.isEmpty())
                           update += "NULL";
                        else
                           update += "?";
                     }

                     update += " WHERE userlicenseid = ?";
                     PreparedStatement statement =
                        users.database.connection.prepareStatement(update);
                     users.database.addStatement(statement);

                     statement.setInt(1, licenseType);
                     statement.setInt(2, quantity);
                     statement.setInt(3, processes);
                     statement.setInt(4, vmAllowed ? 1 : 0);
                     statement.setInt(5, rdAllowed ? 1 : 0);
                     statement.setInt(6, maxDeactivations);
                     statement.setString(7, version);
                     int index = 8;
                     if(hasExpire)
                     {
                        statement.setInt(index++, numDays);
                        if(expiration != null && !expiration.isEmpty())
                           statement.setString(index++, expiration);
                     }

                     statement.setInt(index, licenseId);
                     statement.executeUpdate();

                     // If there are changes to the license, it needs to be
                     // signed again.
                     boolean expChanged = false;
                     if(hasExpire)
                     {
                        if(expiration == null)
                           expChanged = oldExpiration != null;
                        else if(oldExpiration == null)
                           expChanged = true;
                        else
                           expChanged = !expiration.equals(oldExpiration);
                     }

                     boolean changes = expChanged || licenseType != oldLicenseType ||
                        processes != oldProcesses;
                     if(!changes && licenseType == DatabaseLicenses.FloatingLicense)
                        changes = oldQuantity != quantity;

                     if(!changes && licenseType != DatabaseLicenses.FloatingLicense)
                     {
                        // Check the virtual machine and remote desktop settings.
                        changes = oldVmAllowed != vmAllowed || oldRdAllowed != rdAllowed;
                     }

                     // Clear out the signatures for the license.
                     if(changes)
                        licenses.removeUserLicenseSignatures(licenseId);

                     // See if the floating-license virtual machine signature needs
                     // to be removed.
                     changes = expChanged || licenseType != oldLicenseType ||
                        oldVmAllowed != vmAllowed;
                     if(changes)
                        licenses.removeUserLicenseVmSignatures(licenseId);

                     users.database.connection.commit();
                     message.setResult(ResultMessage.Success);
                  }
                  else
                     message.setResult(ResultMessage.NotAuthorized);
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
}

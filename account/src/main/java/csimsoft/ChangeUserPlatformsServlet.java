/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.TreeSet;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class ChangeUserPlatformsServlet extends HttpServlet
{

   public ChangeUserPlatformsServlet()
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
               "The license platforms have been successfully changed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to change the license platforms.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to change the license platforms." +
               "<br />Please log in as an administrator or distributor to do so.");
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
      // Get the parameters.
      int userLicenseId = 0;
      try
      {
         userLicenseId = Integer.parseInt(request.getParameter("license"));
      }
      catch(Exception e)
      {
      }

      TreeSet<Integer> selected = new TreeSet<Integer>();
      ParameterList params = new csimsoft.ParameterList();
      params.setParameters(request);
      params.removeParameter("license");
      for(int i = 0; i < params.getParameterCount(); ++i)
      {
         try
         {
            String platform = params.getParameterName(i);
            if(platform != null && platform.equals(request.getParameter(platform)))
               selected.add(new Integer(Integer.parseInt(platform)));
         }
         catch(Exception e)
         {
         }
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ChangeUserPlatformsServlet.Message message =
         new ChangeUserPlatformsServlet.Message(LoginServlet.getResultMessage(session));

      // Make sure the user is logged in.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      boolean isAdmin = user.isAdministrator();
      boolean isDistributor = user.isDistributor();
      if(!LoginServlet.isUserLoggedIn(session))
         message.setResult(ResultMessage.LoginError);
      else if(userLicenseId < 1)
      {
         if(isAdmin || isDistributor)
            page = "/clientlicenses.jsp";
         else
            page = "/licenses.jsp";

         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />The given license id is not valid.");
      }
      else
      {
         if(isAdmin || isDistributor)
            page = "/clientlicense.jsp?license=" + userLicenseId;
         else
            page = "/licensedetail?license=" + userLicenseId;

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
               // Make sure the license id is valid. Also, make sure the
               // distributor or user can access the license.
               int userId = licenses.getUserLicenseOwner(userLicenseId);
               if(userId < 1)
               {
                  message.setResult(ResultMessage.Failure);
                  message.addMessage(
                     "<br />The given license id is not valid.");
               }
               else if(isAdmin || userId == user.getUserId() ||
                  (isDistributor && users.isDistributor(userId, user.getUserId())))
               {
                  // Get the product license id.
                  int licenseId = 0;
                  String licenseKey = null;
                  licenses.database.results = licenses.database.statement.executeQuery(
                     "SELECT licenseid, licensekey FROM licenses " +
                     "WHERE userlicenseid = " + userLicenseId);
                  if(licenses.database.results.next())
                  {
                     licenseId = licenses.database.results.getInt("licenseid");
                     licenseKey = licenses.database.results.getString("licensekey");
                  }

                  licenses.database.results.close();
                  if(licenseKey == null)
                     licenseKey = "";

                  // Get the list of currently selected platforms.
                  TreeSet<Integer> current = new TreeSet<Integer>();
                  licenses.database.results = licenses.database.statement.executeQuery(
                     "SELECT platformid FROM userplatforms WHERE userlicenseid = " +
                     userLicenseId);
                  while(licenses.database.results.next())
                  {
                     current.add(new Integer(
                        licenses.database.results.getInt("platformid")));
                  }

                  licenses.database.results.close();

                  // Add the newly selected platforms.
                  int count = 0;
                  Iterator<Integer> iter = selected.iterator();
                  while(iter.hasNext())
                  {
                     Integer platformId = iter.next();
                     if(!current.contains(platformId))
                     {
                        ++count;
                        licenses.database.statement.executeUpdate(
                           "INSERT INTO userplatforms (userlicenseid, platformid) " +
                           "VALUES (" + userLicenseId + ", " + platformId.intValue() +
                           ")");
                     }
                  }

                  // Add the missing downloads to the user's list.
                  if(count > 0 && !licenseKey.isEmpty())
                     orders.addNewUserDownloads(userId, licenseId, userLicenseId);

                  // Remove the unselected features.
                  iter = current.iterator();
                  while(iter.hasNext())
                  {
                     Integer platformId = iter.next();
                     if(!selected.contains(platformId))
                     {
                        int productId = 0;
                        licenses.database.results =
                           licenses.database.statement.executeQuery(
                           "SELECT productid FROM platformdownloads WHERE licenseid = " +
                           licenseId + " AND platformid = " + platformId.intValue());
                        if(licenses.database.results.next())
                           productId = licenses.database.results.getInt("productid");

                        licenses.database.results.close();
                        if(productId < 1)
                           continue;

                        ++count;
                        licenses.database.statement.executeUpdate(
                           "DELETE FROM userplatforms WHERE userlicenseid = " +
                           userLicenseId + " AND platformid = " + platformId.intValue());

                        if(licenseKey.isEmpty())
                           continue;

                        // Remove the platform product if it is not part of
                        // another license.
                        if(!licenses.isPlatformDownloadUsed(userId, productId))
                        {
                           licenses.database.statement.executeUpdate(
                           "DELETE FROM userdownloads WHERE userid = " + userId +
                           " AND productid = " + productId);
                        }
                     }
                  }

                  if(count > 0)
                     licenses.database.connection.commit();

                  message.setResult(ResultMessage.Success);
               }
               else
               {
                  if(isDistributor)
                     page = "/clientlicenses.jsp";
                  else
                     page = "/licenses.jsp";

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

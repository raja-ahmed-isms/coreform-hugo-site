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


public class UpgradeLicensesServlet extends HttpServlet
{

   public UpgradeLicensesServlet()
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
               "The licenses have been successfully upgraded.");
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
               "Unable to upgrade any licenses.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to upgrade licenses.<br />" +
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

   private static class UserLicense
   {
      public int licenseId = 0;
      public int userId = 0;

      public UserLicense()
      {
      }

      public UserLicense(int license, int user)
      {
         this.licenseId = license;
         this.userId = user;
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
      int upgradeId = 0;
      try
      {
         upgradeId = Integer.parseInt(request.getParameter("upgrade"));
      }
      catch(Exception e)
      {
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      UpgradeLicensesServlet.Message message = new UpgradeLicensesServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to modify a license group.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in as an administrator to upgrade licenses.");
      }
      else if(!user.isAdministrator())
         message.setResult(ResultMessage.NotAuthorized);
      else if(upgradeId < 1)
      {
         page = "/allupgrades.jsp";
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />Missing required parameters.");
      }
      else
      {
         page = "/allupgrades.jsp";
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
               // Make sure the upgrade path is valid.
               String upgradeName = licenses.getUpgradeName(upgradeId);
               if(upgradeName == null)
                  message.setResult(ResultMessage.DoesNotExist);
               else
               {
                  // Get the newest license from the upgrade path.
                  int licenseId = 0;
                  licenses.getUpgradePathItems(upgradeId);
                  if(users.database.results.next())
                     licenseId = users.database.results.getInt("licenseid");

                  users.database.results.close();
                  if(licenseId < 1)
                  {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage(
                        "<br/>There are no licenses in the upgrade path.");
                  }
                  else
                  {
                     java.util.Calendar today = java.util.Calendar.getInstance();
                     today.set(java.util.Calendar.DAY_OF_MONTH,
                        today.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
                     String date = Database.formatDate(today);

                     // Get the list of user licenses to upgrade.
                     ArrayList<UpgradeLicensesServlet.UserLicense> list =
                        new ArrayList<UpgradeLicensesServlet.UserLicense>();
                     users.database.results = users.database.statement.executeQuery(
                        "SELECT userlicenseid, userid FROM licenses " +
                        "WHERE licenseid <> " + licenseId + " AND upgradeid = " +
                        upgradeId + " AND (upgradedate IS NULL OR upgradedate >= '" +
                        date + "')");
                     while(users.database.results.next())
                     {
                        list.add(new UpgradeLicensesServlet.UserLicense(
                           users.database.results.getInt("userlicenseid"),
                           users.database.results.getInt("userid")));
                     }

                     users.database.results.close();

                     // Upgrade the user licenses.
                     Iterator<UpgradeLicensesServlet.UserLicense> iter = list.iterator();
                     while(iter.hasNext())
                     {
                        UpgradeLicensesServlet.UserLicense license = iter.next();
                        UpgradeLicenseServlet.upgradeLicense(licenses, orders,
                           license.userId, license.licenseId, licenseId);
                     }

                     if(!list.isEmpty())
                        users.database.connection.commit();

                     message.setResult(ResultMessage.Success);
                     message.addMessage(
                        list.isEmpty() ?
                        "<br/>There are no licenses that need to be upgraded." :
                        "<br/>" + list.size() + "license(s) upgraded.");

                     // Send an email to each of the users.
                     int succeeded = 0;
                     int failed = 0;
                     csimsoft.Email messenger = new csimsoft.Email();
                     iter = list.iterator();
                     while(iter.hasNext())
                     {
                        UpgradeLicensesServlet.UserLicense license = iter.next();
                        try
                        {
                           String email = users.getUserEmail(license.userId);
                           if(email == null)
                              ++failed;
                           else
                           {
                              Exception e = messenger.sendUpgradeMessage(email,
                                 license.licenseId);
                              if(e == null)
                                 ++succeeded;
                              else
                              {
                                 ++failed;
                                 break;
                              }
                           }
                        }
                        catch(SQLException sqle3)
                        {
                           ++failed;
                        }
                     }

                     if(succeeded > 0)
                        message.addMessage("<br/>" + succeeded + " email(s) sent.");

                     if(failed > 0)
                     {
                        message.addMessage("<br/>Failed to send " + failed +
                           " email(s).");
                     }
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
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

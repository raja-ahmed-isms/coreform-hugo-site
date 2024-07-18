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



public class ChangeUserUpgradeServlet extends HttpServlet
{

   public ChangeUserUpgradeServlet()
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
               "The license upgrade path has been successfully changed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to change the license upgrade path.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to change the license upgrade path." +
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
      int licenseId = 0;
      try
      {
         licenseId = Integer.parseInt(request.getParameter("license"));
      }
      catch(Exception e)
      {
      }

      int upgradeId = -1;
      try
      {
         upgradeId = Integer.parseInt(request.getParameter("upgrade"));
      }
      catch(Exception e)
      {
      }

      boolean changePath = upgradeId > -1;
      boolean changeDate = false;
      String expiration = null;
      ParameterList params = new ParameterList();
      params.setParameters(request);
      if(params.hasParameter("year"))
      {
         changeDate = true;
         if("yes".equals(request.getParameter("expiration")))
         {
            try
            {
               int year = Integer.parseInt(request.getParameter("year"));
               int month = Integer.parseInt(request.getParameter("month")) - 1;
               java.util.Calendar date = java.util.Calendar.getInstance();

               date.set(year, month, 1);
               date.set(java.util.Calendar.DAY_OF_MONTH,
                  date.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
               expiration = Database.formatDate(date);
            }
            catch(Exception nfe)
            {
               changeDate = false;
            }
         }
      }
      else if(upgradeId == 0)
         changeDate = true;

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ChangeUserUpgradeServlet.Message message = new ChangeUserUpgradeServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is logged in.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      boolean isAdmin = user.isAdministrator();
      boolean isDistributor = user.isDistributor();
      if(!LoginServlet.isUserLoggedIn(session))
         message.setResult(ResultMessage.LoginError);
      else if(!isAdmin && !isDistributor)
         message.setResult(ResultMessage.NotAuthorized);
      else if(licenseId < 1 || (!changePath && !changeDate))
      {
         page = "/clientlicenses.jsp";
         message.setResult(ResultMessage.Failure);
      }
      else
      {
         page = "/clientlicense.jsp?license=" + licenseId;
         DatabaseUsers users = new DatabaseUsers();
         DatabaseLicenses licenses = new DatabaseLicenses(users.database);
         try
         {
            users.database.connect();
            try
            {
               // Make sure the license id is valid. Also, make sure the
               // distributor can access the license.
               int userId = licenses.getUserLicenseOwner(licenseId);
               if(userId < 1)
               {
                  message.setResult(ResultMessage.Failure);
                  message.addMessage(
                     "<br />The given license id is not valid.");
               }
               else if(isAdmin || users.isDistributor(userId, user.getUserId()))
               {
                  String update = "UPDATE licenses SET ";
                  if(changePath)
                  {
                     update += "upgradeid = ? ";
                     if(changeDate)
                        update += ", ";
                  }

                  if(changeDate)
                  {
                     if(expiration == null)
                        update += "upgradedate = NULL ";
                     else
                        update += "upgradedate = ? ";
                  }

                  update += "WHERE userlicenseid = ?";
                  PreparedStatement statement =
                     users.database.connection.prepareStatement(update);
                  users.database.addStatement(statement);

                  int index = 1;
                  if(changePath)
                  {
                     statement.setInt(index, upgradeId);
                     ++index;
                  }

                  if(changeDate && expiration != null)
                  {
                     statement.setString(index, expiration);
                     ++index;
                  }

                  statement.setInt(index, licenseId);
                  statement.executeUpdate();
                  message.setResult(ResultMessage.Success);
                  if(changePath)
                     message.addMessage("<br />The new path has been set.");
                  if(changeDate)
                     message.addMessage("<br />The expiration date has been changed.");
               }
               else
               {
                  page = "/clientlicenses.jsp";
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

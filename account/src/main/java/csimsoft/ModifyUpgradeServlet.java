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


public class ModifyUpgradeServlet extends HttpServlet
{

   public ModifyUpgradeServlet()
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
               "The upgrade path has been successfully modified.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The new upgrade path name already exists.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to modify the upgrade path.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to modify an upgrade path.<br />" +
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
      String upgradeName = request.getParameter("upgradename");
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
      ModifyUpgradeServlet.Message message = new ModifyUpgradeServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to modify an upgrade path.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in as an administrator to modify an upgrade path.");
      }
      else if(!user.isAdministrator())
         message.setResult(ResultMessage.NotAuthorized);
      else if(upgradeId < 1 || upgradeName == null || upgradeName.isEmpty())
      {
         page = "/allupgrades.jsp";
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />Missing required parameters.");
      }
      else
      {
         page = "/allupgrades.jsp";
         if(upgradeName.length() > 64)
            upgradeName = upgradeName.substring(0, 64);

         DatabaseLicenses licenses = new DatabaseLicenses();
         try
         {
            licenses.database.connect();
            try
            {
               // Make sure the group name is not a duplicate.
               if(licenses.isUpgradeInDatabase(upgradeName))
               {
                  message.setResult(ResultMessage.Exists);
                  page = "/upgradeinfo.jsp?upgrade=" + upgradeId;
               }
               else
               {
                  PreparedStatement statement =
                     licenses.database.connection.prepareStatement(
                     "UPDATE upgradepaths SET upgradename = ? WHERE upgradeid = ?");
                  licenses.database.addStatement(statement);
                  statement.setString(1, upgradeName);
                  statement.setInt(2, upgradeId);
                  statement.executeUpdate();
                  message.setResult(ResultMessage.Success);
               }
            }
            catch(SQLException sqle2)
            {
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

         licenses.database.cleanup();
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

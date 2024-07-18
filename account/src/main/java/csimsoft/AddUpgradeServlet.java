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


public class AddUpgradeServlet extends HttpServlet
{

   public AddUpgradeServlet()
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
               "The new upgrade path has been successfully added.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to add the new upgrade path.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The specified upgrade path already exists.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to add a new upgrade path.<br />" +
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

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      AddUpgradeServlet.Message message = new AddUpgradeServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin in order to add a new upgrade path.
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!user.isAdministrator())
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
         return;
      }

      // Get the parameters from the request.
      String upgradeName = request.getParameter("upgradename");

      // Make sure the required parameters are present.
      if(upgradeName == null || upgradeName.isEmpty())
      {
         response.sendRedirect(response.encodeRedirectURL(
            contextPath + "/addupgrade.jsp"));
         return;
      }

      if(upgradeName.length() > 64)
         upgradeName = upgradeName.substring(0, 64);

      String page = "/allupgrades.jsp";
      DatabaseLicenses licenses = new DatabaseLicenses();
      try
      {
         licenses.database.connect();
         try
         {
            // Make sure the upgrade name is new.
            if(licenses.isUpgradeInDatabase(upgradeName))
            {
               message.setResult(ResultMessage.Exists);
               page = "/addupgrade.jsp";
            }
            else
            {
               // Get the next upgrade id from the database.
               int nextId = licenses.database.getNextId("upgradecount", "upgradeid");

               PreparedStatement update = licenses.database.connection.prepareStatement(
                  "INSERT INTO upgradepaths (upgradeid, upgradename) VALUES (?, ?)");
               licenses.database.addStatement(update);
               update.setInt(1, nextId);
               update.setString(2, upgradeName);
               update.executeUpdate();
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
      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

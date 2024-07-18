/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.io.IOException;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class RemoveUpgradeServlet extends HttpServlet
{

   public RemoveUpgradeServlet()
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
               "The upgrade path has been successfully removed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to remove the upgrade path.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to remove an upgrade path.<br />" +
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
      RemoveUpgradeServlet.Message message = new RemoveUpgradeServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to remove an upgrade path.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in as an administrator to remove an upgrade path.");
      }
      else if(!user.isAdministrator())
         message.setResult(ResultMessage.NotAuthorized);
      else if(upgradeId < 1)
      {
         page = "/allupgrades.jsp";
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />The given upgrade path id is not valid.");
      }
      else
      {
         page = "/allupgrades.jsp";
         Database database = new Database();
         try
         {
            database.connect();
            database.connection.setAutoCommit(false);
            database.statement = database.connection.createStatement();
            try
            {
               // Remove the upgrade path from the database.
               database.statement.executeUpdate(
                  "UPDATE licenses SET upgradeid = 0 WHERE upgradeid = " + upgradeId);
               database.statement.executeUpdate(
                  "DELETE FROM upgradeitems WHERE upgradeid = " + upgradeId);
               database.statement.executeUpdate(
                  "DELETE FROM upgradepaths WHERE upgradeid = " + upgradeId);

               database.connection.commit();
               message.setResult(ResultMessage.Success);
            }
            catch(SQLException sqle2)
            {
               database.connection.rollback();
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

         database.cleanup();
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

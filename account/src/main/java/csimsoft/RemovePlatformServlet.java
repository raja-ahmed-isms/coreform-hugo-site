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


public class RemovePlatformServlet extends HttpServlet
{

   public RemovePlatformServlet()
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
               "The platform has been successfully removed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to remove the platform.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to remove a platform.<br />" +
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
      int platformId = 0;
      try
      {
         platformId = Integer.parseInt(request.getParameter("platform"));
      }
      catch(Exception e)
      {
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      RemovePlatformServlet.Message message = new RemovePlatformServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to remove a platform.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in as an administrator to remove a platform.");
      }
      else if(!user.isAdministrator())
         message.setResult(ResultMessage.NotAuthorized);
      else if(platformId < 1)
      {
         page = "/allplatforms.jsp";
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />The given platform id is not valid.");
      }
      else
      {
         page = "/allplatforms.jsp";
         Database database = new Database();
         try
         {
            database.connect();
            database.connection.setAutoCommit(false);
            database.statement = database.connection.createStatement();
            try
            {
               // Remove the platform from the database.
               database.statement.executeUpdate(
                  "DELETE FROM userplatforms WHERE platformid = " + platformId);
               database.statement.executeUpdate(
                  "DELETE FROM platformdownloads WHERE platformid = " + platformId);
               database.statement.executeUpdate(
                  "DELETE FROM platforms WHERE platformid = " + platformId);

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

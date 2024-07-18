/**
 * @(#)RemoveUniversityServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/6/29
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


public class RemoveUniversityServlet extends HttpServlet
{

   public RemoveUniversityServlet()
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
               "The university has been successfully removed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to remove the university.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to remove a university.");
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
      int universityId = 0;
      try
      {
         universityId = Integer.parseInt(request.getParameter("universityid"));
      }
      catch(Exception e)
      {
      }

      // Get the optional parameters.
      String pageNumber = request.getParameter("page");
      String optional = "";
      if(pageNumber != null && !pageNumber.isEmpty())
         optional = "?page=" + pageNumber;

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      RemoveUniversityServlet.Message message = new RemoveUniversityServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to remove a university.
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in as an administrator to remove universities.");
      }
      else if(!user.isAdministrator())
         message.setResult(ResultMessage.NotAuthorized);
      else if(universityId < 1)
      {
         page = "/universities.jsp";
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />The given university id is not valid.");
      }
      else
      {
         page = "/universities.jsp";
         DatabaseUsers users = new DatabaseUsers();
         DatabaseUniversities universities = new DatabaseUniversities(users.database);
         try
         {
            users.database.connect();
            users.database.connection.setAutoCommit(false);
            try
            {
               // Get the contact id(s).
               int adminId = 0;
               int techId = 0;
               universities.getContacts(universityId);
               if(users.database.results.next())
               {
                  adminId = users.database.results.getInt("adminid");
                  techId = users.database.results.getInt("techid");
               }

               users.database.results.close();

               // Remove the university from the database.
               users.database.statement.executeUpdate(
                  "DELETE FROM universities WHERE universityid = " + universityId);
               users.database.connection.commit();

               // Remove the contacts if they are not used elsewhere.
               if(adminId > 0)
                  users.removeUnusedContact(adminId);

               if(techId > 0 && techId != adminId)
                  users.removeUnusedContact(techId);

               users.database.connection.commit();
               message.setResult(ResultMessage.Success);
            }
            catch(SQLException sqle2)
            {
               users.database.connection.rollback();
               message.setResult(ResultMessage.Failure);
            }
         }
         catch(SQLException sqle)
         {
            message.setResult(ResultMessage.ResourceError);
         }
         catch(NamingException ne)
         {
            message.setResult(ResultMessage.ResourceError);
         }

         users.database.cleanup();
      }

      response.sendRedirect(response.encodeRedirectURL(
         request.getContextPath() + page + optional));
   }
}
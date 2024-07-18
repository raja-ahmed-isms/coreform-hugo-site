/**
 * @(#)RemoveUniversityContactServlet.java
 *
 *
 * @author Mark richardson
 * @version 1.00 2011/8/2
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


public class RemoveUniversityContactServlet extends HttpServlet
{

   public RemoveUniversityContactServlet()
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
               "The contact has been removed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to remove the contact.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to access this information.");
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
      // Get the result message object for the session.
      HttpSession session = request.getSession();
      RemoveUniversityContactServlet.Message message =
         new RemoveUniversityContactServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is logged in.
      String page = "/universityinfo.jsp";
      String contextPath = request.getContextPath();
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in to remove the contact.");
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Get the user from the session.
      UserInfo user = LoginServlet.getUserInfo(session);
      boolean isAdmin = user.isAdministrator();
      message.Admin = isAdmin;

      // Get the request parameters.
      int contactType = DatabaseUniversities.getContactType(
         request.getParameter("contact"));
      int universityId = 0;
      try
      {
         universityId = Integer.parseInt(request.getParameter("universityid"));
         if(isAdmin)
            page += "?universityid=" + universityId;
      }
      catch(Exception e)
      {
      }

      if(universityId < 1 || contactType == 0)
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      DatabaseUsers users = new DatabaseUsers();
      DatabaseUniversities universities = new DatabaseUniversities(users.database);
      try
      {
         users.database.connect();
         users.database.connection.setAutoCommit(false);
         try
         {
            // Make sure the user is authorized to remove the contact.
            if(!isAdmin && users.getUniversityId(user.getUserId()) != universityId)
               message.setResult(ResultMessage.NotAuthorized);
            else
            {
               // Remove the contact.
               universities.setContact(universityId, 0, contactType);
               users.database.connection.commit();
               message.setResult(ResultMessage.Success);
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
      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}
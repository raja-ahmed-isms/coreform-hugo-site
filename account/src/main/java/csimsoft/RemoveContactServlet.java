/**
 * @(#)RemoveContactServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/6/30
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


public class RemoveContactServlet extends HttpServlet
{

   public RemoveContactServlet()
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
               "The contact information has been successfully removed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to remove the contact information.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to remove a contact.");
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
      int contactId = 0;
      try
      {
         contactId = Integer.parseInt(request.getParameter("contactid"));
      }
      catch(Exception e)
      {
      }

      // Get the optional parameters.
      ParameterBuilder params = new ParameterBuilder();
      params.add("page", request.getParameter("page"));

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      RemoveContactServlet.Message message = new RemoveContactServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is logged in.
      // The user role must be admin to remove a contact.
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in as an administrator to remove contacts.");
      }
      else if(!user.isAdministrator())
         message.setResult(ResultMessage.NotAuthorized);
      else if(contactId < 1)
      {
         page = "/allcontacts.jsp";
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />The given contact id is not valid.");
      }
      else
      {
         // Make sure the contact id is valid.
         page = "/allcontacts.jsp";
         DatabaseUniversities universities = new DatabaseUniversities();
         try
         {
            universities.database.connect();
            universities.database.connection.setAutoCommit(false);
            try
            {
               universities.removeContact(contactId);
               universities.database.statement.executeUpdate(
                  "DELETE FROM contacts WHERE contactid = " + contactId);

               universities.database.connection.commit();
               message.setResult(ResultMessage.Success);
            }
            catch(SQLException sqle2)
            {
               universities.database.connection.rollback();
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

         universities.database.cleanup();
      }

      response.sendRedirect(response.encodeRedirectURL(
         request.getContextPath() + page + params));
   }
}
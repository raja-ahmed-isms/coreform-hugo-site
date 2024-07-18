/**
 * @(#)AddLicenseGroupServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/4/15
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


public class AddLicenseGroupServlet extends HttpServlet
{

   public AddLicenseGroupServlet()
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
               "The new license group has been successfully added.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to add the new license group.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The specified license group already exists.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to add a new license group.<br />" +
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
      AddLicenseGroupServlet.Message message = new AddLicenseGroupServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin in order to add a new license group.
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!user.isAdministrator())
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
         return;
      }

      // Get the parameters from the request.
      String groupName = request.getParameter("groupname");

      // Make sure the required parameters are present.
      if(groupName == null || groupName.isEmpty())
      {
         response.sendRedirect(response.encodeRedirectURL(
            contextPath + "/addgroup.jsp"));
         return;
      }

      if(groupName.length() > 64)
         groupName = groupName.substring(0, 64);

      String page = "/allproducts.jsp";
      DatabaseLicenses licenses = new DatabaseLicenses();
      try
      {
         licenses.database.connect();
         try
         {
            // Make sure the license group name is new.
            if(licenses.isLicenseGroupInDatabase(groupName))
            {
               message.setResult(ResultMessage.Exists);
               page = "/addgroup.jsp";
            }
            else
            {
               // Get the next group id from the database.
               int nextId = licenses.database.getNextId("licensegroupcount", "groupid");

               PreparedStatement update = licenses.database.connection.prepareStatement(
                  "INSERT INTO licensegroups (groupid, groupname) VALUES (?, ?)");
               licenses.database.addStatement(update);
               update.setInt(1, nextId);
               update.setString(2, groupName);
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
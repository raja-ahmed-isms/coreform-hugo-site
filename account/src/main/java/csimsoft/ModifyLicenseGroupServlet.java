/**
 * @(#)ModifyLicenseGroupServlet.java
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


public class ModifyLicenseGroupServlet extends HttpServlet
{

   public ModifyLicenseGroupServlet()
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
               "The license group has been successfully modified.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The new license group name already exists.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to modify the license group.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to modify a license group.<br />" +
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
      String groupName = request.getParameter("groupname");
      int groupId = 0;
      try
      {
         groupId = Integer.parseInt(request.getParameter("group"));
      }
      catch(Exception e)
      {
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ModifyLicenseGroupServlet.Message message = new ModifyLicenseGroupServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to modify a license group.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in as an administrator to modify a license group.");
      }
      else if(!user.isAdministrator())
         message.setResult(ResultMessage.NotAuthorized);
      else if(groupId < 1 || groupName == null || groupName.isEmpty())
      {
         page = "/allproducts.jsp";
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />Missing required parameters.");
      }
      else
      {
         page = "/allproducts.jsp";
         if(groupName.length() > 64)
            groupName = groupName.substring(0, 64);

         DatabaseLicenses licenses = new DatabaseLicenses();
         try
         {
            licenses.database.connect();
            try
            {
               // Make sure the group name is not a duplicate.
               if(licenses.isLicenseGroupInDatabase(groupName))
               {
                  message.setResult(ResultMessage.Exists);
                  page = "/groupinfo.jsp?group=" + groupId;
               }
               else
               {
                  PreparedStatement statement =
                     licenses.database.connection.prepareStatement(
                     "UPDATE licensegroups SET groupname = ? WHERE groupid = ?");
                  licenses.database.addStatement(statement);
                  statement.setString(1, groupName);
                  statement.setInt(2, groupId);
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
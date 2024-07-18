/**
 * @(#)AddLicenseServlet.java
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


public class AddLicenseServlet extends HttpServlet
{

   public AddLicenseServlet()
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
               "The new license has been successfully added.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to add the new license.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The specified license already exists.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to add a new license.<br />" +
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
      AddLicenseServlet.Message message = new AddLicenseServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin in order to add a new license.
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      AcctLogger.get().info("User id: " + user.getUserId());
      if(!user.isAdministrator())
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
         return;
      }

      // Get the parameters from the request.
      String licenseName = request.getParameter("licensename");
      String version = request.getParameter("version");
      int processes = -1;
      try
      {
         processes = Integer.parseInt(request.getParameter("pcount"));
      }
      catch(Exception e)
      {
      }

      if(processes < 0)
         processes = 0;

      // Make sure the required parameters are present.
      if(licenseName == null || licenseName.isEmpty() ||
         version == null || version.isEmpty())
      {
         response.sendRedirect(response.encodeRedirectURL(
            contextPath + "/addlicense.jsp"));
         return;
      }

      if(licenseName.length() > 64)
         licenseName = licenseName.substring(0, 64);

      if(version.length() > 16)
         version = version.substring(0, 16);

      String page = "/allproducts.jsp";
      DatabaseLicenses licenses = new DatabaseLicenses();
      try
      {
         licenses.database.connect();
         try
         {
            // Make sure the license name is new.
            if(licenses.isLicenseInDatabase(licenseName))
            {
               message.setResult(ResultMessage.Exists);
               page = "/addlicense.jsp";
            }
            else
            {
               // Get the next license id from the database.
               int nextId = licenses.database.getNextId("licensecount", "licenseid");

               PreparedStatement update = licenses.database.connection.prepareStatement(
                  "INSERT INTO licenseproducts (licenseid, licensename, version, " +
                  "allowedinst) VALUES (?, ?, ?, ?)");
               licenses.database.addStatement(update);
               update.setInt(1, nextId);
               update.setString(2, licenseName);
               update.setString(3, version);
               update.setInt(4, processes);
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
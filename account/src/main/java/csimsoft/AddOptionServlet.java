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


public class AddOptionServlet extends HttpServlet
{

   public AddOptionServlet()
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
               "The new product option has been successfully added.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to add the new product option.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The specified product option already exists.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to add a new product option.<br />" +
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
      AddOptionServlet.Message message = new AddOptionServlet.Message(
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
      String optionName = request.getParameter("optionname");
      String optionKey = request.getParameter("optionkey");

      // Make sure the required parameters are present.
      if(optionName == null || optionName.isEmpty() ||
         optionKey == null || optionKey.isEmpty())
      {
         response.sendRedirect(response.encodeRedirectURL(
            contextPath + "/addoption.jsp"));
         return;
      }

      if(optionName.length() > 64)
         optionName = optionName.substring(0, 64);

      if(optionKey.length() > 32)
         optionKey = optionKey.substring(0, 32);

      String page = "/alloptions.jsp";
      DatabaseLicenses licenses = new DatabaseLicenses();
      try
      {
         licenses.database.connect();
         try
         {
            // Make sure the option name is new.
            if(licenses.isOptionInDatabase(optionName))
            {
               message.setResult(ResultMessage.Exists);
               page = "/addoption.jsp";
            }
            else
            {
               // Get the next option id from the database.
               int nextId = licenses.database.getNextId("optioncount", "optionid");

               PreparedStatement update = licenses.database.connection.prepareStatement(
                  "INSERT INTO options (optionid, optionname, optionkey) " +
                  "VALUES (?, ?, ?)");
               licenses.database.addStatement(update);
               update.setInt(1, nextId);
               update.setString(2, optionName);
               update.setString(3, optionKey);
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

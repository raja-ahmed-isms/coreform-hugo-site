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


public class ModifyOptionServlet extends HttpServlet
{

   public ModifyOptionServlet()
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
               "The product option has been successfully modified.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The new product option name already exists.");
         }
         else if(result == ResultMessage.DoesNotExist)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The given product option does not exist.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to modify the product option.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to modify a product option.<br />" +
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
      String optionName = request.getParameter("optionname");
      String optionKey = request.getParameter("optionkey");
      int optionId = 0;
      try
      {
         optionId = Integer.parseInt(request.getParameter("option"));
      }
      catch(Exception e)
      {
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ModifyOptionServlet.Message message = new ModifyOptionServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to modify a license group.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in as an administrator to modify a option.");
      }
      else if(!user.isAdministrator())
         message.setResult(ResultMessage.NotAuthorized);
      else if(optionId < 1 || optionName == null || optionName.isEmpty() ||
         optionKey == null || optionKey.isEmpty())
      {
         page = "/alloptions.jsp";
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />Missing required parameters.");
      }
      else
      {
         page = "/alloptions.jsp";
         if(optionName.length() > 64)
            optionName = optionName.substring(0, 64);

         if(optionKey.length() > 32)
            optionKey = optionKey.substring(0, 32);

         DatabaseLicenses licenses = new DatabaseLicenses();
         try
         {
            licenses.database.connect();
            try
            {
               // Get the current option name from the database.
               String currentName = null;
               licenses.getOptionInfo(optionId);
               if(licenses.database.results.next())
                  currentName = licenses.database.results.getString("optionname");

               licenses.database.results.close();

               // Make sure the option name is not a duplicate.
               if(currentName == null)
                  message.setResult(ResultMessage.DoesNotExist);
               if(!currentName.equals(optionName) &&
                  licenses.isOptionInDatabase(optionName))
               {
                  message.setResult(ResultMessage.Exists);
                  page = "/optioninfo.jsp?option=" + optionId;
               }
               else
               {
                  PreparedStatement statement =
                     licenses.database.connection.prepareStatement(
                     "UPDATE options SET optionname = ?, optionkey = ? " +
                     "WHERE optionid = ?");
                  licenses.database.addStatement(statement);
                  statement.setString(1, optionName);
                  statement.setString(2, optionKey);
                  statement.setInt(3, optionId);
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

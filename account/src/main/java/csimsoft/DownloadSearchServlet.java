/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class DownloadSearchServlet extends HttpServlet
{

   public DownloadSearchServlet()
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
         if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to download the specified user search.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to download the user search.");
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
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
   {
      // Get the result message object for the session.
      HttpSession session = request.getSession();
      DownloadSearchServlet.Message message = new DownloadSearchServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure a search is specified.
      String page = "/index.jsp";
      String contextPath = request.getContextPath();
      String search = request.getParameter("search");
      if(search == null || search.isEmpty())
      {
         message.setResult(ResultMessage.Failure);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Check the login status for the user.
      boolean isLoggedIn = LoginServlet.isUserLoggedIn(session);

      // See if the user is an administrator or distributor.
      boolean isAdmin = false;
      boolean isDistributor = false;
      UserInfo user = null;
      if(isLoggedIn)
      {
         user = LoginServlet.getUserInfo(session);
         isAdmin = user.isAdministrator();
         isDistributor = user.isDistributor();
         if(isAdmin)
            page = "/userlist.jsp";
         else if(isDistributor)
            page = "/distusers.jsp";
      }

      message.Admin = isAdmin;
      if(!isAdmin && !isDistributor)
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Make sure the search is valid.
      UserSearch parser = new UserSearch();
      if(!parser.setSearch(search))
      {
         message.setResult(ResultMessage.Failure);
         message.addMessage("<br/>Error parsing the search query.");
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      DatabaseUsers users = new DatabaseUsers();
      try
      {
         users.database.connect();
         if(!isAdmin)
         {
            boolean problem = false;
            try
            {
               // Make sure the distributor is authorized.
               int groupId = users.getDistibutorGroup(user.getUserId());
               problem = !UserSearch.isDistributorValid(search.toLowerCase(), groupId);
            }
            catch(SQLException sqle2)
            {
               problem = true;
            }

            if(problem)
            {
               users.database.cleanup();
               message.setResult(ResultMessage.NotAuthorized);
               response.sendRedirect(response.encodeRedirectURL(contextPath + page));
               return;
            }
         }

         // Get the output stream and set the content headers.
         ServletOutputStream stream = response.getOutputStream();
         response.setContentType("text/csv");
         response.setHeader("Content-Disposition",
            "attachment; filename=\"usersearch.csv\"");

         try
         {
            parser.executeQuery(users.database);
            while(users.database.results.next())
            {
               stream.print(users.database.results.getString("lastname"));
               stream.print(",");
               stream.print(users.database.results.getString("firstname"));
               stream.print(",");
               stream.print(users.database.results.getString("email"));
               stream.print(",");
               stream.print(users.database.results.getString("company"));
               stream.print(",");
               stream.print(users.database.results.getString("phone"));
               stream.print(",");
               stream.print(users.database.results.getString("country"));
               stream.println();
            }

            users.database.results.close();
         }
         catch(SQLException sqle3)
         {
         }
      }
      catch(java.sql.SQLException sqle)
      {
         users.database.cleanup();
         message.setResult(ResultMessage.ResourceError);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }
      catch(javax.naming.NamingException ne)
      {
         users.database.cleanup();
         message.setResult(ResultMessage.ResourceError);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }
      catch(IOException ioe)
      {
         users.database.cleanup();
         throw ioe;
      }

      users.database.cleanup();
   }
}

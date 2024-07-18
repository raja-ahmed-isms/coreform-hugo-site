/**
 * @(#)CleanupFilesServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/6/8
 */

package csimsoft;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class CleanupFilesServlet extends HttpServlet
{
   public CleanupFilesServlet()
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
               "The file system has been successfully cleaned.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to clean up the file system.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to clean up the file system.<br />" +
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
      // Get the result message object for the session.
      HttpSession session = request.getSession();
      CleanupFilesServlet.Message message = new CleanupFilesServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to alter the database.
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session) || !user.isAdministrator())
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
         return;
      }

      // Get the download diectory.
      String downloads = DownloadServlet.getDirectory(session, request);

      String page = "/alldownloads.jsp";
      Database database = new Database();
      try
      {
         // Get the id of the root directory.
         int rootId = 0;
         String rootString = request.getParameter("rootid");
         if(rootString != null && !rootString.isEmpty())
         {
            rootId = Integer.parseInt(rootString);
            page += "?rootid=" + rootId;
         }

         // Connect to the database.
         database.connect();
         try
         {
            // Build the tree of files in the database.
            ProductList products = new ProductList();
            products.setProducts(database, rootId);
            ProductListGroup root = products.getRoot();

            // Get the tree of files in the directory.
            FileCleanupTree tree = new FileCleanupTree();
            rootString = root.getFullLocation();
            if(rootString == null)
               rootString = "";
            else if(!rootString.isEmpty())
               rootString = File.separator + rootString.replace('/', File.separatorChar);

            tree.buildFileTree(downloads + rootString);

            // Remove the files from the file tree that match the ones
            // in the database.
            ProductListItem item = products.getLastItem();
            while(item != null)
            {
               String path = item.getFullLocation().replace('/', File.separatorChar);
               tree.matchPath(downloads + File.separator + path);
               item = products.getPreviousItem(item);
            }

            // Remove the left over files from the file system.
            int numDeleted = tree.getNumberOfFiles();
            int remaining = tree.removeFiles();
            numDeleted -= remaining;

            message.setResult(ResultMessage.Success);
            message.addMessage("<br />Deleted " + numDeleted + " files.");
            if(remaining > 0)
               message.addMessage("<br />" + remaining + " files remaining.");
         }
         catch(SQLException sqle2)
         {
            message.setResult(ResultMessage.Failure);
            message.addException(sqle2);
         }
      }
      catch(NumberFormatException nfe)
      {
         message.setResult(ResultMessage.Failure);
         message.addException(nfe);
      }
      catch(SecurityException se)
      {
         message.setResult(ResultMessage.Failure);
         message.addException(se);
      }
      catch(SQLException sqle)
      {
         message.setResult(ResultMessage.ResourceError);
         message.addException(sqle);
      }
      catch(javax.naming.NamingException ne)
      {
         message.setResult(ResultMessage.ResourceError);
         message.addException(ne);
      }

      database.cleanup();

      // Redirect the response back to the account page.
      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

/**
 * @(#)UpdateFilesServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/2/23
 */

package csimsoft;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class UpdateFilesServlet extends HttpServlet
{

   public UpdateFilesServlet()
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
               "The download files have been successfully updated.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to update the download files.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to update the download files.<br />" +
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

   public static class ProductLocation
   {
      public int ProductId = 0;
      public String Location = null;

      public ProductLocation()
      {
      }

      public ProductLocation(int id, String location)
      {
         this.ProductId = id;
         this.Location = location;
      }
   }

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
   {
      // Get the result message object for the session.
      HttpSession session = request.getSession();
      UpdateFilesServlet.Message message = new UpdateFilesServlet.Message(
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

      Database database = new Database();
      try
      {
         database.connect();
         database.statement = database.connection.createStatement();
         try
         {
            // Get the list of products to move. Move the file to the
            // groupname folder.
            int failedToMove = 0;
            ArrayList<ProductLocation> products = new ArrayList<ProductLocation>();
            database.results = database.statement.executeQuery(
               "SELECT productid, product, groupname, location FROM products");
            while(database.results.next())
            {
               int productId = database.results.getInt("productid");
               String product = database.results.getString("product");
               String groupName = database.results.getString("groupname");
               String location = database.results.getString("location");
               String[] path = location.split("/");
               if(path == null || path.length < 3)
                  continue;

               File original = new File(downloads + File.separator +
                  location.replace('/', File.separatorChar));
               location = path[0] + "/" + path[path.length - 1];
               File newFile = new File(downloads + File.separator +
                  location.replace('/', File.separatorChar));
               try
               {
                  if(original.renameTo(newFile))
                     products.add(new ProductLocation(productId, location));
                  else
                     failedToMove++;
               }
               catch(Exception e)
               {
                  failedToMove++;
               }
            }

            database.results.close();

            // Fix the product locations in the database.
            Iterator<ProductLocation> iter = products.iterator();
            while(iter.hasNext())
            {
               ProductLocation info = iter.next();
               database.statement.executeUpdate(
                  "UPDATE products SET location = '" + info.Location +
                  "' WHERE productid = " + info.ProductId);
            }

            // Clean up the left over folders.
            int failedToDelete = 0;
            if(!products.isEmpty())
            {
               ArrayList<File> cleanup = new ArrayList<File>();
               cleanup.add(new File(downloads));
               while(!cleanup.isEmpty())
               {
                  File check = cleanup.remove(cleanup.size() - 1);
                  File[] children = check.listFiles();
                  for(int i = 0; i < children.length; ++i)
                  {
                     if(children[i].isDirectory())
                        cleanup.add(children[i]);
                  }

                  if(children.length == 0)
                  {
                     try
                     {
                        if(!check.delete())
                           failedToDelete++;
                     }
                     catch(Exception e)
                     {
                        failedToDelete++;
                     }
                  }
               }
            }

            message.setResult(ResultMessage.Success);
            message.addMessage("<br />Moved " + products.size() + " files.");
            if(failedToMove > 0)
               message.addMessage("<br />Failed to move " + failedToMove + " files.");
            if(failedToDelete > 0)
               message.addMessage("<br />Failed to delete " + failedToDelete + " folders.");
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
      catch(javax.naming.NamingException ne)
      {
         message.setResult(ResultMessage.ResourceError);
         message.addException(ne);
      }

      database.cleanup();

      // Redirect the response back to the account page.
      response.sendRedirect(response.encodeRedirectURL(contextPath + "/alldownloads.jsp"));
   }
}
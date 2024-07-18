/**
 * @(#)ChangeDownloadsPathServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/3/19
 */

package csimsoft;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class ChangeDownloadsPathServlet extends HttpServlet
{

   public ChangeDownloadsPathServlet()
   {
   }

   private static class Message implements ResultMessageAdapter
   {
      public ResultMessage Report = null;
      public boolean Admin = false;
      public int Operation = RenameProductServlet.RenameProduct;

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
               "The downloads path has been successfully changed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to change the downloads path.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The specified directory already exists.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to change the downloads path.<br />" +
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
      ChangeDownloadsPathServlet.Message message =
         new ChangeDownloadsPathServlet.Message(LoginServlet.getResultMessage(session));

      // The user role must be admin in order to add a new product.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!user.isAdministrator())
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Get the parameters.
      page = "/alldownloads.jsp";
      String downloads = request.getParameter("downloads");
      if(downloads == null || downloads.isEmpty())
      {
         message.setResult(ResultMessage.Failure);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      char toReplace = File.separatorChar == '/' ? '\\' : '/';
      downloads = downloads.replace(toReplace, File.separatorChar);
      File folder = new File(downloads);

      // Get the current path.
      String directory = DownloadServlet.getDirectory(session, request);
      File original = new File(directory);
      FolderMover mover = new FolderMover();
      DatabaseProducts products = new DatabaseProducts();
      try
      {
         if(!folder.isAbsolute())
         {
            message.setResult(ResultMessage.Failure);
            message.addMessage("<br />The path is not absolute.");
         }
         else if(folder.exists())
            message.setResult(ResultMessage.Exists);
         else
         {
            products.database.connect();

            // Move the files to the new directory and update the database.
            if(mover.moveFolder(message, original, folder))
            {
               try
               {
                  PreparedStatement statement = null;
                  String current = products.getDownloadDirectory();
                  if(current == null)
                  {
                     statement = products.database.connection.prepareStatement(
                        "INSERT INTO downloadpath (directory) VALUES (?)");
                  }
                  else
                  {
                     statement = products.database.connection.prepareStatement(
                        "UPDATE downloadpath SET directory = ?");
                  }

                  products.database.addStatement(statement);
                  statement.setString(1, downloads);
                  statement.executeUpdate();
                  message.setResult(ResultMessage.Success);
               }
               catch(SQLException sqle2)
               {
                  message.setResult(ResultMessage.Failure);
                  message.addException(sqle2);
               }
            }
         }
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
      catch(NamingException ne)
      {
         message.setResult(ResultMessage.ResourceError);
         message.addException(ne);
      }

      products.database.cleanup();
      if(message.Report.getResult() == ResultMessage.Success)
         mover.completeMove(message);
      else
         mover.revertMove(message);

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}
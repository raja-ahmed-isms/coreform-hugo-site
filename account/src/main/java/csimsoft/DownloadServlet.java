/**
 * @(#)DownloadServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/7/12
 */

package csimsoft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class DownloadServlet extends HttpServlet
{

   public DownloadServlet()
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
               "No file was specified to download.");
         }
         else if(result == ResultMessage.DoesNotExist)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The specified file does not exist.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to download the specified file.");
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

   public static class Range
   {
      private long First = -1;
      private long Last = -1;

      public Range()
      {
      }

      public Range(long first, long last)
      {
         this.First = first;
         this.Last = last;
      }

      public void setRange(long first, long last)
      {
         this.First = first;
         this.Last = last;
      }

      public void setFirst(long first)
      {
         this.First = first;
      }

      public long getFirst()
      {
         return this.First;
      }

      public void setLast(long last)
      {
         this.Last = last;
      }

      public long getLast()
      {
         return this.Last;
      }

      public long getLength()
      {
         return this.Last - this.First + 1;
      }
   }

   public static String getDirectory(HttpSession session, HttpServletRequest request)
   {
      // The directory should be in a location that is not accessible
      // by http. Then, the user will have to be logged in to access
      // the file.
      String directory = null;
      DatabaseProducts products = new DatabaseProducts();
      try
      {
         products.database.connect();
         directory = products.getDownloadDirectory();
      }
      catch(java.sql.SQLException sqle)
      {
      }
      catch(javax.naming.NamingException ne)
      {
      }

      products.database.cleanup();

      if(directory == null)
      {
         // Use a default path if the directory is not in the database.
         ServletContext context = session.getServletContext();
         return context.getRealPath(request.getContextPath()) + File.separator +
            "downloads";
      }
      else
         return directory;
   }

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
   {
      // Get the result message object for the session.
      HttpSession session = request.getSession();
      DownloadServlet.Message message = new DownloadServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure a file name is specified.
      String page = "/index.jsp";
      String contextPath = request.getContextPath();
      String fileName = request.getParameter("file");
      if(fileName == null || fileName.isEmpty())
      {
         message.setResult(ResultMessage.Failure);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Check the login status for the user.
      boolean isLoggedIn = LoginServlet.isUserLoggedIn(session);

      // See if the user is an administrator.
      boolean isAdmin = false;
      UserInfo user = null;
      if(isLoggedIn)
      {
         user = LoginServlet.getUserInfo(session);
         isAdmin = user.isAdministrator();
         if(isAdmin)
            page = "/alldownloads.jsp";
         else if(user.isDistributor())
            page = "/distributor.jsp";
      }

      message.Admin = isAdmin;
      if(!isAdmin)
      {
         boolean hasError = false;
         DatabaseProducts products = new DatabaseProducts();
         try
         {
            // See if the user is authorized to download the file.
            products.database.connect();
            int productId = products.getProductId(fileName);
            if(productId > 0)
            {
               if(!products.isOpenProduct(productId))
               {
                  if(!isLoggedIn)
                  {
                     message.setResult(ResultMessage.LoginError);
                     message.addMessage(
                        "Please log in to download the specified file.");
                     hasError = true;
                  }
                  else if(!products.isDownloadOk(user.getUserId(), productId))
                  {
                     message.setResult(ResultMessage.NotAuthorized);
                     hasError = true;
                  }
               }
            }
            else
            {
               message.setResult(ResultMessage.DoesNotExist);
               hasError = true;
            }
         }
         catch(java.sql.SQLException sqle)
         {
            message.setResult(ResultMessage.ResourceError);
         }
         catch(javax.naming.NamingException ne)
         {
            message.setResult(ResultMessage.ResourceError);
         }

         products.database.cleanup();
         if(hasError)
         {
            response.sendRedirect(response.encodeRedirectURL(contextPath + page));
            return;
         }
      }

      // Make sure the file name has the corrent file separator.
      if(File.separatorChar != '/')
         fileName = fileName.replace('/', File.separatorChar);

      // Get the download directory.
      String directory = DownloadServlet.getDirectory(session, request);
      File download = new File(directory + File.separator + fileName);
      if(!download.exists())
      {
         message.setResult(ResultMessage.DoesNotExist);
         message.addMessage("<br />" + fileName);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // See if there's a valid byte range request.
      long fileSize = download.length();
      DownloadServlet.Range range = null;
         //this.parseRange(request.getHeader("Range"), fileSize);

      try
      {
         // Open the file.
         FileInputStream input = new FileInputStream(download);
         OutputStream stream = response.getOutputStream();

         long numToRead = fileSize;
         if(range != null)
         {
            if(range.getFirst() > 0)
            {
               long numSkipped = input.skip(range.getFirst());
               if(numSkipped < range.getFirst())
                  range.setFirst(numSkipped);
            }

            numToRead = range.getLength();
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
         }

         // Set the response headers. Determine the type using the
         // file extension.
         if(fileName.endsWith(".pdf"))
            response.setContentType("application/pdf");
         else if(fileName.endsWith(".html") || fileName.endsWith(".htm"))
            response.setContentType("text/html");
         else if(fileName.endsWith(".xml"))
            response.setContentType("text/xml");
         else if(fileName.endsWith(".jar"))
            response.setContentType("application/x-java-archive");
         else if(fileName.endsWith(".zip"))
            response.setContentType("application/zip");
         else if(fileName.endsWith(".tar.gz") || fileName.endsWith(".tgz"))
            response.setContentType("application/x-gzip");
         else if(fileName.endsWith(".mp4"))
            response.setContentType("video/mp4");
         else
            response.setContentType("application/octet-stream");

         response.setContentLength((int)numToRead);
         //response.setHeader("Accept-Ranges", "bytes");
         response.setHeader("Content-Disposition",
            "attachment; filename=\"" + download.getName() + "\"");

         if(range != null)
            response.setHeader("Content-Range", this.getRange(range, fileSize));

         // Send the file in the response.
         long numRead = 0;
         int current = 0;
         int used = 0;
         byte[] buffer = new byte[1024];
         do
         {
            current = input.read(buffer, used, buffer.length - used);
            if(current > 0)
            {
               used += current;
               numRead += current;
               if(numRead > numToRead)
                  used -= numRead - numToRead;

               if(numRead >= numToRead)
                  current = -1;
            }

            if(used > buffer.length - 32 || (used > 0 && current < 0))
            {
               stream.write(buffer, 0, used);
               used = 0;
            }
         } while(current >= 0);

         input.close();
      }
      catch(FileNotFoundException fnfe)
      {
         message.setResult(ResultMessage.DoesNotExist);
         message.addException(fnfe);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
      }
   }

   private DownloadServlet.Range parseRange(String header, long fileSize)
   {
      if(header == null)
         return null;

      // Split the range type from the rest.
      String[] strings = header.split("=");
      if(strings.length != 2)
         return null;

      String unit = strings[0].trim();
      if(!unit.equalsIgnoreCase("bytes"))
         return null;

      // TODO: Add support for multiple ranges with multi-part response.
      String[] ranges = strings[1].split(",");
      if(ranges.length > 1)
         return null;

      // Get the range values.
      String[] values = ranges[0].split("-");
      if(values.length > 2 || values.length < 1)
         return null;

      try
      {
         String text = values[0].trim();
         long first = text.isEmpty() ? -1 : Long.parseLong(text);
         long last = -1;
         if(values.length > 1)
         {
            text = values[1].trim();
            if(!text.isEmpty())
               last = Long.parseLong(text);
         }
         else if(ranges[0].trim().startsWith("-"))
         {
            last = first;
            first = -1;
         }

         if(first < 0)
         {
            if(last < 0)
               return null;

            first = fileSize - last;
            last = fileSize - 1;
         }
         else if(last < 0)
            last = fileSize - 1;
         else if(last < first || last >= fileSize)
            return null;

         return new DownloadServlet.Range(first, last);
      }
      catch(Exception e)
      {
      }

      return null;
   }

   private String getRange(DownloadServlet.Range range, long total)
   {
      String contentRange = "";
      if(range.getFirst() >= 0)
         contentRange += range.getFirst();

      contentRange += "-";
      if(range.getLast() >= 0)
         contentRange += range.getLast();

      contentRange += "/" + total;
      return contentRange;
   }
}
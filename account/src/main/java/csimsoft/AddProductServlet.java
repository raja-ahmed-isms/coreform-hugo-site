/**
 * @(#)AddProductServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/4/23
 */

package csimsoft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


public class AddProductServlet extends HttpServlet
{

   public AddProductServlet()
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
         if(result == ResultMessage.Success)
         {
            this.Report.setType(ResultMessage.InfoMessage);
            this.Report.setMessage(
               "The new product has been successfully added.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to add the new product.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The specified product already exists.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to add a new product.<br />" +
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
      AddProductServlet.Message message = new AddProductServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin in order to add a new product.
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      boolean isAdmin = user.isAdministrator();
      boolean isDistributor = user.isDistributor();
      if(!isAdmin && !isDistributor)
         message.setResult(ResultMessage.NotAuthorized);

      String page = "/index.jsp";
      if(isAdmin)
         page = "/alldownloads.jsp";
      else if(isDistributor)
         page = "/distributor.jsp";

      boolean sendingXml = false;
      ParameterBuilder extra = new ParameterBuilder();
      if(ServletFileUpload.isMultipartContent(request))
      {
         // Get the download directory.
         String directory = null;
         if(isAdmin || isDistributor)
            directory = DownloadServlet.getDirectory(session, request);

         int parentId = 0;
         String name = null;
         String product = null;
         File download = null;
         DatabaseProducts products = new DatabaseProducts();
         try
         {
            if(isAdmin || isDistributor)
            {
               // Connect to the database.
               products.database.connect();
               products.database.connection.setAutoCommit(false);
            }

            // Create the apache upload handler.
            ServletFileUpload upload = new ServletFileUpload();

            // Parse the request.
            FileItemIterator iter = upload.getItemIterator(request);
            while(iter.hasNext())
            {
               FileItemStream item = iter.next();
               String parameter = item.getFieldName();
               InputStream stream = item.openStream();
               if(item.isFormField())
               {
                  String value =
                     org.apache.commons.fileupload.util.Streams.asString(stream);
                  if(parameter.equals("parentid"))
                  {
                     parentId = Integer.parseInt(value);
                     if(parentId < 0)
                        parentId = 0;
                  }
                  else if(parameter.equals("productname"))
                     name = value;
                  else if(parameter.equals("back"))
                  {
                     if(value != null && !value.isEmpty())
                        page = "/" + value;
                  }
                  else if(parameter.equals("xml"))
                  {
                     sendingXml = "yes".equalsIgnoreCase(value);
                     if(!isAdmin && !isDistributor)
                        break;
                  }
                  else
                     extra.add(parameter, value);
               }
               else if(parameter.equals("product"))
               {
                  if(!isAdmin && !isDistributor)
                     break;

                  // If the user is not an administrator, make sure the parent
                  // folder is accessible.
                  if(!isAdmin && !products.isDistributorProduct(
                     user.getUserId(), parentId))
                  {
                     message.setResult(ResultMessage.NotAuthorized);
                     break;
                  }

                  if(name == null || name.isEmpty())
                  {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage(
                        "<br />Undefined product name.");
                     break;
                  }

                  // The file name may contain the full path.
                  String [] pathList = item.getName().split("[/\\\\]");
                  String fileName = pathList[pathList.length - 1];
                  if(fileName == null || fileName.isEmpty())
                     continue;

                  // Find the parent path.
                  String parentPath = "";
                  if(parentId > 0)
                  {
                     parentPath = products.getProductLocation(parentId);
                     if(parentPath == null)
                     {
                        message.setResult(ResultMessage.Failure);
                        message.addMessage(
                           "<br />The parent folder id is not valid.");
                        break;
                     }

                     parentPath = parentPath.replace('/', File.separatorChar);
                     parentPath += File.separator;
                  }

                  // Add the parent path to the location.
                  product = fileName;
                  download = new File(directory + File.separator +
                     parentPath + fileName);
                  if(download.exists())
                  {
                     download = null;
                     message.setResult(ResultMessage.Exists);
                     message.addMessage(
                        "<br />Duplicate file name: " + parentPath + fileName);
                     break;
                  }

                  // Set up the directory path if needed.
                  download.getParentFile().mkdirs();

                  // Save the file.
                  FileOutputStream output = new FileOutputStream(download);
                  int current = 0;
                  int used = 0;
                  byte[] buffer = new byte[1024];
                  do
                  {
                     current = stream.read(buffer, used, buffer.length - used);
                     if(current > 0)
                        used += current;

                     if(used > buffer.length - 32 || (used > 0 && current < 0))
                     {
                        output.write(buffer, 0, used);
                        used = 0;
                     }
                  } while(current >= 0);

                  output.close();
               }
            }

            if(message.Report.getResult() == ResultMessage.NoResult)
            {
               // Make sure the necessary variables are present.
               if(product == null || product.isEmpty())
               {
                  message.setResult(ResultMessage.Failure);
                  message.addMessage("<br />Missing product file.");
               }
               else
               {
                  try
                  {
                     // Add the product to the database.
                     products.addProduct(message, parentId, name, product, false);
                     if(message.Report.getResult() == ResultMessage.NoResult)
                     {
                        products.database.connection.commit();
                        message.setResult(ResultMessage.Success);
                        message.addMessage("<br />" + product);
                     }
                  }
                  catch(SQLException sqle2)
                  {
                     products.database.connection.rollback();
                     message.setResult(ResultMessage.Failure);
                     message.addException(sqle2);
                  }

                  // Delete the file from the disk on failure.
                  if(message.Report.getResult() != ResultMessage.Success &&
                     download != null)
                  {
                     download.delete();
                  }
               }
            }
         }
         catch(FileUploadException fue)
         {
            message.setResult(ResultMessage.Failure);
            message.addException(fue);
         }
         catch(NumberFormatException nfe)
         {
            message.setResult(ResultMessage.Failure);
            message.addException(nfe);
         }
         catch(FileNotFoundException fnfe)
         {
            message.setResult(ResultMessage.Failure);
            message.addException(fnfe);
         }
         catch(IOException ioe)
         {
            message.setResult(ResultMessage.Failure);
            message.addException(ioe);
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

         products.database.cleanup();
      }
      else
      {
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            " The form encoding should be multipart/form-data.");
      }

      if(sendingXml)
         message.Report.sendXml(response);
      else
         response.sendRedirect(response.encodeRedirectURL(contextPath + page + extra));
   }
}
/**
 * @(#)UpdateProductServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/12/7
 */

package csimsoft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


public class UpdateProductServlet extends HttpServlet
{

   public UpdateProductServlet()
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
               "The product has been successfully updated.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to update the product.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to update a product.<br />" +
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
      UpdateProductServlet.Message message = new UpdateProductServlet.Message(
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
            directory = DownloadServlet.getDirectory(session, request) + File.separator;

         int productId = 0;
         String product = null;
         String original = null;
         File download = null;
         File oldFile = null;
         DatabaseProducts products = new DatabaseProducts();
         try
         {
            // Connect to the database.
            if(isAdmin || isDistributor)
               products.database.connect();

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
                  if(parameter.equals("productid"))
                     productId = Integer.parseInt(value);
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

                  // Make sure the product id was sent.
                  if(productId == 0)
                  {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage("<br />Undefined product id.");
                     break;
                  }

                  // If the user is not an administrator, make sure the product
                  // is accessible.
                  if(!isAdmin && !products.isDistributorProduct(user.getUserId(),
                     productId))
                  {
                     message.setResult(ResultMessage.NotAuthorized);
                     break;
                  }

                  // Get the current location from the database.
                  String location = null;
                  try
                  {
                     location = products.getProductLocation(productId);
                  }
                  catch(java.sql.SQLException sqle2)
                  {
                     message.setResult(ResultMessage.Failure);
                     message.addException(sqle2);
                     productId = 0;
                     break;
                  }

                  // Make sure the product exists.
                  if(location == null || location.isEmpty())
                  {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage("<br />The specified product does not exist.");
                     productId = 0;
                     break;
                  }

                  // The file name may contain the full path.
                  String[] pathList = item.getName().split("[/\\\\]");
                  product = pathList[pathList.length - 1];
                  if(product == null || product.isEmpty())
                     continue;

                  // Construct the new path.
                  pathList = location.split("[/\\\\]");
                  original = pathList[pathList.length - 1];
                  String path = "";
                  for(int i = 0; i < pathList.length - 1; i++)
                     path += pathList[i] + File.separator;

                  // Save the file.
                  download = new File(directory + path + product);
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

                  if(!product.equals(original))
                     oldFile = new File(directory + path + original);
               }
            }

            // Make sure the necessary variables are present.
            if(product == null || product.isEmpty())
            {
               if(productId != 0)
               {
                  message.setResult(ResultMessage.Failure);
                  message.addMessage("<br />Missing product file.");
               }
            }
            else if(product.equals(original))
            {
               message.setResult(ResultMessage.Success);
               message.addMessage("<br />" + product);
            }
            else
            {
               // Update the product location in the database.
               try
               {
                  PreparedStatement statement =
                     products.database.connection.prepareStatement(
                     "UPDATE products SET location = ? WHERE productid = ?");
                  products.database.addStatement(statement);
                  statement.setString(1, product);
                  statement.setInt(2, productId);
                  statement.executeUpdate();
                  message.setResult(ResultMessage.Success);
                  message.addMessage("<br />" + product);
               }
               catch(java.sql.SQLException sqle3)
               {
                  message.setResult(ResultMessage.Failure);
                  message.addException(sqle3);
               }

               if(message.Report.getResult() == ResultMessage.Success)
               {
                  // Remove the old file on success.
                  if(oldFile != null)
                  {
                     if(!oldFile.delete())
                        message.addMessage("<br />Unable to remove the old file.");
                  }
               }
               else if(download != null)
               {
                  // Remove the new file on failure.
                  if(!download.delete())
                     message.addMessage("<br />Unable to remove the uploaded file.");
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
         catch(SecurityException se)
         {
            message.setResult(ResultMessage.Failure);
            message.addException(se);
         }
         catch(java.sql.SQLException sqle)
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
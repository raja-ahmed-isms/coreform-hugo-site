/**
 * @(#)MoveProductServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/3/16
 */

package csimsoft;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class MoveProductServlet extends HttpServlet
{

   public MoveProductServlet()
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
               "The product has been successfully moved.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to move the product.");
         }
         else if(result == ResultMessage.DoesNotExist)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The given product does not exist.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to move the product because one already exists.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to access this information.");
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
      MoveProductServlet.Message message =
         new MoveProductServlet.Message(LoginServlet.getResultMessage(session));

      // The user role must be admin in order to add a new product.
      String page = "/index.jsp";
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      boolean isAdmin = user.isAdministrator();
      boolean isDistributor = user.isDistributor();
      if(!isAdmin && !isDistributor)
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      page = "/alldownloads.jsp";
      ParameterBuilder extra = new ParameterBuilder();

      // Get the parameters.
      ParameterList params = new ParameterList();
      params.setParameters(request);
      params.removeParameter("product");
      params.removeParameter("parentid");
      params.removeParameter("back");
      String back = request.getParameter("back");
      if(back != null && !back.isEmpty())
         page = "/" + back;

      for(int i = 0; i < params.getParameterCount(); i++)
      {
         String paramName = params.getParameterName(i);
         extra.add(paramName, request.getParameter(paramName));
      }

      int productId = 0;
      int parentId = 0;
      try
      {
         productId = Integer.parseInt(request.getParameter("productid"));
         parentId = Integer.parseInt(request.getParameter("product"));
      }
      catch(Exception e)
      {
      }

      // Make sure the necessary parameters are present.
      if(productId == 0 || parentId < 0)
      {
         message.setResult(ResultMessage.Failure);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page + extra));
         return;
      }

      // Get the download directory.
      String directory = DownloadServlet.getDirectory(session, request) +
         File.separator;

      DatabaseProducts products = new DatabaseProducts();
      try
      {
         File file = null;
         File original = null;
         products.database.connect();
         try
         {
            // If the user is not an administrator, make sure the product
            // is accessible.
            if(!isAdmin && !products.isDistributorProduct(user.getUserId(), productId))
               message.setResult(ResultMessage.NotAuthorized);
            else
            {
               // Get the product information.
               products.getProductInfo(productId);
               if(products.database.results.next())
               {
                  if(products.database.results.getInt("folder") > 0)
                  {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage(
                        "<br />The product to move must not be a folder.");
                  }
               }
               else
                  message.setResult(ResultMessage.DoesNotExist);

               products.database.results.close();
            }

            int depth = 0;
            if(message.Report.getResult() == ResultMessage.NoResult && parentId > 0)
            {
               // Make sure the parent is a valid folder.
               products.getProductInfo(parentId);
               if(products.database.results.next())
               {
                  depth = products.database.results.getInt("depth") + 1;
                  if(products.database.results.getInt("folder") == 0)
                  {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage(
                        "<br />The product must be moved to a folder.");
                  }
               }
               else
               {
                  message.setResult(ResultMessage.DoesNotExist);
                  message.addMessage("<br />The folder is not valid.");
               }

               products.database.results.close();
            }

            if(message.Report.getResult() == ResultMessage.NoResult)
            {
               // Get the file paths.
               String location = products.getProductLocation(productId);
               String path = "";
               if(parentId > 0)
               {
                  path = products.getProductLocation(parentId);
                  path = path.replace('/', File.separatorChar) + File.separator;
               }

               String[] filePath = location.split("/");
               String fileName = filePath[filePath.length - 1];
               location = location.replace('/', File.separatorChar);
               file = new File(directory + path + fileName);
               if(file.exists())
                  message.setResult(ResultMessage.Exists);
               else
               {
                  original = new File(directory + location);
                  if(original.renameTo(file))
                  {
                     // Change the parent folder and depth in the database.
                     products.changeProductParent(productId, parentId, depth);
                     message.setResult(ResultMessage.Success);
                  }
                  else
                  {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage("<br />Unable to move the product file.");
                  }
               }
            }
         }
         catch(SQLException sqle2)
         {
            message.setResult(ResultMessage.Failure);
            message.addException(sqle2);
            if(file != null && original != null)
               file.renameTo(original);
         }
         catch(SecurityException se)
         {
            message.setResult(ResultMessage.Failure);
            message.addException(se);
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

      products.database.cleanup();
      response.sendRedirect(response.encodeRedirectURL(contextPath + page + extra));
   }
}
/**
 * @(#)RenameProductServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/3/8
 */

package csimsoft;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class RenameProductServlet extends HttpServlet
{
   public static final int RenameProduct = 0;
   public static final int RenameFolder = 1;
   public static final int AddFolder = 2;

   public RenameProductServlet()
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
         String typeName = "product";
         if(this.Operation > RenameProductServlet.RenameProduct)
            typeName = "folder";

         if(result == ResultMessage.Success)
         {
            String action = "renamed";
            if(this.Operation == RenameProductServlet.AddFolder)
               action = "added";

            this.Report.setType(ResultMessage.InfoMessage);
            this.Report.setMessage(
               "The " + typeName + " has been successfully " + action + ".");
         }
         else if(result == ResultMessage.Failure)
         {
            String action = "rename";
            if(this.Operation == RenameProductServlet.AddFolder)
               action = "add";

            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to " + action + " the " + typeName + ".");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The specified name already exists.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to rename a product.<br />" +
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
      RenameProductServlet.Message message = new RenameProductServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin in order to add a new product.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
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
      params.removeParameter("productname");
      params.removeParameter("parentid");
      params.removeParameter("back");
      params.removeParameter("next");
      String back = request.getParameter("back");
      if(back != null && !back.isEmpty())
         page = "/" + back;

      String next = request.getParameter("next");
      for(int i = 0; i < params.getParameterCount(); i++)
      {
         String paramName = params.getParameterName(i);
         extra.add(paramName, request.getParameter(paramName));
      }

      int productId = 0;
      try
      {
         String idString = request.getParameter("product");
         if(idString != null && !idString.isEmpty())
            productId = Integer.parseInt(idString);
      }
      catch(NumberFormatException nfe)
      {
         message.setResult(ResultMessage.Failure);
         message.addMessage("<br />Invalid product id.");
         response.sendRedirect(response.encodeRedirectURL(contextPath + page + extra));
         return;
      }

      String productName = request.getParameter("productname");
      if(productId == 0)
         message.Operation = RenameProductServlet.AddFolder;

      int parentId = 0;
      try
      {
         String idString = request.getParameter("parentid");
         if(idString != null && !idString.isEmpty())
            parentId = Integer.parseInt(idString);
      }
      catch(NumberFormatException nfe)
      {
         message.setResult(ResultMessage.Failure);
         message.addMessage("<br />Invalid parent folder id.");
         response.sendRedirect(response.encodeRedirectURL(contextPath + page + extra));
         return;
      }

      // Make sure the necessary parameters are present.
      if(productName == null || productName.isEmpty() || productId < 0 || parentId < 0)
      {
         message.setResult(ResultMessage.Failure);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page + extra));
         return;
      }

      // Get the download folder location.
      String directory = DownloadServlet.getDirectory(session, request) + File.separator;

      DatabaseProducts products = new DatabaseProducts();
      try
      {
         FolderMover mover = new FolderMover();
         products.database.connect();
         products.database.connection.setAutoCommit(false);
         try
         {
            // If the user is not an admin, make sure the product is accessible.
            if(!isAdmin && !products.isDistributorProduct(
               user.getUserId(), productId > 0 ? productId : parentId))
            {
               message.setResult(ResultMessage.NotAuthorized);
            }
            else if(productId == 0)
            {
               // Create the new folder.
               productId = this.createFolder(products, mover, message, productName,
                  parentId, directory);
               if(productId > 0)
                  extra.add("product", productId);
            }
            else if(this.checkRenameParameters(products, message, productId,
               productName))
            {
               if(message.Operation == RenameProductServlet.RenameFolder)
               {
                  // Rename the folder and move the contents.
                  this.renameFolder(products, mover, message, productId, productName,
                     directory);
               }
               else
               {
                  // Rename the product in the database.
                  products.setProductName(productId, productName);
                  products.database.connection.commit();
                  message.setResult(ResultMessage.Success);
               }
            }
         }
         catch(SQLException sqle2)
         {
            products.database.connection.rollback();
            message.setResult(ResultMessage.Failure);
            message.addException(sqle2);
         }
         catch(NullPointerException npe)
         {
            message.setResult(ResultMessage.Failure);
            message.addMessage("<br />The " + (productId == 0 ? "parent " : "") +
               "folder is not valid.");
         }
         catch(SecurityException se)
         {
            message.setResult(ResultMessage.Failure);
            message.addException(se);
         }

         // Finish the move or revert the changes.
         if(message.Report.getResult() == ResultMessage.Success)
         {
            mover.completeMove(message);
            if(next != null && !next.isEmpty())
               page = "/" + next;
         }
         else
            mover.revertMove(message);
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

   private String makeSafe(String name)
   {
      String newName = null;
      String[] nameList = name.split("\\W");
      if(nameList.length > 0)
         newName = nameList[0];

      for(int i = 1; i < nameList.length; i++)
         newName += "-" + nameList[i];

      return newName;
   }

   private int createFolder(DatabaseProducts products, FolderMover mover,
      RenameProductServlet.Message message, String productName, int parentId,
      String directory) throws SQLException
   {
      // Make the folder location from the label.
      String location = this.makeSafe(productName);

      // Get the parent folder.
      String path = "";
      if(parentId > 0)
      {
         path = products.getProductLocation(parentId);
         path = path.replace('/', File.separatorChar) + File.separator;
      }

      // Make sure the location is new.
      File folder = new File(directory + path + location);
      if(folder.exists())
      {
         message.setResult(ResultMessage.Exists);
         message.addMessage(
            "<br />Duplicate file name: " + path + location);
         return 0;
      }

      // Make the folder on the disk.
      if(folder.mkdirs())
      {
         // Add the product to the database.
         mover.addNewFolder(folder);
         int productId = products.addProduct(message, parentId, productName,
            location, true);
         if(message.Report.getResult() == ResultMessage.NoResult)
         {
            products.database.connection.commit();
            message.setResult(ResultMessage.Success);
            return productId;
         }
      }
      else
      {
         message.setResult(ResultMessage.Failure);
         message.addMessage("<br />Unable to create the folder.");
      }

      return 0;
   }

   private boolean checkRenameParameters(DatabaseProducts products,
      RenameProductServlet.Message message, int productId, String productName)
      throws SQLException
   {
      // Get the product information.
      String oldName = null;
      products.getProductInfo(productId);
      if(products.database.results.next())
      {
         oldName = products.database.results.getString("product");
         if(products.database.results.getInt("folder") > 0)
            message.Operation = RenameProductServlet.RenameFolder;
      }

      products.database.results.close();
      if(oldName == null)
      {
         message.setResult(ResultMessage.Failure);
         message.addMessage("<br />The product id is not valid.");
         return false;
      }
      else if(oldName.equals(productName))
      {
         message.setResult(ResultMessage.Success);
         return false;
      }

      // Make sure the new product name is unique.
      PreparedStatement statement = products.database.connection.prepareStatement(
         "SELECT productid FROM products WHERE product = ? AND parentid IN " +
         "(SELECT parentid FROM products WHERE productid = ?)");
      products.database.addStatement(statement);
      statement.setString(1, productName);
      statement.setInt(2, productId);
      products.database.results = statement.executeQuery();
      boolean duplicate = products.database.results.next();
      products.database.results.close();
      if(duplicate)
         message.setResult(ResultMessage.Exists);

      return !duplicate;
   }

   private void renameFolder(DatabaseProducts products, FolderMover mover,
      RenameProductServlet.Message message, int productId, String productName,
      String directory) throws SQLException
   {
      String location = this.makeSafe(productName);

      // Get the folder location.
      String[] pathList = products.getProductLocation(productId).split("/");
      String oldLocation = pathList[pathList.length - 1];
      if(location.equals(oldLocation))
      {
         // Rename the folder in the database.
         products.setProductName(productId, productName);
         products.database.connection.commit();
         message.setResult(ResultMessage.Success);
         return;
      }

      String path = "";
      for(int i = 0; i < pathList.length - 1; i++)
         path += pathList[i] + File.separator;

      // Make sure the location is new.
      File folder = new File(directory + path + location);
      if(folder.exists())
      {
         message.setResult(ResultMessage.Exists);
         message.addMessage("<br />Duplicate file name: " + path + location);
         return;
      }

      // Use the folder mover to create the new folder and
      // move all the contents.
      File original = new File(directory + path + oldLocation);
      if(mover.moveFolder(message, original, folder))
      {
         // Update the database.
         products.setProductInfo(productId, productName, location);
         products.database.connection.commit();
         message.setResult(ResultMessage.Success);
      }
   }
}
/**
 * @(#)RemoveProductServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2010/8/20
 */

package csimsoft;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class RemoveProductServlet extends HttpServlet
{

   public RemoveProductServlet()
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
               "The product has been successfully removed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to remove the product.");
         }
         else if(result == ResultMessage.DoesNotExist)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The given product does not exist.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to remove products.<br />" +
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

   private static class LicensePlatform
   {
      public int licenseId = 0;
      public int platformId = 0;

      public LicensePlatform()
      {
      }
   }

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
   {
      // Get the result message object for the session.
      HttpSession session = request.getSession();
      RemoveProductServlet.Message message = new RemoveProductServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to remove a product.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      ParameterBuilder extra = new ParameterBuilder();
      UserInfo user = LoginServlet.getUserInfo(session);
      boolean isAdmin = user.isAdministrator();
      boolean isDistributor = user.isDistributor();
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in as an administrator to remove products.");
      }
      else if(!isAdmin && !isDistributor)
         message.setResult(ResultMessage.NotAuthorized);
      else
      {
         page = "/alldownloads.jsp";

         // Get the parameters.
         ParameterList params = new ParameterList();
         params.setParameters(request);
         params.removeParameter("product");
         params.removeParameter("contents");
         params.removeParameter("back");
         String back = request.getParameter("back");
         if(back != null && !back.isEmpty())
            page = "/" + back;

         for(int i = 0; i < params.getParameterCount(); i++)
         {
            String paramName = params.getParameterName(i);
            extra.add(paramName, request.getParameter(paramName));
         }

         String contents = request.getParameter("contents");
         int productId = 0;
         try
         {
            productId = Integer.parseInt(request.getParameter("product"));
         }
         catch(Exception e)
         {
         }

         // Make sure the product id is valid.
         if(productId > 0)
         {
            DatabaseProducts manager = new DatabaseProducts();
            DatabaseProcesses processes = new DatabaseProcesses(manager.database);
            try
            {
               manager.database.connect();
               manager.database.connection.setAutoCommit(false);
               try
               {
                  // If the user is not an admin, make sure the product is accessible.
                  if(!isAdmin && !manager.isDistributorProduct(
                     user.getUserId(), productId))
                  {
                     message.setResult(ResultMessage.NotAuthorized);
                  }
                  else
                  {
                     // If the product is a folder, get the list of contents.
                     ProductList products = new ProductList();
                     products.setProducts(manager.database, productId);
                     if(!products.getRoot().isEmpty() && !"delete".equals(contents))
                     {
                        // Ask the user about deleting the contents.
                        page = "/confirmdeletefolder.jsp";
                        extra.add("product", productId);
                        extra.add("back", back);
                     }
                     else
                     {
                        // Delete all the contents.
                        int failed = 0;
                        String directory =
                           DownloadServlet.getDirectory(session, request) +
                           File.separator;
                        ProductListItem item = products.getLastItem();
                        while(item != null)
                        {
                           failed += this.removeProduct(manager, processes,
                              item.getId(), directory);
                           item = products.getPreviousItem(item);
                        }

                        // Remove the product.
                        failed += this.removeProduct(manager, processes,
                           productId, directory);
                        message.setResult(ResultMessage.Success);
                        if(failed > 0)
                        {
                           message.addMessage("<br />Failed to remove " + failed +
                              " files.");
                        }
                     }
                  }
               }
               catch(SQLException sqle2)
               {
                  manager.database.connection.rollback();
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

            manager.database.cleanup();
         }
         else
         {
            message.setResult(ResultMessage.Failure);
            message.addMessage("<br />The given product id is not valid.");
         }
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page + extra));
   }

   private int removeProduct(DatabaseProducts products, DatabaseProcesses processes,
      int productId, String directory) throws SQLException
   {
      // Get the product location.
      String location = products.getProductLocation(productId);

      // Get and execute all the updates for the order process handlers.
      List<OrderProcessHandler> handlers = processes.getOrderHandlers();
      Iterator<OrderProcessHandler> iter = handlers.iterator();
      while(iter.hasNext())
         iter.next().removeProduct(processes.database, productId);

      // Remove any license platforms associated with the product.
      this.removeLicensePlatforms(products.database, productId);

      // Remove the product from the database.
      products.database.statement.executeUpdate(
         "UPDATE distributors SET folder = 0 WHERE folder = " + productId);
      products.database.statement.executeUpdate(
         "DELETE FROM orderproducts WHERE productid = " + productId);
      products.database.statement.executeUpdate(
         "DELETE FROM userdownloads WHERE productid = " + productId);
      products.database.statement.executeUpdate(
         "DELETE FROM productpaths WHERE productid = " + productId);
      products.database.statement.executeUpdate(
         "DELETE FROM products WHERE productid = " + productId);

      products.database.connection.commit();
      if(location != null)
      {
         try
         {
            // Remove the file from the server.
            File file = new File(directory + location);
            if(!file.delete())
               return 1;
         }
         catch(SecurityException se)
         {
            return 1;
         }
      }

      return 0;
   }

   private void removeLicensePlatforms(Database database, int productId)
      throws SQLException
   {
      ArrayList<RemoveProductServlet.LicensePlatform> licenses =
         new ArrayList<RemoveProductServlet.LicensePlatform>();
      database.results = database.statement.executeQuery(
         "SELECT licenseid, platformid FROM platformdownloads WHERE productid = " +
         productId);
      while(database.results.next())
      {
         RemoveProductServlet.LicensePlatform license =
            new RemoveProductServlet.LicensePlatform();
         license.licenseId = database.results.getInt("licenseid");
         license.platformId = database.results.getInt("platformid");
      }

      database.results.close();

      Iterator<RemoveProductServlet.LicensePlatform> iter = licenses.iterator();
      while(iter.hasNext())
      {
         RemoveProductServlet.LicensePlatform license = iter.next();
         database.statement.executeUpdate(
            "DELETE FROM userplatforms WHERE platformid = " + license.platformId +
            " AND userlicenseid IN (SELECT userlicenseid FROM licenses " +
            "WHERE licenseid = " + license.licenseId + ")");
      }

      database.statement.executeUpdate(
         "DELETE FROM platformdownloads WHERE productid = " + productId);
   }
}
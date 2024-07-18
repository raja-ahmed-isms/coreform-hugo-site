/**
 * @(#)EnableProductServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/12/21
 */

package csimsoft;

import java.io.IOException;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class EnableProductServlet extends HttpServlet
{

   public EnableProductServlet()
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
               "The product has been added to the user's download list.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to add the product to the user's download list.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to enable a product.<br />" +
               "Please log in as an administrator or distributor to do so.");
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
      EnableProductServlet.Message message = new EnableProductServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is an administrator.
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

      // Get the user id and product id from the parameters.
      int userId = 0;
      int productId = 0;
      try
      {
         userId = Integer.parseInt(request.getParameter("user"));
         productId = Integer.parseInt(request.getParameter("product"));
      }
      catch(NumberFormatException nfe)
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Make sure the user id is valid.
      if(userId < 1 || productId < 1)
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Set up the redirect page.
      page = "/clientdownloads.jsp?user=" + userId;

      String email = null;
      String product = "A new product";
      DatabaseUsers users = new DatabaseUsers();
      DatabaseProducts products = new DatabaseProducts(users.database);
      try
      {
         users.database.connect();
         users.database.statement = users.database.connection.createStatement();
         try
         {
            // Make sure the distributor can access the user.
            if(isAdmin || (isDistributor &&
               users.isDistributor(userId, user.getUserId())))
            {
               // See if the download is already enabled.
               users.database.results = users.database.statement.executeQuery(
                  "SELECT userid FROM userdownloads WHERE userid = " + userId +
                  " AND productid = " + productId);
               boolean isEnabled = users.database.results.next();
               users.database.results.close();
               if(!isEnabled)
               {
                  users.database.statement.executeUpdate(
                     "INSERT INTO userdownloads (userid, productid) VALUES (" +
                     userId + ", " + productId + ")");
                  try
                  {
                     email = users.getUserEmail(userId);
                     product = products.getProductName(productId);
                  }
                  catch(Exception e)
                  {
                  }
               }

               message.setResult(ResultMessage.Success);
            }
            else
               message.setResult(ResultMessage.NotAuthorized);
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
      catch(NamingException ne)
      {
         message.setResult(ResultMessage.ResourceError);
         message.addException(ne);
      }

      users.database.cleanup();
      if(email != null)
      {
         // Send an email to notify the user.
         csimsoft.Email messenger = new csimsoft.Email();
         messenger.sendDownloadMessage(message, email, product);
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}
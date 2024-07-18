/**
 * @(#)ChangeDistributorFolderServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/4/2
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


public class ChangeDistributorFolderServlet extends HttpServlet
{

   public ChangeDistributorFolderServlet()
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
               "The distributor folder has been successfully changed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to change the distributor folder.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to set a distributor folder.<br />" +
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
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
   {
      this.doPost(request, response);
   }

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
   {
      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ChangeDistributorFolderServlet.Message message =
         new ChangeDistributorFolderServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is an administrator.
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
      int userId = 0;
      int productId = 0;
      try
      {
         userId = Integer.parseInt(request.getParameter("user"));
         productId = Integer.parseInt(request.getParameter("product"));
      }
      catch(Exception nfe)
      {
      }

      if(userId > 0 && productId > 0)
      {
         page = "/distributor.jsp?user=" + userId;
         DatabaseProducts products = new DatabaseProducts();
         try
         {
            products.database.connect();
            try
            {
               products.getProductInfo(productId);
               boolean validFolder = products.database.results.next();
               if(validFolder)
                  validFolder = products.database.results.getInt("folder") > 0;

               if(validFolder)
               {
                  products.database.statement.executeUpdate(
                     "UPDATE distributors SET folder = " + productId +
                     " WHERE userid = " + userId);
                  message.setResult(ResultMessage.Success);
               }
               else
               {
                  message.setResult(ResultMessage.Failure);
                  message.addMessage(
                     "<br />The given product id is not a valid folder.");
               }
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

         products.database.cleanup();
      }
      else
      {
         message.setResult(ResultMessage.Failure);
         message.addMessage("<br />The user or product id is not valid.");
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}
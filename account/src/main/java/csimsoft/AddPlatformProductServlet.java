/**
 *
 * @author Mark Richardson
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


public class AddPlatformProductServlet extends HttpServlet
{

   public AddPlatformProductServlet()
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
               "The platform download has been successfully added.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to add the platform download.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to add a platform download.<br />" +
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
      // Make sure the character decoding is correct.
      if(request.getCharacterEncoding() == null)
         request.setCharacterEncoding("UTF-8");

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      AddPlatformProductServlet.Message message = new AddPlatformProductServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin in order to add a new license group.
      String contextPath = request.getContextPath();
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!user.isAdministrator())
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/index.jsp"));
         return;
      }

      // Get the parameters from the request.
      int licenseId = 0;
      try
      {
         licenseId = Integer.parseInt(request.getParameter("license"));
      }
      catch(Exception e)
      {
      }

      int platformId = 0;
      try
      {
         platformId = Integer.parseInt(request.getParameter("platform"));
      }
      catch(Exception e)
      {
      }

      int productId = 0;
      try
      {
         productId = Integer.parseInt(request.getParameter("product"));
      }
      catch(Exception e)
      {
      }

      // Make sure the required parameters are present.
      if(licenseId < 1 || platformId < 1 || productId < 1)
      {
         response.sendRedirect(response.encodeRedirectURL(
            contextPath + "/allproducts.jsp"));
         return;
      }

      String page = "/licenseplatforms.jsp?license=" + licenseId;
      DatabaseLicenses licenses = new DatabaseLicenses();
      try
      {
         licenses.database.connect();
         licenses.database.connection.setAutoCommit(false);
         try
         {
            // See if the license platform exists.
            int oldProductId = licenses.getPlatformProduct(licenseId, platformId);
            if(oldProductId > 0)
            {
               // Set the new download.
               licenses.database.statement.executeUpdate(
                  "UPDATE platformdownloads SET productid = " + productId +
                  " WHERE licenseid = " + licenseId + " AND platformid = " + platformId);

               // Update the affected user download lists.
               licenses.database.statement.executeUpdate(
                  "UPDATE userdownloads SET productid = " + productId +
                  " WHERE productid = " + oldProductId + " AND userid IN " +
                  "(SELECT userid FROM licenses WHERE licenseid = " + licenseId + ")");
            }
            else
            {
               // Add a new license platform.
               licenses.database.statement.executeUpdate(
                  "INSERT INTO platformdownloads (platformid, licenseid, productid, " +
                  "autoadd) VALUES (" + platformId + ", " + licenseId + ", " +
                  productId + ", 0)");
            }

            licenses.database.connection.commit();
            message.setResult(ResultMessage.Success);
         }
         catch(SQLException sqle2)
         {
            licenses.database.connection.rollback();
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

      licenses.database.cleanup();
      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

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


public class ForwardLicensePlatformServlet extends HttpServlet
{

   public ForwardLicensePlatformServlet()
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
         if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The license platform already exists.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to add a new license platform.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to add a license platform.<br />" +
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

      // Get the request parameters.
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

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ForwardLicensePlatformServlet.Message message =
         new ForwardLicensePlatformServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to add a license platform.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in as an administrator to add a license platform.");
      }
      else if(!user.isAdministrator())
         message.setResult(ResultMessage.NotAuthorized);
      else if(platformId < 1 || licenseId < 1)
      {
         page = "/allproducts.jsp";
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />Missing required parameters.");
      }
      else
      {
         page = "/licenseplatforms.jsp?license=" + licenseId;

         DatabaseLicenses licenses = new DatabaseLicenses();
         try
         {
            licenses.database.connect();
            try
            {
               // Make sure the platform is not a duplicate.
               if(licenses.isPlatformUsed(licenseId, platformId))
                  message.setResult(ResultMessage.Exists);
               else
               {
                  page = "/selectproduct.jsp?next=newplatformproduct&seltype=files" +
                     "&license=" + licenseId + "&platform=" + platformId;
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

         licenses.database.cleanup();
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

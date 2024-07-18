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


public class LicenseSearchServlet extends HttpServlet
{
   public static final int NoExpiration = 1;
   public static final int NullExpiration = 2;
   public static final int CloseExpiration = 3;
   public static final int AlreadyExpired = 4;

   public LicenseSearchServlet()
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
         if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to set up a user license search.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to do a user license search.<br />" +
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
      LicenseSearchServlet.Message message = new LicenseSearchServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin in order to modify a process.
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

      // Get the request parameters.
      int licenseId = 0;
      int distributor = 0;
      int expType = LicenseSearchServlet.NoExpiration;
      int monthsLeft = 0;
      try
      {
         licenseId = Integer.parseInt(request.getParameter("license"));
      }
      catch(Exception nfe)
      {
      }

      try
      {
         distributor = Integer.parseInt(request.getParameter("dist"));
      }
      catch(Exception nfe)
      {
      }

      try
      {
         expType = Integer.parseInt(request.getParameter("exptype"));
      }
      catch(Exception nfe)
      {
      }

      try
      {
         monthsLeft = Integer.parseInt(request.getParameter("months"));
      }
      catch(Exception nfe)
      {
      }

      // Make sure the required parameters are present.
      if(licenseId < 1 || distributor < 0 ||
         expType < LicenseSearchServlet.NoExpiration ||
         expType > LicenseSearchServlet.AlreadyExpired ||
         (!isAdmin && distributor < 1) || (monthsLeft < 1 &&
         expType == LicenseSearchServlet.CloseExpiration))
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      if(!isAdmin && isDistributor && user.getUserId() != distributor)
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      String search = "";
      DatabaseUsers users = new DatabaseUsers();
      try
      {
         if(distributor > 0)
            users.database.connect();

         try
         {
            if(distributor > 0)
            {
               int distGroupId = users.getDistibutorGroup(distributor);
               search = "?search=groupid/e/" + distGroupId + "/a/";
            }
            else
               search = "?search=";

            page = "/usersearch.jsp";
            search += "lpl/";
            if(expType == LicenseSearchServlet.NullExpiration)
               search += "expires/en/a/";
            else if(expType == LicenseSearchServlet.CloseExpiration ||
               expType == LicenseSearchServlet.AlreadyExpired)
            {
               try
               {
                  java.util.Calendar date = java.util.Calendar.getInstance();
                  String today = Database.formatDate(date);
                  if(expType == LicenseSearchServlet.AlreadyExpired)
                     search += "expires/lt/" + today + "/a/";
                  else
                  {
                     date.add(java.util.Calendar.MONTH, monthsLeft);
                     search += "expires/gte/" + today + "/a/expires/lte/" +
                        Database.formatDate(date) + "/a/";
                  }
               }
               catch(Exception dfe)
               {
               }
            }

            search += "licenseid/e/" + licenseId + "/lpr";
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
      response.sendRedirect(response.encodeRedirectURL(contextPath + page + search));
   }
}

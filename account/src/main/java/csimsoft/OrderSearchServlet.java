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


public class OrderSearchServlet extends HttpServlet
{

   public OrderSearchServlet()
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
               "Unable to set up a user order search.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to do a user order search.<br />" +
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
      OrderSearchServlet.Message message = new OrderSearchServlet.Message(
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
      int productId = 0;
      int licenseId = 0;
      int distributor = 0;
      try
      {
         productId = Integer.parseInt(request.getParameter("product"));
      }
      catch(Exception nfe)
      {
      }

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

      // Make sure the required parameters are present.
      if(productId < 0 || licenseId < 0 || distributor < 0 ||
         (!isAdmin && distributor < 1) || (productId < 1 && licenseId < 1))
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

      // Get the rest of the parameters.
      String before = null;
      String after = null;
      if("yes".equalsIgnoreCase(request.getParameter("usedate")))
      {
         try
         {
            int year = 0;
            int month = 0;
            int day = 0;
            java.util.Calendar date = java.util.Calendar.getInstance();
            if("yes".equalsIgnoreCase(request.getParameter("before")))
            {
               year = Integer.parseInt(request.getParameter("by"));
               month = Integer.parseInt(request.getParameter("bm")) - 1;
               day = Integer.parseInt(request.getParameter("bd"));
               date.set(year, month, day);
               before = Database.formatDate(date);
            }

            if("yes".equalsIgnoreCase(request.getParameter("after")))
            {
               year = Integer.parseInt(request.getParameter("ay"));
               month = Integer.parseInt(request.getParameter("am")) - 1;
               day = Integer.parseInt(request.getParameter("ad"));
               date.set(year, month, day);
               after = Database.formatDate(date);
            }
         }
         catch(Exception e)
         {
            message.setResult(ResultMessage.Failure);
            message.addMessage("<br/>The search date(s) are not valid.");
            response.sendRedirect(response.encodeRedirectURL(contextPath + page));
            return;
         }
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
            search += "opl/";
            if(before != null && !before.isEmpty())
               search += "orderdate/lt/" + before + "/a/";

            if(after != null && !after.isEmpty())
               search += "orderdate/gt/" + after + "/a/";

            if(productId > 0)
               search += "oppl/e/" + productId + "/oppr/opr";
            else
               search += "olpl/e/" + licenseId + "/olpr/opr";
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

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


public class ProductSearchServlet extends HttpServlet
{

   public ProductSearchServlet()
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
               "Unable to set up a user product search.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to do a user product search.<br />" +
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
      ProductSearchServlet.Message message = new ProductSearchServlet.Message(
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

      // Get the parameters from the request.
      int productId = 0;
      int distributor = 0;
      try
      {
         productId = Integer.parseInt(request.getParameter("product"));
         distributor = Integer.parseInt(request.getParameter("dist"));
      }
      catch(Exception nfe)
      {
      }

      // Make sure the required parameters are present.
      if(productId < 1 || distributor < 0 ||
         (!isAdmin && isDistributor && distributor < 1))
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
               int groupId = users.getDistibutorGroup(distributor);
               search = "?search=groupid/e/" + groupId + "/a/";
            }
            else
               search = "?search=";

            search += "ppl/productid/e/" + productId + "/ppr";
            page = "/usersearch.jsp";
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

/**
 * @(#)ChangeInfoServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/4/18
 */

package csimsoft;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class ChangeInfoServlet extends HttpServlet
{

   public ChangeInfoServlet()
   {
   }

   private static class Message implements ResultMessageAdapter
   {
      public ResultMessage Report = null;
      public boolean Admin = false;

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
               "The contact information has been successfully changed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to change the contact information.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to update the user's contact information." +
               "<br />Please log in as an administrator, distributor, or reseller to do so.");
         }
      }

      @Override
      public void addException(Exception e)
      {
         if(this.Admin)
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
      String idString = request.getParameter("user");
      String company = request.getParameter("company");
      String phone = request.getParameter("phone");
      String address = request.getParameter("address");
      String country = request.getParameter("country");

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ChangeInfoServlet.Message message = new ChangeInfoServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is logged in.
      String contextPath = request.getContextPath();
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in to change your contact information.");
         response.sendRedirect(response.encodeRedirectURL(
            contextPath + "/changeprofile.jsp"));
         return;
      }

      // Make sure the user is authorized to make the changes.
      UserInfo user = LoginServlet.getUserInfo(session);
      int userId = user.getUserId();
      boolean badId = false;
      try
      {
         if(idString != null && !idString.isEmpty())
            userId = Integer.parseInt(idString);
      }
      catch(Exception e)
      {
         badId = true;
      }

      String page = "/index.jsp";
      boolean isAdmin = user.isAdministrator();
      message.Admin = isAdmin;
      boolean isDistributor = user.isDistributor();
      boolean isReseller = user.isReseller();
      if(userId != user.getUserId() && !isAdmin && !isDistributor && !isReseller)
      {
         message.setResult(ResultMessage.NotAuthorized);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Make sure the required parameters are valid.
      if(badId || userId < 1 || country == null || country.isEmpty() ||
         phone == null || phone.isEmpty() || address == null || address.isEmpty())
      {
         // Send the request parameters back to the form.
         ParameterBuilder params = new ParameterBuilder();
         params.add("company", company);
         params.add("phone", phone);
         params.add("address", address);
         params.add("country", country);
         response.sendRedirect(response.encodeRedirectURL(
            contextPath + "/changeinfo.jsp" + params));
         return;
      }

      // Truncate the user info strings to make sure they fit.
      if(company == null)
         company = "";
      else if(company.length() > 128)
         company = company.substring(0, 128);

      if(phone.length() > 32)
         phone = phone.substring(0, 32);

      if(address.length() > 512)
         address = address.substring(0, 512);

      if(country.length() > 32)
         country = country.substring(0, 32);

      DatabaseUsers users = new DatabaseUsers();
      try
      {
         users.database.connect();
         try
         {
            // Make sure the distributor can access the user.
            if(isAdmin || userId == user.getUserId() || (isDistributor &&
               users.isDistributor(userId, user.getUserId()))
			   || (isReseller && users.isReseller(userId, user.getUserId())))
            {
               PreparedStatement statement = users.database.connection.prepareStatement(
                  "UPDATE users SET company = ?, phone = ?, address = ?, country = ? " +
                  "WHERE userid = ?");
               users.database.addStatement(statement);

               // Update the user information.
               statement.setString(1, company);
               statement.setString(2, phone);
               statement.setString(3, address);
               statement.setString(4, country);
               statement.setInt(5, userId);
               statement.executeUpdate();

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

      if(userId != user.getUserId())
      {
         if(isAdmin)
            page = "/clientinfo.jsp?user=" + userId;
         else if(isDistributor)
            page = "/distclient.jsp?user=" + userId;
         else if(isReseller)
            page = "/resellerclient.jsp?user=" + userId;
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

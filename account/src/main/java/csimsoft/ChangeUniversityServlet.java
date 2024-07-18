/**
 * @(#)ChangeUniversityServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/6/8
 */

package csimsoft;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class ChangeUniversityServlet extends HttpServlet
{

   public ChangeUniversityServlet()
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
               "The university information has been saved.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to save the university information.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The university name has already been used.<br />" +
               "Please select another name.");
         }
         else if(result == ResultMessage.DoesNotExist)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The order process handler does not exist.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to access this information.");
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

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ChangeUniversityServlet.Message message =
         new ChangeUniversityServlet.Message(LoginServlet.getResultMessage(session));

      // Make sure the user is logged in.
      String page = "/orders.jsp";
      String contextPath = request.getContextPath();
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in to set the university information.");
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Get the user from the session.
      UserInfo user = LoginServlet.getUserInfo(session);
      boolean isAdmin = user.isAdministrator();
      message.Admin = isAdmin;
      if(isAdmin)
         page = "/allorders.jsp";

      // Get the parameters from the request.
      String university = request.getParameter("university");
      String fax = request.getParameter("fax");
      String contact = request.getParameter("contact");
      int orderId = 0;
      try
      {
         orderId = Integer.parseInt(request.getParameter("orderid"));
      }
      catch(Exception e)
      {
      }

      int universityId = 0;
      try
      {
         universityId = Integer.parseInt(request.getParameter("universityid"));
      }
      catch(Exception e)
      {
      }

      // Make sure the parameters are valid.
      if(university == null || university.isEmpty() || orderId < 0 ||
         universityId < 0 || (orderId == 0 && universityId == 0 && !isAdmin))
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Get the contact type for the order form.
      int contactType = 0;
      if(orderId > 0)
      {
         contactType = DatabaseUniversities.getContactType(contact);
         if(contactType == 0)
         {
            response.sendRedirect(response.encodeRedirectURL(contextPath + page));
            return;
         }
      }

      String extra = "";
      if(orderId == 0)
      {
         if(universityId > 0)
         {
            page = "/universityinfo.jsp";
            if(isAdmin)
               extra = "?universityid=" + universityId;
         }
         else if(isAdmin)
            page = "/universities.jsp";
      }

      if(university.length() > 128)
         university = university.substring(0, 128);

      if(fax == null)
         fax = "";
      else if(fax.length() > 32)
         fax = fax.substring(0, 32);

      DatabaseUsers users = new DatabaseUsers();
      DatabaseOrders orders = new DatabaseOrders(users.database);
      DatabaseUniversities universities = new DatabaseUniversities(users.database);
      try
      {
         users.database.connect();
         users.database.connection.setAutoCommit(universityId > 0);
         try
         {
            // Set or create the university.
            if(universityId > 0)
            {
               // Make sure the user has access to the university.
               if(!isAdmin && users.getUniversityId(user.getUserId()) != universityId)
                  message.setResult(ResultMessage.NotAuthorized);
               else
               {
                  String oldName = universities.getUniversityName(universityId);
                  if(oldName == null)
                  {
                     message.setResult(ResultMessage.Failure);
                     message.addMessage("<br />The university id is not valid.");
                  }
                  else if(!oldName.equals(university) &&
                     universities.getUniversityId(university) > 0)
                  {
                     message.setResult(ResultMessage.Exists);
                     page = "/changeuniversity.jsp";
                     extra = "?universityid=" + universityId +
                        "&university=" + URLEncoder.encode(university, "UTF-8");
                     if(!fax.isEmpty())
                        extra += "&fax=" + URLEncoder.encode(fax, "UTF-8");
                  }
                  else
                  {
                     PreparedStatement statement =
                        users.database.connection.prepareStatement(
                        "UPDATE universities SET university = ?, fax = ? " +
                        "WHERE universityid = ?");
                     users.database.addStatement(statement);
                     statement.setString(1, university);
                     statement.setString(2, fax);
                     statement.setInt(3, universityId);
                     statement.executeUpdate();
                     message.setResult(ResultMessage.Success);
                  }
               }
            }
            else
            {
               // Get the order step.
               OrderInfo order = new OrderInfo(orderId);
               if(orderId > 0)
               {
                  if(orders.getOrderStep(orderId, order))
                  {
                     DatabaseProcesses processes = new DatabaseProcesses(users.database);
                     order.setHandler(processes.getProcessHandler(order.getProcessId()));
                     if(order.getHandler() == null)
                        message.setResult(ResultMessage.DoesNotExist);
                     else if(!isAdmin && order.getUserId() != user.getUserId())
                        message.setResult(ResultMessage.NotAuthorized);
                  }
                  else
                     message.setResult(ResultMessage.Failure);
               }

               if(message.Report.getResult() == ResultMessage.NoResult)
               {
                  // Make sure the university name is unique.
                  if(universities.getUniversityId(university) > 0)
                  {
                     message.setResult(ResultMessage.Exists);
                     extra += "?university=" + URLEncoder.encode(university, "UTF-8");
                     if(!fax.isEmpty())
                        extra += "&fax=" + URLEncoder.encode(fax, "UTF-8");

                     if(orderId > 0)
                     {
                        page = "/orderuniversity.jsp";
                        extra += "&orderid=" + orderId + "&contact=" +
                           URLEncoder.encode(contact, "UTF-8");
                     }
                     else
                        page = "/changeuniversity.jsp";
                  }
                  else
                  {
                     // Get the next university id from the database.
                     int nextId = users.database.getNextId(
                        "universitycount", "universityid");

                     // Add the new university.
                     int adminId = 0;
                     int techId = 0;
                     if((contactType & DatabaseUniversities.AdminContact) > 0)
                        adminId = order.getUserId();

                     if((contactType & DatabaseUniversities.TechContact) > 0)
                        techId = order.getUserId();

                     PreparedStatement statement =
                        users.database.connection.prepareStatement(
                        "INSERT INTO universities (universityid, university, fax, " +
                        "adminid, techid) VALUES (?, ?, ?, ?, ?)");
                     users.database.addStatement(statement);
                     statement.setInt(1, nextId);
                     statement.setString(2, university);
                     statement.setString(3, fax);
                     statement.setInt(4, adminId);
                     statement.setInt(5, techId);

                     statement.executeUpdate();
                     users.database.connection.commit();
                     message.setResult(ResultMessage.Success);

                     // Add the university role to the user.
                     if(order.getUserId() == user.getUserId())
                        user.addRole(UserInfo.UniversityRoleName);

                     if(orderId > 0)
                     {
                        order.incrementProcessStep();
                        if(orders.updateOrderStep(order))
                           page = "/orderinfo?orderid=" + orderId;

                        users.database.connection.commit();
                     }
                  }
               }
            }
         }
         catch(SQLException sqle2)
         {
            if(!users.database.connection.getAutoCommit())
               users.database.connection.rollback();

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
      response.sendRedirect(response.encodeRedirectURL(contextPath + page + extra));
   }
}
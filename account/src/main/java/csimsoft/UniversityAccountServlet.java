/**
 * @(#)UniversityAccountServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/6/9
 */

package csimsoft;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class UniversityAccountServlet extends HttpServlet
{

   public UniversityAccountServlet()
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
               "The contact information has been saved.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to save the contact information.");
         }
         else if(result == ResultMessage.DoesNotExist)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
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
      UniversityAccountServlet.Message message =
         new UniversityAccountServlet.Message(LoginServlet.getResultMessage(session));

      // Make sure the user is logged in.
      String page = "/orders";
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
      String email = request.getParameter("email");
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
      if(email == null || email.isEmpty() || orderId < 0 || universityId < 0 ||
         (orderId == 0 && universityId == 0))
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Get the contact type for the order form.
      int contactType = DatabaseUniversities.getContactType(contact);
      if(contactType == 0)
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      String extra = "";
      if(orderId == 0)
      {
         page = "/universityinfo.jsp";
         if(isAdmin)
            extra = "?universityid=" + universityId;
      }

      DatabaseUsers users = new DatabaseUsers();
      try
      {
         users.database.connect();
         users.database.connection.setAutoCommit(false);
         try
         {
            if(!isAdmin && universityId > 0)
            {
               // Make sure the user is part of the university.
               if(users.getUniversityId(user.getUserId()) != universityId)
                  message.setResult(ResultMessage.NotAuthorized);
            }

            OrderInfo order = null;
            DatabaseOrders orders = null;
            if(orderId > 0)
            {
               order = new OrderInfo(orderId);
               orders = new DatabaseOrders(users.database);
               universityId = this.checkOrder(users, orders, message, user, order);
            }

            if(message.Report.getResult() == ResultMessage.NoResult)
            {
               // Get the account id for the given email address.
               int accountId = users.getUserId(email);
               if(accountId == 0)
                  accountId = users.getContactId(email);

               if(accountId == 0)
               {
                  message.setResult(ResultMessage.DoesNotExist);
                  message.addMessage(
                     "The given email address does not exist.");
                  if(orderId > 0)
                  {
                     message.addMessage(
                        "<br />Please enter another email address or add " +
                        "the given email address as a new contact.");
                     page = "/ordercontact.jsp";
                     extra = "?orderid=" + orderId + "&email=" +
                        URLEncoder.encode(email, "UTF-8");
                  }
               }
               else
               {
                  // Set the university contact based on the role.
                  DatabaseUniversities universities = new DatabaseUniversities(
                     users.database);
                  List<Integer> previous = universities.setContact(universityId,
                     accountId, contactType);
                  Iterator<Integer> iter = previous.iterator();
                  while(iter.hasNext())
                     users.removeUnusedContact(iter.next());

                  if(orderId > 0)
                  {
                     order.incrementProcessStep();
                     if(orders.updateOrderStep(order))
                        page = "/orderinfo?orderid=" + orderId;
                  }

                  users.database.connection.commit();
                  message.setResult(ResultMessage.Success);
               }
            }
         }
         catch(java.sql.SQLException sqle2)
         {
            users.database.connection.rollback();
            message.setResult(ResultMessage.Failure);
            message.addException(sqle2);
         }
      }
      catch(java.sql.SQLException sqle)
      {
         message.setResult(ResultMessage.ResourceError);
         message.addException(sqle);
      }
      catch(javax.naming.NamingException ne)
      {
         message.setResult(ResultMessage.ResourceError);
         message.addException(ne);
      }

      users.database.cleanup();
      response.sendRedirect(response.encodeRedirectURL(contextPath + page + extra));
   }

   private int checkOrder(DatabaseUsers users, DatabaseOrders orders,
      ResultMessageAdapter message, UserInfo user, OrderInfo order) throws SQLException
   {
      // Get the user id and order step.
      orders.getOrderStep(order.getId(), order);

      // Make sure the order is valid and accessible by the user.
      if(order.getUserId() == 0)
      {
         message.setResult(ResultMessage.Failure);
         return 0;
      }
      else if(!user.isAdministrator() && order.getUserId() != user.getUserId())
      {
         message.setResult(ResultMessage.NotAuthorized);
         return 0;
      }

      // Make sure the handler is assigned.
      DatabaseProcesses processes = new DatabaseProcesses(users.database);
      order.setHandler(processes.getProcessHandler(order.getProcessId()));
      if(order.getHandler() == null)
      {
         message.setResult(ResultMessage.DoesNotExist);
         message.addMessage(
            "The order process handler does not exist.");
         return 0;
      }

      // Get the university id for the order.
      int universityId = users.getUniversityId(order.getUserId());
      if(universityId == 0)
         message.setResult(ResultMessage.Failure);

      return universityId;
   }
}
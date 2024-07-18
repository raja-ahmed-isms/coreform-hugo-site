/**
 * @(#)ChangeContactServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/6/10
 */

package csimsoft;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class ChangeContactServlet extends HttpServlet
{

   public ChangeContactServlet()
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
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The email address already exists.");
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
      ChangeContactServlet.Message message =
         new ChangeContactServlet.Message(LoginServlet.getResultMessage(session));

      // Make sure the user is logged in.
      String page = "/orders.jsp";
      String contextPath = request.getContextPath();
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in to set the contact information.");
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
      String first = request.getParameter("first");
      String last = request.getParameter("last");
      String phone = request.getParameter("phone");
      String address = request.getParameter("address");
      String country = request.getParameter("country");
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

      int contactId = 0;
      try
      {
         contactId = Integer.parseInt(request.getParameter("contactid"));
      }
      catch(Exception e)
      {
      }

      // Make sure the parameters are valid.
      if(email == null || email.isEmpty() || first == null || first.isEmpty() ||
         last == null || last.isEmpty() || country == null || country.isEmpty() ||
         orderId < 0 || universityId < 0 || contactId < 0 ||
         (orderId == 0 && universityId == 0 && contactId == 0 && !isAdmin))
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      // Get the contact type for the order form.
      int contactType = DatabaseUniversities.getContactType(contact);
      if(!isAdmin && contactType == 0 && contactId == 0)
      {
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      ParameterBuilder extra = new ParameterBuilder();
      if(orderId == 0)
      {
         if(universityId > 0)
         {
            page = "/universityinfo.jsp";
            if(isAdmin)
               extra.add("universityid", universityId);
         }
         else if(isAdmin)
            page = "/allcontacts.jsp";
      }

      // Make sure the parameters aren't too long for the database.
      if(phone == null)
         phone = "";
      else if(phone.length() > 32)
         phone = phone.substring(0, 32);

      if(address == null)
         address = "";
      else if(address.length() > 512)
         address = address.substring(0, 512);

      if(country.length() > 32)
         country = country.substring(0, 32);


      DatabaseUsers users = new DatabaseUsers();
      try
      {
         users.database.connect();
         users.database.connection.setAutoCommit(contactId > 0);
         users.database.statement = users.database.connection.createStatement();
         try
         {
            if(contactId > 0)
            {
               // Make sure the contact information can be changed.
               this.checkSetInfo(users, message, user, contactId, email);
               if(message.Report.getResult() == ResultMessage.Exists)
               {
                  message.addMessage(
                     "<br />Please enter another email address.");
                  page = "/changecontact.jsp";
                  extra.clear();
                  extra.add("contactid", contactId);
                  extra.add("first", first);
                  extra.add("last", last);
                  extra.add("country", country);
                  extra.add("phone", phone);
                  extra.add("address", address);
                  if(universityId > 0)
                     extra.add("universityid", universityId);
               }
               else if(message.Report.getResult() == ResultMessage.NoResult)
               {
                  // Set the contact information.
                  PreparedStatement update = users.database.connection.prepareStatement(
                     "UPDATE contacts SET email = ?, firstname = ?, lastname = ?, " +
                     "phone = ?, address = ?, country = ? WHERE contactid = ?");
                  users.database.addStatement(update);
                  update.setString(1, email);
                  update.setString(2, first);
                  update.setString(3, last);
                  update.setString(4, phone);
                  update.setString(5, address);
                  update.setString(6, country);
                  update.setInt(7, contactId);
                  update.executeUpdate();
                  message.setResult(ResultMessage.Success);
               }
            }
            else
            {
               // Get the order step.
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
                  // Make sure the new email address is unique.
                  if(users.isUserInDatabase(email) || users.isContactInDatabase(email))
                  {
                     message.setResult(ResultMessage.Exists);
                     extra.clear();
                     extra.add("first", first);
                     extra.add("last", last);
                     extra.add("country", country);
                     extra.add("phone", phone);
                     extra.add("address", address);
                     if(universityId == 0 && isAdmin)
                     {
                        message.addMessage(
                           "<br />Please enter another email address.");
                        page = "/changecontact.jsp";
                     }
                     else
                     {
                        page = "/confirmcontact.jsp";
                        extra.add("universityid", universityId);
                        extra.add("email", email);
                        extra.add("contact", contact);
                        if(orderId > 0)
                           extra.add("orderid", orderId);
                     }
                  }
                  else
                  {
                     // Add the new contact to the database.
                     contactId = users.database.getNextId("usercount", "userid");

                     PreparedStatement update =
                        users.database.connection.prepareStatement(
                        "INSERT INTO contacts(contactid, email, firstname, lastname, " +
                        "phone, address, country) VALUES (?, ?, ?, ?, ?, ?, ?)");
                     users.database.addStatement(update);
                     update.setInt(1, contactId);
                     update.setString(2, email);
                     update.setString(3, first);
                     update.setString(4, last);
                     update.setString(5, phone);
                     update.setString(6, address);
                     update.setString(7, country);
                     update.executeUpdate();
                     message.setResult(ResultMessage.Success);
                     if(universityId > 0)
                     {
                        // Set the new contact for the university.
                        DatabaseUniversities universities = new DatabaseUniversities(
                           users.database);
                        List<Integer> previous = universities.setContact(universityId,
                           contactId, contactType);
                        Iterator<Integer> iter = previous.iterator();
                        while(iter.hasNext())
                           users.removeUnusedContact(iter.next());
                     }

                     users.database.connection.commit();
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
         catch(java.sql.SQLException sqle2)
         {
            if(!users.database.connection.getAutoCommit())
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

   private void checkSetInfo(DatabaseUsers users, ResultMessageAdapter message,
      UserInfo user, int contactId, String email) throws SQLException
   {
      // Make sure the user has access to the contact.
      if(!user.isAdministrator() &&
         !users.isContactAccessible(contactId, user.getUserId()))
      {
         message.setResult(ResultMessage.NotAuthorized);
         return;
      }

      // Get the old email for the contact.
      String oldEmail = null;
      users.database.results = users.database.statement.executeQuery(
         "SELECT email FROM contacts WHERE contactid = " + contactId);
      if(users.database.results.next())
         oldEmail = users.database.results.getString("email");

      users.database.results.close();

      // Make sure the contact is in the database. If the new email
      // is different from the old one, make sure it isn't already
      // in the database.
      if(oldEmail == null)
         message.setResult(ResultMessage.Failure);
      else if(!oldEmail.equals(email) && (users.isUserInDatabase(email) ||
         users.isContactInDatabase(email)))
      {
         message.setResult(ResultMessage.Exists);
      }
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
         return 0;
      }

      // Get the university id for the order.
      int universityId = users.getUniversityId(order.getUserId());
      if(universityId == 0)
         message.setResult(ResultMessage.Failure);

      return universityId;
   }
}
/**
 * @(#)AddUserServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/3/29
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
import org.mindrot.jbcrypt.BCrypt;

public class AddUserServlet extends HttpServlet {

   public AddUserServlet() {
   }

   public static class Message implements ResultMessageAdapter {
      public ResultMessage Report = null;
      public boolean Admin = false;

      public Message(ResultMessage message) {
         this.Report = message;
      }

      @Override
      public void setResult(int result) {
         this.Report.setResult(result);
         if (result == ResultMessage.Success) {
            this.Report.setType(ResultMessage.InfoMessage);
            this.Report.setMessage("Thank you for signing up at coreform.com!");
         } else if (result == ResultMessage.Failure) {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage("We're sorry. The sign up process has failed due to an internal "
                  + "error. Please send us an email or try again later.");
         } else if (result == ResultMessage.Exists) {
            this.Report.setType(ResultMessage.WarningMessage);
            this.Report
                  .setMessage("The specified email address already has an associated account.");
         }
      }

      @Override
      public void addException(Exception e) {
         if (this.Admin)
            this.Report.appendToMessage("<br />" + e);
      }

      @Override
      public void addMessage(String extra) {
         this.Report.appendToMessage(extra);
      }
   }

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
         throws IOException, ServletException {
      // Make sure the character decoding is correct.
      if (request.getCharacterEncoding() == null)
         request.setCharacterEncoding("UTF-8");

      // Get the request parameters.
      String first = request.getParameter("first");
      String last = request.getParameter("last");
      String email = request.getParameter("email");
      String password = request.getParameter("pass");
      String confirm = request.getParameter("confirm");
      String company = request.getParameter("company");
      String phone = request.getParameter("phone");
      String address = request.getParameter("address");
      String country = request.getParameter("country");
      String ind_or_edu = request.getParameter("ind_or_edu");
      String industry = "";
      String edu_kind = "";
      String opt_in = request.getParameter("opt-in");
      boolean newsletter = false;
      if (opt_in != null && opt_in.equals("on")) {
         newsletter = true;
      }

      if (ind_or_edu.equals("industry")) {
         industry = request.getParameter("ind_specific");
      } else {
         industry = "Education";
         edu_kind = request.getParameter("edu_specific");
      }

      String contextPath = request.getContextPath();
      // This checks for export restricted countries. It's circumventable, but no uncircumventable
      // methods exist.
      // I was told to do this by Matt Sederberg. -Scot Wilcox, 7/10/2020
      String countryLower = country.toLowerCase();
      if (countryLower.equals("iran") || countryLower.equals("north korea")
            || countryLower.equals("sudan") || countryLower.equals("cuba") || countryLower.equals("russia")) {
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/export/"));
         return;
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      AddUserServlet.Message message =
            new AddUserServlet.Message(LoginServlet.getResultMessage(session));

      UserInfo user = LoginServlet.getUserInfo(session);
      boolean isAdmin = user.isAdministrator();
      message.Admin = isAdmin;

      // Make sure all the required fields are not null.
      if (first == null || first.isEmpty() || last == null || last.isEmpty() || email == null
            || email.isEmpty() || password == null || password.isEmpty() || confirm == null
            || !confirm.equals(password) || country == null || country.isEmpty() || phone == null
            || phone.isEmpty() || address == null || address.isEmpty()) {
         // Send the request parameters back to the form.
         ParameterBuilder params = new ParameterBuilder();
         params.add("first", first);
         params.add("last", last);
         params.add("email", email);
         params.add("company", company);
         params.add("phone", phone);
         params.add("address", address);
         params.add("country", country);
         response.sendRedirect(response.encodeRedirectURL(contextPath + "/signup.jsp" + params));
         return;
      }

      String page = "/index.jsp";
      int userId = createAccount(message, first, last, email, password, company, phone, address,
            country, industry, edu_kind, "", false);
      if (message.Report.getResult() == ResultMessage.Exists) {
         if (!isAdmin) {
            message.addMessage(" If you have forgotten your password, please click " + "<a href=\""
                  + contextPath + "/forgot.jsp\">here</a> " + "to have it emailed to you.");
         } else {
            message.addMessage("The account already exists");
         }
      } else if (userId != -1) {
         if (isAdmin)
            page = "/clientinfo.jsp?user=" + userId;
         else {
            MailchimpApi.addToMailchimp(email, first, last, company, newsletter);
            // Log in the new user.
            LoginServlet.loginUser(session);
            user.setUserId(userId);

            // Send an email to the administrator.
            csimsoft.Email messenger = new csimsoft.Email();
            messenger.sendUserNotification(userId, email, first, last);
            try {
               // If the user was ordering something, send them back
               // to that process.
               String orderType = (String) session.getAttribute("ordertype");
               String orderItem = (String) session.getAttribute("orderitem");
               String orderDays = null;
               String orderSource = null;
               try {
                  orderDays = (String) session.getAttribute("orderdays");
               } catch (Exception e) {
               }

               try {
                  orderSource = (String) session.getAttribute("ordersource");
               } catch (Exception e) {
               }

               if (orderType != null && !orderType.isEmpty() && orderItem != null
                     && !orderItem.isEmpty()) {
                  page = "/neworder?" + orderType + "=" + orderItem;
                  if (orderDays != null && !orderDays.isEmpty())
                     page += "&days=" + orderDays;
                  if (orderSource != null && !orderSource.isEmpty())
                     page += "&source=" + orderSource;

                  session.removeAttribute("ordertype");
                  session.removeAttribute("orderitem");
                  session.removeAttribute("orderdays");
                  session.removeAttribute("ordersource");
               }
            } catch (Exception e) {
            }
         }
      }
      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }

   public static int createAccount(AddUserServlet.Message message, String first, String last,
         String email, String password, String company, String phone, String address,
         String country, String industry, String edu_kind, String plannedUse, Boolean blogAllowed) {
      int userId = -1;
      DatabaseUsers users = new DatabaseUsers();
      try {
         users.database.connect();
         users.database.connection.setAutoCommit(false);

         if (users.isUserInDatabase(email)) {
            message.setResult(ResultMessage.Exists);
         } else {
            PreparedStatement statement = users.database.connection
                  .prepareStatement("INSERT INTO users (userid, email, password, firstname, "
                        + "lastname, groupid, company, phone, address, country, industry, edutype, planneduse, blogallowed) "
                        + "VALUES (?, ?, ?, ?, ?, 0, ?, ?, ?, ?, ?, ?, ?, ?)");
            users.database.addStatement(statement);
            try {
               // Make sure the strings will fit in the database.
               if (company == null)
                  company = "";
               else if (company.length() > 128)
                  company = company.substring(0, 128);

               if (phone.length() > 32)
                  phone = phone.substring(0, 32);

               if (address.length() > 512)
                  address = address.substring(0, 512);

               if (country.length() > 32)
                  country = country.substring(0, 32);

               if (plannedUse.length() > 512)
                  plannedUse = plannedUse.substring(0, 512);

               // Get the current user id from the database.
               userId = users.database.getNextId("usercount", "userid");
               String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());

               // Add the user to the database.
               statement.setInt(1, userId);
               statement.setString(2, email);
               statement.setString(3, hashPassword);
               statement.setString(4, first);
               statement.setString(5, last);
               statement.setString(6, company);
               statement.setString(7, phone);
               statement.setString(8, address);
               statement.setString(9, country);
               statement.setString(10, industry);
               statement.setString(11, edu_kind);
               statement.setString(12, plannedUse);
               statement.setBoolean(13, blogAllowed);
               statement.executeUpdate();

               users.database.connection.commit();
               message.setResult(ResultMessage.Success);
               /*UserProfileInfo userInfo = new UserProfileInfo();
               userInfo.setId(userId);
               userInfo.setFirstName(first);
               userInfo.setLastName(last);
               userInfo.setEmail(email);
               userInfo.setCompany(company);
               userInfo.setPhone(phone);
               userInfo.setAddress(address);
               userInfo.setCountry(country);
               userInfo.setIndustry(industry);
               userInfo.setEduType(edu_kind);
               userInfo.setPlannedUse(plannedUse);
               userInfo.setBlogAllowed(blogAllowed);
               csimsoft.SalesforceApiServlet.newTrialSignup(userInfo);*/

            } catch (SQLException sqle2) {
               AcctLogger.get().info(sqle2.toString());
               message.setResult(ResultMessage.Failure);
               users.database.connection.rollback();
               message.addException(sqle2);
            }
         }
      } catch (SQLException sqle) {
         message.setResult(ResultMessage.ResourceError);
         message.addException(sqle);
      } catch (NamingException ne) {
         message.setResult(ResultMessage.ResourceError);
         message.addException(ne);
      }

      users.database.cleanup();

      return userId;
   }
}


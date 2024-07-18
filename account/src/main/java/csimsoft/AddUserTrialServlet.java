/**
 * @(#)AddUserTrialServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/3/29
 */

package csimsoft;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



public class AddUserTrialServlet extends HttpServlet {

   public AddUserTrialServlet() {
   }

   public static class Message implements ResultMessageAdapter {
      public ResultMessage Report = null;

      public Message(ResultMessage message) {
         this.Report = message;
      }

      @Override
      public void setResult(int result) {
         this.Report.setResult(result);
         if (result == ResultMessage.Success) {
            this.Report.setType(ResultMessage.InfoMessage);
            this.Report.setMessage("Thank you for signing up for your free trial!");
         } else if (result == ResultMessage.Failure) {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage("We're sorry. The sign up process has failed due to an internal "
                  + "error. Please send us an email or try again later.");
         } else if (result == ResultMessage.NotAuthorized) {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
                  "This account has already ordered a free trial.  Please contact us for more information.");
         } else if (result == ResultMessage.Exists) {
            this.Report.setType(ResultMessage.WarningMessage);
            this.Report
                  .setMessage("The specified email address already has an associated account.");
         } else if (result == ResultMessage.ResourceError) {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage("Resource error: ");
         }
      }

      @Override
      public void addException(Exception e) {
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

      int licenseId = 0;
      int numDays = 0;
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
      String opt_in = request.getParameter("opt-in");
      boolean newsletter = false;
      if (opt_in != null && opt_in.equals("on")) {
         newsletter = true;
      }

      String industry = "";
      String edu_kind = "";

      if (ind_or_edu.equals("industry")) {
         industry = request.getParameter("ind_specific");
      } else {
         industry = "Education";
         edu_kind = request.getParameter("edu_specific");
      }
      String license = request.getParameter("license");
      String days = request.getParameter("days");
      String lead = request.getParameter("source");


      String page = "/signuptrial.jsp";

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      AddUserTrialServlet.Message message =
            new AddUserTrialServlet.Message(LoginServlet.getResultMessage(session));

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
         response
               .sendRedirect(response.encodeRedirectURL(contextPath + "/signuptrial.jsp" + params));
         return;
      }
      try {
         if (license != null && !license.isEmpty()) {
            licenseId = Integer.parseInt(license);
            if (days != null && !days.isEmpty()) {
               numDays = Integer.parseInt(days);
            }
         } else {
            message.setResult(ResultMessage.Failure);
            message.addMessage("The license parameters were invalid");
            response.sendRedirect(response.encodeRedirectURL(contextPath + page));
            return;
         }
      } catch (Exception nfe) {
         message.setResult(ResultMessage.Failure);
         message.addException(nfe);
         response.sendRedirect(response.encodeRedirectURL(contextPath + page));
         return;
      }

      AddUserServlet.Message acctMessage =
            new AddUserServlet.Message(LoginServlet.getResultMessage(session));
      int userId = AddUserServlet.createAccount(acctMessage, first, last, email, password, company,
            phone, address, country, industry, edu_kind, "", false);
      // Log in the new user.
      LoginServlet.loginUser(session);
      UserInfo user = LoginServlet.getUserInfo(session);
      user.setUserId(userId);

      if (acctMessage.Report.getResult() == ResultMessage.Success) {
         activateTrial(message, userId, licenseId, numDays, lead);

         page = "/trialthankyou.jsp";
         csimsoft.MailchimpApi.sendMailchimpWelcome(email, first, last, company, newsletter);
      } else if (acctMessage.Report.getResult() == ResultMessage.Exists) {
         message.setResult(ResultMessage.Exists);
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }

   public static void activateTrial(AddUserTrialServlet.Message message, int userId, 
         int licenseId, int numDays, String lead) {
      DatabaseUsers users = new DatabaseUsers();
      AcctLogger.get().info("Start activateTrial");
      try {
         users.database.connect();
         users.database.connection.setAutoCommit(false);
         DatabaseLicenses licenses = new DatabaseLicenses(users.database);
         PreparedStatement query = users.database.connection
               .prepareStatement("SELECT trialorder FROM users WHERE userid=?");
         query.setInt(1, userId);
         users.database.addStatement(query);
         users.database.results = query.executeQuery();
         boolean alreadyOrderedTrial = false;
         if (users.database.results.next()) {
            alreadyOrderedTrial = users.database.results.getBoolean(1);
         }
         users.database.results.close();
         if (!alreadyOrderedTrial) {
            // Set a default expiration.
            String expiration = null;
            if (numDays < 1) {
               java.util.Calendar date = java.util.Calendar.getInstance();
               date.add(java.util.Calendar.MONTH, 1);
               date.set(java.util.Calendar.DAY_OF_MONTH,
                     date.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));

               expiration = Database.formatDate(date);
               AcctLogger.get().info("set expiration");
            }

            // Create a new instance of the license.
            licenses.addClientLicense(userId, licenseId, expiration, numDays);
         }

         // Mark this user as having ordered a trial
         PreparedStatement updateTrial = users.database.connection
               .prepareStatement("UPDATE users SET trialorder=? WHERE userid=?");
         updateTrial.setBoolean(1, true);
         updateTrial.setInt(2, userId);
         users.database.addStatement(updateTrial);
         updateTrial.executeUpdate();
         users.database.connection.commit();
         message.setResult(ResultMessage.Success);
      } catch (Exception sqle) {
         message.setResult(ResultMessage.ResourceError);
         message.addException(sqle);
      }
      users.database.cleanup();
      AcctLogger.get().info("End activateTrial");
   }
}



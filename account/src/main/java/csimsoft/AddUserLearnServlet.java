/**
 * @(#)AddUserLearnServlet.java
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


public class AddUserLearnServlet extends HttpServlet {

   public AddUserLearnServlet() {
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
            this.Report.setMessage("Thank you for signing up for Cubit Learn!");
         } else if (result == ResultMessage.Failure) {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage("We're sorry. The sign up process has failed due to an internal "
                  + "error. Please send us an email or try again later.");
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
      String opt_in = request.getParameter("opt-in");
      String plannedUse = request.getParameter("planneduse");
      String blog = request.getParameter("blog");
      boolean newsletter = false;
      if (opt_in != null && opt_in.equals("on")) {
         newsletter = true;
      }
      boolean blogAllowed = false;
      if (blog != null && blog.equals("on")) {
         blogAllowed = true;
      }

      String industry = "Education";
      String edu_kind = "Student";

      String page = "/signuplearn.jsp";

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      AddUserLearnServlet.Message message =
            new AddUserLearnServlet.Message(LoginServlet.getResultMessage(session));

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
         params.add("planneduse", plannedUse);
         response
               .sendRedirect(response.encodeRedirectURL(contextPath + "/signuplearn.jsp" + params));
         return;
      }

      AddUserServlet.Message acctMessage =
            new AddUserServlet.Message(LoginServlet.getResultMessage(session));
      int userId = AddUserServlet.createAccount(acctMessage, first, last, email, password, company,
            phone, address, country, industry, edu_kind, plannedUse, blogAllowed);
      // Log in the new user.
      LoginServlet.loginUser(session);
      UserInfo user = LoginServlet.getUserInfo(session);
      user.setUserId(userId);

      if (acctMessage.Report.getResult() == ResultMessage.Success) {
         int userlicenseid = activateLearn(message, userId);

         page = "/licensecubitlearn.jsp?license=" + userlicenseid;
         message.setResult(ResultMessage.Success);
         csimsoft.MailchimpApi.sendMailchimpLearnWelcome(email, first, last, company, newsletter);
      } else if (acctMessage.Report.getResult() == ResultMessage.Exists) {
         message.setResult(ResultMessage.Exists);
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }

   public static int activateLearn(AddUserLearnServlet.Message message, int userId) {
      int userlicenseid = 0;
      DatabaseUsers users = new DatabaseUsers();
      try {
         users.database.connect();
         users.database.connection.setAutoCommit(false);
         DatabaseLicenses licenses = new DatabaseLicenses(users.database);
         try {
            // Create a new instance of the license.
            userlicenseid = licenses.addLearnLicense(userId);
         } catch (SQLException sqle2) {
            message.setResult(ResultMessage.Failure);
            users.database.connection.rollback();
            message.addException(sqle2);
         }
      } catch (SQLException sqle) {
         message.setResult(ResultMessage.ResourceError);
         message.addException(sqle);
      } catch (NamingException ne) {
         message.setResult(ResultMessage.ResourceError);
         message.addException(ne);
      }
      users.database.cleanup();
      return userlicenseid;
   }
}



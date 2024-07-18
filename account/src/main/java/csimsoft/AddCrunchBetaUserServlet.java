/**
 * @(#)AddCrunchBetaUserServlet.java.java
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

public class AddCrunchBetaUserServlet extends HttpServlet {

   public AddCrunchBetaUserServlet() {
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

      MailchimpApi.addToMailchimp(email, first, last, "", true);
      response.sendRedirect("https://coreform.com/products/crunch-beta-thankyou/");

   }

}


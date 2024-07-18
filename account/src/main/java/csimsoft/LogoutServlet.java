/**
 * @(#)LogoutServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/3/21
 */

package csimsoft;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class LogoutServlet extends HttpServlet
{

   public LogoutServlet()
   {
   }

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
   {
      // Update the login state for the session.
      HttpSession session = request.getSession(false);
      if(session != null)
         session.invalidate();

      // Redirect the user back to the account page.
      response.sendRedirect(response.encodeRedirectURL(
            request.getContextPath() + "/index.jsp"));
   }

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
   {
      doGet(request, response);
   }
}


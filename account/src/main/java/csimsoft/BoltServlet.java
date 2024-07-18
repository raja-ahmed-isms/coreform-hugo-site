package csimsoft;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class BoltServlet extends HttpServlet
{

   public BoltServlet()
   {
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
   {
      // Redirect the user to the lower-case bolt page.
      String contextPath = request.getContextPath();
      String page = "/bolt/index.jsp";
      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

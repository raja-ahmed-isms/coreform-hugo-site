/**
 * @(#)TranslateOrderServlet.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/11/9
 */

package csimsoft;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class TranslateOrderServlet extends HttpServlet
{

   public TranslateOrderServlet()
   {
   }

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
   {
      // Get the contect path from the session.
      String contextPath = request.getContextPath();

      String page = null;
      DatabaseProducts products = new DatabaseProducts();
      DatabaseLicenses licenses = new DatabaseLicenses(products.database);
      try
      {
         products.database.connect();
         products.database.statement = products.database.connection.createStatement();

         // The order request could be for a product or license.
         String license = request.getParameter("license");
         if(license != null && !license.isEmpty())
         {
            // Get the license id from the database.
            int licenseId = licenses.getLicenseId(license);
            if(licenseId > 0)
            {
               page = "/neworder?license=" + licenseId;
               String days = request.getParameter("days");
               if(days != null && !days.isEmpty())
                  page += "&days=" + days;

               String source = request.getParameter("source");
               if(source != null && !source.isEmpty())
                  page += "&source=" + source;
            }
         }

         // Get the product path name from the request.
         String product = request.getParameter("product");
         if(product != null && !product.isEmpty())
         {
            // Get the product id from the database.
            int productId = products.getProductIdFromName(product);
            if(productId > 0)
               page = "/neworder?product=" + productId;
         }
      }
      catch(Exception e)
      {
      }

      products.database.cleanup();

      if(page == null)
         page = "/order.jsp?product=unknown";

      // Forward the request to the order confirmation page.
      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}
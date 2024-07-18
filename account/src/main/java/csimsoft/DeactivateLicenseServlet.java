/**
 *
 * @author Mark Richardson
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


public class DeactivateLicenseServlet extends HttpServlet
{

   public DeactivateLicenseServlet()
   {
   }

   private static class Message implements ResultMessageAdapter
   {
      public ResultMessage Report = null;
      public boolean Admin = false;
      public boolean Distrib = false;

      public Message(ResultMessage message)
      {
         this.Report = message;
      }

      @Override
      public void setResult(int result)
      {
         this.Report.setResult(result);
         if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to deactivate the product license.");
         }
         else if(result == ResultMessage.Success)
         {
            this.Report.setType(ResultMessage.InfoMessage);
            this.Report.setMessage(
               "The product license was successfully deactivated.");
         }
         else if(result == ResultMessage.DoesNotExist)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The specified product license key does not exist.");
         }
      }

      @Override
      public void addException(Exception e)
      {
         if(this.Admin || this.Distrib)
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

      // Get the parameters from the request.
      String hostId = request.getParameter("hostid");
      String licenseKey = request.getParameter("licensekey");
      if(licenseKey != null)
         licenseKey = licenseKey.replace("-", "");

      boolean noXml = "no".equalsIgnoreCase(request.getParameter("xml"));

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      DeactivateLicenseServlet.Message message = new DeactivateLicenseServlet.Message(
         LoginServlet.getResultMessage(session));

      // Check if the user is an administrator.
      if(LoginServlet.isUserLoggedIn(session))
      {
         UserInfo user = LoginServlet.getUserInfo(session);
         message.Admin = user.isAdministrator();
         message.Distrib = user.isDistributor();
      }

      int licenseId = 0;
      if(hostId == null || hostId.isEmpty() || licenseKey == null ||
         licenseKey.isEmpty())
      {
         message.setResult(ResultMessage.Failure);
         message.addMessage(" The request is missing parameters.");
      }
      else
      {
         Database database = new Database();
         try
         {
            database.connect();
            database.connection.setAutoCommit(false);
            try
            {
               // Get the license id from the database.
               int deactivations = 0;
               int maxDeactivations = 0;
               PreparedStatement query = database.connection.prepareStatement(
                  "SELECT userlicenseid, deactivations, maxdeactivations " +
                  "FROM licenses WHERE licensekey = ?");
               database.addStatement(query);
               query.setString(1, licenseKey);
               database.results = query.executeQuery();
               if(database.results.next())
               {
                  licenseId = database.results.getInt("userlicenseid");
                  deactivations = database.results.getInt("deactivations");
                  maxDeactivations = database.results.getInt("maxdeactivations");
               }

               database.results.close();
               if(licenseId < 1)
                  message.setResult(ResultMessage.DoesNotExist);
               else if(!message.Admin && !message.Distrib && deactivations >= maxDeactivations)
               {
                  message.setResult(ResultMessage.Failure);
                  message.addMessage(
                     " You have exceeded the number of allowed deactivations.");
               }
               else
               {
                  // Deactivate the license.
                  PreparedStatement update1 = database.connection.prepareStatement(
                     "DELETE FROM activationfeatures WHERE activationid IN " +
                     "(SELECT activationid FROM licenseactivations " +
                     "WHERE userlicenseid = ? AND hostid = ?)");
                  database.addStatement(update1);
                  update1.setInt(1, licenseId);
                  update1.setString(2, hostId);
                  update1.execute();
                  PreparedStatement update2 = database.connection.prepareStatement(
                     "DELETE FROM licenseactivations " +
                     "WHERE userlicenseid = ? AND hostid = ?");
                  database.addStatement(update2);
                  update2.setInt(1, licenseId);
                  update2.setString(2, hostId);
                  update2.execute();

                  if(database.statement == null)
                     database.statement = database.connection.createStatement();

                  // Clean up any floating license virtual machine signatures
                  // left behind.
                  database.statement.executeUpdate(
                     "DELETE FROM vmsign WHERE licenseid = " + licenseId);

                  // Keep track of the number of deactivations.
                  deactivations++;
                  database.statement.executeUpdate(
                     "UPDATE licenses SET deactivations = " + deactivations +
                     " WHERE userlicenseid = " + licenseId);

                  database.connection.commit();
                  message.setResult(ResultMessage.Success);
               }
            }
            catch(SQLException sqle2)
            {
               message.setResult(ResultMessage.Failure);
               message.addException(sqle2);
               database.connection.rollback();
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

         database.cleanup();
      }
      if(noXml)
      {
          String page = request.getContextPath();
          if(message.Admin || message.Distrib)
          {
             if(licenseId > 0)
                page += "/clientlicense.jsp?license=" + licenseId;
             else
                page += "/allproducts.jsp";
          }
          else
          {
             if(licenseId > 0)
                page += "/licensedetail.jsp?license=" + licenseId;
             else
                page += "/licenses.jsp";
          }
          response.sendRedirect(response.encodeRedirectURL(page));
      }
      else
      {
         message.Report.sendXml(response);
      }
   }
}

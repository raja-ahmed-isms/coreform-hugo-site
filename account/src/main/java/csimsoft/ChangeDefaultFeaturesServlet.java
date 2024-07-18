/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.TreeSet;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class ChangeDefaultFeaturesServlet extends HttpServlet
{

   public ChangeDefaultFeaturesServlet()
   {
   }

   private static class Message implements ResultMessageAdapter
   {
      public ResultMessage Report = null;

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
               "The default license features have been successfully changed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to change the default license features.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to change the default license features." +
               "<br />Please log in as an administrator to do so.");
         }
      }

      @Override
      public void addException(Exception e)
      {
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
      // Get the parameters.
      int licenseId = 0;
      try
      {
         licenseId = Integer.parseInt(request.getParameter("license"));
      }
      catch(Exception e)
      {
      }

      TreeSet<Integer> selected = new TreeSet<Integer>();
      ParameterList params = new csimsoft.ParameterList();
      params.setParameters(request);
      params.removeParameter("license");
      for(int i = 0; i < params.getParameterCount(); ++i)
      {
         try
         {
            String feature = params.getParameterName(i);
            if(feature != null && feature.equals(request.getParameter(feature)))
               selected.add(Integer.parseInt(feature));
         }
         catch(Exception e)
         {
         }
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ChangeDefaultFeaturesServlet.Message message =
         new ChangeDefaultFeaturesServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is logged in.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      boolean isAdmin = user.isAdministrator();
      if(!LoginServlet.isUserLoggedIn(session))
         message.setResult(ResultMessage.LoginError);
      else if(!isAdmin)
         message.setResult(ResultMessage.NotAuthorized);
      else if(licenseId < 1)
      {
         page = "/allproducts.jsp";
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />The given license id is not valid.");
      }
      else
      {
         page = "/licensefeatures.jsp?license=" + licenseId;
         DatabaseUsers users = new DatabaseUsers();
         DatabaseLicenses licenses = new DatabaseLicenses(users.database);
         try
         {
            users.database.connect();
            users.database.connection.setAutoCommit(false);
            users.database.statement = users.database.connection.createStatement();
            try
            {
               // Get the list of current default features.
               TreeSet<Integer> current = new TreeSet<Integer>();
               licenses.getDefaultFeatures(licenseId);
               while(licenses.database.results.next())
                  current.add(licenses.database.results.getInt("featureid"));

               licenses.database.results.close();

               // Add the new default features.
               int count = 0;
               Iterator<Integer> iter = selected.iterator();
               while(iter.hasNext())
               {
                  int featureId = iter.next();
                  if(!current.contains(featureId))
                  {
                     ++count;
                     licenses.database.statement.executeUpdate(
                        "UPDATE licensefeatures SET autoadd = 1 WHERE featureid = " +
                        featureId + " AND licenseid = " + licenseId);
                  }
               }

               // Remove the unselected features.
               iter = current.iterator();
               while(iter.hasNext())
               {
                  int featureId = iter.next();
                  if(!selected.contains(featureId))
                  {
                     ++count;
                     licenses.database.statement.executeUpdate(
                        "UPDATE licensefeatures SET autoadd = 0 WHERE featureid = " +
                        featureId + " AND licenseid = " + licenseId);
                  }
               }

               if(count > 0)
                  licenses.database.connection.commit();

               message.setResult(ResultMessage.Success);
            }
            catch(SQLException sqle2)
            {
               users.database.connection.rollback();
               message.setResult(ResultMessage.Failure);
               message.addException(sqle2);
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

         users.database.cleanup();
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

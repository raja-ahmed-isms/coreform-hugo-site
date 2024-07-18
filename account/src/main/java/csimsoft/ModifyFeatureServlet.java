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


public class ModifyFeatureServlet extends HttpServlet
{

   public ModifyFeatureServlet()
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
               "The product feature has been successfully modified.");
         }
         else if(result == ResultMessage.Exists)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The new product feature name already exists.");
         }
         else if(result == ResultMessage.DoesNotExist)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "The given product feature does not exist.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to modify the product feature.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to modify a product feature.<br />" +
               "Please log in as an administrator to do so.");
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
      // Make sure the character decoding is correct.
      if(request.getCharacterEncoding() == null)
         request.setCharacterEncoding("UTF-8");

      // Get the request parameters.
      String featureName = request.getParameter("featurename");
      String featureKey = request.getParameter("featurekey");
      int featureId = 0;
      try
      {
         featureId = Integer.parseInt(request.getParameter("feature"));
      }
      catch(Exception e)
      {
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ModifyFeatureServlet.Message message = new ModifyFeatureServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin to modify a license group.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      if(!LoginServlet.isUserLoggedIn(session))
      {
         message.setResult(ResultMessage.LoginError);
         message.addMessage(
            "You must log in as an administrator to modify a feature.");
      }
      else if(!user.isAdministrator())
         message.setResult(ResultMessage.NotAuthorized);
      else if(featureId < 1 || featureName == null || featureName.isEmpty() ||
         featureKey == null || featureKey.isEmpty())
      {
         page = "/allfeatures.jsp";
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />Missing required parameters.");
      }
      else
      {
         page = "/allfeatures.jsp";
         if(featureName.length() > 64)
            featureName = featureName.substring(0, 64);

         if(featureKey.length() > 32)
            featureKey = featureKey.substring(0, 32);

         DatabaseLicenses licenses = new DatabaseLicenses();
         try
         {
            licenses.database.connect();
            try
            {
               // Get the current option name from the database.
               String currentName = null;
               licenses.getFeatureInfo(featureId);
               if(licenses.database.results.next())
                  currentName = licenses.database.results.getString("featurename");

               licenses.database.results.close();

               // Make sure the feature name is not a duplicate.
               if(currentName == null)
                  message.setResult(ResultMessage.DoesNotExist);
               if(!currentName.equals(featureName) &&
                  licenses.isFeatureInDatabase(featureName))
               {
                  message.setResult(ResultMessage.Exists);
                  page = "/featureinfo.jsp?feature=" + featureId;
               }
               else
               {
                  PreparedStatement statement =
                     licenses.database.connection.prepareStatement(
                     "UPDATE features SET featurename = ?, featurekey = ? " +
                     "WHERE featureid = ?");
                  licenses.database.addStatement(statement);
                  statement.setString(1, featureName);
                  statement.setString(2, featureKey);
                  statement.setInt(3, featureId);
                  statement.executeUpdate();
                  message.setResult(ResultMessage.Success);
               }
            }
            catch(SQLException sqle2)
            {
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

         licenses.database.cleanup();
      }

      response.sendRedirect(response.encodeRedirectURL(contextPath + page));
   }
}

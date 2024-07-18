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


public class ChangeUserOptionsServlet extends HttpServlet
{

   public ChangeUserOptionsServlet()
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
               "The license options have been successfully changed.");
         }
         else if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to change the license options.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to change the license options." +
               "<br />Please log in as an administrator or distributor to do so.");
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
            String option = params.getParameterName(i);
            if(option != null && option.equals(request.getParameter(option)))
               selected.add(new Integer(Integer.parseInt(option)));
         }
         catch(Exception e)
         {
         }
      }

      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ChangeUserOptionsServlet.Message message = new ChangeUserOptionsServlet.Message(
         LoginServlet.getResultMessage(session));

      // Make sure the user is logged in.
      String contextPath = request.getContextPath();
      String page = "/index.jsp";
      UserInfo user = LoginServlet.getUserInfo(session);
      boolean isAdmin = user.isAdministrator();
      boolean isDistributor = user.isDistributor();
      if(!LoginServlet.isUserLoggedIn(session))
         message.setResult(ResultMessage.LoginError);
      else if(!isAdmin && !isDistributor)
         message.setResult(ResultMessage.NotAuthorized);
      else if(licenseId < 1)
      {
         page = "/clientlicenses.jsp";
         message.setResult(ResultMessage.Failure);
         message.addMessage(
            "<br />The given license id is not valid.");
      }
      else
      {
         page = "/clientlicense.jsp?license=" + licenseId;
         DatabaseUsers users = new DatabaseUsers();
         DatabaseLicenses licenses = new DatabaseLicenses(users.database);
         try
         {
            users.database.connect();
            users.database.connection.setAutoCommit(false);
            users.database.statement = users.database.connection.createStatement();
            try
            {
               // Make sure the license id is valid. Also, make sure the
               // distributor can access the license.
               int userId = licenses.getUserLicenseOwner(licenseId);
               if(userId < 1)
               {
                  message.setResult(ResultMessage.Failure);
                  message.addMessage(
                     "<br />The given license id is not valid.");
               }
               else if(isAdmin || users.isDistributor(userId, user.getUserId()))
               {
                  // Get the list of currently selected options.
                  TreeSet<Integer> current = new TreeSet<Integer>();
                  licenses.database.results = licenses.database.statement.executeQuery(
                     "SELECT optionid FROM useroptions WHERE userlicenseid = " +
                     licenseId);
                  while(licenses.database.results.next())
                  {
                     current.add(new Integer(
                        licenses.database.results.getInt("optionid")));
                  }

                  licenses.database.results.close();

                  // Add the newly selected options.
                  int count = 0;
                  Iterator<Integer> iter = selected.iterator();
                  while(iter.hasNext())
                  {
                     Integer optionId = iter.next();
                     if(!current.contains(optionId))
                     {
                        ++count;
                        licenses.database.statement.executeUpdate(
                           "INSERT INTO useroptions (userlicenseid, optionid) " +
                           "VALUES (" + licenseId + ", " + optionId.intValue() + ")");
                     }
                  }

                  // Remove the unselected options.
                  iter = current.iterator();
                  while(iter.hasNext())
                  {
                     Integer optionId = iter.next();
                     if(!selected.contains(optionId))
                     {
                        ++count;
                        licenses.database.statement.executeUpdate(
                           "DELETE FROM useroptions WHERE userlicenseid = " +
                           licenseId + " AND optionid = " + optionId.intValue());
                     }
                  }

                  if(count > 0)
                  {
                     // Clear out the signatures for the license when
                     // there is a change to the options.
                     licenses.removeUserLicenseSignatures(licenseId);
                     licenses.database.connection.commit();
                  }

                  message.setResult(ResultMessage.Success);
               }
               else
               {
                  page = "/clientlicenses.jsp";
                  message.setResult(ResultMessage.NotAuthorized);
               }
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

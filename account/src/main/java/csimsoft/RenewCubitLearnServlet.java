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

public class RenewCubitLearnServlet extends HttpServlet {

	public RenewCubitLearnServlet()
	{
	}

	private static class Message implements ResultMessageAdapter
	{
		public ResultMessage Report = null;
		public boolean Admin = false;

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
				this.Report.setMessage("Your license has been renewed.");
			}
			else if(result == ResultMessage.Failure)
			{
				this.Report.setType(ResultMessage.ErrorMessage);
				this.Report.setMessage( "An internal error has occurred." );
			}
		}

		@Override
		public void addException(Exception e)
		{
			if(this.Admin)
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
		// Get the result message object for the session.
		HttpSession session = request.getSession();
		RenewCubitLearnServlet.Message message = new RenewCubitLearnServlet.Message(
			LoginServlet.getResultMessage(session));

		// Make sure the user is logged in.
		String contextPath = request.getContextPath();
		String page = "/index.jsp";
		UserInfo user = LoginServlet.getUserInfo(session);
		boolean isAdmin = user.isAdministrator();
		boolean isDistributor = user.isDistributor();
		message.Admin = isAdmin || isDistributor;

		if(!LoginServlet.isUserLoggedIn(session))
			message.setResult(ResultMessage.LoginError);
		else {
			// Get the parameters.
			int licenseId = 0;
			try
			{
				licenseId = Integer.parseInt(request.getParameter("license"));
			}
			catch(Exception e)
			{
				message.setResult(ResultMessage.Failure);
				message.addMessage("<br />The given license id is not valid.");
			}

			DatabaseUsers users = new DatabaseUsers();
         		DatabaseOrders orders = new DatabaseOrders(users.database);
			try
			{
				users.database.connect();
				users.database.connection.setAutoCommit(false);
				users.database.statement = users.database.connection.createStatement();
				try
				{
					// Make sure the license id is valid.
					int userId = 0;
					int licenseTemplateId = 0;
					users.database.results = users.database.statement.executeQuery(
						"SELECT userid, licenseid FROM licenses " +
						"WHERE userlicenseid = " + licenseId);
					if(users.database.results.next())
					{
						userId = users.database.results.getInt("userid");
						licenseTemplateId = users.database.results.getInt("licenseid");
					}
					users.database.results.close();
					if(userId < 1)
					{
						message.setResult(ResultMessage.Failure);
						message.addMessage(
						"<br />The given license id is not valid.");
					}
					else
					{	
						// Make sure the distributor or user can access the license.
						boolean allowed = false;
						if(isAdmin) {
							allowed = true;
						}
						else if(isDistributor) {
							allowed = users.isDistributor(userId, user.getUserId());
						}
						else if(userId == user.getUserId()) {
							allowed = true;
						}

						if(allowed)
						{
							if(isAdmin || isDistributor)
								page = "/clientlicense.jsp?license=" + licenseId;
							else
								page = "/licensecubitlearn.jsp?license=" + licenseId;
							String licensename = "";
							//Make sure it's a Cubit Learn license
							users.database.results = users.database.statement.executeQuery(
								"SELECT licensename FROM licenseproducts WHERE licenseid = " + licenseTemplateId);
							if(users.database.results.next())
							{
								licensename = users.database.results.getString("licensename");
							}
							users.database.results.close();
							if(licensename.contains("Cubit Learn")) 
							{
								String update = "UPDATE licenses SET expiration = ? WHERE userlicenseid = ?";
								PreparedStatement statement = users.database.connection.prepareStatement(update);
								java.util.Calendar date = java.util.Calendar.getInstance();
								date.add(java.util.Calendar.YEAR, 1);
								date.set(java.util.Calendar.DAY_OF_MONTH,
								date.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
								String expiration = Database.formatDate(date);
								statement.setString(1, expiration);
								statement.setInt(2, licenseId);
								statement.executeUpdate();
								users.database.connection.commit();
								message.setResult(ResultMessage.Success);
							}
							else
							{
								message.setResult(ResultMessage.NotAuthorized);
							}
						}
						else
						{
							message.setResult(ResultMessage.NotAuthorized);
						}
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

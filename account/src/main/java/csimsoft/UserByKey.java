package csimsoft;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserByKey extends HttpServlet {

    public UserByKey() {
    }

    private static class Message implements ResultMessageAdapter {
        public ResultMessage Report = null;

        public Message(ResultMessage message) {
            this.Report = message;
        }

        @Override
        public void setResult(int result) {
            this.Report.setResult(result);
            if (result == ResultMessage.Success) {
                this.Report.setType(ResultMessage.InfoMessage);
                this.Report.setMessage("User found.");
            } else if (result == ResultMessage.DoesNotExist) {
                this.Report.setType(ResultMessage.ErrorMessage);
                this.Report.setMessage("The user associated with this license was not found.");
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
        if (request.getCharacterEncoding() == null)
            request.setCharacterEncoding("UTF-8");

        // Get the request parameters.
        String key = request.getParameter("key");
        key = key.replace("-", "");
        AcctLogger.get().info("Key: " + key);
        HttpSession session = request.getSession();
        UserByKey.Message message = new UserByKey.Message(LoginServlet.getResultMessage(session));

        DatabaseUsers users = new DatabaseUsers();
        try {
            users.database.connect();
            PreparedStatement statement = users.database.connection
                    .prepareStatement("SELECT userid FROM licenses WHERE licensekey=?");
            users.database.addStatement(statement);
            statement.setString(1, key);
            users.database.results = statement.executeQuery();
            int userId = -1;
            if (users.database.results.next()) {
                userId = users.database.results.getInt(1);
            }
            users.database.results.close();
            AcctLogger.get().info("User ID: " + userId);
            String page = "/searchkey.jsp";
            String contextPath = request.getContextPath();
            if (userId >= 0) {
                page = "/clientinfo.jsp?user=" + userId;
                message.setResult(ResultMessage.Success);
                response.sendRedirect(response.encodeRedirectURL(contextPath + page));
            } else {
                message.setResult(ResultMessage.DoesNotExist);
                response.sendRedirect(response.encodeRedirectURL(contextPath + page));
            }
        } catch (SQLException sqle) {
            message.setResult(ResultMessage.ResourceError);
            message.addException(sqle);
        } catch (NamingException ne) {
            message.setResult(ResultMessage.ResourceError);
            message.addException(ne);
        }
        users.database.cleanup();
    }
}

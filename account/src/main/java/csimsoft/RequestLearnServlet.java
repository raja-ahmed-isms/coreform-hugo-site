package csimsoft;

import java.io.IOException;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RequestLearnServlet extends HttpServlet {

    public RequestLearnServlet() {
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
                this.Report.setMessage("Thank you for signing up for Coreform Cubit Learn!");
            } else if (result == ResultMessage.Failure) {
                this.Report.setType(ResultMessage.ErrorMessage);
                this.Report
                        .setMessage("We're sorry. The order process has failed due to an internal "
                                + "error. Please send us an email or try again later.");
            } else if (result == ResultMessage.ResourceError) {
                this.Report.setType(ResultMessage.ErrorMessage);
                this.Report.setMessage("Resource error: ");
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
        // Make sure the character decoding is correct.
        if (request.getCharacterEncoding() == null)
            request.setCharacterEncoding("UTF-8");

        String page = "/licenses";

        // Get the result message object for the session.
        HttpSession session = request.getSession();
        RequestLearnServlet.Message message =
                new RequestLearnServlet.Message(LoginServlet.getResultMessage(session));

        String contextPath = request.getContextPath();

        UserInfo user = LoginServlet.getUserInfo(session);
        int userId = user.getUserId();
        if (userId != 0) {
            DatabaseUsers users = new DatabaseUsers();
            try {
                users.database.connect();
                UserProfileInfo info = new UserProfileInfo();
                if (users.getUserProfileInfo(userId, info)) {
                    AddUserLearnServlet.Message trialMessage =
                            new AddUserLearnServlet.Message(LoginServlet.getResultMessage(session));
                    int userlicenseid = AddUserLearnServlet.activateLearn(trialMessage, userId);
                    page = "/licensecubitlearn.jsp?license=" + userlicenseid;
                    if (trialMessage.Report.getResult() == ResultMessage.Success) {
                        message.setResult(ResultMessage.Success);
                        csimsoft.MailchimpApi.sendMailchimpLearnWelcome(info.getEmail(),
                                info.getFirstName(), info.getLastName(), info.getCompany(), false); // We'll have
                                                                                 // already added
                                                                                 // the account and
                                                                                 // set the
                                                                                 // newsletter, no
                                                                                 // need to set the
                                                                                 // tag again
                    } else {
                        message.Report = trialMessage.Report;
                    }
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
        response.sendRedirect(response.encodeRedirectURL(contextPath + page));
    }

}

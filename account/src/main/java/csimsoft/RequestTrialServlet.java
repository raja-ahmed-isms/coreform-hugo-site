package csimsoft;

import java.io.IOException;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RequestTrialServlet extends HttpServlet {

    public RequestTrialServlet() {
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
                this.Report.setMessage("Thank you for signing up for your free trial!");
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

        int licenseId = 0;
        int numDays = 0;
        // Get the request parameters.
        String license = request.getParameter("license");
        String days = request.getParameter("days");

        String page = "/trialthankyou.jsp";

        // Get the result message object for the session.
        HttpSession session = request.getSession();
        RequestTrialServlet.Message message =
                new RequestTrialServlet.Message(LoginServlet.getResultMessage(session));

        String contextPath = request.getContextPath();

        try {
            if (license != null && !license.isEmpty()) {
                licenseId = Integer.parseInt(license);
                if (days != null && !days.isEmpty()) {
                    numDays = Integer.parseInt(days);
                }
            }  else {
                message.setResult(ResultMessage.Failure);
                message.addMessage("The license parameters were invalid");
                response.sendRedirect(response.encodeRedirectURL(contextPath + page));
                return;
            }
        } catch (Exception nfe) {
            message.setResult(ResultMessage.Failure);
            message.addException(nfe);
            response.sendRedirect(response.encodeRedirectURL(contextPath + page));
            return;
        }
        UserInfo user = LoginServlet.getUserInfo(session);
        int userId = user.getUserId();
        if (userId != 0) {
            DatabaseUsers users = new DatabaseUsers();
            try {
                users.database.connect();
                UserProfileInfo info = new UserProfileInfo();
                if (users.getUserProfileInfo(userId, info)) {
                    AddUserTrialServlet.Message trialMessage =
                            new AddUserTrialServlet.Message(LoginServlet.getResultMessage(session));
                    AddUserTrialServlet.activateTrial(trialMessage, userId, licenseId,
                            numDays, "");
                    if (trialMessage.Report.getResult() == ResultMessage.Success) {
                        message.setResult(ResultMessage.Success);
                        csimsoft.MailchimpApi.sendMailchimpWelcome(info.getEmail(),
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

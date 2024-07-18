package csimsoft;

import java.io.IOException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.client.ClientProtocolException;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;

import csimsoft.AcctLogger;

class LoginInfo {
    String accessToken;
    String instanceUrl;

    LoginInfo() {
        accessToken = null;
        instanceUrl = null;
    }
}

public class SalesforceApiServlet {

  static final String USERNAME     = "dallin@coreform.com";
  static final String PASSWORD     = "bnNgwy9qUbsEIvMtCTCikhLzP5uGZvFJXN";
  static final String LOGINURL     = "https://login.salesforce.com";
  static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
  static final String CLIENTID     = "3MVG9QDx8IX8nP5TDJ7Toy9o4lzexMAXWoxpB_nrAt3YajwqPh7hp2KQLcgXpynng9WWxbKI23A0GnC_y2Hme";
  static final String CLIENTSECRET = "90AC46A82538134D891664DFDF5396DE1F292FCC48858ADEA7B7188615597CD1";

  private static LoginInfo login() {

      DefaultHttpClient httpclient = new DefaultHttpClient();

      // Assemble the login request URL
      String loginURL = LOGINURL + 
                        GRANTSERVICE + 
                        "&client_id=" + CLIENTID + 
                        "&client_secret=" + CLIENTSECRET +
                        "&username=" + USERNAME +
                        "&password=" + PASSWORD;

      // Login requests must be POSTs
      HttpPost httpPost = new HttpPost(loginURL);
      HttpResponse response = null;

      try {
          // Execute the login POST request
          response = httpclient.execute(httpPost);
      } catch (Exception exception) {
          AcctLogger.get().info("Exception with post: " + exception);
          return null;
      }

      // verify response is HTTP OK
      final int statusCode = response.getStatusLine().getStatusCode();
      String getResult = null;
      try {
          getResult = EntityUtils.toString(response.getEntity());
      } catch (IOException exception) {
          AcctLogger.get().info("Exception with result: " + exception);
          return null;
      }
      if (statusCode != HttpStatus.SC_OK) {
          AcctLogger.get().info("Error authenticating to Force.com: " + statusCode);
          AcctLogger.get().info("Error: " + getResult);
          return null;
      }

      JSONObject jsonObject = null;
      LoginInfo info = new LoginInfo();
      try {
          jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
          info.accessToken = jsonObject.getString("access_token");
          info.instanceUrl = jsonObject.getString("instance_url");
      } catch (JSONException jsonException) {
          AcctLogger.get().info("Exception with JSON: " + jsonException);
          return null;
      }
      // release connection
      httpPost.releaseConnection();
      return info;
  }

  public static void newTrialSignup(UserProfileInfo info)
  {
    LoginInfo loginInfo = SalesforceApiServlet.login();
    if (loginInfo != null) {
      JSONObject event = new JSONObject();
      event.put("Address__c", info.getAddress());
      event.put("Company__c", info.getCompany());
      event.put("Country__c", info.getCountry());
      event.put("Industry__c", info.getIndustry());
      event.put("Email__c", info.getJsEmail());
      event.put("First_Name__c", info.getFirstName());
      event.put("Last_Name__c", info.getLastName());
      event.put("Phone__c", info.getPhone());

      try {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(loginInfo.instanceUrl + "/services/data/v48.0/sobjects/NewTrialSignup__e");
        httpPost.setEntity(new StringEntity(event.toString()));
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + loginInfo.accessToken);

        HttpResponse response = null;
        response = httpclient.execute(httpPost);
        if(response != null) {
            final int statusCode = response.getStatusLine().getStatusCode();
            String body = EntityUtils.toString(response.getEntity());
        }
      } catch (Exception exception) {
          AcctLogger.get().info("Exception in trial signup: " + exception);
      } 
    }
  }
}
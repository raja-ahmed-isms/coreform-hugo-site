package csimsoft;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.client.ClientProtocolException;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.json.JSONException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MailchimpApi {

    static final String API_KEY = "7cef18509dd8c829b4bf4e2e886d28d7-us15";
    static final String URL = "https://us15.api.mailchimp.com/3.0/lists/";

    private static String subscriberHash(String email) {
        try {
            //Why does this throw an exception?  I hate Java.
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(email.toLowerCase().getBytes());
            byte[] hash = md.digest();
            //See https://stackoverflow.com/questions/5470219/get-md5-string-from-message-digest
            //Why isn't this built in?  I just want the MD5 of something.  This whole function would be a two-liner with better performance in Rust.  I hate Java so much.
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                if ((0xff & hash[i]) < 0x10) {
                    hexString.append("0"
                            + Integer.toHexString((0xFF & hash[i])));
                } else {
                    hexString.append(Integer.toHexString(0xFF & hash[i]));
                }
            }
            AcctLogger.get().info("Subscriber hash: " + email + " -> " + hexString.toString());
            return hexString.toString();
        }
        catch(NoSuchAlgorithmException e)
        {
            return "";
        }
    }

    private static boolean addContact(String list_id, String email, String first, String last, String company) {
        boolean success = false;
        JSONObject contact = new JSONObject();
        contact.put("email_address", email);
        contact.put("status", "subscribed");
        JSONObject merge = new JSONObject();
        merge.put("FNAME", first);
        merge.put("LNAME", last);
        merge.put("MMERGE3", company);
        contact.put("merge_fields", merge);

        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL + list_id + "/members/");
            httpPost.setEntity(new StringEntity(contact.toString()));
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Authorization", "Basic " + API_KEY);

            HttpResponse response = httpclient.execute(httpPost);
            if (response != null) {
                final int statusCode = response.getStatusLine().getStatusCode();
                String body = EntityUtils.toString(response.getEntity());
                if (statusCode == 400) {
                    JSONObject jsonObject = (JSONObject) new JSONTokener(body).nextValue();
                    if (jsonObject.getString("title").equals("Member Exists")) {
                        AcctLogger.get().info("Mailchimp contact already exists: " + email);
                        success = true;
                    }
                } else {
                    if (statusCode > 300) {
                        AcctLogger.get().info("Body: " + body);
                    } else {
                        success = true;
                    }
                }
            }
        } catch (Exception exception) {
            AcctLogger.get().info("Exception in contact creation: " + exception);
        }
        return success;
    }

    private static String getList() {
        String list_id = null;
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(URL);
            httpGet.setHeader("Authorization", "Basic " + API_KEY);

            HttpResponse response = httpclient.execute(httpGet);
            if (response != null) {
                final int statusCode = response.getStatusLine().getStatusCode();
                String body = EntityUtils.toString(response.getEntity());
                if (statusCode < 300) {
                    JSONObject jsonObject = (JSONObject) new JSONTokener(body).nextValue();
                    JSONObject list = jsonObject.getJSONArray("lists").getJSONObject(0);
                    list_id = list.getString("id");
                } else {
                    AcctLogger.get().info("Body: " + body);
                }
            }
        } catch (Exception exception) {
            AcctLogger.get().info("Exception in getting list id: " + exception);
        }
        return list_id;
    }

    private static void setTag(String listId, String tagName, String email) {
        JSONObject body = new JSONObject();
        body.put("is_syncing", false);
        JSONArray array = new JSONArray();
        JSONObject tag = new JSONObject();
        tag.put("name", tagName);
        tag.put("status", "active");
        array.put(tag);
        body.put("tags", array);
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL + listId + "/members/" + subscriberHash(email) + "/tags");
            httpPost.setEntity(new StringEntity(body.toString()));
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Authorization", "Basic " + API_KEY);

            HttpResponse response = httpclient.execute(httpPost);
            if (response != null) {
                final int statusCode = response.getStatusLine().getStatusCode();
                String respBody = EntityUtils.toString(response.getEntity());
                if (statusCode > 300) {
                    AcctLogger.get().info("Body: " + respBody);
                }
            }
        } catch (Exception exception) {
            AcctLogger.get().info("Exception in tag setting: " + exception);
        }
    }

    public static void addToMailchimp(String email, String first, String last, String company, boolean newsletter) {
        String list_id = getList();
        if (list_id != null) {
            if (addContact(list_id, email, first, last, company)) {
                if (newsletter) {
                    setTag(list_id, "Newsletter", email);
                }
            }
        }
    }

    public static void sendMailchimpWelcome(String email, String first, String last, String company,
            boolean newsletter) {
        String list_id = getList();
        if (list_id != null) {
            if (addContact(list_id, email, first, last, company)) {
                setTag(list_id, "Free Trial Signup", email);
                if (newsletter) {
                    setTag(list_id, "Newsletter", email);
                }
            }
        }
    }

    public static void sendMailchimpLearnWelcome(String email, String first, String last, String company,
            boolean newsletter) {
        String list_id = getList();
        if (list_id != null) {
            if (addContact(list_id, email, first, last, company)) {
                setTag(list_id, "Cubit Learn", email);
                if (newsletter) {
                    setTag(list_id, "Newsletter", email);
                }
            }
        }
    }

    public static void sendMailchimpActivate(String email) {
        String list_id = getList();
        if (list_id != null) {
            setTag(list_id, "Free Trial Activated", email);
        }
    }
}

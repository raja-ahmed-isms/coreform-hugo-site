/**
 * @(#)Email.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/9/11
 */

package csimsoft;

import java.util.Iterator;
import java.util.List;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Email {

   public Email() {
   }

   public Session getMailSession() {
      java.util.Properties props = System.getProperties();
      props.setProperty("mail.smtp.host", "localhost");
      props.setProperty("mail.smtp.port", "587");
      props.setProperty("mail.from", "no-reply@coreform.com");
      // props.setProperty("mail.from", "sales@coreform.com");
      return Session.getInstance(props, null);
   }

   public void sendMessage(Session session, String recipients, String subject, String body)
         throws AddressException, MessagingException {
      // Set up the message.
      MimeMessage email = new MimeMessage(session);
      email.setSubject(subject);
      email.setSentDate(new java.util.Date());
      email.setFrom();
      email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients, false));
      email.setText(body);

      // Send the message.
      Transport.send(email);
   }

   public void sendForgotMessage(ResultMessageAdapter message, String email, String link) {
      try {
         Session session = getMailSession();

         // Set up the message body.
         String body = "We're sorry that you couldn't log on to coreform.com. "
               + "As requested, here is a link to reset your password:\n\n" + "   " + link + "\n\n"
               + "If you did not request that your password be reset, "
               + "please contact Coreform.\n\n" + "Thank you,\n\n"
               + "Coreform LLC \n(801) 717-2296";

         // Send the message.
         sendMessage(session, email, "Coreform Account", body);
         message.setResult(ResultMessage.Success);
      } catch (AddressException ae) {
         message.setResult(ResultMessage.Failure);
      } catch (MessagingException me) {
         message.setResult(ResultMessage.Failure);
      }
   }

   public void sendUserNotification(int userId, String email, String first, String last) {
      try {
         Session session = getMailSession();

         // Set up the message body.
         String body = "A new user has signed up for the website:\n" + "\t" + first + " " + last
               + "\n" + "\t" + email + "\n\n"
               + "For more information, see http://www.coreform.com/account/clientinfo.jsp?user="
               + userId;

         // Send the message.
         // sendMessage(session, "info@coreform.com", "New Coreform User", body);
      } catch (Exception e) {
      }
   }



   public void sendClientNotification(int userId, String email, String first, String last,
         String distributorEmail) {
      if (distributorEmail == null || distributorEmail.isEmpty())
         return;

      try {
         Session session = getMailSession();

         // Set up the message body.
         String body = "A new client has been added to your list:\n" + "\t" + first + " " + last
               + "\n" + "\t" + email + "\n\n"
               + "For more information, see http://www.coreform.com/account/distclient.jsp?user="
               + userId;

         // Send the message.
         sendMessage(session, distributorEmail, "New Coreform Client", body);
      } catch (Exception e) {
      }
   }

   public void sendOrderNotification(String distributorEmail, String email, int orderId,
         String name, boolean isProduct, boolean isToUser) {
      try {
         Session session = getMailSession();

         // Set up the message body.
         String body = null;
         if (isToUser) {
            body = "Thank you for ordering from Coreform.\n" + "\tOrder:   " + orderId + "\n";
            if (distributorEmail != null)
               body += "\tContact: " + distributorEmail + "\n";
         } else {
            body = "A client has made a new order on the website:\n" + "\tClient:  " + email + "\n"
                  + "\tOrder:   " + orderId + "\n";
         }

         if (isProduct) {
            if (name == null || name.isEmpty())
               body += "\tProduct: Unknown\n";
            else
               body += "\tProduct: ";
         } else {
            if (name == null || name.isEmpty())
               body += "\tLicense: Unknown\n";
            else
               body += "\tLicense: ";
         }

         body += name + "\n";
         if (isToUser) {
            body += "\nPlease go to http://www.coreform.com/account/orders.jsp "
                  + "to track the progress of your order. You may need to enter "
                  + "some information to process your order.";
         }

         // Send the message.
         String whereTo = "info@coreform.com";
         if (isToUser)
            whereTo = email;
         else if (distributorEmail != null)
            whereTo = distributorEmail;

         sendMessage(session, whereTo, "New Coreform Order", body);
      } catch (Exception e) {
      }
   }

   public void sendLicenseMessage(ResultMessageAdapter message, String email, int orderId) {
      try {
         Session session = getMailSession();

         // Set up the message body.
         String body = "Thank you! Your license agreement for order " + orderId + " has been "
               + "received and approved. Please visit the Coreform website to "
               + "view the status of your order.\n\n"
               + "   http://www.coreform.com/account/orderinfo?orderid=" + orderId + "\n\n"
               + "Thank you,\n\n" + "Coreform \n(801) 717-2296";

         // Send the message.
         sendMessage(session, email, "Coreform Order " + orderId, body);
      } catch (Exception e) {
         message.addMessage("<br />Unable to send an email message to: " + email + "<br />" + e);
      }
   }

   public void sendPaymentMessage(ResultMessageAdapter message, String email, int orderId) {
      try {
         Session session = getMailSession();

         // Set up the message body.
         String body = "Thank you! Your payment for order " + orderId + " has been received. "
               + "Please visit the Coreform website to view the status of " + "your order.\n\n"
               + "   http://www.coreform.com/account/orderinfo?orderid=" + orderId + "\n\n"
               + "Thank you,\n\n" + "Coreform \n(801) 717-2296";

         // Send the message.
         sendMessage(session, email, "Coreform Order " + orderId, body);
      } catch (Exception e) {
         message.addMessage("<br />Unable to send an email message to: " + email + "<br />" + e);
      }
   }

   public void sendOrderCompleteMessage(ResultMessageAdapter message, String email, int orderId,
         List<String> products, List<String> licenses) {
      try {
         Session session = getMailSession();

         // Set up the message body.
         String body = "Thank you for your order! Order " + orderId + " has been completed. "
               + "The order contains the following software:\n\n";

         Iterator<String> iter = products.iterator();
         while (iter.hasNext())
            body += "   - " + iter.next() + "\n";

         iter = licenses.iterator();
         while (iter.hasNext())
            body += "   - " + iter.next() + "\n";

         body += "\n";
         if (licenses.isEmpty()) {
            body += "Please visit the Coreform website to download the software.\n\n"
                  + "   http://www.coreform.com/account/orderinfo?orderid=" + orderId + "\n\n";
         } else {
            body += "You can find the software download and license key by clicking on the "
                  + "license on the order page. You can also find it by going to the "
                  + "My Licenses page in your account.\n\n"
                  + "   1. Visit http://www.coreform.com/account/orderinfo?orderid=" + orderId
                  + "\n" + "   2. Download and install the software.\n"
                  + "   3. Use the product key to activate the software\n\n"
                  + "Instructions for installing and activating your software can be "
                  + "found at:\n\n"
                  + "   https://coreform.com/support/activation/cubit/activate/\n\n";
         }

         body += "Thank you,\n\n" + "Coreform \n(801) 717-2296";

         // Send the message.
         sendMessage(session, email, "Coreform Order " + orderId, body);
      } catch (Exception e) {
         message.addMessage("<br />Unable to send an email message to: " + email + "<br />" + e);
      }
   }

   public Exception sendUpgradeMessage(String email, int userLicenseId) {
      try {
         Session session = getMailSession();

         // Set up the message body.
         String body = "A new version of your software is now available. "
               + "You can find the software download and product key on "
               + "the My Licenses page in your account.\n\n" +

               "   1. Visit https://www.coreform.com/account/licensedetail?license=" + userLicenseId
               + "\n" + "   2. Download and install the software\n"
               + "   3. If this is a major or minor update, use the product key to update the "
               + "software license (Help-Product Activation)\n\n"
               + "NOTE: Floating licenses might require an RLM-Server update. "
               + "See https://www.coreform.com/account/downloads\n\n"
               + "Additional instructions for installing and activating your software can be "
               + "found at:\n\n" + "   https://coreform.com/support/activation/cubit/activate/.\n\n"
               + "Thank you,\n\n" + "Coreform \n(801) 717-2296";

         // Send the message.
         sendMessage(session, email, "Coreform software update is now available", body);
         // sendMessage(session, email, "csimsoft " <Trelis 16.5.3> + " update is now available",
         // body);
      } catch (Exception e) {
         return e;
      }

      return null;
   }

   public void sendUpgradeMessage(ResultMessageAdapter message, String email, int userLicenseId) {
      Exception e = this.sendUpgradeMessage(email, userLicenseId);
      if (e != null && message != null) {
         message.addMessage("<br />Unable to send an email message to: " + email + "<br />" + e);
      }
   }

   public void sendTrialMessage(ResultMessageAdapter message, String email, int orderId,
         int approval, List<String> products, List<String> licenses) {
      try {
         Session session = getMailSession();

         // Set up the message body.
         String body = null;
         if (approval == DatabaseLicenses.DownloadApproved) {
            body = "Thank you! Your request to download the trial software from " + "order "
                  + orderId + " has been approved. The order contains the "
                  + "following software:\n\n";

            Iterator<String> iter = products.iterator();
            while (iter.hasNext())
               body += "   - " + iter.next() + "\n";

            iter = licenses.iterator();
            while (iter.hasNext())
               body += "   - " + iter.next() + "\n";

            body += "\n";
            if (licenses.isEmpty()) {
               body += "Please visit the Coreform website to download the trial.\n\n"
                     + "   http://www.coreform.com/account/orderinfo?orderid=" + orderId + "\n\n";
            } else {
               body += "You can find the software download and license key for the trial "
                     + "by clicking on the license on the order page. You can also find it "
                     + "by going to the My Licenses page in your account.\n\n"
                     + "   1. Visit http://www.coreform.com/account/orderinfo?orderid=" + orderId
                     + "\n" + "   2. Download and install the software.\n"
                     + "   3. Use the product key to activate the software\n\n"
                     + "Instructions for installing and activating your software can be "
                     + "found at:\n\n"
                     + "   https://coreform.com/support/activation/cubit/activate/\n\n";
            }

            body += "Thank you,\n\n" + "Coreform \n(801) 717-2296";
         } else {
            body = "We're sorry. Your request to download the trial software from order " + orderId
                  + " has not been approved. If you believe there is an error, "
                  + "please contact Coreform at (801) 717-2296 to resolve the issue.\n\n"
                  + "Thank you,\n\n" + "Coreform \n(801) 717-2296\nsales@coreform.com";
         }

         // Send the message.
         sendMessage(session, email, "Coreform Order " + orderId, body);
      } catch (Exception e) {
         message.addMessage("<br />Unable to send an email message to: " + email + "<br />" + e);
      }
   }

   public void sendDownloadMessage(ResultMessageAdapter message, String email, String download) {
      try {
         Session session = getMailSession();

         // Set up the message body.
         String body =
               "Thank you! " + download + " is available to download from your Coreform account. "
                     + "Please visit the Coreform site to download it.\n\n"
                     + "   http://www.coreform.com/account/downloads\n\n" + "Thank you,\n\n"
                     + "Coreform LLC \n(801) 717-2296";

         // Send the message.
         sendMessage(session, email, "New Coreform Download Available", body);
      } catch (Exception e) {
         message.addMessage("<br />Unable to send an email message to: " + email + "<br />" + e);
      }
   }
}


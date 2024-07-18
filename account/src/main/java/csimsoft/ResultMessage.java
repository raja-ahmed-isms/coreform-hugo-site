/**
 * @(#)ResultMessage.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2007/4/11
 */

package csimsoft;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class ResultMessage
{
   public static final int NoMessageType = 0;
   public static final int InfoMessage = 1;
   public static final int WarningMessage = 2;
   public static final int ErrorMessage = 3;

   public static final int NoResult = 0;
   public static final int LoginError = 1;
   public static final int ResourceError = 2;
   public static final int Success = 3;
   public static final int Failure = 4;
   public static final int NotAuthorized = 5;
   public static final int Exists = 6;
   public static final int DoesNotExist = 7;

   private String Message = null;
   private String IconName = null;
   private int MessageType = ResultMessage.NoMessageType;
   private int Result = ResultMessage.NoResult;

   public ResultMessage()
   {
   }

   public String getMessage()
   {
      return this.Message;
   }

   public void setMessage(String msg)
   {
      this.Message = msg;
   }

   public String getIcon()
   {
      return this.IconName;
   }

   public void setIcon(String name)
   {
      this.IconName = name;
   }

   public int getType()
   {
      return this.MessageType;
   }

   public void setType(int type)
   {
      this.MessageType = type;
      if(this.MessageType > ResultMessage.ErrorMessage)
         this.MessageType = ResultMessage.InfoMessage;
   }

   public int getResult()
   {
      return this.Result;
   }

   public String getResultName()
   {
      switch(this.Result)
      {
         case ResultMessage.NoResult:
            return "NoResult";
         case ResultMessage.LoginError:
            return "LoginError";
         case ResultMessage.ResourceError:
            return "ResourceError";
         case ResultMessage.Success:
            return "Success";
         case ResultMessage.Failure:
            return "Failure";
         case ResultMessage.NotAuthorized:
            return "NotAuthorized";
         case ResultMessage.Exists:
            return "Exists";
         case ResultMessage.DoesNotExist:
            return "DoesNotExist";
      }

      return "";
   }

   public void setResult(int result)
   {
      clear();
      this.Result = result;
      if(this.Result == ResultMessage.LoginError)
      {
         this.MessageType = ResultMessage.ErrorMessage;
         this.Message =
            "You are not logged in. ";
      }
      else if(this.Result == ResultMessage.ResourceError)
      {
         this.MessageType = ResultMessage.ErrorMessage;
         this.Message = "The database resource is not available.";
      }
   }

   public boolean isEmpty()
   {
      return this.Message == null || this.Message.isEmpty();
   }

   public void clear()
   {
      this.Message = null;
      this.IconName = null;
      this.MessageType = ResultMessage.NoMessageType;
      this.Result = ResultMessage.NoResult;
   }

   public void appendToMessage(String extra)
   {
      if(this.Message == null)
         this.Message = extra;
      else
         this.Message += extra;
   }

   public void sendXml(HttpServletResponse response) throws IOException
   {
      try
      {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document document = builder.newDocument();

         // Create the root element.
         Element root = document.createElement("CsimsoftWeb");
         document.appendChild(root);

         // Create the message element.
         Element message = document.createElement("Message");
         root.appendChild(message);

         // Add the result type attribute.
         message.setAttribute("type", this.getResultName());

         if(this.Message != null && !this.Message.isEmpty())
         {
            // Add the message text.
            Element element = document.createElement("Text");
            message.appendChild(element);
            element.appendChild(document.createTextNode(this.Message));
         }

         // Send the xml to the client.
         Transformer trans = TransformerFactory.newInstance().newTransformer();
         trans.setOutputProperty(OutputKeys.INDENT, "yes");
         trans.setOutputProperty(OutputKeys.METHOD, "xml");
         trans.setOutputProperty(OutputKeys.STANDALONE, "yes");
         trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

         response.setContentType("text/xml");
         trans.transform(new DOMSource(document),
            new StreamResult(response.getOutputStream()));
      }
      catch(ParserConfigurationException pce)
      {
      }
      catch(TransformerConfigurationException tce)
      {
      }
      catch(TransformerException te)
      {
         // IOException?
      }

      this.clear();
   }
}


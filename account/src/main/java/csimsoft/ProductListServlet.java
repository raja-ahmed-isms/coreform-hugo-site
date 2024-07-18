/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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


public class ProductListServlet extends HttpServlet
{

   public ProductListServlet()
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
         if(result == ResultMessage.Failure)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "Unable to get the list of products.");
         }
         else if(result == ResultMessage.NotAuthorized)
         {
            this.Report.setType(ResultMessage.ErrorMessage);
            this.Report.setMessage(
               "You are not authorized to view this page.");
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
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
   {
      // Get the result message object for the session.
      HttpSession session = request.getSession();
      ProductListServlet.Message message = new ProductListServlet.Message(
         LoginServlet.getResultMessage(session));

      // The user role must be admin in order to modify a process.
      UserInfo user = LoginServlet.getUserInfo(session);
      boolean isAdmin = user.isAdministrator();
      boolean isDistributor = user.isDistributor();

      // Get the parameters from the request.
      int rootId = 0;
      try
      {
         rootId = Integer.parseInt(request.getParameter("rootid"));
      }
      catch(Exception nfe)
      {
      }

      if(!LoginServlet.isUserLoggedIn(session))
         message.setResult(ResultMessage.LoginError);
      else if(!isAdmin && !isDistributor)
         message.setResult(ResultMessage.NotAuthorized);
      else if(rootId < 0)
         message.setResult(ResultMessage.Failure);
      else
      {
         DatabaseProducts manager = new DatabaseProducts();
         try
         {
            manager.database.connect();
            try
            {
               int homeId = 0;
               if(!isAdmin)
               {
                  homeId = manager.getDistributorFolder(user.getUserId());
                  if(rootId == 0)
                     rootId = homeId;
               }

               ProductList products = new ProductList(homeId);
               products.setProducts(manager.database, rootId);
               manager.database.cleanup();

               ProductListGroup root = products.getRoot();
               if(root.getId() == rootId)
               {
                  this.sendProductList(response, products);
                  return;
               }
               else
               {
                  message.setResult(ResultMessage.Failure);
                  message.addMessage(
                     " The given folder is not valid or not accessible.");
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

         manager.database.cleanup();
      }

      message.Report.sendXml(response);
   }

   private void sendProductList(HttpServletResponse response, ProductList products)
      throws IOException
   {
      try
      {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document document = builder.newDocument();

         // Create the root element.
         Element xmlRoot = document.createElement("CsimsoftWeb");
         document.appendChild(xmlRoot);

         // Add the root folder element.
         ProductListGroup root = products.getRoot();
         Element home = document.createElement("Products");
         home.setAttribute("productId", String.valueOf(root.getId()));
         xmlRoot.appendChild(home);

         // TODO: Set the root folder name if it has one.

         HashMap<ProductListGroup, Element> elementMap =
            new HashMap<ProductListGroup, Element>();
         elementMap.put(root, home);
         ProductListItem item = products.getNextItem(root);
         while(item != null)
         {
            // Create an element for the product. Put it under its
            // parent folder's element.
            boolean isFolder = item instanceof ProductListGroup;
            Element element = document.createElement(isFolder ? "Folder" : "Product");
            element.setAttribute("productId", String.valueOf(item.getId()));
            Element parent = elementMap.get(item.getParent());
            parent.appendChild(element);
            if(isFolder)
               elementMap.put((ProductListGroup)item, element);

            // Add the product name to the xml.
            Element name = document.createElement("Name");
            element.appendChild(name);
            name.appendChild(document.createTextNode(item.getName()));

            // Add the file location if this is not a folder.
            if(!isFolder)
            {
               Element location = document.createElement("Location");
               element.appendChild(location);
               location.appendChild(document.createTextNode(item.getLocation()));
            }

            item = products.getNextItem(item);
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
   }
}

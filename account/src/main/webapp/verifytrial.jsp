<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Verify Trial</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="formhelp.js"></script>

<% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
   boolean isAdmin = user.isAdministrator();
   boolean isDistributor = user.isDistributor();
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\"";
   else if(isAdmin || isDistributor)
   { %>
<script type="text/javascript">
function sendRemoveLicenseForm(licenseid)
{
  if(confirm("Are you sure you want to delete\nlicense #" + licenseid + "?"))
  {
    document.removelicenseform.license.value = licenseid
    document.removelicenseform.submit()
  }
}
</script>
<% } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="verifytrial.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin || isDistributor)
     {
       csimsoft.DatabaseOrders orders = new csimsoft.DatabaseOrders();
       csimsoft.DatabaseLicenses licenses = new csimsoft.DatabaseLicenses(orders.database);
       try
       {
         int orderId = Integer.parseInt(request.getParameter("orderid"));
         orders.database.connect();
         if(isAdmin || orders.isOrderOwnedByUser(orderId, user.getUserId()))
         {
           csimsoft.OrderSummary summary = new csimsoft.OrderSummary(orderId);
           summary.setOrderSummary(orders);
           csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
           if(orders.getOrderUser(orderId, info))
           {
             int approval = licenses.getOrderDownloadApproval(info.getId(), orderId); %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr>
        <td colspan="5">
        <strong>Order Date:</strong> <%= summary.getOrderDate() %><br />
<%           if(!summary.getLeadSource().isEmpty())
             { %>
        <strong>Source:</strong> <%= summary.getLeadSource() %><br />
<%           } %>
        <form action="newpurchaseorder" method="POST" name="newpoform"
          accept-charset="UTF-8">
          <input type="hidden" name="orderid" value="<%= orderId %>" />
        <table border="0" cellpadding="3" cellspacing="0">
          <tr>
            <td><strong>Purchase Order:</strong></td>
            <td>
              <input type="text" name="po" value="<%= summary.getPurchaseOrder() %>" />
            </td>
          </tr>
          <tr>
            <td><strong>Reference Number:</strong></td>
            <td>
              <input type="text" name="rn" value="<%= summary.getReferenceNumber() %>" />
            </td>
          </tr>
          <tr>
            <td colspan="2" align="right"><input type="submit" value="Set" /></td>
          </tr>
        </table>
        </form>
        </td>
      </tr>
<%           if(!summary.isLicensesEmpty())
             { %>
      <tr><td class="title-cell" colspan="5">Order Licenses</td></tr>
      <tr>
        <td class="heading-cell" colspan="2">ID</td>
        <td class="heading-cell">Name</td>
        <td class="heading-cell">Quantity</td>
        <td class="heading-cell">Type</td>
      </tr>
<%             for(int i = 0; i < summary.getLicenseCount(); i++)
               {
                 csimsoft.OrderSummaryLicense license = summary.getLicense(i); %>
      <tr>
        <td class="list-icon">
           <a href="javascript:sendRemoveLicenseForm('<%= license.getId() %>')"
             ><img src="./images/icon-delete.jpg" width="20" height="20" /></a>
        </td>
        <td class="list-row"><%= license.getId() %></td>
        <td class="list-row">
          <a href="clientlicense.jsp?license=<%= license.getId() %>"><%= license.getName() %></a>
        </td>
        <td class="list-row"><%= license.getQuantity() %></td>
        <td class="list-row"><%= license.getLicenseTypeName() %></td>
      </tr>
<%             } %>
      <tr>
        <td colspan="5">
          <form action="removeorderlicense" method="POST" name="removelicenseform">
            <input type="hidden" name="license" value="" />
            <input type="hidden" name="orderid" value="<%= orderId %>" />
          </form>
        </td>
      </tr>
<%           }
             if(!summary.isProductsEmpty())
             { %>
      <tr><td class="title-cell" colspan="5">Order Products</td></tr>
<%             int count = summary.getProductCount();
               for(int i = 0; i < count; i++)
               {
                 csimsoft.OrderSummaryDownload download = summary.getProduct(i); %>
      <tr>
        <td class="list-row" colspan="5"><%= download.getName() %></td>
      </tr>
<%             }
             } %>
      <tr><td class="title-cell" colspan="5">Download Approval</td></tr>
<%           String unassigned = "";
             String approved = "";
             String rejected = "";
             if(approval == csimsoft.DatabaseLicenses.DownloadApproved)
               approved = "selected=\"selected\"";
             else if(approval == csimsoft.DatabaseLicenses.DownloadRejected)
               rejected = "selected=\"selected\"";
             else
               unassigned = "selected=\"selected\"";

             String clientLink = "clientinfo";
             if(isDistributor)
               clientLink = "distclient";
             String lead = summary.getLeadSource();
             if(!summary.isLeadSourceEmpty())
               lead = "&source=" + lead; %>
      <tr>
        <td colspan="5">
          <a href="<%= clientLink %>.jsp?user=<%= info.getId() %><%= lead %>"><%= info.getFullName() %></a>
        has requested a trial download. Review the user account information. Then,
        select "Approved" or "Rejected" from the combo-box, and click the "Finish"
        button below.
        </td>
      </tr>
      <tr>
        <td class="list-row" align="right" colspan="5">
        <form action="approvetrial" method="POST" name="verifytrialform">
          <input type="hidden" name="orderid" value="<%= orderId %>" />
          <input type="hidden" name="otype" value="product" />
          <select name="approval">
            <option value="<%= csimsoft.DatabaseLicenses.ApprovalUnassigned %>"
              <%= unassigned %>>Unassigned</option>
            <option value="<%= csimsoft.DatabaseLicenses.DownloadApproved %>"
              <%= approved %>>Approved</option>
            <option value="<%= csimsoft.DatabaseLicenses.DownloadRejected %>"
              <%= rejected %>>Rejected</option>
          </select>
          <input type="submit" value="Finish" />
        </form>
        </td>
      </tr>
      </table>
    </div>

<%         }
           else
           { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The order does not have an associated user.</td>
      </tr>
      </table>
    </div>
<%         }
         }
         else
         { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">You are not authorized to view the contents of this page.</td>
      </tr>
      </table>
    </div>
<%       }
       }
       catch(NumberFormatException nfe)
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The requested order id is not valid.</td>
      </tr>
      </table>
    </div>
<%     }
       catch(java.sql.SQLException sqle)
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Unable to connect to the database.<br /><%= sqle %></td>
      </tr>
      </table>
    </div>
<%     }
       catch(javax.naming.NamingException ne)
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Unable to locate the database resource.<br /><%= ne %></td>
      </tr>
      </table>
    </div>
<%     }
       orders.database.cleanup();
     }
     else
     { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">You are not authorized to view the contents of this page.</td>
      </tr>
      </table>
    </div>
<%   }
   }
   else
   {
     String query = request.getQueryString();
     if(query == null)
       query = "";
     else if(!query.isEmpty())
       query = "?" + query; %>
    <div class="content-login">
      <jsp:include page="loginform.jsp" flush="true" >
      <jsp:param name="page" value="<%= request.getServletPath() + query %>" />
      </jsp:include>
    </div>
<% } %>

  </div>

  <div class="push"></div>

  <jsp:include page="footer.jsp" flush="true" />


</div>

</body>

</html>

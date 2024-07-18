<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - License Information</title>

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
        <jsp:param name="current" value="licensetrial.jsp" />
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
           try
           {
             csimsoft.OrderSummary summary = new csimsoft.OrderSummary(orderId);
             summary.setOrderSummary(orders);
             int approval = licenses.getOrderLicenseApproval(orderId); %>

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
      <tr><td class="title-cell" colspan="5">Order Licenses</td></tr>
<%           if(summary.isLicensesEmpty())
             { %>
      <tr>
        <td class="list-row" colspan="5">No order licenses.</td>
      </tr>
<%           }
             else
             { %>
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
<%             }
             } %>
      <tr>
        <td colspan="5" align="right">
          <form action="selectlicense.jsp" method="GET" name="addlicenseform">
            <input type="hidden" name="next" value="addorderlicense" />
            <input type="hidden" name="orderid" value="<%= orderId %>" />
            <table border="0" cellpadding="6" cellspacing="0">
              <tr>
                <td>
                  <input type="text" name="quantity" value="1" />
                </td>
                <td>
                  <input type="submit" value="Add Licenses" />
                </td>
              </tr>
            </table>
          </form>
        </td>
      </tr>
      <tr><td class="title-cell" colspan="5">Order Completion</td></tr>
      <tr>
        <td class="list-row" colspan="5">
          There are two steps to completing the order:
          <ol>
            <li>
              Fill out the license information. Click on each of the license links in
              the list above to go to the license details page. There you can set the
              license type and quantity. You can also select the license features and
              options. You can add additional licenses or remove them using the list
              above.
            </li>
            <li>
              Review the user account information. Then, select "Approved" or "Rejected"
              from the combo-box, and click the "Finish" button below.
            </li>
          </ol>
        </td>
      </tr>
      <tr>
        <td colspan="5" align="right">
          <form action="approvetrial" method="POST" name="finishorderform">
            <input type="hidden" name="orderid" value="<%= orderId %>" />
            <input type="hidden" name="otype" value="license" />
            <table>
              <tr>
                <td>
<%           String unassigned = "";
             String approved = "";
             String rejected = "";
             if(approval == csimsoft.DatabaseLicenses.DownloadApproved)
               approved = "selected=\"selected\"";
             else if(approval == csimsoft.DatabaseLicenses.DownloadRejected)
               rejected = "selected=\"selected\"";
             else
               unassigned = "selected=\"selected\""; %>
                  <select name="approval">
                  <option value="<%= csimsoft.DatabaseLicenses.ApprovalUnassigned %>"
                    <%= unassigned %>>Unassigned</option>
                  <option value="<%= csimsoft.DatabaseLicenses.DownloadApproved %>"
                    <%= approved %>>Approved</option>
                  <option value="<%= csimsoft.DatabaseLicenses.DownloadRejected %>"
                    <%= rejected %>>Rejected</option>
                  </select>
                </td>
                <td>
                  <input type="submit" value="Finish" />
                </td>
              </tr>
            </table>
          </form>
        </td>
      </tr>
      <tr>
        <td colspan="5">
          <form action="removeorderlicense" method="POST" name="removelicenseform">
            <input type="hidden" name="license" value="" />
            <input type="hidden" name="orderid" value="<%= orderId %>" />
          </form>
        </td>
      </tr>
      </table>
    </div>

<%         }
           catch(java.sql.SQLException sqle)
           { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Internal error reading order summary from the database.</td>
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
       catch(NullPointerException npe)
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The order process has not been configured correctly.</td>
      </tr>
      </table>
    </div>
<%     }
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
        <td class="error-msg">Error: Unable to connect to the database.</td>
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
        <td class="error-msg">Error: Unable to locate the database resource.</td>
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

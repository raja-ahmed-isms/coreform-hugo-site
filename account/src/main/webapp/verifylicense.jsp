<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Verify License</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="formhelp.js"></script>

<% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\""; %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="verifylicense.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     String contextPath = request.getContextPath();
     csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
     if(user.isAdministrator())
     {
       csimsoft.DatabaseOrders orders = new csimsoft.DatabaseOrders();
       csimsoft.DatabaseProducts products =
         new csimsoft.DatabaseProducts(orders.database);
       csimsoft.DatabaseLicenses licenses =
         new csimsoft.DatabaseLicenses(orders.database);
       try
       {
         int orderId = Integer.parseInt(request.getParameter("orderid"));
         orders.database.connect();
         try
         {
           csimsoft.OrderSummary summary = new csimsoft.OrderSummary(orderId);
           summary.setOrderSummary(orders);
           int productId = licenses.getOrderProcessLicense(orderId);
           String location = products.getProductLocation(productId);
           if(location != null && !location.isEmpty())
           { %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr><td class="title-cell">License Approval</td></tr>
      <tr>
        <td>
        License: <a href="<%= contextPath %>/download?file=<%= location %>"><%= location %></a><br />
        When you have received the completed and signed license from the client,
        click the "Approve License" button below.
        </td>
      </tr>
      <tr>
        <td class="list-row" align="right">
        <form action="approvelicense" method="POST" name="verifylicenseform">
          <input type="hidden" name="orderid" value="<%= orderId %>" />
          <input type="submit" value="Approve License" />
        </form>
        </td>
      </tr>
      </table>
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr>
        <td colspan="2">
        <strong>Order Date:</strong> <%= summary.getOrderDate() %>
<%           if(!summary.getLeadSource().isEmpty())
             { %>
        <br /><strong>Source:</strong> <%= summary.getLeadSource() %>
<%           }
             if(!summary.isPurchaseOrderEmpty())
             { %>
        <br /><strong>Purchase Order:</strong> <%= summary.getPurchaseOrder() %>
<%           }
             if(!summary.isReferenceNumberEmpty())
             { %>
        <br /><strong>Reference Number:</strong> <%= summary.getReferenceNumber() %>
<%           } %>
        </td>
      </tr>
<%           if(!summary.isLicensesEmpty())
             { %>
      <tr><td class="title-cell" colspan="2">Order Licenses</td></tr>
      <tr>
        <td class="heading-cell">Quantity</td>
        <td class="heading-cell">License</td>
      </tr>
<%             for(int i = 0; i < summary.getLicenseCount(); i++)
               {
                 csimsoft.OrderSummaryLicense license = summary.getLicense(i); %>
      <tr>
        <td class="list-row"><%= license.getQuantity() %></td>
        <td class="list-row">
          <a href="clientlicense.jsp?license=<%= license.getId() %>"><%= license.getName() %></a>
        </td>
      </tr>
<%             }
             }
             if(!summary.isProductsEmpty())
             { %>
      <tr><td class="title-cell" colspan="2">Order Products</td></tr>
<%             int count = summary.getProductCount();
               for(int i = 0; i < count; i++)
               {
                 csimsoft.OrderSummaryDownload download = summary.getProduct(i); %>
      <tr>
        <td class="list-row" colspan="2"><%= download.getName() %></td>
      </tr>
<%             }
             } %>
      </table>
    </div>
<%         }
           else
           { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The license agreement to download is missing.</td>
      </tr>
      </table>
    </div>
<%         }
         }
         catch(java.sql.SQLException sqle2)
         { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error reading order summary from the database.<br /><%= sqle2 %></td>
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

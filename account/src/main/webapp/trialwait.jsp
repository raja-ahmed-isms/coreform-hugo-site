<%@page contentType="text/html; charset=UTF-8" %>
<% java.util.Locale locale = request.getLocale();
   boolean isJapanese = locale.getLanguage().equals(
     java.util.Locale.JAPANESE.getLanguage()) ||
     locale.getCountry().equals(java.util.Locale.JAPAN.getCountry()); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Trial Wait</title>

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
        <jsp:param name="current" value="trialwait.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     csimsoft.DatabaseOrders orders = new csimsoft.DatabaseOrders();
     try
     {
       int orderId = Integer.parseInt(request.getParameter("orderid"));
       orders.database.connect();
       csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
       if(user.isAdministrator() ||
         orders.isOrderOwnedByUser(orderId, user.getUserId()))
       {
         csimsoft.OrderSummary summary = new csimsoft.OrderSummary(orderId);
         summary.setOrderSummary(orders); %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr>
        <td colspan="4">
        <strong>Order Date:</strong> <%= summary.getOrderDate() %><br />
<%         if(!summary.isReferenceNumberEmpty())
           { %>
        <strong>Reference Number:</strong> <%= summary.getReferenceNumber() %><br />
<%         } %>
        </td>
      </tr>
<%       if(!summary.isLicensesEmpty())
         { %>
      <tr><td class="title-cell" colspan="4">Order Licenses</td></tr>
      <tr>
        <td class="heading-cell">ID</td>
        <td class="heading-cell">Name</td>
        <td class="heading-cell">Quantity</td>
        <td class="heading-cell">Type</td>
      </tr>
<%         for(int i = 0; i < summary.getLicenseCount(); i++)
           {
             csimsoft.OrderSummaryLicense license = summary.getLicense(i); %>
      <tr>
        <td class="list-row"><%= license.getId() %></td>
        <td class="list-row"><%= license.getName() %></td>
        <td class="list-row"><%= license.getQuantity() %></td>
        <td class="list-row"><%= license.getLicenseTypeName() %></td>
      </tr>
<%         }
         }
         if(!summary.isProductsEmpty())
         { %>
      <tr><td class="title-cell" colspan="4">Order Products</td></tr>
<%         int count = summary.getProductCount();
           for(int i = 0; i < count; i++)
           {
             csimsoft.OrderSummaryDownload download = summary.getProduct(i); %>
      <tr>
        <td class="list-row" colspan="4"><%= download.getName() %></td>
      </tr>
<%         }
         } %>
      <tr><td class="title-cell" colspan="4">Thank you for your order!</td></tr>
      <tr>
        <td colspan="4">
        Your request has been submitted. Once your request has been approved, you will be
        notified by email and you can download the trial. Thank you!
        </td>
      </tr>
      </table>
    </div>

<%     }
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
<%   }
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
<%   }
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
<%   }
     orders.database.cleanup();
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

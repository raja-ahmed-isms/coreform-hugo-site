<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Basic Order</title>

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

  <jsp:include page="header.jsp" flush="true" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="basicorder.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     csimsoft.DatabaseOrders orders = new csimsoft.DatabaseOrders();
     csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers(orders.database);
     try
     {
       int orderId = Integer.parseInt(request.getParameter("orderid"));
       orders.database.connect();
       csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
       boolean isAdmin = user.isAdministrator();
       boolean isDistributor = user.isDistributor();
       boolean isOwned = isAdmin;
       if(!isOwned)
       {
         int userId = orders.getOrderUserId(orderId);
         isOwned = userId == user.getUserId();
         if(isDistributor)
         {
           if(isOwned)
             isDistributor = false;
           else
             isOwned = users.isDistributor(userId, user.getUserId());
         }
       }

       if(isOwned)
       {
         try
         {
           csimsoft.OrderSummary summary = new csimsoft.OrderSummary(orderId);
           summary.setOrderSummary(orders); %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr>
        <td colspan="2">
        <strong>Order Date:</strong> <%= summary.getOrderDate() %>
<%         if(summary.getNumberOfSteps() > 0)
           {
             if(summary.getProcessStep() == -1)
             { %>
        <br /><strong>Step:</strong> Completed
<%           }
             else
             { %>
        <br /><strong>Step:</strong> <%= summary.getProcessStep() + 1 %> of
          <%= summary.getNumberOfSteps() %>
<%           }
           }
           if(isAdmin || isDistributor)
           { %>
        <br />
<%           if(!summary.getLeadSource().isEmpty())
             { %>
        <strong>Source:</strong> <%= summary.getLeadSource() %><br />
<%           } %>
        <form action="newpurchaseorder" method="POST" name="newpoform"
          accept-charset="UTF-8">
          <input type="hidden" name="orderid" value="<%= orderId %>" />
        <table border="0" cellpadding="3" cellspacing="0">
          <tr>
            <td><b>Purchase Order:</strong></td>
            <td>
              <input type="text" name="po" value="<%= summary.getPurchaseOrder() %>" />
            </td>
          </tr>
          <tr>
            <td><b>Reference Number:</strong></td>
            <td>
              <input type="text" name="rn" value="<%= summary.getReferenceNumber() %>" />
            </td>
          </tr>
          <tr>
            <td colspan="2" align="right"><input type="submit" value="Set" /></td>
          </tr>
        </table>
        </form>
<%         }
           else
           {
             if(!summary.isPurchaseOrderEmpty())
             { %>
        <br /><b>Purchase Order:</strong> <%= summary.getPurchaseOrder() %>
<%           }
             if(!summary.isReferenceNumberEmpty())
             { %>
        <br /><b>Reference Number:</strong> <%= summary.getReferenceNumber() %>
<%           }
           } %>
        </td>
      </tr>
<%         if(!summary.isLicensesEmpty())
           {
             String detailPage = "licensedetail";
             if(isAdmin || isDistributor)
               detailPage = "clientlicense.jsp"; %>
      <tr><td class="title-cell" colspan="2">Order Licenses</td></tr>
      <tr>
        <td class="heading-cell">Quantity</td>
        <td class="heading-cell">License</td>
      </tr>
<%           for(int i = 0; i < summary.getLicenseCount(); i++)
             {
               csimsoft.OrderSummaryLicense license = summary.getLicense(i); %>
      <tr>
        <td class="list-row"><%= license.getQuantity() %></td>
        <td class="list-row">
          <a href="<%= detailPage %>?license=<%= license.getId() %>"><%= license.getName() %></a>
        </td>
      </tr>
<%           }
           }
           if(!summary.isProductsEmpty())
           { %>
      <tr><td class="title-cell" colspan="2">Order Products</td></tr>
<%           int count = summary.getProductCount();
             String contextPath = request.getContextPath();
             for(int i = 0; i < count; i++)
             {
               csimsoft.OrderSummaryDownload download = summary.getProduct(i);
               String downloadText = download.getName();
               if(summary.getProcessStep() == -1)
               {
                 downloadText = "<a href=\"" + contextPath + "/download?file=" +
                   download.getLocation() + "\">" + downloadText + "</a>";
               } %>
      <tr>
        <td class="list-row" colspan="2"><%= downloadText %></td>
      </tr>
<%           }
           } %>
      </table>
    </div>

<%       }
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
<%       }
       }
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
<%   }
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
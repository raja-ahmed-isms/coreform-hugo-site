<%@page contentType="text/html; charset=UTF-8" %>
<% java.util.Locale locale = request.getLocale();
   boolean isJapanese = locale.getLanguage().equals(
     java.util.Locale.JAPANESE.getLanguage()) ||
     locale.getCountry().equals(java.util.Locale.JAPAN.getCountry()); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Orders</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="formhelp.js"></script>
<% boolean loginSuccess = csimsoft.LoginServlet.isUserLoggedIn(session);
   String initFocus = "";
   if(loginSuccess)
   { %>
<script type="text/javascript">
function sendDeleteOrderForm(orderId)
{
  if(confirm("Are you sure you want to delete order #" + orderId + "?"))
  {
    document.deleteorderform.orderid.value = orderId
    document.deleteorderform.submit()
  }
}
</script>
<% }
   else
     initFocus = "onload=\"focusLoginForm()\""; %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

  <jsp:include page="header.jsp" flush="true" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="orders" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(loginSuccess)
   {
     csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session); %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr><td class="title-cell" colspan="5">Active Orders</td></tr>
<%   csimsoft.DatabaseOrders orders = new csimsoft.DatabaseOrders();
     try
     {
       orders.database.connect();
       orders.getActiveUserOrders(user.getUserId()); %>
      <tr>
        <td class="heading-cell">Order Number</td>
        <td class="heading-cell">Date</td>
        <td class="heading-cell">PO Number</td>
        <td class="heading-cell">Reference</td>
        <td class="heading-cell">Details</td>
      </tr>
<%     int i = 0;
       String orderId = null;
       String orderDate = null;
       String orderNumber = null;
       String refNumber = null;
       for( ; orders.database.results.next(); i++)
       {
         orderId = orders.database.results.getString("orderid");
         orderDate = csimsoft.Database.formatDate(
           orders.database.results.getDate("orderdate"));
         orderNumber = orders.database.results.getString("ordernumber");
         if(orderNumber == null || orderNumber.isEmpty())
           orderNumber = "&nbsp;";
         refNumber = orders.database.results.getString("distnumber");
         if(refNumber == null || refNumber.isEmpty())
           refNumber = "&nbsp;"; %>
      <tr>
        <td class="list-row"><%= orderId %></td>
        <td class="list-row"><%= orderDate %></td>
        <td class="list-row"><%= orderNumber %></td>
        <td class="list-row"><%= refNumber %></td>
        <td class="list-row"><a href="orderinfo?orderid=<%= orderId %>">Show Details</a></td>
      </tr>
<%     }

       orders.database.results.close();
       if(i == 0)
       { %>
      <tr><td class="list-row" colspan="5">
      There are no active orders available.
      </td></tr>
<%     } %>
      <tr><td colspan="5">&nbsp;</td></tr>
      <tr><td class="title-cell" colspan="5">Completed Orders</td></tr>
<%     orders.getCompletedUserOrders(user.getUserId()); %>
      <tr>
        <td class="heading-cell">Order Number</td>
        <td class="heading-cell">Date</td>
        <td class="heading-cell">PO Number</td>
        <td class="heading-cell">Reference</td>
        <td class="heading-cell">Details</td>
      </tr>
<%     for(i = 0; orders.database.results.next(); i++)
       {
         orderId = orders.database.results.getString("orderid");
         orderDate = csimsoft.Database.formatDate(
           orders.database.results.getDate("orderdate"));
         orderNumber = orders.database.results.getString("ordernumber");
         if(orderNumber == null || orderNumber.isEmpty())
           orderNumber = "&nbsp;";
         refNumber = orders.database.results.getString("distnumber");
         if(refNumber == null || refNumber.isEmpty())
           refNumber = "&nbsp;"; %>
      <tr>
        <td class="list-row"><%= orderId %></td>
        <td class="list-row"><%= orderDate %></td>
        <td class="list-row"><%= orderNumber %></td>
        <td class="list-row"><%= refNumber %></td>
        <td class="list-row"><a href="orderinfo?orderid=<%= orderId %>">Show Details</a></td>
      </tr>
<%     }

       orders.database.results.close();
       if(i == 0)
       { %>
      <tr><td class="list-row" colspan="5">
      There are no completed orders available.
      </td></tr>
<%     }
     }
     catch(java.sql.SQLException sqle)
     { %>
      <tr><td class="error-msg" colspan="5">
      Error: Unable to connect to the database.
      </td></tr>
<%   }
     catch(javax.naming.NamingException ne)
     { %>
      <tr><td class="error-msg" colspan="5">
      Error: Unable to locate the database resource.
      </td></tr>
<%   }
     orders.database.cleanup(); %>
      </table>
    </div>

<% }
   else
   { %>
    <div class="content-login">
      <jsp:include page="loginform.jsp" flush="true" >
      <jsp:param name="page" value="<%= request.getServletPath() %>" />
      </jsp:include>
    </div>
<% } %>

  </div>

  <div class="push"></div>

  <jsp:include page="footer.jsp" flush="true" />


</div>

</body>

</html>

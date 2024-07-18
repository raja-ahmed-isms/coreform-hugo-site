<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Client Orders</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="formhelp.js"></script>
<% boolean loginSuccess = csimsoft.LoginServlet.isUserLoggedIn(session);
   csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
   boolean isAdmin = user.isAdministrator();
   boolean isDistributor = user.isDistributor();
   String initFocus = "";
   if(!loginSuccess)
     initFocus = "onload=\"focusLoginForm()\"";
   else if(isAdmin || isDistributor)
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
<% } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

<% String idString = request.getParameter("user");
   String currentPage = "distorders";
   if(idString == null || idString.isEmpty())
     currentPage = "distorders.jsp"; %>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
         <jsp:param name="current" value="<%= currentPage %>" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(loginSuccess)
   {
     if(isAdmin || isDistributor)
     {
       csimsoft.OrderList orders = new csimsoft.OrderList();
       orders.setType(request.getParameter("otype"));
       orders.pages.setPage(request.getParameter("page"));
       try
       {
         int userId = 0;
         String activeUser = "";
         String completedUser = "";
         if(isDistributor)
           userId = user.getUserId();
         else if(idString != null)
         {
           userId = Integer.parseInt(request.getParameter("user"));
           activeUser = "?user=" + userId;
           completedUser = "&user=" + userId;
         }

         if(userId > 0)
         {
           orders.setUserId(userId, isAdmin);
           orders.connect(); %>

    <div class="content">
      <table border="0" celpadding="6" celspacing="0" width="100%">
<%         if(isAdmin || orders.pages.getPages() > 1)
           { %>
      <tr>
        <td colspan="5">
<%           if(isAdmin)
             {
               csimsoft.UserProfileInfo info = orders.getUserInfo(); %>
          <strong><%= info.getFullName() %></strong>:
          <a href="clientinfo.jsp?user=<%= userId %>"><%= info.getEmail() %></a>
<%           }
             if(orders.pages.getPages() > 1)
             { %>
          <%= orders.pages.getMenu() %>
<%           } %>
        </td>
      </tr>
<%         } %>
      <tr>
        <td class="title-cell" colspan="5">
        <ul class="title-menu">
          <li class="title-menuitem"><a
          class="<%= orders.getType() == csimsoft.OrderList.ActiveOrders ? "title-linkcurrent" : "title-link" %>"
          href="distorders.jsp<%= activeUser %>">Active Orders</a></li>
          <li class="title-menuitem"><a
          class="<%= orders.getType() == csimsoft.OrderList.CompletedOrders ? "title-linkcurrent" : "title-link" %>"
          href="distorders.jsp?otype=completed<%= completedUser %>">Completed Orders</a></li>
        </ul>
        </td>
      </tr>
      <tr>
        <td colspan="2" class="heading-cell">Order Number</td>
        <td class="heading-cell">Date</td>
        <td class="heading-cell">Process</td>
        <td class="heading-cell">Email</td>
      </tr>
<%         while(orders.hasNext())
           {
             csimsoft.OrderInfo order = orders.getNext(); %>
      <tr>
        <td class="list-icon"><a href="javascript:sendDeleteOrderForm('<%= order.getId() %>')"><img
        src="./images/icon-delete.jpg" width="20" height="20" /></a></td>
        <td class="list-row"><a href="orderinfo?orderid=<%= order.getId() %>"><%= order.getId() %></a></td>
        <td class="list-row"><%= order.getDate() %></td>
        <td class="list-row"><%= order.getProcessName() %></td>
        <td class="list-row"><a href="distclient.jsp?user=<%= order.getUserId() %>"><%= order.getEmail() %></a></td>
      </tr>
<%         }

           if(orders.isEmpty())
           {
             String emptyMessage = "";
             if(orders.pages.getStartRow() > 1)
               emptyMessage = "more ";
             if(orders.getType() == csimsoft.OrderList.ActiveOrders)
               emptyMessage += "active";
             else
               emptyMessage += "completed"; %>
      <tr><td class="list-row" colspan="5">
      There are no <%= emptyMessage %> orders in the database.
      </td></tr>
<%         }
           else if(orders.pages.getPages() > 1)
           { %>
      <tr>
        <td colspan="5">
          <%= orders.pages.getMenu() %>
        </td>
      </tr>
<%         } %>
      <tr><td colspan="5"><form action="removeorder" method="POST"
        name="deleteorderform">
        <input type="hidden" name="orderid" value="" />
<%         if(isAdmin)
           { %>
        <input type="hidden" name="dist" value="<%= userId %>" />
<%         }
           if(orders.getType() > csimsoft.OrderList.ActiveOrders)
           { %>
        <input type="hidden" name="otype" value="<%= orders.getTypeName() %>" />
<%         }
           if(orders.pages.getPage() > 1)
           { %>
        <input type="hidden" name="page" value="<%= orders.pages.getPage() %>" />
<%         } %>
      </form></td></tr>
      </table>
    </div>

<%       }
         else
         { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The user id is not valid.</td>
      </tr>
      </table>
    </div>
<%       }
       }
       catch(NumberFormatException nfe)
       { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The user id is not valid.</td>
      </tr>
      </table>
    </div>
<%     }
       catch(java.sql.SQLException sqle)
       { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
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
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Unable to locate the database resource.<br /><%= ne %></td>
      </tr>
      </table>
    </div>
<%     }
       orders.cleanup();
     }
     else
     { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">You are not authorized to view the contents of this page.</td>
      </tr>
      </table>
    </div>
<%   }
   }
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

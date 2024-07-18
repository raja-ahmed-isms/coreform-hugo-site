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

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="clientorders.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin || isDistributor)
     {
       csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers(); %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
<%     try
       {
         int userId = Integer.parseInt(request.getParameter("user"));
         users.database.connect();
         csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
         if(users.getUserProfile(userId, info))
         {
           if(isAdmin || (isDistributor &&
             info.getGroupId() == users.getDistibutorGroup(user.getUserId())))
           { %>
      <tr>
        <td colspan="5">
          <table border="0" celpadding="0" celspacing="0" width="100%">
            <tr>
              <td>
                <strong><%= info.getFullName() %></strong>:
                <a href="<%= isAdmin ? "clientinfo" : "distclient" %>.jsp?user=<%= userId %>">
                <%= info.getEmail() %></a>
              </td>
              <td align="right">
                <form action="selectlicense.jsp" method="GET">
                  <input type="hidden" name="user" value="<%= userId %>" />
                  <input type="hidden" name="next" value="neworder" />
                  <input type="hidden" name="seltype" value="both" />
                  <input type="submit" value="Add Order" />
                </form>
              </td>
            </tr>
          </table>
        </td>
      </tr>
<%           csimsoft.DatabaseOrders orders = new csimsoft.DatabaseOrders(users.database);
             orders.getActiveUserOrders(userId); %>
      <tr>
        <td class="title-cell" colspan="6">Active Orders</td>
      </tr>
      <tr>
        <td class="heading-cell" colspan="2">Order Number</td>
        <td class="heading-cell">Date</td>
        <td class="heading-cell">PO Number</td>
        <td class="heading-cell">Reference</td>
        <td class="heading-cell">Details</td>
      </tr>
<%           int i = 0;
             int orderId = 0;
             String orderDate = null;
             String orderNumber = null;
             String refNumber = null;
             for( ; orders.database.results.next(); i++)
             {
               orderId = orders.database.results.getInt("orderid");
               orderDate = csimsoft.Database.formatDate(
                 orders.database.results.getDate("orderdate"));
               orderNumber = orders.database.results.getString("ordernumber");
               if(orderNumber == null || orderNumber.isEmpty())
                 orderNumber = "&nbsp;";
               refNumber = orders.database.results.getString("distnumber");
               if(refNumber == null || refNumber.isEmpty())
                 refNumber = "&nbsp;"; %>
      <tr>
        <td class="list-icon"><a href="javascript:sendDeleteOrderForm('<%= orderId %>')"
        ><img src="./images/icon-delete.jpg" width="20" height="20" /></a></td>
        <td class="list-row"><%= orderId %></td>
        <td class="list-row"><%= orderDate %></td>
        <td class="list-row"><%= orderNumber %></td>
        <td class="list-row"><%= refNumber %></td>
        <td class="list-row"><a href="orderinfo?orderid=<%= orderId %>">Show Details</a></td>
      </tr>
<%           }
             orders.database.results.close();
             if(i == 0)
             { %>
      <tr><td class="list-row" colspan="6">
      There are no active orders available for the user.
      </td></tr>
<%           } %>
      <tr>
        <td class="title-cell" colspan="6">Completed Orders</td>
      </tr>
      <tr>
        <td class="heading-cell" colspan="2">Order Number</td>
        <td class="heading-cell">Date</td>
        <td class="heading-cell">PO Number</td>
        <td class="heading-cell">Reference</td>
        <td class="heading-cell">Details</td>
      </tr>
<%           orders.getCompletedUserOrders(userId);
             for(i = 0; orders.database.results.next(); i++)
             {
               orderId = orders.database.results.getInt("orderid");
               orderDate = csimsoft.Database.formatDate(
                 orders.database.results.getDate("orderdate"));
               orderNumber = orders.database.results.getString("ordernumber");
               if(orderNumber == null || orderNumber.isEmpty())
                 orderNumber = "&nbsp;";
               refNumber = orders.database.results.getString("distnumber");
               if(refNumber == null || refNumber.isEmpty())
                 refNumber = "&nbsp;"; %>
      <tr>
        <td class="list-icon"><a href="javascript:sendDeleteOrderForm('<%= orderId %>')"
        ><img src="./images/icon-delete.jpg" width="20" height="20" /></a></td>
        <td class="list-row"><%= orderId %></td>
        <td class="list-row"><%= orderDate %></td>
        <td class="list-row"><%= orderNumber %></td>
        <td class="list-row"><%= refNumber %></td>
        <td class="list-row"><a href="orderinfo?orderid=<%= orderId %>">Show Details</a></td>
      </tr>
<%           }
             orders.database.results.close();
             if(i == 0)
             { %>
      <tr><td class="list-row" colspan="6">
      There are no completed orders available for the user.
      </td></tr>
<%           } %>
      <tr>
        <td colspan="6">
          <form action="removeorder" method="POST" name="deleteorderform">
            <input type="hidden" name="orderid" value="" />
            <input type="hidden" name="user" value="<%= userId %>" />
          </form>
        </td>
      </tr>
<%         }
           else
           { %>
      <tr>
        <td class="error-msg" colspan="6">Error: You are not authorized to view this client.</td>
      </tr>
<%         }
         }
         else
         { %>
      <tr>
        <td class="error-msg" colspan="6">Error: Invalid user Id.</td>
      </tr>
<%       }
       }
       catch(NumberFormatException nfe)
       { %>
      <tr>
        <td class="error-msg" colspan="6">Error: Invalid user Id.</td>
      </tr>
<%     }
       catch(Exception e)
       { %>
      <tr>
        <td class="error-msg" colspan="6">Error: Unable to connect to the database.<br /><%= e %></td>
      </tr>
<%     }
       users.database.cleanup(); %>
      </table>
    </div>
<%   }
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

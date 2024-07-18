<%@page contentType="text/html; charset=UTF-8" %>
<% if(request.getCharacterEncoding() == null)
     request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Reseller Clients</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="formhelp.js"></script>
<% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
   boolean isAdmin = user.isAdministrator();
   boolean isReseller = user.isReseller();
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\"";
   else if(isAdmin)
   { %>
<script type="text/javascript">
function sendDeleteForm(userid, email)
{
  if(confirm("Are you sure you want to remove\n" + email + "?"))
  {
    document.deleteuserform.user.value = userid
    document.deleteuserform.submit()
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
   String currentPage = "resellerusers";
   if(idString == null || idString.isEmpty())
     currentPage = "resellerusers.jsp"; %>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="<%= currentPage %>" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />
<% if(isLoggedIn)
   {
     if(isAdmin || isReseller)
     {
       csimsoft.ResellerUsers userList = new csimsoft.ResellerUsers();
       try
       {
         int userId = 0;
         if(idString != null)
           userId = Integer.parseInt(request.getParameter("user"));
         else if(isReseller)
           userId = user.getUserId();

         if(userId > 0)
         {
           userList.setId(userId, isAdmin);
           userList.setSearch(request.getParameter("search"));
           userList.pages.setPage(request.getParameter("page"));
           userList.connect(); %>
    <div class="content">
      <table border="0" celpadding="6" celspacing="0" width="100%">
<%         if(isAdmin)
           {
             csimsoft.UserProfileInfo info = userList.getUserInfo(); %>
        <tr>
          <td colspan="3">
            <strong><%= info.getFullName() %></strong>:
            <a href="clientinfo.jsp?user=<%= userId %>"><%= info.getEmail() %></a>
          </td>
        </tr>
<%         } %>
        <tr>
          <td colspan="3">
            <ul class="tool-bar">
              <li class="tool-button">
                <a href="selectlicense.jsp?next=licensesearch.jsp&seltype=both&reseller=<%= userId %>">
                <img src="./images/icon-license.png" width="20" height="20" />
                License Search</a>
              </li>
            </ul>
<%       if(userList.pages.getPages() > 1)
         { %>
            <%= userList.pages.getMenu() %>
<%       } %>
          </td>
        </tr>
        <tr>
          <td class="title-cell" colspan="3">
            Clients
            <form action="resellerusers.jsp" method="GET" name="searchForm"
              accept-charset="UTF-8" style="float:right;margin-bottom:1px">
              <input type="text" name="search" value="<%= userList.getSearch() %>" />
              <input type="submit" value="Find" />
            </form>
          </td>
        </tr>
        <tr>
          <td class="heading-cell" colspan="2">Name</td>
          <td class="heading-cell">Email</td>
        </tr>
<%         while(userList.hasNext())
           {
             csimsoft.UserProfileInfo client = userList.getNext(); %>
        <tr>
<%           if(isAdmin)
             { %>
          <td class="list-icon">
            <a href="javascript:sendDeleteForm('<%= client.getId() %>', '<%= client.getEmail() %>')">
            <img src="./images/icon-delete.jpg" width="20" height="20" /></a>
          </td>
<%           }
             else
             { %>
          <td class="list-icon">
            <img src="./images/icon-user.png" width="20" height="20" />
          </td>
<%           } %>
          <td class="list-row">
            <a href="resellerclient.jsp?user=<%= client.getId() %>">
            <%= client.getFullName() %></a>
          </td>
          <td class="list-row"><%= client.getEmail() %></td>
        </tr>
<%         }
           if(userList.isEmpty())
           { %>
        <tr>
          <td class="list-row" colspan="3">
            There are no clients in the database.
          </td>
        </tr>
<%         }
           else if(userList.pages.getPages() > 1)
           { %>
        <tr>
          <td colspan="3">
            <%= userList.pages.getMenu() %>
          </td>
        </tr>
<%         }
           if(isAdmin)
           { %>
        <tr>
          <td colspan="3">
            <form action="changeusergroup" method="POST" name="deleteuserform">
              <input type="hidden" name="user" value="" />
              <input type="hidden" name="groupid" value="0" />
              <input type="hidden" name="reseller" value="<%= userId %>" />
            </form>
          </td>
        </tr>
<%         } %>
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
        <td class="error-msg">Error: Unable to connect to the database.<br/><%= sqle %></td>
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
        <td class="error-msg">Error: Unable to locate the database resource.</td>
      </tr>
      </table>
    </div>
<%     }
       userList.cleanup();
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

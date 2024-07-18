<%@page contentType="text/html; charset=UTF-8" %>
<% if(request.getCharacterEncoding() == null)
     request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>

<head>

  <title>Coreform - User List</title>

  <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
  <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
  <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

  <!-- Web Fonts -->
  <link
    href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext'
    rel='stylesheet' type='text/css'>

  <!-- ICON FONTS -->
  <link rel="stylesheet" type="text/css" href="..css/simple-line-icons.css" />

  <script type="text/javascript" src="formhelp.js"></script>
  <% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
   boolean isAdmin = user.isAdministrator();
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\"";
   else if(isAdmin)
   { %>
  <script type="text/javascript">
    function sendDeleteForm(userid, email) {
      if (confirm("Are you sure you want to delete\n" + email + "?")) {
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

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true">
        <jsp:param name="current" value="userlist.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

    <% if(isLoggedIn)
   {
     if(isAdmin)
     {
       csimsoft.UserList userList = new csimsoft.UserList();
       userList.setSearch(request.getParameter("search"), request.getParameter("role"));
       userList.pages.setPage(request.getParameter("page"));
       csimsoft.LicenseCounts licenses = csimsoft.LicenseCounts.getCounts();
       try
       {
         userList.connect(); %>
    <div class="content">
      <label for="all_active">Active Coreform Cubit Licenses:</label>
      <output name="all_active"><%= licenses.activeLicenses %>;</output>
      <label for="all_trials">Active Trial Licenses:</label>
      <output name="all_trials"><%= licenses.activeTrials %></output>
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
        <tr>
          <td colspan="3">
            <ul class="tool-bar">
              <li class="tool-button">
                <a href="signup.jsp">
                  <img src="./images/icon-new-user.png" width="20" height="20" />
                  Add User</a>
              </li>
              <li class="tool-button">
                <a href="selectlicense.jsp?next=licensesearch.jsp">
                  <img src="./images/icon-license.png" width="20" height="20" />
                  License Search</a>
              </li>
              <li class="tool-button">
                <a href="selectproduct.jsp?next=productsearch&seltype=files">
                  <img src="./images/icon-file.png" width="20" height="20" />
                  Product Search</a>
              </li>
              <li class="tool-button">
                <a href="ordersearch.jsp">
                  <img src="./images/icon-order.png" width="20" height="20" />
                  Order Search</a>
              </li>
              <li class="tool-button">
                <a href="searchkey.jsp">
                  <img src="./images/icon-license.png" width="20" height="20" />
                  Search by Key</a>
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
            <ul class="title-menu">
              <li class="title-menuitem">
                <a class="title-link<%= userList.getListType() == csimsoft.UserList.AllUserList ? "current" : "" %>"
                  href="userlist.jsp">All Users</a>
              </li>
              <li class="title-menuitem">
                <a class="title-link<%= userList.getListType() == csimsoft.UserList.AdminList ? "current" : "" %>"
                  href="userlist.jsp?role=<%= java.net.URLEncoder.encode(csimsoft.UserInfo.AdminRoleName, "UTF-8") %>">
                  Administrators</a>
              </li>
              <li class="title-menuitem">
                <a class="title-link<%= userList.getListType() == csimsoft.UserList.DistributorList ? "current" : "" %>"
                  href="userlist.jsp?role=<%= java.net.URLEncoder.encode(csimsoft.UserInfo.DistributorRoleName, "UTF-8") %>">
                  Distributors</a>
              </li>
              <li class="title-menuitem">
                <a class="title-link<%= userList.getListType() == csimsoft.UserList.ResellerList ? "current" : "" %>"
                  href="userlist.jsp?role=<%= java.net.URLEncoder.encode(csimsoft.UserInfo.ResellerRoleName, "UTF-8") %>">
                  Resellers</a>
              </li>
            </ul>
            <form action="userlist.jsp" method="GET" name="searchForm" accept-charset="UTF-8"
              style="float:right;margin-bottom:1px">
              <input type="text" name="search" value="<%= userList.getSearch() %>" />
              <input type="submit" value="Find" />
            </form>
          </td>
        </tr>
        <tr>
          <td colspan="2" class="heading-cell">Name</td>
          <td class="heading-cell">Email</td>
        </tr>
        <%       while(userList.hasNext())
         {
           csimsoft.UserProfileInfo info = userList.getNext(); %>
        <tr>
          <td class="list-icon">
            <a href="javascript:sendDeleteForm('<%= info.getId() %>', '<%= info.getJsEmail() %>')">
              <img src="./images/icon-delete.jpg" width="20" height="20" /></a>
          </td>
          <td class="list-row">
            <a href="clientinfo.jsp?user=<%= info.getId() %>">
              <%= info.getLastFirstName() %></a>
          </td>
          <td class="list-row"><%= info.getEmail() %></td>
        </tr>
        <%       }

         if(userList.isEmpty())
         { %>
        <tr>
          <td class="list-row" colspan="3">
            There are no <%= userList.pages.getStartRow() > 1 ? "more" : "" %> users in
            the database.
          </td>
        </tr>
        <%       }
         else if(userList.pages.getPages() > 1)
         { %>
        <tr>
          <td colspan="3">
            <%= userList.pages.getMenu() %>
          </td>
        </tr>
        <%       } %>
        <tr>
          <td colspan="3">
            <form action="removeuser" method="POST" name="deleteuserform">
              <input type="hidden" name="user" value="" />
              <%       if(userList.getListType() == csimsoft.UserList.SimpleSearchList)
         { %>
              <input type="hidden" name="search" value="<%= userList.getSearch() %>" />
              <%       }
         else if(userList.getListType() > csimsoft.UserList.AllUserList)
         { %>
              <input type="hidden" name="role" value="<%= userList.getRole() %>" />
              <%       }
         if(userList.pages.getPage() > 1)
         { %>
              <input type="hidden" name="page" value="<%= userList.pages.getPage() %>" />
              <%       } %>
            </form>
          </td>
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
       userList.cleanup();
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
   { %>
    <div class="content-login">
      <jsp:include page="loginform.jsp" flush="true">
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

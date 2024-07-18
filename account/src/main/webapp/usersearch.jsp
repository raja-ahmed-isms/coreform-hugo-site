<%@page contentType="text/html; charset=UTF-8" %>
<% if(request.getCharacterEncoding() == null)
     request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - User Search</title>

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
   else if(isAdmin)
   { %>
<script type="text/javascript">
function sendDeleteForm(userid, email)
{
  if(confirm("Are you sure you want to delete\n" + email + "?"))
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

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="usersearch.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin || isDistributor)
     {
       csimsoft.UserSearchList userList = new csimsoft.UserSearchList();
       userList.pages.setPage(request.getParameter("page"));
       if(!isAdmin)
         userList.setDistributorId(user.getUserId());

       if(userList.setSearch(request.getParameter("search")))
       {
         try
         {
           userList.connect();
           if(!isAdmin && !userList.isDistributorValid())
           { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">You are not authorized to make the given search.</td>
      </tr>
      </table>
    </div>
<%         }
           else
           {
             String searchLink = java.net.URLEncoder.encode(userList.getSearch(), "UTF-8"); %>
    <div class="content">
      <table border="0" celpadding="6" celspacing="0" width="100%">
        <tr>
          <td colspan="3">
            <ul class="tool-bar">
              <li class="tool-button">
                <a href="downloadsearch?search=<%= searchLink %>">
                <img src="./images/icon-download.png" width="20" height="20" />
                Download List</a>
              </li>
            </ul>
<%           if(userList.pages.getPages() > 1)
             { %>
            <%= userList.pages.getMenu() %>
<%           } %>
          </td>
        </tr>
        <tr>
          <td class="title-cell" colspan="3">Search Results</td>
        </tr>
        <tr>
          <td colspan="2" class="heading-cell">Name</td>
          <td class="heading-cell">Email</td>
        </tr>
<%           while(userList.hasNext())
             {
               csimsoft.UserProfileInfo info = userList.getNext(); %>
        <tr>
<%             if(isAdmin)
               { %>
          <td class="list-icon">
            <a href="javascript:sendDeleteForm('<%= info.getId() %>', '<%= info.getJsEmail() %>')">
            <img src="./images/icon-delete.jpg" width="20" height="20" /></a>
          </td>
          <td class="list-row">
            <a href="clientinfo.jsp?user=<%= info.getId() %>">
            <%= info.getLastFirstName() %></a>
          </td>
<%             }
               else
               { %>
          <td class="list-icon">
            <img src="./images/icon-user.png" width="20" height="20" />
          </td>
          <td class="list-row">
            <a href="distclient.jsp?user=<%= info.getId() %>">
            <%= info.getLastFirstName() %></a>
          </td>
<%             } %>
          <td class="list-row"><%= info.getEmail() %></td>
        </tr>
<%           }

             if(userList.isEmpty())
             { %>
        <tr>
          <td class="list-row" colspan="3">
            There are no <%= userList.pages.getStartRow() > 1 ? "more" : "" %> users in
            the database matching this search.
          </td>
        </tr>
<%           }
             else if(userList.pages.getPages() > 1)
             { %>
        <tr>
          <td colspan="3">
            <%= userList.pages.getMenu() %>
          </td>
        </tr>
<%           }
             if(isAdmin)
             { %>
        <tr>
          <td colspan="3">
            <form action="removeuser" method="POST" name="deleteuserform">
              <input type="hidden" name="from" value="<%= request.getServletPath() %>" />
              <input type="hidden" name="user" value="" />
              <input type="hidden" name="search" value="<%= userList.getSearch() %>" />
<%             if(userList.pages.getPage() > 1)
               { %>
              <input type="hidden" name="page" value="<%= userList.pages.getPage() %>" />
<%             } %>
            </form>
          </td>
        </tr>
<%           } %>
      </table>
    </div>

<%         }
         }
         catch(java.io.UnsupportedEncodingException uee)
         { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error encoding the search parameters.<br/></td>
      </tr>
      </table>
    </div>
<%       }
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
<%       }
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
<%       }
       }
       else
       { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error parsing the search string.</td>
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

<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Distributor Licenses</title>

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
function sendDeleteLicenseForm(licenseid, licensename)
{
  if(confirm("Are you sure you want to remove\n" + licensename + "?"))
  {
    document.deletelicenseform.license.value = licenseid
    document.deletelicenseform.submit()
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
   String currentPage = "distlicense";
   if(idString == null || idString.isEmpty())
     currentPage = "distlicense.jsp"; %>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="<%= currentPage %>" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />
<% if(isLoggedIn)
   {
     if(isAdmin || isDistributor)
     {
       csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers();
       csimsoft.DatabaseLicenses licenses = new csimsoft.DatabaseLicenses(users.database);
       try
       {
         int userId = 0;
         if(isDistributor)
           userId = user.getUserId();
         else if(idString != null)
           userId = Integer.parseInt(idString);

         if(userId > 0)
         {
           users.database.connect();
           csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
           if(users.getUserProfile(userId, info))
           {
             licenses.getProductLicenses(userId); %>

    <div class="content">
      <table border="0" celpadding="6" celspacing="0" width="100%">
<%           if(isAdmin)
             { %>
        <tr>
          <td colspan="3">
            <strong><%= info.getFullName() %></strong>:
            <a href="clientinfo.jsp?user=<%= userId %>"><%= info.getEmail() %></a><br />
            <ul class="tool-bar">
              <li class="tool-button">
                <a href="selectlicense.jsp?next=newdistlicense&user=<%= userId %>">
                <img src="./images/icon-new-license.png" width="20" height="20" />
                Add License</a>
              </li>
            </ul>
          </td>
        </tr>
<%           } %>
        <tr>
          <td class="title-cell" colspan="3">Licenses</td>
        </tr>
        <tr>
          <td class="heading-cell" colspan="2">Name</td>
          <td class="heading-cell">Version</td>
        </tr>
<%           int i = 0;
             for( ; licenses.database.results.next(); ++i)
             {
               int licenseId = licenses.database.results.getInt("licenseid");
               String licenseName = licenses.database.results.getString("licensename"); %>
        <tr>
          <td class="list-icon">
<%             if(isAdmin)
               { %>
            <a href="javascript:sendDeleteLicenseForm('<%= licenseId %>', '<%= licenseName.replace("'", "\\'") %>')">
            <img src="./images/icon-delete.jpg" width="20" height="20" /></a>
<%             }
               else
               { %>
            <img src="./images/icon-license.png" width="20" height="20" />
<%             } %>
          </td>
          <td class="list-row"><%= licenseName %></td>
          <td class="list-row"><%= licenses.database.results.getString("version") %></td>
        </tr>
<%           }
             if(i == 0)
             { %>
        <tr>
          <td class="list-row" colspan="3">
            There are no licenses in the database.
          </td>
        </tr>
<%           }
             else if(isAdmin)
             { %>
        <tr>
          <td colspan="3">
            <form action="removedistlicense" method="POST" name="deletelicenseform">
              <input type="hidden" name="user" value="<%= userId %>" />
              <input type="hidden" name="license" value="" />
            </form>
          </td>
        </tr>
<%           } %>
      </table>
    </div>

<%         }
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
<%         }
         }
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
        <td class="error-msg">Error: Unable to connect to the database.</td>
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
       users.database.cleanup();
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

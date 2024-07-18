<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Client Licenses</title>

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
   boolean isReseller = user.isReseller();
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\"";
   else if(isAdmin || isDistributor || isReseller)
   { %>
<script type="text/javascript">
function sendDeleteLicenseForm(licenseId, licenseName)
{
  if(confirm("Are you sure you want to delete license:\n" + licenseName + "?"))
  {
    document.deletelicenseform.license.value = licenseId
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

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="clientlicenses.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin || isDistributor || isReseller)
     {
       csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers();
       csimsoft.DatabaseLicenses manager =
         new csimsoft.DatabaseLicenses(users.database);
       try
       {
         int userId = Integer.parseInt(request.getParameter("user"));
         users.database.connect();
         csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
         if(users.getUserProfile(userId, info))
         {
           if(isAdmin || (isDistributor &&
             info.getGroupId() == users.getDistibutorGroup(user.getUserId()))
			 || (isReseller && info.getGroupId() == users.getResellerGroup(user.getUserId())))
           {
             csimsoft.ClientLicenses licenses = new csimsoft.ClientLicenses();
             licenses.setLicenses(manager, userId, true); %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr>
        <td colspan="5">
          <table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr>
              <td>
                <strong><%= info.getFullName() %></strong>:
                <a href="<%= isAdmin ? "clientinfo" : "distclient" %>.jsp?user=<%= userId %>">
                <%= info.getEmail() %></a>
              </td>
			  <% if(isAdmin || isDistributor)
			  { %>
              <td align="right">
                <form action="selectlicense.jsp" method="GET">
                  <input type="hidden" name="user" value="<%= userId %>" />
                  <input type="hidden" name="next" value="newclientlicense" />
                  <input type="submit" value="Add License" />
                </form>
              </td>
			<% } %>
            </tr>
          </table>
        </td>
      </tr>
      <tr>
        <td class="title-cell" colspan="7">Licenses</td>
      </tr>
      <tr>
        <td class="heading-cell" colspan="2">Name</td>
        <td class="heading-cell">#</td>
        <td class="heading-cell">Reference</td>
        <td class="heading-cell">Type</td>
        <td class="heading-cell">Maintenance</td>
        <td class="heading-cell">Expiration</td>
      </tr>
<%           if(licenses.isEmpty())
             { %>
      <tr>
        <td class="list-row" colspan="7">
          There are no licenses available for the user.
        </td>
      </tr>
<%           }
             else
             {
               for(int i = 0; i < licenses.getLicenseCount(); ++i)
               {
                 csimsoft.ClientLicenses.License license = licenses.getLicense(i); %>
      <tr>
        <td class="list-icon">
          <a href="javascript:sendDeleteLicenseForm('<%= license.LicenseId %>', '<%= license.getJsName() %>')"
          ><img src="./images/icon-delete.jpg" width="20" height="20" /></a>
        </td>
        <td class="list-row">
          <a href="clientlicense.jsp?license=<%= license.LicenseId %>"><%= license.Name %></a>
        </td>
        <td class="list-row"><%= license.Quantity %></td>
        <td class="list-row"><%= license.RefNumber %></td>
        <td class="list-row"><%= license.getType() %></td>
        <td class="list-row"><%= license.Maintenance %></td>
        <td class="list-row"><%= license.Expiration %></td>
      </tr>
<%             }
             } %>
      <tr>
        <td colspan="7">
          <form action="removeclientlicense" method="POST" name="deletelicenseform">
            <input type="hidden" name="license" value="" />
            <input type="hidden" name="user" value="<%= userId %>" />
          </form>
        </td>
      </tr>
      </table>
    </div>
<%         }
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
<%         }
         }
         else
         { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Invalid user Id.</td>
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
        <td class="error-msg">Error: Invalid user Id.</td>
      </tr>
      </table>
    </div>
<%     }
       catch(Exception e)
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Unable to connect to the database.<br /><%= e %></td>
      </tr>
      </table>
    </div>
<%     }
       users.database.cleanup();
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

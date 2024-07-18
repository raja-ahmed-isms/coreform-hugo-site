<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - License Key</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

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
function focusLicenseForm()
{
  try
  {
    document.licensekeyform.licensekey.focus()
  }
  catch(err)
  {
  }
}
</script>
<%   initFocus = "onload=\"focusLicenseForm()\"";
   } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="licensekey.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin)
     {
       csimsoft.DatabaseLicenses licenses = new csimsoft.DatabaseLicenses();
       csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers(licenses.database);
       try
       {
         int licenseId = Integer.parseInt(request.getParameter("license"));
         licenses.database.connect();
         String licenseKey = licenses.getUserLicenseKey(licenseId);
         if(licenseKey == null)
            licenseKey = ""; %>

    <div class="content">
        <h2>Edit License Key</h2>
      <table class="inner" cellpadding="0" cellspacing="0">
      <tr><td>
      <form action="newuserlicensekey" method="POST" name="licensekeyform">
        <input type="hidden" name="license" value="<%= licenseId %>" />
        <table class="form-table2"border="0" cellpadding="4" cellspacing="0">
          <tr>
            <td>Key</td>
            <td><input class="form-input" type="text" name="licensekey" value="<%= licenseKey %>" /></td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <input type="submit" value="Set Key" />
            </td>
          </tr>
        </table>
      </form>
      </td></tr>
      </table>
    </div>

<%     }
       catch(NumberFormatException nfe)
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The user or license id is not valid.</td>
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
       licenses.database.cleanup();
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
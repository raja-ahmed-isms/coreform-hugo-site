<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - License Information</title>

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
function validateLicense()
{
  if(isInputEmpty(document.licenseform.licensename))
  {
    alert("The license name is empty.\n" +
          "Please enter a name before submitting.")
    document.licenseform.licensename.focus()
    return false
  }

  if(isInputEmpty(document.licenseform.version))
  {
    alert("The version keyword is empty.\n" +
          "Please enter a version before submitting.")
    document.licenseform.version.focus()
    return false
  }

  return true
}

function focusLicenseForm()
{
  try
  {
    document.licenseform.licensename.focus()
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
        <jsp:param name="current" value="licenseinfo.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin)
     {
       csimsoft.DatabaseLicenses licenses = new csimsoft.DatabaseLicenses();
       try
       {
         int licenseId = Integer.parseInt(request.getParameter("license"));
         licenses.database.connect();
         licenses.getLicenseInfo(licenseId);
         if(licenses.database.results.next())
         {
           String licenseName = licenses.database.results.getString("licensename");
           String version = licenses.database.results.getString("version");
           int processes = licenses.database.results.getInt("allowedinst"); %>

    <div class="content">
      <table class="inner" cellpadding="0" cellspacing="0">
      <tr>
        <td>
        <form action="modifylicense" method="POST" name="licenseform"
          accept-charset="UTF-8" onsubmit="return validateLicense()">
          <input type="hidden" name="license" value="<%= licenseId %>" />
        <table class="form-table2" border="0" cellpadding="4" cellspacing="0">
          <tr>
            <td>License Name</td>
            <td><input class="form-input" type="text" name="licensename" value="<%= licenseName %>" /></td>
          </tr>
          <tr>
            <td>Version Keyword</td>
            <td><input class="form-input" type="text" name="version" value="<%= version %>" /></td>
          </tr>
          <tr>
            <td>Default Processes</td>
            <td>
              <select name="pcount">
<%           for(int i = 0; i < csimsoft.DatabaseLicenses.ProcessTexts.length; ++i)
             {
               String iSelected = i == processes ? "selected=\"selected\"" : ""; %>
                <option value="<%= i %>" <%= iSelected %>>
                  <%= csimsoft.DatabaseLicenses.ProcessTexts[i] %></option>
<%           } %>
              </select>
            </td>
          </tr>
          <tr>
            <td colspan="2" align="right"><input type="submit" value="Submit Changes" /></td>
          </tr>
        </table>
        </form>
        </td>
      </tr>
      </table>
    </div>

<%       }
         else
         { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The license id is not valid.</td>
      </tr>
      </table>
    </div>
<%       }
         licenses.database.results.close();
       }
       catch(NumberFormatException nfe)
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The license id is not valid.</td>
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

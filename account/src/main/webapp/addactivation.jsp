<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Add Activation</title>

    <link rel="stylesheet" type="text/css" media="screen" href="./css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="./css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="./css/menustyle.css" />

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
   else
   { %>
<script type="text/javascript">
function validateNewActivation()
{
  if(isInputEmpty(document.newactivationform.hostid))
  {
    alert("The host Id is empty.\n" +
          "Please enter an Id before submitting.")
    document.newactivationform.hostid.focus()
    return false
  }

  return true
}

function focusActivateForm()
{
  try
  {
    document.newactivationform.hostname.focus()
  }
  catch(err)
  {
  }
}
</script>
<%   initFocus = "onload=\"focusActivateForm()\"";
   } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />

  <div class="main">



    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="addactivation.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers();
     csimsoft.DatabaseLicenses licenses =
       new csimsoft.DatabaseLicenses(users.database);
     try
     {
       int licenseId = Integer.parseInt(request.getParameter("license"));
       licenses.database.connect();
       int userId = licenses.getUserLicenseOwner(licenseId);
       csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
       String licenseKey = licenses.getUserLicenseKey(licenseId);
       if(users.getUserProfile(userId, info) && licenseKey != null &&
         !licenseKey.isEmpty())
       {
         if(userId == user.getUserId() || isAdmin || (isDistributor &&
           info.getGroupId() == users.getDistibutorGroup(user.getUserId())))
         { %>
    <div class="content">
        <h3>Product Activation</h3>
        <p><strong>To activate your license:</strong></p>
        <ul>
            <li>Select your operating system on the <a href="./licenses">My Licenses</a> page</li>
            <li>Download Coreform Cubit</li>
            <li>Install Coreform Cubit</li>
            <li>Enter your product key in the Product Activation windows</li>
        </ul>
        <p></p>
        <p><strong>If you do not have an Internet connection during installation:</strong></p>
        <ul>
            <li>Navigate to the Coreform Cubit \bin folder</li>
            <li>Run "rlmutil rlmhostid" from a command line</li>
            <li>Use the hostid in the field below</li>
            <li>Download the license by clicking on the <img src="./images/icon-download.png" width="20" height="20"> icon</li>
            <li>Save the .lic file to your Coreform Cubit \bin\licenses folder</li>
            <li>Launch Coreform Cubit</li>
        </ul>
        <p></p>

      <table class="inner" cellpadding="0" cellspacing="0">
      <tr>
        <td>
        <form action="<%= request.getContextPath() %>/activatelicense" method="POST"
          name="newactivationform" accept-charset="UTF-8"
          onsubmit="return validateNewActivation()">
          <input type="hidden" name="licensekey" value="<%= licenseKey %>" />
          <input type="hidden" name="xml" value="<%= request.getParameter("xml") %>" />
        <table class="form-table2" border="0" cellpadding="4" cellspacing="0">
          <tr>
          <td>Host Name</td>
          <td><input class="form-input" type="text" name="hostname" /></td>
          </tr>
          <tr>
          <td>Host Id</td>
          <td><input class="form-input" type="text" name="hostid" /></td>
          </tr>
          <tr>
          <td colspan="2" align="right"><input type="submit" value="Add Activation" /></td>
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
        <td class="error-msg">You are not authorized to activate this license.</td>
      </tr>
      </table>
    </div>
<%       }
       }
       else
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Invalid license Id.</td>
      </tr>
      </table>
    </div>
<%     }
     }
     catch(java.sql.SQLException sqle)
     { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Unable to connect to the database.<br/><%= sqle %></td>
      </tr>
      </table>
    </div>
<%   }
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
<%   }
     catch(Exception e)
     { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Invalid license Id.</td>
      </tr>
      </table>
    </div>
<%   }
     users.database.cleanup();
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
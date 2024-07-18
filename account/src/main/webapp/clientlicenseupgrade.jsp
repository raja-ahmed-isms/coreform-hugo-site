<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - License Upgrade Path</title>

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
function focusUpgradeForm()
{
  try
  {
    document.upgradeform.expiration.focus()
  }
  catch(err)
  {
  }
}
</script>
<%   initFocus = "onload=\"focusUpgradeForm()\"";
   } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="clientlicenseupgrade.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin || isDistributor)
     {
       csimsoft.DatabaseLicenses licenses = new csimsoft.DatabaseLicenses();
       csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers(licenses.database);
       try
       {
         int licenseId = Integer.parseInt(request.getParameter("license"));
         licenses.database.connect();
         int userId = licenses.getUserLicenseOwner(licenseId);
         if(userId > 0)
         {
           if(isAdmin || users.isDistributor(userId, user.getUserId()))
           {
             String expirationText = licenses.getUserUpgradeDate(licenseId);
             String useExpire = "checked=\"checked\"";
             java.util.Calendar today = java.util.Calendar.getInstance();
             if(expirationText == null || expirationText.isEmpty())
             {
               today.add(java.util.Calendar.YEAR, 1);
               useExpire = "";
             }
             else
             {
               java.text.DateFormat converter =
                 new java.text.SimpleDateFormat("yyyy-MM-dd");
               try
               {
                 today.setTime(converter.parse(expirationText));
               }
               catch(Exception e)
               {
               }
             } %>

    <div class="content">
        <h2>Edit Upgrade Path</h2>
      <table class="form-table" cellpadding="0" cellspacing="0">
      <tr><td>
      <form action="newuserupgrade" method="POST" name="upgradeform">
        <input type="hidden" name="license" value="<%= licenseId %>" />
        <table border="0" cellpadding="4" cellspacing="0">
          <tr>
            <td colspan="2">
              <input type="checkbox" name="expiration" value="yes" <%= useExpire %> />
              Set Expiration Date
            </td>
          </tr>
          <tr>
            <td>Month</td>
            <td><input type="text" name="month" value="<%= today.get(java.util.Calendar.MONTH) + 1 %>" /></td>
          </tr>
          <tr>
            <td>Year</td>
            <td><input type="text" name="year" value="<%= today.get(java.util.Calendar.YEAR) %>" /></td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <input type="submit" value="Submit Changes" />
            </td>
          </tr>
        </table>
      </form>
      </td></tr>
      </table>
    </div>

<%         }
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
<%         }
         }
         else
         { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The license id is not valid.</td>
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
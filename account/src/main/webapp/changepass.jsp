<%@page contentType="text/html; charset=UTF-8" %>
<% java.util.Locale locale = request.getLocale();
   boolean isJapanese = locale.getLanguage().equals(
     java.util.Locale.JAPANESE.getLanguage()) ||
     locale.getCountry().equals(java.util.Locale.JAPAN.getCountry()); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Change Password</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="formhelp.js"></script>
<% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   String initFocus = "";
   if(isLoggedIn)
   { %>
<script type="text/javascript">
function validateNewPass()
{
  if(isInputEmpty(document.newpassform.pass))
  {
    alert("The password field is empty.\n" +
          "Please choose a password and enter it.")
    document.newpassform.pass.focus()
    return false
  }

  if(isInputEmpty(document.newpassform.confirm))
  {
    alert("The password confirmation is empty.\n" +
          "Please re-enter your password in the confirmation field.")
    document.newpassform.confirm.focus()
    return false
  }

  if(document.newpassform.pass.value != document.newpassform.confirm.value)
  {
    alert("The entered passwords do not match.")
    document.newpassform.confirm.value = ""
    document.newpassform.pass.focus()
    document.newpassform.pass.select()
    return false
  }

  return true
}

function focusPassForm()
{
  try
  {
    document.newpassform.pass.focus()
  }
  catch(err)
  {
  }
}
</script>
<%   initFocus = "onload=\"focusPassForm()\"";
   }
   else
     initFocus = "onload=\"focusLoginForm()\""; %>

</head>

<body <%= initFocus %>>

  <div class="main">

  <jsp:include page="header.jsp" flush="true" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="changepass.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     try
     {
       csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
       int userId = user.getUserId();
       boolean isAdmin = user.isAdministrator();
       boolean isDistributor = user.isDistributor();
       boolean isReseller = user.isReseller();
       String value = request.getParameter("user");
       if((isAdmin || isDistributor || isReseller) && value != null)
         userId = Integer.parseInt(value); %>

    <div class="content">
        <h2>Change Password</h2>
      <table class="inner" cellpadding="0" cellspacing="0">
      <tr><td><form action="newpass" method="POST" name="newpassform"
        accept-charset="UTF-8" onsubmit="return validateNewPass()">
        <input type="hidden" name="user" value="<%= userId %>" />
        <table class="form-table2" border="0" cellpadding="4" cellspacing="0">
        <tr>
        <td>Password</td>
        <td><input class="form-input" type="password" name="pass" /></td>
        </tr>
        <tr>
        <td>Confirm Password</td>
        <td><input class="form-input" type="password" name="confirm" /></td>
        </tr>
        <tr>
        <td colspan="2" align="right"><input type="submit" value="Submit Changes" /></td>
        </tr>
      </table>
      </form></td></tr>
      </table>
    </div>
<%   }
     catch(NumberFormatException nfe)
     { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Bad user ID.</td>
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

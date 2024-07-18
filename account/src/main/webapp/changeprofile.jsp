<%@page contentType="text/html; charset=UTF-8" %>
<% java.util.Locale locale = request.getLocale();
   boolean isJapanese = locale.getLanguage().equals(
     java.util.Locale.JAPANESE.getLanguage()) ||
     locale.getCountry().equals(java.util.Locale.JAPAN.getCountry()); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Change Profile</title>

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
function validateNewProfile()
{
  if(stripAndValidateInput(document.newinfoform.first))
  {
    alert("The first name field is empty.\n" +
          "Please enter your first name.")
    document.newinfoform.first.focus()
    return false
  }

  if(stripAndValidateInput(document.newinfoform.last))
  {
    alert("The last name field is empty.\n" +
          "Please enter your last name.")
    document.newinfoform.last.focus()
    return false
  }

  if(stripAndValidateInput(document.newinfoform.email))
  {
    alert("The email address field is empty.\n" +
          "Please enter a valid email address.")
    document.newinfoform.email.focus()
    return false
  }

  return true
}

function focusProfileForm()
{
  try
  {
    document.newinfoform.first.focus()
  }
  catch(err)
  {
  }
}
</script>
<%   initFocus = "onload=\"focusProfileForm()\"";
   }
   else
     initFocus = "onload=\"focusLoginForm()\""; %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

  <jsp:include page="header.jsp" flush="true" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="changeprofile.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers();
     try
     {
       csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
       int userId = user.getUserId();
       boolean isAdmin = user.isAdministrator();
       boolean isDistributor = user.isDistributor();
       boolean isReseller = user.isReseller();
       String value = request.getParameter("user");
       if((isAdmin || isDistributor || isReseller) && value != null)
         userId = Integer.parseInt(value);

       users.database.connect();
       csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
       if(users.getUserProfile(userId, info))
       {
         boolean isResellerUser = info.getGroupId() == users.getResellerGroup(user.getUserId());
         boolean isDistUser = info.getGroupId() == users.getDistibutorGroup(user.getUserId());
         if(userId != user.getUserId() && !(isDistributor && isDistUser) && !(isReseller && isResellerUser) && !isAdmin)
         { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">You are not authorized to edit the given user.</td>
      </tr>
      </table>
    </div>
<%       }
         else
         { %>

    <div class="content">
        <h2>Change Profile Information</h2>
      <table class="inner" cellpadding="0" cellspacing="0">
      <tr><td><form action="newprofile" method="POST" name="newinfoform"
        accept-charset="UTF-8" onsubmit="return validateNewProfile()">
        <input type="hidden" name="user" value="<%= userId %>" />
      <table class="form-table2" border="0" cellpadding="4" cellspacing="0">
        <tr>
        <td>First Name</td>
        <td><input class="form-input" type="text" name="first" value="<%= info.getFirstName() %>" /></td>
        </tr>
        <tr>
        <td>Last Name</td>
        <td><input class="form-input" type="text" name="last" value="<%= info.getLastName() %>" /></td>
        </tr>
        <tr>
        <td>Email Address</td>
        <td><input class="form-input" type="text" name="email" value="<%= info.getEmail() %>" /></td>
        </tr>
        <tr>
        <td colspan="2" align="right"><input type="submit" value="Submit Changes" /></td>
        </tr>
      </table>
      </form></td></tr>
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
        <td class="error-msg">No profile for the given user ID.</td>
      </tr>
      </table>
    </div>
<%     }
     }
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
     catch(Exception e)
     { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Unable to connect to the database.</td>
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

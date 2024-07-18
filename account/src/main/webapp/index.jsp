<%@page contentType="text/html; charset=UTF-8" %>
<% java.util.Locale locale = request.getLocale();
   boolean isJapanese = locale.getLanguage().equals(
     java.util.Locale.JAPANESE.getLanguage()) ||
     locale.getCountry().equals(java.util.Locale.JAPAN.getCountry()); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - My Account</title>

<link rel="stylesheet" type="text/css" media="screen" href="../css/style.css"/>
<link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />
<link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />

<link href='https://fonts.googleapis.com/css?family=Open+Sans:300,400,500,600,700' rel='stylesheet' type='text/css'>    

<script type="text/javascript" src="formhelp.js"></script>

<% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\""; %>

</head>

<body <%= initFocus %>>
  <jsp:include page="header.jsp" flush="true" />

  <div class="main">

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="index.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
     csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers();
     try
     {
       int userId = user.getUserId();
       users.database.connect();
       csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
       if(users.getUserProfileInfo(userId, info))
       { %>
<div class="content">
  <table border="0" cellpadding="6" cellspacing="0" width="60%">
    <tr>
      <td class="heading-cell" colspan="2">Profile</td>
    </tr>
    <tr>
      <td align="right"><strong>Name</strong></td>
      <td><%= info.getFullName() %></td>
    </tr>
    <tr>
      <td align="right"><strong>Email</strong></td>
      <td><%= info.getEmail() %></td>
    </tr>
<%       String rolesText = user.getRolesString();
         if(rolesText != null)
         { %>
    <tr>
      <td align="right"><strong>Roles</strong></td>
      <td><%= rolesText %></td>
    </tr>
<%       } %>
    <tr>
      <td align="right" colspan="2"><table>
        <tr><td><form action="changeprofile.jsp" method="GET" name="editprofileform">
          <input type="hidden" name="user" value="<%= userId %>" />
          <input type="submit" value="Edit Profile" /></form></td>
        <td><form action="changepass.jsp" method="GET" name="editpassform">
          <input type="hidden" name="user" value="<%= userId %>" />
          <input type="submit" value="Change Password" /></form></td></tr>
      </table></td>
    </tr>
    <tr>
      <td class="heading-cell" colspan="2">Contact Information</td>
    </tr>
    <tr>
      <td align="right"><strong>Company</strong></td>
      <td><%= info.getCompany() %></td>
    </tr>
    <tr>
      <td align="right"><strong>Phone</strong></td>
      <td><%= info.getPhone() %></td>
    </tr>
    <tr>
      <td align="right" valign="top"><strong>Address</strong></td>
      <td><%= info.getHtmlAddress() %></td>
    </tr>
    <tr>
      <td align="right"><strong>Country</strong></td>
      <td><%= info.getCountry() %></td>
    </tr>
    <tr>
      <td align="right" colspan="2">
        <form action="changeinfo.jsp" method="GET" name="editinfoform">
          <input type="submit" value="Edit Information" />
        </form>
      </td>
    </tr>
  </table>
</div>

<%     }
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
<%     }
     }
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
<%   }
     users.database.cleanup();
   }
   else
   { %>
    <div class="content">
      <jsp:include page="loginform.jsp" flush="true" >
        <jsp:param name="page" value="<%= request.getServletPath() %>" />
      </jsp:include>
    </div>
<% } %>

  </div>

  <div class="push"></div>

  <jsp:include page="footer.jsp" flush="true" />
</div> <!--main-wrapper-->

</body>

</html>
<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Client Information</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="formhelp.js"></script>
<% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\""; %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="clientinfo.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
     boolean isAdmin = user.isAdministrator();
     boolean isReseller = user.isReseller();
     if(isAdmin || isReseller)
     {
       csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers();
       try
       {
         int userId = Integer.parseInt(request.getParameter("user"));
         users.database.connect();
         csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
         if(users.getUserProfileInfo(userId, info))
         {
           if(isAdmin || (isReseller &&
             info.getGroupId() == users.getResellerGroup(user.getUserId())))
           { %>

    <div class="content">
      <table border="0" celpadding="6" celspacing="0" width="100%">
      <tr>
        <td class="heading-cell" colspan="2" width="50%">Profile</td>
        <td class="heading-cell" width="50%">Privileges</td>
      </tr>
      <tr>
        <td align="right"><strong>Name</strong></td>
        <td><%= info.getFullName() %></td>
        <td rowspan="9" valign="top">
          <table border="0" celpadding="4" celspacing="0" width="100%">
            <tr><td><a href="clientlicenses.jsp?user=<%= userId %>">Licenses</a></td></tr>
          </table>
        </td>
      </tr>
      <tr>
        <td align="right"><strong>Email</strong></td>
        <td><%= info.getEmail() %></td>
      </tr>
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
            <input type="hidden" name="user" value="<%= userId %>" />
            <input type="submit" value="Edit Info" />
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
      <table class="error-msg" celpadding="8" celspacing="0">
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
      <table class="error-msg" celpadding="8" celspacing="0">
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
      <table class="error-msg" celpadding="8" celspacing="0">
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




</div>

</body>
</html>

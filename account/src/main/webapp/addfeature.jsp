<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Add Feature</title>

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
function validateNewFeature()
{
  if(isInputEmpty(document.newfeatureform.featurename))
  {
    alert("The feature name is empty.\n" +
          "Please enter a name before submitting.")
    document.newfeatureform.featurename.focus()
    return false
  }

  if(isInputEmpty(document.newfeatureform.featurekey))
  {
    alert("The feature key is empty.\n" +
          "Please enter a key before submitting.")
    document.newfeatureform.featurekey.focus()
    return false
  }

  return true
}
</script>
<%   initFocus = "onload=\"document.newfeatureform.featurename.focus()\"";
   } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="addfeature.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin)
     { %>

    <div class="content">
      <table class="inner" cellpadding="0" cellspacing="0">
          <tr>
              <td class="title-cell">Add a Feature</td>
          </tr>
      <tr>
        <td>
        <form action="newfeature" method="POST" name="newfeatureform"
          accept-charset="UTF-8" onsubmit="return validateNewFeature()">
        <table class="form-table2" border="0" cellpadding="4" cellspacing="0">
          <tr>
          <td>Feature Name</td>
          <td><input class="form-input" type="text" name="featurename" /></td>
          </tr>
          <tr>
          <td>Feature Key</td>
          <td><input class="form-input" type="text" name="featurekey" /></td>
          </tr>
          <tr>
          <td colspan="2" align="right"><input type="submit" value="Add Feature" /></td>
          </tr>
        </table>
        </form>
        </td>
      </tr>
      </table>
    </div>
<%   }
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
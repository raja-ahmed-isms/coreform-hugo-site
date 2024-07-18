<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Change Downloads Directory</title>

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
function validateNewPath()
{
  if(isInputEmpty(document.newpathform.downloads))
  {
    alert("The downloads directory path is empty.\n" +
          "Please enter an absolute path before submitting.")
    document.newpathform.downloads.focus()
    return false
  }

  return true
}
</script>
<%   initFocus = "onload=\"document.newpathform.downloads.focus()\"";
   } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="alldownloads.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />
<% if(isLoggedIn)
   {
     if(isAdmin)
     {
       String directory = csimsoft.DownloadServlet.getDirectory(session, request);
       if(directory == null)
         directory = ""; %>

    <div class="content">
      <table class="form-table" cellpadding="0" cellspacing="0">
      <tr>
        <td>
        <form action="changedownloadspath" method="POST" name="newpathform"
          onsubmit="return validateNewPath()">
        <table border="0" cellpadding="4" cellspacing="0">
          <tr>
            <td>Downloads Directory</td>
            <td><input type="text" name="downloads" value="<%= directory %>" /></td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <input type="submit" value="Submit Changes" />
            </td>
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
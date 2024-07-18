<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Option Information</title>

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
function validateOption()
{
  if(isInputEmpty(document.optionform.optionname))
  {
    alert("The option name is empty.\n" +
          "Please enter a name before submitting.")
    document.optionform.optionname.focus()
    return false
  }

  if(isInputEmpty(document.optionform.optionkey))
  {
    alert("The option key is empty.\n" +
          "Please enter a key before submitting.")
    document.optionform.optionkey.focus()
    return false
  }

  return true
}

function focusOptionForm()
{
  try
  {
    document.optionform.optionname.focus()
  }
  catch(err)
  {
  }
}
</script>
<%   initFocus = "onload=\"focusOptionForm()\"";
   } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="optioninfo.jsp" />
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
         int optionId = Integer.parseInt(request.getParameter("option"));
         licenses.database.connect();
         licenses.getOptionInfo(optionId);
         if(licenses.database.results.next())
         {
           String optionName = licenses.database.results.getString("optionname");
           String optionKey = licenses.database.results.getString("optionkey"); %>

    <div class="content">
      <table class="inner" cellpadding="0" cellspacing="0">
      <tr>
        <td>
        <form action="modifyoption" method="POST" name="optionform"
          accept-charset="UTF-8" onsubmit="return validateOption()">
          <input type="hidden" name="option" value="<%= optionId %>" />
        <table class="form-table2" border="0" cellpadding="4" cellspacing="0">
          <tr>
          <td>Option Name</td>
          <td><input class="form-input" type="text" name="optionname" value="<%= optionName %>" /></td>
          </tr>
          <tr>
          <td>Option Key</td>
          <td><input class="form-input" type="text" name="optionkey" value="<%= optionKey %>" /></td>
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
        <td class="error-msg">Error: The option id is not valid.</td>
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
        <td class="error-msg">Error: The option id is not valid.</td>
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
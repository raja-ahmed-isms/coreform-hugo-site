<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Feature Information</title>

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
function validateFeature()
{
  if(isInputEmpty(document.featureform.featurename))
  {
    alert("The feature name is empty.\n" +
          "Please enter a name before submitting.")
    document.featureform.featurename.focus()
    return false
  }

  if(isInputEmpty(document.featureform.featurekey))
  {
    alert("The feature key is empty.\n" +
          "Please enter a key before submitting.")
    document.featureform.featurekey.focus()
    return false
  }

  return true
}

function focusFeatureForm()
{
  try
  {
    document.featureform.featurename.focus()
  }
  catch(err)
  {
  }
}
</script>
<%   initFocus = "onload=\"focusFeatureForm()\"";
   } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="featureinfo.jsp" />
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
         int featureId = Integer.parseInt(request.getParameter("feature"));
         licenses.database.connect();
         licenses.getFeatureInfo(featureId);
         if(licenses.database.results.next())
         {
           String featureName = licenses.database.results.getString("featurename");
           String featureKey = licenses.database.results.getString("featurekey"); %>

    <div class="content">
      <table class="inner" cellpadding="0" cellspacing="0">
      <tr>
        <td>
        <form action="modifyfeature" method="POST" name="featureform"
          accept-charset="UTF-8" onsubmit="return validateFeature()">
          <input type="hidden" name="feature" value="<%= featureId %>" />
        <table class="form-table2" border="0" cellpadding="4" cellspacing="0">
          <tr>
          <td>Feature Name</td>
          <td><input class="form-input" type="text" name="featurename" value="<%= featureName %>" /></td>
          </tr>
          <tr>
          <td>Feature Key</td>
          <td><input class="form-input" type="text" name="featurekey" value="<%= featureKey %>" /></td>
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
        <td class="error-msg">Error: The feature id is not valid.</td>
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
        <td class="error-msg">Error: The feature id is not valid.</td>
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
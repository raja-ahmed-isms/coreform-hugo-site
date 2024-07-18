<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Select License</title>

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
   boolean isReseller = user.isReseller();
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\"";
   else if(isAdmin || isDistributor || isReseller)
   { %>
<script type="text/javascript">
function sendSelectLicenseForm(licenseid)
{
  document.selectlicenseform.license.value = licenseid
  document.selectlicenseform.submit()
}
</script>
<% } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="selectlicense.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin || isDistributor || isReseller)
     {
       csimsoft.DatabaseLicenses licenses = new csimsoft.DatabaseLicenses();
       try
       {
         csimsoft.ParameterList params = new csimsoft.ParameterList();
         params.setParameters(request);
         params.removeParameter("next");
         licenses.database.connect();
         if(isAdmin)
           licenses.getProductLicenses();
         else
           licenses.getProductLicenses(user.getUserId()); %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr><td class="title-cell" colspan="2">Select a License</td></tr>
<%       int i = 0;
         for( ; licenses.database.results.next(); ++i)
         {
           int licenseId = licenses.database.results.getInt("licenseid"); %>
      <tr>
        <td class="list-icon">
          <img src="./images/icon-license.png" width="20" height="20" />
        </td>
        <td class="list-row">
          <a href="javascript:sendSelectLicenseForm('<%= licenseId %>')">
          <%= licenses.database.results.getString("licensename") %></a>
        </td>
      </tr>
<%       }
         licenses.database.results.close();
         if(i == 0)
         { %>
      <tr>
        <td class="list-row" colspan="2">
        There are no licenses in the database.
        </td>
      </tr>
<%       }
         String nextPage = request.getParameter("next");
         if(nextPage == null || nextPage.isEmpty())
         {
           if(isAdmin)
             nextPage = "allproducts.jsp";
           else
             nextPage = "distlicense.jsp";
         }
         String httpMethod = nextPage.endsWith(".jsp") ? "GET" : "POST"; %>
      <tr>
        <td colspan="2">
        <form action="<%= nextPage %>" method="<%= httpMethod %>"
          name="selectlicenseform">
          <input type="hidden" name="license" value="" />
<%       for(i = 0; i < params.getParameterCount(); ++i)
         {
           String paramName = params.getParameterName(i);
           String paramValue = request.getParameter(paramName);
           if(paramValue == null)
             paramValue = ""; %>
          <input type="hidden" name="<%= paramName %>" value="<%= paramValue %>" />
<%       } %>
        </form>
        </td>
      </tr>
      </table>
    </div>

<%     }
       catch(java.sql.SQLException sqle)
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">
          Error: Unable to connect to the database.<br />
          <%= sqle %>
        </td>
      </tr>
      </table>
    </div>
<%     }
       catch(javax.naming.NamingException ne)
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">
          Error: Unable to locate the database resource.<br />
          <%= ne %>
        </td>
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

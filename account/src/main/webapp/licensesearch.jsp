<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Verify Payment</title>

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
        <jsp:param name="current" value="licensesearch.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
     boolean isAdmin = user.isAdministrator();
     boolean isDistributor = user.isDistributor();
     if(isAdmin || isDistributor)
     {
       int licenseId = 0;
       try
       {
          licenseId = Integer.parseInt(request.getParameter("license"));
       }
       catch(Exception e)
       {
       }
       if(licenseId > 0)
       {
         csimsoft.DatabaseLicenses licenses = new csimsoft.DatabaseLicenses();
         try
         {
           licenses.database.connect();
           String licenseName = licenses.getLicenseName(licenseId);

           if(licenseName == null || licenseName.isEmpty())
              licenseName = "Unknown";

           csimsoft.ParameterList params = new csimsoft.ParameterList();
           params.setParameters(request);
           params.removeParameter("next"); %>

    <div class="content">
      <form action="licensesearch" method="POST" name="expirationform">
<%         for(int i = 0; i < params.getParameterCount(); i++)
           {
             String paramName = params.getParameterName(i);
             String paramValue = request.getParameter(paramName);
             if(paramValue == null)
               paramValue = ""; %>
        <input type="hidden" name="<%= paramName %>" value="<%= paramValue %>" />
<%         } %>
      <table border="0" celpadding="6" celspacing="0" width="100%">
      <tr>
        <td class="title-cell" colspan="2">License Name</td>
      </tr>
      <tr>
        <td colspan="2"><%= licenseName %></td>
      </tr>
      <tr>
        <td class="title-cell" colspan="2">Expiration</td>
      </tr>
      <tr>
        <td colspan="2">
           <input type="radio" name="exptype" value="<%= csimsoft.LicenseSearchServlet.NoExpiration %>">
             Don't Search For an Expiration</input>
        </td>
      </tr>
      <tr>
        <td colspan="2">
          <input type="radio" name="exptype" value="<%= csimsoft.LicenseSearchServlet.NullExpiration %>">
            Search For Licenses Without an Expiration</input>
        </td>
      </tr>
      <tr>
        <td colspan="2">
           <input type="radio" name="exptype" value="<%= csimsoft.LicenseSearchServlet.CloseExpiration %>" checked="checked">
             Search For Licenses About to Expire</input>
        </td>
      </tr>
      <tr>
        <td>Months Until Expiration</td>
        <td>
          <input type="text" name="months" value="3" />
        </td>
      </tr>
      <tr>
        <td colspan="2">
           <input type="radio" name="exptype" value="<%= csimsoft.LicenseSearchServlet.AlreadyExpired %>">
             Search For Licenses Already Expired</input>
        </td>
      </tr>
      <tr>
        <td class="list-row" colspan="2" align="right">
          <input type="submit" value="Search" />
        </td>
      </tr>
      </table>
      </form>
    </div>

<%       }
         catch(java.sql.SQLException sqle)
         { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Unable to connect to the database.</td>
      </tr>
      </table>
    </div>
<%       }
         catch(javax.naming.NamingException ne)
         { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Unable to locate the database resource.</td>
      </tr>
      </table>
    </div>
<%       }
         licenses.database.cleanup();
       }
       else
       { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Invalid license id.</td>
      </tr>
      </table>
    </div>
<%     }
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

  <jsp:include page="footer.jsp" flush="true" />


</div>

</body>

</html>

<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>

<head>

  <title>Coreform - License Details</title>

  <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
  <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
  <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

  <!-- Web Fonts -->
  <link
    href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext'
    rel='stylesheet' type='text/css'>

  <script type="text/javascript" src="formhelp.js"></script>
  <% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\"";
    %>
</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />

  <div class="main">

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true">
        <jsp:param name="current" value="licensedetail" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

    <% if(isLoggedIn)
   {
     String contextPath = request.getContextPath();
     csimsoft.DatabaseLicenses licenses = new csimsoft.DatabaseLicenses();
     csimsoft.DatabaseOrders orders = new csimsoft.DatabaseOrders(licenses.database);
     try
     {
       int licenseId = Integer.parseInt(request.getParameter("license"));
       licenses.database.connect();
       if(licenses.isUserLicenseOwner(licenseId, user.getUserId()))
       {
         csimsoft.ClientLicenseInfo license = new csimsoft.ClientLicenseInfo();
         if(license.setLicenseInfo(licenses.database, licenseId))
         { %>
    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
        <tr>
          <td class="heading-cell" colspan="3">License</td>
        </tr>
        <tr>
          <td align="left"><strong>Name:</strong></td>
          <td width="160"><%= license.getLicenseName() %></td>
        </tr>
        <tr>
          <td align="left"><strong>Expiration:</strong></td>
          <td><%= license.getExpiration() %></td>
        </tr>
        <tr><td colspan="3">Download the newest release of Coreform Cubit from <a style="color: #0000ff" href="https://coreform.com/products/downloads">coreform.com/products/downloads</a>.</td></tr>
        <tr><td colspan="3">After you download, install, and launch the latest Coreform Cubit, an activation window will appear.</td></tr>
        <tr><td colspan="3">Select the "Educational" option, then use the email and password from your Coreform account to log in.</td></tr>
        <tr><td colspan="3">Your Coreform Cubit Learn license will be active for one year.</td></tr>
        <tr><td colspan="3">When it expires, you can renew your Coreform Cubit Learn license for non-commercial use through your coreform.com user account, or acquire a commercial license of Coreform Cubit.</td></tr>
      </table>
    </div>
    <%  } else { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
        <tr>
          <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
          <td class="error-msg">Error: Invalid license Id.</td>
        </tr>
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
          <td class="error-msg">Error: The license Id is not one of your licenses.</td>
        </tr>
      </table>
    </div>
    <%     }
     }
     catch(java.sql.SQLException sqle)
     { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
        <tr>
          <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
          <td class="error-msg">Error: Unable to connect to the database.<br /><%= sqle %></td>
        </tr>
      </table>
    </div>
    <%   }
     catch(javax.naming.NamingException ne)
     { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
        <tr>
          <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
          <td class="error-msg">Error: Unable to locate the database resource.</td>
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
          <td class="error-msg">Error: Invalid license Id.</td>
        </tr>
      </table>
    </div>
    <%   }
     licenses.database.cleanup();
   }
   else
   {
     String query = request.getQueryString();
     if(query == null)
       query = "";
     else if(!query.isEmpty())
       query = "?" + query; %>
    <div class="content-login">
      <jsp:include page="loginform.jsp" flush="true">
        <jsp:param name="page" value="<%= request.getServletPath() + query %>" />
      </jsp:include>
    </div>
    <% } %>

  </div>

  <div class="push"></div>

  <jsp:include page="footer.jsp" flush="true" />


  </div>

</body>

</html>
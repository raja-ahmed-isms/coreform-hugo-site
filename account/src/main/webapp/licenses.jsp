<%@page contentType="text/html; charset=UTF-8" %>
<% java.util.Locale locale = request.getLocale();
   boolean isJapanese = locale.getLanguage().equals(
     java.util.Locale.JAPANESE.getLanguage()) ||
     locale.getCountry().equals(java.util.Locale.JAPAN.getCountry()); %>
<!DOCTYPE html>
<html>

<head>

  <title>Coreform - Licenses</title>

  <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
  <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
  <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

  <!-- Web Fonts -->
  <link
    href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext'
    rel='stylesheet' type='text/css'>

  <script type="text/javascript" src="formhelp.js"></script>

  <% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\""; %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <jsp:include page="header.jsp" flush="true" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true">
        <jsp:param name="current" value="licenses" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

    <% if(isLoggedIn)
   {
     csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
     csimsoft.DatabaseLicenses manager = new csimsoft.DatabaseLicenses();
     try
     {
       manager.database.connect();
       csimsoft.ClientLicenses licenses = new csimsoft.ClientLicenses();
       licenses.setLicenses(manager, user.getUserId()); %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
        <tr>
          <td class="title-cell" colspan="5">Licenses</td>
        </tr>
        <tr>
          <td class="heading-cell">Name</td>
          <td class="heading-cell">Quantity</td>
          <td class="heading-cell">Type</td>
          <td class="heading-cell">Maintenance</td>
          <td class="heading-cell">Expiration</td>
        </tr>
        <%     if(licenses.isEmpty())
       { %>
        <tr>
          <td class="list-row" colspan="5">
            There are no licenses in the database.
          </td>
        </tr>
        <%     }
       else
       {
         for(int i = 0; i < licenses.getLicenseCount(); ++i)
         {
           csimsoft.ClientLicenses.License license = licenses.getLicense(i); %>
        <tr>
          <td class="list-row">
            <% if(license.Name.equals("Coreform Cubit Learn")) { %>
            <a href="licensecubitlearn.jsp?license=<%= license.LicenseId %>"><%= license.Name %></a>
            <% } else { %>
            <a href="licensedetail?license=<%= license.LicenseId %>"><%= license.Name %></a>
            <% } %>
          </td>
          <td class="list-row"><%= license.Quantity %></td>
          <td class="list-row"><%= license.getType() %></td>
          <td class="list-row"><%= license.Maintenance %></td>
          <td class="list-row"><%= license.Expiration %></td>
        </tr>
        <%       }
       } %>
      </table>
    </div>

    <%   }
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
    <%   }
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
    <%   }
     manager.database.cleanup();
   }
   else
   { %>
    <div class="content-login">
      <jsp:include page="loginform.jsp" flush="true">
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
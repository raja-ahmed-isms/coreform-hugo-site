<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - License Configuration</title>

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
        <jsp:param name="current" value="licenseconfig.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(csimsoft.LoginServlet.isUserLoggedIn(session))
   {
     csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
     if(user.isAdministrator())
     {
       csimsoft.DatabaseLicenses licenses = new csimsoft.DatabaseLicenses();
       csimsoft.DatabaseProducts products =
         new csimsoft.DatabaseProducts(licenses.database);
       try
       {
         int processId = Integer.parseInt(request.getParameter("process"));
         licenses.database.connect();
         int currentProductId = licenses.getProcessLicense(processId);
         String productName = products.getProductNamePath(currentProductId);
         if(productName == null)
           productName = ""; %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
        <tr>
          <td class="title-cell">
            License Agreement File
          </td>
        </tr>
        <tr>
          <td><strong>Current File:</strong> <%= productName %></td>
        </tr>
        <tr>
          <td class="list-row">
            To change the license agreement file used for this process, please
            push the "Change File" button below. Then, select the file to use.
          </td>
        </tr>
        <tr>
          <td colspan="2" align="right">
          <form action="selectproduct.jsp" method="GET" name="newlicenseform">
            <input type="hidden" name="next" value="changelicense" />
            <input type="hidden" name="seltype" value="files" />
            <input type="hidden" name="process" value="<%= processId %>" />
            <input type="submit" value="Change File" />
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
        <td class="error-msg">Error: Unable to connect to the database.<br /><%= sqle %></td>
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
        <td class="error-msg">Error: Unable to locate the database resource.<br /><%= ne %></td>
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
        <td class="error-msg">Error: The process id is not valid.<br /><%= e %></td>
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

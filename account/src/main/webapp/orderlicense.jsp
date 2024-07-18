<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Order License</title>

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
        <jsp:param name="current" value="orderlicense.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     String contextPath = request.getContextPath();
     csimsoft.DatabaseLicenses licenses = new csimsoft.DatabaseLicenses();
     csimsoft.DatabaseProducts products =
       new csimsoft.DatabaseProducts(licenses.database);
     try
     {
       int orderId = Integer.parseInt(request.getParameter("orderid"));
       licenses.database.connect();
       int productId = licenses.getOrderProcessLicense(orderId);
       String location = products.getProductLocation(productId);
       if(location != null && !location.isEmpty())
       { %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr><td class="title-cell">Download License</td></tr>
      <tr><td>
      You need to sign a license agreement to get the ordered product(s). Please press
      the "Download" button below to download the license. Next, print and sign the
      license. <strong>Then, fax the license to csimsoft at 801-717-2288</strong>. Alternately,
      you can scan the signed license agreement and email it to
      <a href="mailto:info@coreform.com">info@coreform.com</a>. An email will
      be sent to you when csimsoft has received the signed license.
      </td></tr>
      <tr><td class="list-row" align="right"><form
        action="<%= contextPath %>/download" method="GET" name="getlicenseform">
        <input type="hidden" name="file" value="<%= location %>" />
        <input type="submit" value="Download" />
      </form></td></tr>
      </table>
    </div>

<%     }
       else
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">
        Error: The license agreement to download is missing.<br />
        Please contact csimsoft to resolve the issue.
        </td>
      </tr>
      </table>
    </div>
<%     }
     }
     catch(NumberFormatException nfe)
     { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The requested order id is not valid.</td>
      </tr>
      </table>
    </div>
<%   }
     catch(java.sql.SQLException sqle)
     { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Unable to connect to the database.</td>
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
      <jsp:include page="loginform.jsp" flush="true" >
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

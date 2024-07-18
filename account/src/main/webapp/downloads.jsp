<%@page contentType="text/html; charset=UTF-8" %>
<% java.util.Locale locale = request.getLocale();
   boolean isJapanese = locale.getLanguage().equals(
     java.util.Locale.JAPANESE.getLanguage()) ||
     locale.getCountry().equals(java.util.Locale.JAPAN.getCountry()); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Downloads</title>

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

  <jsp:include page="header.jsp" flush="true" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="downloads" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     String contextPath = request.getContextPath();
     csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
     csimsoft.DatabaseProducts products = new csimsoft.DatabaseProducts();
     try
     {
       products.database.connect();
       csimsoft.ClientDownloads downloads = new csimsoft.ClientDownloads();
       downloads.setDownloads(products, user.getUserId()); %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
        <tr><td class="title-cell" colspan="3">Downloads</td></tr>
        <tr><td colspan="3">ATTENTION: Downloads are moving to <a style="color: #0000ff" href="https://coreform.com/products/downloads">https://coreform.com/products/downloads</a>.</td></tr>
        <tr><td colspan="3">Product Keys are available at <a style="color: #0000ff" href="licenses">My Licenses</a>.</td></tr>
        <tr><td colspan="3">After you download, install, and launch Coreform Cubit, an activation window will show. Enter your Product Key in the activation window and Coreform Cubit will start.</td></tr>
        <%     if(downloads.isEmpty())
        { %>
          <tr>
            <td class="list-row" colspan="2">
              There are no downloads available.
            </td>
          </tr>
  <%     }
        else
        { %>
          <tr>
            <td class="heading-cell">Product</td>
            <td class="heading-cell">Filename</td>
          </tr>
  <%       for(int i = 0; i < downloads.getDownloadCount(); i++)
          {
            csimsoft.ClientDownload download = downloads.getDownload(i); 
            %>
          <tr>
            <td class="list-row"><%= download.getName() %></td>
            <td class="list-row">
              <a href="<%= contextPath %>/download?file=<%= download.getLocation() %>"
                ><%= download.getShortLocation() %></a>
            </td>
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
     products.database.cleanup();
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

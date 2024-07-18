<%@page contentType="text/html; charset=UTF-8" %>
<% java.util.Locale locale = request.getLocale();
   boolean isJapanese = locale.getLanguage().equals(
     java.util.Locale.JAPANESE.getLanguage()) ||
     locale.getCountry().equals(java.util.Locale.JAPAN.getCountry()); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Thank You</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="formhelp.js"></script>
<% boolean loginSuccess = csimsoft.LoginServlet.isUserLoggedIn(session);
   String initFocus = "";
   if(loginSuccess)
   { %>
<% }
   else
     initFocus = "onload=\"focusLoginForm()\""; %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

  <jsp:include page="header.jsp" flush="true" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="orders" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(loginSuccess)
   {
%>
    <div class="content">
    <table border="0" cellpadding="6" cellspacing="0" width="100%">
        <tr>
            <td class="title-cell">Thank you for signing up for your free trial!</td>
        </tr>
        <tr>
            <td> Please download Coreform Cubit at <a href="https://coreform.com/products/downloads/">https://coreform.com/products/downloads/</a>.
            </td>
        </tr>
        <tr>
            <td>
              Your license key is available in <a href="licenses">My Licenses</a>.
            </td>
        </tr>
    </table>
    </div>
<% }
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

<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>

<head>

  <title>Coreform - Search by Key</title>

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
   boolean isAdmin = user.isAdministrator();
   boolean isDistributor = user.isDistributor();
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
      <jsp:include page="navigation.jsp" flush="true">
        <jsp:param name="current" value="selectproduct.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

    <% if(isLoggedIn && isAdmin)
   { %>
    <div class="content">
      <form action="userbykey" method="POST" name="searchform" accept-charset="UTF-8">
        <table class="inner" border="0" cellpadding="4" cellspacing="0">
          <tr>
            <td class="title-cell" colspan="2">Search for User by Product Key</td>
          </tr>
          <tr>
            <td><input type="text" placeholder="Product Key*" name="key" size="40"></td>
          </tr>
          <tr>
            <td><input type="submit" value="Find user"></td>
          </tr>
        </table>
      </form>
    </div>
    <% } else { %>

    <div class="content-login">
      <jsp:include page="loginform.jsp" flush="true">
        <jsp:param name="page" value="<%= request.getServletPath() %>" />
      </jsp:include>
    </div>
    <% } %>

  </div>

  <div class="push"></div>
</body>

</html>
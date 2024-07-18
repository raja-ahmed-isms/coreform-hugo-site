<%@page contentType="text/html; charset=UTF-8" %>
<% if(request.getCharacterEncoding() == null)
     request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Confirm Contact</title>

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
        <jsp:param name="current" value="confirmcontact.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers();
     try
     {
       int universityId = Integer.parseInt(request.getParameter("universityid"));
       users.database.connect();
       csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
       boolean isAdmin = user.isAdministrator();
       int orderId = 0;
       try
       {
         orderId = Integer.parseInt(request.getParameter("orderid"));
       }
       catch(Exception e)
       {
       }

       if(isAdmin || universityId == users.getUniversityId(user.getUserId()))
       {
         csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
         info.setEmail(request.getParameter("email"));
         info.setFirstName(request.getParameter("first"));
         info.setLastName(request.getParameter("last"));
         info.setPhone(request.getParameter("phone"));
         info.setAddress(request.getParameter("address"));
         info.setCountry(request.getParameter("country"));
         String contactText = request.getParameter("contact");
         if(contactText == null)
           contactText = ""; %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr><td class="title-cell">Confirm Contact</td></tr>
      <tr>
        <td>
        The given contact email address has already been used by another account.
        Would you like to add the account for <%= info.getEmail() %> as a company
        contact?
        </td>
      </tr>
      <tr>
        <td class="list-row" align="right">
        <form action="addaccount" method="POST" name="confirmform" accept-charset="UTF-8">
<%       if(orderId > 0)
         { %>
          <input type="hidden" name="orderid" value="<%= orderId %>" />
<%       }
         else
         { %>
          <input type="hidden" name="universityid" value="<%= universityId %>" />
<%       } %>
          <input type="hidden" name="email" value="<%= info.getEmail() %>" />
          <input type="hidden" name="contact" value="<%= contactText %>" />
          <input type="submit" value="Yes" />
          <input type="button" value="No" onclick="document.returnform.submit()" />
        </form>
<%       if(orderId > 0)
         { %>
        <form action="ordercontact.jsp" method="GET" name="returnform" accept-charset="UTF-8">
          <input type="hidden" name="orderid" value="<%= orderId %>" />
          <input type="hidden" name="first" value="<%= info.getFirstName() %>" />
          <input type="hidden" name="last" value="<%= info.getLastName() %>" />
          <input type="hidden" name="phone" value="<%= info.getPhone() %>" />
          <input type="hidden" name="address" value="<%= info.getAddress() %>" />
          <input type="hidden" name="country" value="<%= info.getCountry() %>" />
        </form>
<%       }
         else
         { %>
        <form action="universityinfo.jsp" method="GET" name="returnform">
<%         if(isAdmin)
           { %>
          <input type="hidden" name="universityid" value="<%= universityId %>" />
<%         } %>
        </form>
<%       } %>
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
        <td class="error-msg">Error: The requested company id is not valid.</td>
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
     users.database.cleanup();
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

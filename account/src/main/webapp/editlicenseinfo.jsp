<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - License Information</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="formhelp.js"></script>

<% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\"";
   else
   { %>
<script type="text/javascript">
function focusLicenseForm()
{
  try
  {
    document.licenseinfoform.quantity.focus()
  }
  catch(err)
  {
  }
}
</script>
<%   initFocus = "onload=\"focusLicenseForm()\"";
   } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="editlicenseinfo.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     csimsoft.DatabaseLicenses licenses = new csimsoft.DatabaseLicenses();
     csimsoft.DatabaseOrders orders = new csimsoft.DatabaseOrders(licenses.database);
     try
     {
       int licenseId = Integer.parseInt(request.getParameter("license"));
       licenses.database.connect();
       int userId = licenses.getUserLicenseOwner(licenseId);
       if(userId < 1)
       { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The license id is not valid.</td>
      </tr>
      </table>
    </div>
<%     }
       if(userId != user.getUserId())
       { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">This license does not belong to you.</td>
      </tr>
      </table>
    </div>
<%     }
       else
       {
         int orderId = orders.getLicenseOrderId(userId, licenseId);
         boolean active = orderId > 0;
         if(active)
            active = orders.isOrderActive(orderId);

         if(active)
         {
           int quantity = 0;
           String nodelockedSelected = "";
           String floatingSelected = "";
           licenses.getUserLicenseInfo(licenseId);
           if(licenses.database.results.next())
           {
             int licenseType = licenses.database.results.getInt("licensetype");
             quantity = licenses.database.results.getInt("quantity");
             if(licenseType == csimsoft.DatabaseLicenses.FloatingLicense)
                floatingSelected = "selected=\"selected\"";
             else if(licenseType == csimsoft.DatabaseLicenses.NodeLockedLicense)
                nodelockedSelected = "selected=\"selected\"";
           }
           licenses.database.results.close(); %>

    <div class="content">
        <h2>Edit License Information</h2>
      <table class="form-table" cellpadding="0" cellspacing="0">
      <tr><td>
      <form action="newuserlicenseinfo" method="POST" name="licenseinfoform">
        <input type="hidden" name="license" value="<%= licenseId %>" />
        <table border="0" cellpadding="4" cellspacing="0">
          <tr>
            <td>Type</td>
            <td>
              <select name="ltype">
                <option value="<%= csimsoft.DatabaseLicenses.NodeLockedLicense %>" <%= nodelockedSelected %>>
                  <%= csimsoft.DatabaseLicenses.NodeLockedLicenseName %></option>
                <option value="<%= csimsoft.DatabaseLicenses.FloatingLicense %>" <%= floatingSelected %>>
                  <%= csimsoft.DatabaseLicenses.FloatingLicenseName %></option>
              </select>
            </td>
          </tr>
          <tr>
            <td>Quantity</td>
            <td><input type="text" name="quantity" value="<%= quantity %>" /></td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <input type="submit" value="Submit Changes" />
            </td>
          </tr>
        </table>
      </form>
      </td></tr>
      </table>
    </div>

<%       }
         else
         { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Please contact csimsoft to change your license details.</td>
      </tr>
      </table>
    </div>
<%       }
       }
     }
     catch(NumberFormatException nfe)
     { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The license id is not valid.</td>
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
        <td class="error-msg">Error: Unable to connect to the database.<br /><%= e %></td>
      </tr>
      </table>
    </div>
<%   }
     licenses.database.cleanup();
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
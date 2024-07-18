<%@page contentType="text/html; charset=UTF-8" %>
<% if(request.getCharacterEncoding() == null)
     request.setCharacterEncoding("UTF-8"); %>
<% java.util.Locale locale = request.getLocale();
   boolean isJapanese = locale.getLanguage().equals(
     java.util.Locale.JAPANESE.getLanguage()) ||
     locale.getCountry().equals(java.util.Locale.JAPAN.getCountry()); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Change Information</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="formhelp.js"></script>
<% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   String initFocus = "";
   if(isLoggedIn)
   { %>
<script type="text/javascript">
function validateNewInfo()
{
  if(stripAndValidateInput(document.newinfoform.phone))
  {
    alert("The phone field is empty.\n" +
          "Please enter your phone number before submitting the form.")
    document.newinfoform.phone.focus()
    return false
  }

  if(stripAndValidateInput(document.newinfoform.country))
  {
    alert("The country field is empty.\n" +
          "Please enter the country before submitting the form.")
    document.newinfoform.country.focus()
    return false
  }

  if(stripAndValidateInput(document.newinfoform.address))
  {
    alert("The address field is empty.\n" +
          "Please enter the address before submitting the form.")
    document.newinfoform.address.focus()
    return false
  }

  return true
}

function focusContactForm()
{
  try
  {
    document.newinfoform.company.focus()
  }
  catch(err)
  {
  }
}
</script>
<%   initFocus = "onload=\"focusContactForm()\"";
   }
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
        <jsp:param name="current" value="changeinfo.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers();
     try
     {
       csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
       int userId = user.getUserId();
       boolean isAdmin = user.isAdministrator();
       boolean isDistributor = user.isDistributor();
       boolean isReseller = user.isReseller();
       String value = request.getParameter("user");
       if((isAdmin || isDistributor || isReseller) && value != null)
         userId = Integer.parseInt(value);

       users.database.connect();
       csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
       users.getUserInfo(userId, info);
       value = request.getParameter("company");
       if(value != null)
         info.setCompany(value);

       value = request.getParameter("phone");
       if(value != null)
         info.setPhone(value);

       value = request.getParameter("country");
       if(value != null)
         info.setCountry(value);

       value = request.getParameter("address");
       if(value != null)
         info.setAddress(value);

       boolean isResellerUser = info.getGroupId() == users.getResellerGroup(user.getUserId());
       boolean isDistUser = info.getGroupId() == users.getDistibutorGroup(user.getUserId());
       if(userId != user.getUserId() && !(isDistributor && isDistUser) && !(isReseller && isResellerUser) && !isAdmin)
       { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">You are not authorized to edit the given user.</td>
      </tr>
      </table>
    </div>
<%     }
       else
       { %>

    <div class="content">
        <h2>Change Contact Information</h2>
      <table class="inner" cellpadding="0" cellspacing="0">
        <tr>
          <td>
            <form action="newinfo" method="POST" name="newinfoform"
              accept-charset="UTF-8" onsubmit="return validateNewInfo()">
            <input type="hidden" name="user" value="<%= userId %>" />
            <table class="form-table2" border="0" cellpadding="4" cellspacing="0">
              <tr>
                <td>Company</td>
                <td><input class="form-input" type="text" name="company" value="<%= info.getCompany() %>" /><td>
              </tr>
              <tr>
                <td>Phone</td>
                <td><input class="form-input" type="text" name="phone" value="<%= info.getPhone() %>" /><td>
              </tr>
              <tr>
                <td>Country</td>
                <td><input class="form-input" type="text" name="country" value="<%= info.getCountry() %>" /><td>
              </tr>
              <tr>
                <td>Address</td>
                <td><textarea name="address" cols="25" rows="3"><%= info.getAddress() %></textarea></td>
              </tr>
              <tr>
                <td colspan="2" align="right"><input type="submit" value="Submit Changes" /></td>
              </tr>
            </table>
            </form>
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
        <td class="error-msg">Bad user ID.</td>
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
        <td class="error-msg">Unable to connect to the database.</td>
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

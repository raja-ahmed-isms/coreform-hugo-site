<%@page contentType="text/html; charset=UTF-8" %>
<% if(request.getCharacterEncoding() == null)
     request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Order Contact</title>

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
function validateAddAccount()
{
  if(stripAndValidateInput(document.newcontactform.email))
  {
    alert("The email field is empty.\n" +
          "Please enter the email address before submitting the form.")
    document.newcontactform.email.focus()
    return false
  }

  return true
}

function sendAddAccount()
{
  if(validateAddAccount())
  {
    var email = document.newcontactform.email.value
    if(confirm("Are you sure you want to add the " + email +
               "\n account as a contact for the company?"))
    {
      document.addaccountform.email.value = email
      document.addaccountform.submit()
    }
  }
}

function validateNewContact()
{
  if(!validateAddAccount())
  {
    return false;
  }

  if(stripAndValidateInput(document.newcontactform.first))
  {
    alert("The first name field is empty.\n" +
          "Please enter the first name before submitting the form.")
    document.newcontactform.first.focus()
    return false
  }

  if(stripAndValidateInput(document.newcontactform.last))
  {
    alert("The last name field is empty.\n" +
          "Please enter the last name before submitting the form.")
    document.newcontactform.last.focus()
    return false
  }

  if(stripAndValidateInput(document.newcontactform.country))
  {
    alert("The country field is empty.\n" +
          "Please enter the country before submitting the form.")
    document.newcontactform.country.focus()
    return false
  }

  return true
}

function focusContactForm()
{
  try
  {
    document.newcontactform.email.focus()
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

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="ordercontact.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers();
     csimsoft.DatabaseOrders orders = new csimsoft.DatabaseOrders(users.database);
     csimsoft.DatabaseUniversities universities =
        new csimsoft.DatabaseUniversities(users.database);
     try
     {
       int orderId = Integer.parseInt(request.getParameter("orderid"));
       users.database.connect();
       csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
       int userId = orders.getOrderUserId(orderId);
       if(user.isAdministrator() || userId == user.getUserId())
       {
         int universityId = users.getUniversityId(userId);
         int adminId = 0;
         universities.getContacts(universityId);
         if(users.database.results.next())
           adminId = users.database.results.getInt("adminid");

         users.database.results.close();
         csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
         info.setFirstName(request.getParameter("first"));
         info.setLastName(request.getParameter("last"));
         info.setEmail(request.getParameter("email"));
         info.setPhone(request.getParameter("phone"));
         info.setAddress(request.getParameter("address"));
         info.setCountry(request.getParameter("country")); %>

    <div class="content">
      <form action="addaccount" method="POST" name="addaccountform"
        accept-charset="UTF-8" onsubmit="return validateAddAccount()">
        <input type="hidden" name="orderid" value="<%= orderId %>" />
        <input type="hidden" name="contact" value="<%= adminId > 0 ? "tech" : "admin" %>" />
        <input type="hidden" name="email" value="" />
      </form>
      <form action="newcontact" method="POST" name="newcontactform"
        accept-charset="UTF-8" onsubmit="return validateNewContact()">
        <input type="hidden" name="orderid" value="<%= orderId %>" />
        <input type="hidden" name="contact" value="<%= adminId > 0 ? "tech" : "admin" %>" />
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr>
        <td class="title-cell" colspan="2">
        <%= adminId > 0 ? "Technical" : "Administrative" %> Contact Information
        </td>
      </tr>
      <tr>
        <td class="list-row" colspan="2">
        If the contact already has a csimsoft account, enter their email address
        below and click the "Add Account" button. If the contact does not have
        an account, enter all the information below and click the "Add Contact"
        button. The contact's email address must be unique to be added to the
        system.
        </td>
      </tr>
      <tr>
        <td align="right">Email</td>
        <td>
        <input type="text" name="email" value="<%= info.getEmail() %>" />
        <input type="button" value="Add Account" onclick="sendAddAccount()" />
        </td>
      </tr>
      <tr>
        <td align="right">First Name</td>
        <td><input type="text" name="first" value="<%= info.getFirstName() %>" /></td>
      </tr>
      <tr>
        <td align="right">Last Name</td>
        <td><input type="text" name="last" value="<%= info.getLastName() %>" /></td>
      </tr>
      <tr>
        <td align="right">Phone</td>
        <td><input type="text" name="phone" value="<%= info.getPhone() %>" /></td>
      </tr>
      <tr>
        <td align="right">Address</td>
        <td><textarea name="address" cols="25" rows="3"><%= info.getAddress() %></textarea></td>
      </tr>
      <tr>
        <td align="right">Country</td>
        <td><input type="text" name="country" value="<%= info.getCountry() %>" /></td>
      </tr>
      <tr>
        <td class="list-row" colspan="2" align="right">
        <input type="submit" value="Add Contact" />
        </td>
      </tr>
      </table>
      </form>
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
     users.database.cleanup();
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

<%@page contentType="text/html; charset=UTF-8" %>
<% if(request.getCharacterEncoding() == null)
     request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Change Contact</title>

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
function validateNewContact()
{
  if(stripAndValidateInput(document.newcontactform.email))
  {
    alert("The email address field is empty.\n" +
          "Please enter the email address before submitting the form.")
    document.newcontactform.email.focus()
    return false
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

<% String idString = request.getParameter("contactid");
   String currentPage = "changecontact";
   if(idString == null || idString.isEmpty())
     currentPage = "changecontact.jsp"; %>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="<%= currentPage %>" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers();
     try
     {
       csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
       boolean isAdmin = user.isAdministrator();
       int contactId = 0;
       if(idString != null && !idString.isEmpty())
         contactId = Integer.parseInt(idString);

       int universityId = 0;
       try
       {
         universityId = Integer.parseInt(request.getParameter("universityid"));
       }
       catch(Exception e)
       {
       }

       users.database.connect();
       boolean owns = isAdmin || contactId > 0;
       if(!isAdmin)
       {
         if(contactId > 0)
           owns = users.isContactAccessible(contactId, user.getUserId());
         else if(universityId > 0)
           owns = users.getUniversityId(user.getUserId()) == universityId;
       }

       if(owns)
       {
         csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
         boolean validId = true;
         if(contactId > 0)
           validId = users.getContactInfo(contactId, info);

         if(validId)
         {
           String value = request.getParameter("first");
           if(value != null && !value.isEmpty())
             info.setFirstName(value);
           value = request.getParameter("last");
           if(value != null && !value.isEmpty())
             info.setLastName(value);
           value = request.getParameter("country");
           if(value != null && !value.isEmpty())
             info.setCountry(value);
           value = request.getParameter("phone");
           if(value != null && !value.isEmpty())
             info.setPhone(value);
           value = request.getParameter("address");
           if(value != null && !value.isEmpty())
             info.setAddress(value); %>

    <div class="content">
      <table class="inner" cellpadding="0" cellspacing="0">
          <tr>
              <td class="title-cell">Contact Information</td>
          </tr>
      <tr><td><form action="newcontact" method="POST" name="newcontactform"
        accept-charset="UTF-8" onsubmit="return validateNewContact()">
<%         if(contactId > 0)
           { %>
        <input type="hidden" name="contactid" value="<%= contactId %>" />
<%         }
           if(universityId > 0)
           { %>
        <input type="hidden" name="universityid" value="<%= universityId %>" />
<%         }
           String contactType = request.getParameter("contact");
           if(contactType != null && !contactType.isEmpty())
           { %>
        <input type="hidden" name="contact" value="<%= contactType %>" />
<%         } %>
      <table class="form-table2" border="0" cellpadding="4" cellspacing="0">
        <tr>
        <td>Email</td>
        <td><input class="form-input" type="text" name="email" value="<%= info.getEmail() %>" /></td>
        </tr>
        <tr>
        <td>First Name</td>
        <td><input class="form-input" type="text" name="first" value="<%= info.getFirstName() %>" /></td>
        </tr>
        <tr>
        <td>Last Name</td>
        <td><input class="form-input" type="text" name="last" value="<%= info.getLastName() %>" /></td>
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
        <td><textarea name="address" cols="25" rows="3" width="100%"><%= info.getAddress() %></textarea></td>
        </tr>
        <tr>
        <td colspan="2" align="right">
<%         if(contactId > 0)
           { %>
          <input type="submit" value="Submit Changes" />
<%         }
           else
           { %>
          <input type="submit" value="Add Contact" />
<%         } %>
        </td>
        </tr>
      </table>
      </form></td></tr>
      </table>
    </div>
<%       }
         else
         { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">No profile for the given contact ID.</td>
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
        <td class="error-msg">Invalid contact ID.</td>
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
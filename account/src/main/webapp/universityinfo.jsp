<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Company Information</title>

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
function validateRemoveContact()
{
  return confirm("Are you sure you want to remove this contact?")
}

function sendReplaceContact(replaceform)
{
  var emailText = prompt("Please enter the contact email address.", "")
  if(emailText != null && emailText != "")
  {
    replaceform.email.value = emailText
    replaceform.submit()
  }
}
</script>
<% }
   else
     initFocus = "onload=\"focusLoginForm()\""; %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

  <jsp:include page="header.jsp" flush="true" />

    <div class="push"></div>

<% String idString = request.getParameter("universityid");
   String currentPage = "universityinfo";
   if(idString == null || idString.isEmpty())
     currentPage = "universityinfo.jsp"; %>

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
       users.database.connect();
       csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
       int universityId = users.getUniversityId(user.getUserId());
       boolean isAdmin = user.isAdministrator();
       if(isAdmin)
       {
         try
         {
           universityId = Integer.parseInt(request.getParameter("universityid"));
         }
         catch(Exception e)
         {
         }
       }
       if(universityId > 0)
       {
         csimsoft.DatabaseUniversities universities =
            new csimsoft.DatabaseUniversities(users.database);
         csimsoft.UniversityInfo university = new csimsoft.UniversityInfo();
         universities.getUniversityInfo(universityId, university); %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="60%">
      <tr>
        <td class="heading-cell" colspan="2">Company Information</td>
      </tr>
      <tr>
        <td align="right"><strong>Name</strong></td>
        <td><%= university.getName() %></td>
      </tr>
      <tr>
        <td align="right"><strong>Fax Number</strong></td>
        <td><%= university.getFax() %></td>
      </tr>
      <tr>
        <td colspan="2" align="right">
        <form action="changeuniversity.jsp" method="GET" name="edituniversityform">
          <input type="hidden" name="universityid" value="<%= universityId %>" />
          <input type="submit" value="Edit Company" />
        </form>
        </td>
      </tr>
      </table>
    </div>

<%       String contactTitle = "Administrative Contact";
         String contactType = "admin";
         for(int i = 0; i < 2; i++)
         {
           boolean emailFound = false;
           boolean isContact = false;
           int contactId = university.getContactId(i);
           csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
           emailFound = users.getUserProfileInfo(contactId, info);
           if(!emailFound)
           {
             emailFound = users.getContactInfo(contactId, info);
             isContact = emailFound;;
           } %>
    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="60%">
      <tr>
        <td class="heading-cell" colspan="2"><%= contactTitle %></td>
      </tr>
<%         if(emailFound)
           { %>
      <tr>
        <td align="right"><strong>Name</strong></td>
        <td>
<%           if(isAdmin && !isContact)
             { %>
          <a href="clientinfo.jsp?user=<%= contactId %>"><%= info.getFullName() %></a>
<%           }
             else
             { %>
          <%= info.getFullName() %>
<%           } %>
        </td>
      </tr>
      <tr>
        <td align="right"><strong>Email</strong></td>
        <td><%= info.getEmail() %></td>
      </tr>
      <tr>
        <td align="right"><strong>Phone</strong></td>
        <td><%= info.getPhone() %></td>
      </tr>
      <tr>
        <td align="right" valign="top"><strong>Address</strong></td>
        <td><%= info.getHtmlAddress() %></td>
      </tr>
      <tr>
        <td align="right"><strong>Country</strong></td>
        <td><%= info.getCountry() %></td>
      </tr>
<%         }
           else
           { %>
      <tr>
        <td colspan="2">No contact assigned.</td>
      </tr>
<%         } %>
      <tr>
        <td colspan="2" align="right">
        <table>
        <tr>
          <td>
          <form action="changecontact.jsp" method="GET">
          <input type="hidden" name="universityid" value="<%= universityId %>" />
          <input type="hidden" name="contact" value="<%= contactType %>" />
          <input type="submit" value="New" />
          </form>
          </td>
<%         if(isContact)
           { %>
          <td>
          <form action="changecontact.jsp" method="GET">
          <input type="hidden" name="universityid" value="<%= universityId %>" />
          <input type="hidden" name="contactid" value="<%= contactId %>" />
          <input type="submit" value="Edit" />
          </form>
          </td>
<%         } %>
          <td>
          <form action="addaccount" method="POST" name="<%= contactType %>replaceform">
          <input type="hidden" name="universityid" value="<%= universityId %>" />
          <input type="hidden" name="contact" value="<%= contactType %>" />
          <input type="hidden" name="email" />
          <input type="button" value="Replace"
            onclick="sendReplaceContact(document.<%= contactType %>replaceform)" />
          </form>
          </td>
<%         if(emailFound)
           { %>
          <td>
          <form action="removeuniversitycontact" method="POST"
          onsubmit="return validateRemoveContact()">
          <input type="hidden" name="universityid" value="<%= universityId %>" />
          <input type="hidden" name="contact" value="<%= contactType %>" />
          <input type="submit" value="Remove" />
          </form>
          </td>
<%         } %>
        </tr>
        </table>
        </td>
      </tr>
      </table>
    </div>
<%         contactTitle = "Technical Contact";
           contactType = "tech";
         } %>
<%     }
       else
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The company id is not valid.</td>
      </tr>
      </table>
    </div>
<%     }
     }
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

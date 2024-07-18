<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - All Contacts</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>


<script type="text/javascript" src="formhelp.js"></script>
<% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
   boolean isAdmin = user.isAdministrator();
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\"";
   else if(isAdmin)
   { %>
<script type="text/javascript">
function sendDeleteForm(contactid, email)
{
  if(confirm("Are you sure you want to delete\n" + email + "?"))
  {
    document.deletecontactform.contactid.value = contactid
    document.deletecontactform.submit()
  }
}
</script>
<% } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="allcontacts.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin)
     {
       csimsoft.ContactList contacts = new csimsoft.ContactList();
       contacts.pages.setPage(request.getParameter("page"));
       try
       {
         contacts.connect(); %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
        <tr>
          <td colspan="4">
            <ul class="tool-bar">
              <li class="tool-button">
                <a href="changecontact.jsp">
                <img src="./images/icon-new-user.png" width="20" height="20" />
                Add Contact</a>
              </li>
            </ul>
<%         if(contacts.pages.getPages() > 1)
           { %>
            <%= contacts.pages.getMenu() %>
<%         } %>
          </td>
        </tr>
        <tr>
          <td class="title-cell" colspan="4">Contacts</td>
        </tr>
        <tr>
          <td colspan="2" class="heading-cell">Name</td>
          <td class="heading-cell">Email</td>
          <td class="heading-cell">Phone</td>
        </tr>
<%       while(contacts.hasNext())
         {
           csimsoft.UserProfileInfo info = contacts.getNext();;
           if(info.getPhone().isEmpty())
             info.setPhone("&nbsp;"); %>
        <tr>
          <td class="list-icon">
            <a href="javascript:sendDeleteForm('<%= info.getId() %>', '<%= info.getJsEmail() %>')">
            <img src="./images/icon-delete.jpg" width="20" height="20" /></a>
          </td>
          <td class="list-row">
            <a href="changecontact.jsp?contactid=<%= info.getId() %>">
            <%= info.getLastFirstName() %></a>
          </td>
          <td class="list-row"><%= info.getEmail() %></td>
          <td class="list-row"><%= info.getPhone() %></td>
        </tr>
<%       }

         if(contacts.isEmpty())
         { %>
        <tr>
          <td class="list-row" colspan="4">
            There are no <%= contacts.pages.getStartRow() > 1 ? "more" : "" %> contacts
            in the database.
          </td>
        </tr>
<%       }
         else if(contacts.pages.getPages() > 1)
         { %>
        <tr>
          <td colspan="4">
            <%= contacts.pages.getMenu() %>
          </td>
        </tr>
<%       } %>
        <tr>
          <td colspan="4">
            <form action="removecontact" method="POST" name="deletecontactform">
              <input type="hidden" name="contactid" value="" />
<%       if(contacts.pages.getPage() > 1)
         { %>
              <input type="hidden" name="page" value="<%= contacts.pages.getPage() %>" />
<%       } %>
            </form>
          </td>
        </tr>
      </table>
    </div>

<%     }
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
<%     }
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
<%     }
       contacts.cleanup();
     }
     else
     { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">You are not authorized to view the contents of this page.</td>
      </tr>
      </table>
    </div>
<%   }
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
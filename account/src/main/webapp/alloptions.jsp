<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - All Options</title>

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
function sendDeleteOptionForm(optionid, optionname)
{
  if(confirm("Are you sure you want to delete\n" + optionname + "?"))
  {
    document.deleteoptionform.option.value = optionid
    document.deleteoptionform.submit()
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
        <jsp:param name="current" value="alloptions.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin)
     {
       csimsoft.DatabaseLicenses licenses = new csimsoft.DatabaseLicenses();
       try
       {
         licenses.database.connect();
         licenses.getProductOptions(); %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
        <tr>
          <td colspan="3">
            <ul class="tool-bar">
              <li class="tool-button">
                <a href="addoption.jsp">
                <img src="./images/icon-add-option.png" width="20" height="20" />
                Add Option</a>
              </li>
            </ul>
          </td>
        </tr>
        <tr>
          <td class="title-cell" colspan="3">Product Options</td>
        </tr>
        <tr>
          <td class="heading-cell" colspan="2">Option Name</td>
          <td class="heading-cell">Keyword</td>
        </tr>
<%       int i = 0;
         for( ; licenses.database.results.next(); i++)
         {
           int optionId = licenses.database.results.getInt("optionid");
           String optionName = licenses.database.results.getString("optionname");
           String optionKey = licenses.database.results.getString("optionkey"); %>
        <tr>
          <td class="list-icon">
            <a href="javascript:sendDeleteOptionForm(<%= optionId %>, '<%= optionName.replace("'", "\\'") %>')"
              ><img src="./images/icon-delete.jpg" width="20" height="20" /></a>
          </td>
          <td class="list-row">
            <a href="optioninfo.jsp?option=<%= optionId %>"><%= optionName %></a>
          </td>
          <td class="list-row"><%= optionKey %></td>
        </tr>
<%       }
         licenses.database.results.close();
         if(i == 0)
         { %>
        <tr>
          <td class="list-row" colspan="3">
            There are no product options in the database.
          </td>
        </tr>
<%       } %>
        <tr>
          <td colspan="3">
            <form action="removeoption" method="POST" name="deleteoptionform">
              <input type="hidden" name="option" value="" />
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
       licenses.database.cleanup();
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
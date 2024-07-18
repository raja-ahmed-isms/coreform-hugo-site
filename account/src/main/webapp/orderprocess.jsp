<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Order Processes</title>

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
function sendDeleteForm(processid, processname)
{
  if(confirm("Are you sure you want to delete\n" + processname + "?"))
  {
    document.deleteprocessform.process.value = processid
    document.deleteprocessform.submit()
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
        <jsp:param name="current" value="orderprocess.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin)
     {
       csimsoft.DatabaseProcesses processes = new csimsoft.DatabaseProcesses();
       try
       {
         processes.database.connect();
         processes.getOrderProcessList(); %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
        <tr>
          <td colspan="4">
            <ul class="tool-bar">
              <li class="tool-button">
                <a href="addprocess.jsp">
                <img src="./images/icon-new-process.png" width="20" height="20" />
                Add Process</a>
              </li>
            </ul>
          </td>
        </tr>
        <tr><td class="title-cell" colspan="4">Order Processes</td></tr>
        <tr>
          <td class="heading-cell" colspan="2">Process</td>
          <td class="heading-cell">Class</td>
          <td class="heading-cell">Setup</td>
        </tr>
<%       int i = 0;
         for( ; processes.database.results.next(); i++)
         {
           int processId = processes.database.results.getInt("processid");
           String processName = processes.database.results.getString("processname");
           String handlerName = processes.database.results.getString("handlername"); %>
        <tr>
          <td class="list-icon">
            <a href="javascript:sendDeleteForm(<%= processId %>, '<%= processName.replace("'", "\\'") %>')"
              ><img src="./images/icon-delete.jpg" width="20" height="20" /></a>
          </td>
          <td class="list-row">
            <a href="processinfo.jsp?process=<%= processId %>"><%= processName %></a>
          </td>
          <td class="list-row"><%= handlerName %></td>
          <td class="list-row">
            <a href="orderconfig?process=<%= processId %>">Configure</a>
          </td>
        </tr>
<%       }
         processes.database.results.close();
         if(i == 0)
         { %>
        <tr>
          <td class="list-row" colspan="4">
            There are no order processes in the database.
          </td>
        </tr>
<%       } %>
        <tr>
          <td colspan="4">
            <form action="removeprocess" method="POST" name="deleteprocessform">
              <input type="hidden" name="process" value="" />
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
        <td class="error-msg">
          Error: Unable to connect to the database.<br /><%= sqle %>
        </td>
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
        <td class="error-msg">
          Error: Unable to locate the database resource.<br /><%= ne %>
        </td>
      </tr>
      </table>
    </div>
<%     }
       processes.database.cleanup();
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

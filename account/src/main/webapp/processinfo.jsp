<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Process Information</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="formhelp.js"></script>
<% boolean loginSuccess = csimsoft.LoginServlet.isUserLoggedIn(session);
   csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
   boolean isAdmin = user.isAdministrator();
   String initFocus = "";
   if(!loginSuccess)
     initFocus = "onload=\"focusLoginForm()\"";
   else if(isAdmin)
   { %>
<script type="text/javascript">
function validateProcessChanges()
{
  if(isInputEmpty(document.modifyprocessform.newname))
  {
    alert("The process name field is empty.\n" +
          "Please enter a unique process name.")
    document.modifyprocessform.newname.focus()
    return false
  }

  return true
}

function focusProcessForm()
{
  try
  {
    document.modifyprocessform.newname.focus()
  }
  catch(err)
  {
  }
}
</script>
<%   initFocus = "onload=\"focusProcessForm()\"";
   } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="processinfo.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(loginSuccess)
   {
     if(isAdmin)
     {
       int processId = 0;
       try
       {
         processId = Integer.parseInt(request.getParameter("process"));
       }
       catch(Exception nfe)
       {
       }

       csimsoft.DatabaseProcesses processes = new csimsoft.DatabaseProcesses();
       try
       {
         processes.database.connect();
         String processName = processes.getProcessName(processId);
         if(processName != null)
         {
           if(processName.equals("null"))
             processName = ""; %>

    <div class="content">
      <table class="form-table" cellpadding="0" cellspacing="0">
      <tr><td><form action="modifyprocess" method="POST" name="modifyprocessform"
        accept-charset="UTF-8" onsubmit="return validateProcessChanges()">
        <input type="hidden" name="process" value="<%= processId %>" />
      <table border="0" cellpadding="4" cellspacing="0">
        <tr>
        <td>Process Name</td>
        <td><input type="text" name="newname" value="<%= processName %>" /></td>
        </tr>
        <tr>
        <td colspan="2" align="right"><input type="submit" value="Update Process" /></td>
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
        <td class="error-msg">Error: The given order process id is invalid.</td>
      </tr>
      </table>
    </div>
<%       }
       }
       catch(java.sql.SQLException sqle)
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Unable to connect to the database.<br /><%= sqle %></td>
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
        <td class="error-msg">Error: Unable to locate the database resource.<br /><%= ne %></td>
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

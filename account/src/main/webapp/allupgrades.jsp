<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - All Upgrades</title>

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
function sendDeleteUpgradeForm(upgradeid, upgradename)
{
  if(confirm("Are you sure you want to delete\n" + upgradename + "?"))
  {
    document.deleteupgradeform.upgrade.value = upgradeid
    document.deleteupgradeform.submit()
  }
}

function sendDeleteItemForm(upgradeid, licenseid, licensename)
{
  if(confirm("Are you sure you want to delete\n" + licensename + "?"))
  {
    document.deleteupgradeitemform.upgrade.value = upgradeid
    document.deleteupgradeitemform.license.value = licenseid
    document.deleteupgradeitemform.submit()
  }
}

function sendUpgradeAllForm(upgradeid, upgradename)
{
  if(confirm("Are you sure you want to upgrade all\nthe user licenses in " + upgradename + "?"))
  {
    document.upgradeallform.upgrade.value = upgradeid
    document.upgradeallform.submit()
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
        <jsp:param name="current" value="allupgrades.jsp" />
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
         csimsoft.UpgradePaths paths = new csimsoft.UpgradePaths();
         paths.setPaths(licenses); %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
        <tr>
          <td colspan="4">
            <ul class="tool-bar">
              <li class="tool-button">
                <a href="addupgrade.jsp">
                <img src="./images/icon-add-upgrade.png" width="20" height="20" />
                Add Upgrade Path</a>
              </li>
            </ul>
          </td>
        </tr>
        <tr>
          <td class="title-cell" colspan="4">Upgrade Paths</td>
        </tr>
<%       if(paths.isEmpty())
         { %>
        <tr>
          <td class="list-row" colspan="4">
            There are no upgrade paths in the database.
          </td>
        </tr>
<%       }
         else
         {
           for(int i = 0; i < paths.getPathCount(); ++i)
           {
             csimsoft.UpgradePath path = paths.getPath(i); %>
        <tr>
          <td class="list-icon">
            <a href="javascript:sendDeleteUpgradeForm(<%= path.getUpgradeId() %>, '<%= path.getJsUpgradeName() %>')"
              ><img src="./images/icon-delete.jpg" width="20" height="20" /></a>
          </td>
          <td class="list-icon">
            <a href="selectlicense.jsp?next=newupgradeitem&upgrade=<%= path.getUpgradeId() %>&order=<%= path.getNextOrder() %>"
              ><img src="./images/icon-new-license.png" width="20" height="20" /></a>
          </td>
          <td class="list-row">
            <a href="upgradeinfo.jsp?upgrade=<%= path.getUpgradeId() %>"><%= path.getUpgradeName() %></a>
          </td>
          <td class="list-icon">
            <a href="javascript:sendUpgradeAllForm(<%= path.getUpgradeId() %>, '<%= path.getJsUpgradeName() %>')"
              ><img src="./images/icon-upgrade-all.png" width="20" height="20" /></a>
          </td>
        </tr>
<%           for(int j = 0; j < path.getItemCount(); ++j)
             {
               csimsoft.UpgradePathItem item = path.getItem(j); %>
        <tr>
          <td class="list-icon">&nbsp;</td>
          <td class="list-icon">
            <a href="javascript:sendDeleteItemForm(<%= path.getUpgradeId() %>, <%= item.getLicenseId() %>, '<%= item.getJsLicenseName() %>')"
              ><img src="./images/icon-delete.jpg" width="20" height="20" /></a>
          </td>
          <td class="list-row" colspan="2">
            <%= item.getLicenseName() %>
          </td>
        </tr>
<%           }
           }
         } %>
        <tr>
          <td colspan="4">
            <form action="removeupgrade" method="POST" name="deleteupgradeform">
              <input type="hidden" name="upgrade" value="" />
            </form>
            <form action="removeupgradeitem" method="POST" name="deleteupgradeitemform">
              <input type="hidden" name="upgrade" value="" />
              <input type="hidden" name="license" value="" />
            </form>
            <form action="upgradeall" method="POST" name="upgradeallform">
              <input type="hidden" name="upgrade" value="" />
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
<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - License Details</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="formhelp.js"></script>
<% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
   boolean isAdmin = user.isAdministrator();
   boolean isDistributor = user.isDistributor();
   boolean isReseller = user.isReseller();
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\"";
   else if(isAdmin)
   { %>
<script type="text/javascript">
function sendDeactivateForm(hostid, hostname)
{
  if(confirm("Are you sure you want to delete\n" + hostname + " (" + hostid + ")?"))
  {
    document.deactivateform.hostid.value = hostid
    document.deactivateform.submit()
  }
}

function sendActivateForm(hostid)
{
  document.activateform.hostid.value = hostid
  document.activateform.submit()
}

function validateNewDeactivation()
{
  if(confirm("Are you sure you want to add another\nuser deactivation to this license?"))
    return true

  return false
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
        <jsp:param name="current" value="clientlicense.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin || isDistributor || isReseller)
     {
       csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers();
       csimsoft.DatabaseLicenses licenses =
         new csimsoft.DatabaseLicenses(users.database);
       csimsoft.DatabaseOrders orders = new csimsoft.DatabaseOrders(users.database);
       try
       {
         int licenseId = Integer.parseInt(request.getParameter("license"));
         users.database.connect();
         int userId = licenses.getUserLicenseOwner(licenseId);
         csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
         if(users.getUserProfile(userId, info))
         {
           if(isAdmin || (isDistributor &&
             info.getGroupId() == users.getDistibutorGroup(user.getUserId()))
			 || (isReseller && info.getGroupId() == users.getResellerGroup(user.getUserId())))
           {
             csimsoft.ClientLicenseInfo license = new csimsoft.ClientLicenseInfo();
             if(license.setLicenseInfo(licenses.database, licenseId, false))
             {
               boolean activeOrder = false;
               if(license.getOrderId() > 0)
                 activeOrder = orders.isOrderActive(license.getOrderId()); %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr>
        <td colspan="3">
          <strong><%= info.getFullName() %></strong>:
          <a href="<%= isAdmin ? "clientinfo" : "distclient" %>.jsp?user=<%= userId %>">
          <%= info.getEmail() %></a><br/>
          <ul class="tool-bar">
            <li class="tool-button">
              <a href="clientlicenses.jsp?user=<%= userId %>">
              Client Licenses</a>
            </li>
<%             if(license.getOrderId() > 0)
               { %>
            <li class="tool-button">
              <a href="orderinfo?orderid=<%= license.getOrderId() %>">
              Order</a>
            </li>
<%             } %>
          </ul>
        </td>
      </tr>
      <tr>
        <td class="heading-cell" colspan="2" width="50%">License</td>
        <td class="heading-cell" width="50%">Features</td>
      </tr>
      <tr>
        <td align="right"><strong>Name</strong></td>
        <td width="200"><%= license.getLicenseName() %></td>
        <td rowspan="9" valign="top">
<%             int i = 0;
               if(license.getFeatureCount() > 0)
               {
			     if(isReseller)
				 {
				 %>
					<table border="0" cellpadding="6" cellspacing="0" width="100%">
<%
	               for(i = 0; i < license.getFeatureCount(); ++i)
                   {
                     csimsoft.ClientLicenseInfo.Feature feature = license.getFeature(i);
					%>
					<tr><td><%= feature.featureName %></td></tr>
<%
				   }
				%>
					</table>
<%
				} else {
				%>
          <form action="changeuserfeatures" method="POST" name="editfeaturesform">
            <input type="hidden" name="license" value="<%= licenseId %>" />
          <table border="0" cellpadding="6" cellspacing="0" width="100%">
<%               for(i = 0; i < license.getFeatureCount(); ++i)
                 {
                   csimsoft.ClientLicenseInfo.Feature feature = license.getFeature(i); %>
          <tr><td><input type="checkbox" name="<%= feature.featureId %>"
            value="<%= feature.featureId %>" <%= feature.getChecked() %> />
            <%= feature.featureName %>
          </td></tr>
<%               } %>
          <tr><td align="right">
            <input type="submit" value="Change Features" />
          </td></tr>
          </table>
          </form>
<%          }
		  } %>
        </td>
      </tr>
      <tr>
        <td align="right"><strong>Type</strong></td>
        <td><%= license.getLicenseTypeName() %></td>
      </tr>
      <tr>
        <td align="right"><strong>Product Key</strong></td>
        <td><%= license.getLicenseKey() %></td>
      </tr>
      <tr>
        <td align="right"><strong>Processes</strong></td>
        <td><%= license.getProcessText() %></td>
      </tr>
      <tr>
        <td align="right"><strong>Quantity</strong></td>
        <td><%= license.getQuantity() %></td>
      </tr>
      <tr>
        <td align="right"><strong>Virtual Machine</strong></td>
        <td><%= license.getVirtualMachine() %></td>
      </tr>
      <tr>
        <td align="right"><strong>Remote Desktop</strong></td>
        <td><%= license.getRemoteDesktop() %></td>
      </tr>
      <tr>
        <td align="right"><strong>Expiration</strong></td>
        <td><%= license.getExpiration() %></td>
      </tr>
      <tr>
        <td align="right"><strong>Last Eligible Version</strong></td>
        <td><%= license.getVersion() %></td>
      </tr>
<%    if(!isReseller)
      { %>
      <tr>
        <td colspan="2" align="right">
          <table>
            <tr>
              <td>
                <form action="clientlicenseinfo.jsp" method="GET" name="editinfoform">
                  <input type="hidden" name="license" value="<%= licenseId %>" />
                  <input type="submit" value="Edit" />
                </form>
              </td>
<%             if(isAdmin && !activeOrder)
               { %>
              <td>
                <form action="selectlicense.jsp" method="GET" name="upgradeform">
                  <input type="hidden" name="next" value="upgradelicense" />
                  <input type="hidden" name="userlicense" value="<%= licenseId %>" />
                  <input type="submit" value="Upgrade" />
                </form>
              </td>
              <td>
                <form action="generatekey" method="POST" name="generateform">
                  <input type="hidden" name="license" value="<%= licenseId %>" />
                  <input type="submit" value="Generate Key" />
                </form>
              </td>
              <td>
                <form action="licensekey.jsp" method="GET" name="setkeyform">
                  <input type="hidden" name="license" value="<%= licenseId %>" />
                  <input type="submit" value="Set Key" />
                </form>
              </td>
<%             } %>
            </tr>
          </table>
        </td>
      </tr>
	  <% } %>
      <tr>
        <td class="heading-cell" colspan="2">Maintenance Plan</td>
        <td class="heading-cell">Options</td>
      </tr>
      <tr>
        <td align="right"><strong>Plan Name</strong></td>
        <td>
          <a href="selectupgrade.jsp?next=newuserupgrade&license=<%= licenseId %>">
          <%= license.getUpgradeName() %></a>
        </td>
        <td rowspan="3" valign="top">
<%             if(license.getOptionCount() > 0)
               { %>
          <form action="changeuseroptions" method="POST" name="editoptionsform">
            <input type="hidden" name="license" value="<%= licenseId %>" />
          <table border="0" cellpadding="6" cellspacing="0" width="100%">
<%               for(i = 0; i < license.getOptionCount(); ++i)
                 {
                   csimsoft.ClientLicenseInfo.Feature option = license.getOption(i); %>
          <tr><td><input type="checkbox" name="<%= option.featureId %>"
            value="<%= option.featureId %>" <%= option.getChecked() %> />
            <%= option.featureName %>
          </td></tr>
<%               } %>
          <tr><td align="right">
            <input type="submit" value="Change Options" />
          </td></tr>
          </table>
          </form>
<%             } %>
        </td>
      </tr>
      <tr>
        <td align="right"><strong>Expiration</strong></td>
        <td><%= license.getUpgradeDate() %></td>
      </tr>
<%    if(!isReseller)
      { %>
      <tr>
        <td colspan="2" align="right">
          <form action="clientlicenseupgrade.jsp" method="GET" name="editupgradeform">
            <input type="hidden" name="license" value="<%= licenseId %>" />
            <input type="submit" value="Change Expiration" />
          </form>
        </td>
      </tr>
	  <% } %>
      </table>
      <form action="changeuserplatforms" method="POST" name="editplatformsform">
        <input type="hidden" name="license" value="<%= licenseId %>" />
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr>
        <td class="heading-cell" colspan="2">Platforms</td>
      </tr>
<%             for(i = 0; i < license.getPlatformCount(); ++i)
               {
                 csimsoft.ClientLicenseInfo.Platform platform = license.getPlatform(i);
                 String platformChecked = "";
                 if(platform.selected)
                   platformChecked = "checked=\"checked\""; %>
      <tr>
        <td class="list-row"><input type="checkbox" name="<%= platform.platformId %>"
            value="<%= platform.platformId %>" <%= platformChecked %> />
          <%= platform.platformName %>
        </td>
<%               if(platform.product.getId() > 0)
                 { %>
        <td class="list-row"><%= platform.product.getName() %></td>
<%               }
                 else
                 { %>
        <td class="list-row">Not Available</td>
<%               } %>
      </tr>
<%             }
               if(i == 0)
               { %>
      <tr>
        <td class="list-row" colspan="2">There are no license platforms.</td>
      </tr>
<%             }
               else
               { %>
      <tr>
        <td colspan="2" align="right">
          <input type="submit" value="Change Platforms" />
        </td>
      </tr>
<%             } %>
      </table>
      </form>
<%             if(!activeOrder)
               { %>
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr>
        <td class="heading-cell" colspan="4">
          <table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr>
              <td>Activations</td>
              <td align="right">
                ( <%= license.getDeactivationCount() %> /
                <%= license.getMaxDeactivations() %> Deactivations )
              </td>
            </tr>
          </table>
        </td>
      </tr>
<%               String numColumns = isAdmin ? "" : "colspan=\"3\"";
                 for(i = 0; i < license.getActivationCount(); ++i)
                 {
                   csimsoft.ClientLicenseInfo.Activation activation =
                     license.getActivation(i); %>
      <tr>
<%                 if(isAdmin || isDistributor)
                   { %>
        <td class="list-icon">
          <a href="javascript:sendDeactivateForm('<%= activation.getJsHostId() %>', '<%= activation.getJsHostName() %>')"
            ><img src="./images/icon-delete.jpg" width="20" height="20" /></a>
        </td>
        <td class="list-icon">
          <a href="javascript:sendActivateForm('<%= activation.getJsHostId() %>')"
            ><img src="./images/icon-download.png" width="20" height="20" /></a>
        </td>
<%                 } %>
        <td class="list-row" <%= numColumns %>><%= activation.getHostName() %></td>
        <td class="list-row"><%= activation.hostId %></td>
      </tr>
<%               }
                 if(i == 0)
                 { %>
      <tr>
        <td class="list-row" colspan="4">There are no license activations.</td>
      </tr>
<%               } %>
<%    if(!isReseller)
      { %>
      <tr>
        <td colspan="4" align="right">
          <table>
            <tr>
              <td>
                <form action="addactivation.jsp" method="GET" name="addactivationform">
                  <input type="hidden" name="license" value="<%= licenseId %>" />
                  <input type="hidden" name="xml" value="no" />
                  <input type="submit" value="Activate" />
                </form>
              </td>
<%               if(isAdmin || isDistributor)
                 { %>
              <td>
                <form action="newdeactivation" method="POST" name="newdeactivationform"
                  onsubmit="return validateNewDeactivation()">
                  <input type="hidden" name="license" value="<%= licenseId %>" />
                  <input type="submit" value="Add Deactivation" />
                </form>
              </td>
<%               } %>
            </tr>
          </table>
        </td>
      </tr>
<%               if(isAdmin || isDistributor)
                 {
                   String contextPath = request.getContextPath(); %>
      <tr>
        <td colspan="4">
          <form action="<%= contextPath %>/deactivatelicense" method="POST"
            name="deactivateform">
            <input type="hidden" name="licensekey" value="<%= license.getLicenseKey() %>" />
            <input type="hidden" name="hostid" value="" />
            <input type="hidden" name="xml" value="no" />
          </form>
          <form action="<%= contextPath %>/activatelicense" method="POST"
            name="activateform">
            <input type="hidden" name="licensekey" value="<%= license.getLicenseKey() %>" />
            <input type="hidden" name="hostid" value="" />
          </form>
        </td>
      </tr>
<%	             } %>
<%    } %>
      </table>
<%             } %>
    </div>

<%           }
             else
             { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Invalid license Id.</td>
      </tr>
      </table>
    </div>
<%           }
           }
           else
           { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">You are not authorized to view the contents of this page.</td>
      </tr>
      </table>
    </div>
<%         }
         }
         else
         { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Invalid user or license Id.</td>
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
        <td class="error-msg">Error: Unable to connect to the database.<br/><%= sqle %></td>
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
       catch(Exception e)
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Invalid license Id.</td>
      </tr>
      </table>
    </div>
<%     }
       users.database.cleanup();
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

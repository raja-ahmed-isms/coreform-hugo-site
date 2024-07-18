<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Client Information</title>

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
function validateRolesChange()
{
  return true
}
function toggleDist()
{
  var dist_elem = document.getElementById("dist_checkbox");
  if( dist_elem.checked ) {
	var reseller_elem = document.getElementById("reseller_checkbox");
	if( reseller_elem.checked ) {
		reseller_elem.checked = false;
	}
  }
}
function toggleReseller()
{
  var reseller_elem = document.getElementById("reseller_checkbox");
  if( reseller_elem.checked ) {
	var dist_elem = document.getElementById("dist_checkbox");
	if( dist_elem.checked ) {
		dist_elem.checked = false;
	}
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
        <jsp:param name="current" value="clientinfo.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin)
     {
       csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers();
       try
       {
         int userId = Integer.parseInt(request.getParameter("user"));
         String lead = request.getParameter("source");
         if(lead == null)
           lead = "";
         users.database.connect();
         csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
         if(users.getUserProfileInfo(userId, info))
         {
           csimsoft.UserInfo client = new csimsoft.UserInfo(userId);
           users.getUserRoles(client);
           int universityId = users.getUniversityId(userId);
           csimsoft.UserProfileInfo distributor = new csimsoft.UserProfileInfo();
           users.getUserDistributor(info.getGroupId(), distributor);
           csimsoft.UserProfileInfo reseller = new csimsoft.UserProfileInfo();
           users.getUserReseller(info.getGroupId(), reseller); %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr>
        <td class="heading-cell" colspan="2" width="50%">Profile</td>
        <td class="heading-cell" width="50%">Roles</td>
      </tr>
      <tr>
        <td align="right"><strong>Name</strong></td>
        <td><%= info.getFullName() %></td>
        <td rowspan="3" valign="top"><form action="changeroles" method="POST"
          name="editrolesform" onsubmit="return validateRolesChange()">
          <input type="hidden" name="user" value="<%= userId %>" />
        <table border="0" cellpadding="4" cellspacing="0" width="100%">
<%         String adminChecked = "";
           if(client.isAdministrator())
             adminChecked = "checked=\"checked\""; %>
          <tr><td><input type="checkbox" name="<%= csimsoft.UserInfo.AdminRoleName %>"
            value="<%= csimsoft.UserInfo.AdminRoleName %>" <%= adminChecked %> />
            Administrator</td></tr>
<%         String distChecked = "";
           if(client.isDistributor())
             distChecked = "checked=\"checked\""; %>
          <tr><td><input type="checkbox" id="dist_checkbox" onchange="toggleDist()" name="<%= csimsoft.UserInfo.DistributorRoleName %>"
            value="<%= csimsoft.UserInfo.DistributorRoleName %>" <%= distChecked %> />
            Distributor</td></tr>
<%         String resellerChecked = "";
           if(client.isReseller())
             resellerChecked = "checked=\"checked\""; %>
          <tr><td><input type="checkbox" id="reseller_checkbox" onchange="toggleReseller()" name="<%= csimsoft.UserInfo.ResellerRoleName %>"
            value="<%= csimsoft.UserInfo.ResellerRoleName %>" <%= resellerChecked %> />
            Reseller </td></tr>
          <tr><td align="right"><input type="submit" value="Change Roles" /></td></tr>
        </table>
        </form></td>
      </tr>
      <tr>
        <td align="right"><strong>Email</strong></td>
        <td><%= info.getEmail() %></td>
      </tr>
      <tr>
        <td align="right" colspan="2"><table>
        <tr><td><form action="changeprofile.jsp" method="GET" name="editprofileform">
          <input type="hidden" name="user" value="<%= userId %>" />
          <input type="submit" value="Edit Profile" /></form></td>
        <td><form action="changepass.jsp" method="GET" name="editpassform">
          <input type="hidden" name="user" value="<%= userId %>" />
          <input type="submit" value="Change Password" /></form></td></tr>
        </table></td>
      </tr>
      <tr>
        <td class="heading-cell" colspan="2">Contact Information</td>
        <td class="heading-cell">Privileges</td>
      </tr>
      <tr>
        <td align="right"><strong>Company</strong></td>
        <td><%= info.getCompany() %></td>
        <td rowspan="8" valign="top">
          <table border="0" cellpadding="4" cellspacing="0" width="100%">
            <tr><td><a href="clientdownloads.jsp?user=<%= userId %>">Downloads</a></td></tr>
            <tr><td><a href="clientlicenses.jsp?user=<%= userId %>">Licenses</a></td></tr>
            <tr><td><a href="clientorders.jsp?user=<%= userId %>">Orders</a></td></tr>
<%         if(universityId > 0)
           { %>
            <tr><td><a href="universityinfo.jsp?universityid=<%= universityId %>">Company Profile</a></td></tr>
<%         }
           if(client.isReseller())
           { %>
            <tr><td><a href="resellerusers.jsp?user=<%= userId %>">Reseller Users</a></td></tr>
<%         }
           if(client.isDistributor() && !client.isReseller())
           { %>
            <tr><td><a href="distusers.jsp?user=<%= userId %>">Distributor Users</a></td></tr>
            <tr><td><a href="distorders.jsp?user=<%= userId %>">Distributor Orders</a></td></tr>
            <tr><td><a href="distributor.jsp?user=<%= userId %>">Distributor Files</a></td></tr>
            <tr><td><a href="distlicense.jsp?user=<%= userId %>">Distributor Licenses</a></td></tr>
            <tr><td><a href="distupgrades.jsp?user=<%= userId %>">Distributor Upgrades</a></td></tr>
<%         } %>
          </table>
        </td>
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
      <tr>
        <td align="right" colspan="2">
          <table>
            <tr>
              <td>
                <form action="changeinfo.jsp" method="GET" name="editinfoform">
                  <input type="hidden" name="user" value="<%= userId %>" />
                  <input type="submit" value="Edit Info" />
                </form>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      <tr>
        <td class="heading-cell" colspan="2">Distributor Group</td>
      </tr>
      <tr>
        <td align="right"><strong>Distributor</strong></td>
        <td>
<%         if(distributor.getId() > 0)
           { %>
          <a href="clientinfo.jsp?user=<%= distributor.getId() %>">
          <%= distributor.getFullName() %></a>
<%         } %>
        </td>
      </tr>
      <tr>
        <td align="right" colspan="2">
        <form action="selectdist.jsp" method="GET" name="changedistform">
          <input type="hidden" name="user" value="<%= userId %>" />
          <input type="hidden" name="next" value="changeusergroup" />
          <input type="submit" value="Change Distributor" />
        </form>
        </td>
      </tr>
      <tr>
        <td class="heading-cell" colspan="2">Reseller Group</td>
      </tr>
      <tr>
        <td align="right"><strong>Reseller</strong></td>
        <td>
<%         if(reseller.getId() > 0)
           { %>
          <a href="clientinfo.jsp?user=<%= reseller.getId() %>">
          <%= reseller.getFullName() %></a>
<%         } %>
        </td>
      </tr>

      <tr>
        <td align="right" colspan="2">
        <form action="selectreseller.jsp" method="GET" name="changeresellerform">
          <input type="hidden" name="user" value="<%= userId %>" />
          <input type="hidden" name="next" value="changeusergroup" />
          <input type="submit" value="Change Reseller" />
        </form>
        </td>
      </tr>
      </table>
    </div>
<%       }
         else
         { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Invalid user Id.</td>
      </tr>
      </table>
    </div>
<%       }
       }
       catch(NumberFormatException nfe)
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Invalid user Id.</td>
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
        <td class="error-msg">Error: Unable to connect to the database.<br /><%= e %></td>
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

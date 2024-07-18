<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - License Information</title>

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
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\"";
   else if(isAdmin || isDistributor)
   { %>
<script type="text/javascript">
function focusLicenseForm()
{
  try
  {
    document.licenseinfoform.quantity.focus()
  }
  catch(err)
  {
  }
}
</script>
<%   initFocus = "onload=\"focusLicenseForm()\"";
   } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="clientlicenseinfo.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin || isDistributor)
     {
       csimsoft.DatabaseLicenses licenses = new csimsoft.DatabaseLicenses();
       csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers(licenses.database);
       try
       {
         int licenseId = Integer.parseInt(request.getParameter("license"));
         licenses.database.connect();
         int userId = licenses.getUserLicenseOwner(licenseId);
         if(userId > 0)
         {
           if(isAdmin || users.isDistributor(userId, user.getUserId()))
           {
             int processes = 0;
             int quantity = 0;
             int numDays = 0;
             String vmChecked = "";
             String rdChecked = "";
             String expirationText = null;
             String nodelockedSelected = "";
             String floatingSelected = "";
             String usbSelected = "";
             String rlmCloudSelected = "";
             String usePermanent = "";
             String useDays = "";
             String useExpire = "";
             String version = "";
             licenses.getUserLicenseInfo(licenseId);
             if(licenses.database.results.next())
             {
               int licenseType = licenses.database.results.getInt("licensetype");
               quantity = licenses.database.results.getInt("quantity");
               processes = licenses.database.results.getInt("allowedinst");
               if(licenses.database.results.getInt("vmallowed") != 0)
                  vmChecked = "checked=\"checked\"";
               if(licenses.database.results.getInt("rdallowed") != 0)
                  rdChecked = "checked=\"checked\"";
               numDays = licenses.database.results.getInt("numdays");
               expirationText = csimsoft.Database.formatDate(
                  licenses.database.results.getDate("expiration"));
               if(licenseType == csimsoft.DatabaseLicenses.FloatingLicense)
                  floatingSelected = "selected=\"selected\"";
               else if(licenseType == csimsoft.DatabaseLicenses.NodeLockedLicense)
                  nodelockedSelected = "selected=\"selected\"";
               else if(licenseType == csimsoft.DatabaseLicenses.UsbLicense)
                  usbSelected = "selected=\"selected\"";
               else if(licenseType == csimsoft.DatabaseLicenses.RLMCloudLicense)
                  rlmCloudSelected = "selected=\"selected\"";
             }
             version = licenses.getUserLicenseVersion(licenseId);
             licenses.database.results.close();

             java.util.Calendar today = java.util.Calendar.getInstance();
             if(expirationText == null || expirationText.isEmpty())
             {
               today.add(java.util.Calendar.YEAR, 1);
               if(numDays < 1)
                  usePermanent = "checked=\"checked\"";
               else
                  useDays = "checked=\"checked\"";
             }
             else
             {
               if(numDays > 0)
                 useDays = "checked=\"checked\"";
               else
                 useExpire = "checked=\"checked\"";

               java.text.DateFormat converter =
                 new java.text.SimpleDateFormat("yyyy-MM-dd");
               try
               {
                 today.setTime(converter.parse(expirationText));
               }
               catch(Exception e)
               {
               }
             } %>

    <div class="content">
        <h2>Edit License Information</h2>
      <table class="inner" cellpadding="0" cellspacing="0">
      <tr><td>
      <form action="newuserlicenseinfo" method="POST" name="licenseinfoform">
        <input type="hidden" name="license" value="<%= licenseId %>" />
        <table class="form-table2" border="0" cellpadding="4" cellspacing="0">
          <tr>
            <td>Type</td>
            <td>
              <select name="ltype">
                <option value="<%= csimsoft.DatabaseLicenses.NodeLockedLicense %>" <%= nodelockedSelected %>>
                  <%= csimsoft.DatabaseLicenses.NodeLockedLicenseName %></option>
                <option value="<%= csimsoft.DatabaseLicenses.FloatingLicense %>" <%= floatingSelected %>>
                  <%= csimsoft.DatabaseLicenses.FloatingLicenseName %></option>
                <option value="<%= csimsoft.DatabaseLicenses.RLMCloudLicense %>" <%= rlmCloudSelected %>>
                  <%= csimsoft.DatabaseLicenses.RLMCloudLicenseName %></option>
                <option value="<%= csimsoft.DatabaseLicenses.UsbLicense %>" <%= usbSelected %>>
                  <%= csimsoft.DatabaseLicenses.UsbLicenseName %></option>
              </select>
            </td>
          </tr>
          <tr>
            <td>Last Eligible Version</td>
            <td><input class="form-input" type="text" name="version" value="<%= version%>" /></td>
          </tr>
          <tr>
            <td>Processes</td>
            <td>
              <select name="pcount">
<%           for(int i = 0; i < csimsoft.DatabaseLicenses.ProcessTexts.length; ++i)
             {
               String iSelected = i == processes ? "selected=\"selected\"" : ""; %>
                <option value="<%= i %>" <%= iSelected %>>
                  <%= csimsoft.DatabaseLicenses.ProcessTexts[i] %></option>
<%           } %>
              </select>
            </td>
          </tr>
          <tr>
            <td>Quantity</td>
            <td><input class="form-input" type="text" name="quantity" value="<%= quantity %>" /></td>
          </tr>
          <tr>
            <td colspan="2">
              <input type="checkbox" name="vm" value="vm" <%= vmChecked %> />
              Allow Virtual Machine
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <input type="checkbox" name="rd" value="rd" <%= rdChecked %> />
              Allow Remote Desktop
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <input type="radio" name="expiration" value="permanent" <%= usePermanent %> />
              No Expiration
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <input type="radio" name="expiration" value="numdays" <%= useDays %> />
              Limit Days of Use
            </td>
          </tr>
          <tr>
            <td>Number of Days</td>
            <td><input class="form-input" type="text" name="numdays" value="<%= numDays %>" /></td>
          </tr>
          <tr>
            <td colspan="2">
              <input type="radio" name="expiration" value="date" <%= useExpire %> />
              Set Expiration Date
            </td>
          </tr>
          <tr>
            <td>Day</td>
            <td><input class="form-input" type="text" name="day" value="<%= today.get(java.util.Calendar.DAY_OF_MONTH)%>" /></td>
          </tr>
          <tr>
            <td>Month</td>
            <td><input class="form-input" type="text" name="month" value="<%= today.get(java.util.Calendar.MONTH) + 1 %>" /></td>
          </tr>
          <tr>
            <td>Year</td>
            <td><input class="form-input" type="text" name="year" value="<%= today.get(java.util.Calendar.YEAR) %>" /></td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <input type="submit" value="Submit Changes" />
            </td>
          </tr>
        </table>
      </form>
      </td></tr>
      </table>
    </div>

<%         }
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
<%         }
         }
         else
         { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The license id is not valid.</td>
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
        <td class="error-msg">Error: The user or license id is not valid.</td>
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
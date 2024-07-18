<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>

<head>

  <title>Coreform - License Details</title>

  <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
  <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
  <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

  <!-- Web Fonts -->
  <link
    href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext'
    rel='stylesheet' type='text/css'>

  <script type="text/javascript" src="formhelp.js"></script>
  <% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\"";
   else
   { %>
  <script type="text/javascript">

    function sendDeactivateForm(hostid, hostname) {
      if (confirm("Are you sure you want to delete\n" + hostname + " (" + hostid + ")?")) {
        document.deactivateform.hostid.value = hostid
        document.deactivateform.submit()
      }
    }
    function sendActivateForm(hostid) {
      document.activateform.hostid.value = hostid
      document.activateform.submit()
    }
  </script>
  <% } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <jsp:include page="header.jsp" flush="true" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true">
        <jsp:param name="current" value="licensedetail" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

    <% if(isLoggedIn)
   {
     String contextPath = request.getContextPath();
     csimsoft.DatabaseLicenses licenses = new csimsoft.DatabaseLicenses();
     csimsoft.DatabaseOrders orders = new csimsoft.DatabaseOrders(licenses.database);
     try
     {
       int licenseId = Integer.parseInt(request.getParameter("license"));
       licenses.database.connect();
       if(licenses.isUserLicenseOwner(licenseId, user.getUserId()))
       {
         csimsoft.ClientLicenseInfo license = new csimsoft.ClientLicenseInfo();
         if(license.setLicenseInfo(licenses.database, licenseId))
         {
           boolean activeOrder = false;
           if(license.getOrderId() > 0)
             activeOrder = orders.isOrderActive(license.getOrderId()); 
             %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
        <tr>
          <td class="heading-cell" colspan="2" width="60%">License</td>
          <td class="heading-cell" colspan="2" width="40%">Features</td>
        </tr>
        <tr>
          <td align="right"><strong>Name</strong></td>
          <td width="160"><%= license.getLicenseName() %></td>
          <td colspan="2" rowspan="5" valign="top"><%= license.getFeaturesAndOptions() %></td>
        </tr>
        <tr>
          <td align="right"><strong>Order Reference</strong></td>
          <td>
            <%         if(license.getOrderId() > 0)
           {
             if(license.hasOrderReference())
             { %>
            <a href="orderinfo?orderid=<%= license.getOrderId() %>"><%= license.getOrderReference() %></a>
            <%           }
             else
             { %>
            <a href="orderinfo?orderid=<%= license.getOrderId() %>">See Order</a>
            <%           }
           }
           else
           { %>
            None
            <%         } %>
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
          <td align="right"><strong>Virtual Machine</strong></td>
          <td><%= license.getVirtualMachine() %></td>
        </tr>
        <tr>
          <td align="right"><strong>Remote Desktop</strong></td>
          <td><%= license.getRemoteDesktop() %></td>
        </tr>

        <tr>
          <td align="right"><strong>Processes</strong></td>
          <td><%= license.getProcessText() %></td>
          <%         if(license.showUpgrades())
           { %>
          <td class="heading-cell" colspan="2">Maintenance Plan</td>
          <%         }
           else
           { %>
          <td colspan="2" rowspan="3">&nbsp;</td>
          <%         } %>
        </tr>
        <tr>
          <td align="right"><strong>Quantity</strong></td>
          <td><%= license.getQuantity() %></td>
          <%         if(license.showUpgrades())
           { %>
          <td align="right"><strong>Plan Name</strong></td>
          <td width="150"><%= license.getUpgradeName() %></td>
          <%         } %>
        </tr>
        <tr>
          <td align="right"><strong>Expiration</strong></td>
          <td><%= license.getExpiration() %></td>
          <%         if(license.showUpgrades())
           { %>
          <td align="right"><strong>Expiration</strong></td>
          <td><%= license.getUpgradeDate() %></td>
          <%         } %>
        </tr>
        <%         if(activeOrder)
           { %>
        <tr>
          <td colspan="2" align="right">
            <form action="editlicenseinfo.jsp" method="GET" name="editlicenseform">
              <input type="hidden" name="license" value="<%= licenseId %>" />
              <input type="submit" value="Edit" />
            </form>
          </td>
          <td colspan="2">&nbsp;</td>
        </tr>
        <%         } %>
      </table>
      <tr><td> </td></tr>
      <tr><td> </td></tr>
      <tr><td colspan="3">The Coreform Cubit downloads page has moved to <a style="color: #0000ff" href="https://coreform.com/products/downloads">https://coreform.com/products/downloads</a>.</td></tr>
      <tr><td colspan="3">After you download, install, and launch Coreform Cubit, an activation window will show.</td></tr>
      <tr><td colspan="3">Enter your Product Key in the activation window and Coreform Cubit will start.</td></tr>
      <tr><td> </td></tr>
      <tr><td> </td></tr>
      <%         if(!activeOrder)
           { %>
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
        <tr>
          <td class="heading-cell" colspan="3">Activations</td>
        </tr>
        <% int i = 0; %>
        <%           for(i = 0; i < license.getActivationCount(); ++i)
             {
               csimsoft.ClientLicenseInfo.Activation activation =
                 license.getActivation(i); %>

        <tr>
          <td class="list-icon">
            <a
              href="javascript:sendDeactivateForm('<%= activation.getJsHostId() %>', '<%= activation.getJsHostName() %>')"><img
                src="./images/icon-delete.jpg" width="20" height="20" /></a>
          </td>
          <td class="list-icon">
            <a href="javascript:sendActivateForm('<%= activation.getJsHostId() %>')"><img
                src="./images/icon-download.png" width="20" height="20" /></a>
          </td>
          <td class="list-row"><%= activation.getHostName() %></td>
          <td class="list-row"><%= activation.hostId %></td>
        </tr>
        <%           }
             if(i == 0)
             { %>
        <tr>
          <td class="list-row" colspan="3">There are no license activations.</td>
        </tr>
        <%           } %>
        <tr>
          <td colspan="3" align="right">
            <form action="addactivation.jsp" method="GET" name="addactivationform">
              <input type="hidden" name="license" value="<%= licenseId %>" />
              <input type="hidden" name="xml" value="no" />
              <input type="submit" value="Activate" />
            </form>
          </td>
        </tr>
        <tr>
          <td colspan="3">
            <form action="<%= contextPath %>/deactivatelicense" method="POST" name="deactivateform">
              <input type="hidden" name="licensekey" value="<%= license.getLicenseKey() %>" />
              <input type="hidden" name="xml" value="no" />
              <input type="hidden" name="hostid" value="" />
            </form>
            <form action="<%= contextPath %>/activatelicense" method="POST" name="activateform">
              <input type="hidden" name="licensekey" value="<%= license.getLicenseKey() %>" />
              <input type="hidden" name="hostid" value="" />
            </form>
          </td>
        </tr>
      </table>
      <%           } %>
    </div>

    <%       }
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
    <%       }
       }
       else
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
        <tr>
          <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
          <td class="error-msg">Error: The license Id is not one of your licenses.</td>
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
          <td class="error-msg">Error: Unable to connect to the database.<br /><%= sqle %></td>
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
    <%   }
     licenses.database.cleanup();
   }
   else
   {
     String query = request.getQueryString();
     if(query == null)
       query = "";
     else if(!query.isEmpty())
       query = "?" + query; %>
    <div class="content-login">
      <jsp:include page="loginform.jsp" flush="true">
        <jsp:param name="page" value="<%= request.getServletPath() + query %>" />
      </jsp:include>
    </div>
    <% } %>

  </div>

  <div class="push"></div>

  <jsp:include page="footer.jsp" flush="true" />


  </div>

</body>

</html>

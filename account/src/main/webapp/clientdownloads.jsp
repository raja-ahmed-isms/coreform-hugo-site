<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Client Downloads</title>

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
function sendDisableForm(productid, productname)
{
  if(confirm("Are you sure you want to remove\n'" + productname +
             "' from the user's download list?"))
  {
    document.disableproductform.product.value = productid
    document.disableproductform.submit()
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
        <jsp:param name="current" value="clientdownloads.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     String contextPath = request.getContextPath();
     if(isAdmin || isDistributor)
     {
       csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers();
       csimsoft.DatabaseProducts products =
         new csimsoft.DatabaseProducts(users.database);
       try
       {
         int userId = Integer.parseInt(request.getParameter("user"));
         users.database.connect();
         csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
         if(users.getUserProfile(userId, info))
         {
           if(isAdmin || (isDistributor &&
             info.getGroupId() == users.getDistibutorGroup(user.getUserId())))
           {
             csimsoft.ClientDownloads downloads = new csimsoft.ClientDownloads();
             downloads.setDownloads(products, userId);
             int folderId = 0;
             if(isDistributor)
               folderId = products.getDistributorFolder(user.getUserId()); %>
    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr>
        <td colspan="2">
          <strong><%= info.getFullName() %></strong>:
          <a href="<%= isAdmin ? "clientinfo" : "distclient" %>.jsp?user=<%= userId %>"><%= info.getEmail() %></a>
        </td>
        <td align="right">
<%           if(isAdmin || (isDistributor && folderId > 0))
             { %>
          <form action="selectproduct.jsp" method="GET" name="productauthform">
            <input type="hidden" name="next" value="enableproduct" />
            <input type="hidden" name="seltype" value="files" />
            <input type="hidden" name="user" value="<%= userId %>" />
<%             if(isDistributor)
               { %>
            <input type="hidden" name="root" value="<%= folderId %>" />
<%             } %>
            <input type="submit" value="Enable Download" />
          </form>
<%           } %>
        </td>
      </tr>
      <tr><td class="title-cell" colspan="3">User Downloads</td></tr>
<%           if(downloads.isEmpty())
             { %>
      <tr>
        <td class="list-row" colspan="3">
          There are no downloads available for the user.
        </td>
      </tr>
<%           }
             else
             { %>
      <tr>
        <td class="heading-cell" colspan="2">Product</td>
        <td class="heading-cell">Location</td>
      </tr>
<%             for(int i = 0; i < downloads.getDownloadCount(); i++)
               {
                 csimsoft.ClientDownload download = downloads.getDownload(i); %>
      <tr>
        <td class="list-icon">
          <a href="javascript:sendDisableForm('<%= download.getId() %>', '<%= download.getJsName() %>')"
            ><img src="./images/icon-delete.jpg" width="20" height="20" /></a>
        </td>
        <td class="list-row"><%= download.getName() %></td>
        <td class="list-row">
<%               if(isAdmin)
                 { %>
          <a href="<%= contextPath %>/download?file=<%= download.getLocation() %>"
            ><%= download.getShortLocation() %></a>
<%               }
                 else
                 { %>
          <%= download.getShortLocation() %>
<%               } %>
        </td>
      </tr>
<%             }
             } %>
      <tr>
        <td colspan="3"><form action="disableproduct" method="POST"
          name="disableproductform">
          <input type="hidden" name="user" value="<%= userId %>" />
          <input type="hidden" name="product" value="" />
        </form></td>
      </tr>
      </table>
    </div>
<%         }
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

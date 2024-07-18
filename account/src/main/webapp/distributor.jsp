<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Distributor Information</title>

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
function sendDeleteForm(productid, productname)
{
  if(confirm("Are you sure you want to delete\n" + productname + "?"))
  {
    document.deleteproductform.product.value = productid
    document.deleteproductform.submit()
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

<% String idString = request.getParameter("user");
   String currentPage = "distributor";
   if(idString == null || idString.isEmpty())
     currentPage = "distributor.jsp"; %>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="<%= currentPage %>" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />
<% if(isLoggedIn)
   {
     if(isAdmin || isDistributor)
     {
       csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers();
       csimsoft.DatabaseProducts manager = new csimsoft.DatabaseProducts(users.database);
       try
       {
         int userId = 0;
         if(isDistributor)
           userId = user.getUserId();
         else if(idString != null)
           userId = Integer.parseInt(request.getParameter("user"));

         int rootId = 0;
         String rootString = request.getParameter("rootid");
         if(rootString != null && !rootString.isEmpty())
           rootId = Integer.parseInt(rootString);

         users.database.connect();
         users.database.statement = users.database.connection.createStatement();
         String contextPath = request.getContextPath();
         if(userId > 0)
         {
           csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
           boolean isUserValid = users.getUserProfile(userId, info);
           int folderId = 0;
           if(isUserValid)
             folderId = manager.getDistributorFolder(userId);

           if(isUserValid && folderId > 0)
           {
             if(rootId == 0)
               rootId = folderId;
             csimsoft.ProductList products = new csimsoft.ProductList(folderId);
             products.setProducts(users.database, rootId);
             csimsoft.ProductListGroup root = products.getRoot();
             if(root.getId() == rootId)
             {
               products.calculateDepths();
               rootString = "&back=distributor.jsp";
               if(isAdmin)
                 rootString += "&user=" + userId;
               if(rootId != folderId)
                 rootString += "&rootid=" + rootId;
               String userString = isAdmin ? "&user=" + userId : ""; %>
    <div class="content">
      <div class="list-title">Downloads</div>
<%             if(isAdmin)
               {
                 csimsoft.ProductListGroup home = products.getUserHome();
                 if(home != null)
                 { %>
      <div>
        <strong><%= info.getFullName() %></strong>:
        <a href="clientinfo.jsp?user=<%= userId %>"><%= info.getEmail() %></a>
        <strong>Folder</strong>:
        <a href="renameproduct.jsp?product=<%= home.getId() %><%= rootString %>">
        <%= home.getFullName() %></a>
      </div>
<%               }
               } %>
      <div class="list-heading-row">
        <div class="list-item-button">
          <a href="renameproduct.jsp?parentid=<%= rootId %><%= rootString %>">
          <img src="./images/icon-new-folder.png" width="20" height="20" />
          Add Folder</a>
        </div>
        <div class="list-item-button">
          | <a href="addfile.jsp?parentid=<%= rootId %><%= rootString %>">
          <img src="./images/icon-new-file.png" width="20" height="20" />
          Add Product</a>
        </div>
        <div class="list-item-button">
          | <a href="#" id="openAll" title="Open All">
          Expand All</a>
        </div>
        <div class="list-item-button">
          | <a href="#" id="closeAll" title="Close all">
          Collapse All</a>
        </div>
<%             if(rootId != folderId)
               {
                 String userString2 = isAdmin ? "?user=" + userId : ""; %>
        <div class="list-item-button">
          | <a href="distributor.jsp<%= userString2 %>">
          <img src="./images/icon-goto-folder.png" width="20" height="20" />
          <strong>Home</strong></a>
        </div>
<%               csimsoft.ProductListGroup[] rootPath = products.getRootPath();
                 for(int i = 0; rootPath != null && i < rootPath.length; i++)
                 { %>
        <div class="list-item-button">
          / <a href="distributor.jsp?rootid=<%= rootPath[i].getId() %><%= userString %>">
          <%= rootPath[i].getName() %></a>
        </div>
<%               } %>
        <div class="list-item-button">
          / <strong><%= root.getName() %></strong>
        </div>
<%             } %>
      </div>
<%             if(root.isEmpty())
               { %>
      <div class="list-item">
            There are no downloads in this folder.
      </div>
<%             }
               else
               {
                 int current = 0;
                 csimsoft.ProductListItem item = products.getNextItem(root);
                 while(item != null)
                 {
                   String itemClass = item.isEmpty() ? "list-item" : "list-item-colapsible";
                   int depth = item.getDepth();
                   for( ; current > depth; --current)
                   { %>
      </div>
<%                 } %>
      <div class="<%= itemClass %>">
<%                 if(item.isFolder())
                   { %>
        <div class="list-item-icons">
          <a href="distributor.jsp?rootid=<%= item.getId() %><%= userString %>">
          <img src="./images/icon-goto-folder.png" width="20" height="20" /></a>
          <a href="renameproduct.jsp?parentid=<%= item.getId() %><%= rootString %>">
          <img src="./images/icon-new-folder.png" width="20" height="20" /></a>
          <a href="addfile.jsp?parentid=<%= item.getId() %><%= rootString %>">
          <img src="./images/icon-new-file.png" width="20" height="20" /></a>
          <a href="javascript:sendDeleteForm('<%= item.getId() %>', '<%= item.getJsName() %>')">
          <img src="./images/icon-delete.jpg" width="20" height="20" /></a>
        </div>
        <div class="list-item-cell">
          <a href="renameproduct.jsp?product=<%= item.getId() %><%= rootString %>">
          <%= item.getName() %></a>
        </div>
<%                 }
                   else
                   { %>
        <div class="list-item-icons">
          <a href="<%= contextPath %>/download?file=<%= item.getFullLocation() %>">
          <img src="./images/icon-download.png" width="20" height="20" /></a>
          <a href="updatefile.jsp?productid=<%= item.getId() %><%= rootString %>">
          <img src="./images/icon-upload.png" width="20" height="20" /></a>
          <a href="selectproduct.jsp?next=moveproduct&seltype=folders&root=<%= folderId %>&productid=<%= item.getId() %><%= rootString %>">
          <img src="./images/icon-move-file.png" width="20" height="20" /></a>
          <a href="javascript:sendDeleteForm('<%= item.getId() %>', '<%= item.getJsName() %>')">
          <img src="./images/icon-delete.jpg" width="20" height="20" /></a>
        </div>
        <div class="list-item-cell">
          <a href="renameproduct.jsp?product=<%= item.getId() %><%= rootString %>">
          <%= item.getName() %></a><br />
          <span class="item-secondary"><%= item.getLocation() %></span>
        </div>
<%                 } %>
      </div>
<%                 if(!item.isEmpty())
                   {
                     ++current; %>
      <div class="list-item-wrapper">
<%                 }
                   item = products.getNextItem(item);
                 }
                 for( ; current > 0; --current)
                 { %>
      </div>
<%               }
               } %>
      <div>
        <form action="removeproduct" method="POST" name="deleteproductform">
          <input type="hidden" name="back" value="distributor.jsp" />
<%             if(isAdmin)
               { %>
          <input type="hidden" name="user" value="<%= userId %>" />
<%             }
               if(rootId != folderId)
               { %>
          <input type="hidden" name="rootid" value="<%= rootId %>" />
<%             } %>
          <input type="hidden" name="product" value="" />
        </form>
      </div>
    </div>
<%           }
             else
             { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">
          The given folder is not valid or not accessible. Please go back to your
          <a href="distributor.jsp">home folder</a>.
        </td>
      </tr>
      </table>
    </div>
<%           }
           }
           else if(!isUserValid)
           { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The user id is not valid.</td>
      </tr>
      </table>
    </div>
<%         }
           else if(isAdmin)
           {
             int parentId = manager.getProductIdFromName(
               csimsoft.DatabaseProducts.Distributors); %>
    <div class="content">
      <div class="list-title">Downloads</div>
      <div>
        <strong><%= info.getFullName() %></strong>:
        <a href="clientinfo.jsp?user=<%= userId %>"><%= info.getEmail() %></a>
      </div>
      <div class="list-heading-row">
        <div class="list-item-button">
          <a href="renameproduct.jsp?parentid=<%= parentId %>&back=distributor.jsp&next=newdistfolder&user=<%= userId %>">
          <img src="./images/icon-new-folder.png" width="20" height="20" />
          Create Folder</a>
        </div>
        <div class="list-item-button">
          | <a href="selectproduct.jsp?next=newdistfolder&seltype=folders&root=<%= parentId %>&user=<%= userId %>">
          <img src="./images/icon-folder.png" width="20" height="20" />
          Select Folder</a>
        </div>
      </div>
    </div>
<%         }
           else
           { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">
          Your distributor folder has not been set up. Please contact csimsoft to
          correct the error.
        </td>
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
        <td class="error-msg">Error: The user id is not valid.</td>
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
        <td class="error-msg">Error: The user id is not valid.</td>
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



</div>

<!--start collapsible script-->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script type="text/javascript" src="../js/jquery.tablelist.min.js"></script>
<script type="text/javascript">
$(document).ready(function() {

	//collapsible management
       	$('.list-item-colapsible').collapsible({

	});

	//assign open/close all to functions
	function openAll() {
		$('.list-item-colapsible').collapsible('open');
	}
	function closeAll() {
		$('.list-item-colapsible').collapsible('close');
	}

	//listen for close/open all
	$('#closeAll').click(function(event) {
		event.preventDefault();
		closeAll();
	});
	$('#openAll').click(function(event) {
		event.preventDefault();
		openAll();
	});

});
</script>
<!--end collapsible script-->

</body>

</html>

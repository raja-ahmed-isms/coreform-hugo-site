<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - All Downloads</title>

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
function sendDeleteForm(productid, productname)
{
  if(confirm("Are you sure you want to delete\n" + productname + "?"))
  {
    document.deleteproductform.product.value = productid
    document.deleteproductform.submit()
  }
}

function sendCleanupForm()
{
  if(confirm("Are you sure you want to clean up any unused files?"))
  {
    document.cleanupfilesform.submit()
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
        <jsp:param name="current" value="alldownloads.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     String contextPath = request.getContextPath();
     if(isAdmin)
     {
       String downloads = csimsoft.DownloadServlet.getDirectory(session, request);
       csimsoft.DatabaseProcesses processes = new csimsoft.DatabaseProcesses();
       try
       {
         int rootId = 0;
         String rootString = request.getParameter("rootid");
         if(rootString != null && !rootString.isEmpty())
           rootId = Integer.parseInt(rootString);

         processes.database.connect();
         try
         {
           csimsoft.ProcessNames processNames = processes.getOrderProcessMap();
           csimsoft.ProductList products = new csimsoft.ProductList();
           products.setProducts(processes.database, rootId);
           csimsoft.ProductListGroup root = products.getRoot();
           if(rootId == root.getId())
           {
             products.calculateDepths();
             if(rootId > 0)
               rootString = "?parentid=" + rootId + "&rootid=" + rootId;
             else
               rootString = ""; %>
    <div class="content">
      <div class="list-title">Downloads</div>
      <div>
        <strong>Disk Location:</strong>
        <a href="changedownloads.jsp"><%= downloads %></a>
      </div>
      <div class="list-heading-row">
          <div class="list-item-button">
            <a href="renameproduct.jsp<%= rootString %>">
            <img src="./images/icon-new-folder.png" width="20" height="20" />
            Add Folder</a>
          </div>
          <div class="list-item-button">
            | <a href="addfile.jsp<%= rootString %>">
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
          <div class="list-item-button">
            | <a href="javascript:sendCleanupForm()">
            Cleanup</a>
          </div>
<%           if(rootId > 0)
             { %>
          <div class="list-item-button">
            | <a href="alldownloads.jsp">
            <img src="./images/icon-goto-folder.png" width="20" height="20" />
            <strong>All</strong></a>
          </div>
<%             csimsoft.ProductListGroup[] rootPath = products.getRootPath();
               for(int i = 0; rootPath != null && i < rootPath.length; i++)
               { %>
          <div class="list-item-button">
            / <a href="alldownloads.jsp?rootid=<%= rootPath[i].getId() %>">
            <%= rootPath[i].getName() %></a>
          </div>
<%             } %>
          <div class="list-item-button">
            / <strong><%= root.getName() %></strong>
          </div>
<%           } %>
      </div>
<%           if(root.isEmpty())
             { %>
      <div class="list-item">
        There are no downloads in this folder.
      </div>
<%           }
             else
             {
               int current = 0;
               rootString = rootId > 0 ? "&rootid=" + rootId : "";
               csimsoft.ProductListItem item = products.getNextItem(root);
               while(item != null)
               {
                 String itemClass = item.isEmpty() ? "list-item" : "list-item-colapsible";
                 int depth = item.getDepth();
                 for( ; current > depth; --current)
                 { %>
      </div>
<%               } %>
      <div class="<%= itemClass %>">
<%               if(item.isFolder())
                 { %>
        <div class="list-item-icons">
          <a href="alldownloads.jsp?rootid=<%= item.getId() %>">
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
<%               }
                 else
                 {
                   String processName = processNames.find(item.getProcessId());
                   if(processName == null || processName.isEmpty())
                     processName = "No Process"; %>
        <div class="list-item-icons">
          <a href="<%= contextPath %>/download?file=<%= item.getFullLocation() %>">
          <img src="./images/icon-download.png" width="20" height="20" /></a>
          <a href="updatefile.jsp?productid=<%= item.getId() %><%= rootString %>">
          <img src="./images/icon-upload.png" width="20" height="20" /></a>
          <a href="selectproduct.jsp?next=moveproduct&seltype=folders&productid=<%= item.getId() %><%= rootString %>">
          <img src="./images/icon-move-file.png" width="20" height="20" /></a>
          <a href="javascript:sendDeleteForm('<%= item.getId() %>', '<%= item.getJsName() %>')">
          <img src="./images/icon-delete.jpg" width="20" height="20" /></a>
        </div>
        <div class="list-item-cell">
          <a href="renameproduct.jsp?product=<%= item.getId() %><%= rootString %>">
          <%= item.getName() %></a><br/>
          <span class="item-secondary"><%= item.getLocation() %></span>
        </div>
        <div class="list-item-end">
          <a href="selectprocess.jsp?next=changeprocess&product=<%= item.getId() %><%= rootString %>">
          <%= processName %></a>
        </div>
<%               } %>
      </div>
<%               if(!item.isEmpty())
                 {
                   ++current; %>
      <div class="list-item-wrapper">
<%               }
                 item = products.getNextItem(item);
               }
               for( ; current > 0; --current)
               { %>
      </div>
<%             }
             } %>
      <div>
        <form action="removeproduct" method="POST" name="deleteproductform">
<%           if(rootId > 0)
             { %>
          <input type="hidden" name="rootid" value="<%= rootId %>" />
<%           } %>
          <input type="hidden" name="product" value="" />
        </form>
        <form action="cleanupfiles" method="POST" name="cleanupfilesform">
<%           if(rootId > 0)
             { %>
          <input type="hidden" name="rootid" value="<%= rootId %>" />
<%           } %>
        </form>
      </div>
    </div>
<%         }
           else
           { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The requested root id is not valid.</td>
      </tr>
      </table>
    </div>
<%         }
         }
         catch(java.sql.SQLException sqle2)
         { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">
          Internal error reading products from the database.<br /><%= sqle2 %>
        </td>
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
        <td class="error-msg">Error: The requested root id is not valid.</td>
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
<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Select Product</title>

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
function sendSelectProductForm(productid)
{
  document.selectproductform.product.value = productid
  document.selectproductform.submit()
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
        <jsp:param name="current" value="selectproduct.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin || isDistributor)
     {
       csimsoft.DatabaseProducts manager = new csimsoft.DatabaseProducts();
       try
       {
         String selectType = request.getParameter("seltype");
         boolean onlyFolders = "folders".equals(selectType);
         boolean onlyFiles = "files".equals(selectType);
         csimsoft.ParameterList params = new csimsoft.ParameterList();
         params.setParameters(request);
         params.removeParameter("seltype");
         params.removeParameter("next");
         int rootId = 0;
         if(params.hasParameter("root"))
           rootId = Integer.parseInt(request.getParameter("root"));
         params.removeParameter("root");

         manager.database.connect();
         try
         {
           if(isDistributor && !manager.isDistributorProduct(user.getUserId(), rootId))
           { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">You are not authorized to view the contents of this folder.</td>
      </tr>
      </table>
    </div>
<%         }
           else
           {
             csimsoft.ProductList products = new csimsoft.ProductList();
             if(isDistributor)
               products.setUserHomeId(manager.getDistributorFolder(user.getUserId()));
             if(onlyFolders)
               products.setFolders(manager.database, rootId);
             else
             {
               products.setProducts(manager.database, rootId);
               if(isDistributor)
                 products.addDownloads(manager, user.getUserId());
             }
             csimsoft.ProductListGroup root = products.getRoot();
             if(rootId != root.getId())
             { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The requested root id is not valid.</td>
      </tr>
      </table>
    </div>
<%           }
             else if(root.isEmpty() && !onlyFolders)
             { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The requested root is empty.</td>
      </tr>
      </table>
    </div>
<%           }
             else
             {
               int current = 0;
               products.calculateDepths(onlyFolders); %>

    <div class="content">
      <div class="list-title">Select a
<%             if(onlyFolders)
               { %>
          Folder
<%             }
               else if(onlyFiles)
               { %>
          Product
<%             }
               else
               { %>
          Product or Folder
<%             } %>
      </div>
      <div class="list-heading-row">
        <a href="#" id="openAll" title="Open All">Expand All</a> |
        <a href="#" id="closeAll" title="Close all">Collapse All</a>
      </div>
<%             if(onlyFolders)
               {
                 String itemClass = root.isEmpty() ? "list-item" : "list-item-colapsible";
                 String rootName = root.getName();
                 if(rootName == null || rootName.isEmpty())
                   rootName = "Downloads"; %>
      <div class="<%= itemClass %>">
        <img src="./images/icon-folder.png" width="20" height="20" />
        <a href="javascript:sendSelectProductForm('<%= root.getId() %>')"><%= rootName %></a>
      </div>
<%               if(!root.isEmpty())
                 {
                   ++current; %>
      <div class="list-item-wrapper">
<%               }
               }
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
        <img src="./images/icon-folder.png" width="20" height="20" />
<%                 if(!onlyFiles && item.getId() > 0)
                   { %>
        <a href="javascript:sendSelectProductForm('<%= item.getId() %>')">
        <%= item.getName() %></a>
<%                 }
                   else
                   { %>
        <%= item.getName() %>
<%                 }
                 }
                 else
                 { %>
        <img src="./images/icon-file.png" width="20" height="20" />
        <a href="javascript:sendSelectProductForm('<%= item.getId() %>')">
        <%= item.getName() %></a>
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
<%             } %>
      <div>
<%             String nextPage = request.getParameter("next");
               if(nextPage == null || nextPage.isEmpty())
                 nextPage = "index.jsp";
               String httpMethod = nextPage.endsWith(".jsp") ? "GET" : "POST"; %>
        <form action="<%= nextPage %>" method="<%= httpMethod %>"
          name="selectproductform">
          <input type="hidden" name="product" value="" />
<%             for(int i = 0; i < params.getParameterCount(); i++)
               {
                 String paramName = params.getParameterName(i);
                 String paramValue = request.getParameter(paramName);
                 if(paramValue == null)
                   paramValue = ""; %>
          <input type="hidden" name="<%= paramName %>" value="<%= paramValue %>" />
<%             } %>
        </form>
      </div>
    </div>

<%           }
           }
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
       manager.database.cleanup();
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
<script type="text/javascript" src="../js/jquery.collapsible.min.js"></script>
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

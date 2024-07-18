<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Confirm Delete</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="formhelp.js"></script>

<% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\""; %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="confirmdeletefolder.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
     boolean isDistributor = user.isDistributor();
     if(user.isAdministrator() || isDistributor)
     {
       csimsoft.DatabaseProducts manager = new csimsoft.DatabaseProducts();
       try
       {
         int productId = Integer.parseInt(request.getParameter("product"));
         manager.database.connect();
         try
         {
           if(isDistributor && !manager.isDistributorProduct(user.getUserId(),
             productId))
           { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">You are not authorized to delete the given folder.</td>
      </tr>
      </table>
    </div>
<%         }
           else
           {
             csimsoft.ProductList products = new csimsoft.ProductList();
             products.setProducts(manager.database, productId);
             csimsoft.ProductListGroup root = products.getRoot();
             if(root.getId() == productId)
             {
               products.calculateDepths();
               csimsoft.ParameterList params = new csimsoft.ParameterList();
               params.setParameters(request);
               params.removeParameter("product");
               String backPage = request.getParameter("back");
               if(backPage == null || backPage.isEmpty())
                 backPage = "alldownloads.jsp"; %>

    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
        <tr><td class="title-cell">Confirm Delete</td></tr>
        <tr>
          <td>
          The folder you wanted to delete contains files or folders.
          Do you want to delete all the contents?
          </td>
        </tr>
        <tr>
          <td class="list-row" align="right">
            <form action="removeproduct" method="POST" name="confirmform">
              <input type="hidden" name="product" value="<%= productId %>" />
<%             for(int i = 0; i < params.getParameterCount(); i++)
               {
                 String paramName = params.getParameterName(i);
                 String paramValue = request.getParameter(paramName);
                 if(paramValue == null)
                   paramValue = ""; %>
              <input type="hidden" name="<%= paramName %>" value="<%= paramValue %>" />
<%             } %>
              <input type="hidden" name="contents" value="delete" />
              <input type="submit" value="Yes" />
              <input type="button" value="No" onclick="document.cancelform.submit()" />
            </form>
            <form action="<%= backPage %>" method="GET" name="cancelform">
<%             params.removeParameter("back");
               for(int i = 0; i < params.getParameterCount(); i++)
               {
                 String paramName = params.getParameterName(i);
                 String paramValue = request.getParameter(paramName);
                 if(paramValue == null)
                   paramValue = ""; %>
              <input type="hidden" name="<%= paramName %>" value="<%= paramValue %>" />
<%             } %>
            </form>
          </td>
        </tr>
      </table>
      <div class="list-heading-row">
        <a href="#" id="openAll" title="Open All">Expand All</a> |
        <a href="#" id="closeAll" title="Close all">Collapse All</a>
      </div>
<%             if(!root.isEmpty())
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
            <img src="./images/icon-folder.png" width="20" height="20" />
          </div>
          <div class="list-item-cell">
            <%= item.getName() %>
          </div>
<%                 }
                   else
                   { %>
          <div class="list-item-icons">
            <img src="./images/icon-file.png" width="20" height="20" />
          </div>
          <div class="list-item-cell">
            <%= item.getName() %><br />
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
               } %>
    </div>

<%           }
             else
             { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">The product id is not valid.</td>
      </tr>
      </table>
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
        <td class="error-msg">The product id is not valid.</td>
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

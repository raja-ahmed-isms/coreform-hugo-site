<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<% String productIdString = request.getParameter("product");
   int productId = 0;
   try
   {
     productId = Integer.parseInt(productIdString);
   }
   catch(Exception e)
   {
   } %>

<title>Coreform - <%= productId == 0 ? "Add Folder" : "Rename Product" %></title>

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
function validateNewName()
{
  if(isInputEmpty(document.newnameform.productname))
  {
    alert("The product name is empty.\n" +
          "Please enter a name before submitting.")
    document.newnameform.productname.focus()
    return false
  }

  return true
}

function focusProcessForm()
{
  try
  {
    document.newnameform.productname.focus()
  }
  catch(err)
  {
  }
}
</script>
<%   initFocus = "onload=\"focusProcessForm()\"";
   } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="addgroup.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin || isDistributor)
     {
       if(productId < 0 || (productId == 0 &&
         productIdString != null && !productIdString.isEmpty()))
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
       else
       {
         String productType = "New Folder";
         String submitText = "Add Folder";
         String productName = null;
         if(productId > 0)
         {
           productType = "Product";
           csimsoft.DatabaseProducts products = new csimsoft.DatabaseProducts();
           try
           {
             products.database.connect();
             products.getProductInfo(productId);
             if(products.database.results.next())
             {
               productName = products.database.results.getString("product");
               if(products.database.results.getInt("folder") > 0)
                 productType = "Folder";
             }
             products.database.results.close();
           }
           catch(Exception e)
           {
           }
           products.database.cleanup();
           submitText = "Rename " + productType;
         }
         csimsoft.ParameterList params = new csimsoft.ParameterList();
         params.setParameters(request);
         params.removeParameter("product");
         params.removeParameter("parentid");
         String parentId = request.getParameter("parentid");
         if(parentId == null)
           parentId = "";
         if(productName == null)
           productName = ""; %>
    <div class="content">
      <table class="inner" cellpadding="0" cellspacing="0">
          <tr>
              <td class="title-cell">Create a Folder</td>
          </tr>
      <tr>
        <td>
        <form action="renameproduct" method="POST" name="newnameform"
          onsubmit="return validateNewName()">
          <input type="hidden" name="parentid" value="<%= parentId %>" />
<%       if(productId > 0)
         { %>
          <input type="hidden" name="product" value="<%= productId %>" />
<%       }
         for(int i = 0; i < params.getParameterCount(); i++)
         {
           String paramName = params.getParameterName(i);
           String paramValue = request.getParameter(paramName);
           if(paramValue == null)
             paramValue = ""; %>
          <input type="hidden" name="<%= paramName %>" value="<%= paramValue %>" />
<%       } %>
        <table class="form-table2" border="0" cellpadding="4" cellspacing="0">
          <tr>
            <td><%= productType %> Name</td>
            <td><input class="form-input" type="text" name="productname" value="<%= productName %>" /></td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <input type="submit" value="<%= submitText %>" />
            </td>
          </tr>
        </table>
        </form>
        </td>
      </tr>
      </table>
    </div>
<%     }
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

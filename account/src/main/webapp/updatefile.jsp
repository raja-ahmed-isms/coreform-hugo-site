<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Update File</title>

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
function validateFileUpdate()
{
  if(isInputEmpty(document.fileupdateform.product))
  {
    alert("The product file path is empty.\n" +
          "Please choose a file using the browse button.")
    document.fileupdateform.product.focus()
    return false
  }

  if(confirm("Are you sure you want to update this product?"))
  {
    return true
  }

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
        <jsp:param name="current" value="updatefile.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin || isDistributor)
     {
       try
       {
         int productId = Integer.parseInt(request.getParameter("productid"));
         csimsoft.ParameterList params = new csimsoft.ParameterList();
         params.setParameters(request);
         params.removeParameter("productid"); %>
    <div class="content">
      <table class="inner" cellpadding="0" cellspacing="0">
        <tr>
          <td>
            <form action="updateproduct" method="POST" name="fileupdateform"
              enctype="multipart/form-data" onsubmit="return validateFileUpdate()">
              <input type="hidden" name="productid" value="<%= productId %>" />
<%       for(int i = 0; i < params.getParameterCount(); i++)
         {
           String paramName = params.getParameterName(i);
           String paramValue = request.getParameter(paramName);
           if(paramValue == null)
             paramValue = ""; %>
              <input type="hidden" name="<%= paramName %>" value="<%= paramValue %>" />
<%       } %>
              <table class="form-table2" border="0" cellpadding="4" cellspacing="0">
                <tr>
                  <td>Product File</td>
                  <td><input type="file" name="product" /></td>
                </tr>
                <tr>
                  <td colspan="2" align="right">
                    <input type="submit" value="Update Product" />
                  </td>
                </tr>
              </table>
            </form>
          </td>
        </tr>
      </table>
    </div>
<%     }
       catch(NumberFormatException nfe)
       { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Invalid product id.</td>
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

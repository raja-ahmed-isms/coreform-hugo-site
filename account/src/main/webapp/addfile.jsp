<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Add File</title>

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
function validateNewFile()
{
  if(isInputEmpty(document.newfileform.productname))
  {
    alert("The product name is empty.\n" +
          "Please enter a name before submitting.")
    document.newfileform.productname.focus()
    return false
  }

  if(isInputEmpty(document.newfileform.product))
  {
    alert("The product file path is empty.\n" +
          "Please choose a file using the browse button.")
    document.newfileform.product.focus()
    return false
  }

  return true
}
</script>
<%   initFocus = "onload=\"document.newfileform.productname.focus()\"";
   } %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />




  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="addfile.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin || isDistributor)
     {
       csimsoft.ParameterList params = new csimsoft.ParameterList();
       params.setParameters(request);
       params.removeParameter("parentid");
       String parentId = request.getParameter("parentid"); %>

    <div class="content">
      <table class="inner" cellpadding="0" cellspacing="0">
          <tr>
              <td class="title-cell">Add a Product</td>
          </tr>
          <tr>
          <td>
            <form action="newproduct" method="POST" enctype="multipart/form-data"
              name="newfileform" onsubmit="return validateNewFile()">
<%     if(parentId != null && !parentId.isEmpty())
       { %>
              <input type="hidden" name="parentid" value="<%= parentId %>" />
<%     }
       for(int i = 0; i < params.getParameterCount(); i++)
       {
         String paramName = params.getParameterName(i);
         String paramValue = request.getParameter(paramName);
         if(paramValue == null)
           paramValue = ""; %>
              <input type="hidden" name="<%= paramName %>" value="<%= paramValue %>" />
<%     } %>
              <table class="form-table2" border="0" cellpadding="4" cellspacing="0">
                <tr>
                  <td>Product Name</td>
                  <td><input class="form-input "type="text" name="productname" /></td>
                </tr>
                <tr>
                  <td>Product File</td>
                  <td><input type="file" name="product" /></td>
                </tr>
                <tr>
                  <td colspan="2" align="right">
                    <input type="submit" value="Add Product" />
                  </td>
                </tr>
              </table>
            </form>
          </td>
        </tr>
      </table>
    </div>
<%   }
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
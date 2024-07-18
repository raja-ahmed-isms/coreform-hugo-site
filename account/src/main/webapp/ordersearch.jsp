<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Verify Payment</title>

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
function sendSelectProduct()
{
  document.orderdateform.action = "selectproduct.jsp"
  document.orderdateform.seltype.value = "files"
  document.orderdateform.submit()
}

function sendSelectLicense()
{
  document.orderdateform.action = "selectlicense.jsp"
  document.orderdateform.submit()
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
        <jsp:param name="current" value="ordersearch.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin || isDistributor)
     {
       String beforeYear = "";
       String beforeMonth = "";
       String beforeDay = "";
       String afterYear = "";
       String afterMonth = "";
       String afterDay = "";
       try
       {
         java.util.Calendar today = java.util.Calendar.getInstance();
         beforeYear = Integer.toString(today.get(java.util.Calendar.YEAR));
         beforeMonth = Integer.toString(today.get(java.util.Calendar.MONTH) + 1);
         beforeDay = Integer.toString(today.get(java.util.Calendar.DAY_OF_MONTH));
         today.add(java.util.Calendar.MONTH, -3);
         afterYear = Integer.toString(today.get(java.util.Calendar.YEAR));
         afterMonth = Integer.toString(today.get(java.util.Calendar.MONTH) + 1);
         afterDay = Integer.toString(today.get(java.util.Calendar.DAY_OF_MONTH));
       }
       catch(Exception e)
       {
       }
       csimsoft.ParameterList params = new csimsoft.ParameterList();
       params.setParameters(request); %>

    <div class="content">
      <form action="selectproduct.jsp" method="GET" name="orderdateform">
        <input type="hidden" name="next" value="ordersearch" />
        <input type="hidden" name="seltype" value="files" />
<%     for(int i = 0; i < params.getParameterCount(); i++)
       {
         String paramName = params.getParameterName(i);
         String paramValue = request.getParameter(paramName);
         if(paramValue == null)
           paramValue = ""; %>
        <input type="hidden" name="<%= paramName %>" value="<%= paramValue %>" />
<%     } %>
      <table border="0" celpadding="6" celspacing="0" width="100%">
      <tr>
        <td class="title-cell" colspan="6">Order Date</td>
      </tr>
      <tr>
        <td colspan="6">
           <input type="checkbox" name="usedate" value="yes" checked="checked">
             Search By Order Date</input>
        </td>
      </tr>
      <tr>
        <td colspan="6">
          <input type="checkbox" name="before" value="yes" checked="checked">
            Before</input>
        </td>
      </tr>
      <tr>
        <td>Year</td>
        <td><input type="text" name="by" value="<%= beforeYear %>" /></td>
        <td>Month</td>
        <td><input type="text" name="bm" value="<%= beforeMonth %>" /></td>
        <td>Day</td>
        <td><input type="text" name="bd" value="<%= beforeDay %>" /></td>
      </tr>
      <tr>
        <td colspan="6">
           <input type="checkbox" name="after" value="yes" checked="checked">
             After</input>
        </td>
      </tr>
      <tr>
        <td>Year</td>
        <td><input type="text" name="ay" value="<%= afterYear %>" /></td>
        <td>Month</td>
        <td><input type="text" name="am" value="<%= afterMonth %>" /></td>
        <td>Day</td>
        <td><input type="text" name="ad" value="<%= afterDay %>" /></td>
      </tr>
      <tr>
        <td class="list-row" colspan="6" align="right">
          <table>
            <tr>
              <td>
                <input type="button" value="Select Product" onclick="javascript:sendSelectProduct()" />
              </td>
              <td>
                <input type="button" value="Select License" onclick="javascript:sendSelectLicense()" />
              </td>
            </tr>
          </table>
        </td>
      </tr>
      </table>
      </form>
    </div>

<%   }
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

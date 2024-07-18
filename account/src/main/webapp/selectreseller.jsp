<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Select Reseller</title>

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
function sendSelectResellerForm(groupid)
{
  document.selectresellerform.groupid.value = groupid
  document.selectresellerform.submit()
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
        <jsp:param name="current" value="selectreseller.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     if(isAdmin)
     {
       csimsoft.ResellerList resellers = new csimsoft.ResellerList();
       csimsoft.ParameterList params = new csimsoft.ParameterList();
       params.setParameters(request);
       params.removeParameter("page");
       csimsoft.ParameterBuilder extras = new csimsoft.ParameterBuilder();
       for(int i = 0; i < params.getParameterCount(); ++i)
       {
         String paramName = params.getParameterName(i);
         extras.add(paramName, request.getParameter(paramName));
       }

       resellers.setExtraParameters(extras.toString());
       params.removeParameter("next");
       resellers.pages.setPage(request.getParameter("page"));
       try
       {
         resellers.connect(); %>

    <div class="content">
      <table border="0" celpadding="6" celspacing="0" width="100%">
<%       if(resellers.pages.getPages() > 1)
         { %>
        <tr>
          <td colspan="3">
            <%= resellers.pages.getMenu() %>
          </td>
        </tr>
<%       } %>
        <tr>
          <td class="title-cell" colspan="3">Select a Resellers</td>
        </tr>
        <tr>
          <td class="list-icon">
            <img src="./images/icon-user.png" width="20" height="20" />
          </td>
          <td class="list-row">
            <a href="javascript:sendSelectResellerForm('0')">No Reseller</a>
          </td>
          <td class="list-row">&nbsp;</td>
        </tr>
<%       while(resellers.hasNext())
         {
           csimsoft.UserProfileInfo info = resellers.getNext(); %>
        <tr>
          <td class="list-icon">
            <img src="./images/icon-user.png" width="20" height="20" />
          </td>
          <td class="list-row">
            <a href="javascript:sendSelectResellerForm('<%= info.getGroupId() %>')">
            <%= info.getFullName() %></a>
          </td>
          <td class="list-row">
            <a href="clientinfo.jsp?user=<%= info.getId() %>"><%= info.getEmail() %></a>
          </td>
        </tr>
<%       }
         if(resellers.pages.getPages() > 1)
         { %>
        <tr>
          <td colspan="3">
            <%= resellers.pages.getMenu() %>
          </td>
        </tr>
<%       }
         String nextPage = request.getParameter("next");
         if(nextPage == null || nextPage.isEmpty())
           nextPage = "index.jsp";
         String httpMethod = nextPage.endsWith(".jsp") ? "GET" : "POST"; %>
        <tr>
          <td colspan="3">
            <form action="<%= nextPage %>" method="<%= httpMethod %>"
              name="selectresellerform">
              <input type="hidden" name="groupid" value="" />
<%           for(int i = 0; i < params.getParameterCount(); i++)
             {
               String paramName = params.getParameterName(i);
               String paramValue = request.getParameter(paramName);
               if(paramValue == null)
                 paramValue = ""; %>
              <input type="hidden" name="<%= paramName %>" value="<%= paramValue %>" />
<%           } %>
            </form>
          </td>
        </tr>
      </table>
    </div>
<%     }
       catch(java.sql.SQLException sqle)
       { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">
          Error: Unable to connect to the database.<br/><%= sqle %>
        </td>
      </tr>
      </table>
    </div>
<%     }
       catch(javax.naming.NamingException ne)
       { %>
    <div class="content-msg">
      <table class="error-msg" celpadding="8" celspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: Unable to locate the database resource.</td>
      </tr>
      </table>
    </div>
<%     }
       resellers.cleanup();
     }
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

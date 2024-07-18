<%@page contentType="text/html; charset=UTF-8" %>
<% if(request.getCharacterEncoding() == null)
     request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Order Company</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="formhelp.js"></script>
<% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   String initFocus = "";
   if(isLoggedIn)
   { %>
<script type="text/javascript">
function validateNewUniversity()
{
  if(stripAndValidateInput(document.newuniversityform.university))
  {
    alert("The company name field is empty.\n" +
          "Please enter the company name before submitting the form.")
    document.newuniversityform.university.focus()
    return false
  }

  return true
}

function focusUniversityForm()
{
  try
  {
    document.newuniversityform.university.focus()
  }
  catch(err)
  {
  }
}
</script>
<%   initFocus = "onload=\"focusLoginForm()\"";
   }
   else
     initFocus = "onload=\"focusLoginForm()\""; %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

  <jsp:include page="header.jsp" flush="true" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="orderuniversity.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     csimsoft.DatabaseOrders orders = new csimsoft.DatabaseOrders();
     try
     {
       int orderId = Integer.parseInt(request.getParameter("orderid"));
       orders.database.connect();
       csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
       if(user.isAdministrator() ||
         orders.isOrderOwnedByUser(orderId, user.getUserId()))
       {
         csimsoft.UniversityInfo info = new csimsoft.UniversityInfo();
         info.setName(request.getParameter("university"));
         info.setFax(request.getParameter("fax")); %>

    <div class="content">
      <form action="newuniversity" method="POST" name="newuniversityform"
        accept-charset="UTF-8" onsubmit="return validateNewUniversity()">
        <input type="hidden" name="orderid" value="<%= orderId %>" />
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr><td class="title-cell" colspan="2">Company Information</td></tr>
      <tr>
        <td class="list-row" colspan="2">
        Please enter a unique company name. You may want to include
        the college name.
        </td>
      </tr>
      <tr>
        <td align="right">Company Name</td>
        <td><input type="text" name="university" value="<%= info.getName() %>" /></td>
      </tr>
      <tr>
        <td class="list-row" colspan="2">
        The fax number is not required. Enter it if you want to fax your
        license agreement.
        </td>
      </tr>
      <tr>
        <td align="right">Fax Number</td>
        <td><input type="text" name="fax" value="<%= info.getFax() %>" /></td>
      </tr>
      <tr>
        <td class="list-row" colspan="2">
        Please select your role. An administrative contact handles the
        billing for the licenses. A technical contact handles the
        download and distribution of the software.
        </td>
      </tr>
      <tr>
        <td align="right">Contact Role</td>
        <td>
<%       String contact = request.getParameter("contact");
         String current = "selected=\"selected\""; %>
        <select name="contact">
        <option value="admin" <%= "admin".equals(contact) ? current : "" %> >
          Administrative Contact</option>
        <option value="tech" <%= "tech".equals(contact) ? current : "" %> >
          Technical Contact</option>
        <option value="both" <%= "both".equals(contact) ? current : "" %> >
          Both</option>
        </select>
        </td>
      </tr>
      <tr>
        <td class="list-row" colspan="2" align="right">
        <input type="submit" value="Continue" />
        </td>
      </tr>
      </table>
      </form>
    </div>

<%     }
     }
     catch(NumberFormatException nfe)
     { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Error: The requested order id is not valid.</td>
      </tr>
      </table>
    </div>
<%   }
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
<%   }
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
<%   }
     orders.database.cleanup();
   }
   else
   {
     String query = request.getQueryString();
     if(query == null)
       query = "";
     else if(!query.isEmpty())
       query = "?" + query; %>
    <div class="content-login">
      <jsp:include page="loginform.jsp" flush="true" >
      <jsp:param name="page" value="<%= request.getServletPath() + query %>" />
      </jsp:include>
    </div>
<% } %>

  </div>

  <div class="push"></div>

  <jsp:include page="footer.jsp" flush="true" />


</div>

</body>

</html>

<%@page contentType="text/html; charset=UTF-8" %>
<% if(request.getCharacterEncoding() == null)
     request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Change Company</title>

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
    alert("The university name field is empty.\n" +
          "Please enter the university name before submitting the form.")
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
<%   initFocus = "onload=\"focusUniversityForm()\"";
   }
   else
     initFocus = "onload=\"focusLoginForm()\""; %>

</head>

<body <%= initFocus %>>

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

    <img src="./images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

<% String idString = request.getParameter("universityid");
   String currentPage = "changeuniversity";
   if(idString == null || idString.isEmpty())
     currentPage = "changeuniversity.jsp"; %>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="<%= currentPage %>" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% if(isLoggedIn)
   {
     csimsoft.DatabaseUsers users = new csimsoft.DatabaseUsers();
     csimsoft.DatabaseUniversities universities =
       new csimsoft.DatabaseUniversities(users.database);
     try
     {
       csimsoft.UserInfo user = csimsoft.LoginServlet.getUserInfo(session);
       boolean isAdmin = user.isAdministrator();
       int universityId = 0;
       if(idString != null && !idString.isEmpty())
         universityId = Integer.parseInt(idString);

       users.database.connect();
       if(isAdmin || (universityId > 0 &&
         universityId == users.getUniversityId(user.getUserId())))
       {
         csimsoft.UniversityInfo info = new csimsoft.UniversityInfo();
         boolean validId = true;
         if(universityId > 0)
           validId = universities.getUniversityInfo(universityId, info);

         if(validId)
         {
           String value = request.getParameter("university");
           if(value != null && !value.isEmpty())
             info.setName(value);

           value = request.getParameter("fax");
           if(value != null && !value.isEmpty())
             info.setFax(value); %>

    <div class="content">
      <table class="inner" cellpadding="0" cellspacing="0">
          <tr>
              <td class="title-cell">Company Information</td>
          </tr>
      <tr><td><form action="newuniversity" method="POST" name="newuniversityform"
        accept-charset="UTF-8" onsubmit="return validateNewUniversity()">
<%         if(universityId > 0)
           { %>
        <input type="hidden" name="universityid" value="<%= universityId %>" />
<%         } %>
      <table class="form-table2" border="0" cellpadding="4" cellspacing="0">
        <tr>
        <td>Company Name</td>
        <td><input class="form-input" type="text" name="university" value="<%= info.getName() %>" /></td>
        </tr>
        <tr>
        <td>Fax Number</td>
        <td><input class="form input" type="text" name="fax" value="<%= info.getFax() %>" /></td>
        </tr>
        <tr>
        <td colspan="2" align="right">
<%         if(universityId > 0)
           { %>
          <input type="submit" value="Submit Changes" />
<%         }
           else
           { %>
          <input type="submit" value="Add Company" />
<%         } %>
        </td>
        </tr>
      </table>
      </form></td></tr>
      </table>
    </div>
<%       }
         else
         { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">No profile for the given company ID.</td>
      </tr>
      </table>
    </div>
<%       }
       }
     }
     catch(NumberFormatException nfe)
     { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Invalid company ID.</td>
      </tr>
      </table>
    </div>
<%   }
     catch(Exception e)
     { %>
    <div class="content-msg">
      <table class="error-msg" cellpadding="8" cellspacing="0">
      <tr>
        <td class="msg-icon"><img src="./images/icon-error.jpg" width="40" height="40" /></td>
        <td class="error-msg">Unable to connect to the database.</td>
      </tr>
      </table>
    </div>
<%   }
     users.database.cleanup();
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
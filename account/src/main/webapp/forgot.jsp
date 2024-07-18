<%@page contentType="text/html; charset=UTF-8" %>
<% if(request.getCharacterEncoding() == null)
     request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Password Recovery</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="formhelp.js"></script>

<script type="text/javascript">
function validateEmail()
{
  if(stripAndValidateInput(document.forgotform.email))
  {
    alert("The email address field is empty.\n" +
          "Please enter a valid email address.")
    document.forgotform.email.focus()
    return false
  }

  return true
}
</script>

</head>

<body onload="document.forgotform.email.focus()">

  <jsp:include page="header.jsp" flush="true" />



  <div class="main">

  <jsp:include page="header.jsp" flush="true" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="forgot.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

    <div class="content">
<% String emailText = request.getParameter("email");
   if(emailText == null)
   {
     emailText = (String)session.getAttribute("email");
     if(emailText == null)
       emailText = "";
   } %>
   <h2>Password Recovery</h2>
   <p>Enter the email address used to create your account.</p>
   <table class="inner" cellpadding="0" cellspacing="0">
      <tr><td><form action="sendpass" method="POST" name="forgotform"
        accept-charset="UTF-8" onsubmit="return validateEmail()">
      <table class="indent" border="0" cellpadding="4" cellspacing="0">
        <tr>
        <td><input class="form-input" placeholder="Email Address" type="text" name="email" size="40" value="<%= emailText %>" /></td>
        </tr>
        <tr>
        <td class="center"><input type="submit" value="Get Reset Link" /></td>
        </tr>
      </table>
      </form></td></tr>
      </table>
    </div>

  </div>

  <div class="push"></div>

  <jsp:include page="footer.jsp" flush="true" />


</div>

</body>

</html>

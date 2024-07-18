<%@page contentType="text/html; charset=UTF-8" %>
<% java.util.Locale locale = request.getLocale();
   boolean isJapanese = locale.getLanguage().equals(
     java.util.Locale.JAPANESE.getLanguage()) ||
     locale.getCountry().equals(java.util.Locale.JAPAN.getCountry()); %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Change Password</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="formhelp.js"></script>
<script type="text/javascript">
function validateNewPass()
{
  if(isInputEmpty(document.newpassform.pass))
  {
    alert("The password field is empty.\n" +
          "Please choose a password and enter it.")
    document.newpassform.pass.focus()
    return false
  }

  if(isInputEmpty(document.newpassform.confirm))
  {
    alert("The password confirmation is empty.\n" +
          "Please re-enter your password in the confirmation field.")
    document.newpassform.confirm.focus()
    return false
  }

  if(document.newpassform.pass.value != document.newpassform.confirm.value)
  {
    alert("The entered passwords do not match.")
    document.newpassform.confirm.value = ""
    document.newpassform.pass.focus()
    document.newpassform.pass.select()
    return false
  }

  return true
}

function focusPassForm()
{
  try
  {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    document.newpassform.token.value = token;
    document.newpassform.pass.focus()
  }
  catch(err)
  {
  }
}
</script>

</head>

<body onload="focusPassForm()">

  <div class="main">

  <jsp:include page="header.jsp" flush="true" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="changepass.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

    <div class="content">
        <h2>Change Password</h2>
      <table class="inner" cellpadding="0" cellspacing="0">
      <tr><td><form action="resetpass" method="POST" name="newpassform"
        accept-charset="UTF-8" onsubmit="return validateNewPass()">
        <input type="hidden" name="token"/>
        <table class="form-table2" border="0" cellpadding="4" cellspacing="0">
        <tr>
        <td>Password</td>
        <td><input class="form-input" type="password" name="pass" /></td>
        </tr>
        <tr>
        <td>Confirm Password</td>
        <td><input class="form-input" type="password" name="confirm" /></td>
        </tr>
        <tr>
        <td colspan="2" align="right"><input type="submit" value="Submit Changes" /></td>
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
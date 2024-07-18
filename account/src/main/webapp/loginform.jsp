
<% String requestPage = request.getParameter("page");
   String contextPath = request.getContextPath();
   if(requestPage == null)
     requestPage = "/index.jsp";

   if(!csimsoft.LoginServlet.isUserLoggedIn(session))
   {
     String preamble = "Please";
     if(requestPage.endsWith("index.jsp"))
       preamble = "In order to access your account,";
     else if(requestPage.endsWith("changepass.jsp"))
       preamble = "To change your password, you must";
     else if(requestPage.endsWith("changeinfo.jsp"))
       preamble = "To change your information, you must"; %>



    <h2>Sign In</h2>

<p><%= preamble %> enter your email address and password below.</p>

<%   String emailText = (String)session.getAttribute("email");
     if(emailText == null)
       emailText = ""; %>
<form action="<%= contextPath %>/login" method="POST"
  name="loginform" accept-charset="UTF-8" onsubmit="return validateLogin()">
  <input type="hidden" name="page" value="<%= requestPage %>" />

<table class="inner" cellpadding="0" cellspacing="0">
  <tr><td>
  <table class="center" border="0" cellpadding="4" cellspacing="0">
    <tr>
      <td><input type="text" placeholder="Email Address" name="user" size="40" value="<%= emailText %>" /></td>
    </tr>
    <tr>
      <td><input type="password" placeholder="Password" name="pass" size="40" /></td>
    </tr>
    <tr>
        <td style="float: right"><a href="<%= contextPath %>/forgot.jsp">Forgot your password?</a></td>
    </tr>
  </table>

  </td></tr>
</table>
    <div class="center"><p><input type="submit" value="Sign In" /></p><br/></div>
      <div class="center"><p>Don't have an account?<br/><a href="<%= contextPath %>/signup.jsp">Register</a></p></div>

</form>
<% } %>
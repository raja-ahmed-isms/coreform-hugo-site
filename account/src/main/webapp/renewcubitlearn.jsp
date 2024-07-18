<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>

<head>

    <title>Coreform - Renew Coreform Cubit Learn</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

    <!-- Web Fonts -->
    <link
        href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext'
        rel='stylesheet' type='text/css'>

    <script type="text/javascript" src="formhelp.js"></script>

    <% boolean isLoggedIn = csimsoft.LoginServlet.isUserLoggedIn(session);
   String initFocus = "";
   if(!isLoggedIn)
     initFocus = "onload=\"focusLoginForm()\""; %>

</head>

<body <%= initFocus %>>

    <jsp:include page="header.jsp" flush="true" />



    <div class="main">

        <jsp:include page="header.jsp" flush="true" />

        <div class="push"></div>

        <div class="navleft">
            <jsp:include page="navigation.jsp" flush="true">
                <jsp:param name="current" value=".jsp" />
            </jsp:include>
        </div>

        <jsp:include page="showmessage.jsp" flush="true" />

        <% if(isLoggedIn) { 
            int licenseId = Integer.parseInt(request.getParameter("license"));
            %>
        <div class="content">
            <table border="0" cellpadding="6" cellspacing="0" width="100%">
                <tr>
                    <td class="title-cell">Renew Coreform Cubit Learn</td>
                </tr>
                <tr>
                    <td> If you are still using Coreform Cubit for non-commercial purposes, you can renew your Coreform Cubit Learn license for another year.  For commercial pricing, please contact sales@coreform.com.
                    </td>
                </tr>
                <tr>
                    <td>
                        <form action="renewcubitlearn" method="POST" name="renewlearnform">
                            <input type="hidden" name="license" value="<%= licenseId %>" />
                            <input type="submit" value="Renew Coreform Cubit Learn" />
                        </form>
                    </td>
                </tr>
            </table>
        </div>
        <%} else { %>
        <div class="content-login">
            <jsp:include page="loginform.jsp" flush="true">
                <jsp:param name="page" value="/neworder" />
            </jsp:include>
        </div>
        <% } %>

    </div>

    <div class="push"></div>

    <jsp:include page="footer.jsp" flush="true" />


    </div>

</body>

</html>
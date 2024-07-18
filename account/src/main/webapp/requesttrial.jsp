<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>

<head>

    <title>Coreform - Request Trial</title>

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
            </jsp:include>
        </div>

        <jsp:include page="showmessage.jsp" flush="true" />

        <% if(isLoggedIn) { %>
        <div class="content">
            <table border="0" cellpadding="6" cellspacing="0" width="100%">
                <tr>
                    <td class="title-cell">Request a Trial</td>
                </tr>
                <tr>
                    <td>
                        Get a free trial of the latest Coreform Cubit release for 30 days. The trial is completely unrestricted
                        and is
                        suitable for both FEA and CFD workflows.
                    </td>
                </tr>
                <tr>
                    <td>
                        <form action="requesttrial" method="POST" name="requesttrialform">
                            <input type="hidden" name="license" value="236" />
                            <input type="hidden" name="days" value="30" />
                            <input type="submit" value="Get trial" />
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
<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>

<head>

    <title>Coreform - Get Coreform Cubit Learn</title>

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
                <jsp:param name="current" value="requestlearn.jsp" />
            </jsp:include>
        </div>

        <jsp:include page="showmessage.jsp" flush="true" />

        <% if(isLoggedIn) { %>
        <div class="content">
            <table border="0" cellpadding="6" cellspacing="0" width="100%">
                <tr>
                    <td class="title-cell">Coreform Cubit Learn</td>
                </tr>
                <tr>
                    <td> Coreform Cubit Learn is a free license that allows for non-commercial use of Coreform Cubit,
                        with file export limited to 50,000 elements.  Internet access is required.
                    </td>
                </tr>
                <tr>
                    <td>
                        <form action="requestlearn" method="POST" name="requestlearnform">
                            <input type="submit" value="Get Coreform Cubit Learn" />
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
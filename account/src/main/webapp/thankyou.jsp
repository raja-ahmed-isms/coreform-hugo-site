<% java.util.Locale locale = request.getLocale();
   if(locale.getLanguage().equalsIgnoreCase("ja") ||
     locale.getCountry().equalsIgnoreCase("JP"))
   {
     response.sendRedirect("http://www.jpmandt.com");
     return;
   } %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>

    <title>Thank You - Coreform</title>

<!-- Metadata -->
    <meta name="description" content="Coreform Cubit and CUBIT request confirmation page."/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no" />
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />

<!-- Stylesheets -->
    <link rel="stylesheet" type="text/css" href="css/style.css" />
    <link rel="stylesheet" type="text/css" href="css/ghost-buttons.css" />
    <link rel="stylesheet" type="text/css" href="css/menustyle.css" />

<!-- Web Fonts -->
    <link media="all" type="text/css" rel="stylesheet" href="//fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,300,400,600,700," />

</head>

<body>

  <jsp:include page="header.jsp" flush="true" />
  <div class="main">


    <div class="push"></div>
    <jsp:include page="showmessage.jsp" flush="true" />
    <h3>Thank you!</h3>
    <p>Your request has been submitted. </p>
  </div>

    <div class="push"></div>


<!--Start Footer-->
<jsp:include page="footer.jsp" flush="true" />
<!--End Footer-->

</div>

</body>

</html>
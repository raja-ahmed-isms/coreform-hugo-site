<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>

<title>Coreform - Place Order</title>

    <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

<!-- Web Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext' rel='stylesheet' type='text/css'>

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
      <jsp:include page="navigation.jsp" flush="true" >
        <jsp:param name="current" value="order.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

<% String license = request.getParameter("license");
   String product = request.getParameter("product");
   String days = request.getParameter("days");
   String lead = request.getParameter("source");
   if(isLoggedIn)
   { %>
    <div class="content">
      <table border="0" cellpadding="6" cellspacing="0" width="100%">
      <tr>
        <td class="title-cell">Confirm Order</td>
      </tr>
<%   csimsoft.DatabaseLicenses licenses = new csimsoft.DatabaseLicenses();
     try
     {
       licenses.database.connect();
       int orderId = 0;
       String orderName = null;
       if(license != null && !license.isEmpty())
       {
         orderId = Integer.parseInt(license);
         String licenseName = licenses.getLicenseName(orderId);
         if(licenseName != null)
         {
           orderName = "license"; %>
      <tr>
        <td class="list-row"><%= licenseName %></td>
      </tr>
<%       }
         else
         { %>
      <tr>
        <td class="error-msg">
        Error: The requested license id is not in the database.
        </td>
      </tr>
<%       }
       }
       else if(product != null && !product.isEmpty())
       {
         orderId = Integer.parseInt(product);
         csimsoft.DatabaseProducts products =
           new csimsoft.DatabaseProducts(licenses.database);
         String productName = products.getProductNamePath(orderId);
         if(productName != null && !productName.isEmpty())
         {
           orderName = "product"; %>
      <tr>
        <td class="list-row"><%= productName %></td>
      </tr>
<%       }
         else
         { %>
      <tr>
        <td class="error-msg">
        Error: The requested product id is not in the database.
        </td>
      </tr>
<%       }
       }
       else
       { %>
      <tr>
        <td class="error-msg">
        Invalid order parameter.
        </td>
      </tr>
<%     }
       if(orderId > 0 && orderName != null)
       { %>
      <tr>
        <td>
        To order the <%= orderName %> listed above, please press the "Submit" button
        below. If you do not wish to order the product, hit the back button on
        your browser.
        </td>
      </tr>
      <tr>
        <td class="list-row" align="right">
        <form action="neworder" method="POST" name="neworderform">
          <input type="hidden" name="<%= orderName %>" value="<%= orderId %>" />
<%       if(days != null && !days.isEmpty())
         { %>
          <input type="hidden" name="days" value="<%= days %>" />
<%       }
         if(lead != null && !lead.isEmpty())
         { %>
          <input type="hidden" name="source" value="<%= lead %>" />
<%       } %>
          <input type="submit" value="Submit" />
        </form>
        </td>
      </tr>
<%     }
     }
     catch(NumberFormatException nfe)
     { %>
      <tr>
        <td class="error-msg">
        Error: The requested product/license id is not valid.
        </td>
      </tr>
<%   }
     catch(java.sql.SQLException sqle)
     { %>
      <tr>
        <td class="error-msg">
        Error: Unable to connect to the database.
        </td>
      </tr>
<%   }
     catch(javax.naming.NamingException ne)
     { %>
      <tr>
        <td class="error-msg">
        Error: Unable to locate the database resource.
        </td>
      </tr>
<%   }
     licenses.database.cleanup(); %>
      </table>
    </div>

<% }
   else
   {
     if(license != null && !license.isEmpty())
     {
       session.setAttribute("ordertype", "license");
       session.setAttribute("orderitem", license);
       if(days != null && !days.isEmpty())
          session.setAttribute("orderdays", days);
       if(lead != null && !lead.isEmpty())
          session.setAttribute("ordersource", lead);
     }
     else if(product != null && !product.isEmpty())
     {
       session.setAttribute("ordertype", "product");
       session.setAttribute("orderitem", product);
     } %>
    <div class="content-login">
      <jsp:include page="loginform.jsp" flush="true" >
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

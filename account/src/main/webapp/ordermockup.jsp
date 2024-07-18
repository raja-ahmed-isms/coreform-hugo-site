<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>

<title>Coreform - Order</title>

<!-- Metadata -->
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />

<!-- Stylesheets -->
    <link rel="stylesheet" type="text/css" href="css/style.css" />
    <link rel="stylesheet" type="text/css" href="css/ghost-buttons.css" />
    <link rel="stylesheet" type="text/css" href="css/menustyle.css" />
    <link rel="stylesheet" type="text/css" href="css/animate.css" />

<!-- Web Fonts -->
    <link media="all" type="text/css" rel="stylesheet" href="//fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,300,400,600,700," />


<script type="text/javascript" src="account/formhelp.js"></script>

<script type="text/javascript">
          function makeDisable(){
          var prod=document.getElementById("mySelect")
          prod.disabled=true
          prod.value = "Pro"
          }
         function makeEnable(){
             var prod=document.getElementById("mySelect")
              prod.disabled=false
         }
</script>

<script type="text/javascript">
          function perp(){
          document.getElementById("perpType").disabled = false
          document.getElementById("leaseType").disabled = true
          }
         function lease(){
          document.getElementById("perpType").disabled = true
          document.getElementById("leaseType").disabled = false
         }
</script>

<script type="text/javascript">

function validateNewOrder()
{
  if(stripAndValidateInput(document.orderForm.numLicenses))
  {
    alert("The Number of Licenses field is empty.\n" +
          "Please enter the number of licneses.")
    document.orderForm.numLicenses.focus()
    return false
  }


  if (document.orderForm.licPeriod.value = "annual")
  { 
  if(stripAndValidateInput(document.orderForm.leaseExpDate))
  {
     alert("The Lease Expiration Date field is empty.\n" +
         "Please enter an expiration date for the license(s).")
    document.orderForm.company.focus()
    return false
  }
      
  }


  return true
}
</script>

</head>

<body>

  <jsp:include page="header.jsp" flush="true" />

<!-- Start Banner and Main Menu -->
<jsp:include page="bannermenu.jsp" flush="true" />
<!-- End Banner and Main Menu -->

  <div class="main">

    <img src="images/banadmin.jpg" alt="Administrator" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="account/navigation.jsp" flush="true" >
        <jsp:param name="current" value="ordertemp.jsp" />
      </jsp:include>
    </div>

    


    <div class="content">
        <h2>Place New Order</h2>        
        <form action="" method="POST" name="orderForm" onsubmit="return validateNewOrder()>
            <input type="hidden" name="orderid" value="" />
            <table border="0" cellpadding="6" cellspacing="0" width="100%">

            <tr><td class="title-cell" colspan="2">Product</td></tr>

            <tr>
                <td>
                    <input type="radio" name="licType" value="Commercial" checked="checked" onclick="makeEnable()">Commercial<br/>
                    <input type="radio" name="licType" value="Academic" onclick="makeDisable()">Academic<br/>
                    <input type="radio" name="licType" value="NFR" onclick="makeEnable()">Not For Resale
                </td>
            </tr>
            <tr>
                <td>Select the product: <select name="verType" id="mySelect" />
                        <option value="Pro" selected>Trelis Pro</option>
                        <option value="FEA">Trelis FEA</option>
                        <option value="CFD">Trelis CFD</option>
                    </select>
                </td>
            </tr>


            <tr><td class="title-cell" colspan="2">License Terms</td></tr>

            <tr>
                <td>
                    <input type="radio" name="instType" value="stand-alone" checked="checked" />Stand-alone<br/>
                    <input type="radio" name="instType" value="network" />Network
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    Number of licenses: <input type="text" name="numLicenses" />
                </td>
            </tr>

            <tr><td class="title-cell" colspan="2">License Period</td></tr>

            <tr>
                <td>
                    <input type="radio" name="licPeriod" value="annual" checked="checked" onclick="lease()"/>Annual Lease<br/>
                        <input type="radio" name="licPeriod" value="perpetual" onclick="perp()" />Perpetual-use
                </td>
            </tr>

            <tr>
                <td>Lease Expiration Date</td>
                <td><input type="text" name="leaseExpDate" id="leaseType"/></td>

            </tr>
            <tr>
                <td>Maintenance Expiration Date</td>
                <td><input type="text" name="mainExpDate" disabled="true" id="perpType" /></td>
            </tr>


            <tr><td class="title-cell" colspan="2">Downloads</td></tr>

            <tr>
                <td>Select ALL allowed downloads<br/>
                    <input type="checkbox" name="downloads" value="lin32deb" />Linux 32-bit deb<br/>
                    <input type="checkbox" name="downloads" value="lin32rpm" />Linux 32-bit rpm<br/>
                    <input type="checkbox" name="downloads" value="lin64deb" />Linux 64-bit deb<br/>
                    <input type="checkbox" name="downloads" value="lin64rpm" />Linux 64-bit rpm
                </td>
                <td>
                    <input type="checkbox" name="downloads" value="mac32" />Mac 32-bit<br/>
                    <input type="checkbox" name="downloads" value="win32" />Windows 32-bit<br/>
                    <input type="checkbox" name="downloads" value="win64" />Windows 64-bit
                </td>
            </tr>

            <tr>
                <td class="list-row" colspan="2" align="right">
                <input type="submit" value="Place Order" />
                <input type="reset" value="Cancel"
                </td>
            </tr>
            </table>
        </form>
    </div>
  </div>

  <div class="push"></div>

<!--Start Footer-->
<jsp:include page="footer.jsp" flush="true" />
<!--End Footer-->

</div>

</body>

</html>

<%@page contentType="text/html; charset=UTF-8" %>
<% if(request.getCharacterEncoding() == null)
     request.setCharacterEncoding("UTF-8"); %>
<% java.util.Locale locale = request.getLocale();
   boolean isJapanese = locale.getLanguage().equals(
     java.util.Locale.JAPANESE.getLanguage()) ||
     locale.getCountry().equals(java.util.Locale.JAPAN.getCountry()); %>
<!DOCTYPE html>
<html>

<head>

  <title>Coreform Cubit Learn</title>

  <link rel="stylesheet" type="text/css" media="screen" href="../css/style.css" />
  <link rel="stylesheet" type="text/css" media="screen" href="../css/pagestyle.css" />
  <link rel="stylesheet" type="text/css" media="screen" href="../css/menustyle.css" />

  <!-- Web Fonts -->
  <link
    href='http://fonts.googleapis.com/css?family=Lato:400,300,200,200italic,300italic,400italic&subset=latin,latin-ext'
    rel='stylesheet' type='text/css'>
  <!-- Form -->
  <script type="text/javascript" src="formhelp.js"></script>
  <!-- reCAPTCHA 
<script src="https://www.google.com/recaptcha/api.js" async defer></script>-->

  <script type="text/javascript">
    var askCompany = true
    function validateNewClient() {
      if (stripAndValidateInput(document.signupform.first)) {
        alert("The first name field is empty.\n" +
          "Please enter your first name.")
        document.signupform.first.focus()
        return false
      }

      if (stripAndValidateInput(document.signupform.last)) {
        alert("The last name field is empty.\n" +
          "Please enter your last name.")
        document.signupform.last.focus()
        return false
      }

      if (stripAndValidateInput(document.signupform.email)) {
        alert("The email address field is empty.\n" +
          "Please enter a valid email address.")
        document.signupform.email.focus()
        return false
      }

      if (isInputEmpty(document.signupform.pass)) {
        alert("The password field is empty.\n" +
          "Please choose a password and enter it.")
        document.signupform.pass.focus()
        return false
      }

      if (isInputEmpty(document.signupform.confirm)) {
        alert("The password confirmation is empty.\n" +
          "Please re-enter your password in the confirmation field.")
        document.signupform.confirm.focus()
        return false
      }

      if (document.signupform.pass.value != document.signupform.confirm.value) {
        alert("The entered passwords do not match.")
        document.signupform.confirm.value = ""
        document.signupform.pass.focus()
        document.signupform.pass.select()
        return false
      }

      if (askCompany) {
        if (stripAndValidateInput(document.signupform.company)) {
          if (confirm("The school field is empty.  Please enter your school.")) {
            document.signupform.company.focus()
            return false
          }
          else
            askCompany = false
        }
      }

      if (stripAndValidateInput(document.signupform.phone)) {
        alert("The phone field is empty.\n" +
          "Please enter your phone number before submitting the form.")
        document.signupform.phone.focus()
        return false
      }

      if (stripAndValidateInput(document.signupform.country)) {
        alert("The country field is empty.\n" +
          "Please enter the country before submitting the form.")
        document.signupform.country.focus()
        return false
      }
      //This checks for export restricted countries. It's circumventable, but no uncircumventable methods exist.
      //I was told to do this by Matt Sederberg.  -Scot Wilcox, 7/10/2020
      const countryLower = document.signupform.country.value.toLowerCase();
      if (countryLower == "iran" || countryLower == "north korea" || countryLower == "sudan" || countryLower == "cuba" || countryLower == "russia") {
        alert("Coreform LLC is not allowed under U.S. law to distribute software in your country.\n" +
          "You will now be redirected to our export policy.");
        window.location.href = "/export/";
        return false;
      }

      if (stripAndValidateInput(document.signupform.address)) {
        alert("The address field is empty.\n" +
          "Please enter the address before submitting the form.")
        document.signupform.address.focus()
        return false
      }

      if (!document.signupform.privacy.checked) {
        alert("Please accept our terms and conditions to continue.")
        return false
      }

      return true
    }
  </script>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
  <script>
    $(document).ready(function () {
      $('#ind_or_edu').on('change', function () {
        if (this.value == 'industry') {
          $("#ind_specific").show();
          $("#edu_specific").hide();
        }
        else {
          $("#ind_specific").hide();
          $("#edu_specific").show();
        }
      });
    });
  </script>

</head>

<body onload="document.signupform.first.focus()">

  <div class="main">

    <jsp:include page="header.jsp" flush="true" />

    <div class="push"></div>

    <div class="navleft">
      <jsp:include page="navigation.jsp" flush="true">
        <jsp:param name="current" value="signup.jsp" />
      </jsp:include>
    </div>

    <jsp:include page="showmessage.jsp" flush="true" />

    <% csimsoft.UserProfileInfo info = new csimsoft.UserProfileInfo();
   info.setFirstName(request.getParameter("first"));
   info.setLastName(request.getParameter("last"));
   info.setEmail(request.getParameter("email"));
   info.setCompany(request.getParameter("company"));
   info.setPhone(request.getParameter("phone"));
   info.setAddress(request.getParameter("address"));
   info.setIndustry(request.getParameter("industry"));
   info.setCountry(request.getParameter("country")); %>
    <div class="content">
      <h2>Sign up for Coreform Cubit Learn</h2>
      <p style="text-align: left; font-size: 20px; font-weight: bolder; color:#555">Contact information</p>
      <p style="text-align: left; padding-left: 23px; font-size: 15px; color:#555">Please provide the following
        information. <strong>All fields are required</strong>.</p>
      <table class="center" cellpadding="0" cellspacing="0">
        <tr>
          <td>
            <form action="adduserlearn" method="POST" name="signupform" accept-charset="UTF-8"
              onsubmit="return validateNewClient()">
              <table class="inner" border="0" cellpadding="4" cellspacing="0">
                <tr>
                  <td><input type="text" placeholder="First name*" name="first" size="40"
                      value="<%= info.getFirstName() %>" /></td>
                </tr>
                <tr>
                  <td><input type="text" placeholder="Last name*" name="last" size="40"
                      value="<%= info.getLastName() %>" /></td>
                </tr>
                <tr>
                  <td><input type="text" placeholder="Email address*" name="email" size="40"
                      value="<%= info.getEmail() %>" /></td>
                </tr>
                <tr>
                  <td><input type="password" placeholder="Password*" name="pass" size="40" /></td>
                </tr>
                <tr>
                  <td><input type="password" placeholder="Confirm password*" name="confirm" size="40" /></td>
                </tr>
                <tr>
                  <td><input type="text" placeholder="Affiliation*" name="company" size="40"
                      value="<%= info.getCompany() %>" />
                  <td>
                </tr>
                <tr>
                  <td><input type="text" placeholder="Phone*" name="phone" size="40" value="<%= info.getPhone() %>" />
                  <td>
                </tr>
                <tr>
                  <td><textarea placeholder="Address*" name="address" style="width: 70%"
                      rows="4"><%= info.getAddress() %></textarea></td>
                </tr>
                <tr>
                  <td><textarea placeholder="Tell us about how you plan to use Coreform Cubit Learn" name="planneduse"
                      style="width: 70%" rows="4"><%= info.getPlannedUse() %></textarea></td>
                </tr>
                <tr>
                  <td style="color:#555">
                    <label for="country">Country: </label>
                    <select style="color:#555" id="country" name="country">
                      <option value="afghanistan">Afghanistan</option>
                      <option value="albania">Albania</option>
                      <option value="algeria">Algeria</option>
                      <option value="andorra">Andorra</option>
                      <option value="angola">Angola</option>
                      <option value="antigua">Antigua</option>
                      <option value="argentina">Argentina</option>
                      <option value="armenia">Armenia</option>
                      <option value="australia">Australia</option>
                      <option value="austria">Austria</option>
                      <option value="azerbaijan">Azerbaijan</option>
                      <option value="bahamans">The Bahamas</option>
                      <option value="bahrain">Bahrain</option>
                      <option value="bangladesh">Bangladesh</option>
                      <option value="barbados">Barbados</option>
                      <option value="belarus">Belarus</option>
                      <option value="belgium">Belgium</option>
                      <option value="belize">Belize</option>
                      <option value="bolivia">Bolivia</option>
                      <option value="brazil">Brazil</option>
                      <option value="bulgaria">Bulgaria</option>
                      <option value="cambodia">Cambodia</option>
                      <option value="canada">Canada</option>
                      <option value="chad">Chad</option>
                      <option value="chile">Chile</option>
                      <option value="china">China</option>
                      <option value="colombia">Colombia</option>
                      <option value="congo">Congo</option>
                      <option value="costa rica">Costa Rica</option>
                      <option value="croatia">Croatia</option>
                      <option value="cuba">Cuba</option>
                      <option value="cyprus">Cyprus</option>
                      <option value="czech republic">Czech Republic</option>
                      <option value="denmark">Denmark</option>
                      <option value="dominican republic">Dominican Republic</option>
                      <option value="ecuador">Ecuador</option>
                      <option value="egypt">Egypt</option>
                      <option value="el salvador">El Salvador</option>
                      <option value="ethiopia">Ethiopia</option>
                      <option value="fiji">Fiji</option>
                      <option value="finland">Finland</option>
                      <option value="france">France</option>
                      <option value="georgia">Georgia</option>
                      <option value="germany">Germany</option>
                      <option value="ghana">ghana</option>
                      <option value="greece">Greece</option>
                      <option value="guatemala">Guatemala</option>
                      <option value="haiti">Haiti</option>
                      <option value="honduras">Honduras</option>
                      <option value="hungary">Hungary</option>
                      <option value="iceland">Iceland</option>
                      <option value="india">India</option>
                      <option value="indonesia">Indonesia</option>
                      <option value="iran">Iran</option>
                      <option value="iraq">Iraq</option>
                      <option value="ireland">Ireland</option>
                      <option value="israel">Israel</option>
                      <option value="italy">Italy</option>
                      <option value="jamaica">Jamaica</option>
                      <option value="japan">Japan</option>
                      <option value="jordan">Jordan</option>
                      <option value="kazakhstan">Kazakhstan</option>
                      <option value="kenya">Kenya</option>
                      <option value="kuwait">Kuwait</option>
                      <option value="kyrgyzstan">Kyrgyzstan</option>
                      <option value="latvia">Latvia</option>
                      <option value="lebanon">Lebanon</option>
                      <option value="liberia">Liberia</option>
                      <option value="libya">Libya</option>
                      <option value="lithuania">Lithunia</option>
                      <option value="luxembourg">Luxembourg</option>
                      <option value="luxembourg">Luxembourg</option>
                      <option value="madagascar">Madagascar</option>
                      <option value="malawi">Malawi</option>
                      <option value="malaysia">Malaysia</option>
                      <option value="maldives">Maldives</option>
                      <option value="mexico">Mexico</option>
                      <option value="micronesia">Micronesia</option>
                      <option value="mongolia">Mongolia</option>
                      <option value="morocco">Morocco</option>
                      <option value="myanmar">Myanmar</option>
                      <option value="nepal">Nepal</option>
                      <option value="netherlands">Netherlands</option>
                      <option value="new zealand">New Zealand</option>
                      <option value="nicaragua">Nicaragua</option>
                      <option value="niger">Niger</option>
                      <option value="nigeria">Nigeria</option>
                      <option value="north korea">North Korea</option>
                      <option value="norway">Norway</option>
                      <option value="oman">Oman</option>
                      <option value="pakistan">Pakistan</option>
                      <option value="panama">Panama</option>
                      <option value="papua new guinea">Papua New Guinea</option>
                      <option value="paraguay">Paraguay</option>
                      <option value="peru">Peru</option>
                      <option value="philippines">Philippines</option>
                      <option value="poland">Poland</option>
                      <option value="portugal">Portugal</option>
                      <option value="qatar">Qatar</option>
                      <option value="romania">Romania</option>
                      <option value="russia">Russia</option>
                      <option value="rwanda">Rwanda</option>
                      <option value="samoa">Samoa</option>
                      <option value="saudi arabia">Saudi Arabia</option>
                      <option value="senegal">Senegal</option>
                      <option value="serbia">Serbia</option>
                      <option value="sierra leone">Sierra Leone</option>
                      <option value="singapore">Singapore</option>
                      <option value="slovakia">Slovakia</option>
                      <option value="slovenia">Slovenia</option>
                      <option value="somalia">Somalia</option>
                      <option value="south africa">South Africa</option>
                      <option value="south korea">South Korea</option>
                      <option value="spain">Spain</option>
                      <option value="sri lanka">Sri Lanka</option>
                      <option value="sudan">Sudan</option>
                      <option value="sweden">Sweden</option>
                      <option value="switzerland">Switzerland</option>
                      <option value="syria">Syria</option>
                      <option value="taiwan">Taiwan</option>
                      <option value="tanzania">Tanzania</option>
                      <option value="thailand">Thailand</option>
                      <option value="tonga">Tonga</option>
                      <option value="trinidad and tobago">Trinidad and Tobago</option>
                      <option value="tunisia">Tunisia</option>
                      <option value="turkey">Turkey</option>
                      <option value="turkmenistan">Turkmenistan</option>
                      <option value="uganda">Uganda</option>
                      <option value="ukraine">Ukraine</option>
                      <option value="united arab emirates">United Arab Emirates</option>
                      <option value="united kingdom">United Kingdom</option>
                      <option selected value="united states">United States</option>
                      <option value="uruguay">Uruguay</option>
                      <option value="uzbekistan">Uzbekistan</option>
                      <option value="venezuela">Venezuela</option>
                      <option value="vietnam">Vietnam</option>
                      <option value="yemen">Yemen</option>
                      <option value="zambia">Zambia</option>
                      <option value="zimbabwe">Zimbabwe</option>
                    </select>
                  <td>
                </tr>
                <tr>
                  <td style="text-align: left; font-size: 20px; font-weight: bolder; color:#555; padding-top: 20px">
                    Terms and conditions</td>
                </tr>
                <tr>
                  <td style="text-align: left; padding-left: 23px; font-size: 15px; color:#555">We want you to know
                    exactly how our services work and why we need your registration details. Please see our <a
                      href="../privacy" target="_blank">Privacy Policy</a> regarding the use of your personal
                    information before you continue.<br /><br /><input type="checkbox" name="privacy" /> I agree to the
                    <a href="../terms" target="_blank">terms and conditions</a>.</td>
                </tr>
                <tr>
                  <td style="text-align: left; font-size: 20px; font-weight: bolder; color:#555; padding-top: 20px">
                    Contact permission</td>
                </tr>
                <tr>
                  <td style="text-align: left; padding-left: 23px; font-size: 15px; color:#555;">We'd like to include
                    you on the Coreform mailing list to receive news and information about our
                    products.
                    <br /><br />
                    <input type="checkbox" name="opt-in" /> Yes, include me on the Coreform mailing list.
                    <br />
                    <input type="checkbox" name="blog" /> I'm interested in having my work showcased on Coreform Cubit's
                    blog.
                  </td>
                </tr>
                <tr>

                </tr>
                <tr>
                  <td class="center"><br /><input type="submit" value="Submit" /></td>
                </tr>

              </table>
            </form>
          </td>
        </tr>
      </table>
    </div>

  </div>

  <div class="push"></div>

  </div>

</body>

</html>
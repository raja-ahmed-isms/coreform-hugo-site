<nav>Account</nav>

<% String requestContext=request.getContextPath(); String current=request.getParameter("current");
  if(csimsoft.LoginServlet.isUserLoggedIn(session)) { csimsoft.UserInfo user=csimsoft.LoginServlet.getUserInfo(session);
  csimsoft.SideNavigation accountnav=new csimsoft.SideNavigation( requestContext);
  csimsoft.SideNavigation.CubitLearnDetails learnDetails = accountnav.hasCubitLearnLicense(user.getUserId());
  accountnav.addItem("/logout", "Sign Out" ); accountnav.addItem("/index.jsp", "Profile" );
  accountnav.addItem("/licenses", "My Licenses" );
  accountnav.addItem("/orders.jsp", "My Orders" );
  if(learnDetails.hasLearnLicense) {
    if(learnDetails.numDaysRemaining < 60) {
      accountnav.addItem("/renewcubitlearn.jsp?license=" + learnDetails.learnLicenseId, "Renew Coreform Cubit Learn" );
    }
  }
  else {
    accountnav.addItem("/requestlearn.jsp", "Request Free Coreform Cubit Learn License" );
  } %>

  <%= accountnav.buildMenu(current) %>

    <% if(user.isUniversityUser()) { csimsoft.SideNavigation univnav=new csimsoft.SideNavigation( requestContext);
      univnav.addItem("/universityinfo.jsp", "Profile" ); %>

      <h4>Company</h4>

      <%= univnav.buildMenu(current) %>

        <% } if(user.isReseller()) { csimsoft.SideNavigation resellernav=new csimsoft.SideNavigation( requestContext);
			resellernav.addItem("/resellerusers.jsp", "Clients" ); %>

          <h4>Reseller</h4>

          <%= resellernav.buildMenu(current) %>


        <% } if(user.isDistributor() && !user.isReseller()) { csimsoft.SideNavigation distnav=new csimsoft.SideNavigation( requestContext);
          distnav.addItem("/distusers.jsp", "Clients" ); distnav.addItem("/distorders.jsp", "Orders" );
          distnav.addItem("/distributor.jsp", "Downloads" ); distnav.addItem("/distlicense.jsp", "Licenses" );
          distnav.addItem("/distupgrades.jsp", "Upgrades" ); %>

          <h4>Distributor</h4>

          <%= distnav.buildMenu(current) %>

            <% } if(user.isAdministrator()) { csimsoft.SideNavigation adminnav=new csimsoft.SideNavigation(
              requestContext); adminnav.addItem("/userlist.jsp", "Users" );
              adminnav.addItem("/allcontacts.jsp", "Contacts" ); adminnav.addItem("/allorders.jsp", "Orders" );
              adminnav.addItem("/alldownloads.jsp", "Downloads" );
              adminnav.addItem("/allproducts.jsp", "Product Licenses" ); adminnav.addItem("/allfeatures.jsp", "Features"
              ); adminnav.addItem("/alloptions.jsp", "Options" ); adminnav.addItem("/allplatforms.jsp", "Platforms" );
              adminnav.addItem("/allupgrades.jsp", "Upgrades" ); adminnav.addItem("/orderprocess.jsp", "Processes" );
              adminnav.addItem("/universities.jsp", "Companies" ); adminnav.addItem("/updatedb", "Update DB" );
              adminnav.addItem("/testemail", "Test Email" ); %>

              <h4>Administration</h4>

              <%= adminnav.buildMenu(current) %>

                <% } } else { csimsoft.SideNavigation loginnav=new csimsoft.SideNavigation( requestContext);
                  loginnav.addItem("/index.jsp", "Sign In" ); loginnav.addItem("/signup.jsp", "Create Account" ); %>

                  <%= loginnav.buildMenu(current) %>

                    <% } %>

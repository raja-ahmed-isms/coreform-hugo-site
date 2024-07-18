+++
title = "Coreform Cubit Offline Activation"
keywords = "coreform cubit learn, activate, activation, free meshing software"
include_contact = false
include_collapse = true
layout = "oldstyle"
featured_image = "/images/par/parOffice2.jpg"
aliases = ["/support/activation/activateoffline/"]
+++

{{% section %}}

# Coreform Cubit offline activation

Product keys are used to activate Coreform software licenses. Below are instructions for activating your license when your computer is not connected to the internet or if you get an error when using the standard activation process

If the computer running Coreform Cubit is not connected to the Internet, you will need to use another computer that can access the Internet to (i) retrieve the product key, and (ii) activate and download the license file. 

To manually activate your software: 

1. Install and launch Coreform Cubit. 

2. Obtain the hostid of your computer using the rlmutil utility. To do this, navigate to the installation folder in a terminal window (`cd Program Files/Coreform Cubit 2020.2`) Then type `rlmutil rlmhostid ether`

<!-- ![Terminal rlmutil](activate_windows_cp.png) -->
{{< figure src="../activate-windows-cp.png" width="700" >}}


3. On a device with Internet access, select the license in your account's [My Licenses](http://coreform.com/account/licenses) area. 


4. Carefully record the Product Key supplied in the License section: you will rename your license file to this product key in step #8 below.


{{< figure src="../accts2LicensePageCC202X.png" width="600" >}}

5. At the bottom of the page, click the "Activate" button. 

6. In the activation dialog that appears, enter the hostname and hostid of the computer, then click “Add Activation” Please note that rlmutil rlmhostid ether will create a number of host names, you want to pick the first set of characters before the space.


{{< figure src="../activate-windows-cp-hostid.png" width="700" >}}



7. Scroll to Activation at the bottom of the license page and click on the download icon next to the license. 

![Activation license download](/images/activate_download.jpg)


<!-- *Wait, we're still on the computer that is connected to the Internet, not the offline computer. Can't download to the bin/licenses folder of the offline computer.* -->

8. Save the node-locked license file to your `Cubit 202x.x/bin/licenses` folder. 

<!-- Revised instructions: -->






<!-- *Note: A node-locked product key will activate Coreform Cubit on a single computer. A floating product key will activate a floating license of Coreform Cubit on the RLM-Server license server.* -->


If you get an error message during installation, please see [Coreform Cubit FAQs](/products/coreform-cubit/cubitfaqs) for possible solutions. If you have any questions about activating your software, please [contact Coreform support](/company/contact).



































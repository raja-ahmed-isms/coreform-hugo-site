+++
title = "Coreform Cubit License Update"
keywords = "coreform cubit learn, activate, activation, free meshing software"
include_contact = false
include_collapse = true
layout = "oldstyle"
featured_image = "/images/par/parOffice2.jpg"
aliases = ["/support/activation/activateupdate/"]
+++

{{% section %}}

# Coreform Cubit license update

Updates to Coreform Cubit software will require both (i) downloading the new software version and (ii) updating the local license file. 

In general, the local license file needs to be updated when upgrading to the latest significant release, such as upgrading from 2020.2 to 2021.1. In the case of maintenance release upgrades, such as from 202x.x.x to 2020.x.x, no license file update is necessary.  

Follow the update instructions below for your local license type: 

* [Update a node-locked license](#update-a-node-locked-license)
* [Update a floating license](#update-a-floating-license) 

### Update a node-locked license 
A node-locked license is installed on a single standalone computer, whether or not it is connected to a network. To upgrade a node-locked license to a new version: 

1. Download the software update installation package for your computer's operating system from the [downloads page](https://coreform.com/products/downloads/) 

2. Install the updated version of Coreform Cubit on the same computer on which the previous version was installed and activated. (Note that the updated version will install by default to a new folder whose name includes the updated version number, e.g. c:\Program Files\Coreform Cubit 2020.2)

<!-- IS THIS NEEDED? (By default, Coreform Cubit will install to to a new folder with the updated version number (for example c:\Program Files\Coreform Cubit 2020.2).) -->
<!-- The software will by default install to a folder with the version number (for example, c:\Program Files\Coreform Cubit 2020.2).  -->
  <!-- (Maintenance releases will install into the same folder as the release version. (Cubit 202x.x.x installs into c:\Program Files\Coreform Cubit 202x.x)-->   
  
  <!-- Why would we mention this^ if no update is needed for maintenance releases? -->

3. Launch the updated application. 

    * If the application launch is interrupted by a "Cubit Activation" dialog, proceed to Step 4 below.  

    * If the application launches as usual, open the "Cubit Activation" dialog by going to the Help menu and selecting "Product Activation." 

    <!--![Opening activation dialog via Help > Product Activation](/images/activate_help.jpg)-->
  {{< figure src="../productactivation.png" width="600">}}  

4. In the "Cubit Activation" dialog, enter your valid product key in the first field. (You can obtain your product key by clicking the "Coreform Cubit 202x.x" link in your Coreform account's My Licenses area.)   

{{< figure src="../nodelocked.png" width="600">}}

5. Do not change the pre-filled “node-locked” descriptor. Your computer's name should appear automatically in the Hostname field. 

6. Click the "Activate" button. At the bottom left of the dialog, you will see the message "Activation Successful! Press close to continue."


<!-- (If the license is not activated, an error message will appear in the lower left corner of the window. Possible errors include (a) incorrect permissions to save to the license folder, (b) failure to connect to the activation server (due to firewall settings or Internet connection problems). // Why do we list these without providing remedies? -->

<!-- Note: A node-locked product key will activate the application for the local computer only. A node-locked application cannot be activates using a floating product key.  -->

### Update a floating license. 

<!-- If the expiration date or some other option of your license changes, the license on your license server must be updated to reflect the change.  -->

Updating a floating license may involve one or more of the following: 
* [Updating the floating license itself, following the same procedure as the original activation](#updating-the-floating-license-on-the-license-server)
* [Update the RLM-Server license manager on the server](#updating-the-rlm-server-license-manager)
* [Updating Coreform Cubit software on workstation computers connected to the network](#updating-coreform-cubit-software-on-workstation-computers)

#### Updating the floating license on the license server 

<!-- Note: The local license file in the RLM-Server folder of the license server must be updated whenever a change is made to the license, such as a change to the expiration date or a version upgrade. However, the license file does not need to be updated for maintenance releases, such as 202x.x.1 or 202x.x.2.  -->

<!-- To update the floating license on the license server, -->

**Windows** 
1. Run the rlm_activate utility to open the Product Activation dialog. 

2. On the Product Activation dialog, enter the product key and press Activate. The software license is now updated.

![Floating license server activation](/images/activate_server_active.jpg)

**Linux or Mac OS X**

1. From a terminal run as administrator:   
`.\rlm_activate --activate <product_key> <hostname>`

where &lt;product_key&gt; is the product key for the license and &lt;hostname&gt; is the name of the server. The hostname is optional, but will identify the server in your list of licenses in your account. 

#### Updating the RLM-Server license manager

Although previous versions of RLM-Server should still manage newer application licenses, we recommend that you update the RLM-Server to match the version of the application you are using. For example, a Cubit 2020.2 license will run on RLM-Server 2020.1, but we recommend updating to RLM-Server 2020.2. 

To update the RLM-Server: 

1. Uninstall the older license manager. This can be done by running the uninstall utility located in the RLM-Server folder. Several files will remain in the folder after uninstalling RLM-Server, including the license file.

2. Download the new RLM-Server 

3. Install the updated RLM-Server software on the server. For more information, see the [RLM-Server installation instructions](/support/activation/cubit/rlmserverinstall/).

#### Updating Coreform Cubit software on workstation computers  

To install the new version of Coreform Cubit on workstations:

1. Download the appropriate application from the [Downloads page](https://coreform.com/products/downloads/) on coreform.com  

2. Install the application update on each network computer where the application will be run. 

3. During the installation process, you will be prompted to set up the license. In the "Cubit Activation" dialog that appears, select the "Floating: Connect to a license server" option.

4. In the first field, enter the IP address or hostname of the license server on which RLM-Server has been installed along with an activated floating license. 
5. The "Port Number" field should be automatically filled with the default port number (5053). (See [RLM-Server Installation Instructions](/support/activation/rlmserverinstall/) for information about changing the port.)
6. Click the "Connect" button. At the bottom left of the dialog, you will see the message "Connection to license server succeeded. Press close to continue." 

{{< figure src="../floating.png" width="600">}}  

*Need help? If you have further questions about transferring your license, please [contact Coreform support](/company/contact).*

<!-- Is this actually correct, or does the activation dialog appear on first launch, as for node-locked?  -->
<!-- Alt instructions for latter case:  -->
<!-- 4. After installing, launch the updated Coreform Cubit on each workstation. -->
<!-- 5. In the "Cubit Activation" dialog that appears, select the "Floating: Connect to a license server" option. -->





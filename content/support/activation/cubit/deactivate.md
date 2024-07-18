+++
title = "Coreform Cubit License Deactivation"
keywords = "coreform cubit learn, activate, activation, free meshing software"
include_contact = false
include_collapse = true
layout = "oldstyle"
featured_image = "/images/par/parOffice2.jpg"
aliases = ["/support/activation/deactivate/"]
+++

{{% section %}}

# Transfer/deactivate a license

Coreform Cubit licenses can be transferred from one computer or server to another by deactivating the license, uninstalling the relevant software from the old computer or server, installing  the relevant software to the new computer or server, and re-activating the license in the new location. The details of the process differ for node-locked and floating licenses; you can find instructions for both below. 

* [Transfer a node-locked license](#transfer-a-node-locked-license)  
  * [Node-locked license deactivation](#node-locked-license-deactivation)
  * [Node-locked license transfer](#node-locked-license-transfer)
  * [Node-locked license deactivation failure](#node-locked-license-deactivation-failed) <br><br>
* [Transfer a floating license](#transfer-a-floating-license)
  * [Floating license deactivation failure](#floating-license-deactivation-failure)

### Transfer a node-locked license 
A node-locked license is installed on a single standalone computer. Even if the computer is connected to a network, the license is tied to the standalone machine. As long as the license is valid, the license can be deactivated and transferred to another standalone computer. 
<br><br>

##### Node-locked license deactivation 

To transfer a node-locked license to a different computer, first deactivate the license on the original computer. 
1. Select "Product Activation" from Coreform Cubit's "Help" menu.  
2. In the "Cubit Activation" dialog that appears, enter the Product Key into the Product Key field and click the "Deactivate" button. (The product key can be found by following the appropriate link on the My Licenses page of your Coreform account.)  
{{< figure src="../nodelocked.png" width="600">}}

3. A deactivation warning will appear asking you to confirm the deactivation. 

{{< figure src="/images/deactivate_standalone_warning.jpg" width="600">}}  

4. When the license is deactivated, press OK to exit Coreform Cubit.  

{{< figure src="/images/deactivate_standalone_success.jpg" width="400">}}

5. Uninstall Coreform Cubit from the old computer. You may need to remove the installation folder to completely remove the software.  
<br><br>

##### Node-locked license transfer

To transfer the license to a new standalone computer: 

1. Install Coreform Cubit on the new computer. 

2. Launch the software. In the "Cubit Activation" dialog that appears, enter the original Product Key into the Product Key field and click the "Activate" button.  

3. At the bottom left of the dialog, you will see "Activation Successful! Press close to continue."  
<br><br>

##### Node-locked license deactivation failed  

* Deactivation number exceeded  
    * By default, each license has one deactivation allowing you to make one transfer. If you receive a message that the deactivation failed because the number of deactivations has been exceeded, please contact support@coreform.com to request additional deactivations. There is no charge for additional deactivations/transfers. 
    {{< figure src="/images/deactivate_standalone_failed.jpg" width="600">}}
    

* File not found   
    * A license file usually is named using the product key number with a .lic extension (for example, FZBC2CD-3128C4-20F964-DEC545-0300CE4.lic). 
    * If you activated the license from your Coreform account's My Licenses page, or if you changed the file name, be sure that the license file is renamed to the Product Key format (XXXXXXX-XXXXXX-XXXXXX-XXXXXX-XXXXXXX.lic) and try to deactivate again. 
    {{< figure src="/images/deactivate_standalone_failed_file.jpg" width="600" >}}

NOTE: The license file can be found in the `C:/Program Files/Cubit 202x.x/bin/licenses` folder.

### Transfer a floating license 

A floating license is installed on a network server and shared by computers over the network. The license is tied to the server. As long as the license is valid, the license can be deactivated and transferred to another server.

To transfer a floating license to a different server, first deactivate the license on the original server following the instructions below for Windows or Linux/Mac. 

* Log into your Coreform account 
* Go to My Licenses page 
* Find the floating license you want to deactivate 

{{< figure src="../findfloatinglicense.png" width="600">}}
<!-- License deactivation on the server    -->

* Scroll down to the large red X and click 

* Press ok 
{{< figure src="../dialogepopup.png" width="600">}}

* Your license should no longer have a large red X

* You are ready to add a new [RLM server](/support/activation/rlmserverinstall). 

* Update all computers using the floating license with the new RLM server information.  

<!--

<!-- Then follow the [floating license activation instructions](/support/activation/cubit/activatenetwork/) to activate the license on the new server.  
<br><br>

##### Floating license: Windows deactivation and transfer

1. Run the `rlm_activate` utility to open the Product Activation dialog. This can be done from the Start menu or by double-clicking "rlm_activate.exe" in the server's "C:\Program Files\RLM-Server" folder.

2. In the RLM Activation dialog, enter the product key and press the "Deactivate" button. The hostname should fill in automatically. (If not successful, see [Floating license deactivation failed](#floating-license-deactivation-failed) below for help.)

{{<  figure src="/images/activate_server_active.jpg" >}}

3. Uninstall RLM-Server, then delete the RLM-Server folder to remove any remaining log files. 

4. On the new server machine, download RLM-Server from the Coreform Support's [License downloads page](/support/downloads) and install the RLM-Server software on the new server. 

5. Activate the floating license on the server using the original product key. For more information, see the instructions for [RLM-Server installation](/support/activation/rlmserverinstall).  
<br><br>

##### Floating license: Linux, Mac OS X, or Windows Command Prompt deactivation

1. Run from a terminal as administrator: `rlm_activate --deactivate <filename.lic> <product key>` where &lt;filename.lic &gt; is the name of the license file and &lt;product key&gt; is product key for the license. In most cases, the license's filename will simply be the product key followed by the .lic extension. ([See below](#floating-license-deactivation-failed) if your license has a different filename and you are having difficulty deactivating.)

{{< figure src="/images/deactivate_network_win.jpg" >}}

{{< figure src="/images/deactivate_network_linux.jpg" >}}

2. Uninstall RLM-Server, then delete the RLM-Server folder to remove any remaining log files. 

3. Download the new RLM-Server from Coreform Support's [License downloads page](/support/downloads) and install the RLM-Server software on the new server. 

4. Activate the floating license on the server using the original product key. For more information, see the instructions for [RLM-Server installation](/support/activation/rlmserverinstall).  
<br><br> -->

##### Floating license deactivation failure  

* Deactivation number exceeded
    * By default, each license has one deactivation allowing you to make one transfer. If you receive a message that the deactivation failed because the number of deactivations has been exceeded, please contact support@coreform.com to request additional deactivations. There is no charge for additional deactivations. 


*Need help? If you have further questions about transferring your license, please [contact Coreform support](/company/contact).*





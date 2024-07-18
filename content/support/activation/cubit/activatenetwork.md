+++
title = "Coreform Cubit floating license activation"
keywords = "coreform cubit, activate, activation, installation, install"
include_contact = false
include_collapse = true
layout = "oldstyle"
featured_image = "/images/par/parOffice2.jpg"
aliases = ["/support/activation/activatenetwork/"]
+++

<!-- {{% section %}} -->

# Floating license activation

A floating license is a shared license that can be used by multiple people on multiple devices. However, only one simultaneous user is able to access a license. 

To coordinate the use of licenses an license server must be set up.  The computer should be located on your network and may require your IT department to help configure properly.  If you are unable to set up an RLM server for your floating licenses, please contact support@coreform.com. 

To setup a floating license on a network, two steps must be completed: 

  [1. Install RLM-Server software on a license server and activate the license](#install-and-activate-rlm-server) 

  [2. Install Coreform Cubit on individual network computers and indicate the license server IP address](#install-coreform-cubit)

*Already have RLM on your server? If you already are using RLM on your server, you can simply use the [csimsoft.set file](/support/downloads) and your license file.*



## Install and activate RLM-Server

To activate a floating license of Coreform Cubit on a network:  

1. [Download RLM-Server](https://coreform.com/products/downloads/) and install it on the computer that will be the network license server.  
2. Use your product key to activate the floating license. This can be done in two ways:  
    * *Via the activation dialog*  
      * Open a command prompt or terminal window and navigate to the RLM-Server folder.  
      * Run `rlm_activate` to open the RLM Activation window.   
      * Enter your product key and hostname in the appropriate fields and click the "Activate" button.     
    * *Via terminal only*
      * From a terminal run as administrator: `.\rlm_activate --activate <product_key> <hostname>`, where &lt;product_key&gt; is the product key for the license and &lt;hostname&gt; is the name of the server.  
<br>  

3. In either case, the hostname is optional, but will identify the server in your list of licenses in your account's [My Licenses](http://coreform.com/account/licenses) area.)

    ![server active](/images/activate_cmd.jpg)

4. If successful, you will see the following message:  

    ![server active](/images/activate_server_active.jpg)  

5. Restart the server to recognize the new license file. 

    * In a web browser, type [http://localhost:5054](http://localhost:5054) in the address bar to access the Reprise License Server Administration page.   
    * Click  *Reread/Restart Servers* from left sidebar.  
    * Check that the Coreform ISV server is running by clicking *Status*.  
    * Coreform should appear in the ISV Server table.  

    ![ISV Servers table](/images/activateISVServers.jpg)

See [RLM-Server Installation Instructions](/support/activation/cubit/rlmserverinstall2/) for more information.

<!-- {{% /section %}}

{{% section %}} -->

## Install Coreform Cubit  

To setup Coreform Cubit on a network computer:

[Download Coreform Cubit](https://coreform.com/products/downloads/) and install it on each network computer that will run Cubit.  

{{< figure src="../windows.png" width="600">}}
{{< figure src="../mac.png" width="600">}}
{{< figure src="../linux.png" width="600">}}

* Your download will begin immediately. Choose “Save File,” select a folder location, and click “Save.”

* Locate and double-click the downloaded file to launch the installer (look for Coreform-Cubit-“Year of download” (for example 2022.9), followed by .exe, .dmg, .rpm or .deb).

* Follow the prompts to accept the terms of use and begin installation.

* Choose an install location

{{< figure src="../install5Location.png" width="600">}}

* Choose a start menu location

{{< figure src="../install6StartMenu.png" width="600">}}


## Launch Coreform Cubit and Activate   

1. Launch Coreform Cubit 

2. In the Product Activation window, select *Floating: Connect to a license server*. 

3.  Enter the hostname or IP address of the license server where RLM-Server is located. The default port number is 5053.  (See [RLM-Server Installation Instructions](/support/activation/rlmserverinstall) for information about changing the port.) 

    {{< figure src="../floating.png" width="600">}}

4.  Click *Activate*. If successful, you will see the following message: Connection to license sever succeeded,press close to continue.



**Note:** A floating or shared license is only meant to be activated on the machine running the RLM server. Do not use the product key to activate Coreform Cubit on the client computer that is running Coreform Cubit. Instead use the hostname/IP address and the port number of the RLM server. 

If you get an error message during installation, please see [Coreform Cubit FAQs](/products/coreform-cubit/cubit-faqs) for possible solutions. If you have any questions about activating your software, please [contact Coreform support](/company/contact).


<!-- {{% /section %}} -->

















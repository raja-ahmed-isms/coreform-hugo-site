+++
title = "Coreform Cubit Data Translator Activation"
keywords = "coreform cubit learn, activate, activation, free meshing software"
include_contact = false
include_collapse = true
layout = "oldstyle"
featured_image = "/images/par/parOffice2.jpg"
aliases = ["/support/activation/activatetranslator/"]
+++

{{% section %}}

# Coreform Cubit data translator activation

### Node-locked activation  

1. Launch Cubit and select "Product Activation" from the Help menu. 
2. In the Product Activation dialog that appears, select "Node-locked: Use a product key to get a license over the Internet." 
3. Enter your translator product key and your computer's name. Your computer's name will help you identify this license in your Coreform account. Your translator product key is available in your account's [My Licenses](http://coreform.com/account/licenses) area.  
4. Click the "Activate" button. 
5. Restart Coreform Cubit for the data translator to be recognized.

*If your computer is not connected to the Internet, please see [Offline Activation](/support/activation/activateoffline)*


### Floating license activation 

To activate a Cubit Data Translator floating license on a server 


1. Use your translator product key to activate the Data Translator floating license. This can be done in two ways.  
    * *Via the activation dialog* 
      * Open a command prompt or terminal window and navigate to the RLM-Server folder.
      * Run `rlm_activate` to open the RLM Activation window. 
      * Enter your translator product key and hostname in the appropriate fields and click the “Activate” button.
    * *Via terminal only*
      * From a terminal run as administrator `.\rlm_activate --activate <product_key> <hostname>` where &lt;product_key&gt; is the translator product key for the license and &lt;hostname&gt; is the name of the server. 

        ![Activation via terminal](/images/activate_cmd.jpg)

2. In either case, the hostname is optional, but will identify the server in your list of licenses in your account’s [My Licenses](http://coreform.com/account/licenses) area.

3. Restart the RLM server for it to recognize the new license file. In a browser, type [http://localhost:5054/](http://localhost:5054) to access the Reprise License Server Administration page. Click "Reread/Restart Servers" from the left sidebar. (For more information, see [RLM-Server installation instructions](/support/activation/rlmserverinstall) for more information.

4. Restart Coreform Cubit for the data translator to be recognized.

If you get an error message during installation, please see [Coreform Cubit FAQs](/products/coreform-cubit/cubit-faqs) for possible solutions. If you have any questions about activating your software, please [contact Coreform support](/company/contact).

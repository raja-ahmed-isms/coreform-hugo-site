+++
title = "Release Notes for Coreform Cubit 2021.4"
include_collapse = true
layout = "release_notes"
date = "2021-04-22"
description = "In this release, we focused on bug fixes and improving the user experience related to the installation, licensing, and activation of Coreform Cubit."
+++

## Improved packaging/installation for Linux 
We have recently added new capabilities to Coreform Cubit, which resulted in several critical packaging and installation issues for Linux users. We've fixed many of these issues and thoroughly tested on Ubuntu 20.04 and CentOS7. We believe these fixes should address similar issues on other Linux distributions, but if you encounter difficulties with installing Coreform Cubit 2021.4, or notice missing packages, please submit a bug report to support@coreform.com.

## Upgrade instructions for users with an active maintenance plan
- How do you know if you have an active maintenance plan?
  - Go to your Coreform account and look under "My Licenses" to see your licenses.  If your license's expiration date has not yet passed, you have an active maintenance plan.
    - Example of an **active annual license** (maintenance active) \
    <img src="./images/active_annual_license.png" style="zoom: 75%;" />
    - Example of an **active perpetual license** (maintenance active) \
    <img src="./images/active_perpetual_license.png" style="zoom: 75%;" />
    - Example of an **expired annual license** (maintenance *not* active)\
    <img src="./images/expired_annual_license.png" style="zoom: 75%;" />
    - Example of an **expired perpetual license** (maintenance *not* active)\
    <img src="./images/expired_perpetual_license.png" style="zoom: 75%;" />

### Upgrading from Trelis 15, Trelis 16, Trelis 17.0, Trelis 17.1

1) Download the Coreform Cubit 2021.4 installer for your OS from <a href="https://coreform.com/products/downloads/">our publicly-available downloads page</a>
2) Run the installer.
   - You don't need to uninstall previous versions of Trelis or Coreform Cubit as each released version of Coreform Cubit installs in its own unique directory.
3) Follow the license activation instructions <a href="https://coreform.com/support/activation/cubit/activate/">for your situation</a>.
   - **Critical Notes**
      - *All* Coreform Cubit licenses with active manintenance have been automatically upgraded to **2021.3**
      - We understand that some users still need access to Trelis for their existing projects.  If you are one of these users and find that you need a new Trelis license (e.g. bought a new computer or license-server hardware) please contact support@coreform.com to request a new license for your version of Trelis.
      - If you have an *internal* floating license Trelis you cannot host *both* Trelis and Coreform Cubit licenses on the same server. This the result of a hard-coded security feature from our licensing provider, which *cannot* be changed. 
        - *Exception:* If you have Trelis 17.1, we do have a patch that will allow you to host a Trelis 17.1 license and Coreform Cubit licenses on the same server. Find instructions <a href="https://coreform.com/support/downloads">on our support downloads page</a>.
        - *Solution \#1:* Install your new Coreform Cubit license on a separate license server from the server hosting Trelis.
        - *Solution \#2:* Send an email to support@coreform.com asking us to host your licenses on the RLM Cloud (externally hosted license server).

### Upgrading from Coreform Cubit 2020.2

1) Download the Coreform Cubit 2021.4 installer for your OS from <a href="https://coreform.com/products/downloads/">our publicly-available downloads page</a>
2) Run the installer.
   - You don't need to uninstall previous versions of Trelis or Coreform Cubit as each released version of Coreform Cubit installs in its own unique directory.
3) Follow the license activation instructions <a href="https://coreform.com/support/activation/cubit/activate/">for your situation</a>.

### Upgrading from Coreform Cubit 2021.3 (or newer)

1) Download the Coreform Cubit 2021.4 installer for your OS from <a href="https://coreform.com/products/downloads/">our publicly-available downloads page</a>
2) Run the installer.
   - You don't need to uninstall previous versions of Trelis or Coreform Cubit as each released version of Coreform Cubit installs in its own unique directory.
3) Peform *one of the following options* to activate Coreform Cubit 2021.4 
   - *Option \#1:* Copy your license file from your Coreform Cubit 2021.3 directory into your Coreform Cubit 2021.4 directory
     - License files have a `.lic` extenstion and are installed in `./bin/licenses` *within* each Coreform Cubit installation directory: 
   - *Option \#2:* Follow the license activation instructions <a href="https://coreform.com/support/activation/cubit/activate/">for your situation</a>.

## IGES import/export licensing
- As of Coreform Cubit **2020.2** the IGES Data Translator (import & export `.igs`/`.iges` CAD files) is no longer included with the base Coreform Cubit license.  
   - The IGES Data Translator is available as an add-on license, see details <a href="https://coreform.com/products/coreform-cubit/translators/">here</a>
   - For users with active annual or maintenance licenses purchased prior to the Coreform Cubit 2020.2 release, you are entitled to the IGES Data Translator.  Send an email to support@coreform.com to request this license be added to your account, or if you are unsure whether you are eligible.

- We recommend that users who use IGES files explore using either:
   - ACIS (`.sat`) files 
      - Files that are native to Cubit's ACIS geometry kernel
   - STEP (`.stp`, `.step`) files
      - STEP is richer and more robust than IGES. Here are some external links that discuss the differences between IGES and STEP:
         - https://randrmanufacturing.com/blog/iges-vs-step/
         - https://transmagic.com/iges-vs-step/
         - https://www.cadinterop.com/en/by-cad/cad-iges.html

## SDK Documentation
Beginning with the 2021.3 release, we now package the Cubit-SDK with Coreform Cubit. You can find documentation for the SDK by opening the `./docs/Cubit-SDK/index.html` file *within* your Coreform Cubit 2021.4 installation directory. 

## Defects Fixed in Coreform Cubit 2021.4

|      |                                                              |
| - | ------------------------------------------------------------ |
| FIXED | Random Coreform Cubit Learn licensing errors on Linux |
| FIXED | Free surfaces right-click menu should have "delete" not "remove" |
| FIXED | `project curve <id> onto surface <id> imprint`, specifically the `imprint` option, crashes Cubit  |
| FIXED | The `epu` executable that combines Sculpt results for importing into Cubit is broken |
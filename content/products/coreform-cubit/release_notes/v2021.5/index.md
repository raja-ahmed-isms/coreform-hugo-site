+++
title = "Release Notes for Coreform Cubit 2021.5"
include_collapse = true
layout = "release_notes"
date = "2021-05-28"
description = "In this release, we focused on bug fixes and improving the user experience related to the installation, licensing, and activation of Coreform Cubit."
+++



## Features added in Coreform Cubit 2021.5

*  Show EULA on Cubit startup
    * Our installers were not showing the license agreement consistently across platforms, so we've opted to show the license agreement the first time a user starts up Cubit. Accepting the EULA will be remembered across all new installations of Coreform Cubit.
      - Power-users should be aware that the user's acceptance is stored in the `Cubit.ini` file within the user's directory. Deleting the `Cubit.ini` file will require re-accepting the EULA at the next startup.
*  Store licenses in a version-agnostic writable location
    * Coreform Cubit will now read license files in the bin/licenses folder and in a licenses folder in the user data folder.  Newly activated licenses will get placed in the user data folder by default.  This makes it so that when a user upgrades Coreform Cubit, they don't have to copy license files over to the new installation directory, simply install the latest version of Coreform Cubit and start meshing! These new directories can be found:
        - Linux: `/home/<username>/.config/Coreform/licenses/`
        - MacOS: `/home/Users/<username>/Library/Preferences/Coreform/licenses/`
        - Windows: `C:\Users\<username>\AppData\Local\Coreform\Cubit\Coreform\licenses\`
    * Here is an example of these two *equally-valid* license directories on Windows.
        - Example of license in the installation directory :
        <img src="./images/licenses_install_dir.png" style="zoom: 75%;" />
        - ***[New]*** Example of license in the user data directory on Windows: 
        <img src="./images/licenses_user_dir.png" style="zoom: 75%;" />

## Installation Reminder!
As a reminder for Mac users, there's a **critical** README file in the DMG installer (see image).  When installing Corefrom Cubit on OS X, you MUST FIRST run the following commands in the terminal in order to initialize our license manager's temporary directory. If these commands aren't run, Coreform Cubit will not be able to read your license:

```
sudo mkdir "/Library/Application Support/Reprise"
sudo chmod 777 "/Library/Application Support/Reprise"
```

<img src="./images/mac_install_dmg.png" style="zoom: 75%;" />

## Defects fixed in Coreform Cubit 2021.5

*  ***[FIXED]*** Accidentally included alpha-feature "simulation cards" panel
    * In previous releases, we inadvertently released a alpha-feature that we're testing with our strategic partners
*  ***[FIXED]*** Removed EULA from Windows installer 
    * The EULA is now presented on the first startup of Cubit to make the license behavior consistent across platforms. 
*  ***[FIXED]*** Linux .deb installer not including package dependencies
    * Because we build our `.deb` installer on Centos 7, normal Debian packaging tools aren't available so we translate our `.rpm` installer into a `.deb` using `alien`.  That process was not including package dependencies for Debian distributions. 
*  ***[FIXED]*** Need to automatically install `libOpenGL.so.0` in Linux installers
    * Users installing Coreform Cubit on Linux would get errors about `libOpenGL.so.0` being missing.  That library is now installed along with other graphics libraries in the `.deb` and `.rpm` installers. 
*  ***[FIXED]*** Make sure user data license directory is consistent on Windows
    * This was causing problems with license being placed in the wrong spot for different applications within Cubit 
*  ***[FIXED]*** Improve sculpt licensing
    * Sculpt's parallel-compute licensing had not been updated to fit our new license scheme.  This should reduce erroneous licensing errors from Sculpt. 
    * The expected result is that a single Sculpt license will allow the user to launch a multiple compute-node Sculpt command - a common workflow in high-performance computing environments.  We don't limit the number of compute-nodes that a Sculpt executable can run on, so if you still encounter licensing errors in similar situations, let us know!
*  ***[FIXED]*** Resolve paths in PATH on windows
    * `os.add_dll_directory` was throwing an error if the paths found in PATH on Windows were not absolute file paths 
*  ***[FIXED]*** Import STEP files with Python 3 not working outside GUI
    * Big thanks to our forum user "carlj" for bringing this to our attention <a href="https://forum.coreform.com/t/import-step-files-with-python3-not-working-outside-gui/1084/8"> in this forum post</a>
    * ACIS requires a certain path to be in the PATH environment variable for it to load `SPAXStep.dll` correctly.  Cubit already had logic to do this, but that logic was being skipped when Cubit was being imported into Python.  This change makes sure PATH is always set correctly. 
*  ***[FIXED]*** Changing script directory feature not working properly.
    * If the change to script directory option is on, the working directory is changed to the script directory when playback starts. The working directory is changed back when playback finishes. Changing the directory back was not working. Now it is. 
*  ***[FIXED]*** Exodus import string missing a space on feature
    * Most of the strings assumed a leading space. Adjusted a couple of strings for spaces. There may be two spaces in a couple of places, but that is better than missing a space and the command failing. 
*  ***[FIXED]*** Cubit crashes sometimes during "Product Activation" menu command
    * There was a bug introduced with the changes to allow multiple RLM directories where if the "Product Activation" menu command was used, Cubit would crash. 
*  ***[FIXED]*** Tetmesh option "Min Tet Layers" GUI setting doesn't work
    * The GUI was not adding the tetmesh scheme's `proximity layers` option when the `Min Tet Layers` checkbox was selected.
*  ***[FIXED]*** Output from Python commands not being saved to the cubit log file. 
    * Fixed the Python output to get in the log file.
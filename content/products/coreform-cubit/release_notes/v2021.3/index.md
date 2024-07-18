+++
title = "Release Notes for Coreform Cubit 2021.3"
include_collapse = true
layout = "release_notes"
date = "2021-03-10"
description = "This release contains several user experience improvements and bug fixes, as well as access to Sculpt meshing functionality."
+++

## New license file required
Due to the completion of our new versioning scheme (see below), the 2021.3 release will require all users to download a new license file from the "My Licenses" section of their Coreform account. This is a one-time occurrence and you will not need to do this again for the remainder of your license term.

## Version Numbering
Coreform Cubit now follows a <a href="https://calver.org">Calendar Versioning scheme of YYYY.MM</a>.  This versioning scheme was hinted at in the previous release of Coreform Cubit (2020.2), but now is fully in-place.  We intend to release new versions of Coreform Cubit with increasing frequency to provide you with quicker access to the latest bug-fixes and new features.  We hope you enjoy the March 2021 release: Coreform Cubit 2021.3!

## New downloads page
We now provide *public* downloads of Coreform Cubit---logging into your user account is no longer required for downloading new versions of Coreform Cubit. All downloaded version still require licensing in order to use, of course. However, we hope this change will make it easier to get both the latest release and developer builds and to include Coreform Cubit in your own automated processes. 

The `Latest Development Builds` section of the public downloads page contains regular updates of builds that have passed our automated tests. This can be helpful for you to try fresh builds that may contain a new feature or bug fix of interest to you. 

<a href="https://coreform.com/products/downloads/">Here's a link to the public downloads page</a>, or find the link on our banner as shown in the image below

<img src="./images/coreform_downloads.png" style="zoom: 67%;" />

## Sculpt is back!
Due to an incompatibility, Sculpt meshing functionality was not included with Coreform Cubit 2020.2. We are now nearing completion of a sustainable, long-term solution to this issue, and in the meantime we've implemented a short-term fix that has enabled the packaging of Sculpt with Coreform Cubit 2021.3.

<img src="./images/sculpt_example.png" style="zoom: 67%;" />

## Meshing
### Hexset command promoted from developer status
This command was created as part of Jason F. Shepherd’s dissertation, <a href="https://doi.org/10.1007/s00366-008-0091-4">Hexahedral Mesh Generation Constraints</a>. The `hexset separate` command is designed to create groups containing hexahedral elements based an enclosing set of triangles.

The command is:
`Hexset Hex <range> Separate Tri <range> [draw]`

For example, given a tri-meshed volume as shown below:

<img src="./images/hexset_1.png" style="zoom: 67%;" />

We can create a bounding box and mesh it with hexes:

<img src="./images/hexset_2.png" style="zoom: 67%;" />

Then, executing the command:
`hexset hex all separate tri in surf 1 2`
results in:

<img src="./images/hexset_3.png" style="zoom: 67%;" />

### Export Mixed Mesh Blocks to ANSYS CDB
The ANSYS CDB format supports multiple element types to be defined within a single element block.  One of our customers found a bug with our CDB exporter in the case pictured below, so we fixed it!  Now you can export meshes to ANSYS CDB that are similar to the single-block, mixed hex-pyramid-tet example shown here. 

<img src="./images/ansys_cdb_mixedmesh.png" style="zoom: 50%;" />

## Graphical User Interface
### Components renamed to Plugins
We're doing some exciting work to enable new functionality in Coreform Cubit.  So far we've been layering these capabilities on top of Coreform Cubit, using the `Components` functionality.  However, as we've been working we realized that this was a confusing name for a functionality that everyone else understands to be a plugin.  So we've gone ahead and renamed this functionality `Plugins`.  This is most clearly seen to the end user through the taskbar, as pictured below.

<img src="./images/plugins_taskbar.png" style="zoom: 67%;" />

<img src="./images/plugins_window.png" style="zoom: 67%;" />


### Added Button to Return Colors to Defaults
We added a **Reset to Default Colors** button to the **Global Geometry Colors** options panel, allowing you to easily return to Cubit's default geometry colors.

<img src="./images/colors_user.png" style="zoom: 67%;" />

<img src="./images/colors_default.png" style="zoom: 67%;" />


## Defects Fixed in Coreform Cubit 2021.3

|      |                                                              |
| - | ------------------------------------------------------------ |
| FIXED | Cyrillic and unicode characters in file name paths causes errors |
| FIXED | At least one command in Cubit isn't executing "undo group END" |
| FIXED | empty DLIList: File cannot be saved, critical error |
| FIXED | Superfluous packages arguing with Linux system libraries |
| FIXED | Hard crash on boolean unite with groups |
| FIXED | Cubit not reading & executing .cubit file |

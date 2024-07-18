+++
title = "Release Notes for Coreform Cubit 2023.4"
include_collapse = true
layout = "release_notes"
date = "2023-04-18"
description = "Coreform Cubit 2023.4 is now released, with a focus on increased interoperability, improved user experience, and feature polish."
+++


## Interoperability

### Coreform Cubit is now compatible with all Python versions 3.7 and higher.
Coreform Cubit can now be imported into any Python version from 3.7 and up.  This means that users are no longer tied to the version of Python that ships with Coreform Cubit.

### ACIS 2023 upgrade
The ACIS kernel in Coreform Cubit has been upgraded to ACIS 2023. This new version contains bug fixes, improved error reporting, and also offers updates to the Coreform Cubit data translators.

_Note:_  ACIS updates often mean that Cubit geometry IDs may change. No ID changes caused by this update have been found to date in our test suite, but please be aware that existing journal files which reference geometry IDs may break.

### LS-DYNA import
The following keywords are supported by new LS-DYNA import options:
{{% content %}}  
- `NODE`
- `PART`
- `ELEMENT_SOLID`
- `ELEMENT_SHELL`
- `ELEMENT_BEAM`
{{% / content %}} 

### Gambit export
The Gambit export file header was modified to align with the Gambit file format specification.

### Bulk data export option for Abaqus and LS-DYNA
The advanced export options now default to **Write Mesh Only File**. This will export nodes, elements, node groups (nodesets), and element groups (blocks and sidesets). It will not export template data for headers, materials, boundary conditions, etc. This file can be included into a user's own template file. The command line export option `mesh_only` is used to export data in this format.

<!-- ZZ GET FILE -->
{{% figure src="images/adv-export-options.png" width="279" height="300" caption="Advanced export options now defaults to Write Mesh Only File." %}}

### LS-DYNA pyramid export
Support has been added for pyramid elements in LS-DYNA exports.


<!-- ======================== -->
## Improved user experience

### Command line license activation
Users can now opt to activate their Coreform Cubit license via command line rather than through the Coreform Cubit GUI dialogue.

### Improved documentation
- Documentation at coreform.com has improved images and navigation. 
- New tip of the day content added for the `draw [add remove]` command.


<!-- ======================== -->
## Feature polish


### Python improvements
- The `__name__` variable in Python is defined as `__coreformcubit__` when running a script from inside Coreform Cubit. The external script usage remains the same as before (i.e., `__name__` == `__main__`).
- The Python function `is_periodic` provides a more accurate determination of periodic surfaces. 
- **Tools > Options > General >Change to Script Directory** was fixed to work correctly on Windows.

### Mesh and geometric entity measurement
Cubit now correctly handles measuring between mesh entities and geometric entities.

### Boundary layer bug fix
There previously was an error creating boundary layers in blocks using higher-order elements. A user can now specify the block element type and then correctly create a boundary layer using higher-order elements.

### Align using vertex bug fix
Specifying duplicate vertices to define a rotation axis caused a premature abort in the command and broke undo. The case is now captured and an appropriate error message is displayed.

### Three-step align bug fix
The three-step align algorithm requires three independent vectors. Previously, if the vectors were linearly dependent, the resulting transformation created incorrect relationships between vertices and curves. Cubit now catches this case and gives an error message about incorrect input.

### Spline surface merge bug fix
Resolved an issue merging spline surfaces containing a point curve.

### Webcut offset robustness improvement
The webcut offset command is now more robust on complex offset surfaces at larger offset distances. This change removes extraneous small curves and creates offset volumes that are mergeable and meshable. An example is shown in 
<!-- ZZ FIGREF -->

<!-- CHANGE FIGURE PARAMS -->
{{% figure src="images/improved-webcut-offset.png" width="512" height="453" caption="Quarter-piston example showing improved webcut offset robustness." %}}


*Note:* Some of the created volumes in this example are not meshable with autoscheme and still require manual specification of meshing schemes. Future work will address this issue. Also, note that the mesh in the boundary area must be fine enough to allow the insertion of pyramids in thin sections to provide a transition between the hex/tet mesh regions. (Alternatively, wedge elements may be defined in the boundary region, eliminating the need for pyramids.)

### Improved performance of autosize in large assemblies
Some small spline surfaces previously required multiple iterations to converge to an accurate surface area, resulting in long computation times for the autosize algorithm for parts or assemblies that contain these types of surfaces. Changes were made in this release to address this issue. Example performance improvements are provided in the table below, showing modest improvements across the board and steeper reductions in computation time for geometries that contain the problematic small spline surfaces.


| \# of Bodies | \# of Surfaces | Autosize time in 2022.3 (sec) | Autosize time in 2023.4 (sec) |
| ---- | ----------- | -------------- | -------------- |
| 1 solid | 1160 | 0.61 | 0.35 |
| 2 solids | 1495 | .91 | .73 |
| 5 sheet bodies | 1495 | 1.67 | 0.20 |
| 1 solid | 4186 | 1.94 | 1.82 |
| 18 solids | 45704 | 23.15 | 21.20 |
<!-- Table1: Performance improvements for autosize in large assemblies -->

### CAD file import support
Updated CAD import formats supported by Cubit are given in the table below:

| CAD format | Supported Versions |
| ---- | ------------------------------------------------------------ |
| CATIA V4 | 4.1.9 - 4.2.4 |
| CATIA | V5 R8 to V5-6 R2023 |
| JT | JT 8.x, 9.x, 10, 10.1, 10.2, 10.3, 10.5, 10.6, and 10.7  |
| NX | 11 - NX 2212 |
| Parasolid | 9.0 - 35.0.149 |
| ProE/Creo | 16  - Creo 9.0 |
| SolidWorks | 98 - 2023 |
<!-- Table2: Supported CAD import formats -->


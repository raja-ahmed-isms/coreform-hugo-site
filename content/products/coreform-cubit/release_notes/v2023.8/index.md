+++
title = "Release Notes for Coreform Cubit 2023.8"
include_collapse = true
layout = "release_notes"
date = "2023-08-28"
description = "Coreform is pleased to announce Coreform Cubit 2023.8, which features many new capabilities and improvements, including machine learning-driven diagnostics and geometry classification, new tools for facilitating electromagnetic modeling,  increased Python support, and enhancements to Power Tools and Sculpt GUI controls. "
+++


## Geometry

### Part classification using machine learning (ML)
The classify command has been updated to support both volumes and surfaces. To classify a surface, use a command such as `classify surface '<surface_category>'`, which will generate training data and store it on disk with label `'<surface_category>'`. To predict the categorization, you would use `classify surface <id>`. The resulting classification prediction will be displayed in the output window, along with the confidence values for each category. The classify command also supports the  `with`  keyword when used with extended parsing, enabling the identification of surfaces with a specified category using a command such as `draw surface all with category 'surface_category'`

For more information see: [ML Part Classification](https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=environment_control/entity_selection_and_filtering/ml_part_classification.htm)


### Reinforcement learning for thin volume reduction
Cubit now introduces a new feature, Reduce Thin Volumes with Reinforcement Learning. The RL option simplifies a 3D thin volume into connected sheet bodies, similar to the Reduce Thin Volumes Auto. However, instead of relying on a set of pre-defined rules, the RL option uses machine learning techniques to make predictions based on a persistent knowledge base. With each iteration, the method builds training data and improves its choices for reduce operations.

The RL option generates a sequence of Cubit commands that can be exported to a journal file for the user to validate, edit and archive. The resulting sheet bodies are automatically associated with the original 3D geometry and have attributes necessary for shell finite element analysis.

The RL tool operates in two modes: Predict Mode and Learning Mode. In Predict Mode, the method predicts the reduction operations based on established training data. In Learning Mode, the method gathers information about the state space of the assembly to establish training data. The method will stop based on a stopping criteria, reaching the maximum number of iterations, finding no additional unique solutions, or user abort. The user can also influence the learning process by adding their own training data through the Thin Volumes diagnostic in the Cubit Geometry Power Tool.

For more information see: [Reduce Thin Volumes with Reinforcement Learning](https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=geometry/cleanup_and_defeaturing/reduce/reduce_thin_rl.htm)


### New reduce surface slot command
The reduce surface slot command is used to decompose slot surfaces in Electromagnetic (EM) modeling for easier application of boundary conditions. It also provides for additional options for preparing input for the external NGS Morph mesher.

{{% content %}}

```
    reduce surface slot [hardware volume ] [radius ] [group ] [group_edges ][group_inner_edges ] [group_outer_edges ] [name ][name_edges ] [name_inner_edges ][name_outer_edges ] [make_free_curves] [preview]
```

{{% / content %}}

The reduce surface slot command is enhanced with the addition of the geometry power tool's slot surfaces diagnostic, which allows for identification of slot surfaces and fastener hardware through machine learning. The new reduce slot surface panel provides a convenient way to set up the command and visually preview the decomposition of slot surfaces using graphical representation.

For more information see: [Reduce Slot](https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=geometry/cleanup_and_defeaturing/reduce/reduce_slot.htm)


### New blunt tangency command
A new blunt tangency command uses tweaking to increase a surface's radius, increasing small angles at geometric tangencies. It is also equipped with a tolerance parameter parameter to prevent creation of sliver geometry.

```
    blunt tangency tweak vertex [angle] [tolerance] [preview]
```

For more information see: [Reduce Thin Volumes](https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=geometry/cleanup_and_defeaturing/reduce/reduce_thin_volumes.htm), [Blunt Tangency](https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=geometry/cleanup_and_defeaturing/blunt_tangency.htm)

{{% figure src="images/blunt_tangency_tweak.png" width="527" height="257" caption="Example of before and after blunt tangency tweak." %}}


### Additional SGM support in Cubit
Support for the SGM geometry library in Cubit has been expanded:

{{% content %}}
- Names can now be assigned, removed, and renamed for SGM geometry entities.
- SGM geometry can be exported as either an SGM file or a STEP file.
- The `Measure Between` command now supports measuring with SGM geometry entities.
{{% / content %}}

This limited support enables a couple of useful workflows:

{{% content %}}
1. Importing with SGM provides powerful geometry healing, after which the healed geometry can be exported to a STEP or SGM file for subsequent use in Cubit, Morph, or other programs. Names can optionally be assigned to SGM entities, and the names will be written to the STEP or SGM file.
1. SGM geometry can be imported along with free mesh for visualization and to measure distances between mesh and geometry.
{{% / content %}}

### Fix for STEP import of multi-surface sheet bodies
Multi-surface sheet bodies are now imported from a STEP file as such, instead of each surface created as a single-surface sheet body.


## Meshing

### Robustness and speed improvements to node equivalencing
[Equivalencing nodes](https://coreform.com/cubit_help/cubithelp.htm#t=mesh_generation%2Ffreemesh.htm&rhsearch=equivalence&rhhlterm=equivalence) now avoids potentially collapsing elements if two nodes within an element are within tolerance. Additionally, performance of equivalencing nodes is greatly improved for larger meshes.

### Element area and length metrics now correct for all higher-order elements
The [element area](https://coreform.com/cubit_help/cubithelp.htm#t=mesh_generation%2Fmesh_quality_assessment%2Fquadrilateral_metrics.htm&rhsearch=element%20area&rhhlterm=elements%20element%20area) and [length quality](https://coreform.com/cubit_help/cubithelp.htm#t=mesh_generation%2Fmesh_quality_assessment%2Fedge_length.htm&rhsearch=length%20quality&rhhlterm=length%20quality) metrics for 2D and 1D elements respectively did not previously consider the higher-order nodes in the computation, instead behaving as if the elements were linear. This has been corrected so that higher-order nodes are now considered.

### Expanded Sculpt command line
The Sculpt command line in Cubit has been significantly enhanced to further support Sculpt's extensive capabilities. Sculpt is a powerful external application for generating all-hex meshes in parallel, and can be run either standalone from a terminal window or through Cubit, taking advantage of Cubit's powerful geometry tools and visualization capabilities.

The expanded Sculpt command line now supports the same syntax used in the Sculpt input file. The command is still backwards compatible, but the expansion enhances its capability to support a wider range of options in Sculpt.

The new Sculpt command is fully supported in the Cubit command panels providing full interactive support for most options in Sculpt, making it easier for users to set up and execute Sculpt commands within the Cubit environment.

For more information see: [Sculpt Command Reference](https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=mesh_generation/meshing_schemes/parallel/sculpt.htm)


### Scaling options in Sculpt
Sculpt has added a new feature to allow scaling of geometry in different directions. The old scale option has been deprecated and replaced with  `xscale` ,  `yscale ` and  `zscale ` options. The ability to specify different scale factors in x, y, and z directions is now available. If you wish to use the previous constant scale behavior, set  `xscale` ,  `yscale ` and  `zscale ` to the same value.

For more information see: [Sculpt Mesh Transformations](https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=mesh_generation/meshing_schemes/parallel/sculpt_transformations.htm)

### Compressing material IDs from SPN files in Sculpt
A new feature has been added to Sculpt which changes the default behavior of material IDs in the resulting mesh file. Instead of compressing the material IDs found in the <tt>.spn</tt> file, the actual material IDs will now be used for block IDs in the resulting mesh file. If the previous behavior is desired, the user can include the option  `compress_spn_ids=true`.

For more information see: <a href="https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=mesh_generation/meshing_schemes/parallel/sculpt_input.htm#compress_spn_ids">Sculpt Input > <tt>compress_spn_ids</tt></a>

### Improved handling of Stitch format in Sculpt
The  `input_stitch ` option in Sculpt now automatically extracts the intervals from a stitch file when reading it. The previously required specification of the global Cartesian intervals is no longer necessary and will be ignored if provided with the input_stitch option. This change streamlines the input process and eliminates the need for manual specification of the intervals.

For more information see: <a href="https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=mesh_generation/meshing_schemes/parallel/sculpt_input.htm#input_stitch">Sculpt Input > <tt>input_stitch</tt></a>

### Default processors in Sculpt command line
The default number of processors used for Sculpt mesh generation has been changed. The new default is now set to the number of available processors, up to a maximum of 16, if the user does not specify a number. 
The previous default was set to 4 processors.

For more information see: <a href="https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=mesh_generation/meshing_schemes/parallel/sculpt_process_control.htm#num_procs">Sculpt Process Control</a>


## Graphical User Interface

### Webcut solutions in meshing power tool
Introducing a new addition to the Meshing Power Tool  --  a solutions window that displays potential webcuts for the selected volume. With a similar layout to the Geometry Power Tool, you can preview the webcut, and with a right-click on the displayed solutions, access a menu option that brings up the relevant webcut command panel, pre-populated with the necessary parameters. To activate the solutions window, simply check the "Show Webcut Solutions" checkbox at the top of the Meshing Power Tool.

{{% figure src="images/meshing_power_tool.png" width="531" height="432" caption="Meshing power tool shown with new solutions window." %}}

For more information see: [Meshing Tools](https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=environment_control/gui/tree_view/meshing_tools.htm)

### New shell model management with the geometry power tool
Cubit's Geometry Power Tool has been enhanced with the new Beam and Shell diagnostic, making it easier to simplify thin volume assemblies. This diagnostic streamlines the reduction of volumes to connected sheet bodies, which can then be meshed with triangles or quadrilaterals. When run with machine learning models loaded, the Analyze button produces two lists: **Thin Volumes** and **Sheets**. The **Thin Volumes** list offers custom reduction options, while the **Sheets** list displays defined sheet bodies with editable thickness, loft, and block attributes. The diagnostic also provides access to custom solutions for further customization and the option to export sheet data for analysis input deck setup. Additionally, quick access to automatic thin volume reduction tools, including reinforcement learning, is also available.

{{% figure src="images/shell_power_tool.png" width="504" height="554" caption="(Left) Thin Volumes diagnostic showing volume with possible reduction solutions. (Right) Sheets diagnostic showing reduced volumes along with possible solutions for connecting adjacent sheets." %}}


{{% figure src="images/shell_modeling-1024x212.png" width="682" height="141" caption="Example thin volume (left) and preview of two possible reduction solutions (right)." %}}
<!-- 682 141 -->

For more information see: [Beams and Shells](https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=environment_control/gui/tree_view/beams_and_shells.htm)

### Geometry power tool new classify surfaces diagnostic
A new diagnostic for surface classification has been added to the Geometry Power Tool. This diagnostic allows for classification of surfaces into custom categories, similar to the existing part classification feature. The classification is performed using machine learning models that can be loaded through the **Load ML Models** button in the Options panel. Upon analysis, each surface in the model will be assigned a predicted category.

For more information see: [Machine learning with Geometry Power Tool](https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=environment_control/gui/tree_view/machine_learning.htm)

### Geometry power tool new slot surfaces diagnostic
The Slot Surfaces diagnostic is a new addition to the geometry power tool for preparing slot surfaces in electromagnetic (EM) modeling. A slot surface is used to identify potential pathways where EM radiation can potentially exit. This diagnostic will help manage slot surfaces and prepare them for the application of boundary conditions and analysis. The diagnostic utilizes machine learning to predict the most likely slot surfaces. It also provides interactive tools for easily previewing and preparing slot surfaces for meshing using the external NGS Morph tool.

{{% figure src="images/slot_gui-1024x612.png" width="543" height="324" caption="(Left) Example slot surface showing preview of decomposition. (Center) Geometry Power Tool with Slot Surface Diagnostic showing slot surfaces identified with machine learning. (Right) Reduce Surface Slot command panel." %}}


For more information see: [Slot Surface Preparation](https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=environment_control/gui/tree_view/slot_surfaces.htm), [Reduce Slot Surface](https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=geometry/cleanup_and_defeaturing/reduce/reduce_slot.htm)


### New reduce thin volumes with reinforcement learning command panel
The new **Reduce Volume Thin RL** command panel is a valuable addition to the suite of reduce operations that utilizes the latest in machine learning technology. This panel offers full access to the training and prediction of thin volume models using the new command line option for reducing thin volumes. When used in conjunction with the "Beams and Shells" diagnostic from the geometry power tool, the relevant volumes are automatically populated in the command panel, making it easily accessible from a context menu.

{{% figure src="images/RL-command-panel.png" width="285" height="496" caption="New Reinforcement Learning command panel." %}}


For more information see: [Reduce Thin Volumes](https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=geometry/cleanup_and_defeaturing/reduce/reduce_thin_rl.htm)


### New reduce surface slot command panel
The `reduce surface slot` command panel is a new addition to the suite of `reduce` operations that utilizes machine learning capabilities. When used in conjunction with the geometry power tool slot surfaces diagnostic, the slot decomposition process is made easier with the help of the new `reduce surface slot` panel. The panel provides options to input the hardware and radius information, automatically populating the necessary fields based on the slot geometry. Additionally, users have the ability to specify grouping and naming options for the decomposed slot surfaces.

For more information see: [Reduce Slot Surface](https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=geometry/cleanup_and_defeaturing/reduce/reduce_slot.htm)


### New Sculpt command panels
Cubit introduces new command panels for setting up Sculpt input, providing better support for the full range of Sculpt capabilities. The panels are organized based on the structure of the Sculpt input options and provide a comprehensive set of options that were previously only available when running Sculpt external to Cubit.

The new panels can be accessed from the <strong>Meshing > Volume > Sculpt</strong> menu and cover the following categories:

{{% content %}}
- **Process Control:** includes specifications for the number of processors, file names, and run options. 
- **Overlay Grid:** allows you to set up either a Cartesian overlay grid or specify an Exodus mesh as the overlay grid. It also supports Pamgen overlay grids.
- **Input Geometry:** defines the geometry that will be used, with the default being the current geometry in Cubit. Alternative sources for geometry include external STL files, diatom files, volume fraction files, microstructure files, SPN files, or stitch files.
- **Mesh Options:** control the type of mesh including stair-step, mesh void, hex dominant, periodic and degenerate hexes.
- **Smoothing Options:** control options for smoothing curves and surfaces.
- **Mesh Improvement Options:** includes options for pillowing, defeaturing, geometry capture, thickening, and removing bad-quality elements.
- **Adaptivity Options:** specify adaptive criteria, including the recent addition of adaptation based on material ID.
- **Boundary Conditions:** choose from several methods for generating sidesets, defining material and sideset names, and adding new boundary conditions.
- **Output Options:** set the output Exodus file name and define options for transformations, exporting various file types, and log output.
{{% / content %}}

In addition, the Sculpt panel now includes a new <strong>Preview</strong> button that displays the contents of the Sculpt input file in the Cubit output window. This allows you to check the current structure of the commands and to copy and paste to a text file for running externally.

{{% figure src="images/sculpt_gui_panels-1024x782.png" width="530" height="404" caption="New Sculpt command panels showing the nine different categories of options." %}}


For more information see: [Sculpt Commands](https://coreform.com/manuals/tagged/2023.8/cubithelp.htm#t=mesh_generation/meshing_schemes/parallel/sculpt_commands.htm)


## Miscellaneous

### Python 3.6 through Python 3.11 now supported
Easier integration with user's workflows, tools, and scripts is now enabled by supporting more than just Python 3.7. Cubit's Python modules can now be imported into any Python environment of version 3.6 and newer, and Cubit ships now with Python 3.10. This is enabled by Cubit's use of the Python Stable Application Binary Interface.

### <tt>Element_count</tt> extended parsing now supports Exodus entities
When using  `element_count`  in extended parsing, Exodus entities are now supported. For example, the following command now works: `list block with element_count > 2400`


### Tet meshing errors now correct
Error messages generated during tetmeshing previously contained incorrect node IDs. This has been corrected, allowing users to quickly locate areas in the triangle mesh that are preventing tetmeshing from succeeding.

### Cubit's Python interface enhancements
{{% content %}} 
- `gaps_between_volumes`  --  Returns a list of VolumeGap objects defining the gaps
- `is_surface_meshable`  --  Returns whether a surface is meshable with the current scheme
- `get_entity_names`  --  Returns all names of an entity
{{% / content %}} 

## Cubit bug fixes and enhancements

### Defects Fixed in Coreform Cubit 2023.8

| Ref # | Description                                                  |
| ---- | ------------------------------------------------------------ |
|  MESH-817  |  Re-enable testing with graphics on the linux build machine. |
|  MESH-4173  |  `keep_lower_geometry` not recognized  --  remove option from GUI. |
|  MESH-5135  |  Refine in Sculpt based on geometry ID. |
|  MESH-6669  |  Python 3.9+ compatibility. |
|  MESH-6670  |  Running Sculpt 16.04 on a mac. |
|  MESH-6680  |  Open Command Panel if closed and power tool links hit. |
|  MESH-6682  |  SGM STEP import/export in Cubit. |
|  MESH-6695  |  Incorrect journaled command. |
|  MESH-6742  |  Support multiple versions of Python. |
|  MESH-6749  |  Investigate mesa issue on persistent DaaS machines. |
|  MESH-6852  |  Wrong node IDs in tetmeshing error messages. |
|  MESH-6877  |  Rework genSlotModel with SGM and Cubit. |
|  MESH-6878  |  Meshed surface area of tri6 incorrect. |
|  MESH-6900  |  Better handling of reversals in sweeping and interval matching. Using mesh angle for vertex type. |
|  MESH-6909  |  Long delays after Cubit unloads. |
|  MESH-6911  |  `parse_cubit_list` error. |
|  MESH-6948  |  Columnwidth option. |
|  MESH-6951  |  Undocumented command  --  Remove Sliver Curve. |
|  MESH-6962  |  Cubit 16.08 bug in GUI model tree. |
|  MESH-6972  |  Tetmesh issue during training. |
|  MESH-6975  |  Sculpt version in cmake list. |
|  MESH-7097  |  Sculpt parallel command when hitting preview input. |
|  MESH-7105  |  Bug when projecting then imprinting curves onto composite surface. |
|  MESH-7107  |  Super vs. Superelement_topology. |
|  MESH-7118  |  Support for `element_count` to use element blocks, sidesets and nodesets. |
|  MESH-7119  |  Simple volume should be automatically sweepable. |
|  MESH-7121  |  Fix recent regression in create curve polyline with locations. |
|  MESH-7134  |  Improvements in sweeping quality.  |


+++
title = "Release Notes for Coreform Cubit 2020.2"
include_collapse = true
layout = "release_notes"
date = "2020-11-10"
+++

## Summary
This is the first release of the Coreform Cubit branding of our meshing software, which was formerly known as Trelis.

## Graphical User Interface

### New Geometry Power Tool Diagnostics and Solutions

<img src="./images/geom_power_tool1.png" style="zoom: 67%;" ><img src="./images/geom_power_tool2.png" title="Geometry Power Tool Options Panel" style="zoom: 67%;" />



The Geometry power tool includes interactive diagnostics and solutions for defeaturing and repairing CAD models. New diagnostic tests have been added along with rapid solutions for repair. Updated and expanded diagnostics categories include Traits and Assembly Checks.

* **Traits**- This category includes tests for common surface configurations in a geometric volume. This release introduces new and improved Traits diagnostic tests:
   * New **Holes** and **Chamfer Chains** diagnostics tests allow for rapid identification of these common characteristics so users can quickly remove or verify them. These tests identify sorted lists of collections of surfaces that can be expanded and visualized. Collections of Holes and Chamfer chains are sorted by radius and thickness respectively. New associated context solutions also provide for fast removal of selected collections of surfaces.
   * The **Blend Chains** diagnostic test replaces the Blends diagnostic. Blend Chains now identifies sorted collections of blends that are associated with the same blend chain. This allows rapid identification and removal with a single command.
* **Assembly Checks**- The new Assembly Checks category of diagnostic replaces Overlap Checks. It consolidates all diagnostics for checking interactions between volumes in an assembly into one category and adds additional valuable tools for resolving issues prior to imprinting, merging and meshing.
   * The new **Volume Gaps** test will display pairs of volumes that are not touching but are closer than the Volume Gap threshold.
   * **Volume Overlaps** is now updated to generate contextual simplified solutions for removing overlaps between pairs of volumes.
   * The new **Volume Misalignments** diagnostic test will identify volumes that are touching but have entities that would otherwise create slivers when imprinting. This also includes new capabilities for quickly visualizing and resolving misalignments between volumes.
   * **Volume Contacts** replaces the **Overlapping Surfaces** diagnostic. It identifies volume pairs that are touching, but have not been merged along with their overlapping surfaces.
   * The new **Mergeable Geometry** diagnostic replaces the individual vertex, curve and surface mergeable categories. This diagnostic includes entities that are coincident and can be merged.
   * A new option for specifying **Tolerant Imprint** and estimating an **Imprint Tolerance** is now provided. Changing the Imprint Tolerance can change the results of the Gap, Overlap and Misalignment diagnostic results. 

### New Command Panel Navigation Method

<img src="./images/breadcrumb1.png" alt="Breadcrumb Navigation for Command Panels" title="Breadcrumb navigation for command panels" style="zoom: 80%;" /><img src="./images/breadcrumb2.png" alt="More Vertical Space for Command Panels" title="More Vertical space for command panels" style="zoom: 80%;" />


A new, optional, method for navigating the command panels has been added. This new option can be toggled on and off under **Tools/Options/Command Panels**. The obvious benefit of using this 'breadcrumb' navigation method is gaining additional vertical space for command panels which will eliminate the need for scrolling in many cases.

### Support for Dark Mode

Support for **Dark Mode** has been added to Cubit 2020.2. 

### Display Volume Overlaps and Neighbors from Context Menu

<img src="./images/overlaps.png" alt="New Context menu options" title="New Context menu to display volume overlaps and nearby volumes" style="zoom: 80%;" />

Two new options are available in the graphics window when volumes are selected:

* **Draw Nearby Volumes** - All volumes close to the selected volume(s) will also be drawn. This is useful when working with large assemblies to view only local parts for defeaturing or boundary condition assignment.
* **Draw Volume Overlaps** - Draws the wireframe representation of the selected volume(s) and any volumes it overlaps. The overlapping region shared by the volumes will be displayed in red

### Tetmesh and Trimesh GUI Command Panels

<img src="./images/tetmesh_surfaces.png" alt="Tetmesh command panel for surface options" title="Tetmesh command panel for surface options" style="zoom: 80%;" /><img src="./images/tetmesh_volumes.png" alt="Tetmesh command panel for volume options" title="Tetmesh command panel for volume options" style="zoom: 80%;" />



A new command panel for setting options for the **tetmesh** scheme is now available supporting all new local and global options for tetrahedral meshing. This panel now includes two separate tabs for setting options for surface meshing and volume mesh options. The **trimesh** panel has also been updated in a similar manner for setting options for the trimesh scheme.

### Reduce Bolts Command Panel

<img src="./images/reduce_panel.png" alt="Reduce Command Panel" title="Reduce Command Panel for Simplifying and Preparing Bolts for Analysis" style="zoom: 80%;" />

The new **Reduce** command (described below) uses a new command panel to rapidly set up parameters to simplify and assign boundary conditions to bolt connectors.  Access the Reduce options in the command panels by clicking on `Mode:Geometry -> Entity:Volume -> Action:Modify` and using the dropdown to select Reduce.

## Meshing

### New Meshing Collapse Commands

The following commands are good for collapsing low-quality tets and triangles. The user specifies a mesh entity to collapse and optionally a metric. The operation attempts to collapse the mesh entity, ensuring that the quality of surviving neighbor mesh entities does not degrade lower than the mesh entity being collapsed. The specified metric is used to determine quality. If not specified, "Scaled Jacobian" is used. If the collapse can not be done without degrading quality, no collapse is performed. To collapse triangles, use the first two commands. Only triangle and tets can be collapsed, not faces and hexes.

```
Collapse Edge <id> [SCALED JACOBIAN | Aspect Ratio | Shape | Shape and Size]
```

```
Collapse Tri <id> [SCALED JACOBIAN | Aspect Ratio | Shape | Shape and Size]
```

```
Collapse Tet <id> [Altitude | Aspect Ratio | Aspect Ratio Gam | Distortion | Inradius | Jacobian | Normalized Inradius | Node Distance | SCALED JACOBIAN | Shape | Shape and Size | Timestep]
```

<img src="./images/collapse.png" alt="Collapsing a Triangle" title="Before and after collapsing a triangle" style="zoom: 80%;" />

### Enhancements to Meshedit Command

The **[keep node<id>]** option indicates which node to collapse the edge to. The **[compress_ids]** option was also added to preserve the previous behavior of compressing the mesh id space after the collapse.

```
 Meshedit Collapse Edge <id> [keep node <id>] [compress_ids]
```

### Mesh Intersection Group Naming

A new option was added to the **Find Mesh Intersection** command to allow naming the group created containing the intersecting mesh. If no group name is specified, the group is named 'mesh_intersect' for 3D elements and 'surf_intersect' for 2D elements.

```
Find Mesh Intersection {Block|Body|Surface|Volume} <id_list>> [with {Block|Body|Surface|Volume} <id_list>] [low <value=0.0001>] [high <value=0.0001>] [exhaustive] [worst <num_worst>] [draw] [log] [group <'name'>]
```

### Normalized Inradius Metric for tet10 Elements

A new quality metric **Normalized Inradius** is now included as a standard option for all quality commands that affect tets and tris. It is intended to measure quality of TETRA10 and TRI6 elements, but can also measure linear (TETRA4, TRI3) elements. While most tet metrics, such as the default Shape, only take into account the 4 corner nodes of the tet, the **Normalized Inradius** uses the ten nodes of the tet. This is especially useful when measuring quality for a coarse tet mesh at curved surfaces where mid-edge-nodes can be projected to geometry distorting the elements. An example of using the new metric in a command to display the mesh quality is as follows:

```
Quality Volume All Normalized Inradius Draw Mesh 
```

### New Node Constraint Options

When tet meshing with TETRA10 elements or setting a block element type to TETRA10, the mid-edge nodes on the surfaces can be projected to follow the geometry. In some cases, those projections can form an invalid or poor quality element when the linear version of the elements would otherwise be acceptable. Previously, CUBIT provided the **set node constraint command** to control mid-edge node projections. The **SMART option** allows for projections only if element quality does not degrade below a quality threshold. This version provides a new tet quality parameter and threshold parameter.

```
Set Node Constraint \[on|off|SMART][tet quality [distortion|NORMALIZED INRADIUS]] [threshold <value=0.15>]
```

With the introduction of the **Normalized Inradius** metric in this release, the tet quality setting can use this new metric as criteria for projecting mid-edge nodes. The new threshold value permits setting of a value at which mid-edge nodes will be straightened if the quality falls below. The node constraint options can also be set in the Options or Preferences dialog under Mesh Defaults and can be saved between CUBIT runs. 

### Parallel Tetrahedral Meshing

With this release, users have access to MeshGem's parallel tet mesher, TetraHPC. Parallel tet meshing is an option in the Tet Mesh Command panel mentioned above. Parallel tet meshing is threaded and will support up to 8 concurrent threads.

## Geometry

### Reduce Command

The **reduce** command prepares a bolt for analysis by quickly breaking down its geometry into its simplest form and applying boundary conditions. The different options allow the bolt to be simplified, decomposed into its component parts, overlap removed and fitted to surrounding geometry, imprinted and merged with surrounding geometry, meshed with a qtri scheme on the plug volume, and more. The full command syntax for the command is:

```
Reduce {volume<ids>} [fit_volume] [webcut [{Head|Shank|BOTH}]]
        [imprint] [merge][qtri] [increment_block_ids] [summary] [diameter <value>]
        [group_id{<value>|Default}] [group_name {<string>|Default}]
        [head_block_id{<value>|Default}] [head_block_name {<string>|Default}]
        [shank_block_id{<value>|Default}][shank_block_name {|Default}]
        [plug_block_id{<value>|Default}] [plug_block_name {<string>|Default}]
        [bolt_block_id{<value>|Default}] [bolt_block_name {<string>|Default}]
```

<img src="./images/reduce1.png" alt="Bolt before reduce command" title="Bolt and surrounding geometry prior to **reduce** operation" style="zoom: 33%;" /><img src="./images/reduce2.png" alt="Bolt after reduce command" title="Bolt and surrounding geometry after **reduce** operation. Bolt has been simplified, overlap removed and fitted to geometry, webcut, imprinted, merged, and blocks automatically defined." style="zoom: 33%;" />



### Groups Persist across Webcut

When a volume or body of a group is split in a webcut operation, the resultant pieces of the webcut will also be in the group.

## Sculpt

### HTET Unstructured Option

Sculpt now provides an option for splitting hexes into tets using an  unstructured method where each hex is subdivided into six tets.   Previously only the structured approach was available which split each  hex into 24 tets.

### Thicken Void

A new option for ensuring physical separation between elements of different material blocks is now provided.  The new **thicken_void** option will insert elements designated as the **void** block material where non-void blocks would otherwise be in contact.

### Better Fitting STL

Using the **capture=5** option in Sculpt, mesh is fitted to the input STL.  Sculpt now associates the grid edges of the reference mesh to the  STL curve geometry.  This is an extra level of association, in addition to the grid faces associated to the STL surface geometry.   The result is better conforming of the mesh to the STL.

### Color Smoothing Improvement

Color smoothing (spot optimization) in sculpt now extends smoothing out one additional layer of nodes after the first smoothing iteration, ensuring better quality.

## Graphics, Utilities, etc.

### Enhancements to Free Entity Selection

The ability to select free surfaces (sheet bodies) has been added to the command:

```
Select Free [surface <id_list>] [curve <id_list>] [vertex <id_list>] [add|remove] 
```

### No More Highlight Color Collisions

The automatic colors that CUBIT assigns to entities have been slightly changed to avoid colors that are too close to the highlight color, which previously made it difficult to see what was highlighted.

### Case Insensitive Names

When naming entities in Cubit, names are now case insensitive.

### New HDF5 file type supported
<img src="./images/cub5-option.png" alt="Save a .cub5 file" title="Saving a .cub5 file" style="zoom: 80%;" />

A new file type for saving the state of the Cubit model has been introduced in Cubit 2020.2. The new file type is **.cub5**. The contents and structure of the new type is exactly the same as a **.trelis** file, but the extension name has been changed. Beginning with Cubit 2020.2, users will be able to read in `.trelis` files, `.cub5` files,  and `.cub` files. Users will be able to export `.cub` files and `.cub5` files, but they won't be able to export `.trelis` files. Over time, Coreform Cubit will deprecate support for `.trelis` files in favor of supporting only `.cub` and `.cub5` files. 

What are the motivations for this change? 

- The brand name **Trelis** is being discontinued. A file extension of `.trelis` will be confusing
- *Coreform Cubit* and *Sandia Cubit* will continue to work towards aligning the two products more closely. Currently, Sandia Cubit does not support import and export of HDF5-based model files. Adding the `.cub5` file extension will enable Sandia's support of HDF5 model files in the future.
- *Coreform Cubit* will continue to fully support the `.cub` file format. A `.cub` file is the non-HDF5, proprietary format used by Cubit. The `.cub` file is freely exchanged between Coreform Cubit and Sandia Cubit assuming the underlying versions of Cubit are equivalent. 

## Defects Fixed in Coreform Cubit 2020.2

|      |                                                              |
| ---- | ------------------------------------------------------------ |
| 2859 | Draw curve all produces error on composited surface          |
| 3103 | Extended selection *.py files not showing up on Mac          |
| 3485 | Spurious warning on cub import                               |
| 3710 | CUBIT™ slow to select with a long command string             |
| 3791 | Draw command outputs commands when nothing to draw           |
| 4045 | Dark mode on mac - model tree font matches the background color |
| 4065 | Model tree needs refresh event after import                  |
| 4116 | Crash - on merge all                                         |
| 4117 | Draw vol not is_meshed" produces ERROR if everything is meshed |
| 4158 | Nastran Export Issue                                         |
| 4310 | Selecting free vertex, curves and surfaces with is_free      |
| 4440 | Crash in RelWithDebInfo mode on Windows                      |
| 4504 | SAW client has flashing graphics on Windows                  |
| 4519 | Case sensitivity not respected in new parser help strings    |
| 4609 | MGT Function                                                 |
| 4615 | Unique_genesis_ids not working with import 'filename.cub'    |
| 4632 | Change to fire ray output                                    |
| 4646 | Have an explicit option to name a new group                  |
| 4648 | Bug with list geometry after volume regularization           |
| 4691 | Old attributes remain when importing cub file                |
| 4694 | Update get_total_bound_box documentation                     |
| 4695 | Node moving (smoothing) bug in CUBIT™ 15.6                   |
| 4697 | Bug with selecting entities in CUBIT™ 15.6                   |
| 4701 | Group_names_ids() missing from documentation                 |
| 4703 | Crash - get_sub_elements                                     |
| 4737 | Nastran exporter broken since 15.4                           |
| 4740 | Segfault in CUBIT™ python module when importing an exodus database |
| 4743 | Extraneous Command Output for volume copying                 |
| 4747 | Add query for material types for all blocks in a CUBIT™ session |
| 4755 | Update netcdf                                                |
| 4768 | Selection not recognised in batch mode                       |
| 4772 | Python 3.7 with CUBIT™-alpha - error loading shared object   |
| 4774 | Draw command outputs commands when nothing to draw           |
| 4786 | Equivolume and equiangle unrecognized                        |
| 4787 | Documentation update - Mesh Refinement                       |
| 4835 | Bug in splitting surface using "close_to"                    |
| 4864 | Typo when trying to export quality metric from a lite mesh   |
| 4866 | Crash - tetmeshing with MeshGems 2.11                        |
| 4959 | Performance issue with recent merge of parser changes        |
| 4974 | Misplacement of higher-order mid-face nodes during smoothing |
|      | Avoid generation of a new volume in surface removal on multiple shell volume |
|      | 'Simplify' command respects vertices now.  Not respected previously |
|      | Hardlines can now be parsed with 'num_parents=1'             |

## Other Enhancements to Coreform Cubit 2020.2

|       |                                                              |
| ----- | ------------------------------------------------------------ |
| Ref # | Description                                                  |
| 205   | Can't have return carriages within for loops in a CUBIT™ python script |
| 380   | Keep volume in group after a webcut and reflections          |
| 618   | Exodus-based surface mesh sizing by function                 |
| 660   | Select surface based on angle                                |
| 3528  | Sierra mesh_scale to use more updated version of CUBIT™      |
| 3535  | Update CUBIT™ component snap shot in Sierra repository       |
| 4627  | Create standalone Sculpt CMake for Alegra                    |
|       | STL import performance improvement                           |
|       | Import Mesh-basd geometry performance improvement on models with large sidesets |
|       | Enhanced meshing of composites.  Better underlying faceting from ACIS |
|       | Improved robustness in blunt tangency command                |
|       | 'Topology check coincident node' works off absolute distance instead of distance between bounding boxes |
|       | Fix for inability to create group in quality command using 'node distance' metric |
|       | Allowing blocks to persist through unite operation with 'include_mesh' option |
|       | Support Dark Mode |
|       | Add a command panel navigation method that minimizes wasted vertical space |

## Limitations Introduced in Coreform Cubit 2020.2

|       |                                           |
| ----- | ----------------------------------------- |
| Ref # | Description                               |
| 3433  | Superelement export not supported         |
|       | CUBIT™ is not supported on RHEL6 machines |

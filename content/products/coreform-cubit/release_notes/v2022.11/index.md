+++
title = "Release Notes for Coreform Cubit 2022.11"
include_collapse = true
layout = "release_notes"
date = "2022-11-21"
description = "Coreform Cubit 2022.11 features a number of enhancements to core workflows, including meshing, geometry modification, and import/export. Additionally, numerous customer-reported bugs were fixed. Coreform Cubit 2022.11 is compatible with Sandia Cubit 16.06."
+++


## Support for Python 2 has been deprecated
Python 2 was sunset on Jan 1, 2020 and does not even receive security bug-fixes. While Python 2 scripts will still work in Coreform Cubit 2022.11, they are no longer officially supported.

*Please note:* Support for Python 2 will be completely removed from Coreform Cubit by no later than January 2023. Users still using Python 2 are encouraged to update to Python 3 as soon as possible.

## Updates to the `build uspline` command
The `build uspline` command no longer supports building mutiple U-splines of different dimensions in the same command.
For example, 
```
build uspline surface 1 volume 1 as 1 2
```
is no longer supported, and users should instead run 

```
build uspline surface 1 as 1 
build uspline volume 1 as 2
```


## Graphical User Interface

### Model tree performance
The GUI model tree has been refactored to improve performance. The new tree is two times faster than the old tree for most change events. It is ten times faster for block, sideset, and nodeset ID changes.

### Increased control of Command panel layout
When using the standard navigation mode in the command panel, the number of buttons per row can now be changed from an Options/Preferences setting.  Increasing the number of buttons per row can allow more space for complex command panels where limited vertical space is available.



## Meshing

### Superelement support
Coreform Cubit now allows importing element blocks of type 'superelement' from an Exodus file. Visualization, picking, and listing of superelement blocks is supported. When visualizing superelements, they appear as a collection of nodes. 
<!-- Coreform Cubit GUI also supports them as shown in the image below.  -->
Coreform Cubit can also export superelement blocks, writing the data just as it was imported.

### Improvements to Exodus sizing
Exodus sizing now has higher accuracy when computing a size from a location. It also has better performance by using an element based search tree.

### Select blocks by element type
Element blocks can now be specified by element type using the command below. The type needs to be in quotes. Using 'hex' will get higher order hex types: 'hex8', 'hex20' etc.
```
block with type <element_type>
```

### Enhanced ability to obtain mesh in blocks, sidesets, and nodesets
Mesh in blocks can be specified directly now, instead of needing to identify the geometry in the block. For example, if a block contains a volume, to get the hexes it used to be necessary to type `hex in volume in block 1`. Now Coreform Cubit can get the hexes with `hex in block 1`.
```
node|edge|element|face|tri|hex|tet|wedge|pyramid in block|sideset|nodeset <ids>
```

### New option in `collapse tet` command
The `collapse tet` command has a new option [Interior]. It forces the collapse to happen on the interior of the volume, instead of on the exterior tets.  It prevents the collapse command from modifying surface triangles.

### Syntax changes for set node constraint command
The syntax for the `set node constraint` command has been slightly modified to prevent user error. The options quality and threshold, which are only relevant to the `smart` option, can now only be set when specifying the `smart` option.
```
set node constraint [on|off|smart [tet quality NORMALIZED INRADIUS|distortion] [threshold <threshold=0.3>]]
```

### Tetmesh Default
MeshGems MG-Tetra HPC (high performance computing) is now the default tetmesher in Coreform Cubit. Meshgems has recently been putting the bulk of their resources, development, and enhancements into this product rather than the previously-used serial version.  With HPC, users can expect to generate higher-quality tet meshes and better adherence to prescribed sizes. The tetmesher may be changed back to the prior serial version using the syntax below:
```
[set] tetmesher hpc ON|off [threads <value=4>]
```

### Triangle remeshing
The ability to remesh a group of triangles has been added with the command:
```
remesh tri <id_range> | [quality <tri_metric> [less than|greater than] <value> ...] [inflate <value>][preview]
```
Remesh commands are a convenient tool to bypass the mesh deletion process and can replace a localized set of deformed elements after analysis.  Similar to the `remesh tet` command, the new `remesh tri` command generates a new triangle mesh after deleting the existing mesh described by the input list of triangles.

### Composite surface meshing
The `trimesh` scheme has been modified to better handle composite surfaces. If a composite surface has underlying geometric surface definitions, the actual surface definitions will be used by MeshGems hyperpatch meshing capability, allowing hidden curves of the composite to be ignored by the mesh.

### Duplicate corner triangles
The `triadvance` meshing scheme has been enhanced to prohibit the creation of coplanar and nearly coplanar triangle creation at sharp, knife-edge corners of surfaces.  If a vertex contains two triangles sharing three nodes, or two triangles that are nearly coplanar, a simple check is done at the end of meshing to see if swapping an edge of one of the triangles will eliminate this condition, creating volume at the tip.

### Minimum edge length
A new command was added to set the minimum edge length for tetmeshing. Setting the minimum edge length prevents other sizing parameters, such as curvature, from producing mesh edges that are too small. However, smaller mesh edges will still be produced if there are any geometry edges smaller than the given size.
```
[set] tetmesher HPC minimum edge length [<value>]
```
### Sizing on discrete curves
Mesh sizing on curves of a discrete surface are now respected during triangle meshing.

### Clean discrete mesh command
An option was added to clean the discrete geometry prior to tri-meshing. This step remeshes the underlying triangles of the discrete geometry surface to improve the representation prior to meshing with user defined sizes. This is useful if the underlying facets representing the discrete surface are not an optimal representation of the surface.
```
[set] trimesher clean discrete mesh on|OFF
```


### Sculpt enhancements
Two new `adapt_type` options have been added: `resample` and `material`. With the resample option, the input volume fraction data from microstructure file formats (`input_micro`, `input_cart_exo` and `input_spn`) is down sampled, averaging volume fraction data across multiple cells according to the `adapt_levels` set. With the material option, refinement is done in cells where the predominant volume fraction is a user specified material ID. To specify that material ID, use the new `adapt_material` option. Multiple materials can be used along with an expasion distance where the material adaptivity will occur.A new `geometry_and_blocks` option has been added to `input_mesh_material`. with this option, the block IDs in the final mesh come from the input genesis blocks and materials from the diatom/STL file.Two new options have been added to use with the `free_surface_sideset` option. The first is the new sheet wear method. This option wears a swept input mesh in layers. The second is the `crack_min_element_thickness`, which is used with the sheet wear method. It defines the minimum allowed thickness of the elements resolving the side of a crack. The new `input_stitch` file format allows Sculpt to read a stitch file. Stitch is a new I/O system that has been added to Sandia's SPPARKS (Stochastic Parallel PARticle Kinetic Simulator) tool.  See `dump stitch` and `set stitch` for more details.  See also options `stitch_timestep`, `stitch_timestep_id`, `stitch_field`, and `stitch_info` that support the new `input_stitch` capability.

### Paving with small features
The paving scheme is more robust in cases where mesh size approaches feature size, especially at holes. Improvements have corrected the algorithm from meshing over small holes. Increased element quality is also another benefit.



## Geometry

### Webcut with offset surfaces
A new webcut operation has been added that allows a volume to be cut with surfaces offset from its bounding surfaces. This new feature can be used to separate the mesh of the volume boundary.

### Specify surfaces by normal
Surfaces can now be specified by a normal at the surface's center point with the command below. The tolerance is the maximum distance between the tips of the two normalized vectors: the user input vector and actual surface's normal.
```
surface with normal <x> <y> <z> [tolerance <value>]
```


### Specify surfaces by location with tolerance
A tolerance option has been added to specifying surfaces at a location. Surfaces with a centroid within tolerance to the input location will be selected.
```
surface at <x> <y> <z> [tolerance <value>]
```


### STEP export now writes out names
If users have assigned names to geometry entities in Coreform Cubit, those names will be written into a STEP file upon export. Similarly, upon STEP import those names will be assigned to the entities.

### Restricted stitch command
A `restricted` parameter has been added to the `stitch` command. It allows stitching to operate in a restricted mode, in which only the boundary edges of the sheet bodies or volumes participate in the stitch operation. This improves performance for large models.
```
stitch body|volume <ids> [tolerance <value>] [no_simplify] [no_tighten_gaps] [restricted]
```


### Regularize command keep option
The `regularize` command now has a keep option that allows the specified curves and vertices to survive the operation.
```
regularize body|group|volume|surface|curve|vertex <ids> [keep curve|vertex <ids>]
```

### Imprint with tolerance
A new optional `tolerance` parameter has been added to the volume/vertex imprint command.
```
imprint volume|body <range> [with] vertex <range> [keep] [tolerance <value>]
```

### Include parsing
New options can now be used with the `include` parsing keyword: `similar`, `cavity`, `hole`, `blend_chain`, `chamfer_chain`, `continuous`, and `nearby`. The parser compares the specified entities and includes additional entities that match the criteria. For example:
```
draw volume 1 include nearby
select surface 10 include hole
remove surface 20 include blend_chain

```

## Miscellaneous

### New `draw remove` command
Coreform Cubit has long had the ability to draw a portion of the model with the `draw` command. Geometric entities could subsequently be added to the drawn portion. In this release, a new capability was added allowing for geometric entities to be *removed* from the partially drawn model. This simplifies the commands available when displaying only a portion of the model. *Note:* The new `draw remove` command will not hide a part of the model in normal display mode. It only works when drawing a portion of the model.

### Pyramid element support
The Abaqus exporter will now export pyramid elements, and Nastran importer will now read in pyramid elements.

### OpenFOAM boundary patches
The output of boundary patches for OpenFOAM export has been fixed. It will now write out the patches with the user-assigned names. The wall boundary condition type is now used by the OpenFOAM exporter as well.

### Lite mode improvements
When importing a mesh in lite mode, parsing now supports finding elements attached to nodes.  For example, the following now works.
```
draw hex in node X
```


### New GDF exporter
A new export command has been added to support the GDF (Geographic Data Files) format. The command syntax is:
```
export gdf <filename> <entity_list>|block <range> [ulen <value=1.0>] [gravity <value=9.80665>] [isx <value=0> ] [isy <value=0>] [overwrite]
```

### New OBJ importer
Coreform Cubit is now able to import OBJ files that contain tessellation of polygonal faces.
```
import obj <filename> [feature_angle <value>] [surface_feature_angle <value>] [make_elements]
```

### Importing with block names
The import mesh and import mesh geometry commands have a new `block_name` option. This option allows the user to import only the mesh from the named blocks.

### New CubitInterface functions

| Function Name | Description     |
| ---- | ------------------------------------------------------------ |
| `get_entity_color` | A replacement for `CubitInterface::get_entity_color_index`. |
| `get_volume_wedges` | Returns a list (python tuple) of the wedge ids in a volume. |
| `get_volume_pyramids` | Returns a list (python tuple) of the pyramid ids in a volume. |
| `get_sideset_edges` | Returns a list (python tuple) of the edges in the sideset. |
| `get_node_edges` | Returns a list (python tuple) of edge ids adjacent to the node. |
| `get_quality_values` | Gets the individual quality metrices for a set of elements, instead of a summary. Take advantage of Cubit’s ability to compute quality in parallel. |
| `get_edges_to_swap` | Given a curve defining a knife edge between two triangle meshed surfaces, find edges on the tris along the curve to swap to open up the knife edge. |
| `are_adjacent_curves` | Return whether two or more curves share at least one manifold vertex (common vertex is part of exactly two curves). |
| `snap_locations_to_geometry` | Function has been modified to allow better interoperability with Python. A tuple is now supported for the coordinates of a points and can also be seamlessly used with numpy. |
| `get_similar_curves` | Gets curves with the same length. |
| `get_similar_surfaces` | Gets surfaces with the same area and number of curves. |
| `get_similar_volumes` | Gets volumes with the same volume and number of faces. |
| `get_similar_curves` | Added tolerance option to this existing command. |


## Defects Fixed in Coreform Cubit 2022.11

| Ref&nbsp;#     |   Description     |
| :----: | ------------------------------------------------------------ |
|   ---     | psculpt failed on Windows when running from the command line. The psculpt executable has been modified so that it correctly finds the required libraries on start-up. |
| ---  | Fix sculpt licensing issues in an MPI cluster environment. Sculpt will now run in a clustered environment with a single floating license. Each node will communicate with the master Coreform Cubit session to obtain licensing permission. There is no limit on the number of nodes that can be associated with a Coreform Cubit session. |
|   ---     | Resolve issue with Locate command not working from right-click. |
|   ---     | Show backup files with numbered versions in the Open menu so that they can be opened from the GUI. |
|   ---     | Added warnings for mesh schemes that don’t support boundary layer meshing. |
|   ---     | Fix crash on Mac when double clicking on the .app |
|   ---     | Fixed bug where progress bar persists after ’build lattice’ command is finished. |
|   ---     | Fixed bug in building U-splines on certain sub-mapped volumes. |
|   ---     | Avoids cell set id collisions when building two usplines that share a boundary. |
|   ---     | Fixed bug in boundary layers. |
|   ---     | Nodes can be equivalenced accross volumes. |
| 1637 | Trimesh – slow mesh creation |
| 1672 | Sculpting a thing that isn’t a cube |
| 3041 | Tetmesh scheme volume switches to hex after modification |
| 3433 | Super elements working with no_geom |
| 3508 | Selection by Block Element Type |
| 3862 | Equivalencing nodes owned by volumes |
| 4179 | Auto-pillowing – Default Number of Layers should be 2 |
| 4435 | Alignment command fails to transform mesh |
| 4520 | Sweeping in Cubit when there are holes |
| 5061 | Edges not displayed in wireframe with free_mesh |
| 5364 | Hardcopy with clarox |
| 5504 | Easier hardcopy in both interactive and from workflows |
| 5509 | Importing big step files sometimes doesn’t even work at all |
| 5608 | Tetmesh scheme volume switches to hex after modification |
| 5644 | Select surfaces by normal |
| 5897 | Right click, select face > Draw Normal does nothing |
| 5949 | Unexpected behavior – webcut volume in curve x with plane normal to curve x fraction 1 from start |
| 6300 | Make Tetra-HPC work with sizing functions |
| 6486 | Two complex holes a problem for sweeping |
| 6488 | Inconsistent sweeping results |
| 6491 | Specifying sweep w/ target vector is order-dependent |
| 6533 | Mesh files not closed by lite import |
| 6547 | CRASH – interval matching |
| 6551 | Request to make entity selection more robust |
| 6570 | Import Option > Free Mesh > advanced switches back to Mesh_Geometry |
| 6571 | Enable highlighting of superelement blocks |
| 6572 | Superelement list block |
| 6582 | Fix Slow Paver Issues in dimples model |
| 4529 | STEP import unsuccessful on Mac |
| 5513 | Crash playing journal file |
| 5877 | Paver creates different meshes on windows and linux |
| 5888 | Graphics vs. Nographics mesh difference when running same journal file |
| 5923 | Unable to paste commands into Cubit |
| 5953 | Crash after mbg import and reset |
| 5976 | Crash when creating surface from composite vertices |
| 6268 | Crash when using the ‘x’ key in graphics window |
| 6271 | Long STEP file import times in Cubit |
| 6454 | Crash saving cub file with lite mesh |
| 4610 | Paver fails |
| 5307 | Increase number of user defined colors |
| 5665 | Interval Locking and Linking Surface Interval Matching Failures |
| 5852 | Cubit crashes when trying to select elements from graphics window |
| 5891 | Add option to find duplicate volumes |
| 5893 | Right click select similar, click two parts, only first selected |
| 5894 | Right click select similar – loop issue |
| 5913 | Composite error |
| 5914 | Remove overlap command doesn’t accept id-less signature |
| 520 | Open exodus file in lite or no_geom mode |
| 3930 | Fix for bug in sculpt option ‘mesh_void’ |
| 4337 | Cubit 15.5 and Python 3 on Mac startup issue |
| 4562 | Add support for functional selection set even with graphics disabled |
| 4751 | ‘Unite volume include_mesh’ changes owned entities from volumes to hexes |
| 4773 | HO element type not kept during copy |
| 5026 | ‘set copy_block_on_geometry_copy on’ behavior incorrect |
| 5107 | Fix for hang in spider command |
| 5251 | Coloring spider elements by block |
| 5319 | Add exodus export transformation to GUI |
| 5346 | Losing printed strings with python 3 |
| 5384 | If no cubit.init set Cubit should have a fall back |
| 5385 | Open graphics window from python script |
| 5478 | Added option to remove explicit node map on output |
| 5479 | Crash while meshing or interacting with graphics window |
| 5504 | Easier hardcopy in both interactive and from workflows |
| 5506 | Added unique_genesis_ids as a menu option |
| 5507 | Open file in cubit – make the list of options the same as that from the import command |
| 5538 | Ability to import named block in import mesh command |
| 5564 | Fix for crash when collapsing selected tet |
| 5565 | Performance issue with right click context menu |
| 5595 | Duplicate IDs in model tree – refresh event needed |
| 5614 | Fix ‘find curve overlap’ slowdown |
| 5629 | Nodesets getting returned with sidesets |
| 5632 | Keyword names when importing SAT files |
| 5666 | Python’s "quit()" function in the "clarox" executable causes an error message |
| 5697 | False positive bad Euler number, invalid mesh |
| 5716 | Sideset and nodeset character limit in GUI |

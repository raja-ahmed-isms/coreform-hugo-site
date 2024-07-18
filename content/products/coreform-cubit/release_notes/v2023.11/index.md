+++
title = "Release Notes for Coreform Cubit 2023.11"
include_collapse = true
layout = "release_notes"
date = "2023-11-02"
description = "Coreform is pleased to announce Coreform Cubit 2023.11, which features many new capabilities and improvements improving support for nuclear energy applications.  Enhancements include an exporter to the DAGMC radiation transport code, improvements to the ABAQUS export to support the MCNP radiation transport code, as well as an MCNP importer that reconstructs MCNP models within Coreform Cubit.  A new coarse trimesher setting was added to support producing meshes for radiation transport codes used within the nuclear energy industry."
+++

## Support for Radiation Transport Codes

### DAGMC Export

Direct Accelerated Geometry Monte Carlo ([DAGMC](https://svalinn.github.io/DAGMC/)) is a software package that allows users to perform Monte Carlo radiation transport directly on CAD models.
The DAGMC community previously developed a [Coreform Cubit plugin](https://svalinn.github.io/DAGMC/install/plugin.html).  Coreform Cubit now contains native capability to export DAGMC `.h5m` files.  Users familiar with the original DAGMC plugin will now use Coreform Cubit's native Exodus-based workflows for assigning materials and/or boundary conditions (i.e., `block`, `sideset`, `nodeset`) rather than the `group`-based workflow implemented within the original plugin.

{{% content %}}
```
Export cf_dagmc <'filename'> overwrite 
```
{{% / content %}}

{{% figure src="featured.png" width="780" caption="A tokamak-style nuclear fusion reactor, meshed in Coreform Cubit using the new `coarse` trimesh setting, and ready for export to DAGMC." %}}

For more information see: [Exporting DAGMC](https://coreform.com/manuals/tagged/2023.11/cubithelp.htm#t=finite_element_model%2Fexport%2Fexporting_dagmc.htm)

### MCNP Import
The Monte Carlo N-Particle® ([MCNP®](https://mcnp.lanl.gov/)) code, developed by Los Alamos National Laboratory, can be used for general-purpose transport of many particles including neutrons, photons, electrons, ions, and many other elementary particles, up to 1 TeV/nucleon. The transport of these particles is through a three-dimensional representation of materials defined in a constructive solid geometry, bounded by first-, second-, and fourth-degree user-defined surfaces. In addition, external structured and unstructured meshes can be used to define the problem geometry in a hybrid mode by embedding a mesh within a constructive solid geometry cell, providing an alternate path to defining complex geometry. 

 Coreform Cubit now provides a method for importing MCNP files. The standard MCNP file has a .i extension. This file format contains Constructive Solid Geometry (CSG) models. These models are converted into Boundary Representation (B-Rep) files using the ACIS modeling engine.

{{% content %}}
```
import cf_mcnp <'filename'> [verbose] [debug] [debug_output] [debug_input] [extra_effort] [skip_mats] [skip_merge] [skip_imps] [skip_nums]  [skip_graveyard] [skip_imprint] [uwuw_names] [tol <specific tolerance>]
```
{{% / content %}}

For more information see: [Importing MCNP](https://coreform.com/manuals/tagged/2023.11/cubithelp.htm#t=geometry%2Fimport%2Fimporting_mcnp_files.htm)

### Improved assembly support when exporting to ABAQUS / MCNP

Various enhancements made to ABAQUS export include writing additional element sets and node sets based on groups, specifying coordinate frames, and new ‘instance_per_block’ option. These additions will streamline using Coreform Cubit with MCNP.

{{% content %}}
- Write additional element sets and node sets based on groups
- Documentation improved for specifying coordinate frames
- A new `instance_per_block` option was added for automatic part instancing based on defined blocks
{{% / content %}}

For more information see: [Exporting ABAQUS](https://coreform.com/manuals/tagged/2023.11/cubithelp.htm#t=finite_element_model%2Fexport%2Fexporting_abaqus.htm)


## Geometry

### Simplification of merged surfaces now supported
The simplify command for compositing surfaces now supports merged surfaces in addition to external surfaces.

For more information see: [Simplify Geometry](https://coreform.com/manuals/tagged/2023.11/cubithelp.htm#t=geometry%2Fvirtual_geometry%2Fsimplify.htm)

### Improved behavior for splitting surfaces
Coreform Cubit's previous ability to split fillet surfaces was not ideal, creating a split that looked like a projection of a linear curve. The splitting routine was fixed so that the split follows the curvature of the surface, creating a more expected and ideal result.

{{% figure src="images/surface_splitting-1-1024x357.png" width="1024" height="357" caption="(a) Previous split, (b) New, improved split." %}}

For more information see: [Split Surface](https://coreform.com/manuals/tagged/2023.11/cubithelp.htm#t=geometry%2Fdecomposition%2Fsplitting_geometry%2Fsplit_surface.htm)

### New Reduce Bolt Patch command

The new Reduce Bolt Patch command in Coreform Cubit offers an alternative method for representing bolts in simulations. It replaces bolt geometry with concentric circular surfaces imprinted on the model, centered on the bolt axis. Sidesets are automatically applied to these surfaces, representing preload forces for analysis purposes. By default, the command utilizes the Shigley frustum to determine the radius of the sideset patches, but the desired radii can also be directly designated. Additionally, the resulting patches can be meshed using this command, and sidesets can be customized with incremental IDs and names.


{{% figure src="images/reduce-bolt-patch-1-1024x549.png" width="768" height="412" caption="Two bolts showing before and after the *reduce bolt patch* command was applied." %}}


The *reduce bolt patch* command is one of several options for reducing fastener geometry for use in analysis. This option has also been added to the geometry power tool machine learning tools for classifying geometry. When designated as a bolt, additional solution options are presented that now include the `reduce bolt patch` command.

A new command panel for setting up and previewing options for the *reduce bolt patch* comand can be accessed from `Geometry->Modify->Volume->Reduce->Bolt Patch`


{{% figure src="images/reduce-bolt-patch-gui-1024x628.png" width="1024" height="628" caption="(a) New Reduce Bolt Patch GUI panel (b) Geometry Power Tool, machine learning *Classify Parts* diagnostic used to identify bolts. New power tool solutions for bolts include *reduce bolt patch* command." %}}

For more information see: [Reduce Bolt Patch](https://coreform.com/manuals/tagged/2023.11/cubithelp.htm#t=geometry%2Fcleanup_and_defeaturing%2Freduce%2Freduce_bolt_patch.htm)

## Meshing

### Coarse mesh setting for trimesh scheme
While meshing is most commonly seen as an operation for PDE-based simulations that require high-quality regular-shaped elements for accuracy, there are tangential industry applications that also utilize mesh datastructures in their workflows.  Examples of these include additive manufacturing (slice generation) and nuclear energy (Monte Carlo particle transport).  These applications typically resolve the geometry to high-accuracy through the use of a minimal number of high aspect-ratio triangles.  To support these applications Coreform Cubit now supports a `coarse` setting in the `trimesh` scheme.  This allows users to visualize the mesh prior to export (e.g., to STL, DAGMC ) as well as produce different density meshes on different geometric entities (as opposed to a global parameter set on export) and otherwise access the full utility of Coreform Cubit when producing their meshes.

{{% content %}}
```
[Set] Trimesher Coarse {on|OFF [<ratio=100.0> <angle=5.0>]}
```
{{% / content %}}

{{% figure src="images/compare_std_coarse_trimesh.png" width="1024" height="357" caption="(left) Default `trimesh` settings, (right) New, `coarse` option with default settings." %}}
For more information see: [Trimesh](https://coreform.com/manuals/tagged/2023.11/cubithelp.htm#t=mesh_generation%2Fmeshing_schemes%2Ftraditional%2Ftrimesh.htm#coarse_mesh)


### Improved robustness of triangle meshing
Triangle meshing robustness is improved and a correct mesh can now be produced in a few specific combinations of compositing and merging surfaces. Additionally, on some CAD models, triangle meshing would fail due to low precision geometric evaluations. These failures were a regression introduced in a previous version of Coreform Cubit from improving performance. Geometric evaluations were enhanced to retain some performance and re-establish robustness.

### Improved robustness of node equivalencing
Equivalencing nodes is now more robust after importing multiple mesh files as free mesh. Additionally, higher order nodes are now equivalenced when they exist. Performance has been improved allowing the equivalence operation to complete quicker on larger models.

For more information see: [Merging a Free Mesh](https://coreform.com/manuals/tagged/2023.11/cubithelp.htm#t=mesh_generation%2Ffreemesh.htm)

### Updated Sculpt Command Syntax
Previously, the sculpt command could be executed without arguments, utilizing the current volumes as the default target geometry for meshing. However, to accommodate various file types (e.g., STL, diatom, microstructures) containing geometry, the sculpt command now requires the designation of the target geometry as the first argument when invoking the command.

For more information see: [Sculpt](https://coreform.com/manuals/tagged/2023.11/cubithelp.htm#t=mesh_generation%2Fmeshing_schemes%2Fparallel%2Fsculpt.htm)

### Midnode correction on mesh-based geometry
Coreform Cubit previously performed automatic midnode correction during the import mesh geometry command, enhancing the quality of high-order tetrahedral elements (TET10) by straightening edges when quality was below a designated threshold. Users can now toggle the midnode_correction option during import, allowing control over this capability. By default, midnodes will not be corrected based on quality.

For more information see: [Midnode Correction](https://coreform.com/manuals/tagged/2023.11/cubithelp.htm#t=mesh_generation%2Fmesh_import%2Fimporting_exodus2_files.htm#midnode_correction)

## Graphical User Interface

### New right-click automatic surface selection options
After selecting two surfaces (or collections of surfaces), a new right-click *select between* option selects all surfaces between the two already selected surfaces, given there is only a single path of surfaces between them. Merged surfaces are skipped over. If a single surface is selected, the new *select enclosure* option will select all connected surfaces, forming a water-tight enclosure.

For more information see: [Right Click Commands for the GUI Graphics Window](https://coreform.com/manuals/tagged/2023.11/cubithelp.htm#t=environment_control%2Fgui%2Fgraphics_window%2Fright_click_commands.htm)

## Python Interface Enhancements
### New Methods

{{% content %}}
- `get_connected_surfaces()`
   - Given a surface, returns all connected, unmerged surfaces.
- `get_bolt_shigley_radius()`
   - Determines the effective load radius surrounding a bolt based on the diameter of the head and the upper and lower volume thicknesses based on Shigley.
{{% / content %}}

### Deprecated Methods
{{% content %}}
- `get_displacement_coord_system()`
{{% / content %}}

## Cubit bug fixes and enhancements

### Improved OpenMPI version compatibility

When running Sculpt on Linux and macOS, the version of OpenMPI used is no longer restricted to version 4.0. One may use any version of OpenMPI which is ABI compatible with version 4.0. This includes 3.0, 3.1, 4.1 and 5.0. Running Sculpt with other versions of OpenMPI may be useful if targeting a specific hardware which the distribution-included OpenMPI doesn’t support.

### New extended parsing options with blocks, nodesets and sidesets

Extended parsing of blocks, nodesets and sidesets *in* mesh is now fully supported. For example, one can get sidesets using a `sideset in face 40` syntax or get blocks using a `block in hex 32` syntax. This feature now works whether the blocks, sidesets or nodesets are defined by geometry or mesh. This may be useful when writing general commands to work on both types of definitions.

### Generalized coordinate frame commands

The syntax for creating coordinate frames has been updated to no longer include the word `exodus`. The is change is to emphasize that coordinate frames are available for use with ABAQUS as well as EXODUS. Old journal files will continue to work when using the word `exodus`.

For more information see: [Coordinate Frames](https://coreform.com/manuals/tagged/2023.11/cubithelp.htm#t=finite_element_model%2Fexport%2Fexodus_coordinate_frame.htm)

### New string option in locate command

The following locate commands now support an optional string parameter allowing customization of the displayed label. The supplied string is used as the label instead of the default xyz position or name of the entity, as shown below.

{{% content %}}
`locate location [<string>]`
{{% / content %}}

{{% content %}}
`locate [geometry] [genesis_entity] [bc_entity] [contact pair] [node] [element] [<string>]`
{{% / content %}}

{{% figure src="images/locate_with_string-1-768x665.png" width="768" height="665" caption="Location using: `locate vertex 1 'my_text_here'` and `locate vertex 5`" %}}

For more information see: [Drawing, Locating, and Highlighting Entities](https://coreform.com/manuals/tagged/2023.11/cubithelp.htm#t=environment_control%2Fgraphics_window_control%2Fdrawing_and_highlighting.htm)

### Defects Fixed in Coreform Cubit 2023.11

| Ref # | Description                                                  |
| ---- | ------------------------------------------------------------ |
| MESH-4178 | `SHOW` option in GUI not echoing info to command line |
| MESH-5135 | Refine in Sculpt based on geometry id |
| MESH-5465 | Review and add unsupported sculpt options to the GUI |
| MESH-5539 | Coreform Cubit export to MCNP (ABAQUS) |
| MESH-5630 | Equation-Controlled Distribution Factors broken since 15.0 |
| MESH-5642 | Deploy the latest version of Verdict on [https://github.com/sandialabs/verdict](https://github.com/sandialabs/verdict) |
| MESH-6328 | Change package layout to comply with mac format |
| MESH-6585 | Failure in blunt tangency operation |
| MESH-6740 | Long term support of SGM python scripts |
| MESH-6934 | Upgrade gtest for SGM, UMR and Coreform Cubit |
| MESH-6957 | `Align` command doesn’t work with `surf` |
| MESH-6961 | Graphics curves bleed through solid |
| MESH-6971 | Mpiexec issue with sculpt |
| MESH-7102 | Sideset X add – weird ERROR message |
| MESH-7104 | `Align` not working |
| MESH-7124 | Update abaqus export to support mcnp |
| MESH-7126 | Why is sculpt getting called in my Coreform Cubit journal workflow |
| MESH-7128 | Support `sideset in face` |
| MESH-7132 | Better cavity selection |
| MESH-7133 | View in True hidden line mode and view in hidden line mode are the same? |
| MESH-7140 | Sculpt meshing from SPN file |
| MESH-7252 | Volume Id not being honored in individual `.cub` files with multiple `import cubit` commands |
| MESH-7254 | Sculpt to output memory usage between refinement |
| MESH-7271 | Buggy `connected_sets` option in ‘remove surface’ command |
| MESH-7276 | Listing Error in Aprepro Loop |
| MESH-7307 | Webcut `with general plane` case not working |
| MESH-7310 | Meshing "simple" domain |
| MESH-7313 | `Split surface` not working |
| MESH-7314 | Higher order nodes not equivalenced |
| MESH-7315 | Surface normal inconsistent through merge operation & trimeshing |
| MESH-7320 | Import SGM moves part |
| MESH-7321 | `Split surface` not working great |
| - | Re-enable `equiangle skew` quality measure for wedges |
| - | Add missing volume webcuts to entity-based navigation |


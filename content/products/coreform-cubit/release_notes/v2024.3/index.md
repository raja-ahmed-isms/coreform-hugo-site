+++
title = "Release Notes for Coreform Cubit 2024.3"
include_collapse = true
layout = "release_notes"
date = "2024-03-4"
description = "Coreform is pleased to announce Coreform Cubit 2024.3, featuring faster performance on critical commands, an improved Python interface, and numerous feature enhancements and bugfixes. Additionally, the documentation of the DAGMC neutronics workflow supported by Coreform Cubit is significantly improved."
+++


## Geometry
### 2x - 10x faster imprint performance
The **imprint and merge** functionality is one of Coreform Cubit's most-used features, as it allows for the creation of conformal meshes across volumes. In this release, we have significantly improved the speed of the imprint and merge workflow by replacing multiple *O(n^2)* operations with *O(n)* operations (*n* being the number of bodies), yielding speed-ups between 2x and 10x on several benchmarks, such as those shown below. Actual speed improvements will vary depending on the number and complexity of bodies being imprinted. 

{{% figure src="images/imprint_reactor_and_data.jpg" width="700" caption="Coreform Cubit imprint speed improvements: 'Advanced test reactor' benchmark." %}}
{{% figure src="images/imprint_stacked_and_data.jpg" width="700" caption="Coreform Cubit imprint speed improvements: 'Stacked' benchmark." %}}
{{% figure src="images/imprint_array_and_data.jpg" width="700" caption="Coreform Cubit imprint speed improvements: 'Array' benchmark." %}}


For more information on imprint functionality, see: [Imprinting Geometry](https://coreform.com/manuals/tagged/2024.3/cubithelp.htm#t=geometry%2Fimprint_merge%2Fimprint.htm)



### Improved machine learning geometry preparation capabilities
#### Enhanced reduce bolt patch command
The **reduce bolt patch** command aids in the structural dynamics idealization of fasteners for simulation. It converts fasteners into circular patches and corresponding sidesets centered on the bolt axis. In the 2024.3 release of Coreform Cubit, the command was enhanced on several fronts including hole specification, explicit surface identification, radius factor, surface patch naming, incremental ID with start ID, and robustness improvements.

{{% content %}}
- **Hole Specification:** You can now specify hole surfaces if a bolt is not present.
- **Explicit Surface Identification:** If Coreform Cubit cannot auto-detect contact surfaces, you can specify them manually.
- **Radius Factor:** New option to set the radius as a factor multiplied by the bolt shaft radius, or the hole radius if a bolt is not present.
- **Surface Patch Naming:** The resulting circular surface patches can be named for easier referencing in later scripts or journal commands.
- **Incremental ID with Start ID:** The increment ID feature now allows a separate starting ID, enabling the same patch to be added to multiple sidesets.
- **Robustness Improvements:** Enhancements to handle cases where up to three volumes are fastened by the same fastener.
{{% / content %}}

For more information see: [Reduce Bolt Patch](https://coreform.com/manuals/tagged/2024.3/cubithelp.htm#t=geometry%2Fcleanup_and_defeaturing%2Freduce%2Freduce_bolt_patch.htm)

#### J2G Bolt reduction more robust
Several enhancements have been added to make J2G bolt reduction more robust, including:

{{% content %}}
- Handling through-holes in the lower bolt hole volume (previously the lower hole had to be a blind hole)
- Curves of bolt holes can splines (previously curves could only be arcs)
- Bolt hole loops can now have multiple curves (previously loops could only contain one curve)
{{% / content %}}

For more information see: [Reduce Bolt Spider](https://coreform.com/manuals/tagged/2024.3/cubithelp.htm#t=geometry%2Fcleanup_and_defeaturing%2Freduce%2Freduce_bolt_spider.htm)

### Improved hole feature identification
Hole feature recognition is improved to find more cases such as the one illustrated in this image. More accurate identification will assist in rapidly finding holes and removing them.

{{% figure src="images/hole_identification.png" width="600" %}}

For more information see: [Geometry Power Tool > Traits](https://coreform.com/manuals/tagged/2024.3/cubithelp.htm#t=environment_control%2Fgui%2Ftree_view%2Frepair_and_analysis.htm#traits) 

### Validate command accepts SGM geometry
Geometry that has been imported to Coreform Cubit with the SGM geometry libararies can now be checked using the validate command:

{{% content %}}
```
validate {body|volume|surface|curve|vertex|group} <id_range>
```
{{% / content %}}

This is the same command used for checking ACIS geometry. Any errors found validating the geometry will be printed to the command window.

### Remove surface consistency
The `remove surface` command now gives consistent results when the `connected_sets` options is used. Previously, curves resulting from the remove operation would have inconsistent IDs. Now, if a journal file containing this command is replayed, resulting IDs will be consistent.

For more information see: [Removing Surfaces](https://coreform.com/manuals/tagged/2024.3/cubithelp.htm#t=geometry%2Fcleanup_and_defeaturing%2Fremoving_geometric_features%2Fremoving_surfaces.htm)



## Meshing

### New Mesh Intersection Removal
Coreform Cubit now has the ability to reposition nodes of 3D elements to remove intersection. Where intersecting elements have nodes on a volume boundary (vertices, curves, and surfaces), intersection removal moves those nodes slightly off geometry, along surface normals, to remove intersection. The capability is only meant for slight intersection (less than 50% of element volumes), as removing large intersection can significantly compromises element quality. The **detail** option displays the ten nodes most moved, giving the user an idea of the maximum movement.

{{% figure src="images/mesh_intersection_removal.png" width="600"  caption="Before (left) and after (right) mesh intersection removal." %}}

For more information see: [Removing Intersecting Mesh](https://coreform.com/manuals/tagged/2024.3/cubithelp.htm#t=mesh_generation%2Fmesh_modification%2Fmesh_intersection_removal.htm)

### Mesh Intersection Detection Improvements
Performance improvements have been to the mesh intersection detection. On large meshes (2 million elements), users can see up to a 4X speed improvement. Additionally, bugs have been fixed in the routine, allowing for more consistent and accurate results.

For more information see: [Finding Intersecting Mesh](https://coreform.com/manuals/tagged/2024.3/cubithelp.htm#t=mesh_generation%2Fmesh_quality_assessment%2Ffind_intersecting_mesh.htm)


### Node equivalencing defect corrected
When equivalencing a node on geometry with a node not on geometry, elements were incorrectly deleted. A correction was made to preserve the elements during the equivalencing operation.

For more information see: [Equivalence Node](https://coreform.com/manuals/tagged/2024.3/cubithelp.htm#t=mesh_generation%2Ffreemesh.htm#merge) 


### Default sizing function adjustment for curves
The default sizing function for curves when triangle meshing gives better size transition along curves while taking into account sizing on the adjacent surfaces. The following image illustrates an example where a spherical surface is given a small size and 2 flat surfaces are given larger sizes. With the improved sizing function, the straight curve will have size transitions to match adjacent surfaces.

{{% figure src="images/curve_sizing.png" width="800"  caption="Corrected sizing on straight edge shown on the right." %}}

### New surface proximity option for triangle meshing
Mesh refinement on thin surfaces while triangle meshing is now possible with a new surface proximity option. Also available is a `ratio` option to scale mesh sizes after proximity calculations. The image below illustrates a mesh without surface proximity enabled, and another mesh with surface proximity enabled and a ratio of 2. In both cases, the constraining geometry is highlighted.

{{% figure src="images/surface_proximity.png" width="600"  caption="Left: triangle mesh without surface proximity. Right: triangle mesh with surface proximity." %}}


## Python API
### Improved Python interface
A single script file can now support both Python statements and Coreform Cubit commands. Additionally, the script windows in the user interface are consolidated into one, and the console version of Coreform Cubit now supports Python. Batch processing with scripts can now be done using Python syntax, Coreform Cubit syntax, or both. The command window in Coreform Cubit  can be switched between a Python or Coreform Cubit mode either by pushing a GUI button, or by issuing `#!python` or `#!cubit` statements. Using the console version of Coreform Cubit is now recommended when running scripts in non-interactive batch processes. The following image illustrates the new Command window containing both Python and Coreform Cubit syntax.

{{% figure src="images/script_window.png" width="500"   %}}

For more information see: [Command Line Workspace](https://coreform.com/manuals/tagged/2024.3/cubithelp.htm#t=environment_control%2Fgui%2Finput_window.htm)

#### New Python API for hole, chamfer, blend feature identification
Finding hole, chamfer and blend features is now possible using a Python script. The new APIs are: `get_surface_hole_collections`, `get_chamfer_chain_collections`, `get_blend_chain_collections`. Each of these APIs will accept a radius or thickness threshold value. These functions may be useful if wanting to find features and remove them in a script.

For more information see: [Python API](https://coreform.com/manuals/tagged/2024.3/cubithelp.htm#t=python%2Fnamespace_cubit_interface.htm)


## Other improvements

### Automated transfer of boundary conditions accelerates design workflows
The new capability shown in the below figure automates the transfer of sidesets and nodesets from the source mesh (e.g. design domain model) to the target mesh (e.g. topology optimized shape). Transfer of sidesets and nodesets was a major user-intensive, error-prone, bottleneck in the topology optimization design workflow. Users were required to go through the painful step of manually selecting elements to define equivalent sidesets on the topology optimized shape. The command given below streamlines running physics codes on the topology optimized shapes with model attribution.

{{% content %}}
```
transfer sideset onto {tri |face |tet |hex } [tolerance ] [log] transfer nodeset onto {tri |face |tet |hex } [tolerance ]
```
{{% / content %}}

{{% figure src="images/source_mesh_with_bc.jpg" width="700" %}}

{{% figure src="images/target_mesh_with_bc.jpg" width="700" %}}



### Improved DAGMC documentation
Direct Accelerated Geometry Monte Carlo ([DAGMC](https://svalinn.github.io/DAGMC/)) is a software package that allows users to perform Monte Carlo radiation transport directly on CAD models.
The DAGMC community previously developed a [Coreform Cubit plugin](https://svalinn.github.io/DAGMC/install/plugin.html), which was integrated into Coreform Cubit in 2023.11. In connection with the release of Coreform Cubit 2024.3, Coreform has collaborated with the DAGMC developers to improve the DAGMC documentation. This documentation is available here: [DAGMC documentation](https://pshriwise.github.io/dagmc-docs/).


#### Ability to aggregate machine learning classification training data
User-defined/created classification data can be merged into Coreform Cubit's existing data with the command:

{{% content %}}
```
classify aggregate [model category]
```
{{% / content %}}

For more information see: [Part Classification with Machine Learning](https://coreform.com/manuals/tagged/2024.3/cubithelp.htm#t=environment_control%2Fentity_selection_and_filtering%2Fml_part_classification.htm)


### Support for Windows 11
Coreform Cubit is now tested and supported for Windows 11

<!-- =============================================================================== -->
<!-- =============================================================================== -->
<!-- =============================================================================== -->

## <br> Defects Fixed in Coreform Cubit 2024.3

| Ref # | Description                                                  |
| ---- | ------------------------------------------------------------ |
| - | Fix memory errors when importing multiple lite mesh files with nodesets |
| - | Removal of python tab |
| - | Enable Python in cubit_server |
| - | Sped up right-click &#8216;select chamfer chains&#8217; |
| - | Webcut with general plane case fixed |
| - | Diagnose Geometry tab now correctly considers sheet bodies |
| - | Prevent sliver mesh edges when fixing skew with &#8216;adjust boundary&#8217; command |
| - | Better sweeping with reversals |
| - | Deletion of orphaned nodes |
| - | Improved logic for sizing of curves when triangle meshing. The auto bias sizing function now better matches the meshgems behavior |
| - | Better handling of equivalence node when lower id nodes are not owned by geometry |
| - | Python version option fixed |
| - | Python script execution fixes |
| - | Fixed hang in skeleton sizing |
| - | Add &#8216;target&#8217; word in the command for sweep surface target |
| - | On cub file open/import, Coreform Cubit no longer modifies locations of higher-order mid-edge nodes |
| - | Fixed failure in undo because of block name collision |
| - | Fixed failure to import cub file due to extraneous error |
| - | Fixed holes not being completely identified |
| - | Step color import now works |
| - | Fixed error wherein wrong body id returned by cubit.get_last_id( "body") |
| - | Fixed Cubit material commands error occurring when Youngs Modulus >= (2^31) - 1 |
| - | Added automatic creation of identically-named sidesets when named boundary conditions are created. |
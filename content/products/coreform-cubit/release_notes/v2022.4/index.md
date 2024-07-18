+++
title = "Release Notes for Coreform Cubit 2022.4"
include_collapse = true
layout = "release_notes"
date = "2022-04-14"
description = "Coreform is pleased to announce the release of Coreform Cubit 2022.4, which includes an updated geometry kernel, improved meshing and geometry features, and expanded Python interface functionality."
+++



## U-splines
Coreform Cubit now supports U-spline construction on volumes meshed with the following mesh schemes: 
- Map
- SubMap
- Sweep
- Sphere
- Polyhedron
- TetPrimitive

## Graphical User Interface

The right click menu now provides an option to select entities that share the same name.
It will select entities that have the same prefix up to the "@" character.

### Select chamfer chain
When selecting a chamfer surface, the 'select chamfer chain' option is available in the right-click context menu.


## Meshing

### New Mass Increase Ratio metric added to Cubit
The Mass Increase Ratio metric is based on the timestep metric and can be used as a guide to determine how much an element’s mass can be scaled during analysis, to typically increase timestep. Users specify a minimum ‘target timestep’ and then use this metric to see how much an element’s mass will be scaled by to meet the specified timestep. Available for use on tets and hexes.

### Geometry-Aware Spider Blocks
If geometry (surface|curve|vertex) is referenced when creating spider blocks with the command
```
[Vertex <id> | Node <id>] Spider {Surface|Curve|Vertex|Face|Tri|Node} <range> [preview][Element Type{ bar | bar2 | bar3 | BEAM | beam2 | beam3 | truss | truss2 | truss3 }] 
```

the spider block is linked to that geometry, allowing the spider to update/regenerate the BAR elements if the mesh on the geometry is delete, remeshed, or translated.

## Geometry

### ACIS kernel updated to Version 2021.1.0.1
The ACIS geometry kernel has been updated to Version 2021.1.0.1.

*Note:* Changes to the ACIS kernel can result in changes in entity IDs, which may break journal files that rely on entity IDs.
For mission-critical applications, we encourage users to either:

{{% content %}}
- use IDless journal files, or 
- use the Python API to create ID-less Python scripts.
{{% / content %}}

## Sculpt

### New Sculpt options

- `stitch_parallel` option combines parallel files when no Nemesis data is included
- `match_sidesets_nodeset` to more precisely define boundary between sides when using 'match_sidesets' option.
- `material_name`, `sideset_name`, and `nodeset_name` added to define names on materials, sidesets, and nodesets respectively.
- `sideset` and `nodeset` to allow for user-defined sidesets and nodesets based on xyz bounding box boundaries.
- `large_exodus` to generate output Exodus file(s) to allow IDs greater than 2^31 (2.14 billion).
- `input_mesh` option under the gen_sidesets option. Used with the `input_mesh` option where an Exodus file is used as the base grid. Only sidesets and nodesets defined in the input Exodus mesh are transferred to the output mesh.

## Graphics, Utilities, etc.
### Enhancements to free entity selection
The ability to select free surfaces (sheet bodies) has been added to the command
```
[surface <id_list>] [curve <id_list>] [vertex <id_list>] [add|remove]
```

### Added support for Corvid Velodyne files
Cubit now supports exporting files for use in Corvid Velodyne, via the command
```
export velodyne '<filename>' [overwrite]
```

### New CubitInterface functions

CubitInterface is Cubit's Python module, which provides extensive capability for querying and modifying data in Cubit.
The following functions were added to CubitInterface for version 2022.4.
             
| Function&nbsp;Name | Description  |
| ---- | ------------- |
| `get_total_bounding_box` |  Get the bounding box for a list of entities. |
| `gather_surfaces_by_orientation` |  Gathers connected surfaces to those specified, that use shared curves in an opposite sense. |
| `get_overlapping_surfaces_bodies` |  Returns a list of lists of overlapping surfaces. First surface in each list overlaps with all others in that list. |
| `get_chamfer_surfaces` |  Get the list of chamfer surfaces for a list of volumes |
| `get_chamfer_chains` |  Returns the chamfer chains for a surface |
| `get_blend_chain_collections` |  Returns the collections of surfaces that comprise blend chains in the specified volumes. Filter by radius threshold |
| `get_chamfer_chain_collections` |  Returns the collections of surfaces that comprise chamfers in the specified volumes. Filter by thickness of chamfer |
| `get_overlapping_curves` |  For every occurance of two overlapping curves, two curve IDs are returned. |
| `get_overlapping_surfaces_surface` |  Get the list of overlapping surfaces for a single surface |
| `get_volume_gaps` |  For every occurance of a gap between volumes, two surfaces IDs are returned. |
| `get_coincident_entity_pairs` |  Get the list of coincident vertex-vertex, vertex-curve, and vertex-surface pairs and distances from a list of volumes |
| `get_nearby_volumes_volume` |  Get the list of nearby volumes for a single volume within a specified distance |
| `get_blunt_tangency_depth` |get default depth value for blunt tangency operation |
| `is_chamfer_surface` |  Return whether a given surface is a chamfer |
| `measure_between_entities` |  Returns the distance between two specified entities |
| `get_tetmesh_growth_factor`<br> `get_tetmesh_parallel`<br> `get_tetmesh_num_anisotropic_layers`<br> `get_tetmesh_optimization_level`<br> `get_tetmesh_insert_mid_nodes`<br> `get_tetmesh_optimize_mid_nodes`<br> `get_tetmesh_optimize_overconstrained_tets`<br> `get_tetmesh_optimize_overconstrained_edges`<br> `get_tetmesh_minimize_slivers`<br> `get_tetmesh_minimize_interior_points`<br> `get_tetmesh_relax_constraints` | Retrieve the current tetmesh global settings (Meshgems) |
| `get_trimesh_tar`<br> `get_min_size`<br> `get_trimesh_geometry_sizing`<br> `get_trimesh_num_layers`<br> `get_trimesh_split_edges`<br> `get_trimesh_tiny_length`<br> `get_trimesh_ridge_angle` | Retrieve the current trimesh global settings (Meshgems) |
| `get_all_ids_from_name` |  Get all IDs of a geometry type with the prefix given by string. |
| `get_hole_surfaces` |  Given a surface, returns all adjacent surface defining a hole. |
| `get_surface_hole_collections` |  Given a volume(s), returns the collections of surfaces that define holes. |
| `is_hole_surface` |  Return whether the surface is part of a hole. |
| `best_edge_to_collapse_interior_node` |  Given a node owned by a surface, returns the id of the best edge to collapse, so that the node is no longer on the surface. |
| `get_continuous_curves` |  Gets a list of continuous curves that have tangents with 180 degrees +/- and defined tolerance. |
| `get_n_largest_distances_between_meshes` |  Computes the ‘n’ largest distance from the nodes of the first entity to the elements of the second |



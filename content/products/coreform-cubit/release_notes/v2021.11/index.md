+++
title = "Release Notes for Coreform Cubit 2021.11"
include_collapse = true
layout = "release_notes"
date = "2021-11-17"
description = "We are pleased to announce the release of Coreform Cubit 2021.11, which features GUI updates, improved assembly importing, and a streamlined licensing process for Coreform Cubit Learn users."
+++

## Usplines
U-splines are a revolutionary new spline technology, developed by Coreform, and can be thought of as a generalization of B-splines, NURBS, and T-splines. A U-spline is created by first generating a linear mesh to define the spline’s topology, then prescribing the polynomial degrees of the elements and degree of smoothness with their neighboring elements, and finally computing the resulting U-spline in Coreform Cubit. The generated U-spline can then serve as a watertight CAD geometry definition and can be used as an accurate and computationally efficient mesh for simulation, a technique known as isogeometric analysis (IGA). 

## Usplines: Initial support in ANSYS LS-Dyna
Bézier Extraction is an IGA/spline technology that transforms a spline into a traditional "element point of view" format that can be more readily implemented in traditional finite element codes. ANSYS LS-Dyna supports Bézier extraction via DYNA’s `*IGA_INCLUDE_BEZIER` keyword and the "BEXT" files that Coreform Cubit can export from a U-spline.

## Geometry Compliant Lattice Structures
Geometry Compliant Lattice Structures (GCLS) is a novel lattice construction technology that enables the efficient design of lattices that conform to a CAD model’s shape and size. GCLS structures are built using a background U-spline mesh, which the user generates on a CAD volume of the lattice region. Several default lattice unit cells are provided, as is a workflow for designing custom unit cells. This new capability is available as a separately-licensed product, Coreform Lattice GC™ . Contact sales@coreform.com for a free trial or other inquiries.

## Updates to Coreform Cubit Learn licensing
We at Coreform are committed to ensuring that students, hobbyists, and open-source simulation developers have access to the world-class mesh generation capabilities in Coreform Cubit. In the 2020.2 release we accomplished this by releasing the Learn license for Coreform Cubit. Beginning with Coreform Cubit 2021.11, we have streamlined the licensing process for Coreform Cubit Learn. Going forward, all Cubit Learn licenses will require internet access and an account at [coreform.com](https://coreform.com) to be activated. All legacy Coreform Cubit Learn licenses that used the RLM server will expire 60 days following the release of Coreform Cubit 2021.11 (approximately January 2022).

All Coreform Cubit Learn users are invited to obtain a new Coreform Cubit Learn license by following this process:
1. Get the new Coreform Cubit Learn license:
{{% content %}}
- Log in to your Coreform account
- Go to the “Request Free Coreform Cubit Learn License” page
- Click the “Get Coreform Cubit Learn” button
{{% / content %}}

2. Download the latest version of Coreform Cubit:
{{% content %}}
- Go to the Coreform Cubit Downloads page
- Download the most recent release of Coreform Cubit for your platform
{{% / content %}}

3. Activate new Coreform Cubit Learn license:
{{% content %}}
- Launch the latest version of Coreform Cubit
- In the activation window that appears, select “Educational” and enter the email and password associated with your Coreform account
{{% / content %}}

## Changing tool for generating documentation
For many years Cubit’s help content has been generated using Adobe's RoboHelp software. 
We’re transferring to using Scribble as our documentation tool, which is based on the Racket programming language. 
This transfer of documentation tooling, while still in its infancy, is now "live." 
We expect there to be some rough edges, so if you find any issues with the documentation please send us a bug report at support@coreform.com!
<!-- and this manual is rendered using this new tooling.  -->

## Geometry tools: Taper surfaces
Exposed ACIS "taper" operation that tapers an array of faces about an array of corresponding edges and a supplied draft direction by a given draft angle.

## Geometry tools: Create tangent curve
Added a new curve construction method that creates a spline curve that connects two points with prescribed tangent direction at each point. This can be particularly useful for smoothly connecting two discontinuous curves.

## Mesh export: ’mesh-only’ export for ANSYS LS-Dyna
A new option has been added to the LS-DYNA <tt>.k</tt> mesh export utility to enable only writing the mesh data, which can then be `*INCLUDE`-ed into another LS-DYNA <tt>.k</tt> file. Previously, this exporter would output a minimum working input file that included "dummy" keywords for items such as materials, steps, etc.

## User experience: Improved performance importing complex assemblies
Improvements to file-reading algorithms have resulted in dramatically (>10x) faster performance when importing certain STEP files. For example the wall time required to read a large test file has been reduced from ~720 seconds to ~20 seconds, matching the performance of other, industry-leading CAD software. 
The specific change is a refactor of the algorithm that checks for clashing entity names, turning an algorithm into an algorithm. This is not a breaking change.

## New features in Coreform Cubit 2021.11
| Status | Description  |
| ---- | ------------- |
|  NEW  |  Officially release U-splines.  |
|  NEW  |  Officially release Coreform Lattice GC.  |
|  NEW  |  Allow Coreform Cubit Learn users to log in with credentials.  |
|  NEW  |  Implement uspline saves to HDF5.  |
|  NEW  |  Use symmetric tessellation for GCLS default unit cells.  |
|  NEW  |  Adds a GCLS implementation that uses the symmetric tessellation.  |
|  NEW  |  Add methods to unfold a symmetric tessellation from a repeating unit.  |
|  NEW  |  Add ’mesh_only’ option to LS-DYNA export.  |
|  NEW  |  Add routine to make all basis funcs C0 across an edge.  |
|  NEW  |  Add capability to output BEXT from Cubit that is compatible with Dyna.  |
|  NEW  |  Add command to fold lattice reference cell geometry in Cubit.  |
|  NEW  |  Added nodesets for 1D domains to the list of `subdomain_dynamic_cells` created by the build uspline command in Cubit. |
|  NEW  |  Added command to taper a surface or surfaces at a given angle.  |
|  NEW  |  Added a breadcrumbs panel option to use small or large icons.  |
|  NEW  |  Auto change the working directory for the GUI on Windows if started in an unwritable location.  |


## Defects FIXED in Coreform Cubit 2021.11
| Status | Description  |
| ---- | ------------- |
|  FIXED  |  Progress bar bug in import healing.  |
|  FIXED  |  Bug in the pick widget menu setup.  |
|  FIXED  |  Bug in detecting entities added to another block.  |
|  FIXED  |  Bug in the handling of triple quoted strings in Python.  |
|  FIXED  |  Bug in the tree’s context menu when a pick widget was active.  |
|  FIXED  |  Found some duplicate tree item issues.  |
|  FIXED  |  Duplicate nodes in the navigation xml.  |
|  FIXED  |  Location and Direction syntax errors not printed  |
|  FIXED  |  Error in the GUI and a bug in direction parsing.  |
|  FIXED  |  Impetus exporter not handling merged surfaces.  |
|  FIXED  |  Importing some STEP files is slow.  |
|  FIXED  |  Missing axis keyword in GUI panel.  |
|  FIXED  |  Tetmesh adaptivity memory management changed.  |
|  FIXED  |  Bad GUI keyword in volume cleanup.  |
|  FIXED  |  Missing keyword to GUI panel.  |


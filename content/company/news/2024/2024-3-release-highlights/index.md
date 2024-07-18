+++
title = "Coreform Cubit 2024.3 released, featuring faster performance and improved Python interface"
description = "OREM, UTAH — Coreform Cubit 2024.3 is now available, featuring faster performance on critical commands, an improved Python interface, and numerous feature enhancements and bugfixes. Additionally, the documentation of the DAGMC neutronics workflow supported by Coreform Cubit is significantly improved."
date = "2024-03-04"
publishdate = "2024-03-04"
featured = true
layout = "news"
+++

Coreform Cubit 2024.3 is now available, featuring faster performance on critical commands, an improved Python interface, and numerous feature enhancements and bugfixes. Additionally, the documentation of the DAGMC neutronics workflow supported by Coreform Cubit has been significantly improved.

## 2x - 10x faster imprint performance
The **imprint and merge** functionality is one of Coreform Cubit's most-used features, as it allows for the creation of conformal meshes across volumes. In this release, we have significantly improved the speed of the imprint and merge workflow by replacing multiple *O(n^2)* operations with *O(n)* operations (*n* being the number of bodies), yielding speed-ups between 2X and 10X on several benchmarks, such as those shown below. Actual speed improvements will vary depending on the number and complexity of bodies being imprinted. 

{{% figure src="imprint_reactor_and_data.jpg" width="700" caption="Coreform Cubit imprint speed improvements: 'Advanced test reactor' benchmark." %}}
{{% figure src="imprint_stacked_and_data.jpg" width="700" caption="Coreform Cubit imprint speed improvements: 'Stacked' benchmark." %}}
{{% figure src="imprint_array_and_data.jpg" width="700" caption="Coreform Cubit imprint speed improvements: 'Array' benchmark." %}}

## Improved Python interface 
A single script file can now support both Python statements and Coreform Cubit commands. Additionally, the script windows in the user interface have been consolidated into one, and the console version of Coreform Cubit now supports Python. Batch processing with scripts can now be done using Python syntax, Coreform Cubit syntax, or both. The command window in Coreform Cubit  can be switched between a Python or Coreform Cubit mode either by pushing a GUI button, or by issuing `#!python` or `#!cubit` statements. Using the console version of Coreform Cubit is now recommended when running scripts in non-interactive batch processes. The following image illustrates the new Command window containing both Python and Coreform Cubit syntax.

{{% figure src="script_window.png" width="500"   %}}

## Support for Windows 11
Coreform Cubit is now tested and supported for Windows 11.

## Improved DAGMC documentation
Direct Accelerated Geometry Monte Carlo ([DAGMC](https://svalinn.github.io/DAGMC/)) is a software package that allows users to perform Monte Carlo radiation transport directly on CAD models. The DAGMC community previously developed a [Coreform Cubit plugin](https://svalinn.github.io/DAGMC/install/plugin.html), which was integrated into Coreform Cubit in 2023.11. In connection with the release of Coreform Cubit 2024.3, Coreform has collaborated with the DAGMC developers to improve the DAGMC documentation, which is available at the following link: [DAGMC documentation](https://pshriwise.github.io/dagmc-docs/).

## Improved machine learning geometry preparation capabilities
The **reduce bolt patch** command aids in the structural dynamics idealization of fasteners for simulation. It converts fasteners into circular patches and corresponding sidesets centered on the bolt axis. In the 2024.3 release of Coreform Cubit, the command was enhanced on several fronts, including hole specification, explicit surface identification, radius factor, surface patch naming, incremental ID with start ID, and robustness improvements.

Additionally, several enhancements have been added to make J2G bolt reduction more robust, including capabilities for handling through-holes in the lower bolt hole volume, allowing splines in bolt holes curve specification, and allowing multiple curves in bolt hole loops.

Finally, hole feature recognition has been improved to find more cases. More accurate identification will assist in rapidly finding holes and removing them.

A full list of additional improvements and bug fixes is available on the [2024.3 release notes page](/products/coreform-cubit/release_notes/v2024.3/)
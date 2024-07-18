+++
title = "Coreform partners with INL to improve open-source modeling and simulation tool MOOSE"
description = "The collaboration makes advanced nuclear reactor modeling and simulation faster, less costly and more efficient."
date = "2021-01-20"
publishdate = "2021-01-20"
featured = false
featured_image = "/images/par/parMeeting12.jpg"
layout = "news"
+++


Long before an advanced nuclear reactor begins to generate power or another complicated project comes to life, researchers and engineers model and simulate it to ensure success. Now a collaboration between Idaho National Laboratory and Orem, Utah-based Coreform makes this modeling and simulation faster, less costly and more efficient.

The partnership improves upon an open-source INL platform to make simulations more true to life, benefiting the many projects that leverage it.

“Higher fidelity simulations give you better confidence and a better understanding of the science,” said Cody Permann, INL department manager for Computational Frameworks. “It can reduce costs in how one designs reactors or how new technologies are researched. Modeling and simulation can reduce the uncertainty margins. It cuts costs.”

INL’s Multiphysics Object-Oriented Simulation Environment (MOOSE) is a general-purpose, open-source tool that has been used to analyze groundwater transport in Australia, research mining technology in Switzerland, and solve next generation reactor issues at INL and elsewhere. MOOSE works after systems being modeled are divided into tiny regions for simulation purposes, a widely used technique known as the finite element method. MOOSE then computes what happens in each of these small volumes, these finite elements, as they interact with each other under specified heat, pressure, vibration and other environmental conditions.

These calculations eventually result in a prediction of what will happen to the system over time. This information can be used to troubleshoot potential problem areas and locate possible failure points.

In this modeling technique, smaller finite elements result in a finer mesh of points where calculations take place. A finer mesh, in turn, leads to a more realistic simulation. However, the finer the mesh, the more computation and the longer the simulation runs. In fact, said Permann, the ratio of mesh points to run time is about 1:1. So, cutting the mesh points in half reduces the run time to about half.

“The mesh you build often has a strong effect on the accuracy and the efficiency of the simulation. So, for mission-critical applications, researchers and analysts need to have precise, fine-tuned control over their meshes,” said Gregory Vernon, Coreform Product Management director.

Coming up with the right mesh, though, has sometimes required costly commercial software or been a matter of trial and error using a brute force approach. What has been lacking, Permann said, was an inexpensive yet robust and efficient way to build the critical mesh of simulation points. That situation has now changed with the joint INL-Coreform project.

With this MOOSE enhancement project, INL has added Coreform’s easy-to-use, free for noncommercial use tool that helps researchers quickly generate the right set of mesh points for a specific set of circumstances. The MOOSE upgrade project was funded by a Department of Energy SBIR (Small Business Innovation Research) award, and Coreform’s part was built as a variant of the company’s commercial mesh generating software.

In its products, Coreform uses what is known as spline technology, a mathematical approach that can closely approximate the real world, explained Vernon. For instance, consider a cylindrical fuel rod (pictured below). Using a regular rectangular mesh of simulation points might not be the best approach when trying to model the fuel rod’s curved surface. In this situation, Coreform’s spline expertise could help in setting up a more appropriate mesh, perhaps one with spacing that varies to account for the fuel rod’s shape and characteristics.


{{% figure src="../contact_pressure.png" width="900" %}}

*A simulation of a fuel rod’s pellets pressing against rod’s protective cladding performed with Coreform spline technology (left) and MOOSE (right). Contact is one of the most challenging physics to simulate, and in addition to being more accurate, the inherent ability to calculate smooth contact pressures makes these simulations easier to compute.*


Now that it has become available, the mesh generating tool has made the capabilities of MOOSE more accessible to researchers, expanding its pool of potential users. “With the release of this tool, the whole simulation and modeling workflow is complete and free. Anybody anywhere can get these tools off the internet,” Permann said.

Permann hopes that one outcome of the project will be wider use of MOOSE. Coreform may push that along by incorporating simulation capabilities in its products, with some of the analysis done by MOOSE. Further enhancements of MOOSE are also in the works.

#### About Coreform:
Coreform is the leading developer of commercial spline-based simulation software. Founded in 2014, the company’s globally recognized authorities in the field of isogeometric analysis are among the most widely cited authors in engineering sciences. The company’s spline-based simulation software is based on its proprietary spline geometry instead of traditional meshes and provides many significant advantages over traditional Finite Element Analysis (FEA) simulation software. Coreform is headquartered in Orem, Utah. 

#### About INL:
INL is a U.S. Department of Energy (DOE) national laboratory that performs work in each of DOE’s strategic goal areas: energy, national security, science and environment. INL is the nation’s center for nuclear energy research and development. Day-to-day management and operation of the laboratory is the responsibility of Battelle Energy Alliance. 
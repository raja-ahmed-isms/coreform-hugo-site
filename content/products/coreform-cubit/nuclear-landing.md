+++
title = "Hex meshing and hex dominant meshing"
description = "Hex meshing and hex dominant meshing"
keywords = "iga, isogeometric analysis, spline-based simulation, fea, fea solver, implicit solver, fea implicit solver"
layout = "single"
special_page = true
+++

{{% hero background=../purple-hero.png overlayimg=../cubitlogoTM.svg %}}
{{% / hero %}}

{{% section %}}

# Coreform Cubit for nuclear energy applications
Coreform Cubit is used in nuclear simulation workflows as a Python-scriptable meshing software with a simple graphical user experience. Coreform Cubit is often the recommended choice for government and open-sourced codes such as MOOSE, OpenMC, and MCNP. Coreform is the exclusive commercial distributor of the Sandia CUBIT code. Coreform has made several enhancements to CUBIT to make it more suitable for commercial usage, including:

{{% content %}} 
* Direct CAD importers
* Fully integrated neutronics workflows
* Technical Support
* Responsive bug fixing
{{% / content %}}

## Applications

### Fusion
Coreform Cubit is used for fusion workflows, including neutronics, finite element analysis, and computational fluid dynamics. 
Because of Cubit’s origins in the US national laboratories, it has long been the choice of engineers and scientists doing groundbreaking research and development across industries, including fusion. 

As fusion private industry grows, researchers who have exited the labs and entered private industry transition are able to transition to commercial workflows by using Coreform Cubit. In addition to the familiar CUBIT workflows, Coreform Cubit offers import of commercial CAD files, including SolidWorks, Catia, NX, and PRO/E, fully integrated neutronics workflows, and technical support.

### Fission
Coreform Cubit is used by many leading nuclear fission companies to prepare complex parts for simulation. Coreform Cubit’s scriptable Python user interface, tight integration with MOOSE, advanced hex meshing schemes, and neutronics simulation workflows assist users preparing neutronics, FEA, CFD, and multiphysics workflows.



## Codes

### MOOSE


{{% figure src="../hex-meshing-coupling-rod-gears.png" width="500" class="floatright" caption="For complicated geometries, we generally use CUBIT. <br>([INL MOOSE homepage](https://mooseframework.inl.gov/application_usage/mesh_block_type.html#:~:text=Creating%20a%20Mesh,-For%20complicated%20geometries%2C%20we))<br> (will replace wrong image)" %}}


<!-- {{% figure src="case-studies/Radiant/images/SlicedCore_featured.png" width="400" class="floatright" caption="For complicated geometries, we generally use CUBIT (Idaho National Laboratory)" %}} -->


MOOSE is an open-source, parallel finite element framework largely developed at Idaho National Laboratory. MOOSE is broadly used for nuclear fission research and development and is also used for nuclear fusion. MOOSE-based [applications](https://mooseframework.inl.gov/application_usage/tracked_apps.html) have been developed to enable the simulation of many types of nuclear physics. 

{{% content %}} 
* Coreform Cubit and MOOSE both are built on the Exodus file format, which leads to native interoperability.	
* [Case study](https://coreform.com/products/coreform-cubit/case-studies/radiant/)
* Documentation about Cubit and MOOSE
* [Webinar](https://www.youtube.com/watch?v=EFsUSNlH118)
{{% /content %}} 

### OpenMC
OpenMC is an open-source Monte Carlo neutron and photon transport simulation code, developed largely out of MIT and Argonne National Laboratory. Coreform Cubit is used in OpenMC workflows to prepare CAD geometry for simulations without the need to recreate the model as constructive solid geometry (CSG). The DAGMC workflow is used to export the Cubit data for use in OpenMC.
{{% content %}} 
* Case study: [Modeling of the Advanced Test Reactor Using OpenMC, Cubit, and Griffin](https://inldigitallibrary.inl.gov/sites/sti/sti/Sort_67479.pdf)
* Documentation about Cubit and OpenMC
{{% /content %}} 

### MCNP
The MCNP®, Monte Carlo N-Particle®, code can be used for general-purpose transport of many particles including neutrons, photons, and electrons. The transport of these particles is through a three-dimensional representation of materials defined in a constructive solid geometry. In addition, external structured and unstructured meshes can be used to define the problem geometry in a hybrid mode by embedding a mesh within a constructive solid geometry cell, providing an alternate path to defining complex geometry.

Coreform Cubit can be used to generate unstructured meshes for use in MCNP, as well as to generate surface CAD mesh data through its DAGMC functionality to be sent to MCNP.

{{% content %}} 
* Case study: [Using CUBIT to Create Unstructured Mesh Models for MCNP Simulations](https://permalink.lanl.gov/object/tr?what=info:lanl-repo/lareport/LA-UR-23-21715)
* Documentation about Cubit and MCNP
{{% /content %}} 

### ANSYS
ANSYS Mechanical is a leading finite element analysis software used across industries, including the nuclear industry. Meshes created in Coreform Cubit can be exported for simulations in ANSYS. 



## Meshing

### DAGMC
{{% content %}} 
* Tokamak tutorial
* Pin cell tutorial
* Unstructured mesh
{{% /content %}} 


## Licensing options
Commercial and academic licenses are available. Contact [sales@coreform.com](mailto:sales@coreform.com?subject=Sales%20Inquiry)


## Case studies
{{% content %}} 
* Published papers
* Radiant
{{% /content %}} 




{{% /section %}}


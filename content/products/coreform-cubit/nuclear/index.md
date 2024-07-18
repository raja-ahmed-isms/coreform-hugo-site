+++
title = "Coreform Cubit"
keywords = "fea, fea solver, implicit solver, fea implicit solver"
include_collapse = true
layout = "single"
special_page = true
+++

{{% hero background="banner.jpg" overlayimg="../cubitlogoTM.svg" class="floatleft" %}}
{{% / hero %}}

{{% section postSpace=3 %}}

# Advanced meshing for challenging simulations in nuclear energy

Maximize your investment in simulation technology in nuclear energy. [Coreform Cubit](/products/coreform-cubit/)’s pre-processing capabilities minimize the time spent on model preparation for neutronics, FEA, and CFD while maximizing control over mesh quality.


<figure>
<div style="display: flex; justify-content: center; align-items: center; margin: auto 0; padding: 0 200px;">
<img src="Radiant-Image-1.png" width="300" style="display: block; height: auto; max-width: 100%; margin-right: 40px"> 
&nbsp; 
<img src="Radiant-Image-2.png" width="250" style="display: block; height: auto; max-width: 100%; margin-left: 40px">
</div>
<figcaption style="text-align: center; padding-top: 2ex; padding-bottom: 4ex">Meshes created in Coreform Cubit for simulation in MOOSE, courtesy of Radiant Industries.</figcaption>
</figure>


Coreform Cubit is used in nuclear simulation workflows as a Python-scriptable meshing software with a simple graphical user experience. Coreform Cubit is often the recommended choice for government and open-sourced codes such as MOOSE, OpenMC, and MCNP. Coreform is the exclusive commercial distributor of the Sandia CUBIT&#174; code. Coreform Cubit has several enhancements to make it more suitable for commercial usage, including:

{{% content %}} 
* Direct CAD importers
* Fully integrated neutronics workflows
* Technical Support
* Responsive bug fixing


{{% /section %}}

{{% section postSpace=3 %}}


{{% content %}} 
<br>
{{% /content %}} 

## Applications

### Fusion
Coreform Cubit is used for fusion workflows, including neutronics, finite element analysis, and computational fluid dynamics. 
Because of Coreform Cubit’s origins in the US national laboratories, it has long been the choice of engineers and scientists doing groundbreaking research and development across industries, including fusion. 

As fusion private industry grows, researchers who have exited the labs and entered private industry transition are able to transition to commercial workflows by using Coreform Cubit. In addition to the familiar CUBIT&#174; workflows, Coreform Cubit offers import of commercial CAD files, including SolidWorks, Catia, NX, and PRO/E, fully integrated neutronics workflows, and technical support.

### Fission
Coreform Cubit is used by many leading nuclear fission companies to prepare complex parts for simulation. Coreform Cubit’s scriptable Python user interface, tight integration with MOOSE, advanced hex meshing schemes, and neutronics simulation workflows assist users preparing neutronics, FEA, CFD, and multiphysics workflows.


{{% content %}} 
<br>
{{% /content %}} 

## Codes

### MOOSE

<aside class="pquote" style="float:right">
	<p>For complicated geometries, we generally use CUBIT.<br> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &mdash;<a href="https://mooseframework.inl.gov/application_usage/mesh_block_type.html#:~:text=Creating%20a%20Mesh,-For%20complicated%20geometries%2C%20we">INL MOOSE homepage</a>
    </p>
    <img src="nuclear-radiant-sliced-core.png" width="300">
</aside>

MOOSE is an open-source, parallel finite element framework largely developed at Idaho National Laboratory. MOOSE is broadly used for nuclear fission research and development and is also used for nuclear fusion. MOOSE-based [applications](https://mooseframework.inl.gov/application_usage/tracked_apps.html) have been developed to enable the simulation of many types of nuclear physics. Coreform Cubit and MOOSE both read the Exodus file format natively, which leads to strong interoperability. This [case study](https://coreform.com/products/coreform-cubit/case-studies/radiant/) demonstrates how Radiant Nuclear uses Coreform Cubit and MOOSE together. Watch this [webinar](https://www.youtube.com/watch?v=EFsUSNlH118) to learn more about the MOOSE/Coreform Cubit workflow.



<div style="clear:both;"></div>

### OpenMC
OpenMC is an open-source Monte Carlo neutron and photon transport simulation code, developed largely out of MIT and Argonne National Laboratory. 


<figure style="float:right; display: table; margin-left: 30px;">
<img src="nuclear-tokamak.png" width="375"/>
<figcaption style="margin: 0 25px; padding-top: -20; display: table-caption; caption-side: bottom">Nuclear tokamak generated in Paramak. Learn how to set up and run an OpenMC simulation on this model with Coreform Cubit in <a href="/products/coreform-cubit/tutorials/dagmc/tutorial_1/index.html#tokamak-model">this tutorial</a>.</figcaption>
 </figure>
 

Coreform Cubit is used in OpenMC workflows to prepare CAD geometry for simulations without the need to recreate the model as constructive solid geometry (CSG). The DAGMC workflow is used to export the Cubit data for use in OpenMC.

{{% content %}} 

* Case study: [Modeling of the Advanced Test Reactor Using OpenMC, Cubit, and Griffin](https://inldigitallibrary.inl.gov/sites/sti/sti/Sort_67479.pdf)

{{% /content %}} 

<div style="clear:both;"></div>


### MCNP

The MCNP®, Monte Carlo N-Particle®, code can be used for general-purpose transport of many particles including neutrons, photons, and electrons. The transport of these particles is through a three-dimensional representation of materials defined in a constructive solid geometry. 

<figure>
<div style="display: flex; justify-content: center; align-items: center; margin: auto 0; padding: 0 200px;">
<img src="nuclear-candu-graphite-moderator-reflector-total-neutron-flux.png" width="300" style="display: block; height: auto; max-width: 100%; margin-right: 40px"> 
&nbsp; 
<img src="nuclear-candu-coolant-and-fuel-thermal-flux.png" width="200" style="display: block; height: auto; max-width: 100%; margin-left: 40px">
</div>
<figcaption style="text-align: center; padding-top: 2ex; padding-bottom: 4ex">Plots for neutron and prompt fission gamma fluxes for a gas-cooled, graphite moderated nuclear reactor. The system has been modeled with unstructured mesh generated in Coreform Cubit and simulated using MCNP6.3. These plots demonstrate the superiority of unstructured mesh modelling over constructive geometry. Courtesy <a href ="https://www.linkedin.com/in/ibrahim-khalil-attieh/">Ibrahim Attieh</a>.</figcaption>
</figure>

In addition, external structured and unstructured meshes can be used to define the problem geometry in a hybrid mode by embedding a mesh within a constructive solid geometry cell, providing an alternate path to defining complex geometry.

<figure>
<div style="display: flex; justify-content: center; align-items: center; margin: auto 0; padding: 0 200px;">
<img src="nuclear-candu-mesh-horizontal-view.jpg" width="300" style="display: block; height: auto; max-width: 100%; margin-right: 40px"> 
&nbsp; 
<img src="nuclear-candu-detailed-mesh-view.jpg" width="250" style="display: block; height: auto; max-width: 100%; margin-left: 40px">
</div>
<figcaption style="text-align: center; padding-top: 2ex; padding-bottom: 4ex">Hex meshes generated in Coreform Cubit. Courtesy <a href ="https://www.linkedin.com/in/ibrahim-khalil-attieh/">Ibrahim Attieh</a>.</figcaption>
</figure>

{{% vspace 2em %}}

Coreform Cubit can be used to generate unstructured meshes for use in MCNP, as well as to generate surface CAD mesh data through its DAGMC functionality to be sent to MCNP.

{{% content %}} 
* Case study: [Using CUBIT to Create Unstructured Mesh Models for MCNP Simulations](https://permalink.lanl.gov/object/tr?what=info:lanl-repo/lareport/LA-UR-23-21715)
{{% /content %}} 

<!-- ### ANSYS
ANSYS Mechanical is a leading finite element analysis software used across industries, including the nuclear industry. Meshes created in Coreform Cubit can be exported for simulations in ANSYS.  -->

{{% content %}} 
<br>
{{% /content %}} 

## Meshing

### DAGMC
{{% content %}} 
* [Coreform DAGMC Model Preparation and Export Tutorial](/products/coreform-cubit/tutorials/dagmc/tutorial_1/)
{{% /content %}} 

{{% content %}} 
<br>
{{% /content %}} 

## Licensing options
Commercial and academic licenses are available. Contact [sales@coreform.com](mailto:sales@coreform.com?subject=Sales%20Inquiry) for details.


## Free trial

Request a 30-day free trial of [Coreform Cubit](https://coreform.com/products/trial/).



 *CUBIT&#174; is a registered trademark of National Technology & Engineering Solutions, LLC.*


{{% /section %}}

{{% vspace "5em" %}}






{{% start-conversation title="Try Coreform Cubit" %}}
Contact us to learn more about Coreform Cubit, or try a 30-day free trial today.
{{% centering %}}
{{% button link=/products/trial/ label="Try Coreform Cubit" %}}
{{% /centering %}}
{{% /start-conversation %}}


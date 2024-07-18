

+++
title = "Challenging guided-wave ultrasound simulation relies on Coreform Cubit's high-precision meshing tools"
keywords = "coreform cubit, academic meshing software, engineering education"
description = "Mondaic Ltd uses Coreform Cubit to create digital twins of laminated composite materials and for simulation of guided-wave ultrasound using its core product Salvus, a Spectral-Element Method (SEM) based wave physics solver."
type = "case-studies"
featured_image = "mondaic-featured.png"
date = "2024-05-02"
summary="[Mondaic Ltd.](https://mondaic.com/), an innovative ETH Zurich spinoff established in 2018, specializes in high-resolution 3-D imaging through ultrasonic and geophysical wave simulations. Mondaic advances the state of the art in full waveform modeling and inversion across diverse scales and applications, including structural health monitoring (SHM), non-destructive testing (NDT), and geophysical imaging."
+++



{{% section %}}

{{% figure src="mondaic-logo.jpeg" width="200" class="floatright" caption="" %}}<br>

##### Background  
[Mondaic AG/Ltd](https://mondaic.com), an innovative spinoff of [ETH Zurich](https://ethz.ch/en.html) established in 2018, specializes in high-resolution 3-D imaging through ultrasonic and geophysical wave simulations. Originally focusing on applications in computational seismology, Mondaic's mission evolved to advancing the state of the art in full waveform modeling and inversion across diverse scales and applications, including structural health monitoring (SHM), non-destructive testing (NDT), and geophysical imaging. Central to these endeavors is the use of simulation technologies to accurately recreate complex wave propagation physics and investigate material properties under various conditions. This is achieved by pushing the boundaries of numerical wave propagation through Mondaic's main product Salvus, a Spectral-Element Method (SEM) based wave physics solver.

In the context of ultrasonic inspection for NDT and SHM, Mondaic has recently started to investigate the potential of guided-wave ultrasound for detecting defects and material characterization applied to carbon fiber-reinforced polymers (CFRP) components. The use of guided-wave tomography has the potential to both accelerate acquisition for large swaths of material, while providing additional quantitative insights into material properties that are hard to obtain from conventional ultrasound. Central to this is the requirement to accurately simulate the guided waves through complex 3D CFRPs, accounting for both the geometry and spatial variations in material properties, such as anisotropic elasticity and density.


<aside class="pquote">
	<p>The synergy between Coreform Cubit's advanced meshing capabilities and Salvus's powerful wave physics simulation offers a promising pathway for revolutionizing non-destructive testing and structural health monitoring practices, particularly in analyzing complex carbon fiber-reinforced polymer objects.
<br> &nbsp; &nbsp; &nbsp; &nbsp; &mdash;Christian Boehm, Co-founder, Mondaic Ltd</p>
</aside>

##### Problem
Mondaic’s simulation engine and core product Salvus is a flexible tool that can model wave physics for highly complex shapes and (spatially varying) anisotropic elastic materials. Discretizing the domain using conforming hexahedral meshes is crucial for Salvus to exploit the tensorized structure of the SEM basis, and to run efficiently on GPU accelerators. This is known to be a challenging problem for complex geometries, and it sparked Mondaic's interest in Coreform Cubit.

In a recent study together with researchers from ETH Zurich and the University of Applied Sciences and Arts Northwestern Switzerland, Mondaic focused on simulating the propagation of guided wave ultrasound through a CFRP-based T-Stringer. This is a type of construction that combines multiple orientations of CFRP layups to structurally reinforce against bending forces, typically used to reinforce panels in e.g. aircraft construction.

{{% figure src="mondaic-t-stringer-photo.png" width="500" caption="<strong>Figure 1 :</strong> The T-Stringer object discussed in this case study. It is suspended by strings to allow for real data acquisition with a laser doppler vibrometer without interactions with other objects." %}}

However, the geometry of these objects and their layup (the specific arrangement and orientation of the composite layers) is expected to have a strong influence on the physics of guided wave propagation. It is therefore essential that Salvus is fed an accurate digitization of the object to be studied, something that can’t be readily achieved through the built-in functionality for meshing geophysical domains of Salvus itself.
##### Solution
Mondaic was able to accurately recreate the CFRP T-Stringer object using Coreform Cubit. In broad strokes, this process is done the following way:

{{% content %}}
- Geometrical modeling of the cross section of the T-Stringer.
- Extruding the geometry to match the third dimension.
- Assign block IDs to distinguish object sections, such that varying material properties can be assigned.
- Mesh the cross section of the object.
- Analyze resultant elements and optimize for wave physics in the cross section.
- Extrude the mesh in the third dimension.
{{% /content %}}

The proper geometrical modeling and used meshing algorithms played a key part for generating computationally efficient meshes for the SEM-based solver. As Salvus simulates wave physics, part of the computational cost is governed by the Courant–Friedrichs–Lewy (CFL) criterion, a condition on the time step to minimum element size relationship. Conceptually, this condition dictates that the time step to be small enough that it prohibits waves to propagate too fast through the model.

{{% centering %}}
<!-- {{% figure src="mondaic-mesh-initial.png" width="300" caption="<strong>Figure 2: </strong> First iteration of the digitization. The central element of the T-Stringer is showing anomalously small elements that will impact the compuational performance of Salvus." %}} -->
{{% figure src="mondaic-mesh-initial.png" width="300" caption="<strong>Figure 2: </strong> First iteration of the meshing of the T-Stringer in Coreform Cubit. The central element of the T-Stringer is showing anomalously small elements that will impact the compuational performance of Salvus." %}}
<!-- {{% figure src="mondaic-mesh-final.png" width="300" caption="<strong>Figure 3: </strong> The final iteration of digitizing the T-Stringer. The elements in this mesh and specifically in the central element are now of much more uniform size." %}} -->
{{% figure src="mondaic-mesh-final.png" width="300" caption="<strong>Figure 3: </strong> The final iteration of digitizing the T-Stringer in Coreform Cubit. The elements in this mesh and specifically in the central element are now of much more uniform size." %}}
{{% /centering %}}

{{% vspace 2em %}}

It is therefore essential for Mondaic's mesh construction software to create meshes of arbitrary geometries without anomalously small and ill-conditioned elements. The visual interface to Coreform Cubit in combination with element statistics allowed their team to identify problematic regions of geometry, and to adapt local geometric features and meshing algorithms as to produce well-behaved meshes. This directly allows reductions in solver computational cost.

One such problematic region is the joining of the three separate plates at the center of the T-Stringer, as shown above in Figures 2 and 3. Although production specifications define this region to be constructed by 2 quarter circles and a line, this turned out to be both a poor approximation of the produced object and yield “pinched out” elements of abnormally small sizes.


##### Conclusion 
The use of the designed mesh in Mondaic's simulations has been instrumental in enhancing the performance and accuracy of Salvus when analyzing complex CFRP structures. Both the performance and accuracy are key factors controlling the viability of performing iterative guided wave tomography in any domain. 

{{% figure src="mondaic-stringer-sim.png" width="800" caption="<strong>Figure 4: </strong>The qualitative wavefields generated by a piezo-electric transducer placed on a laminated CFRP T-Stringer, visualized by vector magnitude of the velocity, with red corresponding to strong vibration, blue to weak, and translucent to no shaking. These wavefields are computed using Salvus on a mesh produced by Coreform Cubit. Of note is the effect of varying thickness of the material to the bottom left and the fin part of the T-Stringer on the physics of wave propagation, in an otherwise homogeneous material." %}}

The synergy between Coreform Cubit's advanced meshing capabilities and Salvus's powerful wave physics simulation offers a promising pathway for revolutionizing NDT and SHM practices, particularly in analyzing complex CFRP objects. The increased computational efficiency and unparalleled fidelity in material and object representation provided by Coreform Cubit are proving indispensable in pushing the boundaries of ultrasonic inspection technology.



{{% /section %}}

--- 


---  

### Coreform Cubit information

Visit the [product overview page](/products/coreform-cubit/) to learn more about Coreform Cubit. 

Sign up for a [free trial](/products/trial/) of Coreform Cubit. 

[Coreform Cubit Learn](../../free-meshing-software/) is a free limited license available for non-commercial student use.

[Contact Coreform](/company/contact) for more details: sales@coreform.com

<br>

---



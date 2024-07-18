+++
title = "Coreform Cubit’s high-quality hexahedral meshes used by engineering service provider for electron beam simulations"
keywords = "coreform cubit, academic meshing software, engineering education"
description = "Boltzplatz, developer of the open-source space simulation code PICLas, chooses Coreform Cubit for its combination of mesh quality, control, and ease-of-use for full hex meshes"
type = "case-studies"
featured_image = "electron.anode.png"
date = "2024-01-18"
summary="[boltzplatz](https://boltzplatz.eu/) is an engineering service provider and a spin-off from the University of Stuttgart, Germany. The company develops the open-source software PICLas, which was originally developed to simulate space systems, including atmospheric entry and in-space propulsion."
+++

{{% section %}}

{{% figure src="boltzplatz.logo.png" width="200" class="floatright" caption="" %}}<br>

 
<!--<blockquote class="pullquote" style="width: 350px; font: bold 1.333em/1.125em &quot;Open Sans&quot;, sans-serif; margin: 2.5em 2.5em 2.5em 2.5em !important; padding: 0.6em 5px !important; background: none !important; border: 3px double \#ddd; border-width: 3px 0; text-align: center; float: left; ">---<br>we found a perfect compromise among mesh quality, control, and ease of use with Coreform Cubit.<br>&mdash; Dr.-Ing. Paul Nizenkov, Managing director at boltzplatz<br>---</blockquote>-->



##### Background  
[boltzplatz](https://boltzplatz.eu/) is an engineering service provider and a spin-off from the University of Stuttgart, Germany. The company develops the open-source software PICLas, which was originally developed to simulate space systems, including atmospheric entry and in-space propulsion. PICLas allows the prediction of rarefied gas and plasma dynamics under the influence of electromagnetic forces. The software is used in the semiconductor manufacturing and vacuum coating industries. This approach reduces the need for costly prototypes and extensive testing, saving both time and expense. Beyond complete simulation projects, boltzplatz offers technical support and software development for PICLas. 

<aside class="pquote">
	<p>We found a perfect compromise among mesh quality, control, and ease of use with Coreform Cubit.<br> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &mdash;Dr.-Ing. Paul Nizenkov, Managing director and co-founder at boltzplatz</p>
</aside>

##### Problem
The methods employed in PICLas require high-quality hexahedral meshes for improved accuracy and to accelerate convergence, so it is always a matter of importance to select the right meshing tool. In the case of the electron beam generator, electrons are accelerated up to a significant fraction of the speed of light to evaporate materials in a vacuum coating process. The simulation setup requires not only high-quality hexes but also CAD geometry modification. 

The boltzplatz team considered several commercial mesh generators to generate full-hexahedral meshes. One utilized a multi-block approach and offered the required mesh quality and the possibility to fine-tune the number of cells. Unfortunately, it was cumbersome to use and did not offer the ability to modify CAD geometry. Another option offered a more automatic approach using hanging nodes, but it produced a prohibitive number of elements for complex geometries. 

{{% figure src="simdomain.webcuts.png" width="700" class="float" caption="Figure 1: Showing the complete simulation domain and its decomposition using Webcuts in Coreform Cubit."   %}}


##### Solution

The team found their desired mix of mesh quality, control, and ease of use in Coreform Cubit (see Figure 1). 

Their mesh generation process starts with a CAD file provided by their customers, but those files are, unfortunately, not usually suitable for meshing. Coreform Cubit offers an extensive cleanup and repair toolkit, which allows them to perform geometry simplifications and corrections directly in Coreform Cubit. 

{{% figure src="electron.anode.png" width="500" class="floatleft" caption="Figure 2: Showing a zoomed-in view of the electron source and acceleration anode, showing the high-quality mesh that was created by Coreform Cubit."   %}}

The mesh creation tools in Coreform Cubit provide excellent user control over mesh features, while still being straightforward and intuitive to use. The boltzplatz team frequently uses the Cubit webcut feature during the meshing process to break down complex geometries into simpler parts. After a successful mesh generation, they check the quality of the mesh utilizing Cubit’s scaled Jacobian parameter. Problematic mesh elements can be identified and easily treated with Cubit tools (see Figure 2). Once the mesh reaches the required quality, it is exported to the boltzplatz preprocessor to be converted for use with PICLas.
 

##### Conclusion

The simulation results have shown good agreement with experimental measurements, where the beam diameter has been compared (see Figure 3). This allows the customer to utilize the simulations predictively when investigating novel beam generator designs or improvements of existing products.

{{% figure src="results.png" width="500" class="floatleft" caption="Figure 3: The simulation result, including the electron beam generator (gray) and the electron beam itself (blue). Vectors depict the applied magnetic field. Electrons are generated in the lower left corner and accelerated through a potential difference. A magnetic field further downstream focuses the beam and guides it toward the generator exit. The simulation results show good agreement with experimental measurements, as shown in the lower right corner."   %}}
 
Plasma-based processes are crucial in technologies such as vacuum surface coating and semiconductor manufacturing. In this case, an electron beam generator has been successfully simulated by boltzplatz and the results show very good agreement with the experimental measurements. The high-quality meshes by Coreform Cubit combined with a short turnaround time enable future product optimizations through numerical simulations.



{{% /section %}}

---  

### Coreform Cubit information

Visit the [product overview page](/products/coreform-cubit/) to learn more about Coreform Cubit. 

Sign up for a [free trial](/products/trial/) of Coreform Cubit. 

[Coreform Cubit Learn](../../free-meshing-software/) is a free limited license available for non-commercial student use.

[Contact Coreform](/company/contact) for more details: sales@coreform.com

<br>

---


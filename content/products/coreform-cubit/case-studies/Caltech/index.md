+++
title = "Exploring geodynamics on planetary satellites with Coreform Cubit"
keywords = "coreform cubit, academic meshing software, engineering education"
description = "Coreform Cubit’s advanced meshing capabilities enable the California Institute of Technology to explore short-period crustal geodynamics on planetary satellites with complex 3D structures."
type = "case-studies"
featured_image = "images/bannerCal.png"
date = "2022-12-13"
summary="Dr. Mark Simons and his research group at Caltech conduct planetary-scale geodynamics research. They have developed finite-element models to probe crustal structure on planetary satellites, with a particular focus on Enceladus, a small (radius = 252 km), highly geologically active moon orbiting Saturn."
+++

{{% section %}}

<!--{{% figure src="images/caltechlogo.png" width="200" class="floatright" caption="" %}}<br> -->

 
<!-- <blockquote class="pullquote" style="width: 350px; font: bold 1.333em/1.125em &quot;Open Sans&quot;, sans-serif; margin: 2.5em 2.5em 2.5em 2.5em !important; padding: 0.6em 5px !important; background: none !important; border: 3px double \#ddd; border-width: 3px 0; text-align: center; float: left; ">---<br>Once you use Coreform Cubit for scripting automated mesh generation, you will never go back.<br>&mdash; Dr. Klaus Roppert<br>---</blockquote> -->



##### Background  
Dr. Mark Simons and his research group at Caltech conduct planetary-scale geodynamics research. They have developed finite-element models to probe crustal structure on planetary satellites, with a particular focus on Enceladus, a small (radius = 252 km), highly geologically active moon orbiting Saturn.

NASA’s Cassini probe identified active geysers nearby Enceladus’s South Pole emanating from four large, evenly spaced fractures (informally known as “Tiger Stripes,” see Figure 1) powered by regular, large-amplitude solid tides. Enceladus’s geysers may emanate from a subsurface ocean beneath its ice shell, capable of harboring extraterrestrial life. As such, Enceladus is a high-priority target for future spacecraft missions. These missions require accurate models of tidally driven deformation to inform measurement requirements and to develop testable hypotheses.

{{% figure src="images/moon.png" width="300" class="floatright" caption="Figure 1: Image of the south pole of the Saturn moon Enceladus from the NASA Cassini probe. Note the prominent “Tiger Stripe” fractures. Credit: NASA/JPL-Caltech/University of Arizona/ LPG/ CNRS/ University of Nantes/Space Science Institute." %}}<br>

<aside class="pquote">
	<p>Coreform Cubit is our long-time first choice to generate meshes for our most challenging simulation problems.<br> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &mdash;Dr. Mark Simons, Caltech and NASA Jet Propulsion Laboratory.</p>
</aside>

##### Problem
Enceladus’s periodic deformation is sensitive to three-dimensional structure in the body’s outer crust. For example, Enceladus’s Tiger Stripe faults and large-amplitude (>10 km) lateral variations in crustal thickness contribute to structural heterogeneities that significantly amplify diurnal oscillations in strain near its south pole. Whereas analytic tools enable first-order approximations of tidal deformation on spherically symmetric models, finite element analysis (FEA) formulations can readily incorporate structural heterogeneities. As such, sophisticated FEA models enable a detailed exploration of the relationship between tidal deformation and crustal structure at Enceladus.

FEA models of Enceladus must simulate deformation on quasi-spherical geometries with topological variations at material interfaces and quasi-2D faults at user-specified locations/depths. Modifying interface topology regularly introduces ill-conditioned mesh geometries that requires untangling for stable simulations. Moreover, fault generation on spherical interfaces requires a diverse palette of solid modeling tools (e.g., extruding, chopping, and skinning geometries) to explore a broad range of model classes. 


##### Solution

Coreform Cubit’s functionality enabled the complex meshing necessary to generate FEA models of Enceladus. The Caltech team, led by PhD candidate Alexander Berne, ran Coreform Cubit in-line with the finite-element code PyLith, an open-source tool broadly used in the terrestrial crustal dynamics community. Each run interfaced Coreform Cubit in batch mode) and PyLith at a series of user-specified time points to create and update mesh geometries dynamically within simulations to minimize compute burden. Cell size was refined according to dynamically generated fields such as strain.

{{% figure src="images/tiger.png" width="800" class="floatright" caption="Figure 2: Coreform Cubit was used to create this model of Enceladus, capturing the “Tiger Stripe” fault interfaces at user-specified latitude and longitudes (left), and the lateral variations in crustal thickness (right)." %}}<br>

To generate the fault geometries, curves were created and skinned from a series of vertices at user-specified latitude and longitude coordinates taken from geological interpretation of the Cassini image data. These were subsequently extruded and cut by a spherical shell. The subtracted volumes formed a smooth surface penetrating the length of the shell, which allowed for split-node formulations of fault behavior (See Figure 2). This workflow for interface generation enabled straightforward simulation of faults that do not fully penetrate the full length of the crust, deviate from the radial direction, and exhibit a surrounding “weak” area (e.g., region of reduced elastic modulus). For lateral variations in crustal thickness, meshed spherically symmetric shell geometries were exported from Coreform Cubit, and outer and inner surfaces were modified with internal CalTech tools by radially adjusting node locations according to a shape model (See Figure 2 below). Once accomplished, the meshes were re-uploaded to Coreform Cubit to undergo a condition number smoothing procedure.

Using the models generated in Coreform Cubit, Caltech simulated tidal deformation of a wide variety of models of Enceladus (see Figure 2). Quantitative descriptions of displacement and strain fields were extracted both globally and local to individual structural heterogeneities. These simulations allowed Caltech to formulate products that will inform future geodetic missions to Enceladus (see Figure 3, below) and to broadly explore the satellite’s short period geodynamics and evolution.

{{% figure src="images/tidal_featured.png" width="600" class="floatright" caption="Figure 3: Examples of simulations of tidal deformation using geometries generated in Coreform Cubit. Displacements on models with faults and lateral variations in crustal thickness (left) and associated quasi- interferograms (right). Interferograms are data products from orbiting radars designed to track displacements from repeated EM reflections emitted by spacecraft." %}}<br>
 

<!-- {{% figure src="" width="" class="floatleft" caption=""   %}} -->

##### Conclusion

Modeling Enceladus comes with a host of difficult challenges. Dr. Mark Simons and his planetary geodesy-focused research team at Caltech relied on Coreform Cubit to enable them to model this interesting moon and its complex geodesic characteristics.

[View the webinar here.](https://youtu.be/8LroC-yFvak)

{{% /section %}}

--- 

{{% figure src="../PDF_32.png" caption="[Download as PDF](final-CalTech-case-study.pdf)" %}}

---  

### Coreform Cubit information

Visit the [product overview page](/products/coreform-cubit/) to learn more about Coreform Cubit. 

Sign up for a [free trial](/products/trial/) of Coreform Cubit. 

[Coreform Cubit Learn](../../free-meshing-software/) is a free limited license available for non-commercial student use.

[Contact Coreform](/company/contact) for more details: sales@coreform.com

<br>

---


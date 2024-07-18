+++
title = "Simulating the kidney filtration barrier with Coreform Cubit"
keywords = "coreform cubit, academic meshing software, engineering education, biomedical"
description = "University of Buffalo research group uses Coreform Cubit to mesh microscale kidney anatomy for modeling filtration through the tissue."
type = "case-studies"
featured_image = "featured.png"
date = "2023-10-23"
summary="Nicholas Glover is a Ph.D. student in the Systems Biomedicine and Pharmaceutics Laboratory led by Dr. Ashlee N. Ford Versypt, where he works on the mechanisms of chemical and biomechanical damage in kidney tissues during diabetic kidney disease."
+++

{{% section %}}

<!-- ### Engineering instructor teaches multiphysics simulation using Coreform Cubit -->

{{% figure src="UB.logo.jpg" width="200" class="floatright" caption="" %}}<br>

<!-- {{% figure src="tugrazigte2.png" width="300" class="floatright" caption="" %}} -->

##### BACKGROUND 
Nicholas Glover is a Ph.D. student in the Systems Biomedicine and Pharmaceutics Laboratory led by Dr. Ashlee N. Ford Versypt, where he works on the mechanisms of chemical and biomechanical damage in kidney tissues during diabetic kidney disease.  During his masters project in this research group, Nicholas studied the biomechanical properties of the glomerulus, the major anatomical structure of the kidney responsible for filtering wastes from the blood, which is the primary target for diabetic tissue damage. He sought to establish a connection between morphological changes in the anatomical structure as a result of disease and the resulting changes in the transport of filtrate chemicals as they move through the glomerular filtration barrier to better understand the progression of diabetic kidney damage. To simulate these processes in a computational model, FEBio, a finite element analysis software for biomechanics and computational fluid dynamics (CFD) applications, was used. Coreform Cubit provided an intuitive platform for meshing the geometry for the model, especially in the more complex biological tissues containing fine anatomical details. Without the capabilities of Coreform Cubit, many important details would be lost in rendering coarser, more simplified models of the anatomy.

<aside class="pquote">
	<p>Coreform Cubit provided an intuitive platform for meshing the geometry for the model, especially in the more complex biological tissues containing fine anatomical details.<br> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &mdash; Nicholas Glover, graduate student at UB</p>
</aside>

The significance of the project is in connecting several processes that have been shown individually to contribute to injury in kidneys due to diabetes into a sophisticated computer simulation. This computational tool will aid in understanding how the processes interact and how diabetic kidney damage begins and changes over time. In the long-term, results from this project will help to predict the impacts of many competing factors on kidney health during diabetes management. This information can be used to guide effective treatment plans for patients as well as predict and monitor disease states in a non-invasive manner. 

<!-- <blockquote class="pullquote" style="width: 350px; font: bold 1.333em/1.125em &quot;Open Sans&quot;, sans-serif; margin: 2.5em 2.5em 2.5em 2.5em !important; padding: 0.6em 5px !important; background: none !important; border: 3px double \#ddd; border-width: 3px 0; text-align: center; float: left; ">---<br>Once you use Coreform Cubit for scripting automated mesh generation, you will never go back.<br>&mdash; Dr. Klaus Roppert<br>---</blockquote> -->
##### THE PROBLEM

As the function of a kidney to filter wastes from the blood is dependent on the various physical filtering layers, a 3D representative geometry of the filtering structure is first needed to simulate the filtration process. To build such a geometry of the system, biological images from kidney glomeruli were used to construct multiple disease state geometries using structure parameters derived from the images. To have an accurate CFD model for the layered filtration barrier, meshing software that provides a **high-quality mesh** was needed. Some of the layers are the gaps between biological cells of different sizes and shapes, and others are more amorphous porous materials. So it is important to also have **the ability to make element sets** that correspond to the various biological materials present in the model for the different layers. 

{{% figure src="kidneyfig1.png" width="415" class="floatright" caption="Geometry of the glomerular filtration barrier and the<br> appropriate boundary conditions associated with each<br> surface to be meshed in Coreform Cubit. The blue regions<br> are fluid domains, the small channels are between kidney<br> cells, and the yellow and green regions are biphasic<br> porous materials with different material properties." %}}

The limitations of other available software were the lack of meshing tools to accommodate the complex glomerulus structure and non-intuitive user interfaces. For a beginner in the realm of CFD modeling, Coreform Cubit provided Nicholas with an excellent platform via the **intuitive GUI** to mesh the desired complex biological tissue (see Figure 1). 


##### THE SOLUTION

Previously published anatomical measurements were used as the geometry for the solute transport study through the glomerular filtration barrier. These measurements were used to create a domain that filtrate (water, small molecules, and– in disease conditions– proteins) passes through. The domain consists of layers of fluid channels and porous media. In the FEBio finite element analysis software, the CFD module was used for the channels, and the multiphasic (fluid/solid) module was used for the porous media. Before running the multiphysics problem in the FEBio software, it was necessary to create an appropriate mesh for the problem. Coreform Cubit was used to create a high-quality mesh (See Figure 2) for the application, which captures the fine geometry of the layers and contains important information at the boundaries of the domains in the model, indicating contact between the filtrate and cell surfaces that form the channels of the glomerular filtration barrier. 

{{% figure src="kidneyfig2.png" width="350" class="floatleft" caption="High-quality mesh of the glomerular filtration<br> barrier produced by Coreform Cubit. The meshing<br> captured fine features of the narrow channels,<br>  allowed biasing near the domain boundaries,<br> and had coarser resolution in the middle of large<br> fluid domains." %}}

Adapting previously published theoretical formulations for multiphasic transport in the kidney, physical properties were assigned to the layers, and interface conditions were selected that best simulate physiological conditions in the kidney. Within the model there are biphasic and fluid domains, a normal fluid velocity calculated from a single nephron glomerular filtration rate, and boundary conditions that dictate fluid behaviors on cell-filtrate interactions. FEBio’s multiphasic module provided information about the test solute, which had physical properties analogous to the protein of interest, albumin. Albumin is detected in patients when it leaks through the filtration barrier, and it signals irreversible damage to the kidneys. As the research in the kidney project progresses, and the team implements more variations in biological structures, the use of the **Coreform Cubit unique “sculpt” function** will allow for easy-to-use and efficient hexahedral meshing options for the diseased geometries.


##### CONCLUSION
To assess the relative contribution of the various filtering layers of the glomerular filtration barrier, multiple models were made to test for the relative contribution of each filtering layer of the glomerular filtration barrier. From the results, Nicholas was able to assess how glomerular structures in each layer affect the transport of albumin within the domain (Fig 3).  

{{% figure src="kidneyfig3.png" width="500" class="floatright" caption="Leaking protein profile down the z-direction of the<br> glomerular filtration barrier model. The protein<br> concentrations are dependent on morphological<br> structures with meshes generated by Coreform Cubit<br> modeled using the multiphasic module of FEBio.<br> The geometries progress down the left column towards<br> increasing biological realism. The results generated<br> on the right column provide ever more accurate<br> simulations of the clinically important amount of<br> protein that leaks through the filtration barrier,<br> providing a diagnostic indicator mapped to<br> disease progression." %}}

Through support from the NSF CAREER Award, Dr. Ford Versypt’s lab is developing a multiscale model of a virtual kidney during the onset and progression of diabetic kidney disease. Multiscale modeling considers chemical, biological, and physical phenomena that depend on multiple spatial and temporal scales. Before Nicholas’ part of the project, the focus was primarily chemical and biological. Nicholas’ project addressed the physical barriers for filtration and provides an important foundation for starting to couple the dynamic chemical processes that influence the morphological and material properties of the kidney anatomical layers to the biomechanics of those layers, which in turn affect filtration. Now with the Coreform Cubit meshes and the simulation workflow using these meshes, the team can simulate protein leakage, which is the quantity that healthcare professionals use to track disease progression. This will enable the team to develop the next generation of software tools for predicting disease progression in advance of clinical detection of the protein leakage associated with permanent damage.


{{% /section %}}

--- 

### Coreform Cubit academic options

[Coreform Cubit Learn](.//../../free-meshing-software/) is a free limited license available for non-commercial student use.

The [Coreform Cubit academic licensing](../../../educational/) allows for discounted versions of Cubit for academic research and teaching. 

[Contact Coreform](/company/contact) for more details: sales@coreform.com



<br>

+++
title = "Geothermal reservoir simulation with Coreform Cubit"
keywords = "coreform cubit, academic meshing software, engineering education, geothermal, MOOSE"
description = "Dr. Biao Li of Concordia University used Coreform Cubit’s advanced hex meshing and powerful scripting to forecast the behavior of a geothermal energy reservoir."
type = "case-studies"
featured_image = "images/bannerConcorida.png"
date = "2023-04-26"
summary="Concordia University's Dr. Biao Li studies complex thermo-hydro-mechanical (THM) coupled processes associated with geothermal energy sources. Simulation of these processes requires a high degree of control over the mesh. Coreform Cubit provided all the necessary tools, allowing researchers to fine-tune mesh quality and achieve accurate simulations."
+++

{{% section %}}

<!--{{% figure src="images/caltechlogo.png" width="200" class="floatright" caption="" %}}<br> -->

 
<!-- <blockquote class="pullquote" style="width: 350px; font: bold 1.333em/1.125em &quot;Open Sans&quot;, sans-serif; margin: 2.5em 2.5em 2.5em 2.5em !important; padding: 0.6em 5px !important; background: none !important; border: 3px double \#ddd; border-width: 3px 0; text-align: center; float: left; ">---<br>Once you use Coreform Cubit for scripting automated mesh generation, you will never go back.<br>&mdash; Dr. Klaus Roppert<br>---</blockquote> -->



##### Background  
In an ongoing project, the director of Concordia’s Energy and Environmental Geomechanics Group, Dr. Biao Li, aims to forecast the long-term viability and behavior of a proposed geothermal energy reservoir in northern Quebec. At issue is whether this reservoir can safely meet the demands of local electricity and heating systems. To investigate the potential impact of the activities undertaken to harvest this geo-energy on the main fault zone, Coreform Cubit was used with the open-source MOOSE code to perform finite element numerical simulation of thermo-hydro-mechanical (THM) coupled processes. 



<aside class="pquote">
	<p>Coreform Cubit’s journaling capabilities were critical to our success. They allowed our researchers to not only share their geometry and mesh with colleagues, but also to modify the geometry and mesh quickly and easily.<br> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &mdash;Dr. Biao Li, Concorida University.</p>
</aside>



##### Problem
Geothermal reservoirs provide a potentially cheap and efficient form of completely clean and renewable energy. Unfortunately, the initial costs of accessing a suitable location and building infrastructure to transform and transfer the generated energy are high. Understanding the behavior of multicomponent (fractured) porous rock-fluid systems and their multiphysics dynamics is essential for making accurate predictions about how subsurface reservoirs will behave. This matters whether they are being used to harvest geothermal energy and fossil fuels, to store carbon dioxide, or as nuclear waste disposals. In such studies, the physical process that controls the geomaterial behavior is complex and nonlinear. Such simulations must capture the effects of thermo-elastic responses of geomaterials located near the injection fractures, which are affected by the cooling-off effect resulting from injection of cold fluid into a hot reservoir. 
 

##### Solution

{{% figure src="images/fractures.png" width="600" class="floatright" caption="(a) The main geological setting for this project consists of a fault with two fractures. (b) The offset layers and their depths.(c) Coreform Cubit was used to tet mesh the entire geological setting for preliminary results, which were encouraging. (d) Coreform Cubit was used to hex mesh the fault for a more advanced analysis.." %}}<br>

Dr. Biao Li and his team selected Coreform Cubit as the geometry and mesh generation software due to its user-friendly environment, wide selection of meshing algorithms for both surface meshing and volume meshing purposes, built-in tools that suggest the optimal meshing scheme, and most significantly, its ability to fine-tune the mesh quality. 

**The power of Coreform Cubit’s mesh quality tools**

This project called for a high degree of control over the mesh. Coreform Cubit provided all the necessary control, and allowed the team to prepare an accurate mesh for simulation in a MOOSE-based solver called GOLEM. The specific capabilities leveraged for this project included the following:  defining the direction of the mesh (by sweep direction, or defining a source and target surfaces); determining the internal growth rate of the mesh; adjusting the number of elements clustered around a single feature on the domain; refining the mesh of a single surface or curve in order to have more nodes; and finally, changing the location of a node (or introducing a node to a geometry) to establish a source or sink point. 
A 3D geological matrix contains fractures and faults (2D elements), wells (1D elements), and source/sink points(0D components). Coreform Cubit's meshing algorithms are tailored to precisely superimpose geological features onto lower and higher dimensional parts. With this function, the user can properly simulate the three-dimensional flow within the reservoir matrix, including the impacts of discrete flow path interactions along these interacting components.
Furthermore, the entire project was facilitated by Coreform Cubit’s advanced scripting capabilities, which allow for streamlined changes to the geometry and mesh and efficient sharing of data among team members. 
This project required tet meshing of the enormous reservoir system, and hex meshing of the fault line. Coreform Cubit was optimal for both.

**Tet meshing**
For the analysis of a geological model containing faults and fractures, the team opted to use tetrahedral elements and the Delaunay tet meshing technique. This is because tet meshing schemes minimize the need to simplify geometry for simulation. Hex meshing, for example, would not be able to efficiently represent the geological heterogeneities such as  wells, fractures and faults, whereas the tet scheme does. Coreform Cubit's tetrahedral meshing schemes make it possible to combine features of varying sizes into a single unified mesh.

<br>{{% figure src="images/twowells.png" width="600" class="center" caption="Side view of the main geologic setting with two wells. Tet meshing was done with Coreform Cubit." %}}<br>

At a more advanced stage of the project, the team investigated the seismic activity that might be caused by introducing cold fluid into a hot reservoir. This necessitated the addition of wells into the model, which introduced further complications including a change in dimension to 4×4×0.5 km. With the introduction of the wells, it became necessary to greatly simplify the geometry, especially as one of the wells will intersect the fault. With these geometry simplifications, they were able to reduce the number of elements to around 3 million. 

{{% figure src="images/well.png" width="600" class="floatright" caption="An overhead view of the two proposed wells. Coreform Cubit was able to give the team great control over the mesh, including defining the direction of the mesh, adjusting the number of elements around a single feature, refining the mesh in a local region in order to have more nodes, and establishing a source or sink point.." %}}<br>

**Hex Meshing**

To study the slip tendency of the fault with different dip angles, the team chose to use a hex meshing scheme. Coreform Cubit’s hex meshing algorithm provides better and more accurate results than tet meshes. They used the sweep feature to set the target and source surfaces to get the desired level of accuracy along the fault line. The figure below shows a comparison between the use of tetrahedral and hexahedral elements in this problem. 

**Using Coreform Cubit and MOOSE-based applications**
Due to the complexity and nonlinearity of the physical processes controlling the geomaterial behavior, a cutting-edge open-source numerical simulator called GOLEM was used for the study. GOLEM is built on an open-source Multiphysics Object Oriented Simulation Environment framework called MOOSE, which enables the user to find the solution of coupled, non-linear, partial differential systems equations. MOOSE can solve the PDEs implicitly and fully coupled in their weak sense.

{{% figure src="images/faultline.png" width="500" class="floatright" caption="Meshing the fault line problem. The top image shows the fault meshed with tetrahedral elements. The bottom image shows the fault line meshed with hexahedral elements. Better results are derived from the hex mesh." %}}<br>

**Simulation Results**
The extension of the domain is determined to capture the effects of thermo-elastic responses of geomaterials located near the injection fractures, which are affected by the cooling-off effect resulting from injection of cold fluid into a hot reservoir. However, coarse meshes cannot well characterize the actual behavior of a fracture.  Using Coreform Cubit, the feature of a specific fracture can be well described by adjusting an appropriate mesh size for the fracture separately.
Preliminary results with a coarse tet mesh of the geological region have indicated that it is possible that a geothermal energy reservoir at the desired depth can answer the energy needs of the communities living in Kuujjuaq, Quebec. Preliminary results on the fault line also indicate that the geological setting in question is a good candidate for a geothermal energy reservoir. 




{{% figure src="images/hexmesh.png" width="600" class="floatright" caption="Showing the Coreform Cubit generated hex mesh that was used for successful simulation of the fault line. Results justify continued analysis of this potential geothermal energy reservoir." %}}<br>
 

<!-- {{% figure src="" width="" class="floatleft" caption=""   %}} -->

##### Conclusion

Geological matrices are complex and difficult to mesh for simulation. Coreform Cubit provided the required mesh control and element quality to get useful results about a potential geothermal energy reservoir. 


{{% /section %}}

--- 

{{% figure src="../PDF_32.png" caption="[Download as PDF](concordia.pdf)" %}}

---  

### Coreform Cubit information

Visit the [product overview page](/products/coreform-cubit/) to learn more about Coreform Cubit. 

Sign up for a [free trial](/products/trial/) of Coreform Cubit. 

[Coreform Cubit Learn](../../free-meshing-software/) is a free limited license available for non-commercial student use.

[Contact Coreform](/company/contact) for more details: sales@coreform.com

<br>

---


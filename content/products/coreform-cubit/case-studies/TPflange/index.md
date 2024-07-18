+++
title = "Python scripting in Coreform Cubit improves manufacturer’s workflow"
keywords = "coreform cubit, academic meshing software, engineering education, python"
description = "Manufacturer of highly complex mechanical components uses Coreform Cubit to prepare mission-critical parts for simulation"
type = "case-studies"
featured_image = "TPflange1.png"
date = "2023-07-18"
summary="[TP Products](https://tp-products.com/)  manufactures highly complex mechanical components for the most demanding clients and settings. Simulation is an important part of their design process and testing, and they rely on Coreform Cubit scripting capabilities to efficiently automate hex meshes for quality simulations."
+++

{{% section %}}

 
<!-- <blockquote class="pullquote" style="width: 350px; font: bold 1.333em/1.125em &quot;Open Sans&quot;, sans-serif; margin: 2.5em 2.5em 2.5em 2.5em !important; padding: 0.6em 5px !important; background: none !important; border: 3px double \#ddd; border-width: 3px 0; text-align: center; float: left; ">---<br>The ability of Coreform Cubit to fully automate the hex meshing process by Python scripting has been vital for us to be able to perform the large volume of finite element analyses required to fully qualify the Ultra Compact Flanges as quickly as possible. We have found the Python API of Coreform Cubit to be extremely powerful and easy to use, allowing us to parametrically build the mesh, then automatically define all necessary face sets for boundary conditions and loads, and export the mesh to the FEA software.<br>&mdash; Sondre Tjessem, Computational Engineer (MSc)<br>---</blockquote> -->

{{% figure src="LOGO-TP-ORIGINAL.png" width="100" class="floatright" caption="" %}}<br>

##### Background  
[TP Products](https://tp-products.com/)  manufactures highly complex mechanical components for the most demanding clients and settings. Their portfolio includes custom high-pressure compact connectors, innovative wind power flanges, and subsea valves, stabs and jumpers. Design, engineering, manufacturing, and testing of its products is all done in-house at their facilities in Drammen, Norway. Simulation is an important part of their design process and testing, and they rely on Coreform Cubit to efficiently create hex meshes for quality simulations. Coreform Cubit allows them to automate and script the creation of hex meshes, based on changing parameters of the size and material requirements of the flanges. 

<aside class="pquote">
	<p>The ability of Coreform Cubit to fully automate the hex meshing process by Python scripting has been vital for us to be able to perform the large volume of finite element analyses required to fully qualify the Ultra Compact Flanges as quickly as possible. We have found the Python API of Coreform Cubit to be extremely powerful and easy to use, allowing us to parametrically build the mesh, then automatically define all necessary face sets for boundary conditions and loads, and export the mesh to the FEA software.<br> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &mdash;Sondre Tjessem, Computational Engineer (MSc)</p>
</aside>

##### Problem

The compact flange experts at TP-Products recently developed a groundbreaking series of TPC Ultra Compact Connections that are based on the same principles as traditional products but with more environmentally friendly connections. The Ultra Compact connections are smaller, lighter and have higher intercompatibility, resulting in lower costs at every stage from manufacturing to transport. Because the Ultra Compact Flange series has been designed to connect a wide variety of pipe sizes and types, the potential combinations of sizes, materials, and component configurations number in the thousands.  The flanges can connect pipes of sizes from 0.5” to 36” in outer diameter, and are available as weld neck flanges, swivel flanges, and blind flanges.  Additionally, the flanges can be manufactured in a variety of different steels, such as carbon steels, stainless steels and Duplex steels, which all result in different pipe wall thicknesses. 

{{% figure src="TPflange2.png" width="500" caption="Illustration of a TPC Ultra Compact Connection, consisting of a weld neck flange and a swivel flange, with a cross-sectional view to the right." %}}

To produce a reliable documentation of the functionality and structural capacity of the flanges, a simulation is needed for each of the different type, size, and material combinations. Generating a high quality hex mesh manually for each combination would be prohibitively time consuming, so TP-Products sought a software to automate this process. The flanges are subjected to different types of loading - both symmetric loads such as internal pressure and tension loads through the pipes, and asymmetric loads such as bending moments and torsion. Each load requires a unique mesh.
 


##### Solution

## Advanced hex meshing for challenging simulations: 
To evaluate the performance of a given flange design, TP-Products must evaluate how forces are distributed on different sectors of the flange. Due to the way the outputs are taken from the CalculiX FEA solver, this means that separate faces on the mesh must be defined individually. With Coreform Cubit and Python, TP-Products can automatically meet the other requirements for mesh quality and organization required by Calculix. For the larger models with a large number of bolts, doing this process manually would have been extremely inefficient and time-consuming.  

{{% figure src="featured.png" width="400" caption="Mesh generated by Python scripting in Coreform Cubit of a TPC Ultra Compact Connection, consisting of a weld neck flange and a swivel flange, to be used in a bending moment simulation in CalculiX." %}}

## Powerful scripting & process automation for maximum simulation throughput: 
By using the Python API in Coreform Cubit, TP-Products is able to automatically generate high-quality hex meshes of the flanges from a set of input parameters including desired dimensions, bolt sizes and number of bolts. The mesh is then exported to Mecway (a comprehensive FEA package) for application and visualization of loads and boundary conditions, and an input file to the CalculiX FEA solver is written. 

The figures below show the results of analysis for an advanced weld neck flange design. Based on these analyses, the design of flanges can be verified throughout the entire size range, with different materials and for all component combinations, and within a timeframe that would not be possible without the automation of the meshing.


{{% figure src="TPflange4.png" width="400" caption="Von Mises stresses at time step 3 in the analysis of the weld neck flange, where a 1552.5 bar internal pressure has been applied. This is a test pressure, which is 1.5 times higher than the design pressure of the flange." %}}


{{% figure src="TPflange3chart.png" width="400" caption="Plot showing the development bolt force, and heel and toe contact forces, for different time steps in the analysis of a 12” weld neck against swivel flange. Outputs shown are from the CalculiX solver, based on the sidesets defined in the Coreform Cubit mesh. These curves are extremely valuable in assessing the performance of the flanges." %}}
 

<!-- {{% figure src="" width="" class="floatleft" caption=""   %}} -->

##### Conclusion

TP-Products relies on Coreform Cubit to help them design, engineer, and manufacture mission-critical parts such as deep sea connectors and wind turbine flanges. Coreform Cubit’s advanced hex meshing capabilities and Python API are equal to even the most complex and demanding tasks. Coreform Cubit is a critical part of the TP-Products simulation workflow, and has been used to generate hex meshes in several hundred simulations.


{{% /section %}}

--- 

<!-- {{% figure src="../PDF_32.png" caption="[Download as PDF](AkselosCS.pdf)" %}}-->

---  

### Coreform Cubit information

Visit the [product overview page](/products/coreform-cubit/) to learn more about Coreform Cubit. 

Sign up for a [free trial](/products/trial/) of Coreform Cubit. 

[Coreform Cubit Learn](../../free-meshing-software/) is a free limited license available for non-commercial student use.

[Contact Coreform](/company/contact) for more details: sales@coreform.com

<br>

---


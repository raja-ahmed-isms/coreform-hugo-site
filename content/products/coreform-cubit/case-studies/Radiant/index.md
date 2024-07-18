+++
title = "Nuclear energy startup accelerates reactor design with Coreform Cubit"
keywords = "coreform cubit, academic meshing software, engineering education"
description = "Radiant uses Coreform Cubit as a critical part of its process to design a safe, portable nuclear microreactor."
type = "case-studies"
featured_image = "images/top.png"
summary="[Radiant Industries, Inc.](https://www.radiantnuclear.com/) was founded by former SpaceX engineers in 2019 to develop a portable and economical nuclear reactor.Coreform Cubit was the obvious choice for Radiant because of its leading hex meshing tools, Python scripting, and strong integration with leading nuclear industry simulation codes."
+++


{{% section %}}

##### Background
[Radiant Industries, Inc.](https://www.radiantnuclear.com/) was founded by former SpaceX engineers in 2019 to develop a portable and economical nuclear reactor. Their patent-pending design will allow nuclear energy to serve remote regions of the globe, forward-operating military bases, disaster areas, and critical services that otherwise require more expensive and greenhouse-unfriendly energy solutions.

<aside class="pquote">
	<p>Coreform Cubit is critical to our ability to iterate quickly by enabling rapid meshing of complex geometries thanks to its API, compatibility with other modern tools, and its underlying capabilities.<br> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp;&mdash;Paul Keutelian, Radiant COO</p>
</aside>

##### Problem
Radiant plans to perform fueled tests of their new reactor design in five years, twice as fast as reactors currently in development in the microreactor market. To accelerate progress, Radiant engineers have developed a highly optimized workflow relying on cutting-edge technology to minimize time spent on complex but repetitive tasks. Radiant is using modern modeling and simulation tools, including the MOOSE Framework and the NEAMS NEKRS code, which requires versatile meshing of component geometries to satisfy the unique demands of each of these codes. Because of the complex nature of Radiant’s designs and their need to iterate and optimize, Radiant requires the ability to create scripts to automatically mesh the model, as well as the ability to access a suite of semi-automatic tools to hand-craft hard-to-mesh models with optimal element quality.

##### Solution
Coreform Cubit is the obvious choice for Radiant because of its leading hex meshing tools, Python scripting, and strong integration with MOOSE and other NEAMS codes. Coreform Cubit provides advanced meshing for challenging simulations through a comprehensive toolset for high-quality FEA and CFD mesh generation. Both Coreform Cubit and MOOSE natively use the Exodus file format and Coreform Cubit can also import complex geometries from other industry tools. Cubit is widely used in the nuclear energy research community, reducing training costs for the existing Radiant team and new hires.

Radiant leveraged the state-of-the-art semi-automatic hex meshing capabilities in Coreform Cubit to build structured meshes that are required by various solvers and applications.

Radiant built a fully automatic, scripted process to use Coreform Cubit to mesh models for simulation to significantly increase simulation throughput. Radiant estimates that the time saved with Cubit over the other solutions they were evaluating saved over a month of model preparation time for the initial models, which is critical to get their product to market. With the automation built into assembling and meshing geometry, propagating a change from 3D model to a simulation-ready mesh takes on the order of minutes. Below are several examples of Radiant meshes prepared in Coreform Cubit.

{{% figure src="images/Picture1.png" width="500" caption="Figure 1. Fluid plenum meshed in Coreform Cubit in core surrogate to simulate turn in flow. This model is a multiple-inlet, single-outlet forced direction change flow to be simulated in Nek5000, a particularly challenging meshing problem. Work is ongoing to build a fully hexahedral mesh for this problem, and with Coreform Cubit’s interface, it is possible to iterate through many strategies to converge to the most efficient solution."%}}



{{% figure src="images/Core_transparent.png" width="500" caption="Figure 2. Programmatically generated core mesh modeled in Coreform Cubit for thermohydraulic modeling in MOOSE. This is a medium-fidelity model of the Radiant reactor core with some computational simplifications in the geometry. Using the Python API in Coreform Cubit, this fully procedurally generated mesh takes only a few seconds to update and remesh, ideal for rapid simulation iterations in MOOSE." %}}


{{% figure src="images/SlicedCore_featured.png" width="500" caption="Figure 3. An imported CAD model meshed with the Coreform Cubit Python API. For the highest-fidelity models, Radiant employs a 3rd party CAD tool whose geometry is then imported into Coreform Cubit as a STEP file, then webcut and meshed with both hexes and tets entirely through the Python API. In this way, more complicated geometries can be ingested for simulation." %}}


##### Conclusion

Radiant leveraged the advanced meshing and automation capabilities of Coreform Cubit to significantly increase simulation throughput and subsequently accelerate their new reactor design, further enabling their cost-competitive approach to the project.

{{% /section %}}

--- 

{{% figure src="../PDF_32.png" caption="[Download as PDF](RadiantCS.pdf)" %}}

---  

### Coreform Cubit information

Visit the [product overview page](/products/coreform-cubit/) to learn more about Coreform Cubit. 

Sign up for a [free trial](/products/trial/) of Coreform Cubit. 

[Coreform Cubit Learn](../../free-meshing-software/) is a free limited license available for non-commercial student use.

[Contact Coreform](/company/contact) for more details: sales@coreform.com

<br>

---

(*Images courtesy of Radiant Industries*)

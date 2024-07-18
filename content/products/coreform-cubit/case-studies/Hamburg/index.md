+++
title = "Research group relies on Coreform Cubit to generate high-quality models for numerical simulations"
keywords = "coreform cubit, academic meshing software, engineering education"
description = "Coreform Cubit is used at Helmut Schmidt University Hamburg to generate parametrizable meshes for numerical simulations of various problems"
type = "case-studies"
featured_image = "../purple-hero.png"
summary="Theoretical Electrical Engineering and Numerical Field Calculation (TET) is a research group at the Helmut Schmidt University, University of the Federal Armed Forces in Hamburg, Germany. TET researchers use Coreform Cubit for its powerful scripting, ability to generate meshes for parameterized geometries, and clear guided user interface and documentation."
+++

{{% section %}}

 ability to generate meshes of parameterized technical devices using its powerful scripting options is an integral part of teaching in their graduate courses.
<!-- <blockquote class="pullquote" style="width: 350px; font: bold 1.333em/1.125em &quot;Open Sans&quot;, sans-serif; margin: 2.5em 2.5em 2.5em 2.5em !important; padding: 0.6em 5px !important; background: none !important; border: 3px double \#ddd; border-width: 3px 0; text-align: center; float: left; ">---<br>Once you use Coreform Cubit for scripting automated mesh generation, you will never go back.<br>&mdash; Dr. Klaus Roppert<br>---</blockquote> -->



##### Background  
Theoretical Electrical Engineering and Numerical Field Calculation (TET) is a research group at the Helmut Schmidt University, University of the Federal Armed Forces in Hamburg, Germany. Its areas of research include computational electromagnetics, particularly multi-physical simulations such as coupled magneto-mechanical problems; numerical optimization of electromagnetic test environments; bioelectromagnetism; and machine learning and artificial intelligence. In this context, a highly efficient finite element method (FEM) software package in MATLAB was developed, where Coreform Cubit is the preferred tool to generate the required meshes.

Due to its largely self-explanatory features and detailed documentation, Coreform Cubit is used regularly by electrical engineering students to conduct research for their theses. Its ability to generate meshes of parameterized technical devices using its powerful scripting options is an integral part of teaching in their graduate courses.

<aside class="pquote">
	<p>Coreform Cubit is a great software to generate complex meshes. The combination of a clear guided user interface, its easy-to-use computer-aided design technology, and strong meshing algorithms makes it highly valuable for our work.<br> &nbsp; &nbsp; &nbsp; &nbsp; &mdash;	Nils Kielian, research assistant</p>
</aside>

TET also uses Coreform Cubit in its numerical simulation workflow in many research problems, including the analysis of biological reactions to an electromagnetic field detailed below.

##### Problem
As the number of electric devices in our everyday lives has increased, human exposure to electromagnetic fields has risen as well. To understand the impact of the electromagnetic field exposure on the human body, a FEM-based algorithm coupled with a domain decomposition was developed to simulate the behavior of biological cells in an electromagnetic field.

The algorithm consists of three main steps. First, the model of the cell needs to be generated, meshed, and exported into ABAQUS format. Then the created mesh will be imported to MATLAB, where the model equation is solved. Finally, the computed results will be plotted.

One of the difficulties of running this algorithm arises from the multiple electromagnetically-relevant geometrical scales within a biological cell: the diameter of a typical cell is 1000 times bigger than the thickness of the cell membrane, which leads to a complex multiscale problem.


##### Solution

{{% figure src="hamburgFigureOne.png" width="200" class="floatright" caption="<strong>Figure 1:</strong> Model of a simplified cell geometry.<br> This model was difficult to mesh in other meshing<br> software but straightforward to mesh in Coreform Cubit." %}}

To cope with the complexity of the multiscale problem, a high-quality mesh generation software was required. TET chose Coreform Cubit as an easy-to-use tool with the power to handle complex models at the level of accuracy demanded by bioelectromagnetics. Specifically, they noted the clear and intuitive GUI and the computer-aided design technology that allows a simple and time-saving construction of a model and the powerful command panel where models can be generated and meshed with just a few clicks. They also cite the scripting capabilities of Coreform Cubit, including the ability to automatically create scalable models.Coreform Cubit scripts can also be written in the journal editor, which allows for automation of repeated workflows and quick variability of desired meshes. Furthermore, the “aprepo” editor provides helpful tools like loops and different operators which can be used in scripts and provides a variety of possibilities to create models. The ease of setting boundary conditions, even in complex models, is one of the major reasons TET chose Coreform Cubit for this problem. Overall, TET noted that the meshes produced by Coreform Cubit achieved the high quality needed for accurate and efficient simulation of this demanding problem.

TET researchers first used a simplified cell geometry consisting of an interior domain separated by a membrane from a surrounding area, typically filled with electrolyte, which is shown in Figure 1. The zoomed-in image of the cell membrane at the bottom of Figure 1 shows the multiscale issue that makes meshing this problem so difficult.  Coreform Cubit managed to mesh this model properly without any issues.

With this model successfully meshed, the simulation of a biological cell within an electromagnetic field became possible and dosimetry studies could be done. For this purpose, the specific absorption rate (SAR) was computed at different frequencies of the electromagnetic field. SAR is a measure of the rate at which energy is absorbed per unit mass when exposed to an electromagnetic field. The results are shown in Figure 2. 

{{% figure src="hamburgFigureTwo_featured.png" width="" caption="<strong>Figure 2:</strong> Results of computed SAR in watts per kilogram at different frequencies. The higher the frequency of the influencing electromagnetic field is, the more energy is absorbed by the interior of the cell, which leads to its heating." %}}
 
<!-- {{% figure src="" width="" class="floatleft" caption=""   %}} -->

##### Conclusion

Coreform Cubit is the powerful meshing tool selected by the Theoretical Electrical Engineering and Numerical Field Calculation team at Helmut Schmidt University to facilitate some of its most difficult bioelectric simulations. Its ease of use, powerful scripting options, efficient model generation, and high quality meshes make it a key component of the workflow of numerical simulations in their research group.


{{% /section %}}

--- 

{{% figure src="../PDF_32.png" caption="[Download as PDF](hamburg.pdf)" %}}

---  

### Coreform Cubit information

Visit the [product overview page](/products/coreform-cubit/) to learn more about Coreform Cubit. 

Sign up for a [free trial](/products/trial/) of Coreform Cubit. 

[Coreform Cubit Learn](../../free-meshing-software/) is a free limited license available for non-commercial student use.

[Contact Coreform](/company/contact) for more details: sales@coreform.com

<br>

---

(*Images courtesy of Helmut Schmidt University Hamburg*)

+++
title = "Coreform Cubit enables patient-specific cardiac models to combat heart disease"
keywords = "coreform cubit, academic meshing software, engineering education"
description = "Cutting-edge research on heart biomechanics requires high-caliber meshes of complex cardiac geometries. Coreform Cubit was chosen for the job because of it combines an intuitive user interface with sophisticated, high-precision capabilities."
type = "case-studies"
featured_image = "dresden-featured.png"
date = "2024-05-02"
summary="Researchers at TU Dresden's [Institute for Structural Analysis](https://tu-dresden.de/bu/bauingenieurwesen/sdt/das-institut)  use Coreform Cubit to create patient-specific heart models for computational analysis of myocardial damage and evaluation of potential treatments."
+++

<!-- Yongjae Lee, Bariş Cansiz and Michael Kaliske -->

{{% section %}}


{{% figure src="ISD-logo.png" width="200" class="floatright" caption="" %}}<br>

##### Background  

<!-- Yongjae Lee, Bariş Cansiz and Michael Kaliske at  -->
<!-- Technische Universität Dresden (TU Dresden) -->

At Dresden University of Technology's [Institute for Structural Analysis](https://tu-dresden.de/bu/bauingenieurwesen/sdt/das-institut) (Institut für Statik und Dynamik der Tragwerke, ISD), researchers Bariş Cansiz and Yongjae Lee, working in collaboration with institute head Professor Dr. Michael Kaliske, conduct innovative research focusing on the computational biomechanics of the human heart. 
The ultimate goal is to achieve personalized therapies, as it is essential to recognize that every patient’s circumstance is invariably unique.

<aside class="pquote">
	<p>Coreform Cubit provide[s] an intuitive platform for meshing the geometry for the model, especially for complex human ventricular models with fine geometric details.
<br> &nbsp; &nbsp; &nbsp; &nbsp; &mdash;Institute for Structural Analysis, TU Dresden</p>
</aside>

Increased understanding of how the heart functions enables the development of more efficient treatments and, thus, reduces mortality from cardiovascular diseases. Furthermore, the economic consequences of these diseases can be reduced. Due to the high complexity of the cardiovascular system, understanding how it functions is a challenging task, especially because experiments on humans are problematic and each patient’s heart has individual characteristics. Against this background, research is now being carried out on computer-oriented modeling of the heart, with the focus on the development of patient-specific therapies.

In this context, Coreform Cubit has been used to create patient-specific heart models. Furthermore, pathological ventricular remodeling, such as in ischemic hearts, is simulated using the patient heart model. The research group aimed to establish a connection and understanding between the type of myocardial growth at the cellular level and the overall shape of the heart model. To simulate these processes in a computational model, [FEAP](http://projects.ce.berkeley.edu/feap/), a finite element analysis software, was used. Coreform Cubit provided an intuitive platform for meshing the geometry for the model, especially for complex human ventricular models with fine geometric details.

The significance of the project lies in the development of a novel constitutive model that incorporates the viscous and growth features of the myocardium. This computational tool will assist in understanding how the viscosity and growth in the myocardium affect the pathology of the human heart and how myocardial damage begins and evolves over time. In the long term, the results from this project will contribute to predicting the impacts of ventricular remodeling due to ischemia and hemodynamic fluctuation and to developing patient-specific cardiac therapies. This information can guide effective treatment plans for patients, as well as predict and monitor disease states in a non-invasive way.


 
##### Problem
The research group's computational biomechanics simulations demand high-quality mesh representations of cardiac geometries, which are crucial for accurate analysis and results. As the requirement for patient-specific hearts becomes evident, the limitations of existing meshing software emerge as a significant hurdle. High caliber meshes are needed that can be used easily in the institute's in-house finite element framework. The task at hand is to refine and discretize the nodes extracted fromechocardiography data to achieve a mesh of requisite quality.

{{% figure src="dresden-1.jpeg" width="1000" caption="<strong>Figure 1: </strong>The sequential process for generating a virtual left ventricular model derived from TomTec 4D echocardiographic data. The methodology encompasses initial segmentation of the endocardial and epicardial surfaces at end-diastole, followed by establishing a structural framework using the coordinates from the segmentation. Subsequent steps include the formulation of surface elements along the endocardium and epicardium, extending these elements over the basal region to encapsulate a complete volume, and ultimately integrating all surface elements to construct a coherent solid left ventricular geometry." %}}

Due to the complex nature of the endocardium and epicardium surfaces, the creation of their geometries proves to be a challenge. Given the intricate structure of the heart, which varies significantly even among healthy patients, a high-quality mesh is essential for simulations that can accurately describe physiological behaviors and potential pathologies. The research group's simulations require various element types, including tetrahedral four-node elements and triangular three-node elements.


##### Solution
The intricacies of cardiac structure and function necessitate models of exceptional detail and precision. In the realm of computational biomechanics, accurately simulating the heart’s mechanics is pivotal but challenging, especially when dealing with patient-specific models. This research requires the creation of two such models: a detailed left ventricular model and a more comprehensive biventricular model that encapsulates the complexities of both the left and right ventricles.


<div style="font-size:1em; font-style:italic; font-weight:650; margin-bottom:0.6em; margin-left:20px">Left ventricular geometry - 4D echocardiography</div>

The generation of a tailored left ventricular model from 4D echocardiographic data poses a significant challenge. It begins with the delineation of the endocardial and epicardial boundaries using TomTec Imaging Systems GmbH’s 4D LV-Analysis software. Although this software adeptly identifies the surfaces, the subsequent step of accurately tracking and exporting the surface points for model reconstruction demands a higher level of finesse. The steps are shown in Figure 1.

{{% figure src="dresden-2.jpeg" width="1000" caption="<strong>Figure 2: </strong> The process of constructing a virtual left ventricular model using cMRI data is depicted. The procedure initiates with the segmentation of the left ventricle’s endocardial and epicardial walls on a short-axis slice, followed by the assembly of a structural frame using the derived segmentation coordinates. This is succeeded by the development of truss elements along the defined boundaries. Subsequently, surface elements are established over these boundaries, culminating in the formation of a three-dimensional solid structure through the amalgamation of all boundary surface elements." %}}


<div style="font-size:1em; font-style:italic; font-weight:650; margin-bottom:0.6em; margin-left:20px"> Biventricular geometry - magnetic resonance imaging</div>

The creation of the patient-specific biventricular model is a complex endeavor that expands upon the intricacies of the left ventricular modeling process. First of all, cardiac magnetic resonance imaging data is utilized in the short-axis view.
The process begins with the precise delineation of the endocardial and epicardial boundaries of the left ventricle across eight distinct slices along the long axis. This step is facilitated by the 2D Cardiac Performance Analysis software from TomTec Imaging Systems GmbH, which adeptly marks the necessary cardiac boundaries. The software automatically adds 48 points to each boundary for speckle tracking. The coordinates of these points can be saved as a text file for additional changes.
Coreform Cubit plays a pivotal role in the next phase, where the exported coordinates serve as the foundation for mesh generation. Utilizing Cubit’s advanced meshing algorithms, the point data is transformed into a finely-tuned structural framework. This framework is essential for the accurate placement of truss and surface elements over the cardiac boundaries, ensuringthat the mesh accurately represents the complex geometries of the heart’s inner structures. The processes are shown in Figure 2.

Attention to detail is paramount, as the tip of the apex on both the endocardial and epicardial surfaces requires manual identification to ensure the utmost accuracy - a task that Cubit’s precise meshing capabilities makes possible. With these critical points established, researchers proceed to the encapsulation of a three-dimensional volume, which serves as the solid geometry of the left ventricle.

The right ventricle undergoes a similar process of creation with Coreform Cubit’s meshing tools ensuring consistency and accuracy. Once both ventricles are accurately modeled, they are unified within Cubit to form the comprehensive biventricular heart model.


<div style="font-size:1em; font-style:italic; font-weight:650; margin-bottom:0.6em; margin-left:20px">The Two Ventricular Models and Their Application</div>

The completed left ventricular and biventricular models are presented in Figure 3. The left ventricular model is discretized into 18,809 tetrahedral elements across 4,116 nodes. In contrast, the biventricular model is discretized within Coreform Cubit, resulting in a configuration of 25,366 tetrahedral elements. The utilization of Coreform Cubit significantly contributes to the success of the research. One notable example is the simulation of biventricular remodeling due to ischemia. This specific application is illustrated in Figure 4, showcasing the potential of the researchers' models to simulate complex cardiac responses to pathological conditions.

<!--. **Maybe paragraph lead sentence header:** Add content here. -->

##### Conclusion 
The ISD research team at TU Dresden successfully harnesses the sophisticated meshing capabilities of Coreform Cubit for the creation of highly detailed left ventricular and biventricular models. This critical step overcomes the challenges of discretization and refinement inherent in patient-specific heart geometries. The resulting models stand as a testament to the potential of advanced software tools to drive innovation in biomedical research and personalized medicine.

{{% figure src="dresden-3-all.png" width="1000" caption="<strong>Figure 3: </strong> a) The biventricular geometry is discretized in Coreform Cubit using 25366 tetrahedral elements, each with four nodes, across a total of 6270 nodes. b) The left ventricle geometry is discretized to 18809 tetrahedral elements, distributed over 4116 nodes. The sequence from left to right presents the discretized geometry along with its dimensions (lengths given in millimetres), two alternative perspectives of fibre orientation and its angle @${\theta} distribution, and springs with variable stiffness values (in N/mm) that are connected to the basal and epicardial surface to serve as boundary constraints." %}}

{{% figure src="dresden-4-all.png" width="1000" caption="<strong>Figure 4: </strong>Illustration of the remodelled biventricular model due to acute regional ischemia.<br> The contour of growth stretch along fibre λ<sub>f</sub> is shown in (a), while the contour of growth stretch along sheet λ<sub>s</sub> is shown in (b)." %}}


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
<h1>Welcome to CoSyFeAT (Collaborative Systems Feature Analysis Tool)!</h1>
<h2>About</h2>

CoSyFeAT is a tool that has been developed to support the early phases of engineering collaborative systems, i.e., systems that dynamically form collaborations to achieve emergent capabilities that go beyond the individual capabilities of the involved systems. CoSyFeAT allows modelling and analyzing the functional interplay of candidate architectures of collaborative system groups. The tool offers a graphical user interface to create context feature models that are used to model types of collaborative systems and their capabilities, as well as the desired capabilities to be achieved by the collaboration. CoSyFeAT enables the automated instantiation of collaborative system instances as Boolean formulas that can be checked for satisfiability.

<h2>Technical Requirements</h2>

This tool requires Java 8.

<h2>Required Knowledge</h2>

The tool is aimed to support requirements engineers who are familiar with feature models, especially context feature models as used in the approach presented in our ICSA 2026 paper. Basic knowledge about the feature modeling language are expected.

<h2>Run the Tool</h2>

An executable .jar file of the tool can be found in the folder "download-executable". 

<h2>Key Features</h2>

CoSyFeAT provides a graphical user interface with a modeling editor for creating type-level feature models (for explantion see our ICSA paper - Link follows after publication). As input for instantiation, the numbers of instances of the specified system types are specified directly in the models as root feature attributes. CoSyFeAT allows automatically instantiating collaborations and check them against the desired collaboration features.

<h2>How to Use</h2>

The start screen of the tool allows you to select between creating a "New project" or "Load project". To load a simple example model, please download our example file "example.png" and select it in the file selection menu.

A project in CoSyFeAT consists of a set of System Type Feature Models (STFMS) and one Collaboration Type Feature Model (CTFM). These feature models are represented as a root feature and can be added to the project by right-clicking on the blank space and selecting "Add Featuremodel". Each STFM has two static first-level subfeatures: "Collaborative Context" and "System". These cannot be changed. You can only add subfeatures below. To do so, right-click the respective branch feature and select "Add feature".

To specify the kind of feature decomposition, please right-click the edge that has been automatically generated for each new created subfeature, and select "Change edge". You can specify the type of the entire feature group decomposition (e.g., XOR decomposition) there. Requires and excludes links can be drawn by clicking in the middle of a feature (on the label) and holding to draw a line to the desired target feature. Again, you can specify the kind of dependency (requires or exclude) by right-clicking the link and selecting "Change Edge". For requires links this will open up a pop-up menu in which you can specify cardinality constraint attached to the requires dependency. Please enter only natural numbers larger than zero there.

To run an analysis, you have to specify the number of systems for each STFM. To do so, right-click the respective STFM and select "Number of instances". You can enter a number larger than zero there. Once you specified all the numbers, you can generate the Boolean formula and check it for satisfiability by pressing the button "check" on the bottom right-hand side of the tool window. The output is displayed on the bottom left-hand side. 

<h2>Used Libraries</h2>

The following libraries have been used:
- JGraphX (https://github.com/jgraph/jgraphx) for graphical modeling
- LogicNG (https://github.com/logic-ng/LogicNG) for creating Boolean formulas and SAT solving
- Apache Commons Lang (https://commons.apache.org/proper/commons-lang/)

 


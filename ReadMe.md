<h1>Welcome to CoSyFeAT (Collaborative Systems Feature Analysis Tool)!</h1>
<h2>About</h2>

CoSyFeAT is a tool that has been developed to support the early phases of engineering collaborative systems, i.e., systems that dynamically form collaborations to achieve emergent capabilities that go beyond the individual capabilities of the involved systems. CoSyFeAT allows modelling and analyzing the functional interplay of candidate architectures of collaborative system groups. The tool offers a graphical user interface to create context feature models that are used to model types of collaborative systems and their capabilities, as well as the desired capabilities to be achieved by the collaboration. CoSyFeAT enables the automated instantiation of collaborative system instances as Boolean formulas that can be checked for satisfiability.

<h2>Requirements</h2>

This tool requires Java 8.

<h2>Run the Tool</h2>

An executable .jar file of the tool can be found in the folder "download-executable". Example models created using the tool can be found here. This includes models from the case studies presented in our corresponding ICSA 2026 paper.

<h2>Key Features</h2>

CoSyFeAT provides a graphical user interface with a modeling editor for creating type-level feature models (for explantion see our ICSA paper - Link follows after publication). As input for instantiation, the numbers of instances of the specified system types are specified directly in the models as root feature attributes. CoSyFeAT allows automatically instantiating collaborations and check them against the desired collaboration features.

<h2>How to Use</h2>

TODO

<h2>Used Libraries</h2>

The following libraries have been used:
- JGraphX (https://github.com/jgraph/jgraphx) for graphical modeling
- LogicNG (https://github.com/logic-ng/LogicNG) for creating Boolean formulas and SAT solving
- Apache Commons Lang (https://commons.apache.org/proper/commons-lang/)

 


<h1>Welcome to CoSyFeAT (Collaborative Systems Feature Analysis Tool)!</h1>
<h2>About</h2>
This Tool was developed to check 
collaboration-related uncertainties on 
context feature models.
The developed model checker tool was developed in Java and can be started from the command line.
The Tool gets the models as an XML-File. This File describes the Collaborative Sys-tem Feature Model, 
the Collaborative System Group Feature Model and the constraints between the features. 
The developed tool then translates the XML-File with the help of the parser generator [ANTLR](https://www.antlr.org/) into Java Objects. 
After that the feature models and the constraints are translated into a Boolean formula with the Java Library [LogicNG](https://github.com/logic-ng/LogicNG). 
Afterwards LogicNG checks the validity of the Boolean formula.

<h2>How to Use</h2>

Before you can use the tool, the XML-File of the models must be written. 
After that the tool can be started from the command line and needs an 
Input-File as a parameter. With the command -i or --input you can choose 
the Input-File. An exemplary execution of the program could look like this: 

**_java -jar cosyfeat.jar -i input.xml_**

The commands can also be read again in the help, 
which can be called with -h or 
--help. 
After that the tool will check the models and the constraints. 
You can see then the Boolean formula and its result. 
If the models and the constraints are valid the tool shows **TRUE**, 
if not the tool shows **FALSE**. 

 


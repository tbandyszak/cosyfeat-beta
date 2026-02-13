package de.cosyfeat.gui;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import de.cosyfeat.tool.*;

import java.util.ArrayList;

/**
 * Translates the current Graph and checks the Graph.
 */
public class ModelTranslator {
    Storage storage = new Storage();
    private String conSysGroupName;
    private Node.RelationType nodeRelation;
    private Node sourceNode;
    private Node targetNode;
    private int sourceFeaturemodel;
    private int targetFeaturemodel;
    int featuremodelID = 1;
    ArrayList<String> featuremodelList;
    mxGraph graph;
    Object[] allEdgesOfNodes;
    Object[] allEdgesOfConstraint;
    String result = "";

    /**
     * Start translating the Graph into Java-Objects for the CoSyFeAT-Tool.
     *
     * @param graph The Graph.
     */
    public void startTranslating(mxGraph graph) {
        clearAll();
        this.graph = graph;
        graph.clearSelection();
        graph.selectAll();
        featuremodelList = new ArrayList<>();
        allEdgesOfNodes = filterEdges(graph.getSelectionCells(), edgeType.NODE);
        allEdgesOfConstraint = filterEdges(graph.getSelectionCells(), edgeType.CONSTRAINT);

        for (Object edge : allEdgesOfNodes) {
            buildFeaturemodels(edge);
            setSystemRoot(edge);
            setContextRoot(edge);
            setGrouprootRoot(edge);
        }
        for (Object edge : allEdgesOfNodes) {
            if (!((mxCell) edge).getSource().getStyle().equals("featuremodel")) {
                setNodes(edge);
            }
        }

        for (Object constraint : allEdgesOfConstraint) {
            setConstraints(constraint);
        }

        startModelchecking();
        graph.clearSelection();

    }

    private void clearAll(){
        storage = new Storage();
        featuremodelID = 1;
        featuremodelList = null;
        graph = null;
        allEdgesOfNodes = null;
        allEdgesOfConstraint = null;
        result = "";
    }

    /**
     * Starts model-checking the Graph.
     */
    private void startModelchecking() {
        for (Featuremodel featuremodel : storage.featuremodels) {
            if (featuremodel.type.equals("type")) {
                for (int i = 0; i < featuremodel.numberOfInstances; i++) {
                    int instanceid = (i + 1);
                    featuremodel.instances.add(new Instance(instanceid, featuremodel.systemroot.clone()));
                }
            }
        }

        ModelChecker modelChecker = new ModelChecker();

        for (Featuremodel featuremodel : storage.featuremodels) {
            if (featuremodel.type.equals("type")) {
                modelChecker.runInstances(featuremodel.instances);
            } else if (featuremodel.type.equals("group")) {
                modelChecker.runGroupmodel(featuremodel);
            }

        }
        for (Constraint constraint : storage.constraints) {
            modelChecker.runConstraints(constraint, storage.featuremodels);
        }

        result = modelChecker.getResult();
    }

    /**
     * Returns the Result (Formula and Result TRUE/FALSE)
     *
     * @return The result.
     */
    public String getResult() {
        return result;
    }

    /**
     * Defines the Types of Edges. Normal Edges (OR, AND, XOR) or Constraints (REQUIRES, EXCLUDES).
     */
    private enum edgeType {
        NODE, CONSTRAINT;
    }

    /**
     * Filters the Object-Array by Type.
     *
     * @param cells    The Object-Array of all Edges.
     * @param edgeType The Type.
     * @return The filtered Array.
     */
    private Object[] filterEdges(Object[] cells, edgeType edgeType) {
        ArrayList<Object> edgesList = new ArrayList<>();
        if (edgeType == ModelTranslator.edgeType.NODE) {
            for (Object c : cells) {
                if (((mxCell) c).isEdge() && !((mxCell) c).getStyle().equals("requires") && !((mxCell) c).getStyle().equals("exclude"))
                    edgesList.add(c);
            }
        } else if (edgeType == ModelTranslator.edgeType.CONSTRAINT) {
            for (Object c : cells) {
                if (((mxCell) c).isEdge() && (((mxCell) c).getStyle().equals("requires") || ((mxCell) c).getStyle().equals("exclude")))
                    edgesList.add(c);
            }
        }


        return edgesList.toArray();
    }

    /**
     * Builds the Featuremodels.
     *
     * @param c Cell.
     */
    private void buildFeaturemodels(Object c) {
        if (((mxCell) c).getSource().getStyle().equals("featuremodel") && !isInFeaturemodelList(String.valueOf(((mxCell) c).getSource().getValue()))) {

            Featuremodel featuremodel = new Featuremodel();
            featuremodel.setModelId(featuremodelID);
            featuremodel.setName(((String) ((mxCell) c).getSource().getValue()).trim());

            String value = String.valueOf(((mxCell) c).getSource().getValue());
            if (value.contains(":")) {
                String[] instances = value.split(":");
                featuremodel.setNumberOfInstances(Integer.parseInt(instances[1]));
            } else featuremodel.setNumberOfInstances(1);

            featuremodel.type = "type";

            storage.featuremodels.add(featuremodel);
            featuremodelList.add(String.valueOf(((mxCell) c).getSource().getValue()));
            featuremodelID++;

        } else if (((mxCell) c).getSource().getStyle().equals("groupmodel") && !isInFeaturemodelList(String.valueOf(((mxCell) c).getSource().getValue()))) {
            Featuremodel featuremodel = new Featuremodel();
            featuremodel.setModelId(featuremodelID);
            featuremodel.setName(((String) ((mxCell) c).getSource().getValue()).trim());
            featuremodel.type = "group";

            storage.featuremodels.add(featuremodel);
            featuremodelList.add(String.valueOf(((mxCell) c).getSource().getValue()));
            featuremodelID++;


        }

    }

    /**
     * Sets the Context-Root of a Featuremodel.
     *
     * @param c Cell.
     */
    private void setContextRoot(Object c) {

        if (((mxCell) c).getSource().getStyle().equals("featuremodel") && ((mxCell) c).getTarget().getValue().equals("Context")) {

            setConSysGroupProperties(c);

            Node contextroot = new Node(conSysGroupName, nodeRelation, Node.Type.CONTEXT, null);
            contextroot.setID(Integer.parseInt(((mxCell) c).getTarget().getId()));

            Featuremodel featuremodel = storage.getFeaturemodelbyName(((String) ((mxCell) c).getSource().getValue()).trim());
            featuremodel.setContextroot(contextroot);
        }

    }

    /**
     * Sets the System-Root of a Featuremodel.
     *
     * @param c Cell.
     */
    private void setSystemRoot(Object c) {

        if (((mxCell) c).getSource().getStyle().equals("featuremodel") && !((mxCell) c).getTarget().getValue().equals("Context")) {
            setConSysGroupProperties(c);

            Node systemroot = new Node(conSysGroupName, nodeRelation, Node.Type.SYSTEM, null);
            systemroot.setID(Integer.parseInt(((mxCell) c).getTarget().getId()));

            Featuremodel featuremodel = storage.getFeaturemodelbyName(((String) ((mxCell) c).getSource().getValue()).trim());
            featuremodel.setSystemroot(systemroot);

        }

    }



    /**
     * Sets the Group-Root of a Groupmodel.
     *
     * @param c Cell.
     */
    private void setGrouprootRoot(Object c) {
        if (((mxCell) c).getSource().getStyle().equals("groupmodel")) {
            setConSysGroupProperties(c);
            Node grouproot = new Node(conSysGroupName, nodeRelation, Node.Type.GROUP, null);
            grouproot.setID(Integer.parseInt(((mxCell) c).getSource().getId()));

            Featuremodel featuremodel = storage.getFeaturemodelbyName(((String) ((mxCell) c).getSource().getValue()).trim());
            featuremodel.setGrouproot(grouproot);

        }

    }

    /**
     * Sets the name and Node Type of the Context-/System-/Grouproot.
     * @param c Cell.
     */
    private void setConSysGroupProperties(Object c) {
        conSysGroupName = ((String) ((mxCell) c).getTarget().getValue()).trim();

        String relationType = "";
        if (findOutgoingEdge(c) != null) {
            relationType = (String) ((mxCell) findOutgoingEdge(c)).getValue();
        } else relationType = "none";

        nodeRelation = Node.RelationType.NONE;
        switch (relationType) {
            case "":
                nodeRelation = Node.RelationType.AND;
                break;
            case "OR":
                nodeRelation = Node.RelationType.OR;
                break;
            case "XOR":
                nodeRelation = Node.RelationType.ALTERNATIVE;
                break;
        }
    }


    /**
     * Sets the Nodes of a Featuremodel/Groupmodel.
     *
     * @param c Cell.
     */
    private void setNodes(Object c) {

        int sourceId = Integer.parseInt(((mxCell) c).getSource().getId());
        String name = ((String) ((mxCell) c).getTarget().getValue()).trim();

        String relationType = "";
        if (findOutgoingEdge(c) != null) {
            relationType = (String) ((mxCell) findOutgoingEdge(c)).getValue();
        } else relationType = "none";


        Node.RelationType relation = Node.RelationType.NONE;
        switch (relationType) {
            case "":
                relation = Node.RelationType.AND;
                break;
            case "OR":
                relation = Node.RelationType.OR;
                break;
            case "XOR":
                relation = Node.RelationType.ALTERNATIVE;
                break;
            case "none":
                relation = Node.RelationType.NONE;
                break;
        }


        Node nodeToAdd = new Node(name, relation, Node.Type.NONE, null);
        nodeToAdd.setID(Integer.parseInt(((mxCell) c).getTarget().getId()));

        if (((mxCell) c).getStyle().equals("mandatory")) {
            nodeToAdd.setMandopt(Node.MandOpt.MANDATORY);
        } else if (((mxCell) c).getStyle().equals("optional")) {
            nodeToAdd.setMandopt(Node.MandOpt.OPTIONAL);
        } else nodeToAdd.setMandopt(Node.MandOpt.NONE);


        Node sourceNode = null;

        for (Featuremodel featuremodel : storage.featuremodels) {
            if (featuremodel.systemroot != null && sourceNode == null) {
                sourceNode = featuremodel.getNodebyID(featuremodel.systemroot, sourceId);
            }
            if (featuremodel.contextroot != null && sourceNode == null) {
                sourceNode = featuremodel.getNodebyID(featuremodel.contextroot, sourceId);
            }
            if (featuremodel.grouproot != null && sourceNode == null) {
                sourceNode = featuremodel.getNodebyID(featuremodel.grouproot, sourceId);
            }
        }

        if (sourceNode != null) {
            nodeToAdd.setType(sourceNode.getType());
            nodeToAdd.setParent(sourceNode);
            sourceNode.postnodes.add(nodeToAdd);
        }

    }

    /**
     * Checks whether the Featuremodel already exists.
     *
     * @param name Name of the Featuremodel.
     * @return TRUE/FALSE.
     */
    private boolean isInFeaturemodelList(String name) {
        if (featuremodelList.size() > 0) {
            for (String featuremodel : featuremodelList) {
                if (featuremodel.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finds a outgoing Edge of the Cell (Vertex).
     *
     * @param c Cell (Vertex).
     * @return Outgoing Edge.
     */
    private Object findOutgoingEdge(Object c) {

        for (Object edge : allEdgesOfNodes) {
            if (((mxCell) edge).getSource().getId().equals(((mxCell) c).getTarget().getId())) {
                return edge;
            }
        }
        return null;
    }

    /**
     * Sets the Constraints of the Graph.
     *
     * @param c Cell.
     */
    private void setConstraints(Object c) {
        if (((mxCell) c).getStyle().equals("requires")) {
            sourceFeaturemodel = 0;
            targetFeaturemodel = 0;
            String source = ((mxCell) c).getSource().getId();
            String target = ((mxCell) c).getTarget().getId();
            int sourceId = Integer.parseInt(source);
            int targetId = Integer.parseInt(target);

            int lowerbound = -2;
            int upperbound = -2;

            setConstraintProperties(sourceId, targetId);

            String value = ((String) ((mxCell) c).getValue()).trim();
            String[] bounds;

            if (value.contains("[")) {
                bounds = value.split("\\[");
                bounds = bounds[1].split("]");
                bounds = bounds[0].split("\\.\\.");

                String lowerboundValue = String.valueOf(bounds[0]);
                String upperboundValue = String.valueOf(bounds[1]);


                if (lowerboundValue.equals("*")) lowerbound = -1;
                else lowerbound = Integer.parseInt(lowerboundValue);
                if (upperboundValue.equals("*")) upperbound = -1;
                else upperbound = Integer.parseInt(upperboundValue);
            }

            Constraint requires = new Constraint(sourceNode, sourceFeaturemodel, targetNode, targetFeaturemodel, lowerbound, upperbound);
            storage.constraints.add(requires);


        } else if (((mxCell) c).getStyle().equals("exclude")) {
            sourceFeaturemodel = 0;
            targetFeaturemodel = 0;
            int sourceId = Integer.parseInt(((mxCell) c).getSource().getId());
            int targetId = Integer.parseInt(((mxCell) c).getTarget().getId());

            setConstraintProperties(sourceId, targetId);

            Constraint excludes = new Constraint(sourceNode, sourceFeaturemodel, targetNode, targetFeaturemodel);
            storage.constraints.add(excludes);

        }
    }

    /**
     * Sets Source and Target Node/Featuremodel.
     * @param sourceId Id of the Source.
     * @param targetId Id of the Target.
     */
    private void setConstraintProperties(int sourceId, int targetId) {
        sourceNode = null;
        targetNode = null;

        for (Featuremodel featuremodel : storage.featuremodels) {
            if (featuremodel.systemroot != null && sourceNode == null) {
                sourceNode = featuremodel.getNodebyID(featuremodel.systemroot, sourceId);
                sourceFeaturemodel = featuremodel.getModelId();
            }
            if (featuremodel.contextroot != null && sourceNode == null) {
                sourceNode = featuremodel.getNodebyID(featuremodel.contextroot, sourceId);
                sourceFeaturemodel = featuremodel.getModelId();
            }
            if (featuremodel.grouproot != null && sourceNode == null) {
                sourceNode = featuremodel.getNodebyID(featuremodel.grouproot, sourceId);
                sourceFeaturemodel = featuremodel.getModelId();
            }
        }

        for (Featuremodel featuremodel : storage.featuremodels) {
            if (featuremodel.systemroot != null && targetNode == null) {
                targetNode = featuremodel.getNodebyID(featuremodel.systemroot, targetId);
                targetFeaturemodel = featuremodel.getModelId();
            }
            if (featuremodel.contextroot != null && targetNode == null) {
                targetNode = featuremodel.getNodebyID(featuremodel.contextroot, targetId);
                targetFeaturemodel = featuremodel.getModelId();
            }
            if (featuremodel.grouproot != null && targetNode == null) {
                targetNode = featuremodel.getNodebyID(featuremodel.grouproot, targetId);
                targetFeaturemodel = featuremodel.getModelId();
            }
        }
    }


}

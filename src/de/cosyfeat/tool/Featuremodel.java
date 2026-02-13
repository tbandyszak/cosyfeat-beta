package de.cosyfeat.tool;

import java.util.ArrayList;

/**
 * Class of a Featuremodel.
 */
public class Featuremodel {
    public String name;
    public String type;
    public int modelid;
    public int numberOfInstances;

    public Node contextroot;
    public Node systemroot;
    public Node grouproot;
    public ArrayList<Instance> instances = new ArrayList<>();


    /**
     * Sets the Name of the Featuremodel.
     *
     * @param name Name for a Type-Featuremodel or "group" for a Group-Featuremodel.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the type of the Featuremodel.
     *
     * @param type "type" for a Type-Featuremodel or "group" for a Group-Featuremodel.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the Model-ID.
     *
     * @param modelid The ID.
     */
    public void setModelId(int modelid) {
        this.modelid = modelid;
    }

    /**
     * Returns the Model-ID.
     *
     * @return The ID.
     */
    public int getModelId() {
        return modelid;
    }

    /**
     * Sets the start node of the context branch of a type feature model.
     *
     * @param root Start node of the context branch.
     */
    public void setContextroot(Node root) {
        this.contextroot = root;
    }

    /**
     * Sets the start node of the system branch of a type feature model.
     *
     * @param root Start node of the system branch.
     */
    public void setSystemroot(Node root) {
        this.systemroot = root;
    }

    /**
     * Sets the start node of the group branch of a group feature model.
     *
     * @param grouproot Start node of the group branch.
     */
    public void setGrouproot(Node grouproot) {
        this.grouproot = grouproot;
    }

    /**
     * Sets the number of instances.
     *
     * @param numberOfInstances Number of instances.
     */
    public void setNumberOfInstances(int numberOfInstances) {
        this.numberOfInstances = numberOfInstances;
    }

    /**
     * Finds a node in a feature model, starting from a node, and returns the found node.
     *
     * @param node Node from which to start looking e.g. the start node of the system branch of a type feature model.
     * @param name Name of the node searched.
     * @return Node found or Null if the node was not found.
     */
    public Node getNodebyName(Node node, String name) {

        if (node.name.equals(name)) {
            return node;
        }
        for (Node postnode : node.postnodes) {
            Node result = getNodebyName(postnode, name);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    /**
     * Finds whether a feature exists in the model.
     *
     * @param name Name of the feature.
     * @return True/False
     */
    public boolean isNodeInFeaturemodel(String name) {
        if (systemroot != null) {
            return searchNodeInFeaturemodel(name, systemroot) != null;
        }
        return false;
    }

    /**
     * Search for a Node in a Featuremodel.
     *
     * @param name Name of the Node.
     * @param node A Node.
     * @return Node found or Null if the node was not found.
     */
    public Node searchNodeInFeaturemodel(String name, Node node) {

        if (node == null) {
            return null;
        }

        if (node.name.equals(name)) {
            return node;
        }

        Node result = null;
        for (Node postnode : node.postnodes) {
            result = searchNodeInFeaturemodel(name, postnode);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Finds a node in a feature model, starting from a node, and returns the found node.
     *
     * @param node Node from which to start looking e.g. the start node of the system branch of a type feature model.
     * @param id ID of the node searched.
     * @return Node found or Null if the node was not found.
     */
    public Node getNodebyID(Node node, int id) {

        if (node.id == id) {
            return node;
        }
        for (Node postnode : node.postnodes) {
            Node result = getNodebyID(postnode, id);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

}
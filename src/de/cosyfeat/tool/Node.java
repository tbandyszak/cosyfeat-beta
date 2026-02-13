package de.cosyfeat.tool;

import java.util.ArrayList;

/**
 * Class for a node of a feature model.
 *
 * @see Featuremodel
 */
public class Node {
    int id;

    /**
     * Property of a feature, whether it is mandatory or optional (in an AND-relationship).
     * Default: None
     */
    public enum MandOpt {
        MANDATORY,
        OPTIONAL,
        NONE
    }

    /**
     * Description of the relationship of nodes. Describes the subsequent relationship of a node.
     */
    public enum RelationType {
        AND,
        OR,
        ALTERNATIVE,
        NONE
    }

    /**
     * Describes the membership of a node.
     * Membership can be a system branch (System), a context branch (Context)
     * or a group feature model (Group).
     */
    public enum Type {
        SYSTEM,
        CONTEXT,
        GROUP,
        NONE
    }

    public String name;
    public MandOpt mandopt;
    public RelationType relation;
    public Node parent;
    public ArrayList<Node> postnodes;
    public Type type;

    /**
     * Creates a node.
     *
     * @param name     Name of the node.
     * @param relation Relationship to its subsequent nodes (AND, OR, ALTERNATIVE)
     * @param type     Membership of the node or type of node in the feature model (SYSTEM, CONTEXT, GROUP)
     * @param parent   Parent node of the node.
     */
    public Node(String name, RelationType relation, Type type, Node parent) {
        this.name = name;
        this.mandopt = MandOpt.NONE;
        this.relation = relation;
        this.type = type;
        this.postnodes = new ArrayList<>();
        this.parent = parent;
    }


    /**
     * Set whether a node in an AND relationship is mandatory or optional.
     *
     * @param mandopt
     */
    public void setMandopt(MandOpt mandopt) {
        this.mandopt = mandopt;
    }

    /**
     * Set the ID of the Node.
     *
     * @param id
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Returns the Type of the Node.
     *
     * @return Type
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the Type of the Node.
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Sets the Parent of the Node.
     */
    public void setParent(Node parent){
        this.parent = parent;
    }

    /**
     * Clone a node. Is used to create the instances.
     *
     * @return Returns the cloned knot.
     */
    public Node clone() {
        Node clone = new Node(this.name, this.relation, this.type, this.parent);

        clone.mandopt = this.mandopt;

        for (Node postnode : this.postnodes) {
            clone.postnodes.add(postnode.clone());
        }
        return clone;
    }
}
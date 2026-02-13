package de.cosyfeat.tool;

/**
 * Class for creating Constraints.
 */
public class Constraint {

    public enum Type {
        REQUIRES,
        EXCLUDES
    }

    public Type type;
    public Node source;
    public Node target;
    public int lowerbound;
    public int upperbound;
    public int sourceid;
    public int targetid;

    /**
     * Creates a Requires relationship with cardinality.
     *
     * @param source     Node from which the relationship starts (source).
     * @param sourceid   Featuremodel-ID of the source.
     * @param target     Node to which the relationship goes (target).
     * @param targetid   Featuremodel-ID of the target.
     * @param lowerbound Lower bound of cardinality.
     * @param upperbound Upper bound of cardinality.
     */
    public Constraint(Node source, int sourceid, Node target, int targetid, int lowerbound, int upperbound) {

        this.type = Type.REQUIRES;
        this.source = source;
        this.target = target;
        this.lowerbound = lowerbound;
        this.upperbound = upperbound;
        this.sourceid = sourceid;
        this.targetid = targetid;
    }

    /**
     * Creates an excludes relationship.
     *
     * @param source   Node from which the relationship starts (source).
     * @param sourceid Featuremodel-ID of the source.
     * @param target   Node to which the relationship goes (target).
     * @param targetid Featuremodel-ID of the target.
     */
    public Constraint(Node source, int sourceid, Node target, int targetid) {
        this.type = Type.EXCLUDES;
        this.source = source;
        this.target = target;
        this.sourceid = sourceid;
        this.targetid = targetid;
    }

    /**
     * Sets the lower bound of cardinality.
     *
     * @param lowerbound lower bound.
     */
    public void setLowerbound(int lowerbound) {
        this.lowerbound = lowerbound;
    }

    /**
     * Sets the upper bound of cardinality.
     *
     * @param upperbound upper bound.
     */
    public void setUpperbound(int upperbound) {
        this.upperbound = upperbound;
    }


}

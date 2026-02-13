package de.cosyfeat.tool;

/**
 * Class for creating instances of the system branch of a type model.
 *
 * @see Featuremodel
 */
public class Instance {
    public int id;
    public Node instanceroot;

    /**
     * Creates an instance.
     *
     * @param id           ID of the Instance (eg. I1, I2, I3).
     * @param instanceroot Start node of the instance branch.
     */
    public Instance(int id, Node instanceroot) {
        this.id = id;
        this.instanceroot = instanceroot;
    }
}

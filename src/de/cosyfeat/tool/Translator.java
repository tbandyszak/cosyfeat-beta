package de.cosyfeat.tool;


/**
 * This class enters every element of the grammar in the XML and translates it into a corresponding Java object.
 *
 * @link CoSyFeAT.g4   Grammar for the XML structure of the feature model and constraint description.
 * We utilize ANTLR (https://www.antlr.org/)
 */

public class Translator extends CoSyFeATBaseListener {

    public Featuremodel featuremodel;
    public Node node;
    public boolean root;
    public Node lastParent;
    public Constraint constraint;

    public Node.Type type;
    public Storage storage = new Storage();


    /**
     * Creates a new Type-Featuremodel.
     *
     * @param ctx Context.
     */
    @Override
    public void enterTypemodel(CoSyFeATParser.TypemodelContext ctx) {
        featuremodel = new Featuremodel();
        featuremodel.setType("type");
        featuremodel.setModelId(Integer.parseInt(ctx.modelid().getText()));
    }

    @Override
    public void exitTypemodel(CoSyFeATParser.TypemodelContext ctx) {
        storage.featuremodels.add(featuremodel);
    }


    /**
     * Creates a new Group-Featuremodel.
     *
     * @param ctx Context.
     */
    @Override
    public void enterGroupmodel(CoSyFeATParser.GroupmodelContext ctx) {
        featuremodel = new Featuremodel();
        featuremodel.setType("group");
        featuremodel.setModelId(Integer.parseInt(ctx.modelid().getText()));
    }

    @Override
    public void exitGroupmodel(CoSyFeATParser.GroupmodelContext ctx) {
        storage.featuremodels.add(featuremodel);
    }

    /**
     * Sets the node type to "CONTEXT" for the context branch.
     *
     * @param ctx Context.
     */
    @Override
    public void enterContexttree(CoSyFeATParser.ContexttreeContext ctx) {
        type = Node.Type.CONTEXT;
    }

    /**
     * Sets the node type to "SYSTEM" for the system branch and sets the number of instances required later.
     *
     * @param ctx Context.
     */
    @Override
    public void enterSystemtree(CoSyFeATParser.SystemtreeContext ctx) {
        type = Node.Type.SYSTEM;
        featuremodel.setNumberOfInstances(Integer.parseInt(ctx.instances().getText()));
    }

    /**
     * Sets the node type to "GROUP" for the group branch.
     *
     * @param ctx Context.
     */
    @Override
    public void enterGmodel(CoSyFeATParser.GmodelContext ctx) {
        type = Node.Type.GROUP;
    }

    /**
     * Entry into the tree of the feature models.
     *
     * @param ctx Context.
     */
    @Override
    public void enterFtree(CoSyFeATParser.FtreeContext ctx) {
        root = true;
    }

    /**
     * Create a node whose relationship to its subsequent nodes is AND.
     *
     * @param ctx Context.
     */
    @Override
    public void enterAnd(CoSyFeATParser.AndContext ctx) {
        createNode(ctx.fname(), Node.RelationType.AND);
    }

    /**
     * Saving the last node for the later assignment of the parent node of the subsequent node.
     *
     * @param ctx Context.
     */
    @Override
    public void exitAnd(CoSyFeATParser.AndContext ctx) {
        lastParent = lastParent.parent;
    }

    /**
     * Create a node whose relationship to its subsequent nodes is OR.
     *
     * @param ctx Context.
     */
    @Override
    public void enterOr(CoSyFeATParser.OrContext ctx) {
        createNode(ctx.fname(), Node.RelationType.OR);
    }

    /**
     * Saving the last node for the later assignment of the parent node of the subsequent node.
     *
     * @param ctx Context.
     */
    @Override
    public void exitOr(CoSyFeATParser.OrContext ctx) {
        lastParent = lastParent.parent;
    }

    /**
     * Create a node whose relationship to its subsequent nodes is ALTERNATIVE.
     *
     * @param ctx Kontext.
     */
    @Override
    public void enterAlt(CoSyFeATParser.AltContext ctx) {
        createNode(ctx.fname(), Node.RelationType.ALTERNATIVE);
    }

    /**
     * Saving the last node for the later assignment of the parent node of the subsequent node.
     *
     * @param ctx Context.
     */
    @Override
    public void exitAlt(CoSyFeATParser.AltContext ctx) {
        lastParent = lastParent.parent;
    }

    /**
     * Create a node that has no further follow-up nodes and therefore no relationship.
     *
     * @param ctx Context.
     */
    @Override
    public void enterFeature(CoSyFeATParser.FeatureContext ctx) {
        createNode(ctx.fname(), null);

    }


    /**
     * Set whether a node that is in an AND-relationship is mandatory or optional.
     *
     * @param ctx Context.
     */
    @Override
    public void enterRelation(CoSyFeATParser.RelationContext ctx) {
        if (ctx.relationname().getText().toLowerCase().equals("mandatory")) {
            node.setMandopt(Node.MandOpt.MANDATORY);
        } else if (ctx.relationname().getText().toLowerCase().equals("optional")) {
            node.setMandopt(Node.MandOpt.OPTIONAL);
        } else {
            node.setMandopt(Node.MandOpt.NONE);
        }

    }

    /**
     * Create a Requires-Constraint.
     *
     * @param ctx Context.
     */
    @Override
    public void enterRequires(CoSyFeATParser.RequiresContext ctx) {
        Featuremodel featuremodelsource;
        Featuremodel featuremodeltarget;

        Node source;
        Node target;

        featuremodelsource = storage.getFeaturemodelbyID(Integer.parseInt(ctx.sourceid().getText()));
        featuremodeltarget = storage.getFeaturemodelbyID(Integer.parseInt(ctx.targetid().getText()));

        if (ctx.sourcetype().getText().equals("system") || ctx.sourcetype().getText().equals("System")) {
            source = featuremodelsource.getNodebyName(featuremodelsource.systemroot, ctx.source().getText());
        } else if (ctx.sourcetype().getText().equals("context") || ctx.sourcetype().getText().equals("Context")) {
            source = featuremodelsource.getNodebyName(featuremodelsource.contextroot, ctx.source().getText());
        } else {
            source = featuremodelsource.getNodebyName(featuremodelsource.grouproot, ctx.source().getText());
        }

        if (ctx.targettype().getText().equals("system") || ctx.targettype().getText().equals("System")) {
            target = featuremodeltarget.getNodebyName(featuremodeltarget.systemroot, ctx.target().getText());
        } else if (ctx.targettype().getText().equals("context") || ctx.targettype().getText().equals("Context")) {
            target = featuremodeltarget.getNodebyName(featuremodeltarget.contextroot, ctx.target().getText());
        } else {
            target = featuremodeltarget.getNodebyName(featuremodeltarget.grouproot, ctx.target().getText());
        }


        constraint = new Constraint(source, Integer.parseInt(ctx.sourceid().getText()), target, Integer.parseInt(ctx.targetid().getText()), -2, -2);


    }

    /**
     * Saving the created constraint in the Storage.
     *
     * @param ctx Context.
     * @see Storage
     */
    public void exitRequires(CoSyFeATParser.RequiresContext ctx) {
        storage.constraints.add(constraint);
    }

    /**
     * Create an Exclude-Constraints.
     *
     * @param ctx Context.
     */
    @Override
    public void enterExcludes(CoSyFeATParser.ExcludesContext ctx) {
        Featuremodel featuremodelsource;
        Featuremodel featuremodeltarget;

        Node source;
        Node target;

        featuremodelsource = storage.getFeaturemodelbyID(Integer.parseInt(ctx.sourceid().getText()));
        featuremodeltarget = storage.getFeaturemodelbyID(Integer.parseInt(ctx.targetid().getText()));

        if (ctx.sourcetype().getText().equals("system") || ctx.sourcetype().getText().equals("System")) {
            source = featuremodelsource.getNodebyName(featuremodelsource.systemroot, ctx.source().getText());
        } else if (ctx.sourcetype().getText().equals("context") || ctx.sourcetype().getText().equals("Context")) {
            source = featuremodelsource.getNodebyName(featuremodelsource.contextroot, ctx.source().getText());
        } else {
            source = featuremodelsource.getNodebyName(featuremodelsource.grouproot, ctx.source().getText());
        }

        if (ctx.targettype().getText().equals("system") || ctx.targettype().getText().equals("System")) {
            target = featuremodeltarget.getNodebyName(featuremodeltarget.systemroot, ctx.target().getText());
        } else if (ctx.targettype().getText().equals("context") || ctx.targettype().getText().equals("Context")) {
            target = featuremodeltarget.getNodebyName(featuremodeltarget.contextroot, ctx.target().getText());
        } else {
            target = featuremodeltarget.getNodebyName(featuremodeltarget.grouproot, ctx.source().getText());
        }
        constraint = new Constraint(source, Integer.parseInt(ctx.sourceid().getText()), target, Integer.parseInt(ctx.targetid().getText()));

    }

    /**
     * Saving the created constraint in the Storage.
     *
     * @param ctx Context.
     * @see Storage
     */
    @Override
    public void exitExcludes(CoSyFeATParser.ExcludesContext ctx) {
        storage.constraints.add(constraint);
    }

    /**
     * Set the cardinality of a Requires-Constraint.
     *
     * @param ctx Context.
     */
    @Override
    public void enterCardinality(CoSyFeATParser.CardinalityContext ctx) {
        String upperbound = ctx.upperbound().getText();
        String lowerbound = ctx.lowerbound().getText();

        if (upperbound.equals("*")) {
            constraint.setUpperbound(-1);
        } else {
            constraint.setUpperbound(Integer.parseInt(ctx.upperbound().getText()));
        }

        if (lowerbound.equals("*")) {
            constraint.setLowerbound(-1);
        } else {
            constraint.setLowerbound(Integer.parseInt(ctx.lowerbound().getText()));
        }

    }

    /**
     * Method of creating a node.
     *
     * @param fname        Name of the node.
     * @param relationType Relationship of the node to its subsequent nodes (AND, OR, ALTERNATIVE).
     */
    private void createNode(CoSyFeATParser.FnameContext fname, Node.RelationType relationType) {
        if (root) {
            node = new Node(fname.getText(), relationType, type, null);
            if (node.type == Node.Type.SYSTEM) {
                featuremodel.setSystemroot(node);
            } else if (node.type == Node.Type.CONTEXT) {
                featuremodel.setContextroot(node);
            } else {
                featuremodel.setGrouproot(node);
            }
            lastParent = node;
            root = false;

        } else {
            node = new Node(fname.getText(), relationType, type, lastParent);
            lastParent.postnodes.add(node);

            if (relationType != null) {
                lastParent = node;
            }
        }
    }


}

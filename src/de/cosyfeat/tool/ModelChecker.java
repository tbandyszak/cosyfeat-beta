package de.cosyfeat.tool;

import org.apache.commons.lang3.ArrayUtils;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import java.util.ArrayList;

/**
 * Class that checks the feature models and the constraints by translating the individual elements / constraints
 * into a boolean formula using the "LogicNG" tool.
 * This formula is solved by the MiniSat solver.
 * Outputs: Boolean formula and result.
 *
 * We utilize LogicNG distributed under the Apache License (https://github.com/logic-ng) and Apache Commons Lang (https://commons.apache.org/proper/commons-lang/)
 */

public class ModelChecker {

    private FormulaFactory f = new FormulaFactory();
    private Formula formula;
    private String instanceId;
    private String instancename;

    /**
     * Method that goes through the instances and adds the "root" of the instances.
     *
     * @param instances The instance.
     */
    private void checkNumberOfInstances(ArrayList<Instance> instances) {
        for (Instance instance : instances) {
            instanceId = "" + instance.id;
            Variable instancenode = f.variable(instance.instanceroot.name + instance.id);
            addToFormula(instancenode);
        }
    }

    /**
     * Add the grouproot of the group feature model to the formula.
     */
    private void addGrouproot(Featuremodel groupfeaturemodel) {
        Variable groupnode = f.variable(groupfeaturemodel.grouproot.name);
        addToFormula(groupnode);
    }

    /**
     * Method that goes through the instances to check whether the instances of an AND relationship have "Mandatory" or "Optional" nodes.
     *
     * @param instances The instances.
     */
    private void checkMandatoryOrOptionalInstance(ArrayList<Instance> instances) {
        for (Instance instance : instances) {
            instanceId = "" + instance.id;
            instancename = "" + instance.instanceroot.name;
            searchMandatoryOrOptional(instance.instanceroot.postnodes, instancename);
            searchMandatoryOrOptional(instance.instanceroot.postnodes, instancename);
        }
    }

    /**
     * Method that goes through the group feature model to check whether the instances of an AND relationship have "Mandotory" or "Optional" nodes.
     *
     * @param groupfeaturemodel The group feature model.
     */
    private void checkMandatoryOrOptionalGroupFeaturemodel(Featuremodel groupfeaturemodel) {
        instanceId = "";
        instancename = "";
        searchMandatoryOrOptional(groupfeaturemodel.grouproot.postnodes, instancename);
        searchMandatoryOrOptional(groupfeaturemodel.grouproot.postnodes, instancename);
    }

    /**
     * Method that goes through the nodes of the feature model.
     *
     * @param nodes List of the nodes of the feature model.
     */
    private void searchMandatoryOrOptional(ArrayList<Node> nodes, String instancename) {
        for (Node node : nodes) {
            addNode(node, instancename);
            searchMandatoryOrOptional(node.postnodes, instancename);
        }
    }

    /**
     * Adds the respective node that is "Mandatory" or "Optional" to the formula.
     *
     * @param node Node that is Mandatory or Optional.
     */
    private void addNode(Node node, String instancename) {
        String featuremodelname;
        String nodeparentname;

        if (instancename.equals("")) {
            featuremodelname = instancename;
        } else featuremodelname = instancename + "_";

        if (featuremodelname.equals(node.parent.name + "_")) {
            nodeparentname = "";
        } else nodeparentname = featuremodelname;

        Variable nodekind = f.variable(featuremodelname + node.name + instanceId);
        Variable nodeparent = f.variable(nodeparentname + node.parent.name + instanceId);


        if (node.mandopt == Node.MandOpt.MANDATORY) {
            addToFormula(f.equivalence(nodeparent, nodekind));

        } else if (node.mandopt == Node.MandOpt.OPTIONAL) {
            addToFormula(f.implication(nodekind, nodeparent));

        }


    }


    /**
     * Method that goes through the instances to check whether the instance has an OR- or an ALTERNATIVE-relationship.
     *
     * @param instances The instances.
     */
    private void checkORAlternativeInstance(ArrayList<Instance> instances) {
        for (Instance instance : instances) {
            instanceId = "" + instance.id;
            instancename = "" + instance.instanceroot.name;
            addOrAlternative(instance.instanceroot, instancename);
            searchOrAlternative(instance.instanceroot.postnodes, instancename);
        }

    }

    /**
     * Method that goes through the group feature model to check whether the instance has an OR- or an ALTERNATIVE-relationship.
     *
     * @param groupfeaturemodel The group feature model.
     */
    private void checkORAlternativeGroupFeaturemodel(Featuremodel groupfeaturemodel) {
        instanceId = "";
        instancename = "";
        addOrAlternative(groupfeaturemodel.grouproot, instancename);
        searchOrAlternative(groupfeaturemodel.grouproot.postnodes, instancename);
    }

    /**
     * Search the nodes of the feature model for OR- or ALTERNATIVE-relationships.
     *
     * @param nodes Nodes of the feature model.
     */
    private void searchOrAlternative(ArrayList<Node> nodes, String instancename) {
        for (Node node : nodes) {
            addOrAlternative(node, instancename);
            searchOrAlternative(node.postnodes, instancename);
        }
    }

    /**
     * Add the OR- or ALTERNATIVE-relationship to the formula.
     *
     * @param node Nodes and subsequent nodes that are added to the formula.
     */
    private void addOrAlternative(Node node, String instancename) {
        Variable[] postnodes = new Variable[node.postnodes.size()];
        Variable[] shortnodes;
        String featuremodelname;
        String nodeparentname;

        if (instancename.equals("")) {
            featuremodelname = instancename;
        } else featuremodelname = instancename + "_";

        if (featuremodelname.equals(node.name + "_")) {
            nodeparentname = "";
        } else nodeparentname = featuremodelname;

        if (node.relation == Node.RelationType.OR) {
            for (int i = 0; i < node.postnodes.size(); i++) {
                Node postnode = node.postnodes.get(i);
                postnodes[i] = f.variable(featuremodelname + postnode.name + instanceId);
            }

            Variable nodeparent = f.variable(nodeparentname + node.name + instanceId);
            addToFormula(f.equivalence(nodeparent, f.or(postnodes)));

        } else if (node.relation == Node.RelationType.ALTERNATIVE) {
            Variable nodeparent = f.variable(nodeparentname + node.name + instanceId);

            for (int i = 0; i < node.postnodes.size(); i++) {
                Node postnode = node.postnodes.get(i);
                postnodes[i] = f.variable(featuremodelname + postnode.name + instanceId);
            }

            for (Variable postnode : postnodes) {
                shortnodes = ArrayUtils.removeElement(postnodes, postnode);
                addToFormula(f.equivalence(postnode, f.and(f.not(f.or(shortnodes)), nodeparent)));
            }


        }

    }


    /**
     * Method of checking whether a constraint is an EXCLUDES- or REQUIRES-relationship.
     *
     * @param constraint    The constraint.
     * @param featuremodels List of the feature models.
     */

    private void checkExcludesRequires(Constraint constraint, ArrayList<Featuremodel> featuremodels) {

        if (constraint.type == Constraint.Type.EXCLUDES) {

            addExcludes(constraint, featuremodels);

        } else if (constraint.type == Constraint.Type.REQUIRES) {

            checkCardinality(constraint, featuremodels);

        }
    }


    /**
     * Check what cardinality the REQUIRES-relationship has.
     *
     * @param constraint    The constraint.
     * @param featuremodels The feature models.
     */
    private void checkCardinality(Constraint constraint, ArrayList<Featuremodel> featuremodels) {

        if (constraint.lowerbound == 1 && constraint.upperbound == -1) {

            addOneToMany(constraint, featuremodels);

        } else if (constraint.lowerbound == -1 && constraint.upperbound == -1) {
            addRequiresAll(constraint, featuremodels);

        } else if (constraint.lowerbound == -2 && constraint.upperbound == -2) {

            addNoCardinality(constraint, featuremodels);

        } else if (constraint.lowerbound == 1 && constraint.upperbound == 1) {

            addOneToOne(constraint, featuremodels);

        } else if (constraint.lowerbound >= 1 && (constraint.upperbound >= constraint.lowerbound || constraint.upperbound == -1)) {

            addMinMaxDependency(constraint, featuremodels);

        }
    }


    /**
     * Check the EXCLUDES-relationship.
     *
     * @param constraint    The constraint.
     * @param featuremodels The feature models.
     */
    private void addExcludes(Constraint constraint, ArrayList<Featuremodel> featuremodels) {
        Featuremodel sourceFeaturemodel = getFeaturemodelbyID(constraint.sourceid, featuremodels);
        Featuremodel targetFeaturemodel = getFeaturemodelbyID(constraint.targetid, featuremodels);
        Node source = constraint.source;
        Node target = constraint.target;

        if (constraint.source.type == Node.Type.CONTEXT) {
            constraint.source = target;
            constraint.target = source;
            ArrayList<Featuremodel> sourceFeaturemodels = new ArrayList<>();
            for (Featuremodel featuremodel : featuremodels) {
                if (featuremodel.isNodeInFeaturemodel(constraint.target.name)) {
                    sourceFeaturemodels.add(featuremodel);
                }
            }
            excludesToFormula(constraint, targetFeaturemodel, sourceFeaturemodels);
        } else if (constraint.source.type == Node.Type.SYSTEM) {
            ArrayList<Featuremodel> targetFeaturemodels = new ArrayList<>();
            for (Featuremodel featuremodel : featuremodels) {
                if (featuremodel.isNodeInFeaturemodel(constraint.target.name)) {
                    targetFeaturemodels.add(featuremodel);
                }
            }
            excludesToFormula(constraint, sourceFeaturemodel, targetFeaturemodels);

        }

    }

    /**
     * Add the Excludes-relationship to the formula.
     *
     * @param constraint          The Excludes-Constraint.
     * @param sourceFeaturemodel  The feature model that is the source of the excludes relationship.
     * @param targetFeaturemodels The feature model (s) that is the target of the excludes relationship.
     */
    private void excludesToFormula(Constraint constraint, Featuremodel sourceFeaturemodel, ArrayList<Featuremodel> targetFeaturemodels) {


        Variable[] targetarray = getTargetArray(constraint, targetFeaturemodels);

        Variable[] shorttarget;

        for (Instance sourceInstance : sourceFeaturemodel.instances) {
            Variable nodesource = f.variable(sourceFeaturemodel.systemroot.name + "_" + constraint.source.name + sourceInstance.id);

            if (sourceFeaturemodel.isNodeInFeaturemodel(constraint.target.name)) {
                shorttarget = ArrayUtils.removeElement(targetarray, targetarray[sourceInstance.id - 1]);
                addToFormula(f.implication(nodesource, f.not(f.or(shorttarget))));
            } else addToFormula(f.implication(nodesource, f.not(f.or(targetarray))));
        }
    }


    /**
     * Method that adds the REQUIRES-relationship with the cardinality [1..*] to the formula.
     * Differentiation of the type feature model with its system and context branch and the group feature model.
     *
     * @param constraint    The constraint.
     * @param featuremodels The feature models.
     */
    private void addOneToMany(Constraint constraint, ArrayList<Featuremodel> featuremodels) {
        Featuremodel sourceFeaturemodel = getFeaturemodelbyID(constraint.sourceid, featuremodels);
        Featuremodel targetFeaturemodel = getFeaturemodelbyID(constraint.targetid, featuremodels);

        ArrayList<Variable> targetlist = getTargetList(constraint, featuremodels);
        Variable[] targetarray = getTargetArray(constraint, featuremodels);


        if (constraint.sourceid == constraint.targetid && targetarray.length <= 1 && sourceFeaturemodel.isNodeInFeaturemodel(constraint.target.name)) {
            addToFormula(f.falsum());
        } else if (constraint.source.type == Node.Type.SYSTEM && constraint.target.type == Node.Type.CONTEXT) {
            Variable[] shorttarget;

            for (Instance sourceInstance : sourceFeaturemodel.instances) {
                Variable nodesource = f.variable(sourceFeaturemodel.systemroot.name + "_" + constraint.source.name + sourceInstance.id);

                if (sourceFeaturemodel.isNodeInFeaturemodel(constraint.target.name)) {
                    shorttarget = ArrayUtils.removeElement(targetarray, targetarray[sourceInstance.id - 1]);
                    addToFormula(f.implication(nodesource, f.or(shorttarget)));
                } else addToFormula(f.implication(nodesource, f.or(targetarray)));
            }
        } else if (constraint.source.type == Node.Type.GROUP && targetarray.length > 1) {
            Variable nodesource = f.variable(constraint.source.name);
            addToFormula(f.implication(nodesource, f.or(targetlist)));

        } else if (targetarray.length == 1 && constraint.source.type == Node.Type.GROUP) {
            addToFormula(f.implication(f.variable(constraint.source.name), f.variable(targetFeaturemodel.systemroot.name + "_" + constraint.target.name + 1)));
        }
    }

    /**
     * Method that adds the REQUIRES-relationship with the cardinality [*..*] to the formula.
     *
     * @param constraint    The constraint.
     * @param featuremodels The feature models.
     */
    private void addRequiresAll(Constraint constraint, ArrayList<Featuremodel> featuremodels) {
        Featuremodel sourceFeaturemodel = getFeaturemodelbyID(constraint.sourceid, featuremodels);
        Featuremodel targetFeaturemodel = getFeaturemodelbyID(constraint.targetid, featuremodels);


        ArrayList<Variable> targetlist = getTargetList(constraint, featuremodels);
        Variable[] targetarray = getTargetArray(constraint, featuremodels);

        if (constraint.sourceid == constraint.targetid && targetarray.length <= 1 && sourceFeaturemodel.isNodeInFeaturemodel(constraint.target.name)) {
            addToFormula(f.falsum());
        } else if (constraint.source.type == Node.Type.SYSTEM && constraint.target.type == Node.Type.CONTEXT) {
            Variable[] shorttarget;

            for (Instance sourceInstance : sourceFeaturemodel.instances) {
                Variable nodesource = f.variable(sourceFeaturemodel.systemroot.name + "_" + constraint.source.name + sourceInstance.id);

                if (sourceFeaturemodel.isNodeInFeaturemodel(constraint.target.name)) {
                    shorttarget = ArrayUtils.removeElement(targetarray, targetarray[sourceInstance.id - 1]);
                    addToFormula(f.implication(nodesource, f.and(shorttarget)));
                } else addToFormula(f.implication(nodesource, f.and(targetarray)));
            }
        } else if (constraint.source.type == Node.Type.GROUP && targetarray.length > 1) {
            Variable nodesource = f.variable(constraint.source.name);
            addToFormula(f.implication(nodesource, f.and(targetlist)));

        } else if (targetarray.length == 1 && constraint.source.type == Node.Type.GROUP) {
            addToFormula(f.implication(f.variable(constraint.source.name), f.variable(targetFeaturemodel.systemroot.name + "_" + constraint.target.name + 1)));
        }
    }

    /**
     * Method that adds the REQUIRES-relationship with the cardinality [1..1] to the formula.
     *
     * @param constraint    The constraint.
     * @param featuremodels The feature models.
     */
    private void addOneToOne(Constraint constraint, ArrayList<Featuremodel> featuremodels) {
        Featuremodel sourceFeaturemodel = getFeaturemodelbyID(constraint.sourceid, featuremodels);
        Featuremodel targetFeaturemodel = getFeaturemodelbyID(constraint.targetid, featuremodels);


        ArrayList<Variable> targetlist = getTargetList(constraint, featuremodels);


        if (constraint.sourceid == constraint.targetid && targetlist.size() <= 1 && sourceFeaturemodel.isNodeInFeaturemodel(constraint.target.name)) {
            addToFormula(f.falsum());
        } else if (targetlist.size() == 1 && constraint.source.type != Node.Type.GROUP) {
            for (Instance sourceInstance : sourceFeaturemodel.instances) {
                Variable nodesource = f.variable(sourceFeaturemodel.systemroot.name + "_" + constraint.source.name + sourceInstance.id);
                addToFormula(f.implication(nodesource, f.variable(targetFeaturemodel.systemroot.name + "_" + constraint.target.name + 1)));
            }
        } else if (targetlist.size() == 1) {
            addToFormula(f.implication(f.variable(constraint.source.name), f.variable(targetFeaturemodel.systemroot.name + "_" + constraint.target.name + 1)));
        } else if (targetlist.size() > 1) {
            for (int sourceId = 0; sourceId < targetlist.size(); sourceId++) {
                Formula outerAnd = f.verum();
                for (int targetId = 0; targetId < targetlist.size(); targetId++) {
                    if (!sourceFeaturemodel.isNodeInFeaturemodel(constraint.target.name) || sourceId != targetId) {

                        Formula innerOr = f.falsum();
                        for (int innerTargetId = 0; innerTargetId < targetlist.size(); innerTargetId++) {
                            if ((!sourceFeaturemodel.isNodeInFeaturemodel(constraint.target.name) || sourceId != innerTargetId) && targetId != innerTargetId) {

                                innerOr = f.or(innerOr, targetlist.get(innerTargetId));
                            }
                        }

                        outerAnd = f.and(outerAnd, f.equivalence(targetlist.get(targetId), f.not(innerOr)));
                    }
                }
                if (constraint.source.type == Node.Type.GROUP) {
                    addToFormula(f.implication(f.variable(constraint.source.name), outerAnd));
                } else if (constraint.source.type == Node.Type.SYSTEM) {
                    addToFormula(f.implication(f.variable(sourceFeaturemodel.systemroot.name + "_" + constraint.source.name + (sourceId + 1)), outerAnd));
                }

            }

        }

    }

    /**
     * Method that adds the REQUIRES-relationship with no cardinality to the formula.
     * This relationship can only exist between two nodes in a type feature model in the system branch.
     *
     * @param constraint    The constraint.
     * @param featuremodels The feature models.
     */
    private void addNoCardinality(Constraint constraint, ArrayList<Featuremodel> featuremodels) {
        Featuremodel sourceFeaturemodel = getFeaturemodelbyID(constraint.sourceid, featuremodels);
        Featuremodel targetFeaturemodel = getFeaturemodelbyID(constraint.targetid, featuremodels);

        if (constraint.source.type == Node.Type.SYSTEM && constraint.target.type == Node.Type.SYSTEM) {
            for (Instance sourceInstance : sourceFeaturemodel.instances) {

                Variable nodesource = f.variable(sourceFeaturemodel.systemroot.name + "_" + constraint.source.name + sourceInstance.id);
                Variable nodetarget = f.variable(targetFeaturemodel.systemroot.name + "_" + constraint.target.name + sourceInstance.id);

                addToFormula(f.implication(nodesource, nodetarget));
            }


        }
    }

    /**
     * Method that adds the REQUIRES-relationship with [min..max] cardinality to the formula.
     *
     * @param constraint    The constraint.
     * @param featuremodels The feature models.
     */
    public void addMinMaxDependency(Constraint constraint, ArrayList<Featuremodel> featuremodels) {
        Featuremodel sourceFeaturemodel = getFeaturemodelbyID(constraint.sourceid, featuremodels);

        ArrayList<Variable> targetlist = getTargetList(constraint, featuremodels);

        if (constraint.upperbound == -1) constraint.upperbound = targetlist.size();

        if (constraint.lowerbound >= targetlist.size()) {
            addToFormula(f.falsum());
        } else if (constraint.source.type == Node.Type.SYSTEM && constraint.target.type == Node.Type.CONTEXT) {

            Formula andFormula;
            boolean[][] booleanarray = getBoolArray(targetlist.size());

            if ((constraint.upperbound - constraint.lowerbound) >= 3 && constraint.upperbound >= (targetlist.size() - 7)) {  /* TODO: Hier wieder umändern!*/
                andFormula = getCaseOneMinMax(constraint, booleanarray, targetlist);
                //System.out.println("CaseOne");
            } else {
                andFormula = getCaseTwoMinMax(constraint, booleanarray, targetlist);
                // System.out.println("CaseTwo");
            }
            for (Instance sourceInstance : sourceFeaturemodel.instances) {
                Variable nodesource = f.variable(sourceFeaturemodel.systemroot.name + "_" + constraint.source.name + sourceInstance.id);
                addToFormula(f.implication(nodesource, andFormula));
            }
        }

    }

    /**
     * Method for max-min>=3 && max>=(|Number of targets| -2)
     *
     * @param constraint   The constraint.
     * @param booleanarray The boolean array.
     * @param targetlist   The List of Targetnodes.
     * @return The Formula.
     */
    public Formula getCaseOneMinMax(Constraint constraint, boolean[][] booleanarray, ArrayList<Variable> targetlist) {
        Formula firstAnd = f.verum();
        for (int i = 0; i <= targetlist.size(); i++) {
            if (i < constraint.lowerbound || i > constraint.upperbound) {
                ArrayList<boolean[]> filterboolean = filterarray(booleanarray, i);

                for (boolean[] element : filterboolean) {
                    Variable[] frontAnd = new Variable[targetlist.size()];
                    Variable[] backAnd = new Variable[targetlist.size()];
                    for (int j = 0; j <= element.length - 1; j++) {
                        if (element[j]) {
                            frontAnd[j] = targetlist.get(j);
                        } else {
                            backAnd[j] = targetlist.get(j);
                        }
                    }


                    for (int l = (targetlist.size()); l-- > 0; ) {
                        if (frontAnd[l] == null) {
                            frontAnd = ArrayUtils.removeElement(frontAnd, frontAnd[l]);
                        }
                        if (backAnd[l] == null) {
                            backAnd = ArrayUtils.removeElement(backAnd, backAnd[l]);
                        }
                    }

                    if (backAnd.length != 0) {
                        firstAnd = f.and(firstAnd, f.not((f.and(f.and(frontAnd), f.not(f.or(backAnd))))));
                    } else {
                        firstAnd = f.and(firstAnd, f.not(f.and(frontAnd)));
                    }

                }

            }
        }
        return firstAnd;
    }

    /**
     * Method for max-min<3 || max<(|Number of targets| -2)
     *
     * @param constraint   The constraint.
     * @param booleanarray The booleanarray.
     * @param targetlist   The List of Targetnodes.
     * @return The Formula.
     */

    public Formula getCaseTwoMinMax(Constraint constraint, boolean[][] booleanarray, ArrayList<Variable> targetlist) {
        Formula firstAnd = f.falsum();
        for (int i = constraint.lowerbound; i <= constraint.upperbound; i++) {
            ArrayList<boolean[]> filterboolean = filterarray(booleanarray, i);

            for (boolean[] element : filterboolean) {
                Variable[] frontAnd = new Variable[targetlist.size()];
                Variable[] backAnd = new Variable[targetlist.size()];
                for (int j = 0; j <= element.length - 1; j++) {
                    if (element[j]) {
                        frontAnd[j] = targetlist.get(j);
                    } else {
                        backAnd[j] = targetlist.get(j);
                    }
                }


                for (int l = (targetlist.size()); l-- > 0; ) {
                    if (frontAnd[l] == null) {
                        frontAnd = ArrayUtils.removeElement(frontAnd, frontAnd[l]);
                    }
                    if (backAnd[l] == null) {
                        backAnd = ArrayUtils.removeElement(backAnd, backAnd[l]);
                    }
                }

                if (backAnd.length != 0) {
                    firstAnd = f.or(firstAnd, (f.and(f.and(frontAnd), f.not(f.or(backAnd)))));
                } else {
                    firstAnd = f.or(firstAnd, f.and(frontAnd));
                }

            }

        }

        return firstAnd;

    }

    /**
     * Method that returns a two-dimensional Bool array with all combinations of True / False for a certain number of variables.
     *
     * @param size Number of variables.
     * @return Two-dimensional Bool array.
     */
    private boolean[][] getBoolArray(int size) {

        int pow = (int) Math.pow(2, size);
        boolean[][] result = new boolean[pow][size];

        for (int i = 0; i < pow; i++) {
            result[i] = toBinary(i, size);
        }
        return result;
    }

    /**
     * Method that filters the two-dimensional bool array according to the current boundary.
     *
     * @param result Two-dimensional Bool array.
     * @param number Boundary.
     * @return List of all filtered combinations of True / False.
     */
    private ArrayList<boolean[]> filterarray(boolean[][] result, int number) {
        ArrayList<boolean[]> endresult = new ArrayList<>();
        for (int j = 0; j <= result.length - 1; j++) {
            int counttrue = 0;
            for (int k = 0; k <= result[j].length - 1; k++) {
                if (result[j][k]) {
                    counttrue++;
                }
            }
            if (number == counttrue) {
                endresult.add(result[j]);
            }
        }
        return endresult;
    }

    /**
     * Creates all combinations of True / False for a certain number of variables
     *
     * @param number    Number of the current combination.
     * @param arraysize Number of variables.
     * @return Bool array.
     */
    private static boolean[] toBinary(int number, int arraysize) {
        final boolean[] ret = new boolean[arraysize];
        for (int j = 0; j < arraysize; j++) {
            ret[arraysize - 1 - j] = (1 << j & number) != 0;
        }
        return ret;
    }

    /**
     * Returns an list of targetnodes.
     *
     * @param featuremodels The featuremodels.
     * @param constraint    The constraint.
     * @return List of targetnodes.
     */
    private ArrayList<Variable> getTargetList(Constraint constraint, ArrayList<Featuremodel> featuremodels) {
        ArrayList<Variable> targetlist = new ArrayList<>();
        for (Featuremodel featuremodel : featuremodels) {
            if (featuremodel.isNodeInFeaturemodel(constraint.target.name)) {
                for (int i = 0; i < featuremodel.numberOfInstances; i++) {
                    Variable nodet = f.variable(featuremodel.systemroot.name + "_" + constraint.target.name + (i + 1));
                    targetlist.add(nodet);
                }
            }
        }
        return targetlist;
    }

    /**
     * Returns an array of targetnodes.
     *
     * @param featuremodels The featuremodels.
     * @param constraint    The constraint.
     * @return Array of targetnodes.
     */

    private Variable[] getTargetArray(Constraint constraint, ArrayList<Featuremodel> featuremodels) {
        ArrayList<Variable> targetlist = getTargetList(constraint, featuremodels);

        Variable[] targetarray = new Variable[targetlist.size()];

        for (int i = 0; i < targetarray.length; i++) {
            targetarray[i] = targetlist.get(i);
        }
        return targetarray;
    }

    /**
     * Method for running through an instance with the output of the Boolean formula and the result.
     * Called from the de.cosyfeat.Main.
     *
     * @param instances The instances.
     */
    public void runInstances(ArrayList<Instance> instances) {
        checkNumberOfInstances(instances);
        checkMandatoryOrOptionalInstance(instances);
        checkORAlternativeInstance(instances);

    }

    /**
     * Method for running through a feature model with output of the Boolean formula and the result.
     * Called from the de.cosyfeat.Main.
     *
     * @param groupfeaturemodel The instances.
     */
    public void runGroupmodel(Featuremodel groupfeaturemodel) {
        addGrouproot(groupfeaturemodel);
        checkMandatoryOrOptionalGroupFeaturemodel(groupfeaturemodel);
        checkORAlternativeGroupFeaturemodel(groupfeaturemodel);
    }

    /**
     * Method for running through a constraint with the output of the Boolean formula and the result.
     * Called from the de.cosyfeat.Main.
     *
     * @param constraint    The constraint.
     * @param featuremodels The feature models.
     */
    public void runConstraints(Constraint constraint, ArrayList<Featuremodel> featuremodels) {

        checkExcludesRequires(constraint, featuremodels);

    }

    /**
     * Adds the formula to the MiniSat solver and checks it.
     * Returns the result of the formula on the console.
     */
    public void printResult() {
        SATSolver miniSat = MiniSat.miniSat(f);
        miniSat.add(formula);

        System.out.println("Formula : " + formula.toString());
        System.out.println("Result : " + miniSat.sat());
    }

    /**
     * Adds the formula to the MiniSat solver and checks it.
     * Returns the result of the formula as String.
     */
    public String getResult(){
        SATSolver miniSat = MiniSat.miniSat(f);
        miniSat.add(formula);
        String result ="";

        result+="Formula : " + formula.toString();
        result+="\n\nResult : " + miniSat.sat();

        return result;
    }

    /**
     * Adds a sub-formula to the overall formula.
     *
     * @param formulaToAdd The sub-formula.
     */
    private void addToFormula(Formula formulaToAdd) {
        if (formula == null) {
            formula = formulaToAdd;
        } else {
            formula = f.and(formula, formulaToAdd);
        }
    }

    /**
     * Finds a feature model based on the ID and returns the feature model.
     *
     * @param id            ID of the feature model.
     * @param featuremodels The feature models.
     * @return The feature model or Null.
     */
    public Featuremodel getFeaturemodelbyID(int id, ArrayList<Featuremodel> featuremodels) {
        for (Featuremodel featuremodel : featuremodels) {
            if (featuremodel.modelid == id) return featuremodel;
        }
        return null;
    }
}
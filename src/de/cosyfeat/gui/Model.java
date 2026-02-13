package de.cosyfeat.gui;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.*;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates the Model.
 */
public class Model extends JFrame {
    mxGraph graph;
    mxGraphComponent graphComponent;
    int featuremodelCounter = 1;
    ArrayList<Integer> deletedFeaturemodels = new ArrayList<>();
    mxUndoManager undoManager;

    /**
     * Creates the initial Model.
     *
     * @return The Model.
     */
    public mxGraph createModel() {

        graph = new mxGraph();
        undoManager = new mxUndoManager();
        ModelStyle modelStyle = new ModelStyle();
        configureGraph(graph);
        Object defaultParent = graph.getDefaultParent();


        mxStylesheet stylesheet = graph.getStylesheet();
        stylesheet.putCellStyle("featuremodel", modelStyle.createFeaturemodelStyle());
        stylesheet.putCellStyle("groupmodel", modelStyle.createGroupmodelStyle());
        stylesheet.putCellStyle("feature", modelStyle.createFeatureStyle());
        stylesheet.putCellStyle("normaledge", modelStyle.createNormalStyle());
        stylesheet.putCellStyle("requires", modelStyle.createRequiresStyle());
        stylesheet.putCellStyle("exclude", modelStyle.createExcludesStyle());
        stylesheet.putCellStyle("mandatory", modelStyle.createMandatoryStyle());
        stylesheet.putCellStyle("optional", modelStyle.createOptionalStyle());

        stylesheet.setDefaultEdgeStyle(modelStyle.createNormalStyle());

        graph.getModel().beginUpdate();


        try {
            Object featuremodel = graph.insertVertex(defaultParent, null, "Featuremodel" + featuremodelCounter, 250, 20, 80,
                    30, "featuremodel");

            Object context = graph.insertVertex(graph.getDefaultParent(), null, "Context", 200, 80, 80,
                    30, "feature");
            Object system = graph.insertVertex(graph.getDefaultParent(), null, "System", 300, 80, 80,
                    30, "feature");

            Object edgeSystem = graph.insertEdge(graph.getDefaultParent(), null, "", featuremodel, system, "mandatory");
            Object edgeContext = graph.insertEdge(graph.getDefaultParent(), null, "", featuremodel, context, "optional");

            Object groupmodel = graph.insertVertex(defaultParent, null, "Groupmodel", 490, 20,
                    80, 30, "groupmodel");
        } finally {
            graph.getModel().endUpdate();
        }

        graphComponent = new mxGraphComponent(graph);
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.WHITE);

        getContentPane().add(graphComponent);
        return graph;
    }

    protected mxEventSource.mxIEventListener undoHandler = new mxEventSource.mxIEventListener()
    {
        public void invoke(Object source, mxEventObject evt)
        {
            undoManager.undoableEditHappened((mxUndoableEdit) evt
                    .getProperty("edit"));
        }
    };

    public mxUndoManager getUndoManager()
    {
        return undoManager;
    }

    /**
     * Configurations for the Graph.
     *
     * @param graph Graph.
     */
    private void configureGraph(mxGraph graph) {

        graph.getModel().addListener(mxEvent.UNDO, undoHandler);
        graph.getView().addListener(mxEvent.UNDO, undoHandler);

        mxEventSource.mxIEventListener undoHandler = new mxEventSource.mxIEventListener()
        {
            public void invoke(Object source, mxEventObject evt)
            {
                List<mxUndoableEdit.mxUndoableChange> changes = ((mxUndoableEdit) evt
                        .getProperty("edit")).getChanges();
                graph.setSelectionCells(graph
                        .getSelectionCellsForChanges(changes));
            }
        };

        undoManager.addListener(mxEvent.UNDO, undoHandler);
        undoManager.addListener(mxEvent.REDO, undoHandler);
        graph.setEnabled(false);
        graph.setCellsResizable(true);
        graph.setConstrainChildren(true);
        graph.setExtendParents(true);
        graph.setExtendParentsOnAdd(true);
        graph.setDefaultOverlap(0);
        graph.setAllowDanglingEdges(false);
        graph.setDisconnectOnMove(false);
        graph.setCellsEditable(false);
        graph.setAutoSizeCells(true);
    }

    /**
     * Adds a Vertex connected with an Edge to the selected Cell (Vertex).
     *
     * @param c The selected Cell (Vertex).
     */
    public void addVertex(Object c) {
        String edgeStyle = searchForEdgeStyle(c);
        graph.getModel().beginUpdate();
        JTextField name = new JTextField(25);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Name"));
        myPanel.add(name);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please enter a Name", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Object newVertex = graph.insertVertex(graph.getDefaultParent(), null, name.getText(), ((mxCell) c).getGeometry().getX(), ((mxCell) c).getGeometry().getY() + 80, 80,
                    30, "feature");
            if (edgeStyle.equals("")) {
                Object newEdge = graph.insertEdge(graph.getDefaultParent(), null, "", (mxCell) c, newVertex, "normaledge");
            } else if (edgeStyle.equals("mandatory") || edgeStyle.equals("optional")) {
                Object newEdge = graph.insertEdge(graph.getDefaultParent(), null, "", (mxCell) c, newVertex, edgeStyle);
            } else {
                Object newEdge = graph.insertEdge(graph.getDefaultParent(), null, edgeStyle, (mxCell) c, newVertex, "normaledge");
            }

        }

        graph.getModel().endUpdate();

    }

    /**
     * Checks which style the Edge has.
     *
     * @param c The Edge.
     * @return The Style.
     */
    public String searchForEdgeStyle(Object c) {
        Object[] edges = graph.getOutgoingEdges((mxCell) c);
        String result = "";
        if (edges.length != 0) {
            Object firstEdge = edges[0];

            if (((mxCell) firstEdge).getStyle().equals("mandatory") && !((mxCell) firstEdge).getSource().getStyle().equals("featuremodel")) {
                result = "mandatory";
            } else if (((mxCell) firstEdge).getStyle().equals("optional") && !((mxCell) firstEdge).getSource().getStyle().equals("featuremodel")) {
                result = "optional";
            } else if (((mxCell) firstEdge).getValue() == "OR" && !((mxCell) firstEdge).getSource().getStyle().equals("featuremodel")) {
                result = "OR";
            } else if (((mxCell) firstEdge).getValue() == "XOR" && !((mxCell) firstEdge).getSource().getStyle().equals("featuremodel")) {
                result = "XOR";
            }
        }
        return result;
    }

    /**
     * Deletes the selected Cell.
     *
     * @param c The selected Cell.
     */
    public void deleteCell(Object c) {
        if (((mxCell) c).isVertex() && isFeaturemodelRoot(c)) {
            Object[] outgoingEdges = graph.getOutgoingEdges(((mxCell) c));
            String featuremodelname = (String) ((mxCell) c).getValue();
            String[] number = featuremodelname.split("Featuremodel");
            number = number[1].split(" ");
            deletedFeaturemodels.add(Integer.parseInt(number[0]));
            graph.removeCells(new Object[]{c});
            deleteChildNodes(outgoingEdges);
            graph.refresh();

        } else {
            graph.removeCells(new Object[]{c});
            graph.refresh();
        }

    }

    /**
     * Deletes all Child-Nodes from the Featuremodel.
     * @param outgoingEdges Outgoing-Edges of the Featuremodelroot.
     */
    private void deleteChildNodes(Object[] outgoingEdges){
        for(Object edge : outgoingEdges){
                deleteChildNodes(graph.getOutgoingEdges(((mxCell) edge).getTarget()));
                graph.removeCells(new Object[]{((mxCell)edge).getTarget()});
            }
    }

    /**
     * Checks whether the selected Cell is a Vertex.
     *
     * @param c The selected Cell.
     * @return TRUE/FALSE
     */
    public boolean isVertex(Object c) {
        return graph.getModel().isVertex(c);
    }

    /**
     * Checks whether the selected Cell is the Featuremodel-Root.
     *
     * @param c The selected Cell.
     * @return TRUE/FALSE
     */
    public boolean isFeaturemodelRoot(Object c) {
        return (((mxCell) c).getStyle().equals("featuremodel"));
    }

    /**
     * Checks whether the selected Cell is the Groopmodel-Root.
     *
     * @param c The selected Cell.
     * @return TRUE/FALSE
     */
    public boolean isGroupmodelroot(Object c) {
        return (((mxCell) c).getStyle().equals("groupmodel"));
    }

    /**
     * Checks whether the selected Cell is the Context of a Featuremodel.
     *
     * @param c The selected Cell.
     * @return TRUE/FALSE
     */
    public boolean isContext(Object c) {
        return (((mxCell) c).getValue() == "Context");
    }

    /**
     * Checks whether the selected Cell is the Edge between the Featuremodel-Root and the Context/System.
     *
     * @param c The selected Cell.
     * @return TRUE/FALSE
     */
    public boolean isContextSystemEdge(Object c) {
        return (((mxCell) c).getSource().getStyle().equals("featuremodel"));
    }

    /**
     * Changes the Style of the selected Edge.
     *
     * @param c         The selecet Edge.
     * @param edgeStyle The chosen Style.
     */
    public void changeEdge(Object c, edgeStyle edgeStyle) {
        Object[] edges = graph.getOutgoingEdges(((mxCell) c).getSource());

        if (edgeStyle == Model.edgeStyle.MANDATORY) {
            ((mxCell) c).setValue("");
            ((mxCell) c).setStyle("mandatory");
        } else if (edgeStyle == Model.edgeStyle.OPTIONAL) {
            ((mxCell) c).setValue("");
            ((mxCell) c).setStyle("optional");
        } else if (edgeStyle == Model.edgeStyle.OR) {
            for (Object edge : edges) {
                ((mxCell) edge).setValue("OR");
                ((mxCell) edge).setStyle("normaledge");
            }
        } else if (edgeStyle == Model.edgeStyle.XOR) {
            for (Object edge : edges) {
                ((mxCell) edge).setValue("XOR");
                ((mxCell) edge).setStyle("normaledge");
            }
        } else if (edgeStyle == Model.edgeStyle.REQUIRES) {
            String bounds = askForBounds();
            ((mxCell) c).setValue("Requires " + bounds);
            ((mxCell) c).setStyle("requires");
        } else if (edgeStyle == Model.edgeStyle.EXCLUDE) {
            ((mxCell) c).setValue("Excludes");
            ((mxCell) c).setStyle("exclude");
        }

        graph.refresh();

    }

    /**
     * Changes the Number of Instances of a Featuremodel.
     *
     * @param c The Featuremodel-Root.
     */
    public void changeInstances(Object c) {
        String instances = JOptionPane.showInputDialog(null, "Please enter the number of Instances",
                "Number", JOptionPane.PLAIN_MESSAGE);
        if (StringUtils.isNumeric(instances)&& instances!=null) {
            ((mxCell) c).setValue(" " + ((mxCell) c).getValue() + "  \n  " + "Instances:" + instances);
        } else if (!instances.isEmpty()) JOptionPane.showMessageDialog(null, "This is not a Number!");


        graph.updateCellSize(c);
        graph.refresh();

    }

    /**
     * Changes the Name of the Vertex.
     *
     * @param c The selected Vertex.
     */
    public void changeVertexName(Object c) {
        String name = JOptionPane.showInputDialog(null, "Please enter a Name",
                "Name", JOptionPane.PLAIN_MESSAGE);
        ((mxCell) c).setValue("    " + name + "    \n");

        graph.updateCellSize(c);
        graph.refresh();
    }

    public enum edgeStyle {
        MANDATORY, OPTIONAL, OR, XOR, REQUIRES, EXCLUDE;
    }

    /**
     * Asks for the Bounds of a Requires-Edge.
     *
     * @return The Bounds.
     */
    private String askForBounds() {
        String bounds = "";
        JTextField lowerboundField = new JTextField(5);
        JTextField upperboundField = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Lowerbound:"));
        myPanel.add(lowerboundField);
        myPanel.add(Box.createHorizontalStrut(15));
        myPanel.add(new JLabel("Upperbound:"));
        myPanel.add(upperboundField);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please enter Lowerbound and Upperbound", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (lowerboundField.getText().isEmpty() && upperboundField.getText().isEmpty()) {
                bounds = "";
            } else bounds = "[" + lowerboundField.getText() + ".." + upperboundField.getText() + "]";
        }

        return bounds;
    }

    /**
     * Adds a new Featuremodel to the Graph at the Coordinates.
     *
     * @param x X-Coordinate.
     * @param y Y-Coordinate.
     */
    public void addNewFeaturemodel(int x, int y) {
        int featuremodelid;
        if(deletedFeaturemodels.size()>0){
            featuremodelid = deletedFeaturemodels.get(0);
            deletedFeaturemodels.remove(0);
        } else {
            featuremodelCounter++;
            featuremodelid=featuremodelCounter;
        }

        graph.getModel().beginUpdate();

        Object featuremodel = graph.insertVertex(graph.getDefaultParent(), null, "Featuremodel" + featuremodelid, x, y + 20, 80,
                30, "featuremodel");

        Object context = graph.insertVertex(graph.getDefaultParent(), null, "Context", x - 50, y + 80, 80,
                30, "feature");
        Object system = graph.insertVertex(graph.getDefaultParent(), null, "System", x + 50, y + 80, 80,
                30, "feature");

        Object edgeSystem = graph.insertEdge(graph.getDefaultParent(), null, "", featuremodel, system, "mandatory");
        Object edgeContext = graph.insertEdge(graph.getDefaultParent(), null, "", featuremodel, context, "optional");

        graph.getModel().endUpdate();

    }

    public void setCounter(){
        int featuremodels=0;
        graph.clearSelection();
        graph.selectAll();
        Object[] allCells = graph.getSelectionCells();
        graph.clearSelection();

        for (Object cell : allCells){
            if(((mxCell)cell).getStyle().equals("featuremodel")) featuremodels++;
        }

        featuremodelCounter=featuremodels;
    }

}
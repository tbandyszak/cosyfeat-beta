package de.cosyfeat.gui;

import com.mxgraph.io.mxCodec;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import com.mxgraph.util.png.mxPngTextDecoder;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Defines the User Interface of the Tool.
 */
public class Ui extends javax.swing.JFrame {
    JTextArea resultField;
    File fileToOpen;
    JTabbedPane tabpane;
    JPanel top;
    Model model = new Model();
    mxGraphComponent mxGraphComponent;
    ModelTranslator modelTranslator = new ModelTranslator();
    final int widthheight = 20;


    /**
     * Start building the UI.
     */
    public void startUi() {
        setTitle("CoSyFeAT");
        setIconImage(new ImageIcon(getClass().getResource("/de/cosyfeat/gui/resources/CoSyFeAT_Logo.png")).getImage());
        setResizable(true);
        setLocation(350, 350);
        setVisible(true);
        initComponents();
    }

    /**
     * Initial Components of the UI.
     */
    public void initComponents() {
        JButton newProject = new JButton("New Project");
        JButton loadProject = new JButton("Load Project");
        JButton close = new JButton("Close");
        JButton about = new JButton("About");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(350, 200);

        add(Box.createRigidArea(new Dimension(0, 10)));
        getContentPane().add(newProject);
        newProject.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createRigidArea(new Dimension(0, 10)));
        getContentPane().add(loadProject);
        loadProject.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createRigidArea(new Dimension(0, 10)));
        getContentPane().add(close);
        close.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createRigidArea(new Dimension(0, 10)));
        getContentPane().add(about);
        about.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createRigidArea(new Dimension(0, 10)));

        newProject.addActionListener(this::createNewProject);
        loadProject.addActionListener(this::loadExistingProject);
        close.addActionListener(this::closeclicked);
        about.addActionListener(this::showAbout);

    }

    /**
     * Loading an existing Project.
     *
     * @param e Event.
     */
    private void loadExistingProject(ActionEvent e) {
        createNewProject(e);
        menuopen(e);

    }

    /**
     * Creating a new Project / a new Model.
     *
     * @param e Event.
     */
    private void createNewProject(ActionEvent e) {
        getContentPane().removeAll();
        repaint();

        JSplitPane splitpanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitpanel.setDividerLocation(450);

        top = new JPanel();
        JPanel bottom = new JPanel();
        tabpane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

        JPanel result = new JPanel();
        resultField = new JTextArea();
        resultField.setEditable(false);
        JScrollPane scrollresult = new JScrollPane(resultField);
        JButton button = new JButton("check");
        button.addActionListener(this::checkButtonclicked);

        setLayout(new GridLayout());
        add(splitpanel);

        splitpanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitpanel.setDividerLocation(450);
        splitpanel.setTopComponent(tabpane);
        splitpanel.setBottomComponent(bottom);
        splitpanel.setResizeWeight(1.0);

        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        bottom.add(result);

        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        mxGraphComponent = new mxGraphComponent(model.createModel());
        addMouseHandler(mxGraphComponent);

        tabpane.addTab("New Model", mxGraphComponent);

        result.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));

        result.add(scrollresult);
        result.add(button);
        pack();

        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        JMenu menuEdit = new JMenu("Edit");
        JMenu menuHelp = new JMenu("Help");
        JMenuItem menuItemFileOpen = new JMenuItem("Open");
        ImageIcon open = new ImageIcon(getClass().getResource("/de/cosyfeat/gui/resources/baseline_folder_open_black_48dp.png"));
        open.setImage(open.getImage().getScaledInstance(widthheight,widthheight,Image.SCALE_SMOOTH));
        menuItemFileOpen.setIcon(open);
        JMenuItem menuItemFileSave = new JMenuItem("Save");
        ImageIcon save = new ImageIcon(getClass().getResource("/de/cosyfeat/gui/resources/baseline_save_alt_black_48dp.png"));
        save.setImage(save.getImage().getScaledInstance(widthheight,widthheight,Image.SCALE_SMOOTH));
        menuItemFileSave.setIcon(save);
        JMenuItem menuItemFileExit = new JMenuItem("Exit");
        //ImageIcon exit = new ImageIcon("src/de/cosyfeat/gui/resources/baseline_close_black_48dp.png");
       // exit.setImage(exit.getImage().getScaledInstance(widthheight,widthheight,Image.SCALE_SMOOTH));
        //menuItemFileExit.setIcon(exit);


       // JMenuItem menuItemAddModel = new JMenuItem("Add new Model");

        JMenuItem menuUndo = new JMenuItem("Undo");
        ImageIcon undo = new ImageIcon(getClass().getResource("/de/cosyfeat/gui/resources/baseline_undo_black_48dp.png"));
        undo.setImage(undo.getImage().getScaledInstance(widthheight,widthheight,Image.SCALE_SMOOTH));
        menuUndo.setIcon(undo);
        JMenuItem menuRedo = new JMenuItem("Redo");
        ImageIcon redo = new ImageIcon(getClass().getResource("/de/cosyfeat/gui/resources/baseline_redo_black_48dp.png"));
        redo.setImage(redo.getImage().getScaledInstance(widthheight,widthheight,Image.SCALE_SMOOTH));
        menuRedo.setIcon(redo);
        JMenuItem menuItemHelpAbout = new JMenuItem("About");

        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuHelp);
        menuFile.add(menuItemFileOpen).addActionListener(this::menuopen);
        menuFile.add(menuItemFileSave).addActionListener(this::menusave);
        menuFile.addSeparator();
        menuFile.add(menuItemFileExit).addActionListener(this::menuexit);
        menuEdit.add(menuUndo).addActionListener(this::undo);
        menuEdit.add(menuRedo).addActionListener(this::redo);
        menuHelp.add(menuItemHelpAbout).addActionListener(this::showPopUpAbout);
       // menuEdit.add(menuItemAddModel).addActionListener(this::addModel);

        setTitle("New Project");
        setSize(1000, 600);
        setLocation(100, 100);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setJMenuBar(menuBar);
        setVisible(true);
    }

    private void undo(ActionEvent actionEvent) {
        model.getUndoManager().undo();
    }

    private void redo(ActionEvent actionEvent) {
        model.getUndoManager().redo();
    }

    private void showPopUpAbout(ActionEvent actionEvent) {
        JOptionPane.showMessageDialog(this,
                readInFile());
    }

    /**
     * Handler for Mouse-Events (Mouse released).
     *
     * @param mxGraphComponent Graph Component.
     */
    private void addMouseHandler(mxGraphComponent mxGraphComponent) {

        mxGraphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {

                ImageIcon add = new ImageIcon(getClass().getResource("/de/cosyfeat/gui/resources/baseline_add_black_48dp.png"));
                add.setImage(add.getImage().getScaledInstance(widthheight,widthheight, Image.SCALE_SMOOTH));
                ImageIcon textfield = new ImageIcon(getClass().getResource("/de/cosyfeat/gui/resources/baseline_text_fields_black_48dp.png"));
                textfield.setImage(textfield.getImage().getScaledInstance(widthheight,widthheight,Image.SCALE_SMOOTH));
                ImageIcon remove = new ImageIcon(getClass().getResource("/de/cosyfeat/gui/resources/baseline_remove_black_48dp.png"));
                remove.setImage(remove.getImage().getScaledInstance(widthheight,widthheight,Image.SCALE_SMOOTH));
                ImageIcon change = new ImageIcon(getClass().getResource("/de/cosyfeat/gui/resources/baseline_autorenew_black_48dp.png"));
                change.setImage(change.getImage().getScaledInstance(widthheight,widthheight,Image.SCALE_SMOOTH));


                if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1) {
                    Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                            mxGraphComponent);
                    Object cell = mxGraphComponent.getCellAt(e.getX(), e.getY(), false);

                    if (cell != null && !model.isContext(cell) && model.isVertex(cell) && !model.isFeaturemodelRoot(cell)) {
                        JMenuItem menuItemChangeSystemName = new JMenuItem("Change Name");
                        menuItemChangeSystemName.setIcon(textfield);
                        menuItemChangeSystemName.addActionListener(e1 -> model.changeVertexName(cell));

                        JMenuItem menuItemVertex = new JMenuItem("Add Feature");
                        menuItemVertex.setIcon(add);
                        menuItemVertex.addActionListener(e1 -> model.addVertex(cell));

                        JMenuItem menuItemDelete = new JMenuItem("Delete Feature");
                        menuItemDelete.setIcon(remove);
                        menuItemDelete.addActionListener(e1 -> model.deleteCell(cell));

                        JPopupMenu menu = new JPopupMenu();
                        menu.add(menuItemChangeSystemName);
                        menu.add(menuItemVertex);
                        if (!model.isGroupmodelroot(cell)) {
                            menu.addSeparator();
                            menu.add(menuItemDelete);
                        }

                        menu.show(mxGraphComponent, pt.x, pt.y);
                    } else if (cell != null && model.isContext(cell)) {
                        JMenuItem menuItemVertex = new JMenuItem("Add Feature");
                        menuItemVertex.setIcon(add);

                        menuItemVertex.addActionListener(e1 -> model.addVertex(cell));
                        JMenuItem menuItemDelete = new JMenuItem("Delete Feature");
                        menuItemDelete.setIcon(remove);
                        menuItemDelete.addActionListener(e1 -> model.deleteCell(cell));


                        JPopupMenu menu = new JPopupMenu();
                        menu.add(menuItemVertex);
                        menu.addSeparator();
                        menu.add(menuItemDelete);

                        menu.show(mxGraphComponent, pt.x, pt.y);
                    } else if (cell != null && !model.isVertex(cell) && !model.isContextSystemEdge(cell)) {
                        JMenu menuItemChangeStyle = new JMenu("Change Edge");
                        menuItemChangeStyle.setIcon(change);
                        JMenuItem menuItemANDM = new JMenuItem("AND-Mandatory");
                        JMenuItem menuItemANDO = new JMenuItem("AND-Optional");
                        JMenuItem menuItemOR = new JMenuItem("OR");
                        JMenuItem menuItemXOR = new JMenuItem("XOR");
                        JMenuItem menuItemRequires = new JMenuItem("Requires");
                        JMenuItem menuItemExclude = new JMenuItem("Exclude");
                        JMenuItem menuItemDelete = new JMenuItem("Delete Edge");
                        menuItemDelete.setIcon(remove);
                        menuItemDelete.addActionListener(e1 -> model.deleteCell(cell));
                        menuItemChangeStyle.add(menuItemANDM).addActionListener(e1 -> model.changeEdge(cell, Model.edgeStyle.MANDATORY));
                        menuItemChangeStyle.add(menuItemANDO).addActionListener(e1 -> model.changeEdge(cell, Model.edgeStyle.OPTIONAL));
                        menuItemChangeStyle.add(menuItemOR).addActionListener(e1 -> model.changeEdge(cell, Model.edgeStyle.OR));
                        menuItemChangeStyle.add(menuItemXOR).addActionListener(e1 -> model.changeEdge(cell, Model.edgeStyle.XOR));
                        menuItemChangeStyle.add(menuItemRequires).addActionListener(e1 -> model.changeEdge(cell, Model.edgeStyle.REQUIRES));
                        menuItemChangeStyle.add(menuItemExclude).addActionListener(e1 -> model.changeEdge(cell, Model.edgeStyle.EXCLUDE));

                        JPopupMenu menu = new JPopupMenu();

                        menu.add(menuItemChangeStyle);
                        menu.addSeparator();
                        menu.add(menuItemDelete);
                        menu.show(mxGraphComponent, pt.x, pt.y);
                    } else if (cell != null && model.isFeaturemodelRoot(cell) && !model.isContext(cell)) {
                        JMenuItem menuItemInstances = new JMenuItem("Number of Instances");
                        menuItemInstances.setIcon(change);
                        menuItemInstances.addActionListener(e1 -> model.changeInstances(cell));

                        JMenuItem menuItemDelete = new JMenuItem("Delete Featuremodel");
                        menuItemDelete.setIcon(remove);
                        menuItemDelete.addActionListener(e1 -> model.deleteCell(cell));

                        JPopupMenu menu = new JPopupMenu();
                        menu.add(menuItemInstances);
                        menu.addSeparator();
                        menu.add(menuItemDelete);

                        menu.show(mxGraphComponent, pt.x, pt.y);
                    } else if (cell == null) {
                        JMenuItem menuItemAddFeaturemodel = new JMenuItem("Add Featuremodel");
                        menuItemAddFeaturemodel.setIcon(add);

                        menuItemAddFeaturemodel.addActionListener(e1 -> model.addNewFeaturemodel(pt.x, pt.y));

                        JPopupMenu menu = new JPopupMenu();
                        menu.add(menuItemAddFeaturemodel);

                        menu.show(mxGraphComponent, pt.x, pt.y);
                    }
                    e.consume();
                }
            }

        });

    }

    /**
     * Adds a new Featuremodel to the current graph.
     *
     * @param actionEvent Event.
     */
    private void addModel(ActionEvent actionEvent) {
        mxGraphComponent = new mxGraphComponent(new Model().createModel());
        addMouseHandler(mxGraphComponent);
        tabpane.addTab("New Model", mxGraphComponent);
    }

    /**
     * Start model checking the current graph.
     *
     * @param actionEvent Event.
     */
    private void checkButtonclicked(ActionEvent actionEvent) {
        String result = "";
        resultField.setText("Start...");
        modelTranslator.startTranslating(mxGraphComponent.getGraph());

        result = modelTranslator.getResult();

        for (int i = 200; i < result.length(); i = i + 200) {
            result = result.substring(0, i) + System.getProperty("line.separator") + result.substring(i);
        }

        resultField.setText(result);
    }

    /**
     * Saves the current graph as PNG (XML).
     *
     * @param e Event.
     */
    private void menusave(java.awt.event.ActionEvent e) {
        JFileChooser j = new JFileChooser("f:");

        int r = j.showSaveDialog(null);

        if (r == JFileChooser.APPROVE_OPTION) {

            String filename = j.getSelectedFile().getAbsolutePath();

            try {

                //Color bg = null; TODO: vielleicht nach transparenten hintergrund fragen?

                BufferedImage image = mxCellRenderer.createBufferedImage(model.graph, null, 1, Color.WHITE, mxGraphComponent.isAntiAlias(), null, mxGraphComponent.getCanvas());

                mxCodec codec = new mxCodec();
                String xml = URLEncoder.encode(mxXmlUtils.getXml(codec.encode(mxGraphComponent.getGraph().getModel())), StandardCharsets.UTF_8);
                mxPngEncodeParam param = mxPngEncodeParam.getDefaultEncodeParam(image);
                param.setCompressedText(new String[]{"mxGraphModel", xml});

                File filenameToSave;
                if (new File(filename).exists() && JOptionPane.showConfirmDialog(mxGraphComponent,
                        "Do you want to overwrite?") == JOptionPane.YES_OPTION) {
                    filenameToSave = new File(filename);
                } else filenameToSave = new File(filename + ".png");


                try (FileOutputStream outputStream = new FileOutputStream(filenameToSave)) {
                    mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream, param);
                    encoder.encode(image);
                }

            } catch (Exception evt) {
                JOptionPane.showMessageDialog(this, evt.getMessage());
            }
        } else JOptionPane.showMessageDialog(this, "The operation has cancelled!");

    }

    /**
     * Open an existing PNG (XML) File.
     *
     * @param e Event.
     */
    private void menuopen(java.awt.event.ActionEvent e) {
        JFileChooser j = new JFileChooser("f:");

        int r = j.showOpenDialog(null);

        if (r == JFileChooser.APPROVE_OPTION) {
            fileToOpen = new File(j.getSelectedFile().getAbsolutePath());

            setTitle(fileToOpen.getName());
            tabpane.setTitleAt(tabpane.getSelectedIndex(), fileToOpen.getName());

            try {

                Map<String, String> text = mxPngTextDecoder.decodeCompressedText(new FileInputStream(fileToOpen));

                if (text != null) {
                    String value = text.get("mxGraphModel");

                    if (value != null) {
                        Document document = mxXmlUtils.parseXml(URLDecoder.decode(
                                value, StandardCharsets.UTF_8));
                        mxCodec codec = new mxCodec(document);
                        codec.decode(document.getDocumentElement(), mxGraphComponent.getGraph().getModel());
                        model.setCounter();
                        return;
                    }
                }

                JOptionPane.showMessageDialog(mxGraphComponent,
                        mxResources.get("imageContainsNoDiagramData"));


            } catch (Exception evt) {
                JOptionPane.showMessageDialog(this, evt.getMessage());
            }
        } else
            JOptionPane.showMessageDialog(this, "The operation has cancelled!");

    }

    /**
     * Close the UI.
     *
     * @param e Event.
     */
    private void menuexit(java.awt.event.ActionEvent e) {
        System.exit(0);
    }

    private void closeclicked(java.awt.event.ActionEvent e) {
        System.exit(0);
    }

    /**
     * Shows information about the Tool.
     *
     * @param e Event.
     */
    private void showAbout(ActionEvent e) {
        getContentPane().removeAll();
        repaint();

        String text = readInFile();

        JEditorPane editorPane = new JEditorPane();
        editorPane.setText(text);
        editorPane.setCaretPosition(0);
        editorPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(editorPane);

        getContentPane().add(scrollPane);

        JMenuBar menuBar = new JMenuBar();
        JMenuItem back = new JMenuItem("Back");

        menuBar.add(back);
        back.addActionListener(this::menuBack);;


        setTitle("About");
        setSize(500, 200);
        setJMenuBar(menuBar);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setVisible(true);

    }

    /**
     * Back to the initial UI.
     *
     * @param actionEvent ActionEvent
     */
    private void menuBack(ActionEvent actionEvent) {
        getContentPane().removeAll();
        repaint();
        getJMenuBar().removeAll();
        startUi();
    }

    /**
     * Reads in the Text for the information.
     *
     * @return The Text.
     */
    private String readInFile() {
        StringBuffer sb = new StringBuffer();
        try {
            InputStream is = getClass().getResourceAsStream("/de/cosyfeat/gui/about.txt");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
                sb.append(System.getProperty("line.separator"));
            }
            br.close();
            isr.close();
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();

    }


}

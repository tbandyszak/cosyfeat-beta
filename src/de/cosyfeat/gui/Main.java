package de.cosyfeat.gui;

import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            mxSwingConstants.DEFAULT_VALID_COLOR = Color.DARK_GRAY;
            mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
            mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";


        } catch (UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Ui ui = new Ui();
        ui.startUi();

    }
}

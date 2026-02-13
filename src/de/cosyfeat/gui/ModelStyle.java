package de.cosyfeat.gui;

import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;

import java.awt.*;
import java.util.Hashtable;
import java.util.Map;

/**
 * Defines the Style of graph components (Vertex/Edge).
 */
public class ModelStyle {

    /**
     * Style for a Featuremodel.
     *
     * @return Style.
     */
    public Map<String, Object> createFeaturemodelStyle() {
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.LIGHT_GRAY));
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_STROKEWIDTH, 1.0);
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_RECTANGLE);
        style.put(mxConstants.STYLE_EDGE, true);
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
        return style;
    }

    /**
     * Style for a Groupmodel.
     *
     * @return Style.
     */
    public Map<String, Object> createGroupmodelStyle() {
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.GRAY));
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_STROKEWIDTH, 1.0);
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_RECTANGLE);
        style.put(mxConstants.STYLE_EDGE, true);
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
        return style;
    }

    /**
     * Style for a Feature.
     *
     * @return Style.
     */
    public Map<String, Object> createFeatureStyle() {
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE));
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_STROKEWIDTH, 1.0);
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_RECTANGLE);
        style.put(mxConstants.STYLE_EDGE, true);
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
        return style;
    }

    /**
     * Style for a Excludes-Edge.
     *
     * @return Style.
     */
    public Map<String, Object> createExcludesStyle() {
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        style.put(mxConstants.STYLE_STARTARROW, mxConstants.ARROW_CLASSIC);
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_ROUNDED, true);
        style.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ENTITY_RELATION);
        return style;
    }

    /**
     * Style for a Requires-Edge.
     *
     * @return Style.
     */
    public Map<String, Object> createRequiresStyle() {
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        style.put(mxConstants.STYLE_DASHED, true);
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_ROUNDED, true);
        style.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ENTITY_RELATION);
        return style;
    }

    public Map<String, Object> createMandatoryStyle() {
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OVAL);
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
        return style;
    }

    public Map<String, Object> createOptionalStyle() {
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OVAL);
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_ENDFILL, 0);
        return style;
    }

    public Map<String, Object> createNormalStyle() {
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_ENDARROW, false);
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_FONTSIZE, 12);
        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
        return style;
    }

}

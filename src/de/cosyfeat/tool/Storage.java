package de.cosyfeat.tool;

import java.util.ArrayList;

/**
 * Storage for the imported or created feature models and the constraints. Will be used later for the ModelChecker.
 *
 * @see ModelChecker
 */

public class Storage {

    public ArrayList<Featuremodel> featuremodels = new ArrayList<>();
    public ArrayList<Constraint> constraints = new ArrayList<>();


    /**
     * Finds a feature model based on the ID and returns the found feature model.
     *
     * @param id ID of the feature model which should be searched.
     * @return Feature model or Null if the feature model was not found.
     */
    public Featuremodel getFeaturemodelbyID(int id) {
        for (Featuremodel featuremodel : featuremodels) {
            if (featuremodel.modelid == id) return featuremodel;
        }
        return null;
    }

    /**
     * Finds a feature model based on the Name and returns the found feature model.
     *
     * @param name Name of the feature model which should be searched.
     * @return Feature model or Null if the feature model was not found.
     */
    public Featuremodel getFeaturemodelbyName(String name) {
        for (Featuremodel featuremodel : featuremodels) {
            if (featuremodel.name.equals(name)) return featuremodel;
        }
        return null;
    }

}

package org.vcell;

import net.imagej.Dataset;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevingaffney on 6/27/17.
 */
public class Project {

    private String title;

    /** Experimental data */
    private List<Dataset> data;

    /** Geometry definitions */
    private List<Dataset> geometry;
    
    /** Virtual Cell models */
    private List<VCellModel> models;

    /** Results from Virtual Cell */
    private List<Dataset> results;

    public Project(String title) {
        this.title = title;
        data = new ArrayList<>();
        geometry = new ArrayList<>();
        models = new ArrayList<>();
        results = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Dataset> getData() {
        return data;
    }

    public List<Dataset> getGeometry() {
        return geometry;
    }
    
    public List<VCellModel> getModels() {
    	return models;
    }

    public List<Dataset> getResults() {
        return results;
    }
}

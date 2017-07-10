package org.vcell;

import net.imagej.Dataset;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

/**
 * Created by kevingaffney on 6/27/17.
 */
public class Project {

    private String title;

    /** Experimental data */
    private ArrayList<Dataset> data;

    /** Geometry definitions */
    private ArrayList<Dataset> geometry;

    /** Results from Virtual Cell */
    private ArrayList<Dataset> results;

    public Project(String title) {
        this.title = title;
        this.data = new ArrayList<>();
        this.geometry = new ArrayList<>();
        this.results = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DefaultMutableTreeNode getTree() {

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(title);

        DefaultMutableTreeNode dataNode = new DefaultMutableTreeNode("Data");
        for (Dataset dataset : data) {
            dataNode.add(new DefaultMutableTreeNode(dataset));
        }
        root.add(dataNode);

        DefaultMutableTreeNode geometryNode = new DefaultMutableTreeNode("Geometry");
        for (Dataset dataset : geometry) {
            geometryNode.add(new DefaultMutableTreeNode(dataset));
        }
        root.add(geometryNode);

        DefaultMutableTreeNode resultsNode = new DefaultMutableTreeNode("VCell Results");
        for (Dataset dataset : results) {
            resultsNode.add(new DefaultMutableTreeNode(dataset));
        }
        root.add(resultsNode);

        return root;
    }

    public ArrayList<Dataset> getData() {
        return data;
    }

    public ArrayList<Dataset> getGeometry() {
        return geometry;
    }

    public ArrayList<Dataset> getResults() {
        return results;
    }
}

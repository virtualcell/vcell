package org.vcell;

import net.imagej.Dataset;
import net.imagej.DefaultDataset;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by kevingaffney on 6/27/17.
 */
public class VCellProject {

    private String title;
    private ArrayList<Dataset> experimentalDatasets;
    private ArrayList<Dataset> geometryDefinitions;
    private ArrayList<Dataset> vCellResults;

    public VCellProject(String title) {
        this.title = title;
        this.experimentalDatasets = new ArrayList<>();
        this.geometryDefinitions = new ArrayList<>();
        this.vCellResults = new ArrayList<>();
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
        for (Dataset dataset : experimentalDatasets) {
            dataNode.add(new DefaultMutableTreeNode(dataset));
        }
        root.add(dataNode);

        DefaultMutableTreeNode geometryNode = new DefaultMutableTreeNode("Geometry");
        for (Dataset dataset : geometryDefinitions) {
            geometryNode.add(new DefaultMutableTreeNode(dataset));
        }
        root.add(geometryNode);

        DefaultMutableTreeNode resultsNode = new DefaultMutableTreeNode("VCell Results");
        for (Dataset dataset : vCellResults) {
            resultsNode.add(new DefaultMutableTreeNode(dataset));
        }
        root.add(resultsNode);

        return root;
    }

    public ArrayList<Dataset> getExperimentalDatasets() {
        return experimentalDatasets;
    }

    public ArrayList<Dataset> getGeometryDefinitions() {
        return geometryDefinitions;
    }

    public ArrayList<Dataset> getvCellResults() {
        return vCellResults;
    }
}

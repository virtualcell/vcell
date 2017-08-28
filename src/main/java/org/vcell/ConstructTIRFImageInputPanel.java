package org.vcell;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.imagej.Dataset;

/**
 * Created by kevingaffney on 7/6/17.
 */
public class ConstructTIRFImageInputPanel extends TIRFInputPanel {

    private JComboBox<Dataset> cmbData;
    private JComboBox<Dataset> cmbMembraneResults;
    private JComboBox<Dataset> cmbVolumeResults;

    public ConstructTIRFImageInputPanel(Dataset[] geometry, Dataset[] results) {
        super();

        cmbData = new JComboBox<>(geometry);
        cmbMembraneResults = new JComboBox<>(results);
        cmbVolumeResults = new JComboBox<>(results);

        add(new JLabel("Geometry"));
        add(cmbData);
        add(new JLabel("Membrane Results"));
        add(cmbMembraneResults);
        add(new JLabel("Volume Results"));
        add(cmbVolumeResults);
    }

    public Dataset getGeometry() {
        return (Dataset) cmbData.getSelectedItem();
    }

    public Dataset getMembraneResults() {
        return (Dataset) cmbMembraneResults.getSelectedItem();
    }

    public Dataset getVolumeResults() {
        return (Dataset) cmbVolumeResults.getSelectedItem();
    }
}

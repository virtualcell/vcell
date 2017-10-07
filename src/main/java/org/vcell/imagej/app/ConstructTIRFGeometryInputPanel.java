package org.vcell.imagej.app;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import net.imagej.Dataset;

/**
 * Created by kevingaffney on 7/6/17.
 */
public class ConstructTIRFGeometryInputPanel extends TIRFInputPanel {

    private JSpinner spnSliceIndex;
    private JComboBox<Dataset> cmbData;

    public ConstructTIRFGeometryInputPanel(Dataset[] data) {
        super();

        spnSliceIndex = new JSpinner(new SpinnerNumberModel());
        cmbData = new JComboBox<>(data);

        add(new JLabel("Slice Index"));
        add(spnSliceIndex);
        add(new JLabel("Data"));
        add(cmbData);
    }

    public Integer getSliceIndex() {
        return (Integer) spnSliceIndex.getValue();
    }

    public Dataset getData() {
        return (Dataset) cmbData.getSelectedItem();
    }
}

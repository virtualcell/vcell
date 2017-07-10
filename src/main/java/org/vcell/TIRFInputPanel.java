package org.vcell;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kevingaffney on 7/6/17.
 */
abstract class TIRFInputPanel extends JPanel {

    private JSpinner spnWavelength;
    private JSpinner spnAngle;
    private JSpinner spnXSpacing;
    private JSpinner spnYSpacing;
    private JSpinner spnZSpacing;

    public TIRFInputPanel() {

        super(new GridLayout(0, 2, 10, 10));

        spnWavelength = new JSpinner(new SpinnerNumberModel());
        spnWavelength.setValue(530);
        spnAngle = new JSpinner(new SpinnerNumberModel());
        spnAngle.setValue(70);
        spnXSpacing = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000.0, 0.01));
        spnXSpacing.setValue(0.2654);
        spnYSpacing = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000.0, 0.01));
        spnYSpacing.setValue(0.2654);
        spnZSpacing = new JSpinner(new SpinnerNumberModel());
        spnZSpacing.setValue(10);

        add(new JLabel("Wavelength (nm)"));
        add(spnWavelength);
        add(new JLabel("Angle of incidence (˚)"));
        add(spnAngle);
        add(new JLabel("X-spacing (µm/pixel)"));
        add(spnXSpacing);
        add(new JLabel("Y-spacing (µm/pixel)"));
        add(spnYSpacing);
        add(new JLabel("Z-spacing (nm/slice)"));
        add(spnZSpacing);
    }

    public Integer getWavelength() {
        return (Integer) spnWavelength.getValue();
    }

    public Integer getAngle() {
        return (Integer) spnAngle.getValue();
    }

    public Double getXSpacing() {
        return (Double) spnXSpacing.getValue();
    }

    public Double getYSpacing() {
        return (Double) spnYSpacing.getValue();
    }

    public Integer getZSpacing() {
        return (Integer) spnZSpacing.getValue();
    }
}

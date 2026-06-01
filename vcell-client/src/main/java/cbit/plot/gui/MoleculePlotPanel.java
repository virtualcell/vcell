package cbit.plot.gui;

import cbit.vcell.solver.ode.gui.ClusterSpecificationPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MoleculePlotPanel extends AbstractPlotPanel {

    private static final Logger lg = LogManager.getLogger(MoleculePlotPanel.class);

    private JDialog optionsDialog = null;

    public MoleculePlotPanel() {
        super();
        // No additional initialization.
        // All rendering, listeners, scaling, and crosshair logic live in AbstractPlotPanel.
    }

    // If molecule-specific helpers are ever needed, they go here.
    // For now, MoleculePlotPanel is intentionally empty.
    @Override
    void showOptionsDialog() {
        if (optionsDialog != null) {
            optionsDialog.toFront();
            return;
        }
        JPanel panel = new PlotOptionsPanel(this);   // LINE, LOG, whatever your enum defines
        createAndShowDialog(panel);
    }

    @Override
    boolean isBubblePlotMode() {
        return false;  // MoleculePlotPanel does not support bubble plot mode.
    }


}

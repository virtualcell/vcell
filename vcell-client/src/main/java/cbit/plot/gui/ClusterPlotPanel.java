package cbit.plot.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.Border;

import cbit.vcell.solver.ode.gui.ClusterSpecificationPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.gui.GeneralGuiUtils;
import org.vcell.util.*;

public class ClusterPlotPanel extends AbstractPlotPanel {

    private static final Logger lg = LogManager.getLogger(ClusterPlotPanel.class);

    ClusterSpecificationPanel.ClusterSelection clusterSelection = null;

    public ClusterPlotPanel() {
        super();
        // No additional initialization.
        // All rendering, listeners, scaling, and crosshair logic live in AbstractPlotPanel.
    }

    // If cluster-specific helpers are ever needed, they go here.
    // For now, ClusterPlotPanel is intentionally empty.
    public void setClusterSelection(ClusterSpecificationPanel.ClusterSelection clusterSelection) {
        this.clusterSelection = clusterSelection;
    }

    public double getMaxClusterOverall() {
        if(clusterSelection == null) {
            lg.warn("Cannot open options dialog: clusterSelection is null");
            return 1.0;
        }
        if(clusterSelection.mode == ClusterSpecificationPanel.DisplayMode.COUNTS) {
            return clusterSelection.resultSet.getMaxClusterCountOverall();
        } else if(clusterSelection.mode == ClusterSpecificationPanel.DisplayMode.MASS) {
            return clusterSelection.resultSet.getMaxClusterMassOverall();
        } else {
            lg.warn("Unknown display mode: " + clusterSelection.mode);
            return 1.0;
        }
    }

    @Override
    void showOptionsDialog() {
        if(clusterSelection == null) {
            lg.warn("Cannot open options dialog: clusterSelection is null");
            return;
        }
        if (optionsDialog != null) {
            optionsDialog.toFront();
            return;
        }
        // --- choose correct panel based on plot style ---
        JPanel panel;
        if (clusterSelection.plotStyle == ClusterSpecificationPanel.PlotStyle.BUBBLE) {
            panel = new BubbleOptionsPanel(this);
        } else {
            panel = new PlotOptionsPanel(this);   // LINE, LOG, whatever your enum defines
        }
        createAndShowDialog(panel);
    }

}

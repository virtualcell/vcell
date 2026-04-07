package cbit.plot.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

import org.vcell.util.gui.GeneralGuiUtils;
import org.vcell.util.*;

public class ClusterPlotPanel extends AbstractPlotPanel {

    public ClusterPlotPanel() {
        super();
        // No additional initialization.
        // All rendering, listeners, scaling, and crosshair logic live in AbstractPlotPanel.
    }

    // If cluster-specific helpers are ever needed, they go here.
    // For now, ClusterPlotPanel is intentionally empty.
}

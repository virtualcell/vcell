package cbit.plot.gui;

import javax.swing.*;
import java.awt.*;

public class PlotOptionsPanel extends JPanel {

    public PlotOptionsPanel(AbstractPlotPanel plot) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // --- Show nodes ---
        JCheckBox showNodes = new JCheckBox("Show nodes", plot.getShowNodes());
        gbc.gridx = 0; gbc.gridy = row;
        gbc.insets = new Insets(4, 6, 2, 0);
        add(showNodes, gbc);

        // --- Snap to nodes (same row, to the right) ---
        JCheckBox snapToNodes = new JCheckBox("Snap to nodes", plot.isSnapToNodes());
        gbc.gridx = 1; gbc.gridy = row;
        gbc.insets = new Insets(4, 0, 2, 4);
        add(snapToNodes, gbc);
        snapToNodes.addActionListener(e -> plot.setSnapToNodes(snapToNodes.isSelected()));

        // --- Show lines (next row) ---
        JCheckBox showLines = new JCheckBox("Show lines", plot.getShowLines());
        gbc.insets = new Insets(4, 6, 2, 0);
        gbc.gridx = 0; gbc.gridy = ++row;
        add(showLines, gbc);

        // --- Mutual exclusion logic ---
        Runnable enforceAtLeastOne = () -> {
            if (!showNodes.isSelected() && !showLines.isSelected()) {
                // Re-enable the one that was just turned off
                // Prefer re-enabling showLines (arbitrary but consistent)
                showLines.setSelected(true);
                plot.setShowLines(true);
            }
        };

        showNodes.addActionListener(e -> {
            plot.setShowNodes(showNodes.isSelected());
            enforceAtLeastOne.run();
        });

        showLines.addActionListener(e -> {
            plot.setShowLines(showLines.isSelected());
            enforceAtLeastOne.run();
        });

        // --- Separator ---
        gbc.gridy = ++row;
        add(Box.createVerticalStrut(10), gbc);

        // --- Step for series ---
        JCheckBox showAvgAsStep = new JCheckBox("Step for series", plot.isShowAvgAsStep());
        gbc.gridy = ++row;
        add(showAvgAsStep, gbc);
        showAvgAsStep.addActionListener(e -> plot.setShowAvgAsStep(showAvgAsStep.isSelected()));

        // --- Step for bands ---
        JCheckBox showBandAsStep = new JCheckBox("Step for bands", plot.isShowBandAsStep());
        gbc.gridy = ++row;
        add(showBandAsStep, gbc);
        showBandAsStep.addActionListener(e -> plot.setShowBandAsStep(showBandAsStep.isSelected()));

        // --- Separator ---
        gbc.gridy = ++row;
        add(Box.createVerticalStrut(10), gbc);

        // --- Crosshair enabled ---
        JCheckBox showCrosshair = new JCheckBox("Crosshair enabled", plot.isShowCosshair());
        gbc.gridy = ++row;
        add(showCrosshair, gbc);
        showCrosshair.addActionListener(e -> plot.setShowCosshair(showCrosshair.isSelected()));
    }
}
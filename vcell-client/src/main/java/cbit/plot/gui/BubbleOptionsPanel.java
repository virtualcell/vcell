package cbit.plot.gui;

import javax.swing.*;
import java.awt.*;

public class BubbleOptionsPanel extends JPanel {

    public BubbleOptionsPanel(AbstractPlotPanel plot) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // --- showOnlySelectedSeries ---
        JCheckBox showOnlySelected = new JCheckBox("Only Selected", plot.isShowOnlySelectedSeries());
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(4, 6, 2, 0);
        add(showOnlySelected, gbc);
        showOnlySelected.addActionListener(e -> plot.setShowOnlySelectedSeries(showOnlySelected.isSelected()));

        // --- Snap to nodes ---
        JCheckBox snapToNodes = new JCheckBox("Snap to nodes", plot.isSnapToNodes());
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.insets = new Insets(4, 0, 2, 4);
        add(snapToNodes, gbc);
        snapToNodes.addActionListener(e -> plot.setSnapToNodes(snapToNodes.isSelected()));

        // --- Spacer ---
        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.insets = new Insets(10, 6, 2, 0);
        add(Box.createVerticalStrut(10), gbc);

        // ============================================================
        // Bubble draw mode (mutually exclusive)
        // ============================================================
        JRadioButton solid = new JRadioButton("Solid bubble");
        JRadioButton fading = new JRadioButton("Fading bubble");
        JRadioButton contour = new JRadioButton("Contour bubble");

        ButtonGroup group = new ButtonGroup();
        group.add(solid);
        group.add(fading);
        group.add(contour);

        // initial state
        solid.setSelected(plot.isShowBubbleSolid());
        fading.setSelected(plot.isShowBubbleFading());
        contour.setSelected(plot.isShowBubbleAsEmptyCircles());

        // listeners
        solid.addActionListener(e -> plot.setShowBubbleSolid(true));
        fading.addActionListener(e -> plot.setShowBubbleFading(true));
        contour.addActionListener(e -> plot.setShowBubbleAsEmptyCircles(true));

        // add to panel
        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.insets = new Insets(4, 6, 2, 0);
        add(solid, gbc);

        gbc.gridy = ++row;
        add(fading, gbc);

        gbc.gridy = ++row;
        add(contour, gbc);
    }
}

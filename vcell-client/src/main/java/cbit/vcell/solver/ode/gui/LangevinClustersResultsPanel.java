package cbit.vcell.solver.ode.gui;

import cbit.plot.gui.Plot2DDataPanel;
import cbit.plot.gui.Plot2DPanel;
import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.simdata.LangevinSolverResultSet;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.JToolBarToggleButton;
import org.vcell.util.gui.VCellIcons;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@Deprecated
public class LangevinClustersResultsPanel extends DocumentEditorSubPanel {

    private final ODEDataViewer owner;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();

    private DefaultListModel defaultListModelY = null;

    private JPanel clusterSpecificationPanel = null;
    private JPanel clusterVisualizationPanel = null;

    private CollapsiblePanel displayOptionsCollapsiblePanel = null;
    private JScrollPane jScrollPaneYAxis = null;
    private JList yAxisChoiceList = null;

    private JPanel ivjJPanel1 = null;
    private JPanel ivjJPanelPlot = null;
    private JPanel ivjPlot2DPanel1 = null;             // here
    private JLabel ivjJLabelBottom = null;
    private JPanel ivjJPanelData = null;
    private JPanel ivjPlot2DDataPanel1 = null;     // here
    private JPanel bottomRightPanel = null;
    private JPanel ivjJPanelLegend = null;
    private JScrollPane ivjPlotLegendsScrollPane = null;
    private JPanel ivjJPanelPlotLegends = null;
    private JLabel bottomLabel = null;
    private JToolBarToggleButton ivjPlotButton = null;
    private JToolBarToggleButton ivjDataButton = null;


    private enum DisplayMode { COUNTS, MEAN, OVERALL }
    LangevinSolverResultSet langevinSolverResultSet = null;
    SimulationModelInfo simulationModelInfo = null;


    class IvjEventHandler implements java.awt.event.ActionListener, PropertyChangeListener, ChangeListener, ListSelectionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("LangevinClustersResultsPanel.IvjEventHandler.actionPerformed() called");
            System.out.println("     Source: " + e.getSource() + ", Action Command: " + e.getActionCommand());
            Object source = e.getSource();
            String cmd = e.getActionCommand();

            if (cmd.equals("JPanelPlot") || cmd.equals("JPanelData")) {
                CardLayout cl = (CardLayout) ivjJPanel1.getLayout();
                cl.show(ivjJPanel1, cmd);                               // show the plot or the data panel
                ivjPlotButton.setSelected(cmd.equals("JPanelPlot"));    // update button selection state
                ivjDataButton.setSelected(cmd.equals("JPanelData"));
                ivjJPanelLegend.setVisible(cmd.equals("JPanelPlot"));   // show legend only in plot mode
                return;
            }

            if (source instanceof JRadioButton) {
                JRadioButton rb = (JRadioButton) source;
                System.out.println("     Source is JRadioButton with text: " + rb.getText());
                switch (cmd) {
                    case "COUNTS":
                        populateYAxisChoices(DisplayMode.COUNTS);
                        break;
                    case "MEAN":
                        populateYAxisChoices(DisplayMode.MEAN);
                        break;
                    case "OVERALL":
                        populateYAxisChoices(DisplayMode.OVERALL);
                        break;
                }
            }
        }
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            System.out.println("LangevinClustersResultsPanel.IvjEventHandler.propertyChange() called");
            System.out.println("     Source: " + evt.getSource() + ", Property Name: " + evt.getPropertyName() + ", Old Value: " + evt.getOldValue() + ", New Value: " + evt.getNewValue());
        }
        @Override
        public void stateChanged(ChangeEvent e) {
            System.out.println("LangevinClustersResultsPanel.IvjEventHandler.stateChanged() called");
            System.out.println("     Source: " + e.getSource() + ", Class: " + e.getSource().getClass().getName());
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            System.out.println("LangevinClustersResultsPanel.IvjEventHandler.valueChanged() called");
            System.out.println("     Source: " + e.getSource() + ", Class: " + e.getSource().getClass().getName());
            Object source = e.getSource();

        }
    };

    private void populateYAxisChoices(DisplayMode mode) {
        DefaultListModel model = getDefaultListModelY();
        model.clear();
        getYAxisChoice().setEnabled(false);

        ODESolverResultSet srs = null;
        ColumnDescription[] cd = null;

        if (langevinSolverResultSet == null) {
            model.addElement("<no data>");
            return;
        }
        switch (mode) {
            case COUNTS:
                // we may never get non-trivial clusters if there's no binding reaction
                srs = langevinSolverResultSet.getClusterCounts();
                cd = srs.getColumnDescriptions();
                break;
            case MEAN:
                srs = langevinSolverResultSet.getClusterMean();
                cd = srs.getColumnDescriptions();
                break;
            case OVERALL:
                srs = langevinSolverResultSet.getClusterOverall();
                cd = srs.getColumnDescriptions();
                break;
        }
        if(cd == null || cd.length == 1) {
            model.addElement("<no data>");  // if only "t" column is present, treat as no data
            return;
        }
        for (ColumnDescription columnDescription : cd) {
            if(columnDescription.getName().equals("t")) {
                continue; // skip time column
            }
            model.addElement(columnDescription.getName());
            getYAxisChoice().setEnabled(true);
        }
    }

    public LangevinClustersResultsPanel(ODEDataViewer odeDataViewer) {
        super();
        this.owner = odeDataViewer;
        initialize();
    }

    private void initialize() {
        System.out.println("LangevinClustersResultsPanel.initialize() called");
        setName("LangevinClustersResultsPanel");
        setLayout(new BorderLayout());
        add(getClusterSpecificationPanel(), BorderLayout.WEST);
        add(getClusterVisualizationPanel(), BorderLayout.CENTER);
    }

    private JPanel getClusterSpecificationPanel() {
        if (clusterSpecificationPanel == null) {
            clusterSpecificationPanel = new JPanel();
            clusterSpecificationPanel.setName("ClusterSpecificationPanel");
            clusterSpecificationPanel.setPreferredSize(new Dimension(213, 600));
            clusterSpecificationPanel.setLayout(new java.awt.GridBagLayout());
            clusterSpecificationPanel.setSize(248, 604);
            clusterSpecificationPanel.setMinimumSize(new java.awt.Dimension(125, 300));

            JLabel xAxisLabel = new JLabel("X Axis:");
            java.awt.GridBagConstraints constraintsXAxisLabel = new java.awt.GridBagConstraints();
            constraintsXAxisLabel.anchor = GridBagConstraints.WEST;
            constraintsXAxisLabel.gridx = 0; constraintsXAxisLabel.gridy = 0;
            constraintsXAxisLabel.insets = new Insets(4, 4, 0, 4);
            clusterSpecificationPanel.add(xAxisLabel, constraintsXAxisLabel);

            JTextField xAxisTextBox = new JTextField("t");
            xAxisTextBox.setEnabled(false);
            xAxisTextBox.setEditable(false);
            GridBagConstraints constraintsXAxisTextBox = new java.awt.GridBagConstraints();
            constraintsXAxisTextBox.gridx = 0; constraintsXAxisTextBox.gridy = 1;
            constraintsXAxisTextBox.fill = GridBagConstraints.HORIZONTAL;
            constraintsXAxisTextBox.weightx = 1.0;
            constraintsXAxisTextBox.insets = new Insets(0, 4, 4, 4);
            clusterSpecificationPanel.add(xAxisTextBox, constraintsXAxisTextBox);

            JLabel yAxisLabel = new JLabel("Y Axis:");
            GridBagConstraints gbc_YAxisLabel = new GridBagConstraints();
            gbc_YAxisLabel.anchor = GridBagConstraints.WEST;
            gbc_YAxisLabel.insets = new Insets(4, 4, 0, 4);
            gbc_YAxisLabel.gridx = 0; gbc_YAxisLabel.gridy = 2;
            clusterSpecificationPanel.add(yAxisLabel, gbc_YAxisLabel);

            GridBagConstraints gbc_panel = new GridBagConstraints();
            gbc_panel.fill = GridBagConstraints.BOTH;
            gbc_panel.insets = new Insets(4, 4, 5, 4);
            gbc_panel.gridx = 0;
            gbc_panel.gridy = 3;
            clusterSpecificationPanel.add(getDisplayOptionsPanel(), gbc_panel);

            java.awt.GridBagConstraints constraintsJScrollPaneYAxis = new java.awt.GridBagConstraints();
            constraintsJScrollPaneYAxis.gridx = 0; constraintsJScrollPaneYAxis.gridy = 4;
            constraintsJScrollPaneYAxis.fill = GridBagConstraints.BOTH;
            constraintsJScrollPaneYAxis.weightx = 1.0;
            constraintsJScrollPaneYAxis.weighty = 1.0;
            constraintsJScrollPaneYAxis.insets = new Insets(0, 4, 4, 4);
            clusterSpecificationPanel.add(getJScrollPaneYAxis(), constraintsJScrollPaneYAxis);

            initConnectionsLeft();
        }
        return clusterSpecificationPanel;
    }

    private CollapsiblePanel getDisplayOptionsPanel() {
        if(displayOptionsCollapsiblePanel == null) {
            displayOptionsCollapsiblePanel = new CollapsiblePanel("Display Options", true);
            JPanel content = displayOptionsCollapsiblePanel.getContentPanel();
            content.setLayout(new GridBagLayout());

            ButtonGroup group = new ButtonGroup();
            JRadioButton rbCounts = new JRadioButton("Cluster Counts");
            JRadioButton rbMean = new JRadioButton("Cluster Mean");
            JRadioButton rbOverall = new JRadioButton("Cluster Overall");

            rbCounts.setActionCommand("COUNTS");
            rbMean.setActionCommand("MEAN");
            rbOverall.setActionCommand("OVERALL");

            rbCounts.addActionListener(ivjEventHandler);
            rbMean.addActionListener(ivjEventHandler);
            rbOverall.addActionListener(ivjEventHandler);

            group.add(rbCounts);
            group.add(rbMean);
            group.add(rbOverall);

            rbCounts.setSelected(true);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(2, 4, 2, 4);
            gbc.gridy = 0;
            content.add(rbCounts, gbc);
            gbc.gridy = 1;
            content.add(rbMean, gbc);
            gbc.gridy = 2;
            content.add(rbOverall, gbc);
        }
        return displayOptionsCollapsiblePanel;
    }

    private JScrollPane getJScrollPaneYAxis() {
            if(jScrollPaneYAxis == null) {
                jScrollPaneYAxis = new JScrollPane();
                jScrollPaneYAxis.setName("JScrollPaneYAxis");
                jScrollPaneYAxis.setViewportView(getYAxisChoice());
            }
        return jScrollPaneYAxis;
    }
    private JList getYAxisChoice() {
        if ((yAxisChoiceList == null)) {
            yAxisChoiceList = new JList();
            yAxisChoiceList.setName("YAxisChoice");
            yAxisChoiceList.setBounds(0, 0, 160, 120);
            yAxisChoiceList.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
        return yAxisChoiceList;
    }

    private void initConnectionsLeft() {

        getDisplayOptionsPanel().addPropertyChangeListener(ivjEventHandler);
        getYAxisChoice().addListSelectionListener(ivjEventHandler);
        this.addPropertyChangeListener(ivjEventHandler);
        getYAxisChoice().setModel(getDefaultListModelY());

    }
    private javax.swing.DefaultListModel getDefaultListModelY() {
        if (defaultListModelY == null) {
            defaultListModelY = new javax.swing.DefaultListModel();
        }
        return defaultListModelY;
    }

    // ================================================================================================

    private JPanel getClusterVisualizationPanel() {
        if (clusterVisualizationPanel == null) {
            clusterVisualizationPanel = new JPanel();
            clusterVisualizationPanel.setName("ClusterVisualizationPanel");
            clusterVisualizationPanel.setPreferredSize(new java.awt.Dimension(420, 400));
            clusterVisualizationPanel.setLayout(new BorderLayout());
            clusterVisualizationPanel.setSize(513, 457);
            clusterVisualizationPanel.add(getJPanel1(), "Center");
            clusterVisualizationPanel.add(getBottomRightPanel(), "South");
            clusterVisualizationPanel.add(getJPanelLegend(), "East");
            initConnectionsRight();

        }
        return clusterVisualizationPanel;
    }

    private JPanel getJPanel1() {
        if (ivjJPanel1 == null) {
            ivjJPanel1 = new JPanel();
            ivjJPanel1.setName("JPanel1");
            ivjJPanel1.setLayout(new CardLayout());
            ivjJPanel1.add(getJPanelPlot(), getJPanelPlot().getName());
            ivjJPanel1.add(getJPanelData(), getJPanelData().getName());
        }
        return ivjJPanel1;
    }
    private javax.swing.JPanel getJPanelPlot() {
        if (ivjJPanelPlot == null) {
            ivjJPanelPlot = new javax.swing.JPanel();
            ivjJPanelPlot.setName("JPanelPlot");
            ivjJPanelPlot.setLayout(new java.awt.BorderLayout());
            ivjJPanelPlot.add(getPlot2DPanel1(), "Center");
            ivjJPanelPlot.add(getJLabelBottom(), "South");
        }
        return ivjJPanelPlot;
    }
    private javax.swing.JPanel getJPanelData() {
        if (ivjJPanelData == null) {
            ivjJPanelData = new javax.swing.JPanel();
            ivjJPanelData.setName("JPanelData");
            ivjJPanelData.setLayout(new java.awt.BorderLayout());
            ivjJPanelData.add(getPlot2DDataPanel1(), "Center");
        }
        return ivjJPanelData;
    }

    private javax.swing.JLabel getJLabelBottom() {
        if (ivjJLabelBottom == null) {
            ivjJLabelBottom = new javax.swing.JLabel();
            ivjJLabelBottom.setName("JLabelBottom");
            ivjJLabelBottom.setText("t");
            ivjJLabelBottom.setForeground(java.awt.Color.black);
            ivjJLabelBottom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            ivjJLabelBottom.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        }
        return ivjJLabelBottom;
    }
    private JPanel getPlot2DPanel1() {
        if (ivjPlot2DPanel1 == null) {
            try {
                ivjPlot2DPanel1 = new JPanel();
                ivjPlot2DPanel1.setName("Plot2DPanel1");
            } catch (java.lang.Throwable ivjExc) {
               handleException(ivjExc);
            }
        }
        return ivjPlot2DPanel1;
    }
    public JPanel getPlot2DDataPanel1() {
        if (ivjPlot2DDataPanel1 == null) {
            try {
                ivjPlot2DDataPanel1 = new JPanel();
                ivjPlot2DDataPanel1.setName("Plot2DDataPanel1");
            } catch (java.lang.Throwable ivjExc) {
               handleException(ivjExc);
            }
        }
        return ivjPlot2DDataPanel1;
    }


    // ---------------------------------------------------------------------
    private JPanel getBottomRightPanel() {
        if (bottomRightPanel == null) {
            bottomRightPanel = new JPanel();
            bottomRightPanel.setName("JPanelBottom");
            bottomRightPanel.setLayout(new GridBagLayout());

            java.awt.GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 1; gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.insets = new java.awt.Insets(4, 4, 4, 4);
            bottomRightPanel.add(getJBottomLabel(), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 2; gbc.gridy = 0;
            gbc.insets = new Insets(4, 4, 4, 4);
            bottomRightPanel.add(getPlotButton(), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 3; gbc.gridy = 0;
            gbc.insets = new Insets(4, 4, 4, 4);
            bottomRightPanel.add(getDataButton(), gbc);
        }
        return bottomRightPanel;
    }
    private JLabel getJBottomLabel() {
        if (bottomLabel == null) {
            bottomLabel = new JLabel();
            bottomLabel.setName("JBottomLabel");
            bottomLabel.setText(" ");
            bottomLabel.setForeground(Color.blue);
            bottomLabel.setPreferredSize(new Dimension(44, 20));
            bottomLabel.setFont(new Font("dialog", 0, 12));
            bottomLabel.setMinimumSize(new Dimension(44, 20));
        }
        return bottomLabel;
    }
    private JToolBarToggleButton getPlotButton() {
        if (ivjPlotButton == null) {
            ivjPlotButton = new JToolBarToggleButton();
            ivjPlotButton.setName("PlotButton");
            ivjPlotButton.setToolTipText("Show plot(s)");
            ivjPlotButton.setText("");
            ivjPlotButton.setMaximumSize(new Dimension(28, 28));
            ivjPlotButton.setActionCommand("JPanelPlot");
            ivjPlotButton.setSelected(true);
            ivjPlotButton.setPreferredSize(new Dimension(28, 28));
            ivjPlotButton.setIcon(VCellIcons.dataExporterIcon);
            ivjPlotButton.setMinimumSize(new Dimension(28, 28));
        }
        return ivjPlotButton;
    }
    private JToolBarToggleButton getDataButton() {
        if (ivjDataButton == null) {
            ivjDataButton = new JToolBarToggleButton();
            ivjDataButton.setName("DataButton");
            ivjDataButton.setToolTipText("Show data");
            ivjDataButton.setText("");
            ivjDataButton.setMaximumSize(new Dimension(28, 28));
            ivjDataButton.setActionCommand("JPanelData");
            ivjDataButton.setIcon(VCellIcons.dataSetsIcon);
            ivjDataButton.setPreferredSize(new Dimension(28, 28));
            ivjDataButton.setMinimumSize(new Dimension(28, 28));
        }
        return ivjDataButton;
    }

    private javax.swing.JPanel getJPanelLegend() {
        if (ivjJPanelLegend == null) {
            ivjJPanelLegend = new javax.swing.JPanel();
            ivjJPanelLegend.setName("JPanelLegend");
            ivjJPanelLegend.setLayout(new java.awt.BorderLayout());
            getJPanelLegend().add(new JLabel("        "), "South");
            getJPanelLegend().add(new JLabel("Plot Legend:"), "North");
            getJPanelLegend().add(getPlotLegendsScrollPane(), "Center");
        }
        return ivjJPanelLegend;
    }
    private JScrollPane getPlotLegendsScrollPane() {
        if (ivjPlotLegendsScrollPane == null) {
            ivjPlotLegendsScrollPane = new javax.swing.JScrollPane();
            ivjPlotLegendsScrollPane.setName("PlotLegendsScrollPane");
            ivjPlotLegendsScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            ivjPlotLegendsScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
            getPlotLegendsScrollPane().setViewportView(getJPanelPlotLegends());
        }
        return ivjPlotLegendsScrollPane;
    }
    private JPanel getJPanelPlotLegends() {
        if (ivjJPanelPlotLegends == null) {
            ivjJPanelPlotLegends = new javax.swing.JPanel();
            ivjJPanelPlotLegends.setName("JPanelPlotLegends");
            ivjJPanelPlotLegends.setLayout(new BoxLayout(ivjJPanelPlotLegends, javax.swing.BoxLayout.Y_AXIS));
            ivjJPanelPlotLegends.setBounds(0, 0, 72, 360);
        }
        return ivjJPanelPlotLegends;
    }


    private void initConnectionsRight() {
        // Group the two buttons so only one stays selected
        ButtonGroup bg = new ButtonGroup();
        bg.add(getPlotButton());
        bg.add(getDataButton());

        // Add the shared handler
        getPlotButton().addActionListener(ivjEventHandler);
        getDataButton().addActionListener(ivjEventHandler);
    }

    // =================================================================================================

    private void handleException(java.lang.Throwable exception) {
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    @Override
    protected void onSelectedObjectsChange(Object[] selectedObjects) {
        System.out.println("LangevinClustersResultsPanel.onSelectedObjectsChange() called with " + selectedObjects.length + " objects");
    }

    public void refreshData() {
            simulationModelInfo = owner.getSimulationModelInfo();
            langevinSolverResultSet = owner.getLangevinSolverResultSet();
        System.out.println("LangevinClustersResultsPanel.refreshData() called");

        // find the selected radio button inside the collapsible panel
        JPanel content = displayOptionsCollapsiblePanel.getContentPanel();
        for (Component c : content.getComponents()) {
            if (c instanceof JRadioButton rb && rb.isSelected()) {
                ivjEventHandler.actionPerformed(new ActionEvent(rb, ActionEvent.ACTION_PERFORMED, rb.getActionCommand()));
                break;
            }
        }

    }


}

package cbit.vcell.solver.ode.gui;

import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import org.vcell.util.gui.CollapsiblePanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LangevinClustersResultsPanel extends DocumentEditorSubPanel {

    private final ODEDataViewer owner;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();

    private DefaultListModel defaultListModelY = null;

    private JPanel clusterSpecificationPanel = null;
    private JPanel clusterVisualizationPanel = null;

    private CollapsiblePanel displayOptionsCollapsiblePanel = null;
    private JScrollPane jScrollPaneYAxis = null;
    private JList yAxisChoiceList = null;


    class IvjEventHandler implements java.awt.event.ActionListener, PropertyChangeListener, ChangeListener, ListSelectionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("LangevinClustersResultsPanel.IvjEventHandler.actionPerformed() called");
            System.out.println("     Source: " + e.getSource() + ", Action Command: " + e.getActionCommand());
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
        }
    };

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

            initConnections();
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

    private void initConnections() {

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
        }
        return clusterVisualizationPanel;
    }


    @Override
    protected void onSelectedObjectsChange(Object[] selectedObjects) {
        System.out.println("LangevinClustersResultsPanel.onSelectedObjectsChange() called with " + selectedObjects.length + " objects");
    }

    public void refreshData() {
            System.out.println("LangevinClustersResultsPanel.refreshData() called");
    }

}

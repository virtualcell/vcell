package cbit.vcell.solver.ode.gui;

import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.simdata.LangevinSolverResultSet;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.vcell.util.gui.CollapsiblePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ClusterSpecificationPanel extends DocumentEditorSubPanel {

    private enum DisplayMode { COUNTS, MEAN, OVERALL };
    public static class ClusterSelection {  // used to communicate y-list selection to the ClusterVisualizationPanel
        public final DisplayMode mode;
        public final java.util.List<ColumnDescription> columns;
        public final ODESolverResultSet resultSet;
        public ClusterSelection(DisplayMode mode, java.util.List<ColumnDescription> columns, ODESolverResultSet resultSet) {
            this.mode = mode;
            this.columns = columns;
            this.resultSet = resultSet;
        }
    }

    class IvjEventHandler implements ActionListener, PropertyChangeListener, ListSelectionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if (e.getSource() instanceof JRadioButton rb && SwingUtilities.isDescendingFrom(rb, ClusterSpecificationPanel.this)) {
                System.out.println("ClusterSpecificationPanel.actionPerformed() called. Source is JRadioButton: " + rb.getText());
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
            if(evt.getSource() == ClusterSpecificationPanel.this) {
                System.out.println("ClusterSpecificationPanel.IvjEventHandler.propertyChange() called");
            }
        }
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getSource() == ClusterSpecificationPanel.this.getYAxisChoice() && !e.getValueIsAdjusting()) {
                // extract selected ColumnDescriptions
                java.util.List<ColumnDescription> selected = (java.util.List<ColumnDescription>) yAxisChoiceList.getSelectedValuesList();
                DisplayMode mode = null;
                JPanel content = displayOptionsCollapsiblePanel.getContentPanel();
                for (Component c : content.getComponents()) {
                    if (c instanceof JRadioButton rb && rb.isSelected()) {
                        switch (rb.getActionCommand()) {
                            case "COUNTS":
                                mode = DisplayMode.COUNTS;
                                break;
                            case "MEAN":
                                mode = DisplayMode.MEAN;
                                break;
                            case "OVERALL":
                                mode = DisplayMode.OVERALL;
                                break;
                        }
                    }
                }
                ODESolverResultSet srs = null;
                switch (mode) {
                    case COUNTS:
                        srs = langevinSolverResultSet.getClusterCounts();
                        break;
                    case MEAN:
                        srs = langevinSolverResultSet.getClusterMean();
                        break;
                    case OVERALL:
                        srs = langevinSolverResultSet.getClusterOverall();
                        break;
                }
                // fire the event upward
                firePropertyChange("ClusterSelection", null, new ClusterSelection(mode, selected, srs));
            }
        }
    };

    private final ODEDataViewer owner;    LangevinSolverResultSet langevinSolverResultSet = null;
    SimulationModelInfo simulationModelInfo = null;
    ClusterSpecificationPanel.IvjEventHandler ivjEventHandler = new ClusterSpecificationPanel.IvjEventHandler();

    private CollapsiblePanel displayOptionsCollapsiblePanel = null;
    private JScrollPane jScrollPaneYAxis = null;
    private JList yAxisChoiceList = null;
    private DefaultListModel<ColumnDescription> defaultListModelY = null;


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
            model.addElement(columnDescription);
            getYAxisChoice().setEnabled(true);
        }
    }

    public ClusterSpecificationPanel(ODEDataViewer odeDataViewer) {
        super();
        this.owner = odeDataViewer;
        initialize();
    }

    private void initialize() {
        System.out.println("ClusterSpecificationPanel.initialize() called");
        setPreferredSize(new Dimension(213, 600));
        setLayout(new GridBagLayout());
        setSize(248, 604);
        setMinimumSize(new Dimension(125, 300));

        JLabel xAxisLabel = new JLabel("X Axis:");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(4, 4, 0, 4);
        add(xAxisLabel, gbc);

        JTextField xAxisTextBox = new JTextField("t");
        xAxisTextBox.setEnabled(false);
        xAxisTextBox.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 4, 4, 4);
        add(xAxisTextBox, gbc);

        JLabel yAxisLabel = new JLabel("Y Axis:");
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 0, 4);
        gbc.gridx = 0; gbc.gridy = 2;
        add(yAxisLabel, gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 5, 4);
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(getDisplayOptionsPanel(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 4, 4, 4);
        add(getJScrollPaneYAxis(), gbc);

        initConnections();
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
            yAxisChoiceList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            yAxisChoiceList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {

                    JLabel label = (JLabel) super.getListCellRendererComponent(
                            list, value, index, isSelected, cellHasFocus);

                    ColumnDescription cd = (ColumnDescription) value;
                    label.setText(cd.getName());   // later: cd.getName() + " (molecules)"

                    return label;
                }
            });
        }
        return yAxisChoiceList;
    }

    private void initConnections() {
        getDisplayOptionsPanel().addPropertyChangeListener(ivjEventHandler);
        getYAxisChoice().addListSelectionListener(ivjEventHandler);
        this.addPropertyChangeListener(ivjEventHandler);
        getYAxisChoice().setModel(getDefaultListModelY());
    }

    private DefaultListModel getDefaultListModelY() {
        if (defaultListModelY == null) {
            defaultListModelY = new DefaultListModel();
        }
        return defaultListModelY;
    }

    private void handleException(java.lang.Throwable exception) {
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    @Override
    protected void onSelectedObjectsChange(Object[] selectedObjects) {
        System.out.println("ClusterSpecificationPanel.onSelectedObjectsChange() called with " + selectedObjects.length + " objects");
    }

    public void refreshData() {
        simulationModelInfo = owner.getSimulationModelInfo();
        langevinSolverResultSet = owner.getLangevinSolverResultSet();
        System.out.println("ClusterSpecificationPanel.refreshData() called");

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

package cbit.vcell.solver.ode.gui;

import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.simdata.LangevinSolverResultSet;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.vcell.util.gui.CollapsiblePanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MoleculeSpecificationPanel extends DocumentEditorSubPanel {

    public enum DisplayMode {
        MOLECULES("MOLECULES", "Molecules", "Display molecule counts over time"),
        BOUND_SITES("BOUND_SITES", "Bound Sites", "Display number of bound sites over time"),
        FREE_SITES("FREE_SITES", "Free Sites", "Display number of free sites over time"),
        TOTAL_SITES("TOTAL_SITES", "Total Sites", "Display total number of sites over time");

        private final String actionCommand;
        private final String uiLabel;
        private final String tooltip;
        DisplayMode(String actionCommand, String uiLabel, String tooltip) {
            this.actionCommand = actionCommand;
            this.uiLabel = uiLabel;
            this.tooltip = tooltip;
        }
        public String actionCommand() { return actionCommand; }
        public String uiLabel()       { return uiLabel; }
        public String tooltip()       { return tooltip; }
        public static MoleculeSpecificationPanel.DisplayMode fromActionCommand(String cmd) {
            for (MoleculeSpecificationPanel.DisplayMode m : values()) {
                if (m.actionCommand.equals(cmd)) {
                    return m;
                }
            }
            throw new IllegalArgumentException("Unknown DisplayMode: " + cmd);
        }

    }

    public static class MoleculeSelection {  // used to communicate y-list selection to the MoleculeVisualizationPanel
        public final java.util.List<ColumnDescription> columnDescriptions;
        public final ODESolverResultSet resultSet;
        public MoleculeSelection(java.util.List<ColumnDescription> columnDescriptions, ODESolverResultSet resultSet) {
            this.columnDescriptions = columnDescriptions;
            this.resultSet = resultSet;
        }
    }

    class IvjEventHandler implements ActionListener, PropertyChangeListener, ListSelectionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JCheckBox cb && SwingUtilities.isDescendingFrom(cb, MoleculeSpecificationPanel.this)) {
                System.out.println(this.getClass().getName() + ".IvjEventHandler.actionPerformed() called with " + e.getActionCommand());
                String cmd = e.getActionCommand();
            }
        }
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() == owner.getMoleculeSpecificationPanel()) {
                System.out.println(this.getClass().getName() + ".IvjEventHandler.propertyChange() called with " + evt.getPropertyName());
            }
        }
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getSource() == MoleculeSpecificationPanel.this.getYAxisChoice() && !e.getValueIsAdjusting()) {
                System.out.println(this.getClass().getName() + ".IvjEventHandler.valueChanged() called");
            }
        }
    }
    MoleculeSpecificationPanel.IvjEventHandler ivjEventHandler = new MoleculeSpecificationPanel.IvjEventHandler();

    // these below may go to a base class
    private final ODEDataViewer owner;
    LangevinSolverResultSet langevinSolverResultSet = null;
    SimulationModelInfo simulationModelInfo = null;

    private CollapsiblePanel displayOptionsCollapsiblePanel = null;
    private JScrollPane jScrollPaneYAxis = null;
    private static final String YAxisLabelText = "Y Axis: ";
    private JLabel yAxisLabel = null;
    private JList yAxisChoiceList = null;
    private DefaultListModel<ColumnDescription> defaultListModelY = null;

    public MoleculeSpecificationPanel(ODEDataViewer owner) {
        super();
        this.owner = owner;
        initialize();
    }
    private void initialize() {
        System.out.println(this.getClass().getSimpleName() + ".initialize() called");
        // layout
        setPreferredSize(new Dimension(213, 600));
        setLayout(new GridBagLayout());
        setSize(248, 604);
        setMinimumSize(new Dimension(125, 300));

        JLabel xAxisLabel = new JLabel("<html><b>X Axis: </b></html>");     // non-breaking space is  &nbsp;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(4, 4, 0, 4);
        add(xAxisLabel, gbc);

        JTextField xAxisTextBox = new JTextField("time [seconds]");
        xAxisTextBox.setEnabled(false);
        xAxisTextBox.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 4, 4, 4);
        add(xAxisTextBox, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 0, 4);
        gbc.gridx = 0; gbc.gridy = 2;
        add(getYAxisLabel(), gbc);

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
    private void initConnections() {
        // listeners
        getDisplayOptionsPanel().addPropertyChangeListener(ivjEventHandler);
        getYAxisChoice().addListSelectionListener(ivjEventHandler);
        this.addPropertyChangeListener(ivjEventHandler);
        getYAxisChoice().setModel(getDefaultListModelY());
    }
    private DefaultListModel<ColumnDescription> getDefaultListModelY() {
        if (defaultListModelY == null) {
            defaultListModelY = new DefaultListModel<>();
        }
        return defaultListModelY;
    }

    private JLabel getYAxisLabel() {
        if (yAxisLabel == null) {
            yAxisLabel = new JLabel();
            yAxisLabel.setName("YAxisLabel");
            String text = "<html><b>" + YAxisLabelText + "</b></html>";
            yAxisLabel.setText(text);
        }
        return yAxisLabel;
    }
    private CollapsiblePanel getDisplayOptionsPanel() {
        if (displayOptionsCollapsiblePanel == null) {
            displayOptionsCollapsiblePanel = new CollapsiblePanel("Display Options", true);
            JPanel content = displayOptionsCollapsiblePanel.getContentPanel();
            content.setLayout(new GridBagLayout());


        }
        return displayOptionsCollapsiblePanel;
    }
    private JScrollPane getJScrollPaneYAxis() {
        if(jScrollPaneYAxis == null) {
            jScrollPaneYAxis = new JScrollPane();
            jScrollPaneYAxis.setName("JScrollPaneYAxis");
            jScrollPaneYAxis.setViewportView(getYAxisChoice());
            // prevent collapse when list is empty
            jScrollPaneYAxis.setMinimumSize(new Dimension(100, 120));
            jScrollPaneYAxis.setPreferredSize(new Dimension(100, 120));
        }
        return jScrollPaneYAxis;
    }





    private javax.swing.JList getYAxisChoice() {
        if ((yAxisChoiceList == null)) {
            yAxisChoiceList = new JList();
            yAxisChoiceList.setName("YAxisChoice");
            yAxisChoiceList.setBounds(0, 0, 160, 120);
            yAxisChoiceList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            yAxisChoiceList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof ColumnDescription columnDescription) {
                        label.setText(columnDescription.getName());
                    }
                    return label;
                }
            });
        }
        return yAxisChoiceList;
    }




    // -----------------------------------------------------------


    private void handleException(java.lang.Throwable exception) {
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }
    @Override
    protected void onSelectedObjectsChange(Object[] selectedObjects) {
        System.out.println(this.getClass().getSimpleName() + ".onSelectedObjectsChange() called with " + selectedObjects.length + " objects");

    }
    public void refreshData() {
        System.out.println(this.getClass().getSimpleName() + ".refreshData() called");
        simulationModelInfo = owner.getSimulationModelInfo();
        langevinSolverResultSet = owner.getLangevinSolverResultSet();

    }

}

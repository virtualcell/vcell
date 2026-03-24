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


    public static class MoleculeSelection {  // used to communicate y-list selection to the ClusterVisualizationPanel
        public final java.util.List<ColumnDescription> columns;
        public final ODESolverResultSet resultSet;
        public MoleculeSelection(java.util.List<ColumnDescription> columns, ODESolverResultSet resultSet) {
            this.columns = columns;
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
            if (evt.getSource() == owner.getClusterSpecificationPanel()) {
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

        initConnections();
    }
    private void initConnections() {
        // listeners
    }



    // -----------------------------------------------------------

    private JList getYAxisChoice() {
        return null;
    }






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

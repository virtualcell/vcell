package cbit.vcell.solver.ode.gui;

import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.LangevinSolverResultSet;
import cbit.vcell.solver.SimulationModelInfo;
import org.vcell.util.gui.SpecialtyTableRenderer;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MoleculeVisualizationPanel extends DocumentEditorSubPanel {





    private final ODEDataViewer owner;
    LangevinSolverResultSet langevinSolverResultSet = null;
    SimulationModelInfo simulationModelInfo = null;

    class IvjEventHandler implements ActionListener, PropertyChangeListener, ListSelectionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof Component c && SwingUtilities.isDescendingFrom(c, MoleculeVisualizationPanel.this)) {
                System.out.println(this.getClass().getName() + ".actionPerformed() called with " + e.getActionCommand());
                // switch selection between plot panel and data panel (located in a JCardLayout)
            }
        }
        @Override
        public void propertyChange(PropertyChangeEvent evt) {   // listens to changes in the MoleculeSpecificationPanel
            if (evt.getSource() == owner.getMoleculeSpecificationPanel() && "MoleculeSelection".equals(evt.getPropertyName())) {
                System.out.println(this.getClass().getName() + ".propertyChange() called with " + evt.getPropertyName());
                // redraw everything based on the new selections
                MoleculeSpecificationPanel.MoleculeSelection sel = (MoleculeSpecificationPanel.MoleculeSelection) evt.getNewValue();
                try {
                    redrawLegend(sel);      // redraw legend (one plot, multiple curves)
                    redrawPlot(sel);        // redraw plot (one plot, multiple curves)
                    redrawDataTable(sel);   // redraw data table
                } catch (ExpressionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getSource() instanceof Component c && SwingUtilities.isDescendingFrom(c, MoleculeVisualizationPanel.this)) {
                System.out.println(this.getClass().getName() + ".valueChanged() called");
            }
        }
    }

    public MoleculeVisualizationPanel(ODEDataViewer owner) {
        super();
        this.owner = owner;
        initialize();
    }

    private void initialize() {
        setBackground(Color.white);
        initConnections();
    }

    private void initConnections() {
        // listeners

    }




    // ----------------------------------------------------------------------


    private void redrawPlot(MoleculeSpecificationPanel.MoleculeSelection sel) throws ExpressionException {

    }
    private void redrawLegend(MoleculeSpecificationPanel.MoleculeSelection sel) throws ExpressionException {

    }
    private void redrawDataTable(MoleculeSpecificationPanel.MoleculeSelection sel) throws ExpressionException {

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
    public void setSpecialityRenderer(SpecialtyTableRenderer str) {
//        getClusterDataPanel().setSpecialityRenderer(str);
    }


}

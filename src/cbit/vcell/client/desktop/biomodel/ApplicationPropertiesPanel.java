package cbit.vcell.client.desktop.biomodel;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import cbit.vcell.desktop.BioModelCellRenderer;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public class ApplicationPropertiesPanel extends JPanel {	
	private static final String PROPERTY_NAME_SIMULATION_CONTEXT = "simulationContext";
	private JTree tree = null;
	private SimulationContext simulationContext = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	ApplicationPropertiesTreeModel treeModel = null;

	private class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ApplicationPropertiesPanel.this && evt.getPropertyName().equals(PROPERTY_NAME_SIMULATION_CONTEXT)) {
				treeModel.setSimulationContext(simulationContext);
			}
		}
	};

/**
 * BioModelTreePanel constructor comment.
 */
public ApplicationPropertiesPanel() {
	super();
	initialize();
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		addPropertyChangeListener(ivjEventHandler);
		
		tree = new JTree();
		treeModel = new ApplicationPropertiesTreeModel(tree);
		tree.setModel(treeModel);
		ToolTipManager.sharedInstance().registerComponent(tree);
		tree.setCellRenderer(new BioModelCellRenderer(null));
		
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(tree);
		add(scrollPane, BorderLayout.CENTER);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Set the BioModel to a new value.
 * @param newValue cbit.vcell.biomodel.BioModel
 */
public void setSimulationContext(SimulationContext newValue) {
	SimulationContext oldValue = simulationContext;
	simulationContext = newValue;
	firePropertyChange(PROPERTY_NAME_SIMULATION_CONTEXT, oldValue, newValue);
}

}
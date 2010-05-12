package cbit.vcell.client.desktop.simulation;
import cbit.vcell.solver.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (5/11/2004 1:28:57 PM)
 * @author: Ion Moraru
 */
public class SimulationEditor extends JPanel {
	private static final int TAB_TASK_INDEX = 2;
	private static final int TAB_MESH_INDEX = 1;
	private static final int TAB_PARAMETERS_INDEX = 0;
	private static final String TAB_ADVANCED_TITLE = "Advanced";
	private static final String TAB_TASK_TITLE = "Task";
	private static final String TAB_PARAMETERS_TITLE = "Parameters";
	private static final String TAB_MESH_TITLE = "Mesh";
	private JTabbedPane ivjJTabbedPane1 = null;
	private cbit.vcell.solver.ode.gui.MathOverridesPanel ivjMathOverridesPanel1 = null;
	private cbit.vcell.math.gui.MeshSpecificationPanel ivjMeshSpecificationPanel1 = null;
	private cbit.vcell.solver.ode.gui.SolverTaskDescriptionAdvancedPanel ivjSolverTaskDescriptionAdvancedPanel1 = null;
	private cbit.vcell.solver.ode.gui.SolverTaskDescriptionPanel ivjSolverTaskDescriptionPanel1 = null;
	private cbit.vcell.solver.Simulation fieldClonedSimulation = null;

public SimulationEditor() {
	super();
	initialize();
}

/**
 * connEtoC1:  (SimulationEditor.initialize() --> SimulationEditor.makeBold()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.makeBoldTitle();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Gets the clonedSimulation property (cbit.vcell.solver.Simulation) value.
 * @return The clonedSimulation property value.
 * @see #setClonedSimulation
 */
public cbit.vcell.solver.Simulation getClonedSimulation() {
	return fieldClonedSimulation;
}


/**
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getJTabbedPane1() {
	if (ivjJTabbedPane1 == null) {
		try {
			ivjJTabbedPane1 = new javax.swing.JTabbedPane();
			ivjJTabbedPane1.setName("JTabbedPane1");
			ivjJTabbedPane1.insertTab(TAB_PARAMETERS_TITLE, null, getMathOverridesPanel1(), null, TAB_PARAMETERS_INDEX);
			ivjJTabbedPane1.insertTab(TAB_MESH_TITLE, null, getMeshSpecificationPanel1(), null, TAB_MESH_INDEX);
			ivjJTabbedPane1.insertTab(TAB_TASK_TITLE, null, getSolverTaskDescriptionPanel1(), null, TAB_TASK_INDEX);
			ivjJTabbedPane1.insertTab(TAB_ADVANCED_TITLE, null, getSolverTaskDescriptionAdvancedPanel1(), null, 3);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane1;
}


/**
 * Return the MathOverridesPanel1 property value.
 * @return cbit.vcell.solver.ode.gui.MathOverridesPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.ode.gui.MathOverridesPanel getMathOverridesPanel1() {
	if (ivjMathOverridesPanel1 == null) {
		try {
			ivjMathOverridesPanel1 = new cbit.vcell.solver.ode.gui.MathOverridesPanel();
			ivjMathOverridesPanel1.setName("MathOverridesPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathOverridesPanel1;
}


/**
 * Return the MeshSpecificationPanel1 property value.
 * @return cbit.vcell.math.gui.MeshSpecificationPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.math.gui.MeshSpecificationPanel getMeshSpecificationPanel1() {
	if (ivjMeshSpecificationPanel1 == null) {
		try {
			ivjMeshSpecificationPanel1 = new cbit.vcell.math.gui.MeshSpecificationPanel();
			ivjMeshSpecificationPanel1.setName("MeshSpecificationPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMeshSpecificationPanel1;
}


/**
 * Return the SolverTaskDescriptionAdvancedPanel1 property value.
 * @return cbit.vcell.solver.ode.gui.SolverTaskDescriptionAdvancedPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.ode.gui.SolverTaskDescriptionAdvancedPanel getSolverTaskDescriptionAdvancedPanel1() {
	if (ivjSolverTaskDescriptionAdvancedPanel1 == null) {
		try {
			ivjSolverTaskDescriptionAdvancedPanel1 = new cbit.vcell.solver.ode.gui.SolverTaskDescriptionAdvancedPanel();
			ivjSolverTaskDescriptionAdvancedPanel1.setName("SolverTaskDescriptionAdvancedPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSolverTaskDescriptionAdvancedPanel1;
}


/**
 * Return the SolverTaskDescriptionPanel1 property value.
 * @return cbit.vcell.solver.ode.gui.SolverTaskDescriptionPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.ode.gui.SolverTaskDescriptionPanel getSolverTaskDescriptionPanel1() {
	if (ivjSolverTaskDescriptionPanel1 == null) {
		try {
			ivjSolverTaskDescriptionPanel1 = new cbit.vcell.solver.ode.gui.SolverTaskDescriptionPanel();
			ivjSolverTaskDescriptionPanel1.setName("SolverTaskDescriptionPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSolverTaskDescriptionPanel1;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SimulationEditor");
		setLayout(new java.awt.BorderLayout());
		setSize(547, 346);
		add(getJTabbedPane1(), "Center");
		initConnections();
		connEtoC1();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		JFrame frame = new javax.swing.JFrame();
		SimulationEditor aSimulationEditor;
		aSimulationEditor = new SimulationEditor();
		frame.setContentPane(aSimulationEditor);
		frame.setSize(aSimulationEditor.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
private void makeBoldTitle() {
	getJTabbedPane1().setFont(getJTabbedPane1().getFont().deriveFont(java.awt.Font.BOLD));
}


/**
 * Comment
 */
public void prepareToEdit(cbit.vcell.solver.Simulation simulation) {
	try {
		Simulation clonedSimulation = (Simulation)org.vcell.util.BeanUtils.cloneSerializable(simulation);
		clonedSimulation.refreshDependencies();
		getMathOverridesPanel1().setMathOverrides(clonedSimulation == null ? null : clonedSimulation.getMathOverrides());
		getMeshSpecificationPanel1().setMeshSpecification(clonedSimulation == null ? null : clonedSimulation.getMeshSpecification());
		getSolverTaskDescriptionPanel1().setSolverTaskDescription(clonedSimulation == null ? null : clonedSimulation.getSolverTaskDescription());
		getSolverTaskDescriptionAdvancedPanel1().setSolverTaskDescription(clonedSimulation == null ? null : clonedSimulation.getSolverTaskDescription());
		
		boolean shouldMeshBeEnabled = false;
		MeshSpecification meshSpec = clonedSimulation.getMeshSpecification();
		if(	meshSpec != null && 
			meshSpec.getGeometry() != null &&
			meshSpec.getGeometry().getDimension() > 0){
				shouldMeshBeEnabled = true;
		}
		if (!shouldMeshBeEnabled) {
			getJTabbedPane1().remove(TAB_MESH_INDEX);
		}

//		int meshTabIndex = getJTabbedPane1().indexOfTab(TAB_MESH_TITLE);
//		if(getJTabbedPane1().isEnabledAt(meshTabIndex) != shouldMeshBeEnabled){
//			if(!shouldMeshBeEnabled && getJTabbedPane1().getSelectedIndex() == meshTabIndex){
//				getJTabbedPane1().setSelectedIndex(0);
//			}
//			getJTabbedPane1().setEnabledAt(meshTabIndex,shouldMeshBeEnabled);
//		}
		// ok, we're ready
		setClonedSimulation(clonedSimulation);
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		JOptionPane.showMessageDialog(this, "Could not initialize simulation editor\n"+exc.getMessage(), "Error:", JOptionPane.ERROR_MESSAGE);
	}
}


/**
 * Sets the clonedSimulation property (cbit.vcell.solver.Simulation) value.
 * @param clonedSimulation The new value for the property.
 * @see #getClonedSimulation
 */
private void setClonedSimulation(cbit.vcell.solver.Simulation clonedSimulation) {
	cbit.vcell.solver.Simulation oldValue = fieldClonedSimulation;
	fieldClonedSimulation = clonedSimulation;
	firePropertyChange("clonedSimulation", oldValue, clonedSimulation);
}

}
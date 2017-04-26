/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.simulation;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Objects;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.vcell.util.BeanUtils;

import cbit.vcell.geometry.ChomboInvalidGeometryException;
import cbit.vcell.math.gui.MeshTabPanel;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SimulationOwner.UnitInfo;
import cbit.vcell.solver.ode.gui.MathOverridesPanel;
import cbit.vcell.solver.ode.gui.SolverTaskDescriptionAdvancedPanel;
/**
 * Insert the type's description here.
 * Creation date: (5/11/2004 1:28:57 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class SimulationEditor extends JPanel {
	private static final String TAB_ADVANCED_TITLE = "Solver";
	private static final String TAB_PARAMETERS_TITLE = "Parameters";
	private static final String TAB_MESH_TITLE = "Mesh";
	private JTabbedPane ivjJTabbedPane1 = null;
	private MathOverridesPanel ivjMathOverridesPanel1 = null;
	private SolverTaskDescriptionAdvancedPanel ivjSolverTaskDescriptionAdvancedPanel1 = null;
	private Simulation fieldClonedSimulation = null;
	private MeshTabPanel meshTabPanel;

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
			ivjJTabbedPane1.addTab(TAB_PARAMETERS_TITLE, getMathOverridesPanel1());
			ivjJTabbedPane1.addTab(TAB_MESH_TITLE, getMeshTabPanel());
			ivjJTabbedPane1.addTab(TAB_ADVANCED_TITLE, getSolverTaskDescriptionAdvancedPanel1());
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
private MeshTabPanel getMeshTabPanel() {
	if (meshTabPanel == null) {
		try {
			meshTabPanel = new MeshTabPanel();
			meshTabPanel.setName("MeshTabPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return meshTabPanel;
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
		setPreferredSize(new Dimension(547, 530));
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
 * @param simulation not null
 * @param parent 
 * @throws ChomboInvalidGeometryException 
 * @throws NullPointerException
 */
public void prepareToEdit(Simulation simulation, Component parent) throws ChomboInvalidGeometryException {
	Objects.requireNonNull(simulation);
	try {
		Simulation clonedSimulation = (Simulation)BeanUtils.cloneSerializable(simulation);
		clonedSimulation.refreshDependencies();
		getMathOverridesPanel1().setMathOverrides(clonedSimulation == null ? null : clonedSimulation.getMathOverrides());
		getMeshTabPanel().setSimulation(parent, clonedSimulation);
		SolverTaskDescriptionAdvancedPanel stdap = getSolverTaskDescriptionAdvancedPanel1();
		{
			SimulationOwner so = simulation.getSimulationOwner();
			UnitInfo unitInfo = so.getUnitInfo();
			stdap.setUnitInfo(unitInfo);
			
		}
		stdap.setSolverTaskDescription(clonedSimulation == null ? null : clonedSimulation.getSolverTaskDescription());
		
		boolean shouldMeshBeEnabled = false;
		MeshSpecification meshSpec = clonedSimulation.getMeshSpecification();
		if(	meshSpec != null && 
			meshSpec.getGeometry() != null &&
			meshSpec.getGeometry().getDimension() > 0){
				shouldMeshBeEnabled = true;
		}
		if (!shouldMeshBeEnabled) {
			getJTabbedPane1().remove(getMeshTabPanel());
		}
		setClonedSimulation(clonedSimulation);
	} catch (ChomboInvalidGeometryException exc) {
		// if this happens, sim is not allowed to edit
		exc.printStackTrace(System.out);
		throw exc;
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

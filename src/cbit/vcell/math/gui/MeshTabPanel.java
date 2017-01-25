/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math.gui;

import java.awt.GridBagConstraints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.vcell.chombo.gui.ChomboSolverSpecPanel;

import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverTaskDescription;

/**
 * Insert the type's description here. Creation date: (1/9/01 8:56:10 AM)
 * 
 * @author: Jim Schaff
 */
public class MeshTabPanel extends javax.swing.JPanel {
	private MeshSpecificationPanel meshSpecificationPanel = null;
	private ChomboMeshSpecificationPanel chomboMeshSpecificationPanel = null;
	private ChomboSolverSpecPanel chomboSolverSpecPanel = null;
	private Simulation simulation = null;

	private class EventListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == simulation.getSolverTaskDescription()
					&& evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_SOLVER_DESCRIPTION)) {
				updateDisplay();
			}
		}

	}

	private EventListener eventListener = new EventListener();

	/**
	 * MeshSpecificationPanel constructor comment.
	 */
	public MeshTabPanel() {
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
		// user code begin {1}
		// user code end
			setName("MeshTabPanel");
			setLayout(new java.awt.GridBagLayout());
			setSize(324, 310);

			// 0
			meshSpecificationPanel = new MeshSpecificationPanel();
			int gridy = 0;
			GridBagConstraints gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.fill = java.awt.GridBagConstraints.BOTH;
			gbc.weightx = 1.0;
			gbc.weighty = 0.2;
			gbc.insets = new java.awt.Insets(10, 4, 4, 4);
			add(meshSpecificationPanel, gbc);

			chomboMeshSpecificationPanel = new ChomboMeshSpecificationPanel();
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.fill = java.awt.GridBagConstraints.BOTH;
			gbc.weightx = 1.0;
			gbc.weighty = 0.2;
			gbc.insets = new java.awt.Insets(10, 4, 4, 4);
			add(chomboMeshSpecificationPanel, gbc);

			gridy++;
			chomboSolverSpecPanel = new ChomboSolverSpecPanel();
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.fill = java.awt.GridBagConstraints.BOTH;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.insets = new java.awt.Insets(4, 4, 4, 4);
			add(chomboSolverSpecPanel, gbc);

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
			javax.swing.JFrame frame = new javax.swing.JFrame();
			MeshTabPanel aMeshSpecificationPanel = new MeshTabPanel();
			frame.setContentPane(aMeshSpecificationPanel);
			frame.setSize(aMeshSpecificationPanel.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			java.awt.Insets insets = frame.getInsets();
			frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}
	
/**
 * Sets the meshSpecification property (cbit.vcell.mesh.MeshSpecification) value.
 * @param meshSpecification The new value for the property.
 * @see #getMeshSpecification
 */
	public void setSimulation(Simulation newValue) {
		Simulation oldValue = simulation;
		if (oldValue != null) {
			oldValue.getSolverTaskDescription().removePropertyChangeListener(eventListener);
		}
		simulation = newValue;
		if (simulation != null) {
			simulation.getSolverTaskDescription().addPropertyChangeListener(eventListener);
		}
		updateDisplay();
		chomboMeshSpecificationPanel.setSimulation(simulation);
		chomboSolverSpecPanel.setSimulation(simulation);
		meshSpecificationPanel.setSimulation(simulation);
	}

	private void updateDisplay() {
		if (simulation.getSolverTaskDescription().getSolverDescription().isChomboSolver()) {
			chomboMeshSpecificationPanel.setVisible(true);
			chomboSolverSpecPanel.setVisible(true);
			meshSpecificationPanel.setVisible(false);
		} else {
			chomboMeshSpecificationPanel.setVisible(false);
			chomboSolverSpecPanel.setVisible(false);
			meshSpecificationPanel.setVisible(true);
		}
	}

}

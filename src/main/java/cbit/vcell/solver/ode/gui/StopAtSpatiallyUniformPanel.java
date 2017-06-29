/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode.gui;

import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.SolverDescription.SolverFeature;
import cbit.vcell.solver.SolverTaskDescription;

/**
 * Insert the type's description here.
 * Creation date: (11/2/2000 4:46:30 PM)
 * @author: 
 */
public class StopAtSpatiallyUniformPanel extends javax.swing.JPanel {
	private javax.swing.JTextField absTolTextField = null;
	private javax.swing.JTextField relTolTextField = null;
	private SolverTaskDescription solverTaskDescription = null;
	private JCheckBox stopSpatiallyUniformCheckBox = null;

	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JLabel absTolLabel = null;
	private JLabel relTolLabel = null;

	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == StopAtSpatiallyUniformPanel.this && (evt.getPropertyName().equals("solverTaskDescription"))) { 
				refresh();
			}
			if (evt.getSource() == solverTaskDescription) {
				if (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_STOP_AT_SPATIALLY_UNIFORM_ERROR_TOLERANCE)
					|| evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_SOLVER_DESCRIPTION)) {			
					refresh();
				}
			}	
			
		}
		
		public void actionPerformed(java.awt.event.ActionEvent e) {			
			if (e.getSource() == stopSpatiallyUniformCheckBox) {
				if (stopSpatiallyUniformCheckBox.isSelected()) {
					solverTaskDescription.setStopAtSpatiallyUniformErrorTolerance(ErrorTolerance.getDefaultSpatiallyUniformErrorTolerance());
				} else {
					solverTaskDescription.setStopAtSpatiallyUniformErrorTolerance(null);
				}
			} 
		}
		
		public void focusGained(java.awt.event.FocusEvent e) {
		}

		/**
		 * Method to handle events for the FocusListener interface.
		 * @param e java.awt.event.FocusEvent
		 */
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.isTemporary()) {
				return;
			}
			if (e.getSource() == relTolTextField ||				
				e.getSource() == absTolTextField) {
				setNewErrorTolerance();
			}
		}
	}
	
/**
 * ErrorTolerancePanel constructor comment.
 */
public StopAtSpatiallyUniformPanel() {
	super();
	this.addPropertyChangeListener(ivjEventHandler);
	initialize();
}

public void setNewErrorTolerance() {
	double absError = absTolTextField.isEnabled() ? new Double(absTolTextField.getText()).doubleValue() : 1e-9;
	double relError = relTolTextField.isEnabled() ? new Double(relTolTextField.getText()).doubleValue() : 1e-9;
	ErrorTolerance newErrorTol = new ErrorTolerance(absError, relError);	
	solverTaskDescription.setStopAtSpatiallyUniformErrorTolerance(newErrorTol);		
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
private void initConnections() {	
	stopSpatiallyUniformCheckBox.addActionListener(ivjEventHandler);
	absTolTextField.addFocusListener(ivjEventHandler);
	relTolTextField.addFocusListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ErrorTolerancePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(160, 120);

		// 0
		JLabel label = new javax.swing.JLabel("Error Tolerance");
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = 0;
		gbc.gridwidth = 4;
		gbc.weightx = 1.0;
		gbc.anchor = java.awt.GridBagConstraints.CENTER;
		add(label, gbc);

		// 1
		stopSpatiallyUniformCheckBox = new JCheckBox("Stop at Spatially Uniform");
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = java.awt.GridBagConstraints.LINE_START;
		add(stopSpatiallyUniformCheckBox, gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = 1;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		absTolLabel = new javax.swing.JLabel("Absolute");
		add(absTolLabel, gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 2; 
		gbc.gridy = 1;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		absTolTextField = new javax.swing.JTextField();
		absTolTextField.setColumns(10);
		add(absTolTextField, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 3; 
		gbc.gridy = 1;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		relTolLabel = new javax.swing.JLabel("Relative");
		add(relTolLabel, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 4; 
		gbc.gridy = 1;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		relTolTextField = new javax.swing.JTextField();
		relTolTextField.setColumns(10);
		add(relTolTextField, gbc);

	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

public void setSolverTaskDescription(SolverTaskDescription newValue) {
	SolverTaskDescription oldValue = solverTaskDescription;
	/* Stop listening for events from the current object */
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(ivjEventHandler);
	}
	solverTaskDescription = newValue;

	/* Listen for events from the new object */
	if (newValue != null) {
		newValue.addPropertyChangeListener(ivjEventHandler);
	}		
	solverTaskDescription = newValue;
	firePropertyChange("solverTaskDescription", oldValue, newValue);
	
	initConnections();
}

private void refresh() {
	if (solverTaskDescription == null) {
		return;
	}
	
	if (!solverTaskDescription.getSolverDescription().supports(SolverFeature.Feature_StopAtSpatiallyUniform)) {
		setVisible(false);
		return;
	}
	
	setVisible(true);
	boolean bStopAtSpatiallyUniform = solverTaskDescription.getStopAtSpatiallyUniformErrorTolerance() != null;
	
	absTolLabel.setEnabled(bStopAtSpatiallyUniform);
	absTolTextField.setEnabled(bStopAtSpatiallyUniform);
	relTolLabel.setEnabled(bStopAtSpatiallyUniform);
	relTolTextField.setEnabled(bStopAtSpatiallyUniform);

	if (bStopAtSpatiallyUniform) {
		stopSpatiallyUniformCheckBox.setSelected(true);
		absTolTextField.setText("" + solverTaskDescription.getStopAtSpatiallyUniformErrorTolerance().getAbsoluteErrorTolerance());
		relTolTextField.setText("" + solverTaskDescription.getStopAtSpatiallyUniformErrorTolerance().getRelativeErrorTolerance());
	}	
}

}

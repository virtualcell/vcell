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

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.SolverTaskDescription;

/**
 * Insert the type's description here.
 * Creation date: (11/2/2000 4:46:30 PM)
 * @author: 
 */
public class ErrorTolerancePanel extends javax.swing.JPanel {
	private SolverTaskDescription solverTaskDescription = null;
	private javax.swing.JLabel ivjAbsoluteErrorToleranceLabel = null;
	private javax.swing.JTextField ivjAbsoluteErrorToleranceTextField = null;
	private javax.swing.JLabel ivjErrorTolerancesLabel = null;
	private javax.swing.JLabel ivjRelativeErrorToleranceLabel = null;
	private javax.swing.JTextField ivjRelativeErrorToleranceTextField = null;
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

	class IvjEventHandler implements java.awt.event.FocusListener, java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ErrorTolerancePanel.this && (evt.getPropertyName().equals("solverTaskDescription"))) { 
				refresh();
			}
			if (evt.getSource() == solverTaskDescription) {
				if (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_ERROR_TOLERANCE)
					|| evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_SOLVER_DESCRIPTION)) {			
					refresh();
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
			if (e.getSource() == getAbsoluteErrorToleranceTextField() ||				
				e.getSource() == getRelativeErrorToleranceTextField()) {
				setNewErrorTolerance();
			}
		}
	}	
/**
 * ErrorTolerancePanel constructor comment.
 */
public ErrorTolerancePanel() {
	super();
	addPropertyChangeListener(ivjEventHandler);
	initialize();
}

/**
 * Return the AbsoluteErrorToleranceLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getAbsoluteErrorToleranceLabel() {
	if (ivjAbsoluteErrorToleranceLabel == null) {
		try {
			ivjAbsoluteErrorToleranceLabel = new javax.swing.JLabel();
			ivjAbsoluteErrorToleranceLabel.setName("AbsoluteErrorToleranceLabel");
			ivjAbsoluteErrorToleranceLabel.setText("Absolute");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjAbsoluteErrorToleranceLabel;
}
/**
 * Return the AbsoluteErrorToleranceTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getAbsoluteErrorToleranceTextField() {
	if (ivjAbsoluteErrorToleranceTextField == null) {
		try {
			ivjAbsoluteErrorToleranceTextField = new javax.swing.JTextField();
			ivjAbsoluteErrorToleranceTextField.setName("AbsoluteErrorToleranceTextField");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjAbsoluteErrorToleranceTextField;
}

/**
 * Return the ErrorTolerancesLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getErrorTolerancesLabel() {
	if (ivjErrorTolerancesLabel == null) {
		try {
			ivjErrorTolerancesLabel = new javax.swing.JLabel();
			ivjErrorTolerancesLabel.setName("ErrorTolerancesLabel");
			ivjErrorTolerancesLabel.setText("Error Tolerances");
			ivjErrorTolerancesLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjErrorTolerancesLabel;
}
/**
 * Return the RelativeErrorToleranceLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRelativeErrorToleranceLabel() {
	if (ivjRelativeErrorToleranceLabel == null) {
		try {
			ivjRelativeErrorToleranceLabel = new javax.swing.JLabel();
			ivjRelativeErrorToleranceLabel.setName("RelativeErrorToleranceLabel");
			ivjRelativeErrorToleranceLabel.setText("Relative");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRelativeErrorToleranceLabel;
}
/**
 * Return the RelativeErrorToleranceTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getRelativeErrorToleranceTextField() {
	if (ivjRelativeErrorToleranceTextField == null) {
		try {
			ivjRelativeErrorToleranceTextField = new javax.swing.JTextField();
			ivjRelativeErrorToleranceTextField.setName("RelativeErrorToleranceTextField");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRelativeErrorToleranceTextField;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
	DialogUtils.showWarningDialog(this, "Error in Tolerance value : " + exception.getMessage(), new String[] {"Ok"}, "Ok");
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() {
	getAbsoluteErrorToleranceTextField().addFocusListener(ivjEventHandler);
	getRelativeErrorToleranceTextField().addFocusListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ErrorTolerancePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(160, 120);

		java.awt.GridBagConstraints constraintsErrorTolerancesLabel = new java.awt.GridBagConstraints();
		constraintsErrorTolerancesLabel.gridx = 0; constraintsErrorTolerancesLabel.gridy = 0;
		constraintsErrorTolerancesLabel.gridwidth = 2;
		constraintsErrorTolerancesLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsErrorTolerancesLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getErrorTolerancesLabel(), constraintsErrorTolerancesLabel);

		java.awt.GridBagConstraints constraintsAbsoluteErrorToleranceLabel = new java.awt.GridBagConstraints();
		constraintsAbsoluteErrorToleranceLabel.gridx = 0; constraintsAbsoluteErrorToleranceLabel.gridy = 1;
		constraintsAbsoluteErrorToleranceLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getAbsoluteErrorToleranceLabel(), constraintsAbsoluteErrorToleranceLabel);

		java.awt.GridBagConstraints constraintsRelativeErrorToleranceLabel = new java.awt.GridBagConstraints();
		constraintsRelativeErrorToleranceLabel.gridx = 0; constraintsRelativeErrorToleranceLabel.gridy = 2;
		constraintsRelativeErrorToleranceLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getRelativeErrorToleranceLabel(), constraintsRelativeErrorToleranceLabel);

		java.awt.GridBagConstraints constraintsAbsoluteErrorToleranceTextField = new java.awt.GridBagConstraints();
		constraintsAbsoluteErrorToleranceTextField.gridx = 1; constraintsAbsoluteErrorToleranceTextField.gridy = 1;
		constraintsAbsoluteErrorToleranceTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsAbsoluteErrorToleranceTextField.weightx = 1.0;
		constraintsAbsoluteErrorToleranceTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getAbsoluteErrorToleranceTextField(), constraintsAbsoluteErrorToleranceTextField);

		java.awt.GridBagConstraints constraintsRelativeErrorToleranceTextField = new java.awt.GridBagConstraints();
		constraintsRelativeErrorToleranceTextField.gridx = 1; constraintsRelativeErrorToleranceTextField.gridy = 2;
		constraintsRelativeErrorToleranceTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsRelativeErrorToleranceTextField.weightx = 1.0;
		constraintsRelativeErrorToleranceTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getRelativeErrorToleranceTextField(), constraintsRelativeErrorToleranceTextField);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ErrorTolerancePanel aErrorTolerancePanel;
		aErrorTolerancePanel = new ErrorTolerancePanel();
		frame.setContentPane(aErrorTolerancePanel);
		frame.setSize(aErrorTolerancePanel.getSize());
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
 * Enables or disables this component, depending on the value of the
 * parameter <code>b</code>. An enabled component can respond to user
 * input and generate events. Components are enabled initially by default.
 * @param     b   If <code>true</code>, this component and all its children
 *            are enabled; otherwise they are disabled.
 */
public void setEnabled(boolean b) {
	super.setEnabled(b);
	getErrorTolerancesLabel().setEnabled(b);
	getAbsoluteErrorToleranceLabel().setEnabled(b);
	getAbsoluteErrorToleranceTextField().setEnabled(b);
	getRelativeErrorToleranceLabel().setEnabled(b);
	getRelativeErrorToleranceTextField().setEnabled(b);
}

public void setSolverTaskDescription(SolverTaskDescription newValue) throws java.beans.PropertyVetoException {
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




public void refresh() {
	if (solverTaskDescription == null) {
		return;
	}
	getErrorTolerancesLabel().setText("Error Tolerance");
	if (solverTaskDescription.getSolverDescription().isSemiImplicitPdeSolver()
			|| solverTaskDescription.getSolverDescription().isChomboSolver()) {
		getAbsoluteErrorToleranceLabel().setEnabled(false);
		getAbsoluteErrorToleranceTextField().setEnabled(false);
		getAbsoluteErrorToleranceTextField().setText(null);
		getErrorTolerancesLabel().setText("Linear Solver Tolerance");
		getRelativeErrorToleranceTextField().setEnabled(true);
		ErrorTolerance errTol = solverTaskDescription.getErrorTolerance();
		getRelativeErrorToleranceTextField().setText(errTol.getRelativeErrorTolerance() + "");
	} else if (solverTaskDescription.getSolverDescription().hasErrorTolerance()) {
		setEnabled(true);
		ErrorTolerance errTol = solverTaskDescription.getErrorTolerance();
		getAbsoluteErrorToleranceTextField().setText(errTol.getAbsoluteErrorTolerance() + "");
		getRelativeErrorToleranceTextField().setText(errTol.getRelativeErrorTolerance() + "");
	} else {
		getAbsoluteErrorToleranceTextField().setText("");
		getRelativeErrorToleranceTextField().setText("");
		setEnabled(false);
	}	
}

public void setNewErrorTolerance() {
	try {
		double absError = getAbsoluteErrorToleranceTextField().isEnabled() ? new Double(getAbsoluteErrorToleranceTextField().getText()).doubleValue() : 1e-9;
		double relError = getRelativeErrorToleranceTextField().isEnabled() ? new Double(getRelativeErrorToleranceTextField().getText()).doubleValue() : 1e-9;
		ErrorTolerance newErrTol = new ErrorTolerance(absError, relError);
		solverTaskDescription.setErrorTolerance(newErrTol);
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
}

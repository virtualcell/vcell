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

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.UniformOutputTimeSpec;

/**
 * Insert the type's description here.
 * Creation date: (11/2/2000 4:46:05 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class TimeStepPanel extends javax.swing.JPanel {
	private SolverTaskDescription solverTaskDescription = null;
	private javax.swing.JLabel ivjDefaultTimeStepLabel = null;
	private javax.swing.JTextField ivjDefaultTimeStepTextField = null;
	private javax.swing.JLabel ivjMaximumTimeStepLabel = null;
	private javax.swing.JTextField ivjMaximumTimeStepTextField = null;
	private javax.swing.JLabel ivjMinimumTimeStepLabel = null;
	private javax.swing.JTextField ivjMinimumTimeStepTextField = null;
	private javax.swing.JLabel ivjTimeStepLabel = null;
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

	class IvjEventHandler implements java.awt.event.FocusListener, java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == TimeStepPanel.this && (evt.getPropertyName().equals("solverTaskDescription"))) { 
				refresh();
			}
			if (evt.getSource() == solverTaskDescription) {
				if (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_TIME_STEP)
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
			if (e.getSource() == getDefaultTimeStepTextField()){
				correctUniformOutputTimeStep();
			}
			if (e.getSource() == getDefaultTimeStepTextField() ||				
				e.getSource() == getMinimumTimeStepTextField() || 
				e.getSource() == getMaximumTimeStepTextField()) {
				setNewTimeStep();
			}
		}
	}	
/**
 * TimeStepPanel constructor comment.
 */
public TimeStepPanel() {
	super();
	addPropertyChangeListener(ivjEventHandler);
	initialize();
}

public void correctUniformOutputTimeStep() {
	boolean bValid = true;
	double timeStep = Double.parseDouble(getDefaultTimeStepTextField().getText());
	if (solverTaskDescription.getOutputTimeSpec().isUniform() && !solverTaskDescription.getSolverDescription().hasVariableTimestep()) {
		OutputTimeSpec outputTimeSpec = solverTaskDescription.getOutputTimeSpec();
		if (outputTimeSpec instanceof UniformOutputTimeSpec){
			UniformOutputTimeSpec uniformTimeSpec = (UniformOutputTimeSpec)outputTimeSpec;
			double outputTime = uniformTimeSpec.getOutputTimeStep();
			double suggestedInterval = outputTime;
			if (outputTime < timeStep) {
				suggestedInterval = timeStep;
				bValid = false;
			} else if (!BeanUtils.isIntegerMultiple(outputTime, timeStep)){
				double n = outputTime/timeStep;
				int intn = (int)Math.round(n);
				if (intn != n) {
					bValid = false;
					suggestedInterval = (intn * timeStep);
				}
			} 
		
			if (!bValid) {		
				String ret = PopupGenerator.showWarningDialog(TimeStepPanel.this, "Output Interval", "Output Interval must " +
						"be integer multiple of time step.\n\nChange Output Interval to " + suggestedInterval + "?", 
						new String[]{ UserMessage.OPTION_YES, UserMessage.OPTION_NO}, UserMessage.OPTION_YES);
				if (ret.equals(UserMessage.OPTION_YES)) {
					uniformTimeSpec.setOuputTimeStep(suggestedInterval);
					bValid = true;
				} 
			}
		}
	}
		
}

public void setNewTimeStep() {
	try {
		TimeStep oldTimeStep = solverTaskDescription.getTimeStep();
		double defaultTimeStep = !getDefaultTimeStepTextField().isEnabled() ? oldTimeStep.getDefaultTimeStep() : new Double(getDefaultTimeStepTextField().getText()).doubleValue();
		double minTimeStep = !getMinimumTimeStepTextField().isEnabled() ? oldTimeStep.getMinimumTimeStep() : new Double(getMinimumTimeStepTextField().getText()).doubleValue();
		double maxTimeStep = !getMaximumTimeStepTextField().isEnabled() ? oldTimeStep.getMaximumTimeStep() : new Double(getMaximumTimeStepTextField().getText()).doubleValue();
		TimeStep newTimeStep = new TimeStep(minTimeStep, defaultTimeStep, maxTimeStep);
		solverTaskDescription.setTimeStep(newTimeStep);
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Return the DefaultTimeStepLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDefaultTimeStepLabel() {
	if (ivjDefaultTimeStepLabel == null) {
		try {
			ivjDefaultTimeStepLabel = new javax.swing.JLabel();
			ivjDefaultTimeStepLabel.setName("DefaultTimeStepLabel");
			ivjDefaultTimeStepLabel.setText("Default");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultTimeStepLabel;
}
/**
 * Return the DefaultTimeStepTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getDefaultTimeStepTextField() {
	if (ivjDefaultTimeStepTextField == null) {
		try {
			ivjDefaultTimeStepTextField = new javax.swing.JTextField();
			ivjDefaultTimeStepTextField.setName("DefaultTimeStepTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultTimeStepTextField;
}
/**
 * Return the MaximumTimeStepLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMaximumTimeStepLabel() {
	if (ivjMaximumTimeStepLabel == null) {
		try {
			ivjMaximumTimeStepLabel = new javax.swing.JLabel();
			ivjMaximumTimeStepLabel.setName("MaximumTimeStepLabel");
			ivjMaximumTimeStepLabel.setText("Maximum");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMaximumTimeStepLabel;
}
/**
 * Return the MaximumTimeStepTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMaximumTimeStepTextField() {
	if (ivjMaximumTimeStepTextField == null) {
		try {
			ivjMaximumTimeStepTextField = new javax.swing.JTextField();
			ivjMaximumTimeStepTextField.setName("MaximumTimeStepTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMaximumTimeStepTextField;
}
/**
 * Return the MinimumTimeStepLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMinimumTimeStepLabel() {
	if (ivjMinimumTimeStepLabel == null) {
		try {
			ivjMinimumTimeStepLabel = new javax.swing.JLabel();
			ivjMinimumTimeStepLabel.setName("MinimumTimeStepLabel");
			ivjMinimumTimeStepLabel.setText("Minimum");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMinimumTimeStepLabel;
}
/**
 * Return the MinimumTimeStepTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMinimumTimeStepTextField() {
	if (ivjMinimumTimeStepTextField == null) {
		try {
			ivjMinimumTimeStepTextField = new javax.swing.JTextField();
			ivjMinimumTimeStepTextField.setName("MinimumTimeStepTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMinimumTimeStepTextField;
}

/**
 * Return the TimeStepLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTimeStepLabel() {
	if (ivjTimeStepLabel == null) {
		try {
			ivjTimeStepLabel = new javax.swing.JLabel();
			ivjTimeStepLabel.setName("TimeStepLabel");
			ivjTimeStepLabel.setText("Time Step");
			ivjTimeStepLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeStepLabel;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
	 DialogUtils.showWarningDialog(this, "Possible error in TimeStep value : " + exception.getMessage(), new String[] {"Ok"}, "Ok");
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() {
	getMinimumTimeStepTextField().addFocusListener(ivjEventHandler);
	getDefaultTimeStepTextField().addFocusListener(ivjEventHandler);
	getMaximumTimeStepTextField().addFocusListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TimeStepPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(175, 120);

		java.awt.GridBagConstraints constraintsTimeStepLabel = new java.awt.GridBagConstraints();
		constraintsTimeStepLabel.gridx = 0; constraintsTimeStepLabel.gridy = 0;
		constraintsTimeStepLabel.gridwidth = 2;
		constraintsTimeStepLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsTimeStepLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getTimeStepLabel(), constraintsTimeStepLabel);

		java.awt.GridBagConstraints constraintsMinimumTimeStepLabel = new java.awt.GridBagConstraints();
		constraintsMinimumTimeStepLabel.gridx = 0; constraintsMinimumTimeStepLabel.gridy = 1;
		constraintsMinimumTimeStepLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getMinimumTimeStepLabel(), constraintsMinimumTimeStepLabel);

		java.awt.GridBagConstraints constraintsDefaultTimeStepLabel = new java.awt.GridBagConstraints();
		constraintsDefaultTimeStepLabel.gridx = 0; constraintsDefaultTimeStepLabel.gridy = 2;
		constraintsDefaultTimeStepLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getDefaultTimeStepLabel(), constraintsDefaultTimeStepLabel);

		java.awt.GridBagConstraints constraintsMaximumTimeStepLabel = new java.awt.GridBagConstraints();
		constraintsMaximumTimeStepLabel.gridx = 0; constraintsMaximumTimeStepLabel.gridy = 3;
		constraintsMaximumTimeStepLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getMaximumTimeStepLabel(), constraintsMaximumTimeStepLabel);

		java.awt.GridBagConstraints constraintsMinimumTimeStepTextField = new java.awt.GridBagConstraints();
		constraintsMinimumTimeStepTextField.gridx = 1; constraintsMinimumTimeStepTextField.gridy = 1;
		constraintsMinimumTimeStepTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsMinimumTimeStepTextField.weightx = 1.0;
		constraintsMinimumTimeStepTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getMinimumTimeStepTextField(), constraintsMinimumTimeStepTextField);

		java.awt.GridBagConstraints constraintsDefaultTimeStepTextField = new java.awt.GridBagConstraints();
		constraintsDefaultTimeStepTextField.gridx = 1; constraintsDefaultTimeStepTextField.gridy = 2;
		constraintsDefaultTimeStepTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDefaultTimeStepTextField.weightx = 1.0;
		constraintsDefaultTimeStepTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getDefaultTimeStepTextField(), constraintsDefaultTimeStepTextField);

		java.awt.GridBagConstraints constraintsMaximumTimeStepTextField = new java.awt.GridBagConstraints();
		constraintsMaximumTimeStepTextField.gridx = 1; constraintsMaximumTimeStepTextField.gridy = 3;
		constraintsMaximumTimeStepTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsMaximumTimeStepTextField.weightx = 1.0;
		constraintsMaximumTimeStepTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getMaximumTimeStepTextField(), constraintsMaximumTimeStepTextField);
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
		TimeStepPanel aTimeStepPanel;
		aTimeStepPanel = new TimeStepPanel();
		frame.setContentPane(aTimeStepPanel);
		frame.setSize(aTimeStepPanel.getSize());
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
 * Sets the timeStep property (cbit.vcell.solver.TimeStep) value.
 * @param timeStep The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getTimeStep
 */
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

private void enableMaxTimeStep(boolean bEnabled) 
{
	getMaximumTimeStepLabel().setEnabled(bEnabled);
	getMaximumTimeStepTextField().setEnabled(bEnabled);
	if (!bEnabled) {
		getMaximumTimeStepTextField().setText("");
	}
}

private void enableMinTimeStep(boolean bEnabled) 
{
	getMinimumTimeStepLabel().setEnabled(bEnabled);
	getMinimumTimeStepTextField().setEnabled(bEnabled);
	if (!bEnabled) {
		getMinimumTimeStepTextField().setText("");
	}
}

private void enableDefaultTimeStep(boolean bEnabled) 
{
	getDefaultTimeStepLabel().setEnabled(bEnabled);
	getDefaultTimeStepTextField().setEnabled(bEnabled);
	if (!bEnabled) {
		getDefaultTimeStepTextField().setText("");
	}
}

private void refresh() {
	if (solverTaskDescription == null) {
		return;
	}
	
	SolverDescription solverDescription = solverTaskDescription.getSolverDescription(); 
	if (solverDescription.compareEqual(SolverDescription.StochGibson)) { // stochastic time
		enableDefaultTimeStep(false);
		enableMinTimeStep(false);
		enableMaxTimeStep(false);
	} else if(solverDescription.compareEqual(SolverDescription.NFSim)) {
		ivjTimeStepLabel.setEnabled(false);
		enableDefaultTimeStep(false);
		enableMinTimeStep(false);
		enableMaxTimeStep(false);
	} else {
		setEnabled(true);
		TimeStep ts = solverTaskDescription.getTimeStep();
		getDefaultTimeStepTextField().setText(ts.getDefaultTimeStep()+"");
		getMinimumTimeStepTextField().setText(ts.getMinimumTimeStep()+"");
		getMaximumTimeStepTextField().setText(ts.getMaximumTimeStep()+"");	
		
		// fixed time step solvers and non spatial stochastic solvers only show default time step.
		if (!solverDescription.hasVariableTimestep() || solverDescription.isNonSpatialStochasticSolver()) {
			enableDefaultTimeStep(true);
			enableMinTimeStep(false);
			enableMaxTimeStep(false);
		} else {
			// variable time step solvers shows min and max, but sundials solvers don't show min
			enableDefaultTimeStep(false);
			if (solverDescription.hasSundialsTimeStepping()) {
				enableMinTimeStep(false);
			}
			enableMaxTimeStep(true);			
		}
		
	 }
}

}

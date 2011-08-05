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

import cbit.vcell.solver.TimeBounds;

/**
 * Insert the type's description here.
 * Creation date: (11/2/2000 4:45:38 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class TimeBoundsPanel extends javax.swing.JPanel implements java.awt.event.FocusListener {
	private TimeBounds fieldTimeBounds = null;
	private javax.swing.JLabel ivjEndingTimeLabel = null;
	private javax.swing.JTextField ivjEndingTimeTextField = null;
	private javax.swing.JLabel ivjStartingTimeLabel = null;
	private javax.swing.JTextField ivjStartingTimeTextField = null;
	private javax.swing.JLabel ivjTimeBoundsLabel = null;
/**
 * TimeBoundsPanel constructor comment.
 */
public TimeBoundsPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the FocusListener interface.
 * @param e java.awt.event.FocusEvent
 */
public void focusGained(java.awt.event.FocusEvent e) {
}
/**
 * Method to handle events for the FocusListener interface.
 * @param e java.awt.event.FocusEvent
 */
public void focusLost(java.awt.event.FocusEvent e) {
	try {
		if (e.getSource() == getEndingTimeTextField()
				|| e.getSource() == getStartingTimeTextField()) {
			setTimeBounds(new TimeBounds(new Double(getStartingTimeTextField().getText()).doubleValue(), new Double(getEndingTimeTextField().getText()).doubleValue()));
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Return the EndingTimeLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getEndingTimeLabel() {
	if (ivjEndingTimeLabel == null) {
		try {
			ivjEndingTimeLabel = new javax.swing.JLabel("Ending");
			ivjEndingTimeLabel.setName("EndingTimeLabel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEndingTimeLabel;
}
/**
 * Return the EndingTimeTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getEndingTimeTextField() {
	if (ivjEndingTimeTextField == null) {
		try {
			ivjEndingTimeTextField = new javax.swing.JTextField();
			ivjEndingTimeTextField.setName("EndingTimeTextField");
			ivjEndingTimeTextField.setPreferredSize(new java.awt.Dimension(4, 21));
			ivjEndingTimeTextField.setColumns(10);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEndingTimeTextField;
}
/**
 * Return the StartingTimeLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getStartingTimeLabel() {
	if (ivjStartingTimeLabel == null) {
		try {
			ivjStartingTimeLabel = new javax.swing.JLabel("Starting");
			ivjStartingTimeLabel.setName("StartingTimeLabel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStartingTimeLabel;
}
/**
 * Return the StartingTimeTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getStartingTimeTextField() {
	if (ivjStartingTimeTextField == null) {
		try {
			ivjStartingTimeTextField = new javax.swing.JTextField();
			ivjStartingTimeTextField.setName("StartingTimeTextField");
			ivjStartingTimeTextField.setText("");
			ivjStartingTimeTextField.setEditable(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStartingTimeTextField;
}
/**
 * Gets the timeBounds property (cbit.vcell.solver.TimeBounds) value.
 * @return The timeBounds property value.
 * @see #setTimeBounds
 */
public TimeBounds getTimeBounds() {
	return fieldTimeBounds;
}

/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getTimeBoundsLabel() {
	if (ivjTimeBoundsLabel == null) {
		try {
			ivjTimeBoundsLabel = new javax.swing.JLabel("Time Bounds");
			ivjTimeBoundsLabel.setName("TimeBoundsLabel");
			ivjTimeBoundsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjTimeBoundsLabel;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
	org.vcell.util.gui.DialogUtils.showWarningDialog(this, "Error in Starting or Ending TimeBounds value : " + exception.getMessage(), new String[] {"Ok"}, "Ok");
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("TimeBoundsPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(160, 120);

		java.awt.GridBagConstraints constraintsTimeBoundsLabel = new java.awt.GridBagConstraints();
		constraintsTimeBoundsLabel.gridx = 1; constraintsTimeBoundsLabel.gridy = 0;
		constraintsTimeBoundsLabel.gridwidth = 2;
		constraintsTimeBoundsLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsTimeBoundsLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getTimeBoundsLabel(), constraintsTimeBoundsLabel);

		java.awt.GridBagConstraints constraintsStartingTimeLabel = new java.awt.GridBagConstraints();
		constraintsStartingTimeLabel.gridx = 1; constraintsStartingTimeLabel.gridy = 1;
		constraintsStartingTimeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getStartingTimeLabel(), constraintsStartingTimeLabel);

		java.awt.GridBagConstraints constraintsEndingTimeLabel = new java.awt.GridBagConstraints();
		constraintsEndingTimeLabel.gridx = 1; constraintsEndingTimeLabel.gridy = 2;
		constraintsEndingTimeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getEndingTimeLabel(), constraintsEndingTimeLabel);

		java.awt.GridBagConstraints constraintsEndingTimeTextField = new java.awt.GridBagConstraints();
		constraintsEndingTimeTextField.gridx = 2; constraintsEndingTimeTextField.gridy = 2;
		constraintsEndingTimeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsEndingTimeTextField.weightx = 1.0;
		constraintsEndingTimeTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getEndingTimeTextField(), constraintsEndingTimeTextField);

		java.awt.GridBagConstraints constraintsStartingTimeTextField = new java.awt.GridBagConstraints();
		constraintsStartingTimeTextField.gridx = 2; constraintsStartingTimeTextField.gridy = 1;
		constraintsStartingTimeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsStartingTimeTextField.weightx = 1.0;
		constraintsStartingTimeTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getStartingTimeTextField(), constraintsStartingTimeTextField);

		getEndingTimeTextField().addFocusListener(this);
		getStartingTimeTextField().addFocusListener(this);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
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
	getTimeBoundsLabel().setEnabled(b);
	getStartingTimeLabel().setEnabled(b);
	getStartingTimeTextField().setEnabled(b);
	getEndingTimeLabel().setEnabled(b);
	getEndingTimeTextField().setEnabled(b);
}
/**
 * Sets the timeBounds property (cbit.vcell.solver.TimeBounds) value.
 * @param timeBounds The new value for the property.
 * @see #getTimeBounds
 */
public void setTimeBounds(TimeBounds timeBounds) {
	TimeBounds oldValue = fieldTimeBounds;
	fieldTimeBounds = timeBounds;
	if (fieldTimeBounds != null) {
		getStartingTimeTextField().setText(String.valueOf(fieldTimeBounds.getStartingTime()));
		getEndingTimeTextField().setText(String.valueOf(fieldTimeBounds.getEndingTime()));
	}
	firePropertyChange("timeBounds", oldValue, timeBounds);
}

}

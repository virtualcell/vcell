/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;
import java.awt.Frame;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.vcell.client.logicalwindow.LWContainerHandle;
/**
 * Insert the type's description here.
 * Creation date: (5/18/2004 1:14:29 AM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class DefaultProgressDialog extends ProgressDialog {
	private JPanel mainPanel = null;
	private JLabel messageLabel = null;

/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 6:08:36 PM)
 * @param owner java.awt.Frame
 */
public DefaultProgressDialog(LWContainerHandle owner) {
	super(owner);
	initialize();
}


/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getMainPane() {
	if (mainPanel == null) {
		try {
			mainPanel = new javax.swing.JPanel();
			mainPanel.setName("JDialogContentPane");
			mainPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabel1.weighty = 1.0;
			constraintsJLabel1.ipady = 15;
			mainPanel.add(getMessageLabel(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsCancelButton = new java.awt.GridBagConstraints();
			constraintsCancelButton.gridx = 0; constraintsCancelButton.gridy = 2;
			constraintsCancelButton.insets = new java.awt.Insets(4, 0, 4, 0);
			mainPanel.add(getCancelButton(), constraintsCancelButton);

			java.awt.GridBagConstraints constraintsJProgressBar1 = new java.awt.GridBagConstraints();
			constraintsJProgressBar1.gridx = 0; constraintsJProgressBar1.gridy = 1;
			constraintsJProgressBar1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJProgressBar1.weightx = 1.0;
			constraintsJProgressBar1.weighty = 1.0;
			constraintsJProgressBar1.ipady = 25;
			mainPanel.add(getProgressBar(), constraintsJProgressBar1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return mainPanel;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getMessageLabel() {
	if (messageLabel == null) {
		try {
			messageLabel = new javax.swing.JLabel();
			messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			messageLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return messageLabel;
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

private void initialize() {
	try {
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(341, 116);
		setTitle("Please wait:");
		add(getMainPane());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 1:06:46 PM)
 * @param message java.lang.String
 */
public void setMessageImpl(String message) {
	getMessageLabel().setText(message);
}

}

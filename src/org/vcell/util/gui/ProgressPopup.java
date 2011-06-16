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

import java.awt.*;
import javax.swing.*;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class ProgressPopup extends JFrame {
	private JLabel ivjJLabel1 = null;
	private JProgressBar ivjJProgressBar1 = null;
	private JPanel ivjFrameContentPane = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public ProgressPopup() {
	super();
	initialize();
}
/**
 * ProgressPopup constructor comment.
 * @param title java.lang.String
 */
public ProgressPopup(String title) {
	super(title);
}
/**
 * Insert the method's description here.
 * Creation date: (1/18/00 5:50:51 PM)
 */
public void centerOnScreen() {

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	Dimension progressPopupSize = getSize();
	if (progressPopupSize.height > screenSize.height)
			progressPopupSize.height = screenSize.height;
	if (progressPopupSize.width > screenSize.width)
			progressPopupSize.width = screenSize.width;
	setLocation((screenSize.width - progressPopupSize.width) / 2, (screenSize.height - progressPopupSize.height) / 2);

}
/**
 * Return the FrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getFrameContentPane() {
	if (ivjFrameContentPane == null) {
		try {
			ivjFrameContentPane = new javax.swing.JPanel();
			ivjFrameContentPane.setName("FrameContentPane");
			ivjFrameContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJProgressBar1 = new java.awt.GridBagConstraints();
			constraintsJProgressBar1.gridx = 0; constraintsJProgressBar1.gridy = 1;
			constraintsJProgressBar1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJProgressBar1.weighty = 0.5;
			constraintsJProgressBar1.insets = new java.awt.Insets(5, 10, 10, 10);
			getFrameContentPane().add(getJProgressBar1(), constraintsJProgressBar1);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJLabel1.weightx = 1.0;
			constraintsJLabel1.weighty = 0.5;
			constraintsJLabel1.insets = new java.awt.Insets(10, 10, 5, 10);
			getFrameContentPane().add(getJLabel1(), constraintsJLabel1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFrameContentPane;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Working, please wait...");
			ivjJLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Return the JProgressBar1 property value.
 * @return javax.swing.JProgressBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JProgressBar getJProgressBar1() {
	if (ivjJProgressBar1 == null) {
		try {
			ivjJProgressBar1 = new javax.swing.JProgressBar();
			ivjJProgressBar1.setName("JProgressBar1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJProgressBar1;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ProgressPopup");
		setSize(500, 100);
		setVisible(false);
		setResizable(false);
		setContentPane(getFrameContentPane());
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
		ProgressPopup aProgressPopup;
		aProgressPopup = new ProgressPopup();
		try {
			Class<?> aCloserClass = Class.forName("com.ibm.uvm.abt.edit.WindowCloser");
			Class<?> parmTypes[] = { java.awt.Window.class };
			Object parms[] = { aProgressPopup };
			java.lang.reflect.Constructor<?> aCtor = aCloserClass.getConstructor(parmTypes);
			aCtor.newInstance(parms);
		} catch (java.lang.Throwable exc) {};
		aProgressPopup.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.Frame");
		exception.printStackTrace(System.out);
	}
}
/**
 * This method was created in VisualAge.
 * @param text java.lang.String
 */
public void setLabelText(String text) {
	getJLabel1().setText(text);
}
/**
 * This method was created in VisualAge.
 * @param min int
 */
public void setMaximum(int max) {
	if (max <= 0) max = 100;
	getJProgressBar1().setMaximum(max);
}
/**
 * This method was created in VisualAge.
 * @param value int
 */
public void setValue(int value) {
	if (value < 0) value = 0;
	if (value > getJProgressBar1().getMaximum()) value = getJProgressBar1().getMaximum();
	getJProgressBar1().setValue(value);
}
/**
 * This method was created in VisualAge.
 * @param title java.lang.String
 */
public void setWindowTitle(String title) {
	this.setTitle(title);
}
}

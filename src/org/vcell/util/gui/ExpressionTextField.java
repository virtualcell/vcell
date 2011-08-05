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

/**
 * Insert the type's description here.
 * Creation date: (4/2/01 11:20:10 AM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class ExpressionTextField extends javax.swing.JTextField {
/**
 * ExpressionTextField constructor comment.
 */
public ExpressionTextField() {
	super();
	initialize();
}
/**
 * ExpressionTextField constructor comment.
 * @param columns int
 */
public ExpressionTextField(int columns) {
	super(columns);
}
/**
 * ExpressionTextField constructor comment.
 * @param text java.lang.String
 */
public ExpressionTextField(String text) {
	super(text);
}
/**
 * ExpressionTextField constructor comment.
 * @param text java.lang.String
 * @param columns int
 */
public ExpressionTextField(String text, int columns) {
	super(text, columns);
}
/**
 * ExpressionTextField constructor comment.
 * @param doc javax.swing.text.Document
 * @param text java.lang.String
 * @param columns int
 */
public ExpressionTextField(javax.swing.text.Document doc, String text, int columns) {
	super(doc, text, columns);
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

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
		setName("ExpressionTextField");
		setSize(190, 20);
		setColumns(15);
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
		ExpressionTextField aExpressionTextField;
		aExpressionTextField = new ExpressionTextField();
		frame.setContentPane(aExpressionTextField);
		frame.setSize(aExpressionTextField.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of cbit.vcell.math.gui.ExpressionTextField");
		exception.printStackTrace(System.out);
	}
}
}

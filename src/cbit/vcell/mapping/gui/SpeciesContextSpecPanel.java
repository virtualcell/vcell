/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.parser.ExpressionException;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class SpeciesContextSpecPanel extends DocumentEditorSubPanel {
	private JSortTable ivjScrollPaneTable = null;
	private SpeciesContextSpecParameterTableModel ivjSpeciesContextSpecParameterTableModel1 = null;

/**
 * Constructor
 */
public SpeciesContextSpecPanel() {
	super();
	initialize();
}

/**
 * Return the ScrollPaneTable property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
private JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new JSortTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}

/**
 * Return the SpeciesContextSpecParameterTableModel1 property value.
 * @return cbit.vcell.mapping.gui.SpeciesContextSpecParameterTableModel
 */
private SpeciesContextSpecParameterTableModel getSpeciesContextSpecParameterTableModel1() {
	if (ivjSpeciesContextSpecParameterTableModel1 == null) {
		try {
			ivjSpeciesContextSpecParameterTableModel1 = new SpeciesContextSpecParameterTableModel(getScrollPaneTable());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSpeciesContextSpecParameterTableModel1;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	if (exception instanceof ExpressionException){
		javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
	}
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in SpeciesContextSpecPanel");
	exception.printStackTrace(System.out);
}

/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {	
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("SpeciesContextSpecPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(572, 196);
		setBackground(Color.white);

//		JLabel label = new JLabel("<html><u>Select only one species to set initial conditions</u></html>");
//		label.setHorizontalAlignment(SwingConstants.CENTER);
//		label.setFont(label.getFont().deriveFont(Font.BOLD));
//		add(label, BorderLayout.NORTH);
		add(getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);
		initConnections();
		
		getScrollPaneTable().setModel(getSpeciesContextSpecParameterTableModel1());
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
		java.awt.Frame frame = new java.awt.Frame();
		SpeciesContextSpecPanel aSpeciesContextSpecPanel;
		aSpeciesContextSpecPanel = new SpeciesContextSpecPanel();
		frame.add("Center", aSpeciesContextSpecPanel);
		frame.setSize(aSpeciesContextSpecPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}
}

/**
 * Set the SpeciesContextSpec to a new value.
 * @param newValue cbit.vcell.mapping.SpeciesContextSpec
 */
void setSpeciesContextSpec(SpeciesContextSpec newValue) {
	getSpeciesContextSpecParameterTableModel1().setSpeciesContextSpec(newValue);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	SpeciesContextSpec speciesContextSpec = null;
	if (selectedObjects != null && selectedObjects.length == 1 && selectedObjects[0] instanceof SpeciesContextSpec) {
		speciesContextSpec = (SpeciesContextSpec) selectedObjects[0];
	}
	setSpeciesContextSpec(speciesContextSpec);	
}

}

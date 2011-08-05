/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;
import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;

import cbit.vcell.desktop.BioModelCellRenderer;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public class ApplicationPropertiesPanel extends DocumentEditorSubPanel {
	private JTree tree = null;
	private SimulationContext simulationContext = null;
	private ApplicationPropertiesTreeModel treeModel = null;

/**
 * BioModelTreePanel constructor comment.
 */
public ApplicationPropertiesPanel() {
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
		tree = new JTree();
		treeModel = new ApplicationPropertiesTreeModel();
		tree.setModel(treeModel);
		ToolTipManager.sharedInstance().registerComponent(tree);
		tree.setCellRenderer(new BioModelCellRenderer(null));
		
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(tree);
		add(scrollPane, BorderLayout.CENTER);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Set the BioModel to a new value.
 * @param newValue cbit.vcell.biomodel.BioModel
 */
private void setSimulationContext(SimulationContext newValue) {
	if (simulationContext == newValue) {
		return;
	}
	simulationContext = newValue;
	treeModel.setSimulationContext(simulationContext);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length != 1) {
		setSimulationContext(null);
	} else if (selectedObjects[0] instanceof SimulationContext) {
		setSimulationContext((SimulationContext) selectedObjects[0]);
	} else {
		setSimulationContext(null);
	}
}

}

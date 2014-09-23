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
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.sorttable.JSortTable;
import org.vcell.util.gui.sorttable.SortTableModel;

import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.ReactionStep;

/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:31:14 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class ReactionSpecsPanel extends DocumentEditorSubPanel {
	private JSortTable ivjScrollPaneTable = null;
	private ReactionSpecsTableModel ivjReactionSpecsTableModel = null;
	private SimulationContext fieldSimulationContext = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	
	private class IvjEventHandler implements java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ReactionSpecsPanel.this && (evt.getPropertyName().equals("simulationContext"))) 
				getReactionSpecsTableModel().setSimulationContext(getSimulationContext());
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == getScrollPaneTable().getSelectionModel()) {
				setSelectedObjectsFromTable(getScrollPaneTable(), getReactionSpecsTableModel());
			}
		}
	};
	
public ReactionSpecsPanel() {
	super();
	initialize();
}

public void setSearchText(String searchText){
	ivjReactionSpecsTableModel.setSearchText(searchText);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ReactionSpecsPanel");
		setLayout(new BorderLayout());
		//setSize(456, 539);
		add(getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/*
 * Return the ReactionSpecsTableModel property value.
 * @return cbit.vcell.mapping.gui.ReactionSpecsTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ReactionSpecsTableModel getReactionSpecsTableModel() {
	if (ivjReactionSpecsTableModel == null) {
		try {
			ivjReactionSpecsTableModel = new ReactionSpecsTableModel(getScrollPaneTable());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjReactionSpecsTableModel;
}


/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
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
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
public SimulationContext getSimulationContext() {
	return fieldSimulationContext;
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable().setModel(getReactionSpecsTableModel());
	getScrollPaneTable().setDefaultRenderer(ReactionStep.class, new DefaultScrollTableCellRenderer() {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			defaultToolTipText = null;
			if (value instanceof ReactionStep) {
				setText(((ReactionStep)value).getName());
				defaultToolTipText = getText();
				setToolTipText(defaultToolTipText);
			}
			
			TableModel tableModel = table.getModel();
			if (!(tableModel instanceof SortTableModel)) {
				return this;
			}
			
			DefaultScrollTableCellRenderer.issueRenderer(this, defaultToolTipText, table, row, column, tableModel);
			return this;
		}
	});
	getScrollPaneTable().getSelectionModel().addListSelectionListener(ivjEventHandler);
}

@Override
public void setIssueManager(IssueManager issueManager) {
	super.setIssueManager(issueManager);
	ivjReactionSpecsTableModel.setIssueManager(issueManager);
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ReactionSpecsPanel aReactionSpecsPanel;
		aReactionSpecsPanel = new ReactionSpecsPanel();
		frame.setContentPane(aReactionSpecsPanel);
		frame.setSize(aReactionSpecsPanel.getSize());
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
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(SimulationContext simulationContext) {
	SimulationContext oldValue = fieldSimulationContext;
	fieldSimulationContext = simulationContext;
	firePropertyChange("simulationContext", oldValue, simulationContext);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	setTableSelections(selectedObjects, getScrollPaneTable(), getReactionSpecsTableModel());
}

}

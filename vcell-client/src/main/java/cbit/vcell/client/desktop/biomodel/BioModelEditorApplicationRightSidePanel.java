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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public abstract class BioModelEditorApplicationRightSidePanel<T> extends DocumentEditorSubPanel {
	protected static final String PROPERTY_NAME_SIMULATION_CONTEXT = "simulationContext";
	
	protected JButton addNewButton = null;
	protected JButton deleteButton = null;
	protected EditorScrollTable table;
	protected BioModelEditorApplicationRightSideTableModel<T> tableModel = null;
	protected SimulationContext simulationContext;
	protected JTextField textFieldSearch = null;

	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private class InternalEventHandler implements ActionListener, PropertyChangeListener, ListSelectionListener, DocumentListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditorApplicationRightSidePanel.this && evt.getPropertyName().equals(PROPERTY_NAME_SIMULATION_CONTEXT)) {
				simulationContextChanged();
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == addNewButton) {
//				int[] selectedRows = table.getSelectedRows();
				newButtonPressed();
//				if(selectedRows == null || selectedRows.length == 0){
					table.getSelectionModel().setSelectionInterval(table.getRowCount()-1, table.getRowCount()-1);
//				}
			} else if (e.getSource() == deleteButton) {
				deleteButtonPressed();
			}
		}
		
		public void valueChanged(ListSelectionEvent e) {
			if (simulationContext == null || e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == table.getSelectionModel()) {
				tableSelectionChanged();
			}
			
		}

		public void insertUpdate(DocumentEvent e) {
			searchTable();
		}

		public void removeUpdate(DocumentEvent e) {
			searchTable();
		}

		public void changedUpdate(DocumentEvent e) {
			searchTable();
		}
	}
	
	public BioModelEditorApplicationRightSidePanel() {
		super();
		addPropertyChangeListener(eventHandler);
		initialize();
	}

	private void initialize(){
		addNewButton = new JButton("New");
		deleteButton = new JButton("Delete Selected");
		textFieldSearch = new JTextField(10);
		textFieldSearch.putClientProperty("JTextField.variant", "search");
		table = new EditorScrollTable();
		tableModel = createTableModel();
		table.setModel(tableModel);

		addNewButton.addActionListener(eventHandler);
		deleteButton.addActionListener(eventHandler);
		
		// disable 'add new' button for rate rules temporarily
		if (this instanceof RateRulesDisplayPanel) {
			addNewButton.setEnabled(false);
		}
		
		deleteButton.setEnabled(false);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
		table.getSelectionModel().addListSelectionListener(eventHandler);
	}
	
	@Override
	public void setIssueManager(IssueManager issueManager) {
		tableModel.setIssueManager(issueManager);
	}
	
	public void setSimulationContext(SimulationContext newValue) {
		SimulationContext oldValue = simulationContext;
		simulationContext = newValue;		
		firePropertyChange(PROPERTY_NAME_SIMULATION_CONTEXT, oldValue, newValue);
	}
	
	protected abstract BioModelEditorApplicationRightSideTableModel<T> createTableModel();
	protected abstract void newButtonPressed();
	protected abstract void deleteButtonPressed();
	
	protected void simulationContextChanged() {
		tableModel.setSimulationContext(simulationContext);
	}

	protected void tableSelectionChanged() {
		int[] rows = table.getSelectedRows();
		deleteButton.setEnabled(rows != null && rows.length > 0 && (rows.length > 1 || tableModel.getValueAt(rows[0]) != null));
		setSelectedObjectsFromTable(table, tableModel);
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		setTableSelections(selectedObjects, table, tableModel);
	}

	private void searchTable() {
		String text = textFieldSearch.getText();
		tableModel.setSearchText(text);
	}
}

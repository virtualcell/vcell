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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public abstract class BioModelEditorRightSidePanel<T> extends DocumentEditorSubPanel implements Model.Owner {
	protected static final String PROPERTY_NAME_BIO_MODEL = "bioModel";
	
	protected JButton addNewButton = null;
	protected JButton deleteButton = null;
	protected EditorScrollTable table;
	protected BioModelEditorRightSideTableModel<T> tableModel = null;
	protected BioModel bioModel;
	protected JTextField textFieldSearch = null;

	private InternalEventHandler eventHandler = new InternalEventHandler();

	private class InternalEventHandler implements ActionListener, PropertyChangeListener, DocumentListener, ListSelectionListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditorRightSidePanel.this && evt.getPropertyName().equals(PROPERTY_NAME_BIO_MODEL)) {
				bioModelChange();
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
		
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == addNewButton) {
				newButtonPressed();
			} else if (e.getSource() == deleteButton) {
				deleteButtonPressed();
			}
		}
		
		public void valueChanged(ListSelectionEvent e) {
			if (bioModel == null || e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == table.getSelectionModel()) {
				tableSelectionChanged();
			}
		}
	}
	
	public BioModelEditorRightSidePanel() {
		super();
		addPropertyChangeListener(eventHandler);
		initialize();
	}

	private void initialize(){
		addNewButton = new JButton("New Application");
		deleteButton = new JButton("Delete");
		textFieldSearch = new JTextField(10);
		textFieldSearch.putClientProperty("JTextField.variant", "search");
		table = new EditorScrollTable();
		tableModel = createTableModel();
		table.setModel(tableModel);
		table.setDefaultRenderer(Structure.class, new DefaultScrollTableCellRenderer(){

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				if (value instanceof Structure) {
					setText(((Structure)value).getName());
				}
				return this;
			}			
		});
		
		addNewButton.addActionListener(eventHandler);
		deleteButton.addActionListener(eventHandler);
		deleteButton.setEnabled(false);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
		table.getSelectionModel().addListSelectionListener(eventHandler);
	}
	
	public void setBioModel(BioModel newValue) {
		BioModel oldValue = bioModel;
		bioModel = newValue;		
		firePropertyChange(PROPERTY_NAME_BIO_MODEL, oldValue, newValue);
	}
	
	public Model getModel() {
		return bioModel.getModel();
	}
	
	private void searchTable() {
		String text = textFieldSearch.getText();
		tableModel.setSearchText(text);
	}
	
	protected abstract BioModelEditorRightSideTableModel<T> createTableModel();
	protected abstract void newButtonPressed();
	protected abstract void deleteButtonPressed();
	
	protected void bioModelChange() {
		tableModel.setBioModel(bioModel);
	}

	protected void tableSelectionChanged() {
		int[] rows = table.getSelectedRows();
		deleteButton.setEnabled(rows != null && rows.length > 0 && (rows.length > 1 || tableModel.getValueAt(rows[0]) != null));
		setSelectedObjectsFromTable(table, tableModel);

	}
}

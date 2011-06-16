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

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

public class DefaultScrollTableActionManager implements ScrollTableActionManager, ActionListener {
	
	protected enum ScrollTableCellEditorType {
		jcheckbox,
		jtextfield,
	}
	
	private class ColumnActionComponent {
		JComponent source = null;
		int column = -1;
		boolean booleanValue;
		ColumnActionComponent(int col, JTextField textField) {
			column = col;
			source = textField;
		}
		ColumnActionComponent(int col, JMenuItem menuItem, boolean checkedValue) {
			column = col;
			source = menuItem;
			booleanValue = checkedValue;
		}
		Object getValue() {
			if (source instanceof JTextField) {
				return ((JTextField) source).getText();
			} 
			return booleanValue;
		}
	}
	
	protected JTable ownerTable = null;
	protected JPopupMenu popupMenu = null;
	protected JSeparator popupMenuSeparator = null;
	protected JLabel popupLabel = null;
	protected JLabel popupTextFieldLabel = null;
	protected Set<Integer> disabledColumnPopups = new HashSet<Integer>();
	protected List<ColumnActionComponent> columnActionComponentList = new ArrayList<ColumnActionComponent>();
	protected JMenu[] columnMenus = null;
	protected int[] selectedRows = null;

	protected DefaultScrollTableActionManager(JTable table) {
		ownerTable = table;
	}

	public void actionPerformed(ActionEvent e) {
		popupMenu.setVisible(false);
		TableModel tableModel = ownerTable.getModel();
		for (ColumnActionComponent cac : columnActionComponentList) {
			if (cac.source == e.getSource()) {
				Object value = cac.getValue();
				for (int row : selectedRows) {
					if (tableModel.isCellEditable(row, cac.column)) {
						tableModel.setValueAt(value, row, cac.column);
					}
				}
				break;
			}
		}		
	}
	
	public void disablePopupAtColumn(int column) {
		disabledColumnPopups.add(column);
	}
		
	public void triggerPopup(MouseEvent mouseEvent) {
		if (!mouseEvent.isPopupTrigger()) {
			return;
		}
		selectedRows = ownerTable.getSelectedRows();
		int clickRow = ownerTable.rowAtPoint(mouseEvent.getPoint());
		boolean bFound = false;
		if (selectedRows != null) {
			for (int row : selectedRows) {
				if (clickRow == row) {
					bFound = true;
					break;
				}
			}
		}
		if (!bFound) {			
			Rectangle rect = ownerTable.getVisibleRect();
			ownerTable.getSelectionModel().setSelectionInterval(clickRow, clickRow);
			ownerTable.scrollRectToVisible(rect);
			selectedRows = ownerTable.getSelectedRows();
		}
		constructPopupMenu();
		if (popupMenu != null && popupMenu.getComponents().length > 1) {
			popupMenu.show(ownerTable,mouseEvent.getX(),mouseEvent.getY());
		}
	}
	
	protected int[] getUniqueColumns() {
		return null;
	}

	protected void constructPopupMenu() {
		TableModel tableModel = ownerTable.getModel();
		int numColumns = tableModel.getColumnCount();
		boolean[] bEditable = new boolean[numColumns];
		boolean bTableEditable = false;
		for (int c = 0; c < numColumns; c ++) {
			for (int r = 0; r < selectedRows.length; r ++) {
				if (tableModel.isCellEditable(selectedRows[r], c)) {
					bEditable[c] = true;
					bTableEditable = true;
					break;
				}
			}
		}
		if (!bTableEditable) {
			return;
		}
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			columnMenus = new JMenu[numColumns];
			popupLabel = new javax.swing.JLabel();
			popupLabel.setText(" Specify Column Value for Selected Row(s)");
		}
		popupMenu.removeAll();
		popupMenu.add(popupLabel);
		int[] uniqueColumns = getUniqueColumns();
		for (int c = 0; c < numColumns; c ++) {
			String columnName = tableModel.getColumnName(c);
			if (disabledColumnPopups.contains(c)) {
				continue;
			}
			if (columnName.equalsIgnoreCase("name")) {
				continue;
			}
			
			boolean bUnique = false;
			if (uniqueColumns != null) {
				for (int uc : uniqueColumns) {
					if (uc == c) {
						bUnique = true;
						break;
					}
				}
			}
			if (bUnique) {
				continue;
			}
			if (bEditable[c]) {
				if (columnMenus[c] == null) {
					Class<?> columnClass = tableModel.getColumnClass(c);
					Component editorComponent = null;
					TableCellEditor cellEditor = ownerTable.getColumnModel().getColumn(c).getCellEditor();
					if (cellEditor == null) {
						cellEditor = ownerTable.getDefaultEditor(columnClass); 
					}
					if (cellEditor instanceof DefaultCellEditor) {
						editorComponent = ((DefaultCellEditor)cellEditor).getComponent();
					}
					if (editorComponent == null || !(editorComponent instanceof JCheckBox) && !(editorComponent instanceof JTextField)) {
						continue;
					}
					ScrollTableCellEditorType editorType = editorComponent instanceof JCheckBox ? ScrollTableCellEditorType.jcheckbox : ScrollTableCellEditorType.jtextfield;
					columnMenus[c] = new JMenu(columnName);
					switch (editorType) {
					case jcheckbox:
						JMenuItem menuItemCheckSelected = new JMenuItem("Checked");
						menuItemCheckSelected.addActionListener(this);
						columnActionComponentList.add(new ColumnActionComponent(c, menuItemCheckSelected, true));
						JMenuItem menuItemUncheckSelected = new JMenuItem("Unchecked");
						menuItemUncheckSelected.addActionListener(this);
						columnActionComponentList.add(new ColumnActionComponent(c, menuItemUncheckSelected, false));
						columnMenus[c].add(menuItemCheckSelected);
						columnMenus[c].add(menuItemUncheckSelected);
						break;
					case jtextfield:
						JTextField textField = new JTextField(5);
						textField.addActionListener(this);
						textField.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(2, 4, 2, 4), textField.getBorder()));
						columnActionComponentList.add(new ColumnActionComponent(c, textField));
						JLabel label = new JLabel(" (Press Enter to commit) ");
						label.setFont(label.getFont().deriveFont(label.getFont().getSize2D() - 1));
						columnMenus[c].add(textField);
						columnMenus[c].add(label);
						break;
					}
				}
				columnMenus[c].setEnabled(true);
			} else {
				if (columnMenus[c] != null) {
					columnMenus[c].setEnabled(false);
				}
			}
			if (columnMenus[c] != null) {
				popupMenu.add(columnMenus[c]);
			}
		}
	}

	public final JTable getOwnerTable() {
		return ownerTable;
	}
}

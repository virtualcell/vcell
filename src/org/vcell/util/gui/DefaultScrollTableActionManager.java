package org.vcell.util.gui;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
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
	
	protected JTable ownerTable = null;
	protected JTextField popupTextField = null;
	protected JPopupMenu popupMenu = null;
	protected JLabel popupMenuTitleLabel = null;
	protected JSeparator popupMenuSeparator = null;
	protected JMenuItem popupMenuItemCheckSelected = null;
	protected JMenuItem popupMenuItemUncheckSelected = null;
	protected JLabel popupLabel1 = null;
	protected JLabel popupTextFieldLabel2 = null;
	protected Set<Integer> disabledColumnPopups = new HashSet<Integer>();
	protected int[] selectedRows = null;
	protected int selectedColumn = -1;

	protected DefaultScrollTableActionManager(JTable table) {
		ownerTable = table;
	}

	public void actionPerformed(ActionEvent e) {
		popupMenu.setVisible(false);
		TableModel dataModel = ownerTable.getModel();
		for (int row : selectedRows) {
			Object value = null;
			if (e.getSource() == popupMenuItemCheckSelected) {
				value = true;
			} else if (e.getSource() == popupMenuItemUncheckSelected) {
				value = false;
			} else if (e.getSource() == popupTextField) {
				value = popupTextField.getText();
			}
			dataModel.setValueAt(value, row, selectedColumn);
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
		TableModel tableModel = ownerTable.getModel();
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
		selectedColumn = ownerTable.columnAtPoint(mouseEvent.getPoint());
		if (selectedColumn < 0 || selectedColumn >= tableModel.getColumnCount()) {
			return;
		}
		if (disabledColumnPopups.contains(selectedColumn)) {
			return;
		}
		String columnName = tableModel.getColumnName(selectedColumn);
		if (columnName.equalsIgnoreCase("name")) {
			return;
		}
		int[] uniqueColumns = getUniqueColumns();
		if (uniqueColumns != null) {
			for (int c : uniqueColumns) {
				if (c == selectedColumn) {
					return;
				}
			}
		}
		Class<?> columnClass = tableModel.getColumnClass(selectedColumn);
		Component editorComponent = null;
		TableCellEditor cellEditor = ownerTable.getColumnModel().getColumn(selectedColumn).getCellEditor();
		if (cellEditor == null) {
			cellEditor = ownerTable.getDefaultEditor(columnClass); 
		}
		if (cellEditor instanceof DefaultCellEditor) {
			editorComponent = ((DefaultCellEditor)cellEditor).getComponent();
		}
		if (editorComponent == null || !(editorComponent instanceof JCheckBox) && !(editorComponent instanceof JTextField)) {
			return;
		}		
		constructPopupMenu(editorComponent instanceof JCheckBox ? ScrollTableCellEditorType.jcheckbox : ScrollTableCellEditorType.jtextfield);
		if (popupMenu.getComponents().length > 0) {
			popupMenu.show(ownerTable,mouseEvent.getX(),mouseEvent.getY());
		}
	}
	
	protected int[] getUniqueColumns() {
		return null;
	}

	protected void constructPopupMenu(ScrollTableCellEditorType editorType) {
		TableModel tableModel = ownerTable.getModel();
		boolean bEditable = false;
		for (int row : selectedRows) {
			if (tableModel.isCellEditable(row, selectedColumn)) {
				bEditable = true;
				break;
			}
		}
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenuTitleLabel = new JLabel();
//			popupMenuTitleLabel.setFont(popupMenuTitleLabel.getFont().deriveFont(Font.BOLD));
			popupMenuSeparator = new JSeparator();
		}
		String columnName = tableModel.getColumnName(selectedColumn);
		popupMenuTitleLabel.setText(columnName);
		popupMenu.removeAll();
		if (popupLabel1 == null) {
			popupLabel1 = new javax.swing.JLabel();
		}
		if (bEditable) {
			popupLabel1.setText(" Set '" + columnName + "' of selected to");
			popupMenu.add(popupLabel1);
			switch (editorType) {
			case jcheckbox:
				if (popupMenuItemCheckSelected == null) {
					popupMenuItemCheckSelected = new JMenuItem("Checked");
					popupMenuItemCheckSelected.addActionListener(this);
					popupMenuItemUncheckSelected = new JMenuItem("Unchecked");
					popupMenuItemUncheckSelected.addActionListener(this);
				}
				popupMenu.add(popupMenuItemCheckSelected);
				popupMenu.add(popupMenuItemUncheckSelected);
				break;
			case jtextfield:
				if (popupTextField == null) {
					popupTextField = new JTextField(5);
					popupTextField.addActionListener(this);
					popupTextField.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(2, 4, 2, 4), popupTextField.getBorder()));
					popupTextFieldLabel2 = new JLabel(" (Press Enter or Return) ");
					popupTextFieldLabel2.setFont(popupTextFieldLabel2.getFont().deriveFont(popupTextFieldLabel2.getFont().getSize2D() - 1));
				}
				popupMenu.add(popupTextField);
				popupMenu.add(popupTextFieldLabel2);
				break;
			}
		}
	}

	public final JTable getOwnerTable() {
		return ownerTable;
	}
}

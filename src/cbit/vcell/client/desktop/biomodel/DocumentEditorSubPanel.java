package cbit.vcell.client.desktop.biomodel;

import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTable;

import org.vcell.util.gui.sorttable.DefaultSortTableModel;

@SuppressWarnings("serial")
public abstract class DocumentEditorSubPanel extends JPanel implements PropertyChangeListener {
	private SelectionManager selectionManager = null;
	
	public DocumentEditorSubPanel() {
		super();
	}
	
	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
		if (selectionManager != null) {
			selectionManager.removePropertyChangeListener(this);
			selectionManager.addPropertyChangeListener(this);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == selectionManager && evt.getPropertyName().equals(SelectionManager.PROPERTY_NAME_SELECTED_OBJECTS)) {
			onSelectedObjectsChange(selectionManager.getSelectedObjects());
		}
	}

	protected abstract void onSelectedObjectsChange(Object[] selectedObjects);
	protected <T> void setSelectedObjectsFromTable(JTable table, DefaultSortTableModel<T> tableModel) {
		int[] rows = table.getSelectedRows();
		Object[] selectedObjects = null;
		if (rows != null) {
			selectedObjects = new Object[rows.length];
			for (int i = 0; i < rows.length; i++) {
				selectedObjects[i] = tableModel.getValueAt(rows[i]);
			}
		}
		setSelectedObjects(selectedObjects);
	}
	protected <T> void setTableSelections(Object[] selectedObjects, JTable table, DefaultSortTableModel<T> tableModel) {
		if (selectedObjects == null || selectedObjects.length == 0) {
			table.clearSelection();
			return;
		}
		Set<Integer> oldSelectionSet = new HashSet<Integer>();
		for (int row : table.getSelectedRows()) {
			oldSelectionSet.add(row);
		}
		Set<Integer> newSelectionSet = new HashSet<Integer>();
		for (Object object : selectedObjects) {
			for (int i = 0; i < tableModel.getDataSize(); i ++) {
				if (tableModel.getValueAt(i) == object) {
					newSelectionSet.add(i);
					break;
				}
			}
		}
		
		Set<Integer> removeSet = new HashSet<Integer>(oldSelectionSet);
		removeSet.removeAll(newSelectionSet);
		Set<Integer> addSet = new HashSet<Integer>(newSelectionSet);
		addSet.removeAll(oldSelectionSet);
		for (int row : removeSet) {
			table.removeRowSelectionInterval(row, row);
		}
		for (int row : addSet) {
			table.addRowSelectionInterval(row, row);
		}
		Rectangle r = table.getCellRect(table.getSelectedRow(), 0, true);
		table.scrollRectToVisible(r);
	}
	protected void setSelectedObjects(Object[] selectedObjects) {
		if (selectionManager != null) {
			selectionManager.setSelectedObjects(selectedObjects);
		}
	}
}

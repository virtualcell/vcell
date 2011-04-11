package cbit.vcell.client.desktop.biomodel;

import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTable;

import cbit.vcell.client.desktop.biomodel.IssueManager.IssueEvent;
import cbit.vcell.client.desktop.biomodel.IssueManager.IssueEventListener;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;

@SuppressWarnings("serial")
public abstract class DocumentEditorSubPanel extends JPanel implements PropertyChangeListener, IssueEventListener {
	protected SelectionManager selectionManager = null;
	protected IssueManager issueManager = null;
	
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

	public void setIssueManager(IssueManager newValue) {
		if (issueManager == newValue) {
			return;
		}
		IssueManager oldValue = issueManager;
		if (oldValue != null) {
			oldValue.removeIssueEventListener(this);
		}
		this.issueManager = newValue;
		if (newValue != null) {
			newValue.removeIssueEventListener(this);
			newValue.addIssueEventListener(this);
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == selectionManager) {
			if (evt.getPropertyName().equals(SelectionManager.PROPERTY_NAME_SELECTED_OBJECTS)) {
				onSelectedObjectsChange(selectionManager.getSelectedObjects());
			} else if (evt.getPropertyName().equals(SelectionManager.PROPERTY_NAME_ACTIVE_VIEW)) {
				onActiveViewChange(selectionManager.getActiveView());
			}
		}
	}

	protected void onActiveViewChange(ActiveView activeView){};

	protected abstract void onSelectedObjectsChange(Object[] selectedObjects);
	protected <T> void setSelectedObjectsFromTable(JTable table, VCellSortTableModel<T> tableModel) {
		int[] rows = table.getSelectedRows();
		ArrayList<Object> selectedObjects = new ArrayList<Object>();
		for (int i = 0; i < rows.length; i++) {
			if (rows[i] < tableModel.getRowCount()) {
				T valueAt = tableModel.getValueAt(rows[i]);
				if (valueAt != null) {
					selectedObjects.add(valueAt);
				}
			}
		}
		setSelectedObjects(selectedObjects.toArray());
	}
	
	public static <T> void setTableSelections(Object[] selectedObjects, JTable table, VCellSortTableModel<T> tableModel) {
		tableModel.setTableSelections(selectedObjects, table);
	}
	protected void setSelectedObjects(Object[] selectedObjects) {
		if (selectionManager != null) {
			selectionManager.setSelectedObjects(selectedObjects);
		}
	}
	
	protected void setActiveView(ActiveView activeView) {
		if (selectionManager != null) {
			selectionManager.setActiveView(activeView);
		}
	}
	
	public void issueChange(IssueEvent issueEvent) {
	}
}

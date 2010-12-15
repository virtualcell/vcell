package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class BioModelEditorSubPanel extends JPanel implements PropertyChangeListener {
	private SelectionManager selectionManager = null;
	
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
	protected void setSelectedObjects(Object[] selectedObjects) {
		selectionManager.setSelectedObjects(selectedObjects);
	}
}

package cbit.vcell.client.desktop.biomodel;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.mapping.SimulationContext;

public class SelectionManager {
	public static class ActiveView implements Matchable {
		private SimulationContext simulationContext;
		private DocumentEditorTreeFolderClass documentEditorTreeFolderClass;
		
		public ActiveView(SimulationContext simulationContext, DocumentEditorTreeFolderClass documentEditorTreeFolderClass) {
			super();
			this.simulationContext = simulationContext;
			this.documentEditorTreeFolderClass = documentEditorTreeFolderClass;
		}
		public final SimulationContext getSimulationContext() {
			return simulationContext;
		}
		public final DocumentEditorTreeFolderClass getDocumentEditorTreeFolderClass() {
			return documentEditorTreeFolderClass;
		}
		public boolean compareEqual(Matchable obj) {
			if (obj instanceof ActiveView) {
				ActiveView activeView = (ActiveView) obj;
				if (simulationContext == activeView.simulationContext && 
					documentEditorTreeFolderClass == null && activeView == null 
					|| documentEditorTreeFolderClass != null && documentEditorTreeFolderClass.equals(activeView.documentEditorTreeFolderClass)
					|| activeView.documentEditorTreeFolderClass != null && activeView.documentEditorTreeFolderClass.equals(documentEditorTreeFolderClass)) {
					return true;
				}
			}
			return false;
		}		
	}
	
	public static final String PROPERTY_NAME_ACTIVE_VIEW = "activeView";
	public static final String PROPERTY_NAME_SELECTED_OBJECTS = "selectedObjects";
	private Object[] selectedObjects = null;
	private ActiveView activeView = null;
	private transient java.beans.PropertyChangeSupport propertyChange;
	private boolean bBusy = false;
	private boolean bActiveViewBusy = false;
	
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	private java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}
	
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}
	
	public final void setSelectedObjects(Object[] newValue) {
		if (bBusy) {
			return;
		}
		bBusy = true;
		try {
			Object[] oldValue = this.selectedObjects;
			if (oldValue == newValue) {
				return;
			}
			if (oldValue != null && newValue != null && oldValue.length == 0 && newValue.length == 0) {
				return;
			}
			this.selectedObjects = newValue;
			firePropertyChange(PROPERTY_NAME_SELECTED_OBJECTS, oldValue, newValue);
		} finally {
			bBusy = false;
		}
	}
	
	public final void setActiveView(ActiveView newValue) {
		if (bActiveViewBusy) {
			return;
		}
		bActiveViewBusy = true;
		try {
			ActiveView oldValue = this.activeView;
			if (Compare.isEqualOrNull(oldValue, newValue)) {
				return;
			}
			this.activeView = newValue;
			firePropertyChange(PROPERTY_NAME_ACTIVE_VIEW, oldValue, newValue);
		} finally {
			bActiveViewBusy = false;
		}
	}
 
	public final Object[] getSelectedObjects() {
		return selectedObjects;
	}

	public final ActiveView getActiveView() {
		return activeView;
	}
}

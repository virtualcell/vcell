package cbit.vcell.client.desktop.biomodel;

public class SelectionManager {
	public static final String PROPERTY_NAME_SELECTED_OBJECTS = "selectedObjects";
	private Object[] selectedObjects = null;
	private transient java.beans.PropertyChangeSupport propertyChange;
	private boolean bBusy = false;
	
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
			this.selectedObjects = newValue;
			firePropertyChange(PROPERTY_NAME_SELECTED_OBJECTS, oldValue, newValue);
		} finally {
			bBusy = false;
		}
	}
 
	public final Object[] getSelectedObjects() {
		return selectedObjects;
	}
}

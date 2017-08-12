package org.vcell.model.rbm.common;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class RbmEventHandler implements Serializable {
	
	private transient PropertyChangeSupport propertyChange;
	private transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private Object eventSource = null;
	
	private RbmEventHandler() { }		// we prohibit usage of default constructor
	public RbmEventHandler(Object eventSource) {
		this.eventSource = eventSource;
	}
	
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
		getVetoPropertyChange().addVetoableChangeListener(listener);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
		getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
	}

	private PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(eventSource);
		};
		return propertyChange;
	}

	private java.beans.VetoableChangeSupport getVetoPropertyChange() {
		if (vetoPropertyChange == null) {
			vetoPropertyChange = new java.beans.VetoableChangeSupport(eventSource);
		};
		return vetoPropertyChange;
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
		getVetoPropertyChange().removeVetoableChangeListener(listener);
	}
}

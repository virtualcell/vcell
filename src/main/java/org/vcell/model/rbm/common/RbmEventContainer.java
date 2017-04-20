package org.vcell.model.rbm.common;

import java.beans.PropertyChangeListener;

public interface RbmEventContainer {
	
	public void addPropertyChangeListener(PropertyChangeListener listener);
	public void addVetoableChangeListener(java.beans.VetoableChangeListener listener);
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue);
	public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException;
	public void removePropertyChangeListener(PropertyChangeListener listener);
	public void removeVetoableChangeListener(java.beans.VetoableChangeListener listener);
}

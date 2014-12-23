package org.vcell.workflow;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public abstract class WorkflowObject implements Serializable {
	
	public static final String PROPERTYNAME_NAME = "name";
	
	private String name;
	private transient PropertyChangeSupport propertyChangeSupport;

	public WorkflowObject(String name){
		this.name = name;
	}
	
	private PropertyChangeSupport getPropertyChangeSupport(){
		if (propertyChangeSupport==null){
			propertyChangeSupport = new PropertyChangeSupport(this);
		}
		return propertyChangeSupport;
	}
	
	private void firePropertyChange(String propertyName, Object oldValue, Object newValue){
		getPropertyChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener){
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener){
		getPropertyChangeSupport().removePropertyChangeListener(listener);
	}
	
	public final String getName(){
		return name;
	}

	public final void setName(String name){
		String oldName = this.name;
		this.name = name;
		firePropertyChange(PROPERTYNAME_NAME, oldName, this.name);
	}

	public abstract String getPath();
	
}

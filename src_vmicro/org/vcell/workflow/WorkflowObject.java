package org.vcell.workflow;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Matchable;

public abstract class WorkflowObject implements Serializable {
	
	public static final String PROPERTYNAME_STATUS = "status";
	public static final String PROPERTYNAME_NAME = "name";
	
	public final static class Status implements Serializable, Matchable {
		public final boolean bOutputsDirty;
		public final boolean bRunning;
		public final Issue[] issues;
		
		public Status(boolean bOutputsDirty, boolean bRunning, Issue[] issues){
			this.bOutputsDirty = bOutputsDirty;
			this.bRunning = bRunning;
			this.issues = issues;
		}

		@Override
		public boolean compareEqual(Matchable obj) {
			if (obj instanceof Status){
				Status other = (Status)obj;
				if (bOutputsDirty != other.bOutputsDirty){
					return false;
				}
				if (bRunning != other.bRunning){
					return false;
				}
				if (!Compare.isEqualOrNull(issues, other.issues)){
					return false;
				}
				return true;
			}
			return false;
		}
		
		@Override
		public String toString(){
			return "WorkflowStatus (outputsDirty,running,num issues) = ("+bOutputsDirty+","+bRunning+","+((issues!=null)?(issues.length):(0))+")";
		}
	}

	private Status status = new Status(true,false,new Issue[0]);
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

	public void setStatus(Status status){
		Status oldStatus = this.status;
		this.status = status;
		firePropertyChange(PROPERTYNAME_STATUS, oldStatus, status);
	};
	
	public Status getStatus(){
		return status;
	}

	public abstract String getPath();
	
}

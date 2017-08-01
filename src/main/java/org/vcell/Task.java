package org.vcell;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingWorker;

public abstract class Task<T, V> extends SwingWorker<T, V> {
	
	public static final String STATE = "state";
	public static final String PROGRESS = "progress";
	public static final String SUBTASK = "subtask";
	
	private V subtask;
	
	public V getSubtask() {
		return subtask;
	}
	
	public void setSubtask(V subtask) {
		V oldSubtask = this.subtask;
		this.subtask = subtask;
		this.firePropertyChange(SUBTASK, oldSubtask, subtask);
	}
	
	public void addDoneListener(PropertyChangeListener l) {
		addPropertyChangeListener(propertyChangeEvent -> {
			if (propertyChangeEvent.getPropertyName().equals(Task.STATE) 
					&& propertyChangeEvent.getNewValue().equals(SwingWorker.StateValue.DONE)) {
				l.propertyChange(propertyChangeEvent);
			}
		});
	}
}

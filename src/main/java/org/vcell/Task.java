package org.vcell;

import javax.swing.SwingWorker;

public abstract class Task<T, V> extends SwingWorker<T, V> {
	
	public static final String STATE = "state";
	public static final String PROGRESS = "progress";
	public static final String SUBTASK = "subtask";
	
	private String subtask;
	
	public String getSubtask() {
		return subtask;
	}
	
	public void setSubtask(String subtask) {
		String oldSubtask = this.subtask;
		this.subtask = subtask;
		this.firePropertyChange(SUBTASK, oldSubtask, subtask);
	}
}

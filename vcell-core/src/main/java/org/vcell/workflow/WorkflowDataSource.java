package org.vcell.workflow;

public interface WorkflowDataSource<T> {
	
	public Class<T> getType();
	
	public String getPath();
	
}

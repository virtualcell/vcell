package org.vcell.workflow;

public interface Repository {
	
	public <T> T getData(WorkflowDataSource<T> dataSource);

	public <T> T getDataWithDefault(WorkflowDataSource<T> dataSource, T defaultValue);

	public <T> boolean isDirty(Workflow workflow, WorkflowDataSource<T> dataSource);

	public <T> void setData(WorkflowDataSource<T> dataSource, T data);

	public boolean isDirty(Workflow workflow, DataInput<? extends Object> dataInput);

}

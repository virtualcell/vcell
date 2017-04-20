package org.vcell.workflow;

import java.util.HashMap;

public class MemoryRepository implements Repository {
	
	private HashMap<WorkflowDataSource, Object> dataMap = new HashMap<WorkflowDataSource, Object>();
	private HashMap<WorkflowDataSource, Boolean> dirtyMap = new HashMap<WorkflowDataSource, Boolean>();

	public MemoryRepository(){
		
	}
	
	@Override
	public <T> T getData(WorkflowDataSource<T> dataSource) {
		return (T)dataMap.get(dataSource);
	}

	@Override
	public <T> T getDataWithDefault(WorkflowDataSource<T> dataSource, T defaultValue) {
		T data = getData(dataSource);
		if (data != null){
			return data;
		}else{
			return defaultValue;
		}
	}

	@Override
	public <T> boolean isDirty(Workflow workflow, WorkflowDataSource<T> dataSource) {
		Boolean bDirty = dirtyMap.get(workflow.getDataSource((DataObject<T>)dataSource));
		if (bDirty == null){
			return true;
		}else{
			return bDirty;
		}
	}

	@Override
	public <T> void setData(WorkflowDataSource<T> dataSource, T data) {
		dataMap.put(dataSource,data);
		dirtyMap.put(dataSource, false);
	}

	@Override
	public boolean isDirty(Workflow workflow, DataInput<? extends Object> dataInput) {
		WorkflowDataSource dataSource = workflow.getDataSource(dataInput);
		if (dataSource == null){
			throw new RuntimeException("dataInput "+dataInput.getPath()+" not bound");
		}
		return isDirty(workflow,dataSource);
	}

}

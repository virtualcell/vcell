package org.vcell.workflow;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vmicro.workflow.data.LocalWorkspace;

public class TaskContext {
	private final Workflow workflow;
	private final LocalWorkspace localWorkspace;
	private final Repository repository;
	
	public TaskContext(Workflow workflow, Repository repository, LocalWorkspace localWorkspace) {
		super();
		this.workflow = workflow;
		this.localWorkspace = localWorkspace;
		this.repository = repository;
	}

	public <T> T getData(DataInput<T> dataInput) {
		WorkflowDataSource<T> dataSource = workflow.getDataSource(dataInput);
		if (dataSource==null){
			DataObject<T> connectionSource = workflow.getConnectorSource(dataInput);
			if (connectionSource == null){
				throw new RuntimeException("dataInput "+workflow.getName()+"."+dataInput.getPath()+" not connected to anything");
			}else{
				throw new RuntimeException("dataInput "+workflow.getName()+"."+dataInput.getPath()+" bound to "+connectionSource.getPath()+" but not connected to data source");
			}
		}
		T data = repository.getData(dataSource);
//		if (data == null){
//			throw new RuntimeException("dataInput "+workflow.getName()+"."+dataInput.getPath()+" depends on datasource "+workflow.getName()+"."+dataSource.getPath()+" with null data");
//		}
		return data;
	}

	public <T> void setData(DataOutput<T> dataSource, T data) {
		repository.setData(dataSource,  data);
	}

	public <T> T getDataWithDefault(DataInput<T> dataInput, T defaultValue) {
		return repository.getDataWithDefault(workflow.getDataSource(dataInput), defaultValue);
	}

	public LocalWorkspace getLocalWorkspace() {
		return localWorkspace;
	}

	public User getDefaultOwner() {
		return localWorkspace.getDefaultOwner();
	}

	public KeyValue createNewKeyValue() {
		return localWorkspace.createNewKeyValue();
	}

	public <T> boolean isConnected(DataInput<T> dataInput) {
		return (workflow.getConnectorSource(dataInput) != null);
	}

	public Repository getRepository() {
		return repository;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public boolean areOutputsDirty(Task task) {
		for (DataOutput output : task.getOutputs()){
			if (repository.isDirty(workflow, output)){
				return true;
			}
		}
		return false;
	}

	public <T> void setParameterValue(WorkflowParameter<T> parameter, T data) {
		repository.setData(parameter, data);
	}
}
package org.vcell.workflow;


public class WorkflowParameter<T> extends DataObject<T> implements WorkflowDataSource<T> {
	
	private Workflow parent;
	
	public WorkflowParameter(Class<T> cls, String name, Workflow parent){
		super(cls, name);
		this.parent = parent;
	}
		
	public Workflow getParent() {
		return parent;
	}

	@Override
	public String getPath() {
		return "Workflow.Parameter( \""+getName()+"\" )";
	}
	
	public String toString(){
		return getPath();
	}
}

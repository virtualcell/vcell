package org.vcell.workflow;


public class DataOutput<T> extends DataObject<T> implements WorkflowDataSource<T> {
	
	private Task parent;
	
	public DataOutput(Class<T> cls, String name, Task parent){
		super(cls,name);
		this.parent = parent;
	}
	
	public Task getParent() {
		return parent;
	}

	@Override
	public String getPath() {
		return getParent().getPath()+".Output( \""+getName()+"\" )";
	}
	
	public String toString(){
		return getPath();
	}
}

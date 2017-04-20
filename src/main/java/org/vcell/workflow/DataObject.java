package org.vcell.workflow;


public abstract class DataObject<T> extends WorkflowObject {
	
	private final Class<T> type;
	
	public DataObject(Class<T> cls, String name){
		super(name);
		this.type = cls;
	}
	
	public Class<T> getType(){
		return type;
	}
}

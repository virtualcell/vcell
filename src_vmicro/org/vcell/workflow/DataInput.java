package org.vcell.workflow;


public class DataInput<T> extends DataObject<T> {
	
	public final Task task;
	public final boolean bOptional;

	public DataInput(Class<T> cls, String name, Task task){
		this(cls,name,task,false);
	}
	
	public DataInput(Class<T> cls, String name, Task task, boolean bOptional){
		super(cls,name);
		this.task = task;
		this.bOptional = bOptional;
	}
	
	public boolean isOptional(){
		return bOptional;
	}
	
	public Task getParent(){
		return task;
	}

	@Override
	public String getPath() {
		return task.getPath()+".Input( \""+getName()+"\" )";
	}
	
	public String toString(){
		return getPath();
	}

}

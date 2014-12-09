package org.vcell.workflow;


public class DataInput<T> extends WorkflowObject implements TypedDataContainer<T>{
	
	public final Task task;
	public final boolean bOptional;
	private Class<T> type;

	private TypedDataContainer<T> source = null;
	
	public DataInput(Class<T> cls, String name, Task task){
		this(cls,name,task,false);
	}
	
	public DataInput(Class<T> cls, String name, Task task, boolean bOptional){
		super(name);
		this.type = cls;
		this.task = task;
		this.bOptional = bOptional;
	}
	
	public void clearSource(){
		this.source = null;
	}
	
	public void setSource(DataInput<T> source){
		this.source = source;
	}
	
	public void setSource(DataHolder<T> source){
		this.source = source;
	}
	
	public TypedDataContainer<T> getSource(){
		return source;
	}
	
	public boolean isOptional(){
		return bOptional;
	}
	
	@Override
	public T getData(){
		if (source!=null){
			return source.getData();
		}else{
			return null;
		}
	}
	
	@Override
	public Class<T> getType(){
		return type;
	}
	
	@Override
	public Task getParent(){
		return task;
	}

	@Override
	public String getPath() {
		return task.getPath()+".Input( \""+getName()+"\" )";
	}

	@Override
	public boolean isDirty() {
		if (source!=null){
			return source.isDirty();
		}else{
			return true;
		}
	}

}

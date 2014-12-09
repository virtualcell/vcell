package org.vcell.workflow;

public interface TypedDataContainer<T> {
	
	public Class<T> getType();
	
	public T getData();
	
	public Object getParent();

	public boolean isDirty();

	public String getName();

}

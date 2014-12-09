package org.vcell.workflow;


public class DataHolderOutputPort<T> extends DataHolder<T> {
	private final DataHolder<T> childDataHolder;
	
	public DataHolderOutputPort(Class<T> cls, String name, Task parent, DataHolder<T> childDataHolder){
		super(cls,name,parent);
		this.childDataHolder = childDataHolder;
	}

	@Override
	public void setData(T data) {
		throw new RuntimeException("cannot set the 'data' DataHolderProxy");
	}

	@Override
	public boolean isDirty() {
		return childDataHolder.isDirty();
	}

	@Override
	public void setDirty() {
		childDataHolder.setDirty();
		//throw new RuntimeException("cannot set the 'dirty' flag of a DataHolderProxy");
	}

	@Override
	public T getData() {
		return childDataHolder.getData();
	}
		
}

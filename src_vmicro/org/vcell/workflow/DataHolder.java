package org.vcell.workflow;


public class DataHolder<T> extends WorkflowObject implements TypedDataContainer<T> {
	
	private transient T data;
	private transient Boolean bDirty;
	private Object parent;
	private final Class<T> type;
	
	public DataHolder(Class<T> cls, String name, Task parent){
		super(name);
		this.type = cls;
		this.parent = parent;
		this.data = null;
		this.bDirty = true;
	}
	
	public DataHolder(Class<T> cls, String name, Workflow parent){
		super(name);
		this.type = cls;
		this.parent = parent;
		this.data = null;
		this.bDirty = true;
	}
	
	public void setData(T data){
		this.data = data;
		this.bDirty = false;
		
		if (parent instanceof Workflow){
			Workflow workflow = (Workflow)parent;
			for (Task task : workflow.getTasks()){
				boolean bUsesInput = false;
				for (DataInput dataInput : task.getInputs()){
					if (dataInput.getSource() == this){
						bUsesInput = true;
					}
				}
				if (bUsesInput){
					for (DataHolder dataOutput : task.getOutputs()){
						dataOutput.setDirty();
					}
				}
			}
			workflow.refreshStatus();
		}
	}
	
	public void setDirtyChildren(DataHolder dataHolder){
		// get dependent tasks and set each of their outputs dirty also
	}

	public boolean isDirty(){
		if (bDirty==null || bDirty.booleanValue()==true){
			return true;
		}
		if (parent!=null && parent instanceof Task){
			for (DataInput<? extends Object> input : ((Task)parent).getInputs()){
				if (input.getSource()!=null && input.getSource().isDirty()){
					return true;
				}
			}
		}
		return false;
	}
	
	public void setDirty(){
		bDirty = true;
	}

	@Override
	public T getData(){
		return data;
	}
	
	@Override
	public Class<T> getType(){
		return type;
	}
	
	@Override
	public Object getParent() {
		return parent;
	}

	@Override
	public String getPath() {
		if (getParent() instanceof Workflow){
			return "Workflow.Parameter( \""+getName()+"\" )";
		}else if (getParent() instanceof Task){
			return ((Task)getParent()).getPath()+".Output( \""+getName()+"\" )";
		}else{
			throw new RuntimeException("path not available, unknown parent");
		}
	}
}

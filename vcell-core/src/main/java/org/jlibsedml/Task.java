package org.jlibsedml;

/**
 * Encapsulates the Task element linking a simulation description to a model.
 *
 */
public class Task extends AbstractTask {

	
	@Override
	public String toString() {
		return "Task [modelReference=" + modelReference + ", name=" + getName()
				+ ", simulationReference=" + simulationReference + ", getId()="
				+ getId() + "]";
	}
	
	 @Override
		public String getElementName() {
			return SEDMLTags.TASK_TAG;
		}

	
	private String modelReference = null;
	private String simulationReference = null;

	/**
	 * 
	 * @param id
	 * @param name - optional, can be null.
	 * @param modelReference
	 * @param simulationReference
	 * @throws IllegalArgumentException if any argument except name is null or empty string.
	 */
	public Task(String id, String name, String modelReference, String simulationReference ) {
		super(id,name);
		if(SEDMLElementFactory.getInstance().isStrictCreation()){
		Assert.checkNoNullArgs(modelReference, simulationReference);
		Assert.stringsNotEmpty(modelReference, simulationReference);
		}
		// store and initialize	
		this.modelReference = modelReference;
		this.simulationReference = simulationReference;
	}

	

	/**
	 * Sets the model reference for this task. This should be the value of the 'id'
	 * attribute of a {@link Model} element.
	 * @param modelReference A non-null <code>String</code>.
	 * @since 1.2.0 
	 */
	public void setModelReference(String modelReference) {
        this.modelReference = modelReference;
    }

	/**
	 *  Sets the simulation reference for this task. This should be the value of the 'id'
     * attribute of a {@link Simulation} element.
	 * @param simulationReference  A non-null <code>String</code>.
	 * @since 1.2.0
	 */
    public void setSimulationReference(String simulationReference) {
        this.simulationReference = simulationReference;
    }

    /**
	 * Getter for the model reference.
	 * @return A <code>String</code> that should correspond to a model's <code>id</code> attribute.
	 */
    @Override
	public String getModelReference() {
		return modelReference;
	}
	
	/**
	 * Getter for the simulation reference.
	 * @return A <code>String</code> that should correspond to a simulation's <code>id</code> attribute.
	 */
    @Override
	public String getSimulationReference() {
		return simulationReference;
	}
	
	public  boolean accept(SEDMLVisitor visitor){
        return visitor.visit(this);
    }
}

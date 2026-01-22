package org.jlibsedml.components.task;

import org.jlibsedml.*;
import org.jlibsedml.components.*;
import org.jlibsedml.components.model.Change;
import org.jlibsedml.components.model.Model;
import org.jlibsedml.components.simulation.Simulation;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the Task element linking a simulation description to a model.
 *
 */
public class Task extends AbstractTask {
    private SId modelReference;
    private SId simulationReference;
	
	 @Override
	 public String getElementName() {
			return SedMLTags.TASK_TAG;
		}

	/**
	 * @param id
	 * @param name - optional, can be null.
	 * @param modelReference
	 * @param simulationReference
	 * @throws IllegalArgumentException if any argument except name is null or empty string.
	 */
	public Task(SId id, String name, SId modelReference, SId simulationReference) {
		super(id,name);
		if(SedMLElementFactory.getInstance().isStrictCreation()){
            SedGeneralClass.checkNoNullArgs(id, modelReference, simulationReference);
		}
		// store and initialize	
		this.modelReference = modelReference;
		this.simulationReference = simulationReference;
	}

    /**
     * Getter for the model reference.
     * @return A <code>String</code> that should correspond to a model's <code>id</code> attribute.
     */
    public SId getModelReference() {
        return this.modelReference;
    }

	/**
	 * Sets the model reference for this task. This should be the value of the 'id'
	 * attribute of a {@link Model} element.
	 * @param modelReference A non-null <code>String</code>.
	 * @since 1.2.0 
	 */
	public void setModelReference(SId modelReference) {
        this.modelReference = modelReference;
    }

    /**
     * Getter for the simulation reference.
     * @return A <code>String</code> that should correspond to a simulation's <code>id</code> attribute.
     */
    public SId getSimulationReference() {
        return this.simulationReference;
    }

	/**
	 *  Sets the simulation reference for this task. This should be the value of the 'id'
     * attribute of a {@link Simulation} element.
	 * @param simulationReference  A non-null <code>String</code>.
	 * @since 1.2.0
	 */
    public void setSimulationReference(SId simulationReference) {
        this.simulationReference = simulationReference;
    }

    /**
     * Returns the parameters that are used in <code>this.equals()</code> to evaluate equality.
     * Needs to be returned as `member_name=value.toString(), ` segments, and it should be appended to a `super` call to this function.
     * <br\>
     * e.g.: `super.parametersToString() + ", " + String.format(...)`
     * @return the parameters and their values, listed in string form
     */
    @OverridingMethodsMustInvokeSuper
    public String parametersToString(){
        List<String> params = new ArrayList<>();
        if (this.modelReference != null) params.add(String.format("modelReference=%s", this.modelReference));
        if (this.simulationReference != null) params.add(String.format("simulationReference=%s", this.simulationReference));
        return super.parametersToString() + ", " + String.join(", ", params);
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        return super.searchFor(idOfElement);
    }
}

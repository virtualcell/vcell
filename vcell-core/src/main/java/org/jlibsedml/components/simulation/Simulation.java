package org.jlibsedml.components.simulation;

import org.jlibsedml.components.SId;
import org.jlibsedml.components.algorithm.Algorithm;
import org.jlibsedml.SedMLElementFactory;
import org.jlibsedml.SEDMLVisitor;
import org.jlibsedml.components.SedGeneralClass;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.output.DataSet;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a description of a simulation.
  * @author anu/radams
 *
 */
public abstract class Simulation extends SedBase {
	protected Algorithm algorithm;

	/**	  
	 * @param id A required <code>String</code> identifier for this element.
	 * @param name - optional, can be null.
	 * @param algorithm - not null.
	 * @throws IllegalArgumentException if  <code>id</code> is <code>null</code> or empty string.
	 */
	public Simulation(SId id, String name, Algorithm algorithm) {
		super(id,name);
		if(SedMLElementFactory.getInstance().isStrictCreation()){
            SedGeneralClass.checkNoNullArgs(id, algorithm);
		}
		this.algorithm = algorithm;
	}

    public Simulation clone() throws CloneNotSupportedException {
        Simulation clone = (Simulation) super.clone();
        clone.algorithm = this.algorithm.clone();
        return clone;
    }
	
	/**
	 * Returns the {@link Algorithm} for this simulation
	 * @return the {@link Algorithm}
	 */
	public Algorithm getAlgorithm() {
		return this.algorithm;
	}

	/**
	 * Setter for the {@link Algorithm} element of this simulation
	 * @param algorithm a non-null {@link Algorithm}.
	 */
	public void setAlgorithm(Algorithm algorithm) {
        SedGeneralClass.checkNoNullArgs(algorithm);
		this.algorithm = algorithm;
	}

	/**
	 * Getter for the type of this simulation.
	 * @return A <code>String</code>
	 */
	public abstract String getSimulationKind();

    /**
     * Returns the parameters that are used in <code>this.equals()</code> to evaluate equality.
     * Needs to be returned as `member_name=value.toString(), ` segments, and it should be appended to a `super` call to this function.
     * <br\>
     * e.g.: `super.parametersToString() + ", " + String.format(...)`
     * @return the parameters and their values, listed in string form
     */
    @OverridingMethodsMustInvokeSuper
    public String parametersToString(){
        String algoString = String.format("algorithm=%s", this.algorithm.getId() != null ? this.algorithm.getId() : '{' + this.algorithm.parametersToString() + '}') ;
        return super.parametersToString() + ", " + algoString;
    }

    @Override
    public SedBase searchFor(SId idOfElement){
        SedBase elementFound = super.searchFor(idOfElement);
        if (elementFound != null) return elementFound;
        elementFound = this.algorithm.searchFor(idOfElement);
        return elementFound;
    }
}
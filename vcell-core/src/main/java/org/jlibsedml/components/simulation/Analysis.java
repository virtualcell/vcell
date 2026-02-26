package org.jlibsedml.components.simulation;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.algorithm.Algorithm;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public class Analysis extends Simulation {
    //TODO THIS CLASS IS NOT PRODUCTION READY!!
    /**
     * @param id        A required <code>String</code> identifier for this element.
     * @param name      - optional, can be null.
     * @param algorithm - not null.
     * @throws IllegalArgumentException if  <code>id</code> is <code>null</code> or empty string.
     */
    public Analysis(SId id, String name, Algorithm algorithm) {
        super(id, name, algorithm);
    }

    public Analysis clone() throws CloneNotSupportedException {
        return (Analysis) super.clone();
    }

    /**
     * Getter for the type of this simulation.
     *
     * @return A <code>String</code>
     */
    @Override
    public String getSimulationKind() {
        return SedMLTags.SIMUL_ANALYSIS;
    }

    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.SIMUL_ANALYSIS;
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
        return super.parametersToString();
    }

    @Override
    public SedBase searchFor(SId idOfElement){
        return super.searchFor(idOfElement);
    }
}

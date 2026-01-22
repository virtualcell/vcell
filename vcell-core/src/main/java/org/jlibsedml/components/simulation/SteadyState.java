package org.jlibsedml.components.simulation;

import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.algorithm.Algorithm;
import org.jlibsedml.SedMLTags;

import java.util.ArrayList;
import java.util.List;

public class SteadyState extends Simulation {

    public SteadyState(SId id, String name, Algorithm algorithm) {
        super(id, name, algorithm);
    }

    @Override
    public String getSimulationKind() {
        return SedMLTags.SIMUL_SS_KIND;
    }

    @Override
    public String getElementName() {
        return SedMLTags.SIM_STEADY_STATE;
    }

    /**
     * Returns the parameters that are used in <code>this.equals()</code> to evaluate equality.
     * Needs to be returned as `member_name=value.toString(), ` segments, and it should be appended to a `super` call to this function.
     * <br\>
     * e.g.: `super.parametersToString() + ", " + String.format(...)`
     * @return the parameters and their values, listed in string form
     */
    @Override
    public String parametersToString(){
        return super.parametersToString();
    }

    @Override
    public SedBase searchFor(SId idOfElement){
        return super.searchFor(idOfElement);
    }

}

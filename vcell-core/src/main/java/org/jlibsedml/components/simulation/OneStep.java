package org.jlibsedml.components.simulation;

import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.algorithm.Algorithm;
import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.output.DataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Represents the OneStep class in SED-ML. This defines  the next output point
 *  that should be reached by the simulation, as an increment from the current point.
 * @since 2.1.0
 */
public class OneStep extends Simulation {
    private double step;
    
    public OneStep(SId id, String name, Algorithm algorithm, double step) {
        super(id, name, algorithm);
        this.setStep(step);
    }

    public OneStep clone() throws CloneNotSupportedException {
        OneStep clone = (OneStep) super.clone();
        clone.step = this.step;
        return clone;
    }

    @Override
    public String getSimulationKind() {
        return SedMLTags.SIMUL_OS_KIND;
    }

    @Override
    public String getElementName() {
        return SedMLTags.SIM_ONE_STEP;
    }
    /**
     * Sets the step.
     * @param step
     */
    public void setStep(double step) {
        this.step = step;
    }

    /**
     * Gets the step
     * @return a double.
     */
    public double getStep() {
        return this.step;
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
        // SEE ORIGINAL PARENT!!
        List<String> params = new ArrayList<>();
        params.add(String.format("stepLength=%f", this.getStep()));
        return super.parametersToString() + ", " + String.join(", ", params);
    }

    @Override
    public SedBase searchFor(SId idOfElement){
        return super.searchFor(idOfElement);
    }
}

package org.jlibsedml.components.simulation;

import org.jlibsedml.SedMLElementFactory;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedGeneralClass;
import org.jlibsedml.components.algorithm.Algorithm;
import org.jlibsedml.SedMLTags;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a basic time course simulation of a model.
 *
 */
public final class UniformTimeCourse extends Simulation {
    private double initialTime;
    private double outputStartTime;
    private double outputEndTime;
    private int numberOfSteps;

    /**
     * This constructor does not perform validation at this stage of the simulation configuration ( for example,
     * that outputStartTime < outputEndTime). This can be checked by validating the SEDML document by a call to
     * <pre>
     *   doc.validate();
     * </pre>
     *
     * @param id                a  mandatory, unique identifier for this element
     * @param name              optional, can be null.
     * @param initialTime       The value of time at which the simulation is said to start from
     * @param outputStartTime   The value of time at which the output of the simulation should start to be collected
     * @param outputEndTime     The value of time after which the output of the simulation should cease being collected
     * @param numberOfSteps     The number of iterations the algorithm should perform to get from <code>outputStartTime</code> to <code>outputEndTime</code>
     */
    public UniformTimeCourse(SId id, String name, double initialTime, double outputStartTime, double outputEndTime, int numberOfSteps, Algorithm algorithm) {
        super(id, name, algorithm);
        if (SedMLElementFactory.getInstance().isStrictCreation()){
            SedGeneralClass.checkNoNullArgs(id, algorithm);
        }
        this.initialTime = initialTime;
        this.outputStartTime = outputStartTime;
        this.outputEndTime = outputEndTime;
        this.numberOfSteps = numberOfSteps;
    }

    /**
     * Getter for the initial time value, i.e., the value of <code>t</code> at the start of the simulation.
     * @return a double
     */
    public double getInitialTime() {
        return this.initialTime;
    }

    /**
     * Sets the initial time for this simulation.
     * @param initialTime A <code>double</code>.
     * @since 1.2.0
     */
    public void setInitialTime(double initialTime) {
        this.initialTime = initialTime;
    }

    /**
     * Getter for the time value at which output should be started
     * @return a double
     */
    public double getOutputStartTime() {
        return this.outputStartTime;
    }

    /**
     * Sets the output start  time for this simulation.
     * @param outputStartTime A <code>double</code>.
     * @since 1.2.0
     */
    public void setOutputStartTime(double outputStartTime) {
        this.outputStartTime = outputStartTime;
    }

    /**
     * Getter for the time value at which output should be terminated.
     * @return a double
     */
    public double getOutputEndTime() {
        return this.outputEndTime;
    }

    /**
     * Sets the output end  time for this simulation.
     * @param outputEndTime A <code>double</code>.
     * @since 1.2.0
     */
    public void setOutputEndTime(double outputEndTime) {
        this.outputEndTime = outputEndTime;
    }

    /**
     * Getter for the number of time-points in  the simulation.
     * @return a double
     */
    public int getNumberOfSteps() {
        return this.numberOfSteps;
    }

    /**
     * Sets the number of output points for  this simulation.
     * @param numberOfSteps A <code>double</code>.
     * @since 1.2.0
     */
    public void setNumberOfSteps(int numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
    }

    /**
     * @return {@link SedMLTags#SIMUL_UTC_KIND}
     */
    public String getSimulationKind() {
        return SedMLTags.SIMUL_UTC_KIND;
    }

    @Override
    public String getElementName() {
        return SedMLTags.SIM_UTC;
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
        params.add(String.format("initialTime=%f", this.initialTime));
        params.add(String.format("outputStartTime=%f", this.outputStartTime));
        params.add(String.format("outputEndTime=%f", this.outputEndTime));
        params.add(String.format("numberOfSteps=%d", this.numberOfSteps));
        return super.parametersToString() + ", " + String.join(", ", params);
    }

    @Override
    public SedBase searchFor(SId idOfElement){
        return super.searchFor(idOfElement);
    }
}	   

package org.jlibsedml;
/**
 * 
 * Represents the OneStep class in SED-ML. This defines  the next output point
 *  that should be reached by the simulation, as an increment from the current point.
 * @since 2.1.0
 */
public class OneStep extends Simulation {

    
    private double step;
    
    public OneStep(String id, String name, Algorithm algorithm, double step) {
        super(id, name, algorithm);
        this.setStep(step);
    }

    @Override
    public String toString() {
        return "OneStep [" + getAlgorithm()
            + ", name=" + getName()
            + ", getId()=" + getId() 
            + ", getStep()=" + getStep() 
            + "]";
    }

    @Override
    public String getSimulationKind() {
        return SEDMLTags.SIMUL_OS_KIND;
    }

    @Override
    public String getElementName() {
        return SEDMLTags.SIM_OS;
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
        return step;
    }
}

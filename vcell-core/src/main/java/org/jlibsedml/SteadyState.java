package org.jlibsedml;

public class SteadyState extends Simulation {

    public SteadyState(String id, String name, Algorithm algorithm) {
        super(id, name, algorithm);
    }

    @Override
    public String toString() {
        return "SteadyState [" + getAlgorithm()
            + ", name=" + getName()
            + ", getId()=" + getId() 
            + "]";
    }
    @Override
    public String getSimulationKind() {
        return SEDMLTags.SIMUL_SS_KIND;
    }

    @Override
    public String getElementName() {
        return SEDMLTags.SIM_SS;
    }

}

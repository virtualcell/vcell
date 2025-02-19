package cbit.vcell.simdata;

import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.solver.*;

public class LangevinPostProcessorOutput {

    private final Simulation sim;
    private final SimulationOwner simOwner;

    private boolean failed = false;
    private boolean isMultiTrial = false;   // springsalad / langevin definition of multi-trial: numTasks > 1

    // the results
    RowColumnResultSet averagesResultSet = null;
    RowColumnResultSet stdResultSet = null;
    RowColumnResultSet minResultSet = null;
    RowColumnResultSet maxResultSet = null;


    public LangevinPostProcessorOutput(Simulation sim, SimulationOwner simOwner) {
        this.sim = sim;
        this.simOwner = simOwner;
    }

    public Simulation getSimulation() {
        return sim;
    }
    public SimulationOwner getSimulationOwner() {
        return simOwner;
    }
    public boolean isFailed() {
        return failed;
    }
    public boolean isMultiTrial() {
        return isMultiTrial;
    }
    public RowColumnResultSet getAveragesResultSet() {
        return averagesResultSet;
    }
    public RowColumnResultSet getStdResultSet() {
        return stdResultSet;
    }
    public RowColumnResultSet getMinResultSet() {
        return minResultSet;
    }
    public RowColumnResultSet getMaxResultSet() {
        return maxResultSet;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }
    public void setMultiTrial(boolean multiTrial) {
        isMultiTrial = multiTrial;
    }
    public void setAveragesResultSet(RowColumnResultSet averagesResultSet) {
        this.averagesResultSet = averagesResultSet;
    }
    public void setStdResultSet(RowColumnResultSet stdResultSet) {
        this.stdResultSet = stdResultSet;
    }
    public void setMinResultSet(RowColumnResultSet minResultSet) {
        this.minResultSet = minResultSet;
    }
    public void setMaxResultSet(RowColumnResultSet maxResultSet) {
        this.maxResultSet = maxResultSet;
    }
}

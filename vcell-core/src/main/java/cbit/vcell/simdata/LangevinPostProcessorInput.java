package cbit.vcell.simdata;

import cbit.vcell.solver.*;
import cbit.vcell.solver.ode.ODESolverResultSet;

import java.util.*;

// post-processing for langevin batch runs is now being handled server-side in the langevin solver
@Deprecated
public class LangevinPostProcessorInput {

    private final Simulation sim;
    private final SimulationOwner simOwner;

    Map<Integer, ODESolverResultSet> odeSolverResultSetMap = new LinkedHashMap<>(); // key = trialIndex

    private boolean failed = false;


    public LangevinPostProcessorInput(Simulation sim, SimulationOwner simOwner) {
        this.sim = sim;
        this.simOwner = simOwner;
    }

    public Simulation getSimulation() {
        return sim;
    }
    public SimulationOwner getSimulationOwner() {
        return simOwner;
    }
    public Map<Integer, ODESolverResultSet> getOdeSolverResultSetMap() {
        return odeSolverResultSetMap;
    }
    public boolean isFailed() {
        return failed;
    }

    public void setOdeSolverResultSetMap(Map<Integer, ODESolverResultSet> odeSolverResultSetMap) {
        this.odeSolverResultSetMap = odeSolverResultSetMap;
    }
    public void setFailed(boolean failed) {
        this.failed = failed;
    }
}

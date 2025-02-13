package cbit.vcell.simdata;

import cbit.vcell.solver.*;

import java.util.*;


public class LangevinPostProcessorInput {

    private final Simulation sim;
    private final SimulationOwner simOwner;

    Map<Integer, ODEDataManager> odeDataManagerMap = new LinkedHashMap<>(); // key = trialIndex

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
    public Map<Integer, ODEDataManager> getOdeDataManagerMap() {
        return odeDataManagerMap;
    }
    public boolean isFailed() {
        return failed;
    }

    public void setOdeDataManagerMap(Map<Integer, ODEDataManager> odeDataManagerMap) {
        this.odeDataManagerMap = odeDataManagerMap;
    }
    public void setFailed(boolean failed) {
        this.failed = failed;
    }
}

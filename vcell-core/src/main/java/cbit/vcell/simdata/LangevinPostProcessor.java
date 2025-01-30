package cbit.vcell.simdata;

import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.solver.*;
import cbit.vcell.solver.ode.ODESimData;

import java.util.ArrayList;
import java.util.Hashtable;

public class LangevinPostProcessor {

    public static final String FAILURE_KEY = "FAILURE_KEY";
	public static final String SIMULATION_KEY = "SIMULATION_KEY";
    public static final String SIMULATION_OWNER = "SIMULATION_OWNER";

    boolean failure;
    Simulation sim;
    SimulationOwner simOwner;

    public void postProcessLangevinResults(Hashtable<String, Object> hashTable) {

        failure = (boolean) hashTable.get(FAILURE_KEY);
        sim = (Simulation)hashTable.get(SIMULATION_KEY);
        simOwner = (SimulationOwner)hashTable.get(SIMULATION_OWNER);;

        if(sim.getVersion() == null) {
            throw new RuntimeException("Missing Version.");
        }
        if(sim.getSimulationInfo() == null) {
            throw new RuntimeException("Missing Simulation Info.");
        }

        retrieveLangevinResultsTask();
        calculateLangevinAveragesTask();
        calculateLangevinAdvancedStatisticsTask();
    }

    private void retrieveLangevinResultsTask() {
        SolverTaskDescription std = sim.getSolverTaskDescription();
        int numTrials = std.getNumTrials();
        VCSimulationIdentifier vcSimulationIdentifier = sim.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
        MathOverrides mathOverrides = sim.getMathOverrides();
        int sizeOverrides = mathOverrides.getSize();
        int scanCount = mathOverrides.getScanCount();

        System.out.println(" --- " + sim.getName() + ", numTrials = " + numTrials);
        System.out.println(" --- " + sim.getName() + ", jobCount" + sim.getJobCount());
        System.out.println(" --- " + sim.getName() + ", sizeOverrides = " + sizeOverrides);
        System.out.println(" --- " + sim.getName() + ", scanCount = " + scanCount);

        SimulationInfo simInfo = sim.getSimulationInfo();
        VCSimulationIdentifier asi = simInfo.getAuthoritativeVCSimulationIdentifier();
        VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(asi, 0);

//        ODEDataManager dm = (ODEDataManager)getDocumentWindowManager().getRequestManager().getDataManager(null, vcSimulationDataIdentifier, false);
//        ODESimData osd = (ODESimData)dm.getODESolverResultSet();


        System.out.println(" ------------------------------------");

    }
    private void calculateLangevinAveragesTask() {

    }
    private void calculateLangevinAdvancedStatisticsTask() {

    }

//    public void postProcessLangevinResults(Simulation sim) {
//
//        final String FAILURE_KEY = "FAILURE_KEY";
//        final String SIMULATION_KEY = "SIMULATION_KEY";
//        Hashtable<String, Object> hashTable = new Hashtable<String, Object>();
//        hashTable.put(FAILURE_KEY, false);
//        hashTable.put(SIMULATION_KEY, sim);
//
//        ArrayList<AsynchClientTask> taskList = new ArrayList<AsynchClientTask>();
//        AsynchClientTask retrieveLangevinResultsTask = new AsynchClientTask("Retrieving results", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING)  {
//            public void run(Hashtable<String, Object> hashTable) throws Exception {
//                boolean failure = (boolean) hashTable.get(FAILURE_KEY);
//                SolverTaskDescription std = sim.getSolverTaskDescription();
//                int numTrials = std.getNumTrials();
//                System.out.println(sim.getName() + ", numTrials = " + numTrials);
////			LangevinSimulationOptions lso = std.getLangevinSimulationOptions();
//                final VCSimulationIdentifier vcSimulationIdentifier = sim.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
//                SimulationOwner simOwner = getSimWorkspace().getSimulationOwner();
//                Simulation allSims[] = simOwner.getSimulations();
//                for (Simulation simCandidate : allSims) {
//                    if (simCandidate.getName().startsWith(sim.getName())) {
//                        System.out.println(" --- " + simCandidate.getName() + ", jobCount" + simCandidate.getJobCount());
//                        MathOverrides mathOverrides = simCandidate.getMathOverrides();
//                        int sizeOverrides = mathOverrides.getSize();
//                        int scanCount = mathOverrides.getScanCount();
//                        System.out.println(" --- " + sim.getName() + ", sizeOverrides = " + sizeOverrides);
//                        System.out.println(" --- " + sim.getName() + ", scanCount = " + scanCount);
//
//                        ExportSpecs.ExportParamScanInfo es = ExportSpecs.getParamScanInfo(simCandidate,0);
//                        ExportSpecs.ExportParamScanInfo es1 = ExportSpecs.getParamScanInfo(simCandidate,1);
//                        ExportSpecs.ExportParamScanInfo es2 = ExportSpecs.getParamScanInfo(simCandidate,2);
//
//                        if(sim.getVersion() == null) {
//                            throw new RuntimeException("Missing Version.");
//                        }
//                        SimulationInfo simInfo = sim.getSimulationInfo();
//                        if(simInfo == null) {
//                            throw new RuntimeException("Missing Simulation Info.");
//                        }
//
//                        VCSimulationIdentifier asi = simInfo.getAuthoritativeVCSimulationIdentifier();
//                        VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(asi, 0);
//
//                        ODEDataManager dm = (ODEDataManager)getDocumentWindowManager().getRequestManager().getDataManager(outputContext, vcSimulationDataIdentifier, false);
//                        ODESimData osd = (ODESimData)dm.getODESolverResultSet();
//
//
//                        System.out.println(" ------------------------------------");
//                    }
//                }
//            }
//        };
//        AsynchClientTask calculateLangevinAveragesTask = new AsynchClientTask("Retrieving results", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING)  {
//            public void run(Hashtable<String, Object> hashTable) throws Exception {
//                boolean failure = (boolean)hashTable.get(FAILURE_KEY);
//
//            }
//        };
//        AsynchClientTask calculateLangevinAdvancedStatisticsTask = new AsynchClientTask("Retrieving results", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING)  {
//            public void run(Hashtable<String, Object> hashTable) throws Exception {
//                boolean failure = (boolean)hashTable.get(FAILURE_KEY);
//
//            }
//        };
//        taskList.add(retrieveLangevinResultsTask);
//        taskList.add(calculateLangevinAveragesTask);
//        taskList.add(calculateLangevinAdvancedStatisticsTask);
//        AsynchClientTask[] taskArray = new AsynchClientTask[taskList.size()];
//        taskList.toArray(taskArray);
//        ClientTaskDispatcher.dispatch(getDocumentWindowManager().getComponent(), hashTable, taskArray, false, true, null);
//
//    }

}

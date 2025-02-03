package cbit.vcell.simdata;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.solver.*;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import org.vcell.util.DataAccessException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.SimulationVersion;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class LangevinPostProcessor {

    public static final String FAILURE_KEY = "FAILURE_KEY";
	public static final String SIMULATION_KEY = "SIMULATION_KEY";
    public static final String SIMULATION_OWNER_KEY = "SIMULATION_OWNER_KEY";
    public static final String ODE_SIM_DATA_MAP_KEY = "ODE_SIM_DATA_MAP_KEY";
    public static final String LANGEVIN_MULTI_TRIAL_KEY = "LANGEVIN_MULTI_TRIAL_KEY";
    Hashtable<String, Object> hashTable;

    boolean isMultiTrial = false;   // springsalad / langevin definition of multi-trial: numTasks > 1
    boolean failure;
    Simulation sim;
    SimulationOwner simOwner;
    Map<Integer, ODEDataManager> odeDataManagerMap;

    // the results
    ODESolverResultSet averagesResultSet;
    ODESolverResultSet atdResultSet;
    ODESolverResultSet minResultSet;
    ODESolverResultSet maxResultSet;

    public void postProcessLangevinResults(Hashtable<String, Object> aHashTable) throws DataAccessException {

        this.hashTable = aHashTable;
        failure = (boolean) hashTable.get(FAILURE_KEY);
        sim = (Simulation)hashTable.get(SIMULATION_KEY);
        simOwner = (SimulationOwner)hashTable.get(SIMULATION_OWNER_KEY);

        // key = trial index, value = simulation results (ODESimData object) for that trial
        odeDataManagerMap = (Map<Integer, ODEDataManager>)hashTable.get(ODE_SIM_DATA_MAP_KEY);

        ODEDataManager tempODEDataManager0 = odeDataManagerMap.get(0);
        ODEDataManager tempODEDataManager1 = odeDataManagerMap.get(1);
        ODESimData tempODESimData = (ODESimData)tempODEDataManager0.getODESolverResultSet();
        String format = tempODESimData.getFormatID();
        String mathName = tempODESimData.getMathName();     // should be different instances?

        // sanity check: shouldn't be, that only works for non-spatial stochastic where things are done differently
        System.out.println("isGibsonMultiTrial: " + tempODEDataManager0.getODESolverResultSet().isMultiTrialData());

        // the
        averagesResultSet = new ODESolverResultSet(tempODEDataManager0.getODESolverResultSet());
        atdResultSet = new ODESolverResultSet(tempODEDataManager0.getODESolverResultSet());
        minResultSet = new ODESolverResultSet(tempODEDataManager0.getODESolverResultSet());
        maxResultSet = new ODESolverResultSet(tempODEDataManager0.getODESolverResultSet());

        initializeResultSetValues(averagesResultSet);

        if(failure) {
            return;
        }

        calculateLangevinAveragesTask();
        calculateLangevinAdvancedStatisticsTask();
    }

    private void initializeResultSetValues(ODESolverResultSet osrs) {
        int columnDescriptionCount = osrs.getColumnDescriptionsCount();
        int rowCount = osrs.getRowCount();
        List<double[]> rows = osrs.getRows();

        System.out.println("post process langevin");
    }

    private void calculateLangevinAveragesTask() {
        SolverTaskDescription std = sim.getSolverTaskDescription();
        int numTrials = std.getNumTrials();
        isMultiTrial = true;
        hashTable.put(LANGEVIN_MULTI_TRIAL_KEY, isMultiTrial);

        SimulationInfo simInfo = sim.getSimulationInfo();
        VCSimulationIdentifier vcSimulationIdentifier = simInfo.getAuthoritativeVCSimulationIdentifier();
        MathOverrides mathOverrides = sim.getMathOverrides();
        SimulationVersion simVersion = simInfo.getSimulationVersion();

        // TODO: go through time series and compute averages, erc

        System.out.println(" ------------------------------------");

    }

    private void calculateLangevinAdvancedStatisticsTask() {

    }

//    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
//    private static File createDirFile(SimulationContext simulationContext){
//        String userid = simulationContext.getBioModel().getVersion().getOwner().getName();
//        String simContextDirName =
//                TokenMangler.fixTokenStrict(userid)+"-"+
//                        TokenMangler.fixTokenStrict(simulationContext.getBioModel().getName())+"-"+
//                        TokenMangler.fixTokenStrict(simulationContext.getName())+"-"+
//                        TokenMangler.fixTokenStrict(simpleDateFormat.format(simulationContext.getBioModel().getVersion().getDate()));
////		simContextDirName = TokenMangler.fixTokenStrict(simContextDirName);
//        File dirFile = new File("C:\\temp\\ruleBasedTestDir\\"+simContextDirName);
//        if(!dirFile.exists()){
//            dirFile.mkdirs();
//        }
//        return dirFile;
//    }

}

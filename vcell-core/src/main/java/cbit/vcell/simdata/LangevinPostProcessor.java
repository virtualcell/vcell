package cbit.vcell.simdata;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.solver.*;
import cbit.vcell.solver.ode.ODESimData;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.SimulationVersion;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

public class LangevinPostProcessor {

    public static final String FAILURE_KEY = "FAILURE_KEY";
	public static final String SIMULATION_KEY = "SIMULATION_KEY";
    public static final String SIMULATION_OWNER_KEY = "SIMULATION_OWNER_KEY";
    public static final String ODE_SIM_DATA_MAP_KEY = "ODE_SIM_DATA_MAP_KEY";

    boolean failure;
    Simulation sim;
    SimulationOwner simOwner;
    Map<Integer, ODESimData> odeSimDataMap;

    public void postProcessLangevinResults(Hashtable<String, Object> hashTable) {

        failure = (boolean) hashTable.get(FAILURE_KEY);
        sim = (Simulation)hashTable.get(SIMULATION_KEY);
        simOwner = (SimulationOwner)hashTable.get(SIMULATION_OWNER_KEY);
        odeSimDataMap = (Map<Integer, ODESimData>)hashTable.get(ODE_SIM_DATA_MAP_KEY);

        if(failure) {
            return;
        }

        calculateLangevinAveragesTask();
        calculateLangevinAdvancedStatisticsTask();
    }

    private void calculateLangevinAveragesTask() {
        SolverTaskDescription std = sim.getSolverTaskDescription();
        int numTrials = std.getNumTrials();
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

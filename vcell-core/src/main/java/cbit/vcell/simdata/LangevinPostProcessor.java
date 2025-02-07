package cbit.vcell.simdata;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.solver.*;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.vcell.util.DataAccessException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.SimulationVersion;

import javax.swing.*;
import java.awt.*;
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
    RowColumnResultSet averagesResultSet;
    RowColumnResultSet stdResultSet;
    RowColumnResultSet minResultSet;
    RowColumnResultSet maxResultSet;

    public void postProcessLangevinResults(Hashtable<String, Object> aHashTable) throws DataAccessException {

        this.hashTable = aHashTable;
        failure = (boolean) hashTable.get(FAILURE_KEY);
        sim = (Simulation)hashTable.get(SIMULATION_KEY);
        simOwner = (SimulationOwner)hashTable.get(SIMULATION_OWNER_KEY);

        // key = trial index, value = simulation results (ODESimData object) for that trial
        odeDataManagerMap = (Map<Integer, ODEDataManager>)hashTable.get(ODE_SIM_DATA_MAP_KEY);

        SolverTaskDescription std = sim.getSolverTaskDescription();
        int numTrials = std.getNumTrials();
        isMultiTrial = numTrials > 1 ? true : false;
        hashTable.put(LANGEVIN_MULTI_TRIAL_KEY, isMultiTrial);

        // probably useless at this point
        SimulationInfo simInfo = sim.getSimulationInfo();
        VCSimulationIdentifier vcSimulationIdentifier = simInfo.getAuthoritativeVCSimulationIdentifier();
        MathOverrides mathOverrides = sim.getMathOverrides();
        SimulationVersion simVersion = simInfo.getSimulationVersion();

        ODEDataManager tempODEDataManager = odeDataManagerMap.get(0);
//        ODEDataManager tempODEDataManager1 = odeDataManagerMap.get(1);
        ODESimData tempODESimData = (ODESimData)tempODEDataManager.getODESolverResultSet();
        String format = tempODESimData.getFormatID();
        String mathName = tempODESimData.getMathName();     // should be different instances?

        // sanity check: shouldn't be, that only works for non-spatial stochastic where things are done differently
        System.out.println("isGibsonMultiTrial: " + tempODEDataManager.getODESolverResultSet().isMultiTrialData());

        averagesResultSet = RowColumnResultSet.deepCopy(tempODEDataManager.getODESolverResultSet(), RowColumnResultSet.DuplicateMode.ZeroInitialize);
        stdResultSet = RowColumnResultSet.deepCopy(tempODEDataManager.getODESolverResultSet(), RowColumnResultSet.DuplicateMode.ZeroInitialize);
        minResultSet = RowColumnResultSet.deepCopy(tempODEDataManager.getODESolverResultSet(), RowColumnResultSet.DuplicateMode.CopyValues);
        maxResultSet = RowColumnResultSet.deepCopy(tempODEDataManager.getODESolverResultSet(), RowColumnResultSet.DuplicateMode.CopyValues);

        // we leave the min and max initialized with whatever the first trial has and adjust as we go through the other trials
        if(failure) {
            return;
        }

        calculateLangevinPrimaryStatistics();   // averages, standard deviation, min, max
        calculateLangevinAdvancedStatistics();
    }

    private void calculateLangevinPrimaryStatistics() throws DataAccessException {

        int numTrials = odeDataManagerMap.size();
        for(int trialIndex = 0; trialIndex < numTrials; trialIndex++) {
            ODEDataManager sourceOdm = odeDataManagerMap.get(trialIndex);
            ODESolverResultSet sourceOsrs = sourceOdm.getODESolverResultSet();
            int rowCount = sourceOsrs.getRowCount();
            for (int row = 0; row < rowCount; row++) {
                double[] sourceRowData = sourceOsrs.getRow(row);
                double[] averageRowData = averagesResultSet.getRow(row);    // destination average
                double[] minRowData = minResultSet.getRow(row);             // destination min
                double[] maxRowData = maxResultSet.getRow(row);             // destination max

                for (int i = 0; i < averageRowData.length; i++) {
                    ColumnDescription cd = averagesResultSet.getColumnDescriptions(i);
                    String name = cd.getName();
                    if (name.equals("t")) {
                        continue;
                    }
                    averageRowData[i] += sourceRowData[i] / numTrials;
                    if (minRowData[i] > sourceRowData[i]) {
                        minRowData[i] = sourceRowData[i];
                    }
                    if (maxRowData[i] < sourceRowData[i]) {
                        maxRowData[i] = sourceRowData[i];
                    }
                }
            }
        }

        for(int trialIndex = 0; trialIndex < numTrials; trialIndex++) {
            ODEDataManager sourceOdm = odeDataManagerMap.get(trialIndex);
            ODESolverResultSet sourceOsrs = sourceOdm.getODESolverResultSet();
            int rowCount = sourceOsrs.getRowCount();
            for (int row = 0; row < rowCount; row++) {
                double[] sourceRowData = sourceOsrs.getRow(row);
                double[] averageRowData = averagesResultSet.getRow(row);
                double[] stdRowData = stdResultSet.getRow(row);    // destination std

                for (int i = 0; i < averageRowData.length; i++) {
                    ColumnDescription cd = averagesResultSet.getColumnDescriptions(i);
                    String name = cd.getName();
                    if (name.equals("t")) {
                        continue;
                    }

                    double variance = Math.pow(sourceRowData[i] - averageRowData[i], 2);
                    stdRowData[i] += variance / numTrials;
                }
            }
        }

        int rowCount = stdResultSet.getRowCount();
        for (int row = 0; row < rowCount; row++) {
            double[] stdRowData = stdResultSet.getRow(row);
            for (int i = 0; i < stdRowData.length; i++) {
                ColumnDescription cd = stdResultSet.getColumnDescriptions(i);
                String name = cd.getName();
                if (name.equals("t")) {
                    continue;
                }
                double variance = stdRowData[i];
                stdRowData[i] = Math.sqrt(variance);
            }
        }
        System.out.println(" ------------------------------------");
    }

    private void calculateLangevinAdvancedStatistics() {

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

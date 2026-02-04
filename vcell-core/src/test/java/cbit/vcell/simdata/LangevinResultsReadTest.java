package cbit.vcell.simdata;

import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.DataSetControllerProvider;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.ODESolverResultSet;


import com.google.common.io.Resources;
import org.junit.jupiter.api.*;
import org.vcell.util.CacheException;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.LocalVCDataIdentifier;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class LangevinResultsReadTest {

    private static File primaryDir; // temporary working dir for this test
    private static File localSimDir;

    // the files are in vcell-core/src/test/resources  /cbit/vcell/simdata/langevin/batch
    private static File ida_avg_file;
    private static File ida_min_file;
    private static File ida_max_file;
    private static File ida_std_file;
    private static File clusters_counts_file;
    private static File clusters_mean_file;
    private static File clusters_overall_file;
    private static File functions_file;
    private static File simtask_xml_file;
    private static File langevin_input_file;
    private static File messaging_config_file;

    private static File euler_log_file;
    private static File euler_ode_file;

    // results for first task in the batch, named to match single run file (ending in "_") ex: SimID_303404574_0_
    private static File ida_0_file;
    // log produced by the solver for first task in the batch, named using the normal convention ex: SimID_303404574_0_0.log
    private static File log_0_file;
    // general default ida data log file, contains:
    //    IDAData logfile
    //    IDAData text format version 1
    //    SimID_303404574_0_.ida
    private static File log_data_file;

    // resource location of files for various test cases
    private static String resourceBase = null;

    @BeforeAll
    public static void setUp() throws IOException {

        Path tempDir = Files.createTempDirectory("langevin_");
        primaryDir = tempDir.toFile();
        // If you want it to delete on JVM exit as a fallback:
        primaryDir.deleteOnExit();

        // Create subdirectory "temp" inside primaryDir
        localSimDir = new File(primaryDir, "temp");
        if (!localSimDir.mkdirs()) {
            throw new IOException("Failed to create subdirectory: " + localSimDir.getAbsolutePath());
        }

        resourceBase = "cbit/vcell/simdata/langevin/batch/";
        functions_file = copyToPrimaryDir("SimID_303404574_0_.functions");
        ida_0_file = copyToPrimaryDir("SimID_303404574_0_.ida");    // results for batch run0 (or for single run)
        log_0_file = copyToPrimaryDir("SimID_303404574_0_0.log");   // langevin-made log for run 0
        log_data_file = copyToPrimaryDir("SimID_303404574_0_.log"); // general log made by preprocessor
        langevin_input_file = copyToPrimaryDir("SimID_303404574_0_.langevinInput");
        messaging_config_file = copyToPrimaryDir("SimID_303404574_0_.langevinMessagingConfig");
        simtask_xml_file = copyToPrimaryDir("SimID_303404574_0__0.simtask.xml");

        ida_avg_file = copyToPrimaryDir("SimID_303404574_0__Avg.ida");
        clusters_counts_file = copyToPrimaryDir("SimID_303404574_0__clusters_counts.csv");
        clusters_mean_file = copyToPrimaryDir("SimID_303404574_0__clusters_mean.csv");
        clusters_overall_file = copyToPrimaryDir("SimID_303404574_0__clusters_overall.csv");
        ida_max_file = copyToPrimaryDir("SimID_303404574_0__Max.ida");
        ida_min_file = copyToPrimaryDir("SimID_303404574_0__Min.ida");
        ida_std_file = copyToPrimaryDir("SimID_303404574_0__Std.ida");

        resourceBase = "cbit/vcell/simdata/langevin/euler/";
        euler_log_file = copyToPrimaryDir("SimID_303690651_0_.log");
        euler_ode_file = copyToPrimaryDir("SimID_303690651_0_.ode");


//        ida_1_File = File.createTempFile("SimID_284673710_0_", ".ida");
//        Resources.asByteSource(Resources.getResource("cbit/vcell/simdata/SimID_284673710_0_.ida"))
//                .copyTo(com.google.common.io.Files.asByteSink(ida_1_File));

    }

    @AfterAll
    public static void tearDown() {

        deleteRecursively(primaryDir);
//        ida_1_File.delete();
   }


    @Test
    public void testReadDataLangevinBatch() throws IOException, DataAccessException, CacheException {

        String simID = "303404574";
        KeyValue key = new KeyValue(simID);
        User usr = new User("temp",key);
        VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(key, usr);

        VCDataIdentifier vCDataIdentifier = new VCSimulationDataIdentifier(vcSimID, 0);
        VCData vcData = new SimulationData(vCDataIdentifier, primaryDir, localSimDir, null);
        SimulationData simData = (SimulationData)vcData;
        String identifier = simData.getDataIdentifierSafe();
        assertTrue(identifier.equals(SimDataConstants.IDA_DATA_IDENTIFIER), "Langevin simulation should be IDA data");
        assertFalse(identifier.equals(SimDataConstants.ODE_DATA_IDENTIFIER), "Langevin simulation should not be ODE data");

        Cachetable aCacheTable = new Cachetable(10 * Cachetable.minute, 100000);
        aCacheTable.put(vCDataIdentifier, vcData);
        DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(aCacheTable, primaryDir, localSimDir);

        LangevinBatchResultSet lbrs = dataSetControllerImpl.getLangevinBatchResultSet(vCDataIdentifier);

        assertNotNull(lbrs, "Result set should not be null");

        ODEDataInfo odeDataInfo = lbrs.getOdeDataInfo();
        assertNotNull(odeDataInfo, "ODEDataInfo should not be null");
        assertTrue(odeDataInfo.getSimID().contains(simID), "ODEDataInfo SimID should contain the simulation ID");

        // --- Avg data loaded ---
        ODESimData odeSimDataAvg = lbrs.getOdeSimDataAvg();
        assertNotNull(odeSimDataAvg, "Avg ODE data should be loaded");
        assertTrue(odeSimDataAvg.getMathName().contains(simID), "Avg ODE data mathName should contain the simulation ID");
        // --- Minimal structural checks ---
        assertTrue(odeSimDataAvg.getDataColumnCount() > 0, "Avg ODE data should have at least one column");
        assertTrue(odeSimDataAvg.getRowCount() > 0, "Avg ODE data should have at least one row");

        // Cluster Counts data loaded
        ODESimData odeSimDataClusterCounts = lbrs.getOdeSimDataClusterCounts();
        assertNotNull(odeSimDataClusterCounts, "Cluster Counts ODE data should be loaded");
        assertTrue(odeSimDataClusterCounts.getMathName().contains(simID), "Cluster Counts ODE data mathName should contain the simulation ID");
        // --- Minimal structural checks ---
        assertTrue(odeSimDataClusterCounts.getDataColumnCount() > 0, "Cluster Counts ODE data should have at least one column");
        assertTrue(odeSimDataClusterCounts.getRowCount() > 0, "Cluster Counts ODE data should have at least one row");

    }

    // we should not try to read euler results using LangevinBatchResultSet
    @Test
    public void testReadDataEulerBatch() throws IOException, DataAccessException, CacheException {

        String simID = "303690651";
        KeyValue key = new KeyValue(simID);
        User usr = new User("temp",key);
        VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(key, usr);

        VCDataIdentifier vCDataIdentifier = new VCSimulationDataIdentifier(vcSimID, 0);
        VCData vcData = new SimulationData(vCDataIdentifier, primaryDir, localSimDir, null);
        SimulationData simData = (SimulationData)vcData;
        String identifier = simData.getDataIdentifierSafe();
        assertFalse(identifier.equals(SimDataConstants.IDA_DATA_IDENTIFIER), "Langevin simulation should not be IDA data");
        assertTrue(identifier.equals(SimDataConstants.ODE_DATA_IDENTIFIER), "Langevin simulation should be ODE data");

        Cachetable aCacheTable = new Cachetable(10 * Cachetable.minute, 100000);
        aCacheTable.put(vCDataIdentifier, vcData);
        DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(aCacheTable, primaryDir, localSimDir);

        LangevinBatchResultSet lbrs = dataSetControllerImpl.getLangevinBatchResultSet(vCDataIdentifier);
        assertNull(lbrs, "Result set should be null for Euler data when read as LangevinBatchResultSet");

    }

//    @Test
//    public void testRead() throws IOException {
//
//        // read the input data
//        ODESolverResultSet osrs_1 = getOdeSolverResultSet(ida_1_File);
//        assertNotNull(osrs_1, "Failed to read IDA file: " + ida_1_File.getAbsolutePath());
//    }

    private static void deleteRecursively(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }


    private static File copyToPrimaryDir(String resourceName) throws IOException {
        File out = new File(primaryDir, resourceName);
        Resources.asByteSource(Resources.getResource(resourceBase + resourceName))
                .copyTo(com.google.common.io.Files.asByteSink(out));
        return out;
    }

    // also see ODESimData.readIDADataFile()
    private static ODESolverResultSet getOdeSolverResultSet(File idaFile) throws IOException {
        ODESolverResultSet odeSolverResultSet = new ODESolverResultSet();
        FileInputStream inputStream = null;
        inputStream = new FileInputStream(idaFile);
        if(readIDA(odeSolverResultSet, inputStream) == null) {
            return null;
        }
        return (odeSolverResultSet);
    }
    private static ODESolverResultSet readIDA(ODESolverResultSet odeSolverResultSet, FileInputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        //  read header
        String line = bufferedReader.readLine();
        if (line == null) {
            return null;
        }
        while (line.indexOf(':') > 0) {
            String name = line.substring(0, line.indexOf(':'));
            odeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(name));
            line = line.substring(line.indexOf(':') + 1);
        }
        //  read data
        while ((line = bufferedReader.readLine()) != null) {
            line = line + " ";
            double[] values = new double[odeSolverResultSet.getDataColumnCount()];
            boolean bCompleteRow = true;
            for (int i = 0; i < odeSolverResultSet.getDataColumnCount(); i++) {
                if (line.indexOf(' ')==-1) {    // here and below we assume separator is ' ', in other cases might also be '\t'
                    bCompleteRow = false;
                    break;
                }else{
                    String value = line.substring(0, line.indexOf(' ')).trim();
                    values[i] = Double.valueOf(value).doubleValue();
                    line = line.substring(line.indexOf(' ') + 1);
                }
            }
            if (bCompleteRow){
                odeSolverResultSet.addRow(values);
            }else{
                break;
            }
        }
        return odeSolverResultSet;
    }
}

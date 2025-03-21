package cbit.vcell.message.server.dispatcher;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.math.*;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.SimulationExecutionStatus;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
import org.joda.time.DateTime;
import org.vcell.util.DataAccessException;
import org.vcell.util.ISize;
import org.vcell.util.document.*;

import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;

public class DispatcherTestUtils {
    private static String previousServerID = "";
    private static String previousHtcMax = "";
    private static String previousHtcMin = "";
    private static String previousHtcPowerFloor = "";
    private static String previousHtcPowerMax = "";
    private static String previousMongoBlob = "";
    private static String previousJMSIntHostProperty = "";
    private static String previousJMSIntPortProperty = "";
    private static String previousSimJMSIntHostProperty = "";
    private static String previousSimJMSIntPortProperty = "";
    private static String previousHTCHost = "";
    private static String previousHTCUser = "";
    private static String previousHTCUserKeyFile = "";
    private static String previousMaxJobsPerScan = "";
    private static String previousOdeJobsPerUser = "";
    private static String previousPdeJobsPerUser = "";

    public static final VCellServerID testVCellServerID = VCellServerID.getServerID("test");
    public static final MockVCMessageSession testMessageSession = new MockVCMessageSession();
    public static final int jobIndex = 0;
    public static final int taskID = 0;
    public static final KeyValue simKey = new KeyValue("0");
    public static User alice = new User("Alice", new KeyValue("0"));
    public static User bob = new User("Bob", new KeyValue("1"));
    public static final VCSimulationIdentifier simID = new VCSimulationIdentifier(simKey, alice);
    public static final HtcJobID htcJobID = new HtcJobID("2", HtcJobID.BatchSystemType.SLURM);

    public static void setRequiredProperties(){
        previousServerID = PropertyLoader.getProperty(PropertyLoader.vcellServerIDProperty, "");
        PropertyLoader.setProperty(PropertyLoader.vcellServerIDProperty, testVCellServerID.toString());

        previousHtcMax = PropertyLoader.getProperty(PropertyLoader.htcMaxMemoryMB, "");
        PropertyLoader.setProperty(PropertyLoader.htcMaxMemoryMB, "4096");

        previousHtcMin = PropertyLoader.getProperty(PropertyLoader.htcMinMemoryMB, "");
        PropertyLoader.setProperty(PropertyLoader.htcMinMemoryMB, "1024");

        previousHtcPowerFloor = PropertyLoader.getProperty(PropertyLoader.htcPowerUserMemoryFloorMB, "");
        PropertyLoader.setProperty(PropertyLoader.htcPowerUserMemoryFloorMB, "51200");

        previousHtcPowerMax = PropertyLoader.getProperty(PropertyLoader.htcPowerUserMemoryMaxMB, "");
        PropertyLoader.setProperty(PropertyLoader.htcPowerUserMemoryMaxMB, "64000");

        previousMongoBlob = PropertyLoader.getProperty(PropertyLoader.jmsBlobMessageUseMongo, "");
        PropertyLoader.setProperty(PropertyLoader.jmsBlobMessageUseMongo, "");

        previousJMSIntHostProperty = PropertyLoader.getProperty(PropertyLoader.jmsIntHostInternal, "");
        PropertyLoader.setProperty(PropertyLoader.jmsIntHostInternal, "host");

        previousJMSIntPortProperty = PropertyLoader.getProperty(PropertyLoader.jmsIntPortInternal, "");
        PropertyLoader.setProperty(PropertyLoader.jmsIntPortInternal, "80");

        previousSimJMSIntHostProperty = PropertyLoader.getProperty(PropertyLoader.jmsSimHostInternal, "");
        PropertyLoader.setProperty(PropertyLoader.jmsSimHostInternal, "host");

        previousSimJMSIntPortProperty = PropertyLoader.getProperty(PropertyLoader.jmsSimPortInternal, "");
        PropertyLoader.setProperty(PropertyLoader.jmsSimPortInternal, "80");

        previousHTCHost = PropertyLoader.getProperty(PropertyLoader.htcHosts, "");
        PropertyLoader.setProperty(PropertyLoader.htcHosts, "host");

        previousHTCUser = PropertyLoader.getProperty(PropertyLoader.htcUser, "");
        PropertyLoader.setProperty(PropertyLoader.htcUser, "user");

        previousHTCUserKeyFile = PropertyLoader.getProperty(PropertyLoader.htcUserKeyFile, "");
        PropertyLoader.setProperty(PropertyLoader.htcUserKeyFile, "keyFile");

        previousMaxJobsPerScan = PropertyLoader.getProperty(PropertyLoader.maxJobsPerScan, "");
        PropertyLoader.setProperty(PropertyLoader.maxJobsPerScan, "100");

        previousPdeJobsPerUser = PropertyLoader.getProperty(PropertyLoader.maxPdeJobsPerUser, "");
        PropertyLoader.setProperty(PropertyLoader.maxPdeJobsPerUser, "100");

        previousOdeJobsPerUser = PropertyLoader.getProperty(PropertyLoader.maxOdeJobsPerUser, "");
        PropertyLoader.setProperty(PropertyLoader.maxOdeJobsPerUser, "100");

        PropertyLoader.setProperty(PropertyLoader.mongodbDatabase, "fakehost");
    }

    public static void restoreRequiredProperties(){
        PropertyLoader.setProperty(PropertyLoader.vcellServerIDProperty, previousServerID);
        PropertyLoader.setProperty(PropertyLoader.htcMaxMemoryMB, previousHtcMax);
        PropertyLoader.setProperty(PropertyLoader.htcMinMemoryMB, previousHtcMin);
        PropertyLoader.setProperty(PropertyLoader.htcPowerUserMemoryFloorMB, previousHtcPowerFloor);
        PropertyLoader.setProperty(PropertyLoader.htcPowerUserMemoryMaxMB, previousHtcPowerMax);
        PropertyLoader.setProperty(PropertyLoader.jmsBlobMessageUseMongo, previousMongoBlob);
        PropertyLoader.setProperty(PropertyLoader.jmsIntPortInternal, previousJMSIntPortProperty);
        PropertyLoader.setProperty(PropertyLoader.jmsIntHostInternal, previousJMSIntHostProperty);
        PropertyLoader.setProperty(PropertyLoader.jmsSimPortInternal, previousSimJMSIntPortProperty);
        PropertyLoader.setProperty(PropertyLoader.jmsSimHostInternal, previousSimJMSIntHostProperty);
        PropertyLoader.setProperty(PropertyLoader.htcHosts, previousHTCHost);
        PropertyLoader.setProperty(PropertyLoader.htcUser, previousHTCUser);
        PropertyLoader.setProperty(PropertyLoader.htcUserKeyFile, previousHTCUserKeyFile);
        PropertyLoader.setProperty(PropertyLoader.maxJobsPerScan, previousMaxJobsPerScan);
        PropertyLoader.setProperty(PropertyLoader.maxOdeJobsPerUser, previousOdeJobsPerUser);
        PropertyLoader.setProperty(PropertyLoader.maxPdeJobsPerUser, previousPdeJobsPerUser);
    }

    public static Simulation createMockSimulation(int iSizeX, int iSizeY, int iSizeZ, User user) throws PropertyVetoException, MathException, ExpressionBindingException {
        VolVariable volVariable = new VolVariable("t", new Variable.Domain(new CompartmentSubDomain("t", 1)));
        VolVariable volVariable2 = new VolVariable("b", new Variable.Domain(new CompartmentSubDomain("b", 2)));
        MathSymbolMapping mathSymbolMapping = new MathSymbolMapping();
        Geometry geometry = new Geometry("T", 3);
        MathModel mathModel = new MathModel(new Version("Test", user));
        MathDescription mathDescription = new MathDescription("Test", mathSymbolMapping);
        mathDescription.setGeometry(new Geometry("T", 3));
        SimulationVersion simulationVersion = new SimulationVersion(new KeyValue("5"), "Test", user,
                new GroupAccessNone(), null, new BigDecimal("2"), Date.from(Instant.now()), VersionFlag.fromInt(1),
                "", new KeyValue("3"));
        Simulation simulation = new Simulation(simulationVersion,
                mathDescription, mathModel);
        MeshSpecification meshSpecification = new MeshSpecification(geometry);
        meshSpecification.setSamplingSize(new ISize(iSizeX, iSizeY, iSizeZ));
        simulation.setMeshSpecification(meshSpecification);
        mathDescription.setAllVariables(new Variable[]{volVariable, volVariable2});
        return simulation;
    }

    public static Simulation createMockSimulation(int iSizeX, int iSizeY, int iSizeZ) throws PropertyVetoException, MathException, ExpressionBindingException {
        return createMockSimulation(iSizeX, iSizeY, iSizeZ, alice);
    }

    public static void insertOrUpdateStatus(KeyValue simKey, int jobIndex, int taskID, User user, SimulationJobStatus.SchedulerStatus status, SimulationDatabase simulationDB) throws SQLException, DataAccessException {
        SimulationJobStatus jobStatus = simulationDB.getLatestSimulationJobStatus(simKey, jobIndex);
        VCSimulationIdentifier simID = new VCSimulationIdentifier(simKey, user);
        SimulationJobStatus simulationJobStatus = new SimulationJobStatus(testVCellServerID, simID, jobIndex, Date.from(Instant.now()), status, taskID,
                SimulationMessage.workerAccepted("accepted"), null,
                new SimulationExecutionStatus(Date.from(Instant.now()), "",
                        Date.from(Instant.now()), Date.from(Instant.now()), false, htcJobID));
        if (jobStatus == null){
            simulationDB.insertSimulationJobStatus(simulationJobStatus);
        } else {
            simulationDB.updateSimulationJobStatus(simulationJobStatus);
        }
    }

    public static void insertOrUpdateStatus(KeyValue simKey, int jobIndex, int taskID, User user, SimulationDatabase simulationDB) throws SQLException, DataAccessException {
        insertOrUpdateStatus(simKey, jobIndex, taskID, user, SimulationJobStatus.SchedulerStatus.RUNNING, simulationDB);
    }

    public static void insertOrUpdateStatus(SimulationDatabase simulationDatabase, SimulationJobStatus.SchedulerStatus status) throws SQLException, DataAccessException {
        insertOrUpdateStatus(simKey, jobIndex, taskID, alice, status, simulationDatabase);
    }

    /**
     Defaults to a running status.
     */
    public static void insertOrUpdateStatus(SimulationDatabase simulationDatabase) throws SQLException, DataAccessException {
        insertOrUpdateStatus(simKey, jobIndex, taskID, alice, simulationDatabase);
    }

}

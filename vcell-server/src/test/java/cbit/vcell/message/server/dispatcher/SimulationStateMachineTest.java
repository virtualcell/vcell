package cbit.vcell.message.server.dispatcher;

import cbit.rmi.event.WorkerEvent;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.math.*;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.local.LocalVCMessageAdapter;
import cbit.vcell.message.messages.StatusMessage;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.SimulationExecutionStatus;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
import org.junit.jupiter.api.*;
import org.vcell.util.DataAccessException;
import org.vcell.util.ISize;
import org.vcell.util.document.*;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

@Tag("Fast")
public class SimulationStateMachineTest {

    private static final VCellServerID testVCellServerID = VCellServerID.getServerID("test");
    private static final User testUser = new User("Alice", new KeyValue("0"));
    private static final VCMessageSession testMessageSession = new LocalVCMessageAdapter(null);
    private static final int jobIndex = 0;
    private static final int taskID = 0;
    private static final KeyValue simKey = new KeyValue("1");
    private static final VCSimulationIdentifier simID = new VCSimulationIdentifier(simKey, testUser);

    public static String previousServerID = "";
    public static String previousHtcMax = "";
    public static String previousHtcMin = "";
    public static String previousHtcPowerFloor = "";

    private MockSimulationDB simulationDB;
    private SimulationStateMachineCopy stateMachine;

    @BeforeAll
    public static void setSystemProperties(){
        previousServerID = PropertyLoader.getProperty(PropertyLoader.vcellServerIDProperty, "");
        PropertyLoader.setProperty(PropertyLoader.vcellServerIDProperty, "");

        previousHtcMax = PropertyLoader.getProperty(PropertyLoader.htcMaxMemoryMB, "");
        PropertyLoader.setProperty(PropertyLoader.htcMaxMemoryMB, "4096");

        previousHtcMin = PropertyLoader.getProperty(PropertyLoader.htcMinMemoryMB, "");
        PropertyLoader.setProperty(PropertyLoader.htcMinMemoryMB, "1024");

        previousHtcPowerFloor = PropertyLoader.getProperty(PropertyLoader.htcPowerUserMemoryFloorMB, "");
        PropertyLoader.setProperty(PropertyLoader.htcPowerUserMemoryFloorMB, "51200");
    }

    @AfterAll
    public static void restoreSystemProperties(){
        PropertyLoader.setProperty(PropertyLoader.vcellServerIDProperty, previousServerID);
        PropertyLoader.setProperty(PropertyLoader.htcMaxMemoryMB, previousHtcMax);
        PropertyLoader.setProperty(PropertyLoader.htcMinMemoryMB, previousHtcMin);
        PropertyLoader.setProperty(PropertyLoader.htcPowerUserMemoryFloorMB, previousHtcPowerFloor);
    }

    @BeforeEach
    public void setUp(){
        simulationDB = new MockSimulationDB();
        stateMachine = new SimulationStateMachineCopy(simKey, jobIndex);
    }

    private record ChangedStateValues(
            VCSimulationIdentifier simID,
            SimulationJobStatus.SchedulerStatus schedulerStatus,
            int workerEventJob,
            int taskID,
            String changesResult
    ){ }

    private WorkerEvent createWorkerEvent(ChangedStateValues w){
        SimulationMessage acceptedSimulationMessage = SimulationMessage.workerAccepted("accepted");
        return new WorkerEvent(w.workerEventJob, simKey,
                w.simID, jobIndex, "",
                w.taskID, null, null,
                acceptedSimulationMessage);
    }

    private Simulation createMockSimulation(int iSizeX, int iSizeY, int iSizeZ) throws PropertyVetoException, MathException, ExpressionBindingException {
        VolVariable volVariable = new VolVariable("t", new Variable.Domain(new CompartmentSubDomain("t", 1)));
        VolVariable volVariable2 = new VolVariable("b", new Variable.Domain(new CompartmentSubDomain("b", 2)));
        MathSymbolMapping mathSymbolMapping = new MathSymbolMapping();
        Geometry geometry = new Geometry("T", 3);
        MathModel mathModel = new MathModel(new Version("Test", testUser));
        MathDescription mathDescription = new MathDescription("Test", mathSymbolMapping);
        mathDescription.setGeometry(new Geometry("T", 3));
        Simulation simulation = new Simulation(SimulationVersion.createTempSimulationVersion(),
                mathDescription, mathModel);
        MeshSpecification meshSpecification = new MeshSpecification(geometry);
        meshSpecification.setSamplingSize(new ISize(iSizeX, iSizeY, iSizeZ));
        simulation.setMeshSpecification(meshSpecification);
        mathDescription.setAllVariables(new Variable[]{volVariable, volVariable2});
        return simulation;
    }

    private void insertOrUpdateStatus(KeyValue simKey, int jobIndex, int taskID, User user, SimulationJobStatus.SchedulerStatus status) throws SQLException, DataAccessException {
        SimulationJobStatus jobStatus = simulationDB.getLatestSimulationJobStatus(simKey, jobIndex);
        VCSimulationIdentifier simID = new VCSimulationIdentifier(simKey, user);
        SimulationJobStatus simulationJobStatus = new SimulationJobStatus(testVCellServerID, simID, jobIndex, Date.from(Instant.now()), status, taskID,
                SimulationMessage.workerAccepted("accepted"), null,
                new SimulationExecutionStatus(Date.from(Instant.now()), "",
                Date.from(Instant.now()), Date.from(Instant.now()), false, new HtcJobID("2", HtcJobID.BatchSystemType.SLURM)));
        if (jobStatus == null){
            simulationDB.insertSimulationJobStatus(simulationJobStatus);
        } else {
            simulationDB.updateSimulationJobStatus(simulationJobStatus);
        }
    }

    private void insertOrUpdateStatus(KeyValue simKey, int jobIndex, int taskID, User user) throws SQLException, DataAccessException {
        insertOrUpdateStatus(simKey, jobIndex, taskID, user, SimulationJobStatus.SchedulerStatus.RUNNING);
    }

    /**
    Defaults to a running status.
     */
    private void insertOrUpdateStatus() throws SQLException, DataAccessException {
        insertOrUpdateStatus(simKey, jobIndex, taskID, testUser);
    }

    private SimulationJobStatus getLatestJobSubmission() throws SQLException, DataAccessException {
        return simulationDB.getLatestSimulationJobStatus(simKey, jobIndex);
    }

    @Test
    public void workerEventRejectionsTest() throws SQLException, DataAccessException {
        int taskID = 16;

        ArrayList<ChangedStateValues> changedValues = new ArrayList<>(){{
            add(new ChangedStateValues(simID, SimulationJobStatus.SchedulerStatus.RUNNING, WorkerEvent.JOB_WORKER_ALIVE, taskID, "No old status.")); // no old status failure
            add(new ChangedStateValues(simID, SimulationJobStatus.SchedulerStatus.COMPLETED, WorkerEvent.JOB_WORKER_ALIVE, taskID, "Work is already done.")); // work is done failure
            add(new ChangedStateValues(simID, SimulationJobStatus.SchedulerStatus.RUNNING, WorkerEvent.JOB_WORKER_ALIVE, 0, "Task ID is lower")); // old status has higher number taskID failure
        }};

        for (int i = 0; i < changedValues.size(); i++){
            ChangedStateValues workerEventChangedValues = changedValues.get(i);
            if (i > 1) {
                insertOrUpdateStatus(simKey, jobIndex, taskID, testUser, workerEventChangedValues.schedulerStatus);
            }
            WorkerEvent workerEvent = createWorkerEvent(workerEventChangedValues);
            Assertions.assertFalse(stateMachine.isWorkerEventOkay(workerEvent, simulationDB), workerEventChangedValues.changesResult);
        }

        ChangedStateValues passingWorkerValues= new ChangedStateValues(simID, null, WorkerEvent.JOB_WORKER_ALIVE , taskID, "");
        WorkerEvent passingWorkerEvent = createWorkerEvent(passingWorkerValues);

        for (SimulationJobStatus.SchedulerStatus passingStatus: SimulationJobStatus.SchedulerStatus.values()){
            if (!passingStatus.isDone()){
                insertOrUpdateStatus(simKey, jobIndex, taskID, testUser, passingStatus);
                Assertions.assertTrue(stateMachine.isWorkerEventOkay(passingWorkerEvent, simulationDB));
            }
        }

    }

    @Test
    public void stateShouldTransitionToFailure() throws SQLException, DataAccessException, VCMessagingException, PropertyVetoException, MathException, ExpressionBindingException {
        ArrayList<ChangedStateValues> changedValues = new ArrayList<>(){{
           add(new ChangedStateValues(simID, null, WorkerEvent.JOB_FAILURE, taskID, "The current worker has failed."));
           add(new ChangedStateValues(simID, null, WorkerEvent.JOB_WORKER_EXIT_ERROR, taskID, "The current worker exited with an error."));
        }};

        for (ChangedStateValues changedValue : changedValues){
            insertOrUpdateStatus();
            stateMachine.onWorkerEvent(createWorkerEvent(changedValue), simulationDB, null);
            SimulationJobStatus result = getLatestJobSubmission();
            Assertions.assertTrue(result.getSchedulerStatus().isFailed(), changedValue.changesResult);
        }

        simulationDB = new MockSimulationDB();
        StatusMessage statusMessage =  stateMachine.onStartRequest(new User("Bob", new KeyValue("1")), simID, simulationDB, null);
        Assertions.assertTrue(statusMessage.getSimulationJobStatus().getSchedulerStatus().isFailed(), "Different from initial user that owns the simulation");

        SimulationJobStatus jobStatus = getLatestJobSubmission();
        Assertions.assertNull(jobStatus, "If it fails on start request, there should be nothing in the DB.");

        insertOrUpdateStatus();
        Assertions.assertThrows(RuntimeException.class,
                () -> {stateMachine.onStartRequest(testUser, simID, simulationDB, testMessageSession);},
                "Can't start simulation job unless previous is done.");

        insertOrUpdateStatus();
        jobStatus = getLatestJobSubmission();
        stateMachine.onSystemAbort(jobStatus, "Test Abort", simulationDB, testMessageSession);
        jobStatus = getLatestJobSubmission();
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isFailed());

//
        Simulation memoryIntensiveSimulation = createMockSimulation(900, 900, 900);

        insertOrUpdateStatus();
        Assertions.assertThrows(RuntimeException.class,
                () -> {stateMachine.onDispatch(memoryIntensiveSimulation, getLatestJobSubmission(), simulationDB, testMessageSession);},
                "Can't dispatch simulation that is already running.");

        insertOrUpdateStatus(simKey, jobIndex, taskID, testUser, SimulationJobStatus.SchedulerStatus.WAITING);
        stateMachine.onDispatch(memoryIntensiveSimulation, getLatestJobSubmission(), simulationDB, testMessageSession);

        jobStatus = getLatestJobSubmission();
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isFailed(), "Memory size to large");

        insertOrUpdateStatus();
        statusMessage = stateMachine.onStopRequest(new User("Bob", new KeyValue("2")), getLatestJobSubmission(), simulationDB, testMessageSession);
        Assertions.assertTrue(statusMessage.getSimulationJobStatus().getSchedulerStatus().isFailed(), "Stopping as another user.");
    }

    @Test
    public void stateShouldTransitionToWaiting() throws SQLException, VCMessagingException, DataAccessException {
        stateMachine.onStartRequest(testUser, simID, simulationDB, testMessageSession);
        SimulationJobStatus jobStatus = getLatestJobSubmission();
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isWaiting(), "Just started new task.");
    }

    @Test
    public void stateShouldTransitionToDispatched() throws SQLException, DataAccessException, VCMessagingException, PropertyVetoException, MathException, ExpressionBindingException {
        insertOrUpdateStatus(simKey, jobIndex, taskID, testUser, SimulationJobStatus.SchedulerStatus.WAITING);
        WorkerEvent acceptedWorker = createWorkerEvent(new ChangedStateValues(simID, null, WorkerEvent.JOB_ACCEPTED, taskID, "Worker just got accepted"));
        stateMachine.onWorkerEvent(acceptedWorker, simulationDB, testMessageSession);
        SimulationJobStatus jobStatus = getLatestJobSubmission();
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isDispatched(), "Job recently got accepted, only works if previous state was waiting.");

        insertOrUpdateStatus();
        stateMachine.onWorkerEvent(acceptedWorker, simulationDB, testMessageSession);
        jobStatus = getLatestJobSubmission();
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isRunning(), "The state has not changed from running, because something that is running can not be dispatched.");


        insertOrUpdateStatus(simKey, jobIndex, taskID, testUser, SimulationJobStatus.SchedulerStatus.WAITING);
        Simulation simulation = createMockSimulation(50, 50, 50);
        stateMachine.onDispatch(simulation, getLatestJobSubmission(), simulationDB, testMessageSession);
        jobStatus = getLatestJobSubmission();
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isDispatched());
    }

    @Test
    public void stateShouldTransitionToRunning() throws SQLException, DataAccessException, VCMessagingException {
        for (int workerStatus: WorkerEvent.ALL_JOB_EVENTS){
            WorkerEvent workerEvent = createWorkerEvent(new ChangedStateValues(simID, null, workerStatus, taskID, ""));
            insertOrUpdateStatus(simKey, jobIndex, taskID, testUser, SimulationJobStatus.SchedulerStatus.WAITING);
            stateMachine.onWorkerEvent(workerEvent, simulationDB, testMessageSession);
            SimulationJobStatus jobStatus = getLatestJobSubmission();
            if (workerEvent.isProgressEvent() || workerEvent.isNewDataEvent() || workerEvent.isStartingEvent() || workerEvent.isWorkerAliveEvent()){
                Assertions.assertTrue(jobStatus.getSchedulerStatus().isRunning());
            } else {
                Assertions.assertFalse(jobStatus.getSchedulerStatus().isRunning());
            }
        }
    }

    @Test
    public void stateShouldTransitionToCompleted() throws SQLException, VCMessagingException, DataAccessException {
        for (int workerStatus : WorkerEvent.ALL_JOB_EVENTS){
            WorkerEvent workerEvent = createWorkerEvent(new ChangedStateValues(simID, SimulationJobStatus.SchedulerStatus.RUNNING, workerStatus, taskID, ""));
            insertOrUpdateStatus();
            stateMachine.onWorkerEvent(workerEvent, simulationDB, testMessageSession);
            SimulationJobStatus jobStatus = getLatestJobSubmission();
            if (workerEvent.isCompletedEvent()){
                Assertions.assertTrue(jobStatus.getSchedulerStatus().isCompleted());
            } else {
                Assertions.assertFalse(jobStatus.getSchedulerStatus().isCompleted());
            }
        }
    }

    @Test
    public void stateShouldTransitionToStopped() throws SQLException, DataAccessException, VCMessagingException {

        for (SimulationJobStatus.SchedulerStatus status : SimulationJobStatus.SchedulerStatus.values()){
            insertOrUpdateStatus(simKey,jobIndex, taskID,testUser, status);
            if (status.isActive()){
                stateMachine.onStopRequest(testUser, getLatestJobSubmission(), simulationDB, testMessageSession);
                Assertions.assertTrue(getLatestJobSubmission().getSchedulerStatus().isStopped(), "");
            } else {
                StatusMessage statusMessage = stateMachine.onStopRequest(testUser, getLatestJobSubmission(), simulationDB, testMessageSession);
                Assertions.assertNull(statusMessage);
            }
        }
    }

    @Test
    public void stateShouldTransitionToQueued(){
        System.out.print("Not used in state machine");
    }


}

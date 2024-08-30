package cbit.vcell.message.server.dispatcher;

import cbit.rmi.event.WorkerEvent;
import cbit.vcell.math.MathException;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.messages.StatusMessage;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
import org.junit.jupiter.api.*;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

@Tag("Fast")
public class SimulationStateMachineTest {
    private static final User testUser = DispatcherTestUtils.alice;
    private static final MockVCMessageSession testMessageSession = new MockVCMessageSession();
    private static final int jobIndex = DispatcherTestUtils.jobIndex;
    private static final int taskID = DispatcherTestUtils.taskID;
    private static final KeyValue simKey = DispatcherTestUtils.simKey;
    private static final VCSimulationIdentifier simID = DispatcherTestUtils.simID;

    private MockSimulationDB simulationDB;
    private SimulationStateMachine stateMachine;

    @BeforeAll
    public static void setSystemProperties(){
        DispatcherTestUtils.setRequiredProperties();
    }

    @AfterAll
    public static void restoreSystemProperties(){
        DispatcherTestUtils.restoreRequiredProperties();
    }

    @BeforeEach
    public void setUp(){
        simulationDB = new MockSimulationDB();
        stateMachine = new SimulationStateMachine(simKey, jobIndex);
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

    private SimulationJobStatus getLatestJobSubmission() throws SQLException, DataAccessException {
        return simulationDB.getLatestSimulationJobStatus(simKey, jobIndex);
    }

    private SimulationJobStatus getClientTopicMessage(){
        return (SimulationJobStatus) testMessageSession.getTopicMessage(VCellTopic.ClientStatusTopic).getObjectContent();
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
                DispatcherTestUtils.insertOrUpdateStatus(simKey, jobIndex, taskID, testUser, workerEventChangedValues.schedulerStatus, simulationDB);
            }
            WorkerEvent workerEvent = createWorkerEvent(workerEventChangedValues);
            Assertions.assertFalse(stateMachine.isWorkerEventOkay(workerEvent, simulationDB), workerEventChangedValues.changesResult);
        }

        ChangedStateValues passingWorkerValues= new ChangedStateValues(simID, null, WorkerEvent.JOB_WORKER_ALIVE , taskID, "");
        WorkerEvent passingWorkerEvent = createWorkerEvent(passingWorkerValues);

        for (SimulationJobStatus.SchedulerStatus passingStatus: SimulationJobStatus.SchedulerStatus.values()){
            if (!passingStatus.isDone()){
                DispatcherTestUtils.insertOrUpdateStatus(simKey, jobIndex, taskID, testUser, passingStatus, simulationDB);
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
            DispatcherTestUtils.insertOrUpdateStatus(simulationDB);
            stateMachine.onWorkerEvent(createWorkerEvent(changedValue), simulationDB, testMessageSession);
            SimulationJobStatus result = getLatestJobSubmission();
            Assertions.assertTrue(result.getSchedulerStatus().isFailed(), changedValue.changesResult);
            Assertions.assertTrue(getClientTopicMessage().getSchedulerStatus().isFailed(), changedValue.changesResult);
        }

        simulationDB = new MockSimulationDB();
        StatusMessage statusMessage =  stateMachine.onStartRequest(DispatcherTestUtils.bob, simID, simulationDB, testMessageSession);
        Assertions.assertTrue(statusMessage.getSimulationJobStatus().getSchedulerStatus().isFailed(), "Different from initial user that owns the simulation");

        SimulationJobStatus jobStatus = getLatestJobSubmission();
        Assertions.assertNull(jobStatus, "If it fails on start request, there should be nothing in the DB.");
        Assertions.assertTrue(getClientTopicMessage().getSchedulerStatus().isFailed(), "Only the client receives start request failure status.");

        DispatcherTestUtils.insertOrUpdateStatus(simulationDB);
        Assertions.assertThrows(RuntimeException.class,
                () -> {stateMachine.onStartRequest(testUser, simID, simulationDB, testMessageSession);},
                "Can't start simulation job unless previous is done.");
        Assertions.assertThrows(NoSuchElementException.class,() -> getClientTopicMessage().getSchedulerStatus().isFailed(), "No message sent to client.");


        DispatcherTestUtils.insertOrUpdateStatus(simulationDB);
        jobStatus = getLatestJobSubmission();
        stateMachine.onSystemAbort(jobStatus, "Test Abort", simulationDB, testMessageSession);
        jobStatus = getLatestJobSubmission();
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isFailed());
        Assertions.assertTrue(getClientTopicMessage().getSchedulerStatus().isFailed(), "On abort client gets failed status.");

//
        Simulation memoryIntensiveSimulation = DispatcherTestUtils.createMockSimulation(900, 900, 900);

        DispatcherTestUtils.insertOrUpdateStatus(simulationDB);
        Assertions.assertThrows(RuntimeException.class,
                () -> {stateMachine.onDispatch(memoryIntensiveSimulation, getLatestJobSubmission(), simulationDB, testMessageSession);},
                "Can't dispatch simulation that is already running.");
        Assertions.assertThrows(NoSuchElementException.class, () -> getClientTopicMessage().getSchedulerStatus().isFailed(), "Client receives failure because simulation is already running.");

        DispatcherTestUtils.insertOrUpdateStatus(simKey, jobIndex, taskID, testUser, SimulationJobStatus.SchedulerStatus.WAITING, simulationDB);
        stateMachine.onDispatch(memoryIntensiveSimulation, getLatestJobSubmission(), simulationDB, testMessageSession);
        jobStatus = getLatestJobSubmission();
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isFailed(), "Memory size too large");
        Assertions.assertTrue(getClientTopicMessage().getSchedulerStatus().isFailed(), "Failed because of memory size.");

        DispatcherTestUtils.insertOrUpdateStatus(simulationDB);
        statusMessage = stateMachine.onStopRequest(DispatcherTestUtils.bob, getLatestJobSubmission(), simulationDB, testMessageSession);
        Assertions.assertTrue(statusMessage.getSimulationJobStatus().getSchedulerStatus().isFailed(), "Stopping as another user.");
        Assertions.assertTrue(getClientTopicMessage().getSchedulerStatus().isFailed(), "Can't stop as another user.");
    }

    @Test
    public void stateShouldTransitionToWaiting() throws SQLException, VCMessagingException, DataAccessException {
        stateMachine.onStartRequest(testUser, simID, simulationDB, testMessageSession);
        SimulationJobStatus jobStatus = getLatestJobSubmission();
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isWaiting(), "Just started new task.");
        Assertions.assertTrue(getClientTopicMessage().getSchedulerStatus().isWaiting());
    }

    @Test
    public void stateShouldTransitionToDispatched() throws SQLException, DataAccessException, VCMessagingException, PropertyVetoException, MathException, ExpressionBindingException {
        DispatcherTestUtils.insertOrUpdateStatus(simKey, jobIndex, taskID, testUser, SimulationJobStatus.SchedulerStatus.WAITING, simulationDB);
        WorkerEvent acceptedWorker = createWorkerEvent(new ChangedStateValues(simID, null, WorkerEvent.JOB_ACCEPTED, taskID, "Worker just got accepted"));
        stateMachine.onWorkerEvent(acceptedWorker, simulationDB, testMessageSession);
        SimulationJobStatus jobStatus = getLatestJobSubmission();
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isDispatched(), "Job recently got accepted, only works if previous state was waiting.");
        Assertions.assertTrue(getClientTopicMessage().getSchedulerStatus().isDispatched());

        DispatcherTestUtils.insertOrUpdateStatus(simulationDB);
        stateMachine.onWorkerEvent(acceptedWorker, simulationDB, testMessageSession);
        jobStatus = getLatestJobSubmission();
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isRunning(), "The state has not changed from running, because something that is running can not be dispatched.");


        DispatcherTestUtils.insertOrUpdateStatus(simKey, jobIndex, taskID, testUser, SimulationJobStatus.SchedulerStatus.WAITING, simulationDB);
        Simulation simulation = DispatcherTestUtils.createMockSimulation(50, 50, 50);
        stateMachine.onDispatch(simulation, getLatestJobSubmission(), simulationDB, testMessageSession);
        jobStatus = getLatestJobSubmission();
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isDispatched());
        Assertions.assertTrue(getClientTopicMessage().getSchedulerStatus().isDispatched());
    }

    @Test
    public void stateShouldTransitionToRunning() throws SQLException, DataAccessException, VCMessagingException {
        for (int workerStatus: WorkerEvent.ALL_JOB_EVENTS){
            WorkerEvent workerEvent = createWorkerEvent(new ChangedStateValues(simID, null, workerStatus, taskID, ""));
            DispatcherTestUtils.insertOrUpdateStatus(simKey, jobIndex, taskID, testUser, SimulationJobStatus.SchedulerStatus.WAITING, simulationDB);
            stateMachine.onWorkerEvent(workerEvent, simulationDB, testMessageSession);
            SimulationJobStatus jobStatus = getLatestJobSubmission();
            if (workerEvent.isProgressEvent() || workerEvent.isNewDataEvent() || workerEvent.isStartingEvent() || workerEvent.isWorkerAliveEvent()){
                Assertions.assertTrue(jobStatus.getSchedulerStatus().isRunning());
                Assertions.assertTrue(getClientTopicMessage().getSchedulerStatus().isRunning());
            } else {
                Assertions.assertFalse(jobStatus.getSchedulerStatus().isRunning());
                try {
                    Assertions.assertFalse(getClientTopicMessage().getSchedulerStatus().isRunning());
                } catch (NoSuchElementException ignored){}
            }
        }
    }

    @Test
    public void stateShouldTransitionToCompleted() throws SQLException, VCMessagingException, DataAccessException {
        for (int workerStatus : WorkerEvent.ALL_JOB_EVENTS){
            WorkerEvent workerEvent = createWorkerEvent(new ChangedStateValues(simID, SimulationJobStatus.SchedulerStatus.RUNNING, workerStatus, taskID, ""));
            DispatcherTestUtils.insertOrUpdateStatus(simulationDB);
            stateMachine.onWorkerEvent(workerEvent, simulationDB, testMessageSession);
            SimulationJobStatus jobStatus = getLatestJobSubmission();
            if (workerEvent.isCompletedEvent()){
                Assertions.assertTrue(jobStatus.getSchedulerStatus().isCompleted());
                Assertions.assertTrue(getClientTopicMessage().getSchedulerStatus().isCompleted());
            } else {
                Assertions.assertFalse(jobStatus.getSchedulerStatus().isCompleted());
                try {
                    Assertions.assertFalse(getClientTopicMessage().getSchedulerStatus().isCompleted());
                } catch (NoSuchElementException ignored){}
            }
        }
    }

    @Test
    public void stateShouldTransitionToStopped() throws SQLException, DataAccessException, VCMessagingException {

        for (SimulationJobStatus.SchedulerStatus status : SimulationJobStatus.SchedulerStatus.values()){
            DispatcherTestUtils.insertOrUpdateStatus(simKey,jobIndex, taskID,testUser, status, simulationDB);
            if (status.isActive()){
                stateMachine.onStopRequest(testUser, getLatestJobSubmission(), simulationDB, testMessageSession);
                Assertions.assertTrue(getLatestJobSubmission().getSchedulerStatus().isStopped(), "");
                Assertions.assertTrue(getClientTopicMessage().getSchedulerStatus().isStopped());
            } else {
                StatusMessage statusMessage = stateMachine.onStopRequest(testUser, getLatestJobSubmission(), simulationDB, testMessageSession);
                Assertions.assertNull(statusMessage);
                try {
                    Assertions.assertFalse(getClientTopicMessage().getSchedulerStatus().isCompleted());
                } catch (NoSuchElementException ignored){}
            }
        }
    }

    @Test
    public void stateShouldTransitionToQueued(){
        System.out.print("Not used in state machine");
    }


}

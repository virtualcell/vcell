package cbit.vcell.message.server.dispatcher;
import org.junit.jupiter.api.parallel.ResourceLock;

import cbit.vcell.math.MathException;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.solver.Simulation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.junit.jupiter.api.*;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;

@Tag("Fast")
@ResourceLock("vcellGlobalConfig")
public class SimulationDispatcherTest {
    public static ExtendedLogger lg = LoggerContext.getContext().getLogger(SimulationDispatcher.class);
    private final static User testUser = DispatcherTestUtils.alice;
    private final MockSimulationDB mockSimulationDB = new MockSimulationDB();
    private final MockMessagingService mockMessagingServiceInternal = new MockMessagingService();
    private final MockMessagingService mockMessagingServiceSim = new MockMessagingService();
    private final MockHtcProxy mockHtcProxy = new MockHtcProxy(null, "htcUser", mockSimulationDB);
    private static StringWriter logOutPut;
    private static WriterAppender appender;

    @BeforeAll
    public static void setSystemProperties(){
        DispatcherTestUtils.setRequiredProperties();

        logOutPut = new StringWriter();
        appender = WriterAppender.newBuilder().setTarget(logOutPut).setName("Simulation Dispatcher Test").build();
        LoggerContext context = LoggerContext.getContext(false);
        Configuration configuration = context.getConfiguration();
        configuration.addLoggerAppender((Logger) lg, appender);
    }

    @AfterAll
    public static void restoreSystemProperties() throws IOException {
        DispatcherTestUtils.restoreRequiredProperties();
        appender.stop();
        logOutPut.close();
    }

    //################# Test Simulation Service Impl #######################
    // All the get functions withing SimulationDispatcher seem to be exercising the DB and not simulation control, so not tested

    @Test
    public void onStartRequestTest() throws DataAccessException, SQLException {
        SimulationDispatcher simulationDispatcher = SimulationDispatcher.simulationDispatcherCreator(mockSimulationDB, mockMessagingServiceInternal,
                mockMessagingServiceSim, mockHtcProxy, false);
        SimulationStatus simStatus = simulationDispatcher.simServiceImpl.startSimulation(testUser, DispatcherTestUtils.simID, 1);
        SimulationJobStatus jobStatus = mockSimulationDB.getLatestSimulationJobStatus(DispatcherTestUtils.simKey, 0);
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isWaiting());
    }

    @Test
    public void onStopRequestTest() throws DataAccessException, SQLException {
        DispatcherTestUtils.insertOrUpdateStatus(mockSimulationDB);
        SimulationDispatcher simulationDispatcher = SimulationDispatcher.simulationDispatcherCreator(mockSimulationDB, mockMessagingServiceInternal,
                mockMessagingServiceSim, mockHtcProxy, false);
        SimulationStatus simStatus = simulationDispatcher.simServiceImpl.stopSimulation(testUser, DispatcherTestUtils.simID);
        SimulationJobStatus jobStatus = mockSimulationDB.getLatestSimulationJobStatus(DispatcherTestUtils.simKey, 0);
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isStopped());

        String s = mockMessagingServiceInternal.mockVCMessageSession.getTopicMessage(VCellTopic.ServiceControlTopic).getStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY);
        Assertions.assertEquals(MessageConstants.MESSAGE_TYPE_STOPSIMULATION_VALUE, s);
    }


    //###################### Test Dispatcher Thread ###########################

    @Test
    public void dispatcherThreadFailsJobsWithNoSimulationReference() throws SQLException, DataAccessException, InterruptedException {
        DispatcherTestUtils.insertOrUpdateStatus(mockSimulationDB, SimulationJobStatus.SchedulerStatus.WAITING);

        // The job is WAITING before the dispatcher exists. Asserting this after starting the
        // dispatcher would be a race: the dispatch thread runs as soon as it is constructed, so it
        // may already have failed the job by then - which is what made this test flaky.
        SimulationJobStatus jobStatus = mockSimulationDB.getLatestSimulationJobStatus(DispatcherTestUtils.simKey, 0);
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isWaiting(), "job starts out waiting");

        SimulationDispatcher simulationDispatcher = SimulationDispatcher.simulationDispatcherCreator(mockSimulationDB, mockMessagingServiceInternal,
                mockMessagingServiceSim, mockHtcProxy, true);
        SimulationDispatcher.DispatchThread thread = simulationDispatcher.dispatchThread;
        synchronized (thread.dispatcherNotifyObject){
            thread.dispatcherNotifyObject.notify();
        }

        awaitDispatchPass(thread);

        // Makes sure that requests that have no simulation reference within the DB are failed
        jobStatus = mockSimulationDB.getLatestSimulationJobStatus(DispatcherTestUtils.simKey, 0);
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isFailed(), "Simulation gets aborted since theres no simulation in DB.");

    }

    @Test
    public void dispatcherThreadDispatchesWaitingJobsWithSimulationsIn() throws SQLException, DataAccessException, InterruptedException, PropertyVetoException, MathException, ExpressionBindingException {
        // Insert the simulation BEFORE starting the dispatcher, so that any completed pass has
        // necessarily seen it. Inserting afterwards would mean the first pass might have run
        // before the insert, and waiting for it would prove nothing.
        Simulation mockSimulation = DispatcherTestUtils.createMockSimulation(20, 20, 20);
        mockSimulationDB.insertSimulation(DispatcherTestUtils.alice, mockSimulation);
        DispatcherTestUtils.insertOrUpdateStatus(mockSimulation.getKey(), DispatcherTestUtils.jobIndex, DispatcherTestUtils.taskID, DispatcherTestUtils.alice,
                SimulationJobStatus.SchedulerStatus.WAITING, mockSimulationDB);

        SimulationDispatcher simulationDispatcher = SimulationDispatcher.simulationDispatcherCreator(mockSimulationDB, mockMessagingServiceInternal,
                mockMessagingServiceSim, mockHtcProxy, true);
        SimulationDispatcher.DispatchThread thread = simulationDispatcher.dispatchThread;
        synchronized (thread.dispatcherNotifyObject){
            thread.dispatcherNotifyObject.notify();
        }
        awaitDispatchPass(thread);

        SimulationJobStatus jobStatus = mockSimulationDB.getLatestSimulationJobStatus(mockSimulation.getKey(), 0);
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isDispatched(), "Dispatches");
    }

    /** Timeout for waiting on a worker pass - generous, since it only bites when something hangs. */
    private static final long PASS_TIMEOUT_MS = 30_000;

    /**
     * Wait for the dispatch thread to finish a pass over the job table.
     * <p>
     * The job under test is inserted before the dispatcher is constructed, so any completed pass
     * has seen it; waiting for a count rather than a bare notify means a pass that finished before
     * we got here still counts, instead of hanging forever on a missed notification.
     */
    private static void awaitDispatchPass(SimulationDispatcher.DispatchThread thread) throws InterruptedException {
        Assertions.assertTrue(thread.finishListener.awaitCompletedPasses(1, PASS_TIMEOUT_MS),
                "dispatch thread did not complete a pass within " + PASS_TIMEOUT_MS + "ms");
    }

    private static void awaitFlush(SimulationDispatcher.SimulationMonitor.QueueFlusher queueFlusher)
            throws InterruptedException {
        Assertions.assertTrue(queueFlusher.finishListener.awaitCompletedPasses(1, PASS_TIMEOUT_MS),
                "queue flusher did not complete within " + PASS_TIMEOUT_MS + "ms");
    }



    //###################### Test Simulation Monitor ##########################
    // Rig the mock simulation DB to return a simulation job status that features some misbehavior of what's expected,
    // which prompts for removal by the zombie killer
    @Test
    public void zombieKillerTest() throws SQLException, DataAccessException, InterruptedException, IOException {
        SimulationDispatcher.INITIAL_ZOMBIE_DELAY = 10;
        SimulationDispatcher simulationDispatcher = SimulationDispatcher.simulationDispatcherCreator(mockSimulationDB, mockMessagingServiceInternal,
                mockMessagingServiceSim, mockHtcProxy, false);
        DispatcherTestUtils.insertOrUpdateStatus(mockSimulationDB);
        mockHtcProxy.jobsKilledSafely.clear();

        mockSimulationDB.badLatestSimulation = MockSimulationDB.BadLatestSimulation.HIGHER_TASK_ID;
        SimulationDispatcher.SimulationMonitor.ZombieKiller zombieKiller = simulationDispatcher.simMonitor.initialZombieKiller;
        zombieKiller.run();
        Assertions.assertTrue(logOutPut.toString().contains(SimulationDispatcher.SimulationMonitor.ZombieKiller.newJobFound));
        Assertions.assertEquals(1, mockHtcProxy.jobsKilledSafely.size());

        mockSimulationDB.badLatestSimulation = MockSimulationDB.BadLatestSimulation.RETURN_NULL;
        zombieKiller.run();
        Assertions.assertTrue(logOutPut.toString().contains(SimulationDispatcher.SimulationMonitor.ZombieKiller.noJob));
        Assertions.assertEquals(2, mockHtcProxy.jobsKilledSafely.size());

        mockSimulationDB.badLatestSimulation = MockSimulationDB.BadLatestSimulation.IS_DONE;
        zombieKiller.run();
        Assertions.assertTrue(logOutPut.toString().contains(SimulationDispatcher.SimulationMonitor.ZombieKiller.jobIsAlreadyDone));
        Assertions.assertEquals(3, mockHtcProxy.jobsKilledSafely.size());
    }

    @Test
    public void queueFlusherKillIdleJobs() throws SQLException, DataAccessException, InterruptedException {
        SimulationDispatcher simulationDispatcher = SimulationDispatcher.simulationDispatcherCreator(mockSimulationDB, mockMessagingServiceInternal,
                mockMessagingServiceSim, mockHtcProxy, false);
        DispatcherTestUtils.insertOrUpdateStatus(mockSimulationDB);

        SimulationDispatcher.SimulationMonitor simMonitor = simulationDispatcher.simMonitor;
        SimulationDispatcher.SimulationMonitor.QueueFlusher queueFlusher = simMonitor.initialQueueFlusher;
        SimulationStateMachine sm = simulationDispatcher.simDispatcherEngine.getSimulationStateMachine(DispatcherTestUtils.simKey, DispatcherTestUtils.jobIndex);
        sm.setSolverProcessTimestamp(0);
        Thread queueThread = new Thread(queueFlusher);
        queueThread.start();
        int retries = 0;
        while (queueThread.getState() != Thread.State.TIMED_WAITING){
            if (retries == 10){
                break;
            }
            Thread.sleep(500);
            retries += 1;
        }
        synchronized (simMonitor.monitorNotifyObject){
            simMonitor.monitorNotifyObject.notify();
        }
        awaitFlush(queueFlusher);

        SimulationJobStatus status = mockSimulationDB.getLatestSimulationJobStatus(DispatcherTestUtils.simKey, DispatcherTestUtils.jobIndex);
        Assertions.assertTrue(status.getSchedulerStatus().isFailed());
        Assertions.assertTrue(mockHtcProxy.jobsKilledUnsafely.contains(status.getSimulationExecutionStatus().getHtcJobID()));
        Assertions.assertTrue(logOutPut.toString().contains(SimulationDispatcher.SimulationMonitor.QueueFlusher.timeOutFailure));

    }

    @Test
    public void queueFlusherKillsNoLongerReferencedSims() throws SQLException, DataAccessException, InterruptedException {
        SimulationDispatcher simulationDispatcher = SimulationDispatcher.simulationDispatcherCreator(mockSimulationDB, mockMessagingServiceInternal,
                mockMessagingServiceSim, mockHtcProxy, false);
        SimulationDispatcher.SimulationMonitor simMonitor = simulationDispatcher.simMonitor;
        SimulationDispatcher.SimulationMonitor.QueueFlusher queueFlusher = simMonitor.initialQueueFlusher;

        mockSimulationDB.insertUnreferencedSimKey(DispatcherTestUtils.simKey);
        DispatcherTestUtils.insertOrUpdateStatus(mockSimulationDB);
        Thread queueThread = new Thread(queueFlusher);
        queueThread.start();
        int retries = 0;
        while (queueThread.getState() != Thread.State.TIMED_WAITING){
            if (retries == 10){
                break;
            }
            Thread.sleep(500);
            retries += 1;
        }
        synchronized (simMonitor.monitorNotifyObject){
            simMonitor.monitorNotifyObject.notify();
        }
        awaitFlush(queueFlusher);
        SimulationJobStatus status = mockSimulationDB.getLatestSimulationJobStatus(DispatcherTestUtils.simKey, DispatcherTestUtils.jobIndex);
        Assertions.assertTrue(status.getSchedulerStatus().isFailed());
        Assertions.assertTrue(mockHtcProxy.jobsKilledUnsafely.contains(status.getSimulationExecutionStatus().getHtcJobID()));
        Assertions.assertTrue(logOutPut.toString().contains(SimulationDispatcher.SimulationMonitor.QueueFlusher.unreferencedFailure));
    }

}

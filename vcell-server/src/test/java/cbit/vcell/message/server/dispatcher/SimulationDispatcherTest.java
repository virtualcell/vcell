package cbit.vcell.message.server.dispatcher;

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
        SimulationDispatcher simulationDispatcher = SimulationDispatcher.simulationDispatcherCreator(mockSimulationDB, mockMessagingServiceInternal,
                mockMessagingServiceSim, mockHtcProxy, true);
        SimulationDispatcher.DispatchThread thread = simulationDispatcher.dispatchThread;
        synchronized (thread.dispatcherNotifyObject){
            thread.dispatcherNotifyObject.notify();
        }

        // Check that the simulation is in waiting, for the dispatcher hasn't consumed it's request yet
        SimulationJobStatus jobStatus = mockSimulationDB.getLatestSimulationJobStatus(DispatcherTestUtils.simKey, 0);
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isWaiting(), "Still waiting.");

        synchronized (thread.finishListener){
            thread.finishListener.wait();
        }

        // Makes sure that requests that have no simulation reference within the DB are failed
        jobStatus = mockSimulationDB.getLatestSimulationJobStatus(DispatcherTestUtils.simKey, 0);
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isFailed(), "Simulation gets aborted since theres no simulation in DB.");

    }

    @Test
    public void dispatcherThreadDispatchesWaitingJobsWithSimulationsIn() throws SQLException, DataAccessException, InterruptedException, PropertyVetoException, MathException, ExpressionBindingException {
        SimulationDispatcher simulationDispatcher = SimulationDispatcher.simulationDispatcherCreator(mockSimulationDB, mockMessagingServiceInternal,
                mockMessagingServiceSim, mockHtcProxy, true);
        SimulationDispatcher.DispatchThread thread = simulationDispatcher.dispatchThread;
        // Create and insert simulation. Then ensure that this simulation has it's job status changed to dispatched
        Simulation mockSimulation = DispatcherTestUtils.createMockSimulation(20, 20, 20);
        mockSimulationDB.insertSimulation(DispatcherTestUtils.alice, mockSimulation);
        DispatcherTestUtils.insertOrUpdateStatus(mockSimulation.getKey(), DispatcherTestUtils.jobIndex, DispatcherTestUtils.taskID, DispatcherTestUtils.alice,
                SimulationJobStatus.SchedulerStatus.WAITING, mockSimulationDB);
        synchronized (thread.dispatcherNotifyObject){
            thread.dispatcherNotifyObject.notify();
        }
        synchronized (thread.finishListener){
            thread.finishListener.wait();
        }

        SimulationJobStatus jobStatus = mockSimulationDB.getLatestSimulationJobStatus(mockSimulation.getKey(), 0);
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isDispatched(), "Dispatches");
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
        synchronized (queueFlusher.finishListener){
            queueFlusher.finishListener.wait();
        }

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
        synchronized (queueFlusher.finishListener){
            queueFlusher.finishListener.wait();
        }
        SimulationJobStatus status = mockSimulationDB.getLatestSimulationJobStatus(DispatcherTestUtils.simKey, DispatcherTestUtils.jobIndex);
        Assertions.assertTrue(status.getSchedulerStatus().isFailed());
        Assertions.assertTrue(mockHtcProxy.jobsKilledUnsafely.contains(status.getSimulationExecutionStatus().getHtcJobID()));
        Assertions.assertTrue(logOutPut.toString().contains(SimulationDispatcher.SimulationMonitor.QueueFlusher.unreferencedFailure));
    }

}

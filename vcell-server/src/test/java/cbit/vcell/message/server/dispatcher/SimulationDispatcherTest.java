package cbit.vcell.message.server.dispatcher;

import cbit.vcell.math.MathException;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.solver.Simulation;
import org.junit.jupiter.api.*;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

@Tag("Fast")
public class SimulationDispatcherTest {
    private final static User testUser = DispatcherTestUtils.alice;
    private MockSimulationDB mockSimulationDB = new MockSimulationDB();
    private MockMessagingService mockMessagingServiceInternal = new MockMessagingService();
    private MockMessagingService mockMessagingServiceSim = new MockMessagingService();
    private MockHtcProxy mockHtcProxy = new MockHtcProxy(null, null);

    @BeforeAll
    public static void setSystemProperties(){
        DispatcherTestUtils.setRequiredProperties();
    }

    @AfterAll
    public static void restoreSystemProperties(){
        DispatcherTestUtils.restoreRequiredProperties();
    }

    @BeforeEach
    public void beforeEach(){
        mockSimulationDB = new MockSimulationDB();
    }

    //################# Test Simulation Service Impl #######################
    // All the get functions withing SimulationDispatcher seem to be exercising the DB and not simulation control, so not tested

    @Test
    public void onStartRequestTest() throws DataAccessException, SQLException {
        SimulationDispatcher simulationDispatcher = SimulationDispatcher.simulationDispatcherCreator(mockSimulationDB, mockMessagingServiceInternal, mockMessagingServiceSim, mockHtcProxy);
        SimulationStatus simStatus = simulationDispatcher.simServiceImpl.startSimulation(testUser, DispatcherTestUtils.simID, 1);
        SimulationJobStatus jobStatus = mockSimulationDB.getLatestSimulationJobStatus(DispatcherTestUtils.simKey, 0);
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isWaiting());
    }

    @Test
    public void onStopRequestTest() throws DataAccessException, SQLException {
        DispatcherTestUtils.insertOrUpdateStatus(mockSimulationDB);
        SimulationDispatcher simulationDispatcher = SimulationDispatcher.simulationDispatcherCreator(mockSimulationDB, mockMessagingServiceInternal, mockMessagingServiceSim, mockHtcProxy);
        SimulationStatus simStatus = simulationDispatcher.simServiceImpl.stopSimulation(testUser, DispatcherTestUtils.simID);
        SimulationJobStatus jobStatus = mockSimulationDB.getLatestSimulationJobStatus(DispatcherTestUtils.simKey, 0);
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isStopped());

        String s = mockMessagingServiceInternal.mockVCMessageSession.getTopicMessage(VCellTopic.ServiceControlTopic).getStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY);
        Assertions.assertEquals(MessageConstants.MESSAGE_TYPE_STOPSIMULATION_VALUE, s);
    }


    //###################### Test Dispatcher Thread ###########################
    @Test
    public void dispatcherThreadTest() throws SQLException, DataAccessException, InterruptedException, PropertyVetoException, MathException, ExpressionBindingException {
        DispatcherTestUtils.insertOrUpdateStatus(mockSimulationDB, SimulationJobStatus.SchedulerStatus.WAITING);
        SimulationDispatcher simulationDispatcher = SimulationDispatcher.simulationDispatcherCreator(mockSimulationDB, mockMessagingServiceInternal, mockMessagingServiceSim, mockHtcProxy);
        SimulationDispatcher.DispatchThread thread = simulationDispatcher.dispatchThread;
        synchronized (thread.notifyObject){
            thread.notifyObject.notify();
        }
        SimulationJobStatus jobStatus = mockSimulationDB.getLatestSimulationJobStatus(DispatcherTestUtils.simKey, 0);
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isWaiting(), "Still waiting.");

        // needs time for the dispatcher thread to fully update
        Thread.sleep(1000);

        jobStatus = mockSimulationDB.getLatestSimulationJobStatus(DispatcherTestUtils.simKey, 0);
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isFailed(), "Simulation gets aborted since theres no simulation in DB.");

        Simulation mockSimulation = DispatcherTestUtils.createMockSimulation(20, 20, 20);
        mockSimulationDB.insertSimulation(DispatcherTestUtils.alice, mockSimulation);
        DispatcherTestUtils.insertOrUpdateStatus(mockSimulation.getKey(), DispatcherTestUtils.jobIndex, DispatcherTestUtils.taskID, DispatcherTestUtils.alice,
                SimulationJobStatus.SchedulerStatus.WAITING, mockSimulationDB);
        synchronized (thread.notifyObject){
            thread.notifyObject.notify();
        }
        Thread.sleep(1000);

        jobStatus = mockSimulationDB.getLatestSimulationJobStatus(mockSimulation.getKey(), 0);
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isDispatched(), "Dispatches");
    }



    //###################### Test Simulation Monitor






}

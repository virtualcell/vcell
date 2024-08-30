package cbit.vcell.message.server.dispatcher;

import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationStatus;
import org.junit.jupiter.api.*;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.sql.SQLException;

@Tag("Fast")
public class SimulationDispatcherTest {
    private final static User testUser = DispatcherTestUtils.alice;
    private MockSimulationDB mockSimulationDB = new MockSimulationDB();
    private MockMessagingService mockMessagingServiceInternal = new MockMessagingService();
    private MockMessagingService mockMessagingServiceSim = new MockMessagingService();

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


    @Test
    public void onStartRequestTest() throws DataAccessException, SQLException {
        SimulationDispatcher simulationDispatcher = SimulationDispatcher.simulationDispatcherCreator(mockSimulationDB, mockMessagingServiceInternal, mockMessagingServiceSim);
        SimulationStatus simStatus = simulationDispatcher.simServiceImpl.startSimulation(testUser, DispatcherTestUtils.simID, 1);
        SimulationJobStatus jobStatus = mockSimulationDB.getLatestSimulationJobStatus(DispatcherTestUtils.simKey, 0);
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isWaiting());
    }

    @Test
    public void onStopRequestTest() throws DataAccessException, SQLException {
        DispatcherTestUtils.insertOrUpdateStatus(mockSimulationDB);
        SimulationDispatcher simulationDispatcher = SimulationDispatcher.simulationDispatcherCreator(mockSimulationDB, mockMessagingServiceInternal, mockMessagingServiceSim);
        SimulationStatus simStatus = simulationDispatcher.simServiceImpl.stopSimulation(testUser, DispatcherTestUtils.simID);
        SimulationJobStatus jobStatus = mockSimulationDB.getLatestSimulationJobStatus(DispatcherTestUtils.simKey, 0);
        Assertions.assertTrue(jobStatus.getSchedulerStatus().isStopped());
    }

}

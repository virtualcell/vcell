package cbit.vcell.message.server.dispatcher;

import cbit.rmi.event.WorkerEvent;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

@Tag("Fast")
public class SimulationStateMachineTest {

    private static final VCellServerID testVCellServerID = VCellServerID.getServerID("test");
    private static final User testUser = new User("Alice", new KeyValue("0"));


    private MockSimulationDB simulationDB;

    @BeforeEach
    public void setUp(){
        simulationDB = new MockSimulationDB();
    }

    private record ChangedStateValues(
            VCSimulationIdentifier simID,
            SimulationJobStatus.SchedulerStatus schedulerStatus,
            int taskID,
            String changesResult
    ){ }

    private interface WorkerEventFactory {
        WorkerEvent createChangedWorker(ChangedStateValues c);
    }

    @Test
    public void workerEventRejectionsTest() throws SQLException, DataAccessException {
        KeyValue simKey = new KeyValue("1");
        VCSimulationIdentifier simID = new VCSimulationIdentifier(simKey, testUser);
        int jobIndex = 0;
        int taskID = 16;
        SimulationStateMachineCopy stateMachine = new SimulationStateMachineCopy(simKey, jobIndex);
        SimulationMessage acceptedSimulationMessage = SimulationMessage.workerAccepted("accepted");


        WorkerEventFactory workerEventFactory = (w) -> {return new WorkerEvent(WorkerEvent.JOB_ACCEPTED, simKey, w.simID,
                jobIndex, "",
                w.taskID, null, null,
                acceptedSimulationMessage);};


        ArrayList<ChangedStateValues> changedValues = new ArrayList<>(){{
            add(new ChangedStateValues(simID, SimulationJobStatus.SchedulerStatus.RUNNING, taskID, "No old status.")); // no old status failure
            add(new ChangedStateValues(simID, SimulationJobStatus.SchedulerStatus.COMPLETED, taskID, "Work is already done.")); // work is done failure
            add(new ChangedStateValues(simID, SimulationJobStatus.SchedulerStatus.RUNNING, 0, "Task ID is lower")); // old status has higher number taskID failure
        }};

        for (int i = 0; i < changedValues.size(); i++){
            ChangedStateValues workerEventChangedValues = changedValues.get(i);
            if (i == 1){
                simulationDB.insertSimulationJobStatus(new SimulationJobStatus(testVCellServerID, simID, jobIndex, Date.from(Instant.now()), workerEventChangedValues.schedulerStatus, taskID,
                        acceptedSimulationMessage, null, null));
            } else if (i > 1) {
                SimulationJobStatus failingStatus = new SimulationJobStatus(testVCellServerID, simID, 0, Date.from(Instant.now()), workerEventChangedValues.schedulerStatus, taskID,
                        acceptedSimulationMessage, null, null);
                simulationDB.updateSimulationJobStatus(failingStatus);
            }
            WorkerEvent workerEvent = workerEventFactory.createChangedWorker(workerEventChangedValues);
            Assertions.assertFalse(stateMachine.isWorkerEventOkay(workerEvent, simulationDB), workerEventChangedValues.changesResult);
        }

        ChangedStateValues passingWorkerValues= new ChangedStateValues(simID, null, taskID, "");
        WorkerEvent passingWorkerEvent = workerEventFactory.createChangedWorker(passingWorkerValues);

        for (SimulationJobStatus.SchedulerStatus passingStatus: SimulationJobStatus.SchedulerStatus.values()){
            if (!passingStatus.isDone()){
                SimulationJobStatus simulationJobStatus = new SimulationJobStatus(testVCellServerID, simID, jobIndex, Date.from(Instant.now()), passingStatus, taskID,
                        acceptedSimulationMessage, null, null);
                simulationDB.updateSimulationJobStatus(simulationJobStatus);
                Assertions.assertTrue(stateMachine.isWorkerEventOkay(passingWorkerEvent, simulationDB));
            }
        }

    }

    @Test
    public void stateShouldTransitionToFailure(){
        KeyValue simKey = new KeyValue("1");
        VCSimulationIdentifier simID = new VCSimulationIdentifier(simKey, testUser);
        int jobIndex = 0;
        int taskID = 16;
        SimulationStateMachineCopy stateMachine = new SimulationStateMachineCopy(simKey, jobIndex);
    }

    public void stateShouldTransitionToWaiting(){

    }

    public void stateShouldTransitionToDispatched(){

    }

    public void stateShouldTransitionToRunning(){

    }

    public void stateShouldTransitionToCompleted(){

    }


}

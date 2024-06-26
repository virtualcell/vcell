package org.vcell.restq.Simulations.Control;

import cbit.rmi.event.WorkerEvent;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import org.vcell.restq.Simulations.DTO.SimulationJobStatus;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.server.UpdateSynchronizationException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.restq.Simulations.DTO.StatusMessage;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SimulationDispatcherEngine {
    public static final Logger lg = LogManager.getLogger(cbit.vcell.message.server.dispatcher.SimulationDispatcherEngine.class);

    private HashMap<KeyValue, List<SimulationStateMachine>> simStateMachineHash = new HashMap<KeyValue, List<SimulationStateMachine>>();


    public SimulationDispatcherEngine() {
    }

    public SimulationStateMachine getSimulationStateMachine(KeyValue simulationKey, int jobIndex) {
        List<SimulationStateMachine> stateMachineList = simStateMachineHash.get(simulationKey);
        if (stateMachineList==null){
            stateMachineList = new ArrayList<SimulationStateMachine>();
            simStateMachineHash.put(simulationKey,stateMachineList);
        }
        for (SimulationStateMachine stateMachine : stateMachineList){
            if (stateMachine.getJobIndex() == jobIndex){
                return stateMachine;
            }
        }
        SimulationStateMachine newStateMachine = new SimulationStateMachine(simulationKey, jobIndex);
        stateMachineList.add(newStateMachine);
        return newStateMachine;
    }

    public void onDispatch(Simulation simulation, cbit.vcell.server.SimulationJobStatus simJobStatus, SimulationDatabase simulationDatabase, VCMessageSession dispatcherQueueSession) throws VCMessagingException, DataAccessException, SQLException {
        KeyValue simulationKey = simJobStatus.getVCSimulationIdentifier().getSimulationKey();
        SimulationStateMachine simStateMachine = getSimulationStateMachine(simulationKey, simJobStatus.getJobIndex());

        simStateMachine.onDispatch(simulation, simJobStatus, simulationDatabase, dispatcherQueueSession);
    }

    public ArrayList<StatusMessage> onStartRequest(VCSimulationIdentifier vcSimID, User user, int simulationScanCount, SimulationDatabase simulationDatabase, VCMessageSession session, VCMessageSession dispatcherQueueSession) throws VCMessagingException, DataAccessException, SQLException {
        KeyValue simKey = vcSimID.getSimulationKey();

        User.SpecialUser myUser = simulationDatabase.getUser(user.getName());
        boolean isAdmin = Arrays.asList(myUser.getMySpecials()).contains(User.SPECIAL_CLAIM.admins);

        SimulationInfo simulationInfo = null;
        cbit.vcell.server.SimulationJobStatus simJobStatus = null;
        ArrayList<StatusMessage> status = new ArrayList<>();
        try {
            simulationInfo = simulationDatabase.getSimulationInfo(user, simKey);
        } catch (DataAccessException ex) {
            if (lg.isWarnEnabled()) lg.warn("Bad simulation " + vcSimID);
            simJobStatus = new cbit.vcell.server.SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, -1, null,
                    cbit.vcell.server.SimulationJobStatus.SchedulerStatus.FAILED, 0, SimulationMessage.workerFailure("Failed to dispatch simulation: "+ ex.getMessage()), null, null);
            status.add(new StatusMessage(SimulationJobStatus.fromSimulationJobStatus(simJobStatus), user.getName(), null, null));
            return status;
        }
        if (simulationInfo == null) {
            if (lg.isWarnEnabled()) lg.warn("Can't start, simulation [" + vcSimID + "] doesn't exist in database");
            simJobStatus = new cbit.vcell.server.SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, -1, null,
                    cbit.vcell.server.SimulationJobStatus.SchedulerStatus.FAILED, 0, SimulationMessage.workerFailure("Can't start, simulation [" + vcSimID + "] doesn't exist"), null, null);
            status.add(new StatusMessage(SimulationJobStatus.fromSimulationJobStatus(simJobStatus), user.getName(), null, null));
            return status;
        }

        if (!isAdmin && simulationScanCount > Integer.parseInt(cbit.vcell.resource.PropertyLoader.getRequiredProperty(cbit.vcell.resource.PropertyLoader.maxJobsPerScan))) {
            if (lg.isWarnEnabled()) lg.warn("Too many simulations (" + simulationScanCount + ") for parameter scan." + vcSimID);
            simJobStatus = new cbit.vcell.server.SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, -1, null,
                    cbit.vcell.server.SimulationJobStatus.SchedulerStatus.FAILED, 0, SimulationMessage.workerFailure("Too many simulations (" + simulationScanCount + ") for parameter scan."), null, null);
            status.add(new StatusMessage(SimulationJobStatus.fromSimulationJobStatus(simJobStatus), user.getName(), null, null));
            return status;
        }

        for (int simulationJobIndex = 0; simulationJobIndex < simulationScanCount; simulationJobIndex++){
            SimulationStateMachine simStateMachine = getSimulationStateMachine(simKey, simulationJobIndex);
            try {
                status.add(simStateMachine.onStartRequest(user, vcSimID, simulationDatabase, session));
            }catch (UpdateSynchronizationException e){
                status.add(simStateMachine.onStartRequest(user, vcSimID, simulationDatabase, session));
            }
        }
        return status;
    }


    public ArrayList<StatusMessage> onStopRequest(VCSimulationIdentifier vcSimID, User user, SimulationDatabase simulationDatabase, VCMessageSession session) throws DataAccessException, VCMessagingException, SQLException {
        KeyValue simKey = vcSimID.getSimulationKey();

        cbit.vcell.server.SimulationJobStatus[] allActiveSimJobStatusArray = simulationDatabase.getActiveJobs(VCellServerID.getSystemServerID());
        ArrayList<cbit.vcell.server.SimulationJobStatus> simJobStatusArray = new ArrayList<cbit.vcell.server.SimulationJobStatus>();
        for (cbit.vcell.server.SimulationJobStatus activeSimJobStatus : allActiveSimJobStatusArray){
            if (activeSimJobStatus.getVCSimulationIdentifier().getSimulationKey().equals(vcSimID.getSimulationKey())){
                simJobStatusArray.add(activeSimJobStatus);
            }
        }
        ArrayList<StatusMessage> stoppedSimulations = new ArrayList<>();
        for (cbit.vcell.server.SimulationJobStatus simJobStatus : simJobStatusArray){
            SimulationStateMachine simStateMachine = getSimulationStateMachine(simKey, simJobStatus.getJobIndex());
            try {
                stoppedSimulations.add(simStateMachine.onStopRequest(user, simJobStatus, simulationDatabase, session));
            }catch (UpdateSynchronizationException e){
                stoppedSimulations.add(simStateMachine.onStopRequest(user, simJobStatus, simulationDatabase, session));
            }
        }
        return stoppedSimulations;
    }


    public void onWorkerEvent(WorkerEvent workerEvent, SimulationDatabase simulationDatabase, VCMessageSession session) {
        try {
            KeyValue simKey = workerEvent.getVCSimulationDataIdentifier().getSimulationKey();
            int jobIndex = workerEvent.getJobIndex();
            SimulationStateMachine simStateMachine = getSimulationStateMachine(simKey, jobIndex);
            simStateMachine.onWorkerEvent(workerEvent, simulationDatabase, session);
        } catch (Exception ex) {
            lg.error(ex.getMessage(),ex);
        }
    }


    public void onSystemAbort(cbit.vcell.server.SimulationJobStatus jobStatus, String failureMessage, SimulationDatabase simulationDatabase, VCMessageSession session) {
        try {
            KeyValue simKey = jobStatus.getVCSimulationIdentifier().getSimulationKey();
            int jobIndex = jobStatus.getJobIndex();
            SimulationStateMachine simStateMachine = getSimulationStateMachine(simKey, jobIndex);
            simStateMachine.onSystemAbort(jobStatus, failureMessage, simulationDatabase, session);
        } catch (Exception ex) {
            lg.error(ex.getMessage(),ex);
        }
    }
}

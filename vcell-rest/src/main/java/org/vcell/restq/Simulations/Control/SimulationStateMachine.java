package org.vcell.restq.Simulations.Control;

import cbit.rmi.event.WorkerEvent;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.message.*;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.messages.SimulationTaskMessage;
import org.vcell.restq.Simulations.DTO.SimulationExecutionStatus;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.server.*;
import cbit.vcell.solver.*;
import cbit.vcell.solver.server.SimulationMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.restq.Simulations.DTO.SimulationJobStatus;
import org.vcell.restq.Simulations.DTO.SimulationQueueEntryStatus;
import org.vcell.restq.Simulations.DTO.StatusMessage;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

public class SimulationStateMachine {
    public static final Logger lg = LogManager.getLogger(cbit.vcell.message.server.dispatcher.SimulationStateMachine.class);

    // bitmapped counter so that allows 3 retries for each request (but preserves ordinal nature)
    // bits 0-3: retry count
    // bits 4-31: submit
    // max retries must be less than 15.
    public static final int TASKID_USERCOUNTER_MASK		= SimulationStatus.TASKID_USERCOUNTER_MASK;
    public static final int TASKID_RETRYCOUNTER_MASK	= SimulationStatus.TASKID_RETRYCOUNTER_MASK;
    public static final int TASKID_USERINCREMENT	    = SimulationStatus.TASKID_USERINCREMENT;

    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_DEFAULT = 5;
    public static final int PRIORITY_HIGH = 9;

    private final KeyValue simKey;
    private final int jobIndex;

    /**
     * in memory storage of last time information about this job was received or status was unknown due
     * to transient failure or system restart
     */
    private long solverProcessTimestamp;

    public SimulationStateMachine(KeyValue simKey, int jobIndex){
        this.simKey = simKey;
        this.jobIndex = jobIndex;
        updateSolverProcessTimestamp();
    }

    private void updateSolverProcessTimestamp( ) {
        solverProcessTimestamp = System.currentTimeMillis();
    }

    /**
     * set to specified time (for mass setting)
     * @param solverProcessTimestamp
     */
    void setSolverProcessTimestamp(long solverProcessTimestamp) {
        this.solverProcessTimestamp = solverProcessTimestamp;
    }

    public KeyValue getSimKey() {
        return simKey;
    }

    public int getJobIndex() {
        return jobIndex;
    }


    long getSolverProcessTimestamp() {
        return solverProcessTimestamp;
    }

    public synchronized void onWorkerEvent(WorkerEvent workerEvent, SimulationDatabase simulationDatabase, VCMessageSession session) throws DataAccessException, VCMessagingException, SQLException {
        updateSolverProcessTimestamp();
        WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
        VCMongoMessage.sendWorkerEvent(workerEventMessage);

        String userName = workerEvent.getUserName(); // as the filter of the client
        int workerEventTaskID = workerEvent.getTaskID();

        if (lg.isTraceEnabled()) lg.trace("onWorkerEventMessage[" + workerEvent.getEventTypeID() + "," + workerEvent.getSimulationMessage() + "][simid=" + workerEvent.getVCSimulationDataIdentifier() + ",job=" + jobIndex + ",task=" + workerEventTaskID + "]");

        VCSimulationDataIdentifier vcSimDataID = workerEvent.getVCSimulationDataIdentifier();
        if (vcSimDataID == null) {
            VCMongoMessage.sendInfo("onWorkerEvent() ignoring WorkerEvent - no SimID in message): "+workerEvent.show());
            return;
        }
        KeyValue simKey = vcSimDataID.getSimulationKey();
        cbit.vcell.server.SimulationJobStatus oldSimulationJobStatus = simulationDatabase.getLatestSimulationJobStatus(simKey, jobIndex);

        if (oldSimulationJobStatus == null){
            VCMongoMessage.sendInfo("onWorkerEvent() ignoring WorkerEvent, no current SimulationJobStatus: "+workerEvent.show());
            return;
        }
        if (oldSimulationJobStatus == null || oldSimulationJobStatus.getSchedulerStatus().isDone() || oldSimulationJobStatus.getTaskID() > workerEventTaskID){
            VCMongoMessage.sendInfo("onWorkerEvent() ignoring outdated WorkerEvent, (currState="+oldSimulationJobStatus.getSchedulerStatus().getDescription()+"): "+workerEvent.show());
            return;
        }
        int taskID = oldSimulationJobStatus.getTaskID();
        cbit.vcell.server.SimulationJobStatus.SchedulerStatus oldSchedulerStatus = oldSimulationJobStatus.getSchedulerStatus();

        //
        // status information (initialized as if new record)
        //
        Date startDate = null;
        Date lastUpdateDate = null;
        Date endDate = null;
        boolean hasData = false;
        HtcJobID htcJobID = null;
        String computeHost = null;
        VCellServerID vcServerID = VCellServerID.getSystemServerID();
        Date submitDate = null;
        Date queueDate = null;
        int queuePriority = PRIORITY_DEFAULT;
        cbit.vcell.server.SimulationJobStatus.SimulationQueueID simQueueID = cbit.vcell.server.SimulationJobStatus.SimulationQueueID.QUEUE_ID_WAITING;


        //
        // update using previously stored status (if available).
        //
        cbit.vcell.server.SimulationExecutionStatus oldSimExeStatus = oldSimulationJobStatus.getSimulationExecutionStatus();
        if (oldSimExeStatus!=null && oldSimExeStatus.getStartDate()!=null){
            startDate = oldSimExeStatus.getStartDate();
        }
        if (oldSimExeStatus!=null && oldSimExeStatus.getLatestUpdateDate()!=null){
            lastUpdateDate = oldSimExeStatus.getLatestUpdateDate();
        }
        if (oldSimExeStatus!=null && oldSimExeStatus.getEndDate()!=null){
            endDate = oldSimExeStatus.getEndDate();
        }
        if (oldSimExeStatus!=null && oldSimExeStatus.hasData()){
            hasData = true;
        }
        if (oldSimExeStatus!=null && oldSimExeStatus.getComputeHost()!=null){
            computeHost = oldSimExeStatus.getComputeHost();
        }
        if (oldSimExeStatus!=null && oldSimExeStatus.getHtcJobID()!=null){
            htcJobID = oldSimExeStatus.getHtcJobID();
        }
        vcServerID = oldSimulationJobStatus.getServerID();
        submitDate = oldSimulationJobStatus.getSubmitDate();
        cbit.vcell.server.SimulationQueueEntryStatus oldQueueStatus = oldSimulationJobStatus.getSimulationQueueEntryStatus();
        if (oldQueueStatus!=null && oldQueueStatus.getQueueDate()!=null){
            queueDate = oldQueueStatus.getQueueDate();
        }
        if (oldQueueStatus!=null){
            queuePriority = oldQueueStatus.getQueuePriority();
        }
        if (oldQueueStatus!=null && oldQueueStatus.getQueueID()!=null){
            simQueueID = oldQueueStatus.getQueueID();
        }

        //
        // update using new information from event
        //
        if (workerEvent.getHtcJobID()!=null){
            htcJobID = workerEvent.getHtcJobID();
        }
        if (workerEvent.getHostName()!=null){
            computeHost = workerEvent.getHostName();
        }
        SimulationMessage workerEventSimulationMessage = workerEvent.getSimulationMessage();
        if (workerEventSimulationMessage.getHtcJobId()!=null){
            htcJobID = workerEventSimulationMessage.getHtcJobId();
        }


        cbit.vcell.server.SimulationJobStatus newJobStatus = null;

        if (workerEvent.isAcceptedEvent()) {
            //
            // job message accepted by HtcSimulationWorker and sent to Scheduler (PBS/SGE/SLURM) (with a htcJobID) ... previous state should be "WAITING"
            //
            if (oldSchedulerStatus.isWaiting() || oldSchedulerStatus.isQueued()) {
                // new queue status
                cbit.vcell.server.SimulationQueueEntryStatus newQueueStatus = new cbit.vcell.server.SimulationQueueEntryStatus(queueDate, queuePriority, cbit.vcell.server.SimulationJobStatus.SimulationQueueID.QUEUE_ID_NULL);

                // new exe status
                lastUpdateDate = new Date();
                startDate = lastUpdateDate;
                endDate = null;
                cbit.vcell.server.SimulationExecutionStatus newExeStatus = new cbit.vcell.server.SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, htcJobID);

                newJobStatus = new cbit.vcell.server.SimulationJobStatus(vcServerID, vcSimDataID.getVcSimID(), jobIndex, submitDate, cbit.vcell.server.SimulationJobStatus.SchedulerStatus.DISPATCHED,
                        taskID, workerEventSimulationMessage, newQueueStatus, newExeStatus);
            }

        } else if (workerEvent.isStartingEvent()) {
            // only update database when the job event changes from started to running. The later progress event will not be recorded.
            if ( oldSchedulerStatus.isWaiting() || oldSchedulerStatus.isQueued() || oldSchedulerStatus.isDispatched() || oldSchedulerStatus.isRunning()) {
                // new queue status
                cbit.vcell.server.SimulationQueueEntryStatus newQueueStatus = new cbit.vcell.server.SimulationQueueEntryStatus(queueDate, queuePriority, cbit.vcell.server.SimulationJobStatus.SimulationQueueID.QUEUE_ID_NULL);

                // new exe status
                lastUpdateDate = new Date();
                if (startDate == null){
                    startDate = lastUpdateDate;
                }
                cbit.vcell.server.SimulationExecutionStatus newExeStatus = new cbit.vcell.server.SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, htcJobID);

                newJobStatus = new cbit.vcell.server.SimulationJobStatus(vcServerID, vcSimDataID.getVcSimID(), jobIndex, submitDate, cbit.vcell.server.SimulationJobStatus.SchedulerStatus.RUNNING,
                        taskID, workerEventSimulationMessage, newQueueStatus, newExeStatus);
            }

        } else if (workerEvent.isNewDataEvent()) {
            if (oldSchedulerStatus.isWaiting() || oldSchedulerStatus.isQueued() || oldSchedulerStatus.isDispatched() || oldSchedulerStatus.isRunning()){

                if (!oldSchedulerStatus.isRunning() || simQueueID != cbit.vcell.server.SimulationJobStatus.SimulationQueueID.QUEUE_ID_NULL || hasData==false){

                    // new queue status
                    cbit.vcell.server.SimulationQueueEntryStatus newQueueStatus = new cbit.vcell.server.SimulationQueueEntryStatus(queueDate, queuePriority, cbit.vcell.server.SimulationJobStatus.SimulationQueueID.QUEUE_ID_NULL);

                    // new exe status
                    if (startDate == null){
                        startDate = lastUpdateDate;
                    }
                    hasData = true;
                    cbit.vcell.server.SimulationExecutionStatus newExeStatus = new cbit.vcell.server.SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, htcJobID);

                    newJobStatus = new cbit.vcell.server.SimulationJobStatus(vcServerID, vcSimDataID.getVcSimID(), jobIndex, submitDate, cbit.vcell.server.SimulationJobStatus.SchedulerStatus.RUNNING,
                            taskID, workerEventSimulationMessage, newQueueStatus, newExeStatus);
                }
            }

        } else if (workerEvent.isProgressEvent() || workerEvent.isWorkerAliveEvent()) {
            if (oldSchedulerStatus.isWaiting() || oldSchedulerStatus.isQueued() || oldSchedulerStatus.isDispatched() || oldSchedulerStatus.isRunning()){


                if (!oldSchedulerStatus.isRunning() || simQueueID != cbit.vcell.server.SimulationJobStatus.SimulationQueueID.QUEUE_ID_NULL){
                    // new queue status
                    cbit.vcell.server.SimulationQueueEntryStatus newQueueStatus = new cbit.vcell.server.SimulationQueueEntryStatus(queueDate, queuePriority, cbit.vcell.server.SimulationJobStatus.SimulationQueueID.QUEUE_ID_NULL);

                    // new exe status
                    if (startDate == null){
                        startDate = lastUpdateDate;
                    }
                    cbit.vcell.server.SimulationExecutionStatus newExeStatus = new cbit.vcell.server.SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, htcJobID);

                    newJobStatus = new cbit.vcell.server.SimulationJobStatus(vcServerID, vcSimDataID.getVcSimID(), jobIndex, submitDate, cbit.vcell.server.SimulationJobStatus.SchedulerStatus.RUNNING,
                            taskID, workerEventSimulationMessage, newQueueStatus, newExeStatus);

                }else if (oldSchedulerStatus.isRunning()){
                    if (oldSimExeStatus != null) {
//						Date latestUpdate = oldSimExeStatus.getLatestUpdateDate();
//						if (System.currentTimeMillis() - latestUpdate.getTime() >= MessageConstants.INTERVAL_PING_SERVER_MS * 3 / 5) {
                        // new queue status
                        cbit.vcell.server.SimulationQueueEntryStatus newQueueStatus = new cbit.vcell.server.SimulationQueueEntryStatus(queueDate, queuePriority, cbit.vcell.server.SimulationJobStatus.SimulationQueueID.QUEUE_ID_NULL);
                        cbit.vcell.server.SimulationExecutionStatus newExeStatus = new cbit.vcell.server.SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, htcJobID);

                        newJobStatus = new cbit.vcell.server.SimulationJobStatus(vcServerID, vcSimDataID.getVcSimID(), jobIndex, submitDate, cbit.vcell.server.SimulationJobStatus.SchedulerStatus.RUNNING,
                                taskID, workerEventSimulationMessage, newQueueStatus, newExeStatus);
                    }
//					}
                }
            }

        } else if (workerEvent.isCompletedEvent()) {
            if (oldSchedulerStatus.isWaiting() || oldSchedulerStatus.isQueued() || oldSchedulerStatus.isDispatched() || oldSchedulerStatus.isRunning()){
                // new queue status
                cbit.vcell.server.SimulationQueueEntryStatus newQueueStatus = new cbit.vcell.server.SimulationQueueEntryStatus(queueDate, queuePriority, cbit.vcell.server.SimulationJobStatus.SimulationQueueID.QUEUE_ID_NULL);

                // new exe status
                endDate = new Date();
                hasData = true;

                cbit.vcell.server.SimulationExecutionStatus newExeStatus = new cbit.vcell.server.SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, htcJobID);

                newJobStatus = new cbit.vcell.server.SimulationJobStatus(vcServerID, vcSimDataID.getVcSimID(), jobIndex, submitDate, cbit.vcell.server.SimulationJobStatus.SchedulerStatus.COMPLETED,
                        taskID, workerEventSimulationMessage, newQueueStatus, newExeStatus);

            }

        } else if (workerEvent.isFailedEvent()) {
            if (oldSchedulerStatus.isWaiting() || oldSchedulerStatus.isQueued() || oldSchedulerStatus.isDispatched() || oldSchedulerStatus.isRunning()){
                // new queue status
                cbit.vcell.server.SimulationQueueEntryStatus newQueueStatus = new cbit.vcell.server.SimulationQueueEntryStatus(queueDate, queuePriority, cbit.vcell.server.SimulationJobStatus.SimulationQueueID.QUEUE_ID_NULL);

                // new exe status
                endDate = new Date();

                cbit.vcell.server.SimulationExecutionStatus newExeStatus = new cbit.vcell.server.SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, htcJobID);

                newJobStatus = new cbit.vcell.server.SimulationJobStatus(vcServerID, vcSimDataID.getVcSimID(), jobIndex, submitDate, cbit.vcell.server.SimulationJobStatus.SchedulerStatus.FAILED,
                        taskID, workerEventSimulationMessage, newQueueStatus, newExeStatus);

            }
        } else if (workerEvent.isWorkerExitErrorEvent()) {
            if (oldSchedulerStatus.isWaiting() || oldSchedulerStatus.isQueued() || oldSchedulerStatus.isDispatched() || oldSchedulerStatus.isRunning()){
                // new queue status
                cbit.vcell.server.SimulationQueueEntryStatus newQueueStatus = new cbit.vcell.server.SimulationQueueEntryStatus(queueDate, queuePriority, cbit.vcell.server.SimulationJobStatus.SimulationQueueID.QUEUE_ID_NULL);

                // new exe status
                endDate = new Date();

                cbit.vcell.server.SimulationExecutionStatus newExeStatus = new cbit.vcell.server.SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, htcJobID);

                SimulationMessage simulationMessage = SimulationMessage.workerFailure("solver stopped unexpectedly, "+workerEventSimulationMessage.getDisplayMessage());

                newJobStatus = new cbit.vcell.server.SimulationJobStatus(vcServerID, vcSimDataID.getVcSimID(), jobIndex, submitDate, cbit.vcell.server.SimulationJobStatus.SchedulerStatus.FAILED,
                        taskID, simulationMessage, newQueueStatus, newExeStatus);

            }
        }
        if (newJobStatus!=null){
            if (!newJobStatus.compareEqual(oldSimulationJobStatus) || workerEvent.isProgressEvent() || workerEvent.isNewDataEvent()) {
                Double progress = workerEvent.getProgress();
                Double timepoint = workerEvent.getTimePoint();
                RunningStateInfo runningStateInfo = null;
                if (progress != null && timepoint != null){
                    runningStateInfo = new RunningStateInfo(progress,timepoint);
                }
                simulationDatabase.updateSimulationJobStatus(newJobStatus,runningStateInfo);
                cbit.vcell.message.messages.StatusMessage msgForClient = new cbit.vcell.message.messages.StatusMessage(newJobStatus, userName, progress, timepoint);
                msgForClient.sendToClient(session);
                if (lg.isTraceEnabled()) lg.trace("Send status to client: " + msgForClient);
            } else {
                simulationDatabase.updateSimulationJobStatus(newJobStatus);
                cbit.vcell.message.messages.StatusMessage msgForClient = new cbit.vcell.message.messages.StatusMessage(newJobStatus, userName, null, null);
                msgForClient.sendToClient(session);
                if (lg.isTraceEnabled()) lg.trace("Send status to client: " + msgForClient);
            }
        }else if (workerEvent.isProgressEvent() || workerEvent.isNewDataEvent()){
            Double progress = workerEvent.getProgress();
            Double timepoint = workerEvent.getTimePoint();
            RunningStateInfo runningStateInfo = null;
            if (progress!=null && timepoint!=null){
                runningStateInfo = new RunningStateInfo(progress,timepoint);
            }
            simulationDatabase.updateSimulationJobStatus(oldSimulationJobStatus,runningStateInfo);
            cbit.vcell.message.messages.StatusMessage msgForClient = new cbit.vcell.message.messages.StatusMessage(oldSimulationJobStatus, userName, progress, timepoint);
            msgForClient.sendToClient(session);
            if (lg.isTraceEnabled()) lg.trace("Send status to client: " + msgForClient);
        }else{
            VCMongoMessage.sendInfo("onWorkerEvent() ignoring WorkerEvent (currState="+oldSchedulerStatus.getDescription()+"): "+workerEvent.show());
        }
//		addStateMachineTransition(new StateMachineTransition(new WorkerStateMachineEvent(taskID, workerEvent), oldSimulationJobStatus, newJobStatus));

    }

    public synchronized StatusMessage onStartRequest(User user, VCSimulationIdentifier vcSimID, SimulationDatabase simulationDatabase, VCMessageSession session) throws VCMessagingException, DataAccessException, SQLException {

        if (!user.equals(vcSimID.getOwner())) {
            lg.error(user + " is not authorized to start simulation (key=" + simKey + ")");
            cbit.vcell.server.SimulationJobStatus simulationJobStatus = new cbit.vcell.server.SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, 0, null,
                    cbit.vcell.server.SimulationJobStatus.SchedulerStatus.FAILED, 0, SimulationMessage.workerFailure("You are not authorized to start this simulation!"), null, null);
            StatusMessage message = new StatusMessage(SimulationJobStatus.fromSimulationJobStatus(simulationJobStatus), user.getName(), null, null);
            VCMongoMessage.sendInfo("onStartRequest("+vcSimID.getID()+") ignoring start simulation request - wrong user): simID="+vcSimID);
            return message;
        }

        cbit.vcell.server.SimulationJobStatus newJobStatus = saveSimulationStartRequest(vcSimID, jobIndex, simulationDatabase);

        return new StatusMessage(SimulationJobStatus.fromSimulationJobStatus(newJobStatus), user.getName(), null, null);
    }

    public static cbit.vcell.server.SimulationJobStatus saveSimulationStartRequest(VCSimulationIdentifier vcSimID, int jobIndex, SimulationDatabase simulationDatabase) throws DataAccessException, SQLException {
        //
        // get latest simulation job task (if any).
        //
        cbit.vcell.server.SimulationJobStatus oldSimulationJobStatus = simulationDatabase.getLatestSimulationJobStatus(vcSimID.getSimulationKey(), jobIndex);
        int oldTaskID = -1;
        if (oldSimulationJobStatus != null){
            oldTaskID = oldSimulationJobStatus.getTaskID();
        }
        // if already started by another thread
        if (oldSimulationJobStatus != null && !oldSimulationJobStatus.getSchedulerStatus().isDone()) {
            VCMongoMessage.sendInfo("onStartRequest("+ vcSimID.getID()+") ignoring start simulation request - (currentSimJobStatus:"+oldSimulationJobStatus.getSchedulerStatus().getDescription()+"): simID="+ vcSimID);
            throw new RuntimeException("Can't start, simulation[" + vcSimID + "] job [" + jobIndex + "] task [" + oldTaskID + "] is running already ("+oldSimulationJobStatus.getSchedulerStatus().getDescription()+")");
        }

        int newTaskID;

        if (oldTaskID > -1){
            // calculate new task
            newTaskID = (oldTaskID & SimulationStatus.TASKID_USERCOUNTER_MASK) + SimulationStatus.TASKID_USERINCREMENT;
        }else{
            // first task, start with 0
            newTaskID = 0;
        }

        Date currentDate = new Date();
        // new queue status
        cbit.vcell.server.SimulationQueueEntryStatus newQueueStatus = new cbit.vcell.server.SimulationQueueEntryStatus(currentDate, PRIORITY_DEFAULT, cbit.vcell.server.SimulationJobStatus.SimulationQueueID.QUEUE_ID_WAITING);

        // new exe status
        Date lastUpdateDate = new Date();
        String computeHost = null;
        Date startDate = null;
        Date endDate = null;
        HtcJobID htcJobID = null;
        boolean hasData = false;

        cbit.vcell.server.SimulationExecutionStatus newExeStatus = new cbit.vcell.server.SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, htcJobID);

        VCellServerID vcServerID = VCellServerID.getSystemServerID();
        Date submitDate = currentDate;

        cbit.vcell.server.SimulationJobStatus newJobStatus = new cbit.vcell.server.SimulationJobStatus(vcServerID, vcSimID, jobIndex, submitDate, cbit.vcell.server.SimulationJobStatus.SchedulerStatus.WAITING,
                newTaskID, SimulationMessage.MESSAGE_JOB_WAITING, newQueueStatus, newExeStatus);

        simulationDatabase.insertSimulationJobStatus(newJobStatus);
        return newJobStatus;
    }


    public synchronized void onDispatch(Simulation simulation, cbit.vcell.server.SimulationJobStatus oldSimulationJobStatus, SimulationDatabase simulationDatabase, VCMessageSession session) throws VCMessagingException, DataAccessException, SQLException {
        updateSolverProcessTimestamp();
        VCSimulationIdentifier vcSimID = oldSimulationJobStatus.getVCSimulationIdentifier();
        int taskID = oldSimulationJobStatus.getTaskID();

        if (!oldSimulationJobStatus.getSchedulerStatus().isWaiting()) {
            VCMongoMessage.sendInfo("onDispatch("+vcSimID.getID()+") Can't start, simulation[" + vcSimID + "] job [" + jobIndex + "] task [" + taskID + "] is already dispatched ("+oldSimulationJobStatus.getSchedulerStatus().getDescription()+")");
            throw new RuntimeException("Can't start, simulation[" + vcSimID + "] job [" + jobIndex + "] task [" + taskID + "] is already dispatched ("+oldSimulationJobStatus.getSchedulerStatus().getDescription()+")");
        }

        FieldDataIdentifierSpec[] fieldDataIdentifierSpecs = simulationDatabase.getFieldDataIdentifierSpecs(simulation);
        //Check if user wants long running sims activated in SlurmProxy.generateScript(...)
        //only happens if user is allowed to be power user (entry in vc_specialusers table) and
        //has checked the 'timeoutDisabledCheckBox' in SolverTaskDescriptionAdvancedPanel on the client-side GUI
        boolean isPowerUser = simulation.getSolverTaskDescription().isTimeoutDisabled();//Set from GUI
        if(isPowerUser) {//Check if user allowed to be power user for 'special1' long running sims (see User.SPECIALS and vc_specialusers table)
            User.SpecialUser myUser = simulationDatabase.getUser(simulation.getVersion().getOwner().getName());
            //'powerUsers' (previously called 'special1') assigned to users by request to allow long running sims
            isPowerUser = isPowerUser && Arrays.asList(myUser.getMySpecials()).contains(User.SPECIAL_CLAIM.powerUsers);
        }
        SimulationTask simulationTask = new SimulationTask(new SimulationJob(simulation, jobIndex, fieldDataIdentifierSpecs), taskID,null,isPowerUser);

        double requiredMemMB = simulationTask.getEstimatedMemorySizeMB();
        //SimulationStateMachine ultimately instantiated from {vcellroot}/docker/build/Dockerfile-sched-dev by way of cbit.vcell.message.server.dispatcher.SimulationDispatcher
        String vcellUserid = simulationTask.getUser().getName();
        KeyValue simID = simulationTask.getSimulationInfo().getSimulationVersion().getVersionKey();
        SolverDescription solverDescription = simulationTask.getSimulation().getSolverTaskDescription().getSolverDescription();

        HtcProxy.MemLimitResults allowableMemMB = HtcProxy.getMemoryLimit(vcellUserid,simID,solverDescription, requiredMemMB, isPowerUser);

        final cbit.vcell.server.SimulationJobStatus newSimJobStatus;
        if (requiredMemMB > allowableMemMB.getMemLimit()) {
            //
            // fail the simulation
            //
            Date currentDate = new Date();
            // new queue status
            cbit.vcell.server.SimulationQueueEntryStatus newQueueStatus = new cbit.vcell.server.SimulationQueueEntryStatus(currentDate, PRIORITY_DEFAULT, cbit.vcell.server.SimulationJobStatus.SimulationQueueID.QUEUE_ID_NULL);
            cbit.vcell.server.SimulationExecutionStatus newSimExeStatus = new cbit.vcell.server.SimulationExecutionStatus(null,  null, new Date(), null, false, null);
            newSimJobStatus = new cbit.vcell.server.SimulationJobStatus(VCellServerID.getSystemServerID(),vcSimID,jobIndex,
                    oldSimulationJobStatus.getSubmitDate(), cbit.vcell.server.SimulationJobStatus.SchedulerStatus.FAILED,taskID,
                    SimulationMessage.jobFailed("simulation required "+requiredMemMB+"MB of memory, only "+allowableMemMB.getMemLimit()+"MB allowed from "+allowableMemMB.getMemLimitSource()),
                    newQueueStatus,newSimExeStatus);

            simulationDatabase.updateSimulationJobStatus(newSimJobStatus);

            cbit.vcell.message.messages.StatusMessage message = new cbit.vcell.message.messages.StatusMessage(newSimJobStatus, simulation.getVersion().getOwner().getName(), null, null);
            message.sendToClient(session);

        }else{
            //
            // dispatch the simulation, new queue status
            //
            Date currentDate = new Date();
            cbit.vcell.server.SimulationQueueEntryStatus newQueueStatus = new cbit.vcell.server.SimulationQueueEntryStatus(currentDate, PRIORITY_DEFAULT, cbit.vcell.server.SimulationJobStatus.SimulationQueueID.QUEUE_ID_SIMULATIONJOB);
            cbit.vcell.server.SimulationExecutionStatus newSimExeStatus = new cbit.vcell.server.SimulationExecutionStatus(null,  null, new Date(), null, false, null);
            newSimJobStatus = new cbit.vcell.server.SimulationJobStatus(VCellServerID.getSystemServerID(),vcSimID,jobIndex,
                    oldSimulationJobStatus.getSubmitDate(), cbit.vcell.server.SimulationJobStatus.SchedulerStatus.DISPATCHED,taskID,
                    SimulationMessage.MESSAGE_JOB_DISPATCHED,
                    newQueueStatus,newSimExeStatus);

            SimulationTaskMessage simTaskMessage = new SimulationTaskMessage(simulationTask);
            simTaskMessage.sendSimulationTask(session);

            simulationDatabase.updateSimulationJobStatus(newSimJobStatus);

            cbit.vcell.message.messages.StatusMessage message = new cbit.vcell.message.messages.StatusMessage(newSimJobStatus, simulation.getVersion().getOwner().getName(), null, null);
            message.sendToClient(session);

        }
//		addStateMachineTransition(new StateMachineTransition(new DispatchStateMachineEvent(taskID), oldSimulationJobStatus, newSimJobStatus));

    }

    public synchronized StatusMessage onStopRequest(User user, cbit.vcell.server.SimulationJobStatus simJobStatus, SimulationDatabase simulationDatabase, VCMessageSession session) throws VCMessagingException, DataAccessException, SQLException {
        updateSolverProcessTimestamp();

        if (!user.equals(simJobStatus.getVCSimulationIdentifier().getOwner())) {
            lg.error(user + " is not authorized to stop simulation (key=" + simKey + ")");
            cbit.vcell.server.SimulationJobStatus simulationJobStatus = new cbit.vcell.server.SimulationJobStatus(VCellServerID.getSystemServerID(), simJobStatus.getVCSimulationIdentifier(), 0, null,
                    cbit.vcell.server.SimulationJobStatus.SchedulerStatus.FAILED, 0, SimulationMessage.workerFailure("You are not authorized to stop this simulation!"), null, null);

            VCMongoMessage.sendInfo("onStopRequest("+simJobStatus.getVCSimulationIdentifier()+") ignoring stop simulation request - wrong user)");
            return new StatusMessage(SimulationJobStatus.fromSimulationJobStatus(simulationJobStatus), user.getName(), null, null);
        }

        // stop latest task if active
        cbit.vcell.server.SimulationJobStatus.SchedulerStatus schedulerStatus = simJobStatus.getSchedulerStatus();
        int taskID = simJobStatus.getTaskID();

        if (schedulerStatus.isActive()){
            cbit.vcell.server.SimulationQueueEntryStatus simQueueEntryStatus = simJobStatus.getSimulationQueueEntryStatus();
            cbit.vcell.server.SimulationExecutionStatus simExeStatus = simJobStatus.getSimulationExecutionStatus();
            cbit.vcell.server.SimulationJobStatus newJobStatus = new cbit.vcell.server.SimulationJobStatus(simJobStatus.getServerID(),simJobStatus.getVCSimulationIdentifier(),jobIndex,simJobStatus.getSubmitDate(),
                    cbit.vcell.server.SimulationJobStatus.SchedulerStatus.STOPPED,taskID,SimulationMessage.solverStopped("simulation stopped by user"),simQueueEntryStatus,simExeStatus);

            //
            // send stopSimulation to serviceControl topic
            //
//            VCMessage msg = session.createMessage();
//            msg.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_STOPSIMULATION_VALUE);
//            msg.setLongProperty(MessageConstants.SIMKEY_PROPERTY, Long.parseLong(simKey + ""));
//            msg.setIntProperty(MessageConstants.JOBINDEX_PROPERTY, jobIndex);
//            msg.setIntProperty(MessageConstants.TASKID_PROPERTY, taskID);
//            msg.setStringProperty(VCMessagingConstants.USERNAME_PROPERTY, user.getName());
//            if (simExeStatus.getHtcJobID()!=null){
//                msg.setStringProperty(MessageConstants.HTCJOBID_PROPERTY, simExeStatus.getHtcJobID().toDatabase());
//            }
//            session.sendTopicMessage(VCellTopic.ServiceControlTopic, msg);

            if (lg.isTraceEnabled()) lg.trace("send " + MessageConstants.MESSAGE_TYPE_STOPSIMULATION_VALUE + " to " + VCellTopic.ServiceControlTopic.getName() + " topic");
            SimulationJobStatus simulationJobStatusRecord = new SimulationJobStatus(
                    null, new VCSimulationIdentifier(simKey, user), simJobStatus.getSubmitDate().toInstant(),
                    cbit.vcell.server.SimulationJobStatus.SchedulerStatus.STOPPED, simJobStatus.getSimulationMessage(), taskID,
                    VCellServerID.getSystemServerID().toString(), jobIndex, SimulationExecutionStatus.fromSimulationExecutionStatus(simExeStatus), SimulationQueueEntryStatus.fromStatusRecord(simQueueEntryStatus)
            );

            simulationDatabase.updateSimulationJobStatus(newJobStatus);

            return new StatusMessage(simulationJobStatusRecord, user.getName(), null, null);
        }
        return null;
    }

    public synchronized void onSystemAbort(cbit.vcell.server.SimulationJobStatus oldJobStatus, String failureMessage, SimulationDatabase simulationDatabase, VCMessageSession session) throws VCMessagingException, UpdateSynchronizationException, DataAccessException, SQLException {
        updateSolverProcessTimestamp();

        int taskID = oldJobStatus.getTaskID();

        //
        // status information (initialized as if new record)
        //
        Date startDate = null;
        boolean hasData = false;
        HtcJobID htcJobID = null;
        String computeHost = null;
        VCellServerID vcServerID = VCellServerID.getSystemServerID();
        Date submitDate = null;
        Date queueDate = null;
        int queuePriority = PRIORITY_DEFAULT;


        //
        // update using previously stored status (if available).
        //
        cbit.vcell.server.SimulationExecutionStatus oldSimExeStatus = oldJobStatus.getSimulationExecutionStatus();
        if (oldSimExeStatus!=null && oldSimExeStatus.getStartDate()!=null){
            startDate = oldSimExeStatus.getStartDate();
        }
        if (oldSimExeStatus!=null && oldSimExeStatus.hasData()){
            hasData = true;
        }
        if (oldSimExeStatus!=null && oldSimExeStatus.getComputeHost()!=null){
            computeHost = oldSimExeStatus.getComputeHost();
        }
        if (oldSimExeStatus!=null && oldSimExeStatus.getHtcJobID()!=null){
            htcJobID = oldSimExeStatus.getHtcJobID();
        }
        vcServerID = oldJobStatus.getServerID();
        submitDate = oldJobStatus.getSubmitDate();
        cbit.vcell.server.SimulationQueueEntryStatus oldQueueStatus = oldJobStatus.getSimulationQueueEntryStatus();
        if (oldQueueStatus!=null && oldQueueStatus.getQueueDate()!=null){
            queueDate = oldQueueStatus.getQueueDate();
        }
        if (oldQueueStatus!=null){
            queuePriority = oldQueueStatus.getQueuePriority();
        }

        cbit.vcell.server.SimulationQueueEntryStatus newQueueStatus = new cbit.vcell.server.SimulationQueueEntryStatus(queueDate, queuePriority, cbit.vcell.server.SimulationJobStatus.SimulationQueueID.QUEUE_ID_NULL);

        Date endDate = new Date();
        Date lastUpdateDate = new Date();

        cbit.vcell.server.SimulationExecutionStatus newExeStatus = new cbit.vcell.server.SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, htcJobID);

        cbit.vcell.server.SimulationJobStatus newJobStatus = new cbit.vcell.server.SimulationJobStatus(vcServerID, oldJobStatus.getVCSimulationIdentifier(), jobIndex, submitDate, cbit.vcell.server.SimulationJobStatus.SchedulerStatus.FAILED,
                taskID, SimulationMessage.jobFailed(failureMessage), newQueueStatus, newExeStatus);

        simulationDatabase.updateSimulationJobStatus(newJobStatus);
//		addStateMachineTransition(new StateMachineTransition(new AbortStateMachineEvent(taskID, failureMessage), oldJobStatus, newJobStatus));

        String userName = VCMessagingConstants.USERNAME_PROPERTY_VALUE_ALL;
        cbit.vcell.message.messages.StatusMessage msgForClient = new cbit.vcell.message.messages.StatusMessage(newJobStatus, userName, null, null);
        msgForClient.sendToClient(session);
        if (lg.isTraceEnabled()) lg.trace("Send status to client: " + msgForClient);
    }

}

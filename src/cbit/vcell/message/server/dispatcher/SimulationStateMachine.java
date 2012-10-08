package cbit.vcell.message.server.dispatcher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.vcell.util.DataAccessException;
import org.vcell.util.MessageConstants;
import org.vcell.util.MessageConstants.SimulationQueueID;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.htc.PbsJobID;
import cbit.rmi.event.WorkerEvent;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.messages.SimulationTaskMessage;
import cbit.vcell.message.messages.StatusMessage;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.messaging.db.SimulationExecutionStatus;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.messaging.db.SimulationQueueEntryStatus;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

public class SimulationStateMachine {
	private KeyValue simKey;
	private int jobIndex;
	private ArrayList<SimulationTaskProcessState> simTaskProcessStates = new ArrayList<SimulationTaskProcessState>();
	private ArrayList<AbstractStateMachineEvent> stateMachineEvents = new ArrayList<AbstractStateMachineEvent>();
	
	//==============================================================
	//
	// input events (archived for debugging purposes)
	//
	//==============================================================
	public abstract class AbstractStateMachineEvent {
		final long timestampMS = System.currentTimeMillis();		
	}
	public class WorkerStateMachineEvent extends AbstractStateMachineEvent {
		final WorkerEvent workerEvent;
		public WorkerStateMachineEvent(WorkerEvent workerEvent){
			this.workerEvent = workerEvent;
		}
	}
	public class StartStateMachineEvent extends AbstractStateMachineEvent {
		final User user;
		public StartStateMachineEvent(User user){
			this.user = user;
		}
	}
	public class StopStateMachineEvent extends AbstractStateMachineEvent {
		final User user;
		public StopStateMachineEvent(User user){
			this.user = user;
		}
	}
	public class PreloadStateMachineEvent extends AbstractStateMachineEvent {
		final SimulationJobStatus simJobStatus;
		public PreloadStateMachineEvent(SimulationJobStatus simJobStatus){
			this.simJobStatus = simJobStatus;
		}
	}
	public class DispatchStateMachineEvent extends AbstractStateMachineEvent {
		public DispatchStateMachineEvent(){
		}
	}

	
	public SimulationStateMachine(KeyValue simKey, int jobIndex){
		this.simKey = simKey;
		this.jobIndex = jobIndex;
	}

	public SimulationStateMachine(SimulationJobStatus[] simJobStatus) {
		this.simKey = simJobStatus[0].getVCSimulationIdentifier().getSimulationKey();
		this.jobIndex = simJobStatus[0].getJobIndex();
		for (SimulationJobStatus simulationJobStatus : simJobStatus){
			SimulationExecutionStatus simulationExecutionStatus = simulationJobStatus.getSimulationExecutionStatus();
			if (simulationExecutionStatus!=null){
				SimulationTaskProcessState simTaskProcessState = new SimulationTaskProcessState(simKey,jobIndex,simulationJobStatus.getTaskID());
				simTaskProcessState.computeHost = simulationExecutionStatus.getComputeHost();
				simTaskProcessState.endDate = simulationExecutionStatus.getEndDate();
				simTaskProcessState.latestUpdateDate = simulationExecutionStatus.getLatestUpdateDate();
				simTaskProcessState.pbsJobID = simulationExecutionStatus.getPbsJobID();
				simTaskProcessState.startDate = simulationExecutionStatus.getStartDate();
				simTaskProcessStates.add(simTaskProcessState);
			}
			addStateMachineEvent(new PreloadStateMachineEvent(simulationJobStatus));
		}
	}
	
	public KeyValue getSimKey() {
		return simKey;
	}

	public int getJobIndex() {
		return jobIndex;
	}

	public ArrayList<SimulationTaskProcessState> getSimTaskProcessStates() {
		return simTaskProcessStates;
	}

	public SimulationTaskProcessState getLatestSimTaskProcessState() {
		int taskID = -1;
		SimulationTaskProcessState simTaskProcessState = null;
		for (SimulationTaskProcessState taskState : simTaskProcessStates){
			if (taskState.taskID > taskID){
				taskID = taskState.taskID;
				simTaskProcessState = taskState;
			}
		}
		return simTaskProcessState;
	}

	private void addStateMachineEvent(AbstractStateMachineEvent stateMachineEvent){
		stateMachineEvents.add(stateMachineEvent);
	}
	
	public SimulationTaskProcessState getSimTaskProcessState(int taskID){
		for (SimulationTaskProcessState taskState : simTaskProcessStates){
			if (taskState.taskID == taskID){
				return taskState;
			}
		}
		SimulationTaskProcessState simTaskProcessState = new SimulationTaskProcessState(simKey,  jobIndex, taskID);
		simTaskProcessStates.add(simTaskProcessState);
		return simTaskProcessState;
	}
	
	private void addOrReplaceSimTaskProcessState(SimulationTaskProcessState newSimTaskProcessState){
		SimulationTaskProcessState foundSimTaskProcState = null;
		for (SimulationTaskProcessState taskState : simTaskProcessStates){
			if (taskState.taskID == newSimTaskProcessState.taskID){
				foundSimTaskProcState = taskState;
			}
		}
		if (foundSimTaskProcState!=null){
			simTaskProcessStates.remove(foundSimTaskProcState);
		}
		simTaskProcessStates.add(newSimTaskProcessState);
	}

	private SimulationTaskProcessState getNewTaskState_StopSimulation(int taskID){
		SimulationTaskProcessState simTaskProcessState = new SimulationTaskProcessState(getSimTaskProcessState(taskID));
		simTaskProcessState.endDate = null;
		simTaskProcessState.hasData = false;
		simTaskProcessState.latestUpdateDate = new Date();
		simTaskProcessState.progress = null;
		simTaskProcessState.startDate = null;
		return simTaskProcessState;
	}
	
	private SimulationTaskProcessState getNewTaskState_StartSimulation(int taskID){
		SimulationTaskProcessState simTaskProcessState = new SimulationTaskProcessState(getSimTaskProcessState(taskID));
		simTaskProcessState.endDate = null;
		simTaskProcessState.hasData = false;
		simTaskProcessState.latestUpdateDate = new Date();
		simTaskProcessState.progress = null;
		simTaskProcessState.startDate = null;
		return simTaskProcessState;
	}
	
	private SimulationTaskProcessState getNewTaskState_WorkerEvent(int taskID, WorkerEvent workerEvent){
		SimulationTaskProcessState simTaskProcessState = new SimulationTaskProcessState(getSimTaskProcessState(taskID));
		if (workerEvent.getHostName()!=null){
			simTaskProcessState.computeHost = workerEvent.getHostName();
		}
		if (workerEvent.getProgress()!=null){
			simTaskProcessState.progress = workerEvent.getProgress();
		}
		if (workerEvent.getTimePoint()!=null){
			simTaskProcessState.timePoint = workerEvent.getTimePoint();
		}		
		return simTaskProcessState;
	}

	private SimulationTaskProcessState getNewTaskState_Dispatch(int taskID){
		SimulationTaskProcessState simTaskProcessState = new SimulationTaskProcessState(getSimTaskProcessState(taskID));
		simTaskProcessState.endDate = null;
		simTaskProcessState.hasData = false;
		simTaskProcessState.latestUpdateDate = new Date();
		simTaskProcessState.progress = null;
		simTaskProcessState.startDate = null;
		return simTaskProcessState;
	}
	
	public synchronized void onWorkerEvent(WorkerEvent workerEvent, SimulationDatabase simulationDatabase, VCMessageSession session, SessionLog log) throws DataAccessException, VCMessagingException, SQLException {
		WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
		VCMongoMessage.sendWorkerEvent(workerEventMessage);
		addStateMachineEvent(new WorkerStateMachineEvent(workerEvent));
		
		String userName = workerEvent.getUserName(); // as the filter of the client
		int taskID = workerEvent.getTaskID();
		int jobIndex = workerEvent.getJobIndex();

		log.print("onWorkerEventMessage[" + workerEvent.getEventTypeID() + "," + workerEvent.getSimulationMessage() + "][simid=" + workerEvent.getVCSimulationDataIdentifier() + ",job=" + jobIndex + ",task=" + taskID + "]");

		VCSimulationDataIdentifier vcSimDataID = workerEvent.getVCSimulationDataIdentifier();
		if (vcSimDataID == null) {
			VCMongoMessage.sendInfo("onWorkerEvent() ignoring WorkerEvent - no SimID in message): "+workerEvent.show());
			return;
		}
		KeyValue simKey = vcSimDataID.getSimulationKey();
		SimulationJobStatus oldSimulationJobStatus = simulationDatabase.getSimulationJobStatus(simKey, jobIndex, taskID);

		if (oldSimulationJobStatus == null){
			VCMongoMessage.sendInfo("onWorkerEvent() ignoring WorkerEvent, no current SimulationJobStatus: "+workerEvent.show());
			return;
		}	
		if (oldSimulationJobStatus == null || oldSimulationJobStatus.getSchedulerStatus().isDone()){
			VCMongoMessage.sendInfo("onWorkerEvent() ignoring outdated WorkerEvent, (currState="+oldSimulationJobStatus.getSchedulerStatus().getDescription()+"): "+workerEvent.show());
			return;
		}	
		SchedulerStatus oldSchedulerStatus = oldSimulationJobStatus.getSchedulerStatus();
		
		//
		// status information (initialized as if new record)
		//
		Date startDate = null;
		Date lastUpdateDate = null;
		Date endDate = null;
		boolean hasData = false;
		PbsJobID pbsJobID = null;
		String computeHost = null;
		VCellServerID vcServerID = VCellServerID.getSystemServerID();
		Date submitDate = null;
		Date queueDate = null;
		int queuePriority = MessageConstants.PRIORITY_DEFAULT;
		SimulationQueueID simQueueID = SimulationQueueID.QUEUE_ID_WAITING;
		

		//
		// update using previously stored status (if available).
		//
		SimulationExecutionStatus oldSimExeStatus = oldSimulationJobStatus.getSimulationExecutionStatus();
		if (oldSimExeStatus!=null && oldSimExeStatus.getStartDate()!=null){
			startDate = oldSimExeStatus.getStartDate();
		}
		if (oldSimExeStatus!=null && oldSimExeStatus.getLatestUpdateDate()!=null){
			startDate = oldSimExeStatus.getLatestUpdateDate();
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
		if (oldSimExeStatus!=null && oldSimExeStatus.getPbsJobID()!=null){
			pbsJobID = oldSimExeStatus.getPbsJobID();
		}
		vcServerID = oldSimulationJobStatus.getServerID();
		submitDate = oldSimulationJobStatus.getSubmitDate();
		SimulationQueueEntryStatus oldQueueStatus = oldSimulationJobStatus.getSimulationQueueEntryStatus();
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
		if (workerEvent.getPbsJobID()!=null){
			pbsJobID = workerEvent.getPbsJobID();
		}
		if (workerEvent.getHostName()!=null){
			computeHost = workerEvent.getHostName();
		}
		SimulationMessage workerEventSimulationMessage = workerEvent.getSimulationMessage();
		if (workerEventSimulationMessage.getPbsJobId()!=null){
			pbsJobID = workerEventSimulationMessage.getPbsJobId();
		}
		
		
		SimulationJobStatus newJobStatus = null;
		final SimulationTaskProcessState newTaskState = getNewTaskState_WorkerEvent(taskID, workerEvent);

		if (workerEvent.isAcceptedEvent()) {
			//
			// job message accepted by PbsSimulationWorker and sent to PBS (with a pbsJobID) ... previous state should be "WAITING"
			//
			if (oldSchedulerStatus.isWaiting() || oldSchedulerStatus.isQueued()) {
				// new queue status
				SimulationQueueEntryStatus newQueueStatus = new SimulationQueueEntryStatus(queueDate, queuePriority, SimulationQueueID.QUEUE_ID_NULL);
				
				// new exe status
				lastUpdateDate = new Date();
				startDate = lastUpdateDate;
				endDate = null;
				SimulationExecutionStatus newExeStatus = new SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, pbsJobID);
				
				newJobStatus = new SimulationJobStatus(vcServerID, vcSimDataID.getVcSimID(), jobIndex, submitDate, SchedulerStatus.DISPATCHED,
						taskID, workerEventSimulationMessage, newQueueStatus, newExeStatus);
			}
			
		} else if (workerEvent.isStartingEvent()) {
			// only update database when the job event changes from started to running. The later progress event will not be recorded.
			if ( oldSchedulerStatus.isWaiting() || oldSchedulerStatus.isQueued() || oldSchedulerStatus.isDispatched() || oldSchedulerStatus.isRunning()) {
				// new queue status
				SimulationQueueEntryStatus newQueueStatus = new SimulationQueueEntryStatus(queueDate, queuePriority, SimulationQueueID.QUEUE_ID_NULL);
				
				// new exe status
				lastUpdateDate = new Date();
				if (startDate == null){
					startDate = lastUpdateDate;
				}
				SimulationExecutionStatus newExeStatus = new SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, pbsJobID);
				
				newJobStatus = new SimulationJobStatus(vcServerID, vcSimDataID.getVcSimID(), jobIndex, submitDate, SchedulerStatus.RUNNING,
						taskID, workerEventSimulationMessage, newQueueStatus, newExeStatus);
			}

		} else if (workerEvent.isNewDataEvent()) {
			if (oldSchedulerStatus.isWaiting() || oldSchedulerStatus.isQueued() || oldSchedulerStatus.isDispatched() || oldSchedulerStatus.isRunning()){
				
				simulationDatabase.dataMoved(vcSimDataID, workerEvent.getUser());
				
				
				if (!oldSchedulerStatus.isRunning() || simQueueID != SimulationQueueID.QUEUE_ID_NULL || hasData==false){
					
					// new queue status		
					SimulationQueueEntryStatus newQueueStatus = new SimulationQueueEntryStatus(queueDate, queuePriority, SimulationQueueID.QUEUE_ID_NULL);
					
					// new exe status
					if (startDate == null){
						startDate = lastUpdateDate;
					}
					hasData = true;
					SimulationExecutionStatus newExeStatus = new SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, pbsJobID);

					newJobStatus = new SimulationJobStatus(vcServerID, vcSimDataID.getVcSimID(), jobIndex, submitDate, SchedulerStatus.RUNNING,
							taskID, workerEventSimulationMessage, newQueueStatus, newExeStatus);
				}
			}

		} else if (workerEvent.isProgressEvent() || workerEvent.isWorkerAliveEvent()) {
			if (oldSchedulerStatus.isWaiting() || oldSchedulerStatus.isQueued() || oldSchedulerStatus.isDispatched() || oldSchedulerStatus.isRunning()){
				
				
				if (!oldSchedulerStatus.isRunning() || simQueueID != SimulationQueueID.QUEUE_ID_NULL){
					// new queue status		
					SimulationQueueEntryStatus newQueueStatus = new SimulationQueueEntryStatus(queueDate, queuePriority, SimulationQueueID.QUEUE_ID_NULL);
					
					// new exe status
					if (startDate == null){
						startDate = lastUpdateDate;
					}
					SimulationExecutionStatus newExeStatus = new SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, pbsJobID);

					newJobStatus = new SimulationJobStatus(vcServerID, vcSimDataID.getVcSimID(), jobIndex, submitDate, SchedulerStatus.RUNNING,
							taskID, workerEventSimulationMessage, newQueueStatus, newExeStatus);

				}else if (oldSchedulerStatus.isRunning()){
					if (oldSimExeStatus != null) {
						Date latestUpdate = oldSimExeStatus.getLatestUpdateDate();
						Date sysDate = oldSimulationJobStatus.getTimeDateStamp();
						if (sysDate.getTime() - latestUpdate.getTime() >= MessageConstants.INTERVAL_PING_SERVER * 3 / 5) {
							// new queue status		
							SimulationQueueEntryStatus newQueueStatus = new SimulationQueueEntryStatus(queueDate, queuePriority, SimulationQueueID.QUEUE_ID_NULL);
							SimulationExecutionStatus newExeStatus = new SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, pbsJobID);

							newJobStatus = new SimulationJobStatus(vcServerID, vcSimDataID.getVcSimID(), jobIndex, submitDate, SchedulerStatus.RUNNING,
									taskID, workerEventSimulationMessage, newQueueStatus, newExeStatus);
						}
					}
				}
			}

		} else if (workerEvent.isCompletedEvent()) {			
			if (oldSchedulerStatus.isWaiting() || oldSchedulerStatus.isQueued() || oldSchedulerStatus.isDispatched() || oldSchedulerStatus.isRunning()){
				// new queue status		
				SimulationQueueEntryStatus newQueueStatus = new SimulationQueueEntryStatus(queueDate, queuePriority, SimulationQueueID.QUEUE_ID_NULL);
				
				// new exe status
				endDate = new Date();
				hasData = true;

				simulationDatabase.dataMoved(vcSimDataID, workerEvent.getUser());
				
				SimulationExecutionStatus newExeStatus = new SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, pbsJobID);

				newJobStatus = new SimulationJobStatus(vcServerID, vcSimDataID.getVcSimID(), jobIndex, submitDate, SchedulerStatus.COMPLETED,
						taskID, workerEventSimulationMessage, newQueueStatus, newExeStatus);

			}

		} else if (workerEvent.isFailedEvent()) {						
			if (oldSchedulerStatus.isWaiting() || oldSchedulerStatus.isQueued() || oldSchedulerStatus.isDispatched() || oldSchedulerStatus.isRunning()){
				// new queue status		
				SimulationQueueEntryStatus newQueueStatus = new SimulationQueueEntryStatus(queueDate, queuePriority, SimulationQueueID.QUEUE_ID_NULL);
				
				// new exe status
				endDate = new Date();
				hasData = true;

				simulationDatabase.dataMoved(vcSimDataID, workerEvent.getUser());
				
				SimulationExecutionStatus newExeStatus = new SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, pbsJobID);

				newJobStatus = new SimulationJobStatus(vcServerID, vcSimDataID.getVcSimID(), jobIndex, submitDate, SchedulerStatus.RUNNING,
						taskID, workerEventSimulationMessage, newQueueStatus, newExeStatus);

			}
		}

		if (newTaskState!=null){
			addOrReplaceSimTaskProcessState(newTaskState);
		}
		if (newJobStatus!=null){
			SimulationJobStatus updatedSimJobStatus = simulationDatabase.updateSimulationJobStatus(oldSimulationJobStatus, newJobStatus);

			if (!newJobStatus.compareEqual(oldSimulationJobStatus) || workerEvent.isProgressEvent() || workerEvent.isNewDataEvent()) {		
				Double progress = workerEvent.getProgress();
				Double timepoint = workerEvent.getTimePoint();
				StatusMessage msgForClient = new StatusMessage(updatedSimJobStatus, userName, progress, timepoint);
				msgForClient.sendToClient(session);
				log.print("Send status to client: " + msgForClient);
			} else {
				StatusMessage msgForClient = new StatusMessage(updatedSimJobStatus, userName, null, null);
				msgForClient.sendToClient(session);
				log.print("Send status to client: " + msgForClient);
			}
		}else if (workerEvent.isProgressEvent() || workerEvent.isNewDataEvent()){
			Double progress = workerEvent.getProgress();
			Double timepoint = workerEvent.getTimePoint();
			StatusMessage msgForClient = new StatusMessage(oldSimulationJobStatus, userName, progress, timepoint);
			msgForClient.sendToClient(session);
			log.print("Send status to client: " + msgForClient);
		}else{
			VCMongoMessage.sendInfo("onWorkerEvent() ignoring WorkerEvent (currState="+oldSchedulerStatus.getDescription()+"): "+workerEvent.show());
		}
	}

	public synchronized void onStartRequest(User user, Simulation simulation, FieldDataIdentifierSpec[] fieldDataIdentifierSpecs, SimulationDatabase simulationDatabase, VCMessageSession session, SessionLog log) throws VCMessagingException, DataAccessException, SQLException {

		addStateMachineEvent(new StartStateMachineEvent(user));

		VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, simulation.getVersion().getOwner());
		if (!user.equals(vcSimID.getOwner())) {
			log.alert(user + " is not authorized to start simulation (key=" + simKey + ")");
			StatusMessage message = new StatusMessage(new SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, 0, null, 
					SchedulerStatus.FAILED, 0, SimulationMessage.workerFailure("You are not authorized to start this simulation!"), null, null), user.getName(), null, null);
			message.sendToClient(session);
			VCMongoMessage.sendInfo("onStartRequest("+vcSimID.getID()+") ignoring start simulation request - wrong user): simID="+vcSimID);
			return;
		}

		//
		// get latest simulation job task (if any).
		//
		SimulationJobStatus[] oldSimulationJobStatusArray = simulationDatabase.getSimulationJobStatusArray(simKey, jobIndex);
		SimulationJobStatus oldSimulationJobStatus = null;
		int oldTaskID = -1;
		for (SimulationJobStatus simJobStatus : oldSimulationJobStatusArray){
			if (simJobStatus.getTaskID() > oldTaskID){
				oldTaskID = simJobStatus.getTaskID();
				oldSimulationJobStatus = simJobStatus;
			}
		}
		// if already started by another thread
		if (oldSimulationJobStatus != null && !oldSimulationJobStatus.getSchedulerStatus().isDone()) {
			VCMongoMessage.sendInfo("onStartRequest("+vcSimID.getID()+") ignoring start simulation request - (currentSimJobStatus:"+oldSimulationJobStatus.getSchedulerStatus().getDescription()+"): simID="+vcSimID);
			throw new RuntimeException("Can't start, simulation[" + vcSimID + "] job [" + jobIndex + "] task [" + oldTaskID + "] is running already ("+oldSimulationJobStatus.getSchedulerStatus().getDescription()+")");
		}
		
		int newTaskID;
		
		if (oldTaskID > -1){
			// calculate new task
			newTaskID = (oldTaskID & MessageConstants.TASKID_USERCOUNTER_MASK) + MessageConstants.TASKID_USERINCREMENT;
		}else{
			// first task, start with 0
			newTaskID = 0;
		}
		
		addOrReplaceSimTaskProcessState(getNewTaskState_StartSimulation(newTaskID));
		
		Date currentDate = new Date();
		// new queue status
		SimulationQueueEntryStatus newQueueStatus = new SimulationQueueEntryStatus(currentDate, MessageConstants.PRIORITY_DEFAULT, SimulationQueueID.QUEUE_ID_WAITING);
		
		// new exe status
		Date lastUpdateDate = null;
		String computeHost = null;
		Date startDate = null;
		Date endDate = null;
		PbsJobID pbsJobID = null;
		boolean hasData = false;
		
		SimulationExecutionStatus newExeStatus = new SimulationExecutionStatus(startDate, computeHost, lastUpdateDate, endDate, hasData, pbsJobID);
		
		VCellServerID vcServerID = VCellServerID.getSystemServerID();
		Date submitDate = currentDate;
		
		SimulationJobStatus newJobStatus = new SimulationJobStatus(vcServerID, vcSimID, jobIndex, submitDate, SchedulerStatus.WAITING,
				newTaskID, SimulationMessage.MESSAGE_JOB_WAITING, newQueueStatus, newExeStatus);
		
		SimulationJobStatus updatedSimJobStatus = simulationDatabase.insertSimulationJobStatus(newJobStatus);
			
		StatusMessage message = new StatusMessage(updatedSimJobStatus, user.getName(), null, null);
		message.sendToClient(session);
	}
	

	public synchronized void onDispatch(VCSimulationIdentifier vcSimID, int taskID, SimulationDatabase simulationDatabase, VCMessageSession session, SessionLog log) throws VCMessagingException, DataAccessException, SQLException {

		addStateMachineEvent(new DispatchStateMachineEvent());
		
		SimulationJobStatus oldSimulationJobStatus = simulationDatabase.getSimulationJobStatus(simKey, jobIndex, taskID);
		if (oldSimulationJobStatus == null) {
			VCMongoMessage.sendInfo("onDispatch("+vcSimID.getID()+") Can't start, simulation[" + vcSimID + "] job [" + jobIndex + "] task [" + taskID + "], status not found)");
			throw new RuntimeException("Can't start, simulation[" + vcSimID + "] job [" + jobIndex + "] task [" + taskID + "], status not found)");
		}
		if (!oldSimulationJobStatus.getSchedulerStatus().isWaiting()) {
			VCMongoMessage.sendInfo("onDispatch("+vcSimID.getID()+") Can't start, simulation[" + vcSimID + "] job [" + jobIndex + "] task [" + taskID + "] is already dispatched ("+oldSimulationJobStatus.getSchedulerStatus().getDescription()+")");
			throw new RuntimeException("Can't start, simulation[" + vcSimID + "] job [" + jobIndex + "] task [" + taskID + "] is already dispatched ("+oldSimulationJobStatus.getSchedulerStatus().getDescription()+")");
		}

		Simulation simulation = simulationDatabase.getSimulation(vcSimID.getOwner(), vcSimID.getSimulationKey());
		FieldDataIdentifierSpec[] fieldDataIdentifierSpecs = simulationDatabase.getFieldDataIdentifierSpecs(simulation);
		SimulationTask simulationTask = new SimulationTask(new SimulationJob(simulation, jobIndex, fieldDataIdentifierSpecs), taskID);

		double requiredMemMB = simulationTask.getEstimatedMemorySizeMB();
		double allowableMemMB = Double.parseDouble(PropertyLoader.getRequiredProperty(PropertyLoader.limitJobMemoryMB));
		
		if (requiredMemMB > allowableMemMB) {						
			//
			// fail the simulation
			//
			Date currentDate = new Date();
			// new queue status
			SimulationQueueEntryStatus newQueueStatus = new SimulationQueueEntryStatus(currentDate, MessageConstants.PRIORITY_DEFAULT, SimulationQueueID.QUEUE_ID_NULL);
			SimulationExecutionStatus newSimExeStatus = new SimulationExecutionStatus(null,  null, new Date(), null, false, null);
			SimulationJobStatus newSimJobStatus = new SimulationJobStatus(VCellServerID.getSystemServerID(),vcSimID,jobIndex,
					oldSimulationJobStatus.getSubmitDate(),SchedulerStatus.FAILED,taskID,
					SimulationMessage.jobFailed("simulation required "+requiredMemMB+"MB of memory, only "+allowableMemMB+"MB allowed"),
					newQueueStatus,newSimExeStatus);
			
			SimulationJobStatus updatedSimJobStatus = simulationDatabase.insertSimulationJobStatus(newSimJobStatus);
			
			StatusMessage message = new StatusMessage(updatedSimJobStatus, simulation.getVersion().getOwner().getName(), null, null);
			message.sendToClient(session);
			
		}else{
			//
			// dispatch the simulation, new queue status
			//
			Date currentDate = new Date();
			SimulationQueueEntryStatus newQueueStatus = new SimulationQueueEntryStatus(currentDate, MessageConstants.PRIORITY_DEFAULT, SimulationQueueID.QUEUE_ID_SIMULATIONJOB);
			SimulationExecutionStatus newSimExeStatus = new SimulationExecutionStatus(null,  null, new Date(), null, false, null);
			SimulationJobStatus newSimJobStatus = new SimulationJobStatus(VCellServerID.getSystemServerID(),vcSimID,jobIndex,
					oldSimulationJobStatus.getSubmitDate(),SchedulerStatus.QUEUED,taskID,
					SimulationMessage.MESSAGE_JOB_DISPATCHED,
					newQueueStatus,newSimExeStatus);
			
			SimulationTaskMessage simTaskMessage = new SimulationTaskMessage(simulationTask);
			simTaskMessage.sendSimulationTask(session);
			
			SimulationJobStatus updatedSimJobStatus = simulationDatabase.updateSimulationJobStatus(oldSimulationJobStatus,newSimJobStatus);
			
			StatusMessage message = new StatusMessage(updatedSimJobStatus, simulation.getVersion().getOwner().getName(), null, null);
			message.sendToClient(session);
		
			addOrReplaceSimTaskProcessState(getNewTaskState_Dispatch(taskID));
		}
	}

	public synchronized void onStopRequest(User user, VCSimulationIdentifier vcSimID, SimulationDatabase simulationDatabase, VCMessageSession session, SessionLog log) throws VCMessagingException, DataAccessException, SQLException {
		
		addStateMachineEvent(new StopStateMachineEvent(user));
		
		if (!user.equals(vcSimID.getOwner())) {
			log.alert(user + " is not authorized to stop simulation (key=" + simKey + ")");
			StatusMessage message = new StatusMessage(new SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, 0, null, 
					SchedulerStatus.FAILED, 0, SimulationMessage.workerFailure("You are not authorized to stop this simulation!"), null, null), user.getName(), null, null);
			message.sendToClient(session);
			VCMongoMessage.sendInfo("onStopRequest("+vcSimID.getID()+") ignoring stop simulation request - wrong user)");
			return;
		} 
		// if the job is in simJob queue, get it out
		KeyValue simKey = vcSimID.getSimulationKey();
		SimulationJobStatus[] oldJobStatusArray = simulationDatabase.getSimulationJobStatusArray(simKey, jobIndex);
		
		// stop each active task.
		for (SimulationJobStatus oldJobStatus : oldJobStatusArray){
			SchedulerStatus schedulerStatus = oldJobStatus.getSchedulerStatus();
			int taskID = oldJobStatus.getTaskID();
			addOrReplaceSimTaskProcessState(getNewTaskState_StopSimulation(taskID));
	
			if (schedulerStatus.isActive()){
				SimulationQueueEntryStatus simQueueEntryStatus = oldJobStatus.getSimulationQueueEntryStatus();
				SimulationExecutionStatus simExeStatus = oldJobStatus.getSimulationExecutionStatus();
				SimulationJobStatus newJobStatus = new SimulationJobStatus(oldJobStatus.getServerID(),vcSimID,jobIndex,oldJobStatus.getSubmitDate(),
						SchedulerStatus.STOPPED,taskID,SimulationMessage.solverStopped("simulation stopped by user"),simQueueEntryStatus,simExeStatus);
				
				if (schedulerStatus.isDispatched() || schedulerStatus.isRunning()){
					// send stopSimulation to serviceControl topic
					log.print("send " + MessageConstants.MESSAGE_TYPE_STOPSIMULATION_VALUE + " to " + VCellTopic.ServiceControlTopic.getName() + " topic");
					VCMessage msg = session.createMessage();
					msg.setStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_STOPSIMULATION_VALUE);
					msg.setLongProperty(MessageConstants.SIMKEY_PROPERTY, Long.parseLong(simKey + ""));
					msg.setIntProperty(MessageConstants.JOBINDEX_PROPERTY, jobIndex);
					msg.setIntProperty(MessageConstants.TASKID_PROPERTY, taskID);
					msg.setStringProperty(MessageConstants.USERNAME_PROPERTY, user.getName());
					session.sendTopicMessage(VCellTopic.ServiceControlTopic, msg);	
				}
				
				SimulationJobStatus updatedSimJobStatus = simulationDatabase.updateSimulationJobStatus(oldJobStatus, newJobStatus);
				
				// update client
				StatusMessage message = new StatusMessage(updatedSimJobStatus, user.getName(), null, null);
				message.sendToClient(session);
			}
		}
	}



}

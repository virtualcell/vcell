package cbit.rmi.event;
import org.vcell.util.document.User;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (2/5/2004 12:35:20 PM)
 * @author: Fei Gao
 */
public class WorkerEvent extends MessageEvent {
	

	private VCSimulationIdentifier vcSimulationIdentifier = null;
	private int jobIndex = -1;
	private String hostName = null;
	private int taskID = -1;	
	private int eventType = -1;
	private Double progress = null;
	private Double timePoint = null;
	private SimulationMessage eventMessage = null;

public WorkerEvent(int eventType0, Object source, VCSimulationIdentifier simId0, int jobIndex0, String hostName0, int taskID0, Double progress0, Double timePoint0, SimulationMessage arg_eventMessage) {
	super(source, new MessageSource(source, Simulation.createSimulationID(simId0.getSimulationKey())), new MessageData(new Double[] {progress0, timePoint0}));
	if (arg_eventMessage == null) {
		throw new RuntimeException("WorkerEvent : SimulationMessage should not be null");
	}	
	eventType = eventType0;
	vcSimulationIdentifier = simId0;
	jobIndex = jobIndex0;
	taskID = taskID0;
	hostName = hostName0;
	progress = progress0;
	timePoint = timePoint0;
	eventMessage = arg_eventMessage;
}

public WorkerEvent(int eventType0, Object source, SimulationJob simJob, String hostName0, SimulationMessage message) {
	this(eventType0, source, simJob.getVCDataIdentifier().getVcSimID(), simJob.getJobIndex(), hostName0, 0, null, null, message);
}

public WorkerEvent(int eventType0, Object source, SimulationJob simJob, String hostName0, Double progress0, Double timePoint0, SimulationMessage message) {
	this(eventType0, source, simJob.getVCDataIdentifier().getVcSimID(), simJob.getJobIndex(), hostName0, 0, progress0, timePoint0, message);
}

public WorkerEvent(int eventType0, Object source, SimulationTask simTask, String hostName0, Double progress0, Double timePoint0, SimulationMessage message) {
	this(eventType0, source, simTask.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), simTask.getSimulationJob().getJobIndex(), hostName0, simTask.getTaskID(), progress0, timePoint0, message);
}

public WorkerEvent(int eventType0, Object source, SimulationTask simTask, String hostName0, SimulationMessage message) {
	this(eventType0, source, simTask.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), simTask.getSimulationJob().getJobIndex(), hostName0, simTask.getTaskID(), null, null, message);
}

/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:37:47 AM)
 * @return java.lang.String
 */

public SimulationMessage getSimulationMessage() {
	return eventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (3/11/2004 9:33:30 AM)
 * @return int
 */
public int getEventTypeID() {
	return eventType;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:59:27 PM)
 * @return java.lang.String
 */
public String getHostName() {
	return hostName;
}


/**
 * Insert the method's description here.
 * Creation date: (10/4/2005 1:34:00 PM)
 * @return int
 */
public int getJobIndex() {
	return jobIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:36:42 AM)
 * @return double
 */
public Double getProgress() {
	return progress;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:58:18 PM)
 * @return double
 */
public int getTaskID() {
	return taskID;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:37:04 AM)
 * @return double
 */
public Double getTimePoint() {
	return timePoint;
}


/**
 * Insert the method's description here.
 * Creation date: (3/11/2004 9:33:30 AM)
 * @return cbit.vcell.server.User
 */
public User getUser() {
	return vcSimulationIdentifier.getOwner();
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:59:27 PM)
 * @return java.lang.String
 */
public String getUserName() {
	return getUser().getName();
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:56:45 PM)
 * @return cbit.vcell.solver.SimulationInfo
 */
public VCSimulationDataIdentifier getVCSimulationDataIdentifier() {
	return new VCSimulationDataIdentifier(vcSimulationIdentifier, jobIndex);
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:17:57 AM)
 * @return boolean
 */
public boolean isAcceptedEvent() {
	return eventType == JOB_ACCEPTED;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:17:57 AM)
 * @return boolean
 */
public boolean isCompletedEvent() {
	return eventType == JOB_COMPLETED;
}


/**
 * Insert the method's description here.
 * Creation date: (3/11/2004 9:33:30 AM)
 * @return boolean
 */
public boolean isSupercededBy(MessageEvent messageEvent) {
	if (messageEvent instanceof WorkerEvent){
		WorkerEvent workerEvent = (WorkerEvent)messageEvent;
		
		if (isProgressEvent() && workerEvent.isProgressEvent()){
			if (getProgress()<workerEvent.getProgress()){
				return true;
			}
		}
		if (isNewDataEvent() && workerEvent.isNewDataEvent()) {
			if (getTimePoint() < workerEvent.getTimePoint()){
				return true;
			}
		}
			
	}
		
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:17:57 AM)
 * @return boolean
 */
public boolean isFailedEvent() {
	return eventType == JOB_FAILURE;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:17:57 AM)
 * @return boolean
 */
public boolean isNewDataEvent() {
	return eventType == JOB_DATA;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:17:57 AM)
 * @return boolean
 */
public boolean isProgressEvent() {
	return eventType == JOB_PROGRESS;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:17:57 AM)
 * @return boolean
 */
public boolean isStartingEvent() {
	return eventType == JOB_STARTING;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:17:57 AM)
 * @return boolean
 */
public boolean isWorkerAliveEvent() {
	return eventType == JOB_WORKER_ALIVE;
}
}
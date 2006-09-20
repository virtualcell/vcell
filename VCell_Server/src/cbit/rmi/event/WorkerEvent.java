package cbit.rmi.event;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.util.events.MessageData;
import cbit.vcell.util.events.MessageEvent;
import cbit.vcell.util.events.MessageSource;

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
	private java.lang.String eventMessage = null;

/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 */
public WorkerEvent(Object source, VCSimulationIdentifier simId0, int jobIndex0, int eventType0, String hostName0, int taskID0, Double progress0, Double timePoint0) {
	this(source, simId0, jobIndex0, eventType0, hostName0, taskID0, progress0, timePoint0, null);
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 */
public WorkerEvent(Object source, VCSimulationIdentifier simId0, int jobIndex0, int eventType0, String hostName0, int taskID0, Double progress0, Double timePoint0, String arg_eventMessage) {
	super(source, new MessageSource(source, cbit.vcell.solver.Simulation.createSimulationID(simId0.getSimulationKey())), new MessageData(new Double[] {progress0, timePoint0}));	
	eventType = eventType0;
	vcSimulationIdentifier = simId0;
	jobIndex = jobIndex0;
	taskID = taskID0;
	hostName = hostName0;
	progress = progress0;
	timePoint = timePoint0;
	eventMessage = arg_eventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 */
public WorkerEvent(Object source, VCSimulationIdentifier simId0, int jobIndex0, int eventType0, String hostName0, int taskID0, String message) {
	this(source, simId0, jobIndex0, eventType0, hostName0, taskID0, null, null, message);
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:37:47 AM)
 * @return java.lang.String
 */
public java.lang.String getEventMessage() {
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
public cbit.util.User getUser() {
	return vcSimulationIdentifier.getOwner();
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:59:27 PM)
 * @return java.lang.String
 */
public String getUserName() {
	return vcSimulationIdentifier.getOwner().getName();
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:56:45 PM)
 * @return cbit.vcell.solver.SimulationInfo
 */
public cbit.vcell.solvers.VCSimulationDataIdentifier getVCSimulationDataIdentifier() {
	return new cbit.vcell.solvers.VCSimulationDataIdentifier(vcSimulationIdentifier, jobIndex);
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
public boolean isConsumable() {
	if (isProgressEvent() || isNewDataEvent()) {
		return true;
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
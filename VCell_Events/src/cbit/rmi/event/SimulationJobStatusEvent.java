package cbit.rmi.event;

import cbit.vcell.simulation.VCSimulationIdentifier;
import cbit.vcell.util.events.MessageData;
import cbit.vcell.util.events.MessageEvent;
import cbit.vcell.util.events.MessageSource;

/**
 * Insert the type's description here.
 * Creation date: (2/10/2004 1:30:21 PM)
 * @author: Fei Gao
 */
public class SimulationJobStatusEvent extends MessageEvent {
	private SimulationJobStatus jobStatus = null;
	private Double progress = null;
	private Double timePoint = null;	
/**
 * SimulationStatusEvent constructor comment.
 * @param source java.lang.Object
 * @param messageSource cbit.rmi.event.MessageSource
 * @param messageData cbit.rmi.event.MessageData
 */
public SimulationJobStatusEvent(Object source, String simID, SimulationJobStatus jobStatus0, Double progress0, Double timePoint0) {
	super(source, new MessageSource(source, simID), new MessageData(new Double[] { progress0, timePoint0 }));
	jobStatus = jobStatus0;
	progress = progress0;
	timePoint = timePoint0;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 1:30:21 PM)
 * @return int
 */
public int getEventTypeID() {
	return 0;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 1:34:47 PM)
 * @return cbit.vcell.messaging.db.SimulationJobStatus
 */
public SimulationJobStatus getJobStatus() {
	return jobStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (1/10/2001 9:50:48 AM)
 * @return double
 */
public Double getProgress() {
	return progress;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 2:50:01 PM)
 * @return java.lang.String
 */
public String getStatusMessage() {
	if (jobStatus == null) {
		return null;
	}
	
	return jobStatus.getStatusMessage();
}
/**
 * Insert the method's description here.
 * Creation date: (1/10/2001 9:50:48 AM)
 * @return double
 */
public Double getTimepoint() {
	return timePoint;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 1:30:21 PM)
 * @return cbit.vcell.server.User
 */
public org.vcell.util.document.User getUser() {
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 2:26:56 PM)
 * @return cbit.sql.KeyValue
 */
public VCSimulationIdentifier getVCSimulationIdentifier() {
	if (jobStatus == null) {
		return null;
	}

	return jobStatus.getVCSimulationIdentifier();
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 1:30:21 PM)
 * @return boolean
 */
public boolean isConsumable() {
	if (jobStatus.isRunning() && getProgress() != null && getProgress().doubleValue() > 0) {
		return true;
	}
	
	return false;
}
}

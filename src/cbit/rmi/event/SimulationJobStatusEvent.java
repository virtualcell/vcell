package cbit.rmi.event;

import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.messaging.db.SimulationJobStatus;

/**
 * Insert the type's description here.
 * Creation date: (2/10/2004 1:30:21 PM)
 * @author: Fei Gao
 */
public class SimulationJobStatusEvent extends MessageEvent {
	private cbit.vcell.messaging.db.SimulationJobStatus jobStatus = null;
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
public SimulationMessage getSimulationMessage() {
	if (jobStatus == null) {
		return null;
	}
	
	return jobStatus.getSimulationMessage();
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
public boolean isSupercededBy(MessageEvent messageEvent) {
	if (messageEvent instanceof SimulationJobStatusEvent){
		SimulationJobStatusEvent simulationJobStatusEvent = (SimulationJobStatusEvent)messageEvent;
		
		if (jobStatus.isRunning() && getProgress() != null && simulationJobStatusEvent.jobStatus.isRunning() && simulationJobStatusEvent.getProgress() !=null){
			if (getProgress()<simulationJobStatusEvent.getProgress()){
				return true;
			}
		}
			
	}
		
	return false;
}

}

package cbit.rmi.event;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.VCDataIdentifier;
import cbit.util.document.User;
import cbit.vcell.util.events.MessageData;
import cbit.vcell.util.events.MessageEvent;
import cbit.vcell.util.events.MessageSource;
/**
 * This is the event class to support the cbit.vcell.desktop.controls.ExportListener interface.
 */
public class DataJobEvent extends MessageEvent {
	private int eventType = 0;
	private Double progress = null;
	private User user = null;
	private long jobID = 0L;
	private VCDataIdentifier vcDataIdentifier = null;
	//private VCSimulationIdentifier vcSimulationIdentifier = null;
	private String hostName = null;

/**
 * ExportEvent constructor comment.
 */
public DataJobEvent(Object source, long jobID, User user, VCDataIdentifier vcdID,int argEventType, Double argProgress,String argHostName) {
	super(source, new MessageSource(source, vcdID.getID()), new MessageData(argProgress));
	this.eventType = argEventType;
	this.progress = argProgress;
	this.jobID = jobID;
	this.user = user;
	this.vcDataIdentifier = vcdID;
	this.hostName = argHostName;
}


/**
 * Insert the method's description here.
 * Creation date: (1/4/01 1:24:16 PM)
 * @return int
 */
public int getEventTypeID() {
	return eventType;
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2006 12:25:04 PM)
 * @return java.lang.String
 */
public java.lang.String getHostName() {
	return hostName;
}


/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 5:08:40 PM)
 * @return long
 */
public long getJobID() {
	return jobID;
}


/**
 * Insert the method's description here.
 * Creation date: (1/9/01 1:53:53 PM)
 * @return java.lang.Double
 */
public Double getProgress() {
	return progress;
}


/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 4:50:43 PM)
 * @return cbit.vcell.server.User
 */
public User getUser() {
	return user;
}


/**
 * Insert the method's description here.
 * Creation date: (7/2/2001 8:59:46 PM)
 * @return cbit.vcell.solver.SimulationInfo
 */
public VCDataIdentifier getVCDataIdentifier() {
	return vcDataIdentifier;
}


/**
 * Insert the method's description here.
 * Creation date: (8/28/2002 2:39:37 PM)
 * @return boolean
 */
public boolean isConsumable() {
	if (eventType == DATA_PROGRESS) {
		return true;
	}

	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (1/10/2001 9:55:32 AM)
 * @return java.lang.String
 */
public String toString() {
	return "ExportEvent: "
		+"source="+getSource().toString()
		+", jobID="
		+ getJobID()
		+ ", progress=\""
		+ getProgress()
		+ "\", user="
		+ getUser()
		+ ", dataID="
		+ (getVCDataIdentifier() != null ?
			getVCDataIdentifier().getID() : null);
}
}
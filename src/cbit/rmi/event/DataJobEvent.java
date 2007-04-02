package cbit.rmi.event;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.EventObject;

import cbit.util.TimeSeriesJobResults;
import cbit.vcell.export.server.*;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.server.*;
import cbit.vcell.solver.*;
/**
 * This is the event class to support the cbit.vcell.desktop.controls.ExportListener interface.
 */
public class DataJobEvent extends MessageEvent {
	private int eventType = 0;
	private Double progress = null;
	private Integer jobID;
	private VCDataIdentifier vcDataID = null;
	private TimeSeriesJobResults timeSeriesJobResults = null;
	private Exception failedJobException = null;

/**
 * ExportEvent constructor comment.
 */
public DataJobEvent(Integer argJobID,
		int argEventType,
		VCDataIdentifier argVCDataID,
		Double argProgress,
		TimeSeriesJobResults argTSJR,
		Exception argFJE) {
	super(argJobID,new MessageSource(argVCDataID,argVCDataID.getID()),new MessageData(argTSJR));
	this.jobID = argJobID;
	this.eventType = argEventType;
	this.progress = argProgress;
	this.jobID = argJobID;
	this.vcDataID = argVCDataID;
	this.timeSeriesJobResults = argTSJR;
	this.failedJobException = argFJE;
}

public Exception getFailedJobException(){
	return failedJobException;
}

public VCDataIdentifier getVCDataIdentifier(){
	return vcDataID;
}


public Integer getJobID(){
	return jobID;
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
 * Creation date: (1/9/01 1:53:53 PM)
 * @return java.lang.Double
 */
public Double getProgress() {
	return progress;
}

public TimeSeriesJobResults getTimeSeriesJobResults(){
	return timeSeriesJobResults;
}

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
	return "DataEvent: "
		+"source="+getSource().toString()
		+", jobID="
		+ jobID
		+ ", progress=\""
		+ getProgress();
}

public User getUser() {
	return vcDataID.getOwner();
}
}
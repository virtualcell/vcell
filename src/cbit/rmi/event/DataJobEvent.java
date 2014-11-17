/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.rmi.event;

import org.vcell.util.document.TimeSeriesJobResults;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VCDataJobID;

/**
 * This is the event class to support the cbit.vcell.desktop.controls.ExportListener interface.
 */
public class DataJobEvent extends MessageEvent {
	private int eventType = 0;
	private Double progress = null;
	private VCDataIdentifier vcDataID = null;
	private TimeSeriesJobResults timeSeriesJobResults = null;
	private Exception failedJobException = null;
	private VCDataJobID vcDataJobID = null;

/**
 * ExportEvent constructor comment.
 */
public DataJobEvent(VCDataJobID argVCDataJobID,
		int argEventType,
		VCDataIdentifier argVCDataID,
		Double argProgress,
		TimeSeriesJobResults argTSJR,
		Exception argFJE) {
	super(argVCDataJobID,new MessageSource(argVCDataID,argVCDataID.getID()),new MessageData(argTSJR));
	this.eventType = argEventType;
	this.progress = argProgress;
	this.vcDataID = argVCDataID;
	this.timeSeriesJobResults = argTSJR;
	this.failedJobException = argFJE;
	this.vcDataJobID = argVCDataJobID;
	
}

public Exception getFailedJobException(){
	return failedJobException;
}

public VCDataIdentifier getVCDataIdentifier(){
	return vcDataID;
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

@Override
public boolean isSupercededBy(MessageEvent messageEvent) {
	if (messageEvent instanceof DataJobEvent){
		DataJobEvent dataJobEvent = (DataJobEvent)messageEvent;
		
		if (eventType == DATA_PROGRESS && dataJobEvent.eventType == DATA_PROGRESS){
			if (getProgress() < dataJobEvent.getProgress()){
				return true;
			}
		}
			
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
		+"source="+getSource()
		+", jobID="
		+ vcDataJobID
		+ ", progress=\""
		+ getProgress();
}

public User getUser() {
	return vcDataJobID.getJobOwner();
}

public VCDataJobID getVcDataJobID() {
	return vcDataJobID;
}

@Override
public boolean isIntendedFor(User user){
	if (user == null || getUser()==null){
		return true;
	}
	return user.equals(getUser());
}

}

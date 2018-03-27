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

import org.vcell.api.common.events.DataJobEventRepresentation;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.TimeSeriesJobResults;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataJobID;

/**
 * This is the event class to support the cbit.vcell.desktop.controls.ExportListener interface.
 */
public class DataJobEvent extends MessageEvent {
	private final int eventType;
	private final Double progress;
	private final KeyValue dataKey;
	private final String dataIdString;
	private final VCDataJobID vcDataJobID;

/**
 * ExportEvent constructor comment.
 */
public DataJobEvent(VCDataJobID argVCDataJobID,
		int argEventType,
		KeyValue dataKey,
		String dataIdString,
		Double argProgress
		) {
	super(argVCDataJobID,new MessageSource(argVCDataJobID,dataIdString),new MessageData(argProgress));
	this.eventType = argEventType;
	this.progress = argProgress;
	this.dataKey = dataKey;
	this.dataIdString = dataIdString;
	this.vcDataJobID = argVCDataJobID;
	
}

public KeyValue getDataKey() {
	return dataKey;
}


public String getDataIdString() {
	return dataIdString;
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

public DataJobEventRepresentation toJsonRep() {
	int eventType = this.eventType;
	Double progress = this.progress;
	String username = this.vcDataJobID.getJobOwner().getName();
	String userkey = this.vcDataJobID.getJobOwner().getID().toString();
	long jobid = this.vcDataJobID.getJobID();
	boolean isBackgroundTask = this.vcDataJobID.isBackgroundTask();
	String dataIdString = this.dataIdString;
	String dataKey = this.dataKey.toString();
	
	DataJobEventRepresentation rep = new DataJobEventRepresentation(
			eventType, progress, username, userkey, jobid, isBackgroundTask, dataIdString, dataKey);
	return rep;
}

public static DataJobEvent fromJsonRep(Object eventSource, DataJobEventRepresentation eventRep) {
	Double progress = eventRep.progress;
	User owner = new User(eventRep.username,new KeyValue(eventRep.userkey));
	VCDataJobID dataJobID = new VCDataJobID(eventRep.jobid, owner, eventRep.isBackgroundTask);
	int eventType = eventRep.eventType;
	KeyValue dataKey = new KeyValue(eventRep.dataKey);
	String dataIdString = eventRep.dataIdString;
	DataJobEvent dataJobEvent = new DataJobEvent(dataJobID, eventType, dataKey, dataIdString, progress);
	return dataJobEvent;
}

}

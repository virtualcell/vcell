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
import org.vcell.api.common.events.ExportEventRepresentation;
import org.vcell.api.common.events.ExportTimeSpecs;
import org.vcell.api.common.events.ExportVariableSpecs;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.export.server.VariableSpecs;

/**
 * This is the event class to support the cbit.vcell.desktop.controls.ExportListener interface.
 */
public class ExportEvent extends MessageEvent {
	private final int eventType;
	private final Double progress;
	private final String format;
	private final String location;
	private final User user;
	private final long jobID;
	private final KeyValue dataKey;
	private final String dataIdString;
	private final TimeSpecs timeSpecs;
	private final VariableSpecs variableSpecs;
	private final String clientJobID;
	
	public ExportEvent(Object source, long jobID, User user, 
			VCDataIdentifier vcDataId, int argEventType, 
			String format, String location, Double argProgress,
			TimeSpecs timeSpecs, VariableSpecs variableSpecs,String clientJobID) {

		this(source,jobID,user,
			vcDataId.getID(),vcDataId.getDataKey(),argEventType,
			format,location,argProgress,timeSpecs,variableSpecs,clientJobID);
	}
	
	public ExportEvent(Object source, long jobID, User user, 
		String dataIdString, KeyValue dataKey, int argEventType, 
		String format, String location, Double argProgress,
		TimeSpecs timeSpecs, VariableSpecs variableSpecs,String clientJobID) {
	super(source, new MessageSource(source, dataIdString), new MessageData(argProgress));
	this.eventType = argEventType;
	this.format = format;
	this.location = location;
	this.progress = argProgress;
	this.jobID = jobID;
	this.user = user;
	this.dataIdString = dataIdString;
	this.dataKey = dataKey;
	this.timeSpecs = timeSpecs;
	this.variableSpecs = variableSpecs;
	this.clientJobID = clientJobID;
}

public String getclientJobID() {
	return clientJobID;
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
 * Creation date: (1/9/01 1:51:54 PM)
 * @return java.lang.String
 */
public String getFormat() {
	return format;
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
 * Creation date: (1/9/01 1:51:54 PM)
 * @return java.lang.String
 */
public String getLocation() {
	return location;
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

public KeyValue getDataKey() {
	return dataKey;
}


public String getDataIdString() {
	return dataIdString;
}

public TimeSpecs getTimeSpecs() {
	return timeSpecs;
}
public VariableSpecs getVariableSpecs() {
	return variableSpecs;
}

@Override
public boolean isSupercededBy(MessageEvent messageEvent) {
	if (messageEvent instanceof ExportEvent){
		ExportEvent exportEvent = (ExportEvent)messageEvent;
		
		if (eventType == EXPORT_PROGRESS && exportEvent.eventType == EXPORT_PROGRESS){
			if (getProgress() < exportEvent.getProgress()){
				return true;
			}
		}
			
	}
		
	return false;
}

@Override
public boolean isIntendedFor(User user){
	if (user == null || getUser()==null){
		return true;
	}
	return user.equals(getUser());
}

/**
 * Insert the method's description here.
 * Creation date: (1/10/2001 9:55:32 AM)
 * @return java.lang.String
 */
public String toString() {
	return "ExportEvent: location="
		+ getLocation()
		+ ", jobID="
		+ getJobID()
		+ ", progress=\""
		+ getProgress()
		+ "\", user="
		+ getUser()
		+ ", simID="
		+ dataIdString;
}


public ExportEventRepresentation toJsonRep() {
	ExportTimeSpecs exportTimeSpecs = null;
	if (timeSpecs!=null) {
		exportTimeSpecs = timeSpecs.toJsonRep();
	}
	ExportVariableSpecs exportVariableSpecs = null;
	if (variableSpecs!=null) {
		exportVariableSpecs = variableSpecs.toJsonRep();
	}
	return new ExportEventRepresentation(
			eventType,progress,format,
			location,user.getName(),user.getID().toString(), jobID,
			dataIdString, dataKey.toString(),
			exportTimeSpecs, exportVariableSpecs,clientJobID);
}


public static ExportEvent fromJsonRep(Object eventSource, ExportEventRepresentation rep) {
	User user = new User(rep.username, new KeyValue(rep.userkey));
	TimeSpecs timeSpecs = null;
	if (rep.exportTimeSpecs!=null) {
		timeSpecs = TimeSpecs.fromJsonRep(rep.exportTimeSpecs);
	}
	VariableSpecs variableSpecs = null;
	if (rep.exportVariableSpecs!=null) {
		variableSpecs = VariableSpecs.fromJsonRep(rep.exportVariableSpecs);
	}
	ExportEvent event = new ExportEvent(
		eventSource, rep.jobid, user, 
		rep.dataIdString, new KeyValue(rep.dataKey), rep.eventType, 
		rep.format, rep.location, rep.progress,
		timeSpecs, variableSpecs,rep.clientJobID);
	return event;
}
}

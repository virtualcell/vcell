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
import java.io.Serializable;

import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.export.server.ExportSpecs;

/**
 * This is the event class to support the cbit.vcell.desktop.controls.ExportListener interface.
 */
public class ExportEvent extends MessageEvent {
	private int eventType = 0;
	private Double progress = null;
	private String format = null;
	private String location = null;
	private User user = null;
	private long jobID = 0L;
	private VCDataIdentifier vcDataIdentifier = null;

	public static class AnnotatedExportEvent extends ExportEvent implements Serializable{
		private ExportSpecs exportSpecs;
		public AnnotatedExportEvent(Object source, long jobID, User user, VCDataIdentifier vcsID, int argEventType, String format, String location, Double argProgress,ExportSpecs exportSpecs){
			super(source, jobID, user, vcsID, argEventType, format, location, argProgress);
			this.exportSpecs = exportSpecs;
		}
		public ExportSpecs getExportSpecs(){
			return exportSpecs;
		}
	}
/**
 * ExportEvent constructor comment.
 */
public ExportEvent(Object source, long jobID, User user, VCDataIdentifier vcsID, int argEventType, String format, String location, Double argProgress) {
	super(source, new MessageSource(source, vcsID.getID()), new MessageData(argProgress));
	this.eventType = argEventType;
	this.format = format;
	this.location = location;
	this.progress = argProgress;
	this.jobID = jobID;
	this.user = user;
	this.vcDataIdentifier = vcsID;
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


/**
 * Insert the method's description here.
 * Creation date: (7/2/2001 8:59:46 PM)
 * @return cbit.vcell.solver.SimulationInfo
 */
public VCDataIdentifier getVCDataIdentifier() {
	return vcDataIdentifier;
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
		+ (getVCDataIdentifier() != null ?
			getVCDataIdentifier().getID() : null);
}
}

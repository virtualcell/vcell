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

import cbit.vcell.export.server.ExportEnums;
import cbit.vcell.export.server.HumanReadableExportData;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.export.server.VariableSpecs;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

/**
 * This is the event class to support the cbit.vcell.desktop.controls.ExportListener interface.
 */
public class ExportEvent extends MessageEvent {
	@JsonProperty(value = "eventType")
	private final ExportEnums.ExportProgressType eventType;
	@JsonProperty(value = "progress")
	private final Double progress;
	@JsonProperty(value = "format")
	private final String format;
	@JsonProperty(value = "location")
	private final String location;
	@JsonProperty(value = "user")
	private final User user;
	@JsonProperty(value = "jobID")
	private final long jobID;
	@JsonProperty(value = "dataKey")
	private final KeyValue dataKey;
	@JsonProperty(value = "dataIdString")
	private final String dataIdString;

	@JsonIgnore
	private HumanReadableExportData humanReadableExportData = null;
	
	public ExportEvent(Object source, long jobID, User user, 
			VCDataIdentifier vcDataId, ExportEnums.ExportProgressType argEventType,
			String format, String location, Double argProgress) {

		this(source,jobID,user,
			vcDataId.getID(),vcDataId.getDataKey(),argEventType,
			format,location,argProgress);
	}

	@JsonCreator
	public ExportEvent(@JsonProperty("jobID") long jobID, @JsonProperty("user") User user,
					   @JsonProperty("dataIdString") String vcDataId, @JsonProperty("dataKey") String dataKey,
					   @JsonProperty("eventType") ExportEnums.ExportProgressType argEventType,
					   @JsonProperty("format") String format, @JsonProperty("location") String location, @JsonProperty("progress") Double argProgress,
					   @JsonProperty("humanReadableData") HumanReadableExportData humanReadableExportData) {

		this(ExportEvent.class, jobID,user,
				vcDataId, new KeyValue(dataKey),argEventType,
				format,location,argProgress);
		this.humanReadableExportData = humanReadableExportData;
	}
	
	public ExportEvent(Object source, long jobID, User user,
		String dataIdString, KeyValue dataKey, ExportEnums.ExportProgressType argEventType,
		String format, String location, Double argProgress) {
	super(source, new MessageSource(source, dataIdString), new MessageData(argProgress));
	this.eventType = argEventType;
	this.format = format;
	this.location = location;
	this.progress = argProgress;
	this.jobID = jobID;
	this.user = user;
	this.dataIdString = dataIdString;
	this.dataKey = dataKey;
}


/**
 * Insert the method's description here.
 * Creation date: (1/4/01 1:24:16 PM)
 * @return int
 */
@JsonIgnore
public int getEventTypeID() {
	return eventType.intValue;
}

public ExportEnums.ExportProgressType getEventType() {
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

@Override
public boolean isSupercededBy(MessageEvent messageEvent) {
	if (messageEvent instanceof ExportEvent){
		ExportEvent exportEvent = (ExportEvent)messageEvent;
		
		if (eventType == ExportEnums.ExportProgressType.EXPORT_PROGRESS && exportEvent.eventType == ExportEnums.ExportProgressType.EXPORT_PROGRESS){
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

	public void setHumanReadableExportData(HumanReadableExportData humanReadableExportData){
	this.humanReadableExportData = humanReadableExportData;
}

public HumanReadableExportData getHumanReadableData(){
	return humanReadableExportData;
}

}

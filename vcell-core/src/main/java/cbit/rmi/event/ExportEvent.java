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
public abstract class ExportEvent extends MessageEvent {
	protected final int eventType;
	protected final Double progress;
	protected final String format;
	protected final String location;
	protected final User user;
	protected final long jobID;
	protected final KeyValue dataKey;
	protected final String dataIdString;
	protected final TimeSpecs timeSpecs;
	protected final VariableSpecs variableSpecs;
	
	protected ExportEvent(Object source, long jobID, User user, VCDataIdentifier vcDataId, int argEventType, String format,
					   String location, Double argProgress, TimeSpecs timeSpecs, VariableSpecs variableSpecs) {
		this(source, jobID, user, vcDataId.getID(), vcDataId.getDataKey(), argEventType,
			format, location, argProgress, timeSpecs, variableSpecs);
	}
	
	protected ExportEvent(Object source, long jobID, User user, String dataIdString, KeyValue dataKey, int argEventType,
					   String format, String location, Double argProgress, TimeSpecs timeSpecs,
					   VariableSpecs variableSpecs) {
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
	public abstract boolean isSupercededBy(MessageEvent messageEvent);

	@Override
	public boolean isIntendedFor(User user){
		return user == null || this.getUser() == null || user.equals(getUser());
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/10/2001 9:55:32 AM)
	 * @return java.lang.String
	 */
	public String toString() {
		return String.format("ExportEvent: location=%s, jobID=%s, progress=\"%s\", user=%s, simID=%s",
				this.getLocation(),
				this.getJobID(),
				this.getProgress(),
				this.getUser(),
				this.dataIdString);
	}


	public ExportEventRepresentation toJsonRep() {
		ExportTimeSpecs exportTimeSpecs = timeSpecs != null ? timeSpecs.toJsonRep() : null;
		ExportVariableSpecs exportVariableSpecs = variableSpecs != null ? variableSpecs.toJsonRep() : null;
		return new ExportEventRepresentation(
				eventType, progress, format,
				location, user.getName(), user.getID().toString(), jobID,
				dataIdString, dataKey.toString(),
				exportTimeSpecs, exportVariableSpecs);
	}


	public static ExportEvent fromJsonRep(Object eventSource, ExportEventRepresentation rep) {
		User user = new User(rep.username, new KeyValue(rep.userkey));
		KeyValue key = new KeyValue(rep.dataKey);

		switch (rep.eventType){
			case MessageEvent.EXPORT_ASSEMBLING:
				return new ExportAssemblingEvent(eventSource, rep.jobid, user,
						rep.dataIdString, key, rep.format);

			case MessageEvent.EXPORT_START:
				return new ExportStartEvent(eventSource, rep.jobid, user,
						rep.dataIdString, key, rep.format);

			case MessageEvent.EXPORT_PROGRESS:
				return new ExportProgressEvent(eventSource, rep.jobid, user,
						rep.dataIdString, key, rep.format, rep.progress);

			case MessageEvent.EXPORT_FAILURE:
				return new ExportFailureEvent(eventSource, rep.jobid, user,
						rep.dataIdString, key, rep.format, rep.location);

			case MessageEvent.EXPORT_COMPLETE:
				TimeSpecs timeSpecs = rep.exportTimeSpecs != null ?
						TimeSpecs.fromJsonRep(rep.exportTimeSpecs) : null;
				VariableSpecs variableSpecs = rep.exportVariableSpecs != null ?
						VariableSpecs.fromJsonRep(rep.exportVariableSpecs) : null;
				return new ExportCompleteEvent(eventSource, rep.jobid, user,
						rep.dataIdString, key, rep.format, rep.location, timeSpecs, variableSpecs);

			default:
				throw new RuntimeException("Unexpected ExportEvent type encountered");
		}
	}
}

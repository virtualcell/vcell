/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.server;
import cbit.vcell.solver.VCSimulationIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (2/10/2004 1:30:21 PM)
 * @author: Fei Gao
 */
public class SimStatusEvent extends java.util.EventObject {
	private VCSimulationIdentifier vcSimID = null;
	private boolean newDataEvent = false;
	private int jobIndex = -1;
	private boolean newFailureEvent = false;

/**
 * SimulationStatusEvent constructor comment.
 * @param source java.lang.Object
 * @param messageSource cbit.rmi.event.MessageSource
 * @param messageData cbit.rmi.event.MessageData
 */
public SimStatusEvent(Object source, VCSimulationIdentifier vcSimID, boolean newDataEvent, boolean newFailureEvent, int jobIndex) {
	super(source);
	setVCSimulationIdentifier(vcSimID);
	setNewDataEvent(newDataEvent);
	setNewFailureEvent(newFailureEvent);
	setJobIndex(jobIndex);
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2005 11:43:09 PM)
 * @return int
 */
public int getJobIndex() {
	return jobIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 2:26:56 PM)
 * @return cbit.sql.KeyValue
 */
public VCSimulationIdentifier getVCSimulationIdentifier() {
	return vcSimID;
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2005 11:39:53 PM)
 * @return boolean
 */
public boolean isNewDataEvent() {
	return newDataEvent;
}


/**
 * Insert the method's description here.
 * Creation date: (10/18/2005 4:01:43 PM)
 * @return boolean
 */
public boolean isNewFailureEvent() {
	return newFailureEvent;
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2005 11:43:09 PM)
 * @param newJobIndex int
 */
private void setJobIndex(int newJobIndex) {
	jobIndex = newJobIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2005 11:39:53 PM)
 * @param newNewDataEvent boolean
 */
private void setNewDataEvent(boolean newNewDataEvent) {
	newDataEvent = newNewDataEvent;
}


/**
 * Insert the method's description here.
 * Creation date: (10/18/2005 4:01:43 PM)
 * @param newNewFailureEvent boolean
 */
private void setNewFailureEvent(boolean newNewFailureEvent) {
	newFailureEvent = newNewFailureEvent;
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2005 4:31:25 PM)
 * @param newVcSimID cbit.vcell.solver.VCSimulationIdentifier
 */
private void setVCSimulationIdentifier(cbit.vcell.solver.VCSimulationIdentifier newVcSimID) {
	vcSimID = newVcSimID;
}
}

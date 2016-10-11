/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;

import org.vcell.util.SessionLog;
import org.vcell.util.document.User;

import cbit.vcell.client.server.AsynchMessageManager;

/**
 * Insert the type's description here.
 * Creation date: (9/17/2004 4:16:30 PM)
 * @author: Ion Moraru
 */
public class PerformanceMonitoringFacility implements cbit.rmi.event.PerformanceMonitorListener {
	private User user = null;
	private SessionLog sessionLog = null;

/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 4:28:47 PM)
 * @param vcConn cbit.vcell.server.VCellConnection
 */
public PerformanceMonitoringFacility(User user, SessionLog sessionLog) {
	this.user = user;
	this.sessionLog = sessionLog;
}


/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 4:22:31 PM)
 * @param pme cbit.rmi.event.PerformanceMonitorEvent
 */
public void performanceMonitorEvent(cbit.rmi.event.PerformanceMonitorEvent pme) {
	// for now, just log them
	String logEntry = "Performance Monitor: ";
	logEntry += user + "; ";
	logEntry += pme.getEventTypeName() + "; ";
	logEntry += pme.getPerfData().getMethodName() + "; ";
	for (int i = 0; i < pme.getPerfData().getEntries().length; i++){
		logEntry += pme.getPerfData().getEntries()[i].getIdentifier() + ": ";	
		logEntry += pme.getPerfData().getEntries()[i].getValue() + "; ";	
	}
	if (AsynchMessageManager.lg.isTraceEnabled( )) {
		sessionLog.print(logEntry);
	}
}
}

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
import org.vcell.util.document.User;

/**
 * Insert the type's description here.
 * Creation date: (9/17/2004 12:51:09 PM)
 * @author: Ion Moraru
 */
public class PerformanceMonitorEvent extends MessageEvent {
	private User user = null;
	private PerformanceData perfData = null;

/**
 * PerformanceMonitorEvent constructor comment.
 * @param source java.lang.Object
 * @param messageSource cbit.rmi.event.MessageSource
 * @param messageData cbit.rmi.event.MessageData
 */
public PerformanceMonitorEvent(Object source, User user, PerformanceData perfData) {
	super(source, new MessageSource(source, perfData.getMethodName()), new MessageData(perfData));
	this.user = user;
	this.perfData = perfData;
}


/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 12:51:09 PM)
 * @return int
 */
public int getEventTypeID() {
	return perfData.getDataType();
}


/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 12:51:09 PM)
 * @return int
 */
public String getEventTypeName() {
	switch (getEventTypeID()) {
		case POLLING_STAT: {
			return "POLLING STAT";
		} 
		case SAVING_STAT: {
			return "SAVING STAT";
		} 
		default: {
			return "UKNOWN STAT";
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 4:41:24 PM)
 * @return cbit.rmi.event.PerformanceData
 */
public PerformanceData getPerfData() {
	return perfData;
}


/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 12:51:09 PM)
 * @return cbit.vcell.server.User
 */
public User getUser() {
	return user;
}

@Override
public boolean isIntendedFor(User user){
	if (user == null || getUser()==null){
		return true;
	}
	return user.equals(getUser());
}

@Override
public boolean isSupercededBy(MessageEvent messageEvent) {
	return false;
}

}

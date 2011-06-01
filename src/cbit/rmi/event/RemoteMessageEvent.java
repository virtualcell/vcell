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

/**
 * Insert the type's description here.
 * Creation date: (11/13/2000 2:35:23 PM)
 * @author: IIM
 */
public class RemoteMessageEvent extends java.util.EventObject {
	private MessageEvent messageEvent = null;
	private long remoteSource = 0L;
/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 2:38:46 PM)
 * @param source java.lang.Object
 * @param messageEvent cbit.rmi.event.MessageEvent
 */
public RemoteMessageEvent(long remoteSource, MessageEvent messageEvent) {
	super(new Long(remoteSource)); // super wants something non-null, although it's transient !!
	this.remoteSource = remoteSource;
	this.messageEvent = messageEvent;
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 4:42:47 PM)
 * @return cbit.rmi.event.MessageEvent
 */
public MessageEvent getMessageEvent() {
	return messageEvent;
}
	public long getRemoteSource() {
		return remoteSource;
	}
}

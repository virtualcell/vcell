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

import cbit.vcell.message.VCMessagingConstants;

/**
 * Insert the type's description here.
 * Creation date: (6/16/2006 3:55:11 PM)
 * @author: Jim Schaff
 */
public class VCellMessageEvent extends MessageEvent {
	public static final int VCELL_MESSAGEEVENT_TYPE_BROADCAST = 2001;
	int eventType;
	private String username;

/**
 * VCellMessageEvent constructor comment.
 * @param source java.lang.Object
 * @param messageSource cbit.rmi.event.MessageSource
 * @param messageData cbit.rmi.event.MessageData
 */
public VCellMessageEvent(Object source, String messageID, MessageData messageData,int messageType,String username) {
	super(source, new MessageSource(source, messageID), messageData);
	eventType = messageType;
	this.username = username;
}


/**
 * Insert the method's description here.
 * Creation date: (6/16/2006 3:55:11 PM)
 * @return int
 */
public int getEventTypeID() {
	return eventType;
}


/**
 * Insert the method's description here.
 * Creation date: (6/16/2006 3:55:11 PM)
 * @return cbit.vcell.server.User
 */
public User getUser() {
	return null;
}

@Override
public boolean isIntendedFor(User user){
	if (user == null || username==null || username.equals(VCMessagingConstants.USERNAME_PROPERTY_VALUE_ALL)){
		return true;
	}
	return user.getName().equals(username);
}


@Override
public boolean isSupercededBy(MessageEvent messageEvent) {
	return false;
}


}

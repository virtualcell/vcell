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

import cbit.vcell.mongodb.VCMongoMessage;


/**
 * Insert the type's description here.
 * Creation date: (11/14/2000 12:12:41 AM)
 * @author: 
 */
public class SimpleMessageService implements MessageService, MessageListener {
	private MessageQueue messageQueue = new MessageQueue();
	private User user = null;
/**
 * Insert the method's description here.
 * Creation date: (11/14/2000 12:19:31 AM)
 */
public SimpleMessageService(User user) {
	this.user = user;
}

public void messageEvent(MessageEvent event) {
	if (event.isIntendedFor(user)){
		VCMongoMessage.sendClientMessageEventQueued(event);
		messageQueue.push(event);
	}	
}

public MessageEvent[] getMessageEvents() {
	return messageQueue.popAll();
}

public long timeSinceLastPoll() {
	return messageQueue.timeSinceLastPopAll();
}
}

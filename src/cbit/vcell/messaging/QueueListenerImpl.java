/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.messaging;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Insert the type's description here.
 * Creation date: (9/26/2003 10:48:42 AM)
 * @author: Fei Gao
 */
public abstract class QueueListenerImpl implements javax.jms.MessageListener {
/**
 * QueueListenerImpl constructor comment.
 */
public QueueListenerImpl() {
	super();
}
/**
 * onMessage method comment.
 */
public final void onMessage(Message message) {
	try {
		onQueueMessage(message);
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}	
}
	public abstract void onQueueMessage(Message message) throws JMSException ;
}

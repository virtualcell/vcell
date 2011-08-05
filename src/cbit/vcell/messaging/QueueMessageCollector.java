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
 * Creation date: (9/26/2003 10:54:16 AM)
 * @author: Fei Gao
 */
public class QueueMessageCollector extends QueueListenerImpl {
	private QueueListener queueListener;
/**
 * QueueMessageCollector constructor comment.
 */
public QueueMessageCollector(QueueListener queueListener0) {
	super();
	queueListener = queueListener0;
}
/**
 * onQueueMessage method comment.
 */
public void onQueueMessage(Message message) throws JMSException {
	if (queueListener != null) {
		queueListener.onQueueMessage(message);
	}
}
}

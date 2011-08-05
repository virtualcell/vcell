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

import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (10/10/2003 10:21:50 AM)
 * @author: Fei Gao
 */
public interface JmsConnection extends ExceptionListener {
	public void close() throws JMSException;
	public void closeSession(JmsSession session) throws JMSException;
	JmsProvider getJmsProvider();
	public boolean isBadConnection(JMSException ex);
	public boolean isConnectionDropped();
	public void startConnection() throws JMSException;
	public void stopConnection() throws JMSException;
	
	public JmsSession getAutoSession() throws JMSException;
	public JmsSession getClientAckSession() throws JMSException;
	Connection getConnection();
	public JmsSession getTransactedSession() throws JMSException;

}

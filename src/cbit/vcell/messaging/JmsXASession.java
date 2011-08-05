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
import javax.transaction.TransactionManager;

/**
 * Insert the type's description here.
 * Creation date: (10/8/2003 1:13:29 PM)
 * @author: Fei Gao
 */
public interface JmsXASession extends JmsSession {
	void setupXASession() throws JMSException;
	boolean joinTransaction(TransactionManager tm) throws JMSException;
}

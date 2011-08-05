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

/**
 * Insert the type's description here.
 * Creation date: (2/24/2004 2:24:10 PM)
 * @author: Fei Gao
 */
public interface JmsConnectionFactory {
	public JmsXAConnection createXAConnection() throws JMSException;
	public JmsConnection createConnection() throws JMSException;
}

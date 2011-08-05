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
 * Creation date: (5/22/2003 2:43:23 PM)
 * @author: Fei Gao
 */
public interface ControlTopicListener {
void onControlTopicMessage(javax.jms.Message message) throws JMSException;
}

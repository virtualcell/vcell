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
 * Creation date: (11/13/2000 5:23:03 PM)
 * @author: IIM
 */
public interface MessageService {
/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 5:24:14 PM)
 * @return cbit.rmi.event.MessageCollector
 */
MessageCollector getMessageCollector();
/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 5:25:44 PM)
 * @return cbit.rmi.event.MessageDispatcher
 */
MessageDispatcher getMessageDispatcher();
/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 5:23:38 PM)
 * @return cbit.rmi.event.MessageHandler
 */
MessageHandler getMessageHandler();
}

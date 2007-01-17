package cbit.rmi.event;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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

package cbit.vcell.util.events;


/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (11/12/2000 3:18:29 PM)
 * @author: IIM
 */
public interface MessageListener extends java.util.EventListener {
/**
 * 
 * @param event cbit.rmi.event.MessageEvent
 */
void messageEvent(cbit.vcell.util.events.MessageEvent event);
}

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
MessageEvent[] getMessageEvents();
long timeSinceLastPoll();
}

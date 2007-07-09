package org.vcell.util;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (10/4/00 9:57:39 AM)
 * @author: 
 */
public interface Stateful {
/**
 * Insert the method's description here.
 * Creation date: (10/4/00 9:59:16 AM)
 * @param stateID java.lang.String
 */
void activateMarkedState(String stateID);
/**
 * Insert the method's description here.
 * Creation date: (6/29/2003 1:46:33 PM)
 */
void clearMarkedState(String stateID);
/**
 * Insert the method's description here.
 * Creation date: (6/29/2003 1:46:33 PM)
 */
void clearMarkedStates();
/**
 * Insert the method's description here.
 * Creation date: (11/2/2000 12:16:36 PM)
 * @return boolean
 * @param stateID java.lang.String
 */
boolean hasStateID(String stateID);
/**
 * Insert the method's description here.
 * Creation date: (10/4/00 9:58:23 AM)
 * @param stateID java.lang.String
 */
 void markCurrentState(String stateID);
}

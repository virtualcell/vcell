/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;

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

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;

/**
 * Insert the type's description here.
 * Creation date: (1/14/00 1:53:05 PM)
 * @author: 
 */
public class ODEDataInfo extends DataInfo {
/**
 * Insert the method's description here.
 * Creation date: (1/14/00 1:56:03 PM)
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
public ODEDataInfo(org.vcell.util.document.User user, String simID, long dataBlockTimeStamp) {
	super(user, simID, NO_VARIABLE, NO_TIME, dataBlockTimeStamp, "Ode");
}
}

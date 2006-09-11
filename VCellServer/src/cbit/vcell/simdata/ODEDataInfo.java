package cbit.vcell.simdata;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
public ODEDataInfo(cbit.util.User user, String simID, long dataBlockTimeStamp) {
	super(user, simID, NO_VARIABLE, NO_TIME, dataBlockTimeStamp, "Ode");
}
}

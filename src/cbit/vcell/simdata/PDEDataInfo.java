package cbit.vcell.simdata;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class PDEDataInfo extends DataInfo {
/**
 * PDESimData constructor comment.
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 * @param varName java.lang.String
 * @param simTime double
 * @param dataBlockTimeStamp long
 */
public PDEDataInfo(cbit.vcell.server.User user, String simID, String varName, double simTime, long dataBlockTimeStamp) {
	super(user, simID, varName, simTime, dataBlockTimeStamp, "Pde");
}
}

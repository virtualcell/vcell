package cbit.vcell.simdata;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class ParticleDataInfo extends DataInfo {
/**
 * ParticleDataInfo constructor comment.
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 * @param simTime double
 */
public ParticleDataInfo(cbit.vcell.server.User user, String simID, double simTime, long dataBlockTimeStamp) {
	super(user, simID, NO_VARIABLE, simTime, dataBlockTimeStamp, "Particle");
}
}

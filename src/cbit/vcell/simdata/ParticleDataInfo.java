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
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class ParticleDataInfo extends DataInfo {
/**
 * ParticleDataInfo constructor comment.
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 * @param simTime double
 */
public ParticleDataInfo(org.vcell.util.document.User user, String simID, double simTime, long dataBlockTimeStamp) {
	super(user, simID, NO_VARIABLE, simTime, dataBlockTimeStamp, "Particle");
}
}

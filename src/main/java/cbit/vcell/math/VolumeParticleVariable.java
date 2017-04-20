/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math;
/**
 * Stochastic variable definition.
 * Creation date: (7/7/2006 2:25:09 PM)
 * @author: Tracy LI
 */
public class VolumeParticleVariable extends ParticleVariable {
/**
 * StochVolVariable constructor comment.
 * @param name java.lang.String
 */
public VolumeParticleVariable(String name, Domain domain) {
	super(name, domain);
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj, boolean bIgnoreMissingDomain) 
{
	if (!(obj instanceof VolumeParticleVariable)){
		return false;
	}
	if (!compareEqual0(obj, bIgnoreMissingDomain)){
		return false;
	}
	
	return true;
}

public String getVCML() {
	return VCML.VolumeParticleVariable+"    "+getQualifiedName()+"\n";
}

}




/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.document;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class VersionableRelationship implements java.io.Serializable {
	private VersionableTypeVersion from = null;
	private VersionableTypeVersion to = null;
/**
 * VersionRealtionship constructor comment.
 */
public VersionableRelationship(VersionableTypeVersion argFrom,VersionableTypeVersion argTo) {
	super();
	from = argFrom;
	to = argTo;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param argFrom cbit.vcell.modeldb.VersionableTypeVersion
 * @param argTo cbit.vcell.modeldb.VersionableTypeVersion
 */
public boolean bSame(VersionableRelationship vr) {
	
	VersionableTypeVersion vtvFrom = vr.from();
	VersionableTypeVersion vtvTo = vr.to();
	if (!from.equals(vtvFrom)) {
		return false;
	}
	if (!to.equals(vtvTo)) {
		return false;
	}
	return true;
	
	/*
	//Check from
	if (from == null && argFrom == null) {
	// Do nothing yet
	} else
	if (from == null || argFrom == null) {
	return false;
	} else {
	if (!from.equals(argFrom)) {
	return false;
	}
	}
	//Check to	
	if (to == null && argTo == null) {
	return true;
	} else
	if (to == null || argTo == null) {
	return false;
	} else {
	if (!to.equals(argTo)) {
	return false;
	}
	}
	// 
	return true;
	*/
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableTypeVersion
 */
public VersionableTypeVersion from() {
	return from;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableTypeVersion
 */
public VersionableTypeVersion to() {
	return to;
}
}

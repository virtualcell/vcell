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
public class VersionableTypeVersion implements java.io.Serializable {
	private VersionableType vType = null;
	private Version version = null;
/**
 * VersionRef constructor comment.
 */
public VersionableTypeVersion(VersionableType vt,Version v) {
	super();
	vType = vt;
	version = v;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if(this == obj){
		return true;
	}
	if (obj instanceof VersionableTypeVersion){
		VersionableTypeVersion vtv = (VersionableTypeVersion)obj;
		if (vtv.getVersion().getVersionKey().equals(getVersion().getVersionKey())){
			return true;
		}
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.Field
 */
public Version getVersion() {
	return version;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.VersionableType
 */
public VersionableType getVType() {
	return vType;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int hashCode() {
	return getVersion().getVersionKey().hashCode();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return getVType().getTypeName()+":"+getVersion().getName()+":"+getVersion().getVersionKey();
}
}

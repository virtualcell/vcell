/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.surface;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.geometry.SubVolume;
/**
 * Insert the type's description here.
 * Creation date: (5/27/2004 11:37:23 AM)
 * @author: Jim Schaff
 */
public class VolumeGeometricRegion extends GeometricRegion {
	private int fieldRegionID = -1;
	private SubVolume fieldSubVolume = null;

/**
 * VolumeGeometricRegion constructor comment.
 * @param argName java.lang.String
 */
public VolumeGeometricRegion(String argName, double argSize, VCUnitDefinition argSizeUnit, SubVolume argSubVolume, int argRegionID) {
	super(argName,argSize,argSizeUnit);
	this.fieldSubVolume = argSubVolume;
	this.fieldRegionID = argRegionID;
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj instanceof VolumeGeometricRegion){
		VolumeGeometricRegion volumeRegion = (VolumeGeometricRegion)obj;
		if (!super.compareEqual0(volumeRegion)){
			return false;
		}
		if (fieldRegionID != volumeRegion.fieldRegionID){
			return false;
		}
		if (!org.vcell.util.Compare.isEqual(fieldSubVolume,volumeRegion.fieldSubVolume)){
			return false;
		}
		return true;
	}else{
		return false;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 11:40:19 AM)
 * @return int
 */
public int getRegionID() {
	return fieldRegionID;
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 11:40:19 AM)
 * @return SubVolume
 */
public SubVolume getSubVolume() {
	return fieldSubVolume;
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:25:57 AM)
 * @param newFieldSubVolume cbit.vcell.geometry.SubVolume
 */
void setSubVolume(cbit.vcell.geometry.SubVolume newSubVolume) {
	fieldSubVolume = newSubVolume;
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 1:02:26 PM)
 * @return java.lang.String
 */
public String toString() {
	return "VolumeGeometricRegion@("+Integer.toHexString(hashCode())+") '"+getName()+"', size="+getSize()+" ["+getSizeUnit().getSymbol()+"], regionID="+getRegionID()+", subVolume=("+getSubVolume()+")";
}
}

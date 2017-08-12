/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry;
import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;

/**
 * This type was created in VisualAge.
 */
public class CompartmentSubVolume extends SubVolume {
/**
 * Compartment constructor comment.
 * @param name java.lang.String
 * @param geometry cbit.vcell.geometry.Geometry
 */
public CompartmentSubVolume(KeyValue key, int handleValue) {
	super(key,"Compartment", handleValue);
	//setHandle(0);
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (!compareEqual0(obj)){
		return false;
	}
	if (!(obj instanceof CompartmentSubVolume)){
		return false;
	}

	return true;
}



/**
 * isInside method comment.
 */
public boolean isInside(double x, double y, double z, GeometrySpec geometrySpec) {
	return true;
}
}

package cbit.vcell.geometry;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.Matchable;
import cbit.sql.KeyValue;
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
 * getVCML method comment.
 */
public String getVCML() {
	return "";
}


/**
 * isInside method comment.
 */
public boolean isInside(double x, double y, double z, GeometrySpec geometrySpec) {
	return true;
}
}
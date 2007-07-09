package org.vcell.util;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class Extent implements java.io.Serializable, Matchable {
	private double x;
	private double y;
	private double z;

/**
 * Origin constructor comment.
 */
public Extent(double newX,double newY,double newZ) throws IllegalArgumentException {
	if (newX<=0 || newY<=0 || newZ<=0){
		throw new IllegalArgumentException("extent=("+newX+","+newY+","+newZ+") must be > 0 for x,y,z");
	}
	this.x = newX;
	this.y = newY;
	this.z = newZ;
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param matchable cbit.util.Matchable
 */
public boolean compareEqual(Matchable matchable) {
	if (matchable instanceof Extent){
		Extent extent = (Extent)matchable;

		if ((extent.getX() != getX()) || (extent.getY() != getY()) || (extent.getZ() != getZ())){
			return false;
		}
		return true;
	}else{
		return false;
	}
}


/**
 * This method was created in VisualAge.
 * @return double
 */
public double getX() {
	return x;
}


/**
 * This method was created in VisualAge.
 * @return double
 */
public double getY() {
	return y;
}


/**
 * This method was created in VisualAge.
 * @return double
 */
public double getZ() {
	return z;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "("+getX()+","+getY()+","+getZ()+")";
}
}
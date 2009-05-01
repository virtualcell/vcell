package org.vcell.util;


/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class Origin implements java.io.Serializable, Matchable {
	private double x;
	private double y;
	private double z;
/**
 * Origin constructor comment.
 */
public Origin(double newX,double newY,double newZ) {
	super();
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
	if (matchable instanceof Origin){
		Origin origin = (Origin)matchable;

		if ((origin.getX() != getX()) || (origin.getY() != getY()) || (origin.getZ() != getZ())){
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
	return "Origin["+getX()+","+getY()+","+getZ()+"]";
}
}

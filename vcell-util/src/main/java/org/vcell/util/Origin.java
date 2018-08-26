/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * This type was created in VisualAge.
 */
public class Origin implements java.io.Serializable, Matchable {
	@XmlAttribute
	private double x;
	@XmlAttribute
	private double y;
	@XmlAttribute
	private double z;
	public Origin() {}//For jaxb
	
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

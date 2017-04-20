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


/**
 * This type was created in VisualAge.
 */
public class ISize implements java.io.Serializable, Matchable {
	private int x;
	private int y;
	private int z;

/**
 * Origin constructor comment.
 */
public ISize(int newX,int newY,int newZ) {
	this.x = newX;
	this.y = newY;
	this.z = newZ;
	if (x<0 || y<0 || z<0){
		throw new IllegalArgumentException("("+x+","+y+","+z+") must be all non-negative");
	}
}


/**
 * Origin constructor comment.
 */
public ISize(String newX,String newY,String newZ) {
	this(Integer.parseInt(newX),Integer.parseInt(newY),Integer.parseInt(newZ));
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj instanceof ISize){
		ISize size = (ISize)obj;
		if (x != size.x){
			return false;
		}
		if (y != size.y){
			return false;
		}
		if (z != size.z){
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
public int getX() {
	return x;
}


/**
 * Insert the method's description here.
 * Creation date: (9/30/2005 10:17:58 AM)
 * @return int
 */
public int getXYZ() {
	return x*y*z;
}


/**
 * This method was created in VisualAge.
 * @return double
 */
public int getY() {
	return y;
}


/**
 * This method was created in VisualAge.
 * @return double
 */
public int getZ() {
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

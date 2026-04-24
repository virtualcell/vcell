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
 * This is an immutable class for storing points in 3-space.
 */
public class Coordinate implements java.io.Serializable,Cloneable,org.vcell.util.Matchable {
	private double x;
	private double y;
	private double z;
	//
	public final static int X_AXIS = 0;
	public final static int Y_AXIS = 1;
	public final static int Z_AXIS = 2;
	public static final String[] PLANENAMES = {"YZ","XZ","XY"};
	//Plane name represents which is horizontal(1st letter) and vertical(2nd letter) axis when looking down the normal axis
/**
 * This method was created in VisualAge.
 * @param x double
 * @param y double
 * @param z double
 */
public Coordinate(double x, double y, double z) {
	this.x = x;
	this.y = y;
	this.z = z;
}

private static final double axisConversion(double origX, double origY, double origZ, int axisElement, int normalAxis, boolean bToStandardXYZ) {
	double result = 0;
	switch (normalAxis) {
		case X_AXIS :
			switch (axisElement) {
				case X_AXIS :
					if (bToStandardXYZ) {
						result = origZ;
					} else {
						result = origY;
					}
					break;
				case Y_AXIS :
					if (bToStandardXYZ) {
						result = origX;
					} else {
						result = origZ;
					}
					break;
				case Z_AXIS :
					if (bToStandardXYZ) {
						result = origY;
					} else {
						result = origX;
					}
					break;
				default :
					throw new IllegalArgumentException("axisElement wrong");
			}
			break;
		case Y_AXIS :
			switch (axisElement) {
				case X_AXIS :
					if (bToStandardXYZ) {
						result  = origX;
					} else {
						result = origX;
					}
					break;
				case Y_AXIS :
					if (bToStandardXYZ) {
						result = origZ;
					} else {
						result = origZ;
					}
					break;
				case Z_AXIS :
					if (bToStandardXYZ) {
						result = origY;
					} else {
						result = origY;
					}
					break;
				default :
					throw new IllegalArgumentException("axisElement wrong");
			}
			break;
		case Z_AXIS :
			switch (axisElement) {
				case X_AXIS :
					result = origX;
					break;
				case Y_AXIS :
					result = origY;
					break;
				case Z_AXIS :
					result = origZ;
					break;
				default :
					throw new IllegalArgumentException("axisElement wrong");
			}
			break;
		default :
			throw new IllegalArgumentException("normalAxis wrong");
	}
	return result;
}

public Object clone() {
	try{
	Coordinate c = (Coordinate)super.clone();
	c.x = x;
	c.y = y;
	c.z = z;
	return c;
	}catch(CloneNotSupportedException e){
		//This shouldn't happen
		throw new InternalError(e.toString());
	}
}

public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj == null) {
		return false;
	}
	if (!(obj instanceof Coordinate)) {
		return false;
	}
	Coordinate coord = (Coordinate) obj;
	if (x != coord.x || y != coord.y || z != coord.z) {
		return false;
	}
	return true;
}

public static final Coordinate convertAxisFromStandardXYZToNormal(double origX, double origY, double origZ,int normalAxis) {
	Coordinate newCoord = new Coordinate(
		axisConversion(origX, origY, origZ, X_AXIS, normalAxis,false),
		axisConversion(origX, origY, origZ, Y_AXIS, normalAxis,false),
		axisConversion(origX, origY, origZ, Z_AXIS, normalAxis,false));
	return newCoord;
}
public static final ISize convertAxisFromStandardXYZToNormal(ISize isize,int normalAxis) {
	ISize newISize = new ISize(
		(int)axisConversion(isize.getX(), isize.getY(), isize.getZ(), X_AXIS, normalAxis,false),
		(int)axisConversion(isize.getX(), isize.getY(), isize.getZ(), Y_AXIS, normalAxis,false),
		(int)axisConversion(isize.getX(), isize.getY(), isize.getZ(), Z_AXIS, normalAxis,false));
	return newISize;
}
public static final Extent convertAxisFromStandardXYZToNormal(Extent extent,int normalAxis) {
	Extent newExtent = new Extent(
		(int)axisConversion(extent.getX(), extent.getY(), extent.getZ(), X_AXIS, normalAxis,false),
		(int)axisConversion(extent.getX(), extent.getY(), extent.getZ(), Y_AXIS, normalAxis,false),
		(int)axisConversion(extent.getX(), extent.getY(), extent.getZ(), Z_AXIS, normalAxis,false));
	return newExtent;
}

public static final double convertAxisFromStandardXYZToNormal(double origX, double origY, double origZ, int axisElement, int normalAxis) {
	return axisConversion(origX,origY,origZ,axisElement,normalAxis, false);
}

public static final double convertAxisFromStandardXYZToNormal(Coordinate coord, int axisElement, int normalAxis) {
	return axisConversion(coord.getX(),coord.getY(),coord.getZ(),axisElement,normalAxis, false);
}

public static final Coordinate convertCoordinateFromNormalToStandardXYZ(double origX,double origY,double origZ, int normalAxis) {
	Coordinate newCoord = new Coordinate(
		axisConversion(origX, origY, origZ, X_AXIS, normalAxis,true),
		axisConversion(origX, origY, origZ, Y_AXIS, normalAxis,true),
		axisConversion(origX, origY, origZ, Z_AXIS, normalAxis,true));
	return newCoord;
}

public static final void convertCoordinateIndexFromNormalToStandardXYZ(org.vcell.util.CoordinateIndex coordIndex, int normalAxis) {
	//Re-uses CoordinateIndex
	double origX = coordIndex.x;
	double origY = coordIndex.y;
	double origZ = coordIndex.z;
	coordIndex.x = (int)axisConversion(origX, origY, origZ, X_AXIS, normalAxis,true);
	coordIndex.y = (int)axisConversion(origX, origY, origZ, Y_AXIS, normalAxis,true);
	coordIndex.z = (int)axisConversion(origX, origY, origZ, Z_AXIS, normalAxis,true);
}

public double distanceTo(double cx, double cy, double cz) {
	cx -= x; cy -= y; cz -= z;
    return Math.sqrt(cx*cx + cy*cy + cz*cz);
}

public double distanceTo(Coordinate coord) {
	return distanceTo(coord.x,coord.y,coord.z);
}

public boolean equals(Object obj) {
	if (obj instanceof Coordinate){
		Coordinate c = (Coordinate) obj;
		return(x == c.x && y == c.y && z == c.z);
	}
	return(false);
}

public static final java.awt.geom.Point2D.Double get2DProjection(Coordinate coord,int normalAxis) {
	double newX = Coordinate.convertAxisFromStandardXYZToNormal(coord,Coordinate.X_AXIS,normalAxis);
	double newY = Coordinate.convertAxisFromStandardXYZToNormal(coord,Coordinate.Y_AXIS,normalAxis);
	return new java.awt.geom.Point2D.Double(newX,newY);
}

public double getAxisElement(int axis) {
	switch (axis) {
		case X_AXIS :
			return x;
		case Y_AXIS :
			return y;
		case Z_AXIS :
			return z;
		default :
			throw new RuntimeException("Unknow axis");
	}
}

public final static String getNormalAxisPlaneName(int normalAxis) {
	if(normalAxis < 0 || normalAxis >= PLANENAMES.length){
		throw new IllegalArgumentException("Unknwon Normal Axis = "+normalAxis);
	}
	return PLANENAMES[normalAxis];
}

public double getX () {
	return (x);
}

public double getY () {
	return (y);
}

public double getZ () {
	return (z);
}

public int hashCode() {
	long bits = Double.doubleToLongBits(x + y + z);
	return ((int)(bits ^ (bits >> 32)));
}

public static final boolean isCoordinateInBounds(Coordinate coord, org.vcell.util.Origin origin, org.vcell.util.Extent extent, Coordinate deltaCoord) {
	Coordinate w0 = new Coordinate(origin.getX(), origin.getY(), origin.getZ());
	Coordinate w1 = new Coordinate(w0.getX() + extent.getX(), w0.getY() + extent.getY(), w0.getZ() + extent.getZ());
	//
	double newX = coord.getX() + deltaCoord.getX();
	if (newX < w0.getX()) {
		if ((w0.getX() - newX) > (.000001)) {
			return false; }
	}
	if (newX > w1.getX()) {
		if ((newX - w1.getX()) > (.000001)) {
			return false; }
	}
	//
	double newY = coord.getY() + deltaCoord.getY();
	if (newY < w0.getY()) {
		if ((w0.getY() - newY) > (.000001)) {
			return false; }
	}
	if (newY > w1.getY()) {
		if ((newY - w1.getY()) > (.000001)) {
			return false; }
	}
	//
	double newZ = coord.getZ() + deltaCoord.getZ();
	if (newZ < w0.getZ()) {
		if ((w0.getZ() - newZ) > (.000001)) {
			return false; }
	}
	if (newZ > w1.getZ()) {
		if ((newZ - w1.getZ()) > (.000001)) {
			return false; }
	}
	//
	return true; 
}

public String toString() {
	return "X="+x+" Y="+y+" Z="+z;
}

public static double coordComponentFromSinglePlanePolicy(Origin argOrigin, Extent argExtent, int argAxisFlag) {
	
	if(argAxisFlag == X_AXIS){
		return argExtent.getX()/2.0 + argOrigin.getX();
	}else if(argAxisFlag == Y_AXIS){
		return argExtent.getY()/2.0 + argOrigin.getY();
	}else if(argAxisFlag == Z_AXIS){
		return argExtent.getZ()/2.0 + argOrigin.getZ();
	}
	throw new IllegalArgumentException("Unknown Axis Flag="+argAxisFlag);
}
}

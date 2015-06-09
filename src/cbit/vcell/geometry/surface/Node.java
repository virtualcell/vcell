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

import cbit.vcell.geometry.concept.ThreeSpacePoint;

/**
 * Insert the type's description here.
 * Creation date: (6/27/2003 10:35:01 PM)
 * @author: John Wagner
 */
@SuppressWarnings("serial")
public final class Node implements ThreeSpacePoint, java.io.Serializable {
	private double fieldX = 0.0;
	private double fieldY = 0.0;
	private double fieldZ = 0.0;
	private int fieldGlobalIndex = -1;
	// Could replace with bits...
	private boolean fieldMoveX = true;
	private boolean fieldMoveY = true;
	private boolean fieldMoveZ = true;
	
/**
 * create at origin
 */
public Node() {
	super();
}
/**
 * @param x double
 * @param y double
 * @param z double
 */
public Node(double x, double y, double z) {
	super();
	fieldX = x;
	fieldY = y;
	fieldZ = z;
}
/**
 * copy constructor
 * @param node 
 */
public Node(Node node) {
	super();
	fieldX = node.getX();
	fieldY = node.getY();
	fieldZ = node.getZ();
	setGlobalIndex(node.getGlobalIndex());
}
/**
 * copy constructor
 * @param other 
 */
public Node(ThreeSpacePoint other) {
	fieldX = other.getX();
	fieldY = other.getY();
	fieldZ = other.getZ();
	
}
public int getGlobalIndex() {
	
	return(fieldGlobalIndex);
}

public boolean getMoveX() {
	return(fieldMoveX);
}

public boolean getMoveY() {
	return(fieldMoveY);
}

public boolean getMoveZ() {
	return(fieldMoveZ);
}

public double getX() {
	return(fieldX);
}

public double getY() {
	return(fieldY);
}

public double getZ() {
	return(fieldZ);
}

public void setGlobalIndex(int globalIndex) {
	fieldGlobalIndex = globalIndex;
}

public void setMoveX(boolean moveX) {
	fieldMoveX = moveX;
}

public void setMoveY(boolean moveY) {
	fieldMoveY = moveY;
}

public void setMoveZ(boolean moveZ) {
	fieldMoveZ = moveZ;
}

public void setX(double x) {
	fieldX = x;
}

public void setY(double y) {
	fieldY = y;
}

public void setZ(double z) {
	fieldZ = z;
}

public String toString() {
	return "Node(" + getX() + "," + getY() + "," + getZ() + ")";
}

/**
 * return distance between this and other, squared
 * @param other not null
 * @return distance squared
 */
public double distanceSquared(Node other) {
	double dX = fieldX - other.fieldX;
	double dY = fieldY - other.fieldY;
	double dZ = fieldZ - other.fieldZ;
	return dX * dX + dY * dY + dZ * dZ;
}
/**
 * return distance between this and other.
 * {@link #distanceSquared(Node)} should be used for comparison if exact distance not required
 * @param other not null
 * @return distance squared
 */
public double distance(Node other) {
	return Math.sqrt(distanceSquared(other));
}

/**
 * multiply (scale) all coordinates for value
 * @param v
 */
public void multiply(double v) {
	fieldX *= v; 
	fieldY *= v; 
	fieldZ *= v; 
}

/**
 * move Node 
 * @param translation not null
 */
public void translate(ThreeSpacePoint translation) {
	fieldX += translation.getX();
	fieldY += translation.getY();
	fieldZ += translation.getZ();
}


}

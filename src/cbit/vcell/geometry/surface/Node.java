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

/**
 * Insert the type's description here.
 * Creation date: (6/27/2003 10:35:01 PM)
 * @author: John Wagner
 */
@SuppressWarnings("serial")
public final class Node implements java.io.Serializable {
	private double fieldX = 0.0;
	private double fieldY = 0.0;
	private double fieldZ = 0.0;
	private int fieldGlobalIndex = -1;
	// Could replace with bits...
	private boolean fieldMoveX = true;
	private boolean fieldMoveY = true;
	private boolean fieldMoveZ = true;
/**
 * Node constructor comment.
 * @param x double
 * @param y double
 * @param z double
 */
public Node() {
	super();
}
/**
 * Node constructor comment.
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
 * Node constructor comment.
 * @param coordinate surface.Coordinate
 */
public Node(Node node) {
	super();
	fieldX = node.getX();
	fieldY = node.getY();
	fieldZ = node.getZ();
	setGlobalIndex(node.getGlobalIndex());
}
/**
 * Node constructor comment.
 * @param coordinate surface.Coordinate
 */
public int getGlobalIndex() {
	return(fieldGlobalIndex);
}
/**
 * MutableCoordinate constructor comment.
 */
public boolean getMoveX() {
	return(fieldMoveX);
}
/**
 * MutableCoordinate constructor comment.
 */
public boolean getMoveY() {
	return(fieldMoveY);
}
/**
 * MutableCoordinate constructor comment.
 */
public boolean getMoveZ() {
	return(fieldMoveZ);
}
/**
 * MutableCoordinate constructor comment.
 */
public double getX() {
	return(fieldX);
}
/**
 * MutableCoordinate constructor comment.
 */
public double getY() {
	return(fieldY);
}
/**
 * MutableCoordinate constructor comment.
 */
public double getZ() {
	return(fieldZ);
}
/**
 * Node constructor comment.
 * @param coordinate surface.Coordinate
 */
public void setGlobalIndex(int globalIndex) {
	fieldGlobalIndex = globalIndex;
}
/**
 * MutableCoordinate constructor comment.
 */
public void setMoveX(boolean moveX) {
	fieldMoveX = moveX;
}
/**
 * MutableCoordinate constructor comment.
 */
public void setMoveY(boolean moveY) {
	fieldMoveY = moveY;
}
/**
 * MutableCoordinate constructor comment.
 */
public void setMoveZ(boolean moveZ) {
	fieldMoveZ = moveZ;
}
/**
 * MutableCoordinate constructor comment.
 */
public void setX(double x) {
	fieldX = x;
}
/**
 * MutableCoordinate constructor comment.
 */
public void setY(double y) {
	fieldY = y;
}
/**
 * MutableCoordinate constructor comment.
 */
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

}

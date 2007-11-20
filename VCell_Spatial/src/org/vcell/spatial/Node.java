package org.vcell.spatial;

/**
 * Insert the type's description here.
 * Creation date: (6/27/2003 10:35:01 PM)
 * @author: John Wagner
 */
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
}

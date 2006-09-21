package cbit.vcell.geometry;

import cbit.util.Coordinate;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (10/15/00 2:22:34 PM)
 * @author: 
 */
public class SinglePoint extends SampledCurve {
/**
 * SinglePoint constructor comment.
 */
public SinglePoint() {
	super();
}
/**
 * SinglePoint constructor comment.
 */
public SinglePoint(Coordinate newPoint) {
	super(newPoint);
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2001 1:42:43 PM)
 * @return cbit.vcell.geometry.Coordinate
 * @param normalizedU double
 */
public Coordinate coordinateFromNormalizedU(double normalizedU) {
	return getBeginningCoordinate();
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param coord cbit.vcell.geometry.Coordinate
 */
public double getDistanceTo(Coordinate coord) {
	double shortestDistance = Double.MAX_VALUE;
	if (getControlPointCount() != 0) {
		shortestDistance = getBeginningCoordinate().distanceTo(coord);
	}
	return shortestDistance;
}
/**
 * getMaxControlPoints method comment.
 */
public int getMaxControlPoints() {
	return 1;
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param u double
 */
public double getX(double u) {
	//Assume u is between 0 and 1
	return getControlPoint(0).getAxisElement(Coordinate.X_AXIS);
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param u double
 */
public double getY(double u) {
	//Assume u is between 0 and 1
	return getControlPoint(0).getAxisElement(Coordinate.Y_AXIS);
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param u double
 */
public double getZ(double u) {
	//Assume u is between 0 and 1
	return getControlPoint(0).getAxisElement(Coordinate.Z_AXIS);
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 5:39:32 PM)
 */
public boolean isClosed() {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 12:54:48 PM)
 */
public int pickSegment(Coordinate pickCoord, double minPickDistance) {
	return Curve.NONE_SELECTED;
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/00 2:11:30 PM)
 */
public int pickSegmentProjected(java.awt.geom.Point2D.Double pickPoint, double minPickDistance, int axis) {
	return Curve.NONE_SELECTED;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 4:23:11 PM)
 */
protected double pickUSampledCurve(Coordinate pickCoord, double minPickDistance) {
	if (getDistanceTo(pickCoord) <= minPickDistance) {
		return 0;
	}
	return Curve.NONE_SELECTED;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 5:36:14 PM)
 */
public void setClosed(boolean arg_bClosed) {
	//Do nothing, we should be false by default
}
}

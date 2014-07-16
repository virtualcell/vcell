/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry;

import java.util.Vector;

import org.vcell.util.Coordinate;

/**
 * Insert the type's description here.
 * Creation date: (7/19/00 1:04:53 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public abstract class ControlPointCurve extends Curve {
	private Vector<Coordinate> controlPoints = new Vector<Coordinate>(); 
	public static final int INFINITE = 0;
/**
 * ControlPointCurve constructor comment.
 */
protected ControlPointCurve() {
	super();
}
/**
 * ControlPointCurve constructor comment.
 */
protected ControlPointCurve(Coordinate[] argControlPoints) {
	super();
	for (int c = 0; c < argControlPoints.length; c += 1) {
		controlPoints.addElement(argControlPoints[c]);
	}
}
/**
 * ControlPointCurve constructor comment.
 */
protected ControlPointCurve(Coordinate argControlPoint) {
	super();
	controlPoints.addElement(argControlPoint);
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 5:38:25 PM)
 * @param offset cbit.vcell.geometry.Coordinate
 */
public void addOffsetControlPoint(Coordinate offset, int controlPointIndex) {
    if (controlPointIndex >= 0 && controlPointIndex < getControlPointCount()) {
        addOffsetControlPointPrivate(offset, controlPointIndex);
        sampledCurveDirty();
    }
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 5:38:25 PM)
 * @param offset cbit.vcell.geometry.Coordinate
 */
private void addOffsetControlPointPrivate(Coordinate offset, int controlPointIndex) {
    Coordinate oldCoordinate = controlPoints.elementAt(controlPointIndex);
    Coordinate newCoordinate = new Coordinate (
	    oldCoordinate.getX() + offset.getX(),
		oldCoordinate.getY() + offset.getY(),
		oldCoordinate.getZ() + offset.getZ());
    controlPoints.set(controlPointIndex, newCoordinate);
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 5:38:25 PM)
 * @param offset cbit.vcell.geometry.Coordinate
 */
protected void addOffsetPrivate(Coordinate offset) {
    for (int c = 0; c < controlPoints.size(); c += 1) {
        addOffsetControlPointPrivate(offset, c);
    }
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 2:23:22 PM)
 * @param newControlPoint cbit.vcell.geometry.Coordinate
 */
public boolean appendControlPoint(Coordinate newControlPoint) {
	if (isControlPointAddable()) {
		controlPoints.addElement(newControlPoint);
		sampledCurveDirty();
		return true;
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 2:23:22 PM)
 * @param newControlPoint cbit.vcell.geometry.Coordinate
 */
public boolean appendControlPointCurve(ControlPointCurve controlPointCurve) {
	if (isControlPointAddable()) {
		controlPoints.addAll(controlPointCurve.controlPoints);
		sampledCurveDirty();
		return true;
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (8/8/00 3:45:00 PM)
 * @return java.lang.Object
 */
public Object clone() {
	ControlPointCurve cpc = (ControlPointCurve) super.clone();
	cpc.controlPoints = new Vector<Coordinate>(controlPoints.size( )) ;
	for(int c = 0;c < controlPoints.size();c++){
		cpc.controlPoints.addElement((Coordinate) controlPoints.elementAt(c).clone( ));
	}
	return cpc;
}
/**
 * compareEqual method comment.
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (!super.compareEqual(obj)) {
		return false;
	}
	if (!(obj instanceof ControlPointCurve)) {
		return false;
	}
	ControlPointCurve cpc = (ControlPointCurve) obj;
	if (controlPoints.size() != cpc.controlPoints.size()) {
		return false;
	}
	for (int c = 0; c < controlPoints.size(); c += 1) {
		Coordinate thisCoord = controlPoints.elementAt(c);
		Coordinate cpcCoord = (Coordinate) cpc.controlPoints.elementAt(c);
		if (!org.vcell.util.Compare.isEqual(thisCoord, cpcCoord)) {
			return false;
		}
	}
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 2:23:22 PM)
 * @param newControlPoint cbit.vcell.geometry.Coordinate
 */
public Coordinate getControlPoint(int getIndex) {
	// cbit.util.Assertion.assert(getIndex < getControlPointCount());
	return (Coordinate)controlPoints.elementAt(getIndex).clone();
	}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 3:12:24 PM)
 */
public int getControlPointCount() {
	return controlPoints.size();
	}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 5:05:46 PM)
 * @return java.util.Vector
 */
public java.util.Vector<Coordinate> getControlPointsVector() {
	return controlPoints;
}
/**
 * Insert the method's description here.
 * Creation date: (7/20/00 12:43:55 PM)
 * @return int
 */
public abstract int getMaxControlPoints();
/**
 * Insert the method's description here.
 * Creation date: (7/20/00 12:43:55 PM)
 * @return int
 */
public abstract int getMinControlPoints();
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 2:23:22 PM)
 * @param newControlPoint cbit.vcell.geometry.Coordinate
 */
public int insertControlPoint(Coordinate newControlPoint, int attachCP) {
	//
	// Insert newControlPoint by finding closest existing control point and determining between which
	// Neighboring control points to insert.  In the case of the first or last control point being the
	// nearest, make angle calculation nearestControlPoint->newControlPoint and nearestControlPoint->neighborControlPoint
	// if angle <= 90 degrees, insert newControlPoint between nearest and neighbor.
	// if angle > 90 degrees insert newcontrolPoint before first(prepend) or after last(append).
	// Return Index into controlPoints where newControlPoint finally was placed.
	//
	if(getControlPointCount() == 0 || getControlPointCount() == 1){
		appendControlPoint(newControlPoint);
		return getControlPointCount()-1;
	}
	if (!isControlPointAddable()) {
		return CurveSelectionInfo.NONE_SELECTED;
	}
	int nearestControlPointIndex;
	if (attachCP != CurveSelectionInfo.NONE_SELECTED) {
		int ap = attachCP;
		if (ap == 0) {
			if (prependControlPoint(newControlPoint)) {
				return 0;
			} else {
				return CurveSelectionInfo.NONE_SELECTED;
			}
		} else
			if (ap == (getControlPointCount() - 1)) {
				if (appendControlPoint(newControlPoint)) {
					return getControlPointCount() - 1;
				} else {
					return CurveSelectionInfo.NONE_SELECTED;
				}
			}
		nearestControlPointIndex = ap;
	} else {
		nearestControlPointIndex = pickControlPoint(newControlPoint,Double.MAX_VALUE);
	}
	int before = nearestControlPointIndex - 1;
	Double beforeAngle = null;
	int after = nearestControlPointIndex + 1;
	Double afterAngle = null;
	//
	if (before >= 0) {
		//beforeDist = new Double(getDistanceTo(new Line(getControlPoint(nearestControlPointIndex), getControlPoint(before)), newControlPoint));
		beforeAngle = new Double(getAngle(getControlPoint(nearestControlPointIndex), newControlPoint, getControlPoint(before)));
	} else { // Nearest was first ControlPoint
		if (getAngle(getControlPoint(nearestControlPointIndex), newControlPoint, getControlPoint(nearestControlPointIndex + 1)) <= 90) {
			//Inserts before index
			insertControlPointPrivate(newControlPoint, nearestControlPointIndex + 1);
			return nearestControlPointIndex + 1;
		} else {
			prependControlPoint(newControlPoint);
			return 0;
		}
	}
	//
	if (after < getControlPointCount()) {
		//afterDist = new Double(getDistanceTo(new Line(getControlPoint(nearestControlPointIndex), getControlPoint(after)), newControlPoint));
		afterAngle = new Double(getAngle(getControlPoint(nearestControlPointIndex), newControlPoint, getControlPoint(after)));
	} else { // Nearest was LastControlPoint
		if (getAngle(getControlPoint(nearestControlPointIndex), newControlPoint, getControlPoint(nearestControlPointIndex - 1)) <= 90) {
			//Inserts before index
			insertControlPointPrivate(newControlPoint, nearestControlPointIndex);
			return nearestControlPointIndex;
		} else {
			appendControlPoint(newControlPoint);
			return getControlPointCount() - 1;
		}
	}
	//
	if (afterAngle.doubleValue() <= beforeAngle.doubleValue()) {
		insertControlPointPrivate(newControlPoint, after);
		return after;
	} else {
		//Inserts before index
		insertControlPointPrivate(newControlPoint, nearestControlPointIndex);
		return nearestControlPointIndex;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 2:23:22 PM)
 * @param newControlPoint cbit.vcell.geometry.Coordinate
 */
private void insertControlPointPrivate(Coordinate newControlPoint, int insertAfterIndex) {
	controlPoints.insertElementAt(newControlPoint, insertAfterIndex);
	sampledCurveDirty();
}
/**
 * Insert the method's description here.
 * Creation date: (10/15/00 5:46:28 PM)
 * @return boolean
 */
public boolean isControlPointAddable() {
	return getMaxControlPoints() == INFINITE || getControlPointCount() < getMaxControlPoints();
}
/**
 * Insert the method's description here.
 * Creation date: (8/8/00 1:50:37 PM)
 * @return boolean
 */
public boolean isValid() {
	return (getControlPointCount() >= getMinControlPoints());
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 4:08:13 PM)
 * @return int
 * @param point java.awt.Point
 */
public int pickControlPoint(Coordinate pickPoint, double minPickDistance) {
	double shortestDistance = Double.MAX_VALUE;
	int controlPointIndex = Curve.NONE_SELECTED;
	int controlPointCount = getControlPointCount();
	for (int i = 0; i < controlPointCount; i++) {
		Coordinate controlPointCoord = controlPoints.elementAt(i);
		double distance = pickPoint.distanceTo(controlPointCoord);
		if (distance <= minPickDistance && distance < shortestDistance) {
			controlPointIndex = i;
			shortestDistance = distance;
		}
	}
	return controlPointIndex;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 2:23:22 PM)
 * @param newControlPoint cbit.vcell.geometry.Coordinate
 */
public boolean prependControlPoint(Coordinate newControlPoint) {
	if (isControlPointAddable()) {
		controlPoints.insertElementAt(newControlPoint, 0);
		sampledCurveDirty();
		return true;
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 2:23:22 PM)
 * @param newControlPoint cbit.vcell.geometry.Coordinate
 */
public void removeControlPoint(int i) {
	if (i >= 0 && i < getControlPointCount()) {
		controlPoints.removeElementAt(i);
		sampledCurveDirty();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 2:23:22 PM)
 * @param newControlPoint cbit.vcell.geometry.Coordinate
 */
public int removeControlPoint(int removeIndex, boolean bDeleteKeyPressed) {
	if (getControlPointCount() != 0 && removeIndex < getControlPointCount() && removeIndex >= 0) {
		controlPoints.removeElementAt(removeIndex);
		sampledCurveDirty();
		if (getControlPointCount() == 0) {
			return CurveSelectionInfo.NONE_SELECTED; // Only return null if there are no more control Points
		}
		if (getControlPointCount() == 1) {
			return 0;
		}
		if (bDeleteKeyPressed) { // Press Delete key
			if (removeIndex >= getControlPointCount()) {
				removeIndex = getControlPointCount() - 1;
			}
		} else { // Press Backspace key (Default)
			removeIndex -= 1;
			if (removeIndex < 0) {
				removeIndex = 0;
			}
		}
		return removeIndex;
	}
	return CurveSelectionInfo.NONE_SELECTED;
}

/**
 * Insert the method's description here.
 * Creation date: (7/17/2003 5:53:12 PM)
 * @param index int
 * @param coord cbit.vcell.geometry.Coordinate
 */
public void setControlPoint(int index, Coordinate coord) {
	controlPoints.set(index,coord);
	sampledCurveDirty();
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/01 3:22:23 PM)
 * @return java.lang.String
 */
public String toString() {
	String className = getClass().getName();
	className = className.substring(className.lastIndexOf('.')+1);
	return  className+"@"+Integer.toHexString(hashCode())+"(controlPoints="+controlPoints+")";
}
}

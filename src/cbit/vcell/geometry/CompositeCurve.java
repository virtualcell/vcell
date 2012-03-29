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

import org.vcell.util.Coordinate;

/**
 * This type was created in VisualAge.
 */
public class CompositeCurve extends Curve {
	java.util.Vector fieldCurves = new java.util.Vector ();
/**
 * CompositeCurve constructor comment.
 */
public CompositeCurve() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void addCurve(Curve curve) {
	fieldCurves.addElement(curve);
}
/**
 * addOffset method comment.
 */
protected void addOffsetPrivate(Coordinate offset) {}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 7:13:55 PM)
 * @return cbit.vcell.geometry.SampledCurve
 */
protected SampledCurve createSampledCurve(int numSamples) {
	return null;
}
/**
 * This method was created in VisualAge.
 */
public Curve getCurve(int i) {
	return ((Curve) fieldCurves.elementAt(i));
}
/**
 * This method was created in VisualAge.
 */
public int getCurveCount() {
	return (fieldCurves.size());
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 6:49:26 PM)
 * @return int
 */
protected int getDefaultNumSamples() {
	return 0;
}
/**
 * getDistanceTo method comment.
 */
public double getDistanceTo(Coordinate coord) {
	// cbit.util.Assertion.assert(getCurveCount() > 0);
	double shortestDistance = getCurve(0).getDistanceTo(coord);
	for (int i = 1; i < getCurveCount (); i++) {
		double distance = getCurve(i).getDistanceTo(coord);
		shortestDistance = Math.min (shortestDistance, distance);
	}
	return (shortestDistance);
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:53:04 PM)
 * @return int
 */
public int getSegmentCount() {
	return 0;
}
/**
 * getX method comment.
 */
public double getX(double u) {
	// cbit.util.Assertion.assert(u >= 0.0 && u <= 1.0);
	u *= getCurveCount ();
	int i = (int) Math.floor (u);
	if (i == getCurveCount()) return (getCurve(i-1).getX(1.0));
	return (getCurve(i).getX(u - Math.floor(u)));
}
/**
 * getY method comment.
 */
public double getY(double u) {
	// cbit.util.Assertion.assert(u >= 0.0 && u <= 1.0);
	u *= getCurveCount ();
	int i = (int) Math.floor (u);
	if (i == getCurveCount()) return (getCurve(i-1).getY(1.0));
	return (getCurve(i).getY(u - Math.floor(u)));
}
/**
 * getZ method comment.
 */
public double getZ(double u) {
	// cbit.util.Assertion.assert(u >= 0.0 && u <= 1.0);
	u *= getCurveCount ();
	int i = (int) Math.floor (u);
	if (i == getCurveCount()) return (getCurve(i-1).getZ(1.0));
	return (getCurve(i).getZ(u - Math.floor(u)));
}
/**
 * This method was created in VisualAge.
 */
public void insertCurve(Curve curve, int i) {
	fieldCurves.insertElementAt(curve, i);
}
/**
 * isValid method comment.
 */
public boolean isValid() {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 12:50:54 PM)
 * @return int
 * @param pickCoord cbit.vcell.geometry.Coordinate
 */
public int pickSegment(Coordinate pickCoord,double minPickDistance) {
	return 0;
}
/**
 * This method was created in VisualAge.
 */
public void removeCurve(int i) {
	fieldCurves.removeElementAt(i);
}
/**
 * This method was created in VisualAge.
 */
public void removeCurve(Curve curve) {
	fieldCurves.removeElement(curve);
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 6:10:29 PM)
 * @param numSamplePoints int
 */
public void setDefaultSampling() {}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:44:34 PM)
 * @param numSamplePoints int
 */
public int setDesiredSampling(int argNumSamplePoints) {
	return 0;
}
}

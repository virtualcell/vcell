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

/**
 * This type was created in VisualAge.
 */
public class CompositeSpline extends CompositeCurve {
/**
 * CompositeSpline constructor comment.
 */
public CompositeSpline() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void addCurve(Curve curve) {
	// cbit.util.Assertion.assert(curve instanceof Spline);
	super.addCurve(curve);
}
/**
 * This method was created in VisualAge.
 */
public void addSpline(Spline spline) {
	addCurve(spline);
}
/**
 * This method was created in VisualAge.
 */
public Spline getSpline(int i) {
	return ((Spline) getCurve(i));
}
/**
 * This method was created in VisualAge.
 */
public int getSplineCount() {
	return (getCurveCount());
}
/**
 * This method was created in VisualAge.
 */
public void insertCurve(Curve curve, int i) {
	// cbit.util.Assertion.assert(curve instanceof Spline);
	super.insertCurve(curve, i);
}
/**
 * This method was created in VisualAge.
 */
public void insertSpline(Spline spline, int i) {
	insertCurve(spline, i);
}
/**
 * This method was created in VisualAge.
 */
public void removeSpline (int i) {
	removeCurve (i);
}
/**
 * This method was created in VisualAge.
 */
public void removeSpline (Spline spline) {
	removeCurve (spline);
}
}

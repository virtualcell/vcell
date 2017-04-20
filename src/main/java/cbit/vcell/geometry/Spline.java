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
public class Spline extends ControlPointCurve {
/**
 * Insert the method's description here.
 * Creation date: (7/19/00 12:55:31 PM)
 */
public Spline() {}
/**
 * Spline constructor comment.
 */
public Spline(Coordinate[] argControlPoints) {
	super(argControlPoints);
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 1:28:23 PM)
 * @return int
 * @param sampledsegment int
 */
public int convertSampledSegmentToSplineSegment(int sampledSegment) {
    int sampledSegmentsPerSplineSegment = (getNumSamplePoints() - 1) / getSegmentCount();
    sampledSegment /= sampledSegmentsPerSplineSegment;
    return sampledSegment;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 7:16:24 PM)
 */
protected SampledCurve createSampledCurve(int numSamples) {
    return new SampledCurve(this, numSamples);
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 6:50:14 PM)
 */
protected int getDefaultNumSamples() {
	return (getSegmentCount() * 4) + 1;
}
/**
 * getMaxControlPoints method comment.
 */
public int getMaxControlPoints() {
	return ControlPointCurve.INFINITE;
}
/**
 * getMinControlPoints method comment.
 */
public int getMinControlPoints() {
	return 4;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:54:31 PM)
 */
public int getSegmentCount() {
    if (!isValid()) {
        return 0;
    } else {
        return (isClosed()) ? getControlPointCount() : (getControlPointCount() - 3);
    }
}
/**
 * getX method comment.
 */
public double getX(double u) {
	int segmentIndex = getNonLengthNormalizedSegment(u);
	int numSegments = getSegmentCount();
	int numCP = getControlPointCount();
	//
	// calculates u (and v) for the segment indexed by 'segmentIndex'
	// now u = [0,1] on this segment 'segmentIndex'.
	//
	u = u * numSegments - segmentIndex;
	double v = 1.0 - u;
	//
	// evaluate X on segment 'segmentIndex' which is formed by 4 control points.
	// control point 0 index = (segmentIndex + 0) % numCP 
	// control point 1 index = (segmentIndex + 1) % numCP 
	// control point 2 index = (segmentIndex + 2) % numCP 
	// control point 3 index = (segmentIndex + 3) % numCP
	//
	// where numCP wraps a closed curve around
	//
	return ((v * v * v * getControlPoint(segmentIndex % numCP).getX() + (3.0 * u * u * u - 6.0 * u * u + 4.0) * getControlPoint((segmentIndex + 1) % numCP).getX() + (-3.0 * u * u * u + 3.0 * u * u + 3.0 * u + 1.0) * getControlPoint((segmentIndex + 2) % numCP).getX() + u * u * u * getControlPoint((segmentIndex + 3) % numCP).getX()) / 6.0);
}
/**
 * getX method comment.
 */
public double getY(double u) {
	int segmentIndex = getNonLengthNormalizedSegment(u);
	int numSegments = getSegmentCount();
	int numCP = getControlPointCount();
	//
	// calculates u (and v) for the segment indexed by 'segmentIndex'
	// now u = [0,1] on this segment 'segmentIndex'.
	//
	u = u * numSegments - segmentIndex;
	double v = 1.0 - u;
	//
	// evaluate Y on segment 'segmentIndex' which is formed by 4 control points.
	// control point 0 index = (segmentIndex + 0) % numCP 
	// control point 1 index = (segmentIndex + 1) % numCP 
	// control point 2 index = (segmentIndex + 2) % numCP 
	// control point 3 index = (segmentIndex + 3) % numCP
	//
	// where numCP wraps a closed curve around
	//
	return ((v * v * v * getControlPoint(segmentIndex % numCP).getY() + (3.0 * u * u * u - 6.0 * u * u + 4.0) * getControlPoint((segmentIndex + 1) % numCP).getY() + (-3.0 * u * u * u + 3.0 * u * u + 3.0 * u + 1.0) * getControlPoint((segmentIndex + 2) % numCP).getY() + u * u * u * getControlPoint((segmentIndex + 3) % numCP).getY()) / 6.0);
}
/**
 * getX method comment.
 */
public double getZ(double u) {
	int segmentIndex = getNonLengthNormalizedSegment(u);
	int numSegments = getSegmentCount();
	int numCP = getControlPointCount();
	//
	// calculates u (and v) for the segment indexed by 'segmentIndex'
	// now u = [0,1] on this segment 'segmentIndex'.
	//
	u = u * numSegments - segmentIndex;
	double v = 1.0 - u;
	//
	// evaluate Z on segment 'segmentIndex' which is formed by 4 control points.
	// control point 0 index = (segmentIndex + 0) % numCP 
	// control point 1 index = (segmentIndex + 1) % numCP 
	// control point 2 index = (segmentIndex + 2) % numCP 
	// control point 3 index = (segmentIndex + 3) % numCP
	//
	// where numCP wraps a closed curve around
	//
	return ((v * v * v * getControlPoint(segmentIndex % numCP).getZ() + (3.0 * u * u * u - 6.0 * u * u + 4.0) * getControlPoint((segmentIndex + 1) % numCP).getZ() + (-3.0 * u * u * u + 3.0 * u * u + 3.0 * u + 1.0) * getControlPoint((segmentIndex + 2) % numCP).getZ() + u * u * u * getControlPoint((segmentIndex + 3) % numCP).getZ()) / 6.0);
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 1:01:52 PM)
 */
public int pickSegment(Coordinate pickCoord, double minPickDistance) {
    int segment = getSampledCurve().pickSegment(pickCoord, minPickDistance);
    if (segment != Curve.NONE_SELECTED) {
        segment = convertSampledSegmentToSplineSegment(segment);
    }
    return segment;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:49:46 PM)
 */
public int setDesiredSampling(int argNumSamplePoints) {
    if (argNumSamplePoints == 0) {
        argNumSamplePoints = getSegmentCount() + 1;
    } else if ((argNumSamplePoints - 1) % getSegmentCount() != 0) {
        argNumSamplePoints = (((argNumSamplePoints / getSegmentCount()) + 1) * getSegmentCount()) + 1;
    }
    //
    setNumSamplePoints(argNumSamplePoints);
    return getNumSamplePoints();
}
}

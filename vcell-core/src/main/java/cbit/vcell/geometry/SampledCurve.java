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
public class SampledCurve extends ControlPointCurve {
/**
 * SampledCurve constructor comment.
 */
public SampledCurve() {
	super();
}
/**
 * SampledCurve constructor comment.
 */
public SampledCurve(Coordinate[] argControlPoints) {
	super(argControlPoints);
}
/**
 * SampledCurve constructor comment.
 */
protected SampledCurve(Coordinate argControlPoint) {
	super(argControlPoint);
}
/**
 * SampledCurve constructor comment.
 */
public SampledCurve(Curve sampleThisCurve, int samplePointCount) {
	super();
	for (int c = 0; c < samplePointCount; c += 1) {
		double u = (double) c / (double) (samplePointCount - 1);
		appendControlPoint(new Coordinate(sampleThisCurve.getX(u), sampleThisCurve.getY(u), sampleThisCurve.getZ(u)));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2001 1:42:43 PM)
 * @return cbit.vcell.geometry.Coordinate
 * @param normalizedU double
 */
public Coordinate coordinateFromNormalizedU(double normalizedU) {
	//
	if(normalizedU < 0 || normalizedU > 1.0){
		throw new RuntimeException("coordinateFromNormalizedU: normalizedU must be between 0 and 1");
	}
	//
	Coordinate result = null;
	int segmentCount = getSegmentCount();
	int controlPointCount = getControlPointCount();
	Coordinate p0 = null;
	Coordinate p1 = null;
	//
	double[] segmentLength = new double[segmentCount];
	double[] accumLength = new double[segmentCount];
	double totalLength = 0;
	for (int i = 0; i < segmentCount; i += 1) {
		p0 = getControlPoint(i);
		p1 = getControlPoint((i + 1) % controlPointCount);
		segmentLength[i] = p0.distanceTo(p1);
		accumLength[i] = totalLength;
		totalLength += segmentLength[i];
	}
	double targetLength = normalizedU * totalLength;
	//
	for (int i = 0; i < segmentCount; i += 1) {
		if (targetLength >= accumLength[i] && targetLength <= (accumLength[i] + segmentLength[i])) {
			double segmentU = -1.0;
			if(segmentLength[i] == 0 && (i+1) == segmentCount){
				//Fix problem with duplicate points at end have 0 segment length and create NaN
				segmentU = 1.0;
			}else{
				segmentU = (targetLength-accumLength[i])/segmentLength[i];
			}
			p0 = getControlPoint(i);
			p1 = getControlPoint((i + 1) % controlPointCount);
			double uX = p0.getX() + ((p1.getX() - p0.getX()) * segmentU);
			double uY = p0.getY() + ((p1.getY() - p0.getY()) * segmentU);
			double uZ = p0.getZ() + ((p1.getZ() - p0.getZ()) * segmentU);
			result = new Coordinate(uX,uY,uZ);
		}
	}
	return result;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 7:15:24 PM)
 */
protected SampledCurve createSampledCurve(int numSamples) {
	return this;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2004 6:55:12 PM)
 * @return cbit.vcell.geometry.Coordinate[]
 * @param sgementIndex int
 */
public Coordinate[] getControlPointsForSegment(int segmentIndex) {
	
	int controlPointCount = getControlPointCount();
	Coordinate p0 = null;
	Coordinate p1 = null;
	p0 = getControlPoint(segmentIndex);
	p1 = getControlPoint((segmentIndex + 1) % controlPointCount);

	return new Coordinate[] {p0,p1};
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 6:49:46 PM)
 */
protected int getDefaultNumSamples() {
	return getControlPointCount();
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param coord cbit.vcell.geometry.Coordinate
 */
protected double getDistanceToSampledCurve(Coordinate coord) {
	int segmentCount = getSegmentCount();
	int controlPointCount = getControlPointCount();
	double shortestDistance = Double.MAX_VALUE;
	Coordinate p0 = null;
	Coordinate p1 = null;
	for (int i = 0; i < segmentCount; i += 1) {
		p0 = getControlPoint(i);
		p1 = getControlPoint((i + 1) % controlPointCount);
		double distance = getDistanceToLine(p0, p1, coord);
		if (distance < shortestDistance) {
			shortestDistance = distance;
		}
	}
	return shortestDistance;
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
	return 1;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:53:26 PM)
 */
public int getSegmentCount() {
    if (!isValid()) {
        return 0;
    } else {
        return (isClosed() ? getControlPointCount() : getControlPointCount() - 1);
    }
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 3:23:42 PM)
 * @return double
 */
public double getSegmentSpatialLength(int segmentIndex) {

	return getControlPoint(segmentIndex).distanceTo(getControlPoint((segmentIndex + 1) % getControlPointCount()));
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 3:23:42 PM)
 * @return double
 */
public double getSpatialLength() {
	int segmentCount = getSegmentCount();
	//int controlPointCount = getControlPointCount();
	//Coordinate p0 = null;
	//Coordinate p1 = null;
	double spatialLength = 0.0;
	for (int i = 0; i < segmentCount; i += 1) {
		//p0 = getControlPoint(i);
		//p1 = getControlPoint((i + 1) % controlPointCount);
		//spatialLength+= p0.distanceTo(p1);
		spatialLength+= getSegmentSpatialLength(i);
	}
	return spatialLength;
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param u double
 */
public double getX(double u) {
	return coordinateFromNormalizedU(u).getX();
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param u double
 */
public double getY(double u) {
	return coordinateFromNormalizedU(u).getY();
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param u double
 */
public double getZ(double u) {
	return coordinateFromNormalizedU(u).getZ();
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 7:00:19 PM)
 */
protected boolean isSampledCurveInside(org.vcell.util.Origin origin, org.vcell.util.Extent extent, Coordinate delta) {
	//
	// This util. checks sample of this curve that are straight line segments and that if their endpoints
	// are inside the the whole thing is inside.
	//
	int controlPointCount = getControlPointCount();
	java.util.Vector controlPointsV = getControlPointsVector();
	for (int c = 0; c < controlPointCount; c += 1) {
		Coordinate curveCoord = (Coordinate) controlPointsV.elementAt(c);
		if (!Coordinate.isCoordinateInBounds(curveCoord, origin, extent, delta)) {
			return false;
		}
	}
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 12:54:48 PM)
 */
public int pickSegment(Coordinate pickCoord, double minPickDistance) {
	int segmentCount = getSegmentCount();
	int controlPointCount = getControlPointCount();
	double shortestDistance = Double.MAX_VALUE;
	int closestSegment = Curve.NONE_SELECTED;
	Coordinate p0 = null;
	Coordinate p1 = null;
	for (int i = 0; i < segmentCount; i += 1) {
		p0 = getControlPoint(i);
		p1 = getControlPoint((i + 1) % controlPointCount);
		double distance = getDistanceToLine(p0, p1, pickCoord);
		if (distance <= minPickDistance && distance < shortestDistance) {
			shortestDistance = distance;
			closestSegment = i;
		}
	}
	return closestSegment;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 4:23:11 PM)
 */
protected double pickUSampledCurve(Coordinate pickCoord, double minPickDistance) {
    int segmentCount = getSegmentCount();
    int controlPointCount = getControlPointCount();
    double shortestDistance = Double.MAX_VALUE;
    int closestSegment = Curve.NONE_SELECTED;
    double closestSegmentU = Curve.NONE_SELECTED;
    Coordinate p0 = null;
    Coordinate p1 = null;
    for (int i = 0; i < segmentCount; i += 1) {
        p0 = getControlPoint(i);
        p1 = getControlPoint((i + 1) % controlPointCount);
        double u = calculateUOfV1AlongV2(p0, pickCoord, p1);
        if (u >= 0.0 && u <= 1.0) {
            double uX = p0.getX() + ((p1.getX() - p0.getX()) * u);
            double uY = p0.getY() + ((p1.getY() - p0.getY()) * u);
            double uZ = p0.getZ() + ((p1.getZ() - p0.getZ()) * u);
            double distance = pickCoord.distanceTo(uX, uY, uZ);
            if (distance <= minPickDistance && distance < shortestDistance) {
                shortestDistance = distance;
                closestSegment = i;
                closestSegmentU = u;
            }
        }
    }
    if (closestSegment != Curve.NONE_SELECTED) {
	    //Calculate Non-LengthNormalized U
        double finalU = (closestSegment + closestSegmentU) / getSegmentCount();
        return finalU;
    } else {
        return Curve.NONE_SELECTED;
    }
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:48:16 PM)
 */
public int setDesiredSampling(int argNumSamplePoints) {
	setNumSamplePoints(getDefaultNumSamples());
	return getNumSamplePoints();
}
}

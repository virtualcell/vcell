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
 *  For now, addCurve () and removeCurve () are not defined
 *  in the composite pattern classes...rather, they are left
 *  for the subclasses of CompositeCurve.  This is a design
 *  decision reflecting immutability from the standpoint of
 *  the user.
 */
public abstract class Curve implements org.vcell.util.Matchable, java.io.Serializable, Cloneable {
	//Leave false by default
	private boolean bClosed = false;
	//
	public static final int NONE_SELECTED = -1;
	private static final int DEFAULT_NUM_SAMPLES_FLAG = 0;
	private transient int fieldNumSamplePoints = DEFAULT_NUM_SAMPLES_FLAG;
	private transient SampledCurve sampledCurve = null;
	private transient int sampledCurveID = 0;
	private String description = null;

/**
 * Curve constructor comment.
 */
protected Curve() {
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/00 6:24:50 PM)
 * @param offset cbit.vcell.geometry.Coordinate
 */
public void addOffset(Coordinate offset) {
    addOffsetPrivate(offset);
    sampledCurveDirty();
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/00 6:24:50 PM)
 * @param offset cbit.vcell.geometry.Coordinate
 */
protected abstract void addOffsetPrivate(Coordinate offset);
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 2:50:47 PM)
 * @return double
 * @param vertex cbit.vcell.geometry.Coordinate
 * @param v1 cbit.vcell.geometry.Coordinate
 * @param v2 cbit.vcell.geometry.Coordinate
 */
public static final double calculateUOfV1AlongV2(double vertexX, double vertexY, double vertexZ, double v1X, double v1Y, double v1Z, double v2X, double v2Y, double v2Z) {
	//Calulate U (v2 Length normalized) of vertex->v1 Projected Along vertex->v2
	double v2XLength = (v2X - vertexX);
	double v2YLength = (v2Y - vertexY);
	double v2ZLength = (v2Z - vertexZ);
	//DotProduct v1*v2
	double dotProduct = (v1X - vertexX) * v2XLength + (v1Y - vertexY) * v2YLength + (v1Z - vertexZ) * v2ZLength;
	//Magnitude v2 squared, don't take square root so that u return is normalized relative to vertex->v2
	double magnitudeSquared = v2XLength * v2XLength + v2YLength * v2YLength + v2ZLength * v2ZLength;
	return dotProduct / magnitudeSquared;

}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 2:50:47 PM)
 * @return double
 * @param vertex cbit.vcell.geometry.Coordinate
 * @param v1 cbit.vcell.geometry.Coordinate
 * @param v2 cbit.vcell.geometry.Coordinate
 */
public static final double calculateUOfV1AlongV2(Coordinate vertex, Coordinate v1, Coordinate v2) {
	return calculateUOfV1AlongV2(vertex.getX(), vertex.getY(), vertex.getZ(), v1.getX(), v1.getY(), v1.getZ(), v2.getX(), v2.getY(), v2.getZ());
	/*
	//Calulate U (v2 Length normalized) of vertex->v1 Projected Along vertex->v2
	double v2XLength = (v2.getX() - vertex.getX());
	double v2YLength = (v2.getY() - vertex.getY());
	double v2ZLength = (v2.getZ() - vertex.getZ());
	//DotProduct v1*v2
	double dotProduct = (v1.getX() - vertex.getX())*v2XLength + (v1.getY() - vertex.getY())*v2YLength + (v1.getZ() - vertex.getZ())*v2ZLength;
	//Magnitude v2 squared, don't take square root so that u return is normalized relative to vertex->v2
	double magnitudeSquared = v2XLength*v2XLength + v2YLength*v2YLength +v2ZLength*v2ZLength;
	return dotProduct/magnitudeSquared;
	*/

}
/**
 * Insert the method's description here.
 * Creation date: (8/8/00 3:45:00 PM)
 * @return java.lang.Object
 */
public Object clone() {
	try {
		Curve c = (Curve)super.clone();
		c.setClosed(this.isClosed());
		return c;
	} catch (CloneNotSupportedException e) {
		// this shouldn't happen, since we are Cloneable
		throw new InternalError();
	}
}
/**
 * compareEqual method comment.
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj == null) {
		return false;
	}
	if (!(obj instanceof Curve)) {
		return false;
	}
	Curve curve = (Curve) obj;
	return (isClosed() == curve.isClosed());
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:28:22 PM)
 * @return cbit.vcell.geometry.SampledCurve
 */
protected abstract SampledCurve createSampledCurve(int numSamples);
/*
{
	if (sampledCurve == null) {
		int sampling = getNumSamplePoints();
		if (sampling == DEFAULT_NUM_SAMPLES_FLAG) {
			sampling = getDefaultNumSamples();
		}
		sampledCurve = new SampledCurve(this, sampling);
	}
	return sampledCurve;
}
*/
/**
 * Insert the method's description here.
 * Creation date: (7/20/00 1:09:20 PM)
 * @return double
 * @param vertex cbit.vcell.geometry.Coordinate
 * @param p1 cbit.vcell.geometry.Coordinate
 * @param p2 cbit.vcell.geometry.Coordinate
 */
public static final double getAngle(Coordinate vertex, Coordinate p1, Coordinate p2) {
	//Vector vertex->p1
	Coordinate v1 = new Coordinate(p1.getX()-vertex.getX(),p1.getY()-vertex.getY(),p1.getZ()-vertex.getZ());
	//Length v1
	double v1Length = Math.sqrt(v1.getX()*v1.getX()+v1.getY()*v1.getY()+v1.getZ()*v1.getZ());
	//Vector vertex->p2
	Coordinate v2 = new Coordinate(p2.getX()-vertex.getX(),p2.getY()-vertex.getY(),p2.getZ()-vertex.getZ());
	//Length v2
	double v2Length = Math.sqrt(v2.getX()*v2.getX()+v2.getY()*v2.getY()+v2.getZ()*v2.getZ());
	//Dot Product v1 and v2
	double dotP = 	(v1.getX()*v2.getX())+(v1.getY()*v2.getY())+(v1.getZ()*v2.getZ());
	// positive Angle 0->PI
	double angle = Math.acos(dotP/(v1Length*v2Length));
	//Angle in degrees
	return angle*180/Math.PI;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.Coordinate
 */
public Coordinate getBeginningCoordinate() {
	return getCoordinate(0.0);
}
/**
 * Insert the method's description here.
 * Creation date: (7/20/00 1:53:40 PM)
 * @return cbit.vcell.geometry.Coordinate
 * @param u double
 */
public Coordinate getCoordinate(double u) {
	return (new Coordinate (getX(u), getY(u), getZ(u)));
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 6:49:00 PM)
 * @return int
 */
protected abstract int getDefaultNumSamples();
/**
 * This method was created in VisualAge.
 * @param coord cbit.vcell.geometry.Coordinate
 */
public double getDistanceTo(Coordinate coord) {
	return getSampledCurve().getDistanceToSampledCurve(coord);
}
/**
 *  Helper class for subclasses to calculate getDistanceTo ()...
 */
public static final double getDistanceToLine(Coordinate b, Coordinate e, Coordinate c) {
	//
	double bX = b.getX(), bY = b.getY(), bZ = b.getZ();
	double cX = c.getX(), cY = c.getY(), cZ = c.getZ();
	double eX = e.getX(), eY = e.getY(), eZ = e.getZ();
	//
	double u = -1;
	if (!b.compareEqual(c)) {
		u = calculateUOfV1AlongV2(b,c,e);
	}
	if (u < 0.0) {
	    //distance from beginning endpoint
		return Math.sqrt((bX - cX) * (bX - cX) + (bY - cY) * (bY - cY) + (bZ - cZ) * (bZ - cZ));
	} else if (u > 1.0) {
	    //distance from ending endpoint
		return Math.sqrt((eX - cX) * (eX - cX) + (eY - cY) * (eY - cY) + (eZ - cZ) * (eZ - cZ));
	} else {
	    //distance from point at u on segment b->c
		return c.distanceTo((bX + (eX - bX) * u), (bY + (eY - bY) * u), (bZ + (eZ - bZ) * u));

	}
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.Coordinate
 */
public Coordinate getEndingCoordinate() {
	return getCoordinate(1.0);
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:56:46 PM)
 * @return int
 * @param u double
 */
public double getNonLengthNormalizedBeginU(int segmentIndex) {
    double u = (double)segmentIndex/(double)getSegmentCount();
    return u;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:56:46 PM)
 * @return int
 * @param u double
 */
public double getNonLengthNormalizedEndU(int segmentIndex) {
    double u = getNonLengthNormalizedBeginU(segmentIndex+1);
	return net.sourceforge.interval.ia_math.RMath.prevfp(u);
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:56:46 PM)
 * @return int
 * @param u double
 */
public int getNonLengthNormalizedSegment(double u) {
    int n = getSegmentCount();
    //
    // get close to the answser (give or take roundoff).
    //
    int i = (int) (u * n);
    if (u == 1.0) {
        i = n - 1;
    }
    if (u < getNonLengthNormalizedBeginU(i)){
	    return i-1;
    }else if (u > getNonLengthNormalizedEndU(i)){
	    return i+1;
    }else {
	    return i;
    }
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 6:07:04 PM)
 */
public int getNumSamplePoints() {
	return fieldNumSamplePoints;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:28:22 PM)
 * @return cbit.vcell.geometry.SampledCurve
 */
public SampledCurve getSampledCurve() {
	if (sampledCurve == null) {
		int sampling = getNumSamplePoints();
		if (sampling == DEFAULT_NUM_SAMPLES_FLAG) {
			sampling = getDefaultNumSamples();
		}
		sampledCurve = createSampledCurve(sampling);
		sampledCurve.setClosed(isClosed());
		sampledCurveID+= 1;
	}
	return sampledCurve;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:28:22 PM)
 * @return cbit.vcell.geometry.SampledCurve
 */
public int getSampledCurveID() {
	if (sampledCurve == null) {
		throw new RuntimeException("getSampledCurveID invalid when sampledCurve is dirty, call getSampledCurve first");
	}
	return sampledCurveID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:52:28 PM)
 * @return int
 */
public abstract int getSegmentCount();
/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 3:21:36 PM)
 * @return double
 */
public double getSpatialLength() {
	return getSampledCurve().getSpatialLength();
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param u double
 */
public abstract double getX(double u);
/**
 * This method was created in VisualAge.
 * @return double
 * @param u double
 */
public abstract double getY(double u);
/**
 * This method was created in VisualAge.
 * @return double
 * @param u double
 */
public abstract double getZ(double u);
/**
 * Insert the method's description here.
 * Creation date: (7/31/00 2:44:13 PM)
 * @param bClosed boolean
 */
public boolean isClosed() {
    return this.bClosed;
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 10:43:56 AM)
 * @return boolean
 * @param c0 cbit.vcell.geometry.Coordinate
 * @param c1 cbit.vcell.geometry.Coordinate
 * @param delta cbit.vcell.geometry.Coordinate
 */
public boolean isInside(org.vcell.util.Origin origin, org.vcell.util.Extent extent, Coordinate delta) {
	//
	// This util. checks sample of this curve that are straight line segments and that if their endpoints
	// are inside the the whole thing is inside.
	//
	return getSampledCurve().isSampledCurveInside(origin, extent, delta);
}
/**
 * Insert the method's description here.
 * Creation date: (8/8/00 1:49:32 PM)
 * @return boolean
 */
public abstract boolean isValid();
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 12:50:48 PM)
 * @return int
 * @param pickCoord cbit.vcell.geometry.Coordinate
 */
public abstract int pickSegment(Coordinate pickCoord,double minPickDistance);
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 12:50:48 PM)
 * @return int
 * @param pickCoord cbit.vcell.geometry.Coordinate
 */
public double pickU(Coordinate pickCoord,double minPickDistance){
	return getSampledCurve().pickUSampledCurve(pickCoord,minPickDistance);
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:40:18 PM)
 */
protected void sampledCurveDirty() {
	sampledCurve = null;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/00 2:44:13 PM)
 * @param bClosed boolean
 */
public void setClosed(boolean arg_bClosed) {
    if (this.bClosed != arg_bClosed) {
        this.bClosed = arg_bClosed;
        sampledCurveDirty();
    }
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:31:07 PM)
 * @param numSamplePoints int
 */
public void setDefaultSampling(){
	setNumSamplePoints(DEFAULT_NUM_SAMPLES_FLAG);
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:31:07 PM)
 * @param numSamplePoints int
 */
public abstract int setDesiredSampling(int numSamplePoints);
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 6:07:04 PM)
 */
protected void setNumSamplePoints(int numSamplePoints) {
    if (fieldNumSamplePoints != numSamplePoints) {
        fieldNumSamplePoints = numSamplePoints;
        sampledCurveDirty();
    }
}

public String getDescription() {
	return description;
}
public void setDescription(String description) {
	this.description = description;
}

}

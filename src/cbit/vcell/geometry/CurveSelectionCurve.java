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
 * Insert the type's description here.
 * Creation date: (6/19/2003 8:39:35 PM)
 * @author: Jim Schaff
 */
public class CurveSelectionCurve extends SampledCurve {

	private SampledCurve traceSource = null;
	private CurveSelectionInfo tracedCSI = null;
/**
 * TracedCurve constructor comment.
 */
public CurveSelectionCurve(SampledCurve argTraceSource) {
	super();
	this.traceSource = argTraceSource;
	tracedCSI = new CurveSelectionInfo(this.traceSource);
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 5:38:25 PM)
 * @param offset cbit.vcell.geometry.Coordinate
 */
public void addOffsetControlPoint(Coordinate offset, int controlPointIndex) {
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 5:38:25 PM)
 * @param offset cbit.vcell.geometry.Coordinate
 */
private void addOffsetControlPointPrivate(Coordinate offset, int controlPointIndex) {
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 5:38:25 PM)
 * @param offset cbit.vcell.geometry.Coordinate
 */
protected void addOffsetPrivate(Coordinate offset) {
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 2:23:22 PM)
 * @param newControlPoint cbit.vcell.geometry.Coordinate
 */
public boolean appendControlPoint(Coordinate newControlPoint) {
	tracedCSI.setSegmentExtended(tracedCSI.getCurve().pickSegment(newControlPoint,Double.MAX_VALUE));
	if(getControlPointCount() < 2){
		super.appendControlPoint(newControlPoint);
	}else{
		super.removeControlPoint(1);
		super.appendControlPoint(newControlPoint);
	}
	sampledCurveDirty();
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 2:23:22 PM)
 * @param newControlPoint cbit.vcell.geometry.Coordinate
 */
public boolean appendControlPointCurve(ControlPointCurve controlPointCurve) {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 7:15:24 PM)
 */
protected SampledCurve createSampledCurve(int numSamples) {
	//
	//Always assumes trace is "open" curve
	int[] indexes = tracedCSI.getSegmentsInSelectionOrder();
	Coordinate[] csiControlPoints = new Coordinate[indexes.length+1];
	try{
		for(int i =0;i < indexes.length;i+= 1){
			int baseIndex = (tracedCSI.getDirectionNegative()?(indexes[i]+1) % traceSource.getControlPointCount():indexes[i]);
			int nextIndex = (baseIndex + (tracedCSI.getDirectionNegative()?-1:1)) % traceSource.getControlPointCount();
			if(nextIndex < 0){nextIndex = traceSource.getControlPointCount()-1;}
			csiControlPoints[i] = traceSource.getControlPoint(baseIndex);
			csiControlPoints[i+1] = traceSource.getControlPoint(nextIndex);
		}
	}catch(Exception e){
		return new Line(getBeginningCoordinate(),getEndingCoordinate());
	}
	//
	System.out.println();
	//
	return new SampledCurve(csiControlPoints);
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 6:49:46 PM)
 */
protected int getDefaultNumSamples() {
	return (tracedCSI.getSegmentCount() != 0 ? tracedCSI.getSegmentCount()+1:0);
}
/**
 * getMaxControlPoints method comment.
 */
public int getMaxControlPoints() {
	return 2;
}
/**
 * getMinControlPoints method comment.
 */
public int getMinControlPoints() {
	return 2;
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/2003 6:55:53 PM)
 * @return cbit.vcell.geometry.SampledCurve
 */
public CurveSelectionInfo getSourceCurveSelectionInfo() {
	return tracedCSI;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 2:23:22 PM)
 * @param newControlPoint cbit.vcell.geometry.Coordinate
 */
public int insertControlPoint(Coordinate newControlPoint, int attachCP) {
	appendControlPoint(newControlPoint);
	return getControlPointCount()-1;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 2:23:22 PM)
 * @param newControlPoint cbit.vcell.geometry.Coordinate
 */
private void insertControlPointPrivate(Coordinate newControlPoint, int insertAfterIndex) {
	//throw new Error("insertControlPointPrivate Should never be called on TracedCurve");
}
}

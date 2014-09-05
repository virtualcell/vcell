/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;

import org.vcell.util.Coordinate;
/**
 * Insert the type's description here.
 * Creation date: (7/18/2001 2:39:54 PM)
 * @author: Frank Morgan
 */
public class SpatialSelectionMembrane extends SpatialSelection {
	private int[] fieldSampledDataIndexes = null;
	private cbit.vcell.geometry.SampledCurve selectionSource = null;
/**
 * SpatialSelectionGeometry constructor comment.
 * @param argCurveSelectionInfo cbit.vcell.geometry.CurveSelectionInfo
 * @param argVarType cbit.vcell.simdata.VariableType
 * @param argMesh cbit.vcell.solvers.CartesianMesh
 * @param sampledDataIndexes int[]
 * @param selectionKind int
 */
public SpatialSelectionMembrane(
    cbit.vcell.geometry.CurveSelectionInfo argCurveSelectionInfo,
    cbit.vcell.math.VariableType argVarType,
    cbit.vcell.solvers.CartesianMesh argMesh,
    int[] sampledDataIndexes,
    cbit.vcell.geometry.SampledCurve argSelectionSource) {
    super(argCurveSelectionInfo, argVarType, argMesh);

    selectionSource = argSelectionSource;
    if (argVarType.equals(cbit.vcell.math.VariableType.MEMBRANE)) {
        fieldSampledDataIndexes = sampledDataIndexes;
    } else
        if (argVarType.equals(cbit.vcell.math.VariableType.MEMBRANE_REGION)) {
            fieldSampledDataIndexes = new int[sampledDataIndexes.length];
            for (int i = 0; i < sampledDataIndexes.length; i++) {
                fieldSampledDataIndexes[i] =
                    argMesh.getMembraneRegionIndex(sampledDataIndexes[i]);
            }
        } else {
            throw new IllegalArgumentException(
                "unexpected value for VariableType = " + argVarType.toString());
        }
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/01 4:20:57 PM)
 * @return int
 * @param u double
 */
public int getIndex(double selectionU) {
	int index = -1;

	//
	// here selectionU goes from 0..1 traversing the entire selection, u is the actual curve parameterization.
	//
	double selectionTransformedU = getCurveSelectionInfo().getCurveUfromSelectionU(selectionU);

	int curveSegment = getCurveSelectionInfo().getCurve().getNonLengthNormalizedSegment(selectionTransformedU);
	return fieldSampledDataIndexes[curveSegment];
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/01 5:59:31 PM)
 * @return int[]
 */
public SSHelper getIndexSamples() {

	int[] membraneSegmentSelectionIndexes = getCurveSelectionInfo().getSegmentsInSelectionOrder();
	cbit.vcell.geometry.SampledCurve sampledCurve = (cbit.vcell.geometry.SampledCurve)getCurveSelectionInfo().getCurve();
	
	if(membraneSegmentSelectionIndexes.length == 0){
		return null;
	}
	if(membraneSegmentSelectionIndexes.length == 1){
		org.vcell.util.Coordinate[] twoCP = sampledCurve.getControlPointsForSegment(membraneSegmentSelectionIndexes[0]);
		return new SSHelper(
			new Coordinate[] {twoCP[0],twoCP[1]},
			new int[] {fieldSampledDataIndexes[membraneSegmentSelectionIndexes[0]],fieldSampledDataIndexes[membraneSegmentSelectionIndexes[0]]},
			getVariableType(),null);
	}
	
	int[] indexes = new int[membraneSegmentSelectionIndexes.length+1];
	Coordinate[][] segCPArr= new Coordinate[membraneSegmentSelectionIndexes.length][];
	Coordinate[] wcArr = new Coordinate[indexes.length];

	for(int i =0;i < membraneSegmentSelectionIndexes.length;i+= 1){
		segCPArr[i] = sampledCurve.getControlPointsForSegment(membraneSegmentSelectionIndexes[i]);
	}
	
	for(int i =0;i < membraneSegmentSelectionIndexes.length;i+= 1){
		org.vcell.util.Coordinate[] twoCP = segCPArr[i];
		Coordinate firstSelDirection = null;
		Coordinate secondSelDirection = null;
		
		if (i  != (membraneSegmentSelectionIndexes.length-1)){
			org.vcell.util.Coordinate[] twoCPNext = segCPArr[i+1];
			if(twoCP[0].compareEqual(twoCPNext[0]) || twoCP[0].compareEqual(twoCPNext[1])){
				firstSelDirection = twoCP[1];
				secondSelDirection = twoCP[0];
			}else{
				firstSelDirection = twoCP[0];
				secondSelDirection = twoCP[1];
			}
		}else{
			org.vcell.util.Coordinate[] twoCPPrev = segCPArr[i-1];
			if(twoCP[0].compareEqual(twoCPPrev[0]) || twoCP[0].compareEqual(twoCPPrev[1])){
				firstSelDirection = twoCP[0];
				secondSelDirection = twoCP[1];
			}else{
				firstSelDirection = twoCP[1];
				secondSelDirection = twoCP[0];
			}
		}


		indexes[i] = fieldSampledDataIndexes[membraneSegmentSelectionIndexes[i]];
		wcArr[i] = firstSelDirection;
		if (i  == (membraneSegmentSelectionIndexes.length-1)){
			indexes[i+1] = fieldSampledDataIndexes[membraneSegmentSelectionIndexes[i]];
			wcArr[i+1] = secondSelDirection;
		}
		
		//if(i == 0){
			//indexes[2*i] = fieldSampledDataIndexes[membraneSegmentSelectionIndexes[i]];
			//wcArr[2*i] = firstSelDirection;
			//indexes[(2*i)+1] = fieldSampledDataIndexes[membraneSegmentSelectionIndexes[i]];
			//wcArr[(2*i)+1] = offsetCoordinate(secondSelDirection,firstSelDirection);
		//}else if (i  == (membraneSegmentSelectionIndexes.length-1)){
			//indexes[2*i] = fieldSampledDataIndexes[membraneSegmentSelectionIndexes[i]];
			//wcArr[2*i] = offsetCoordinate(firstSelDirection,secondSelDirection);
			//indexes[(2*i)+1] = fieldSampledDataIndexes[membraneSegmentSelectionIndexes[i]];
			//wcArr[(2*i)+1] = secondSelDirection;
		//}else{
			//indexes[2*i] = fieldSampledDataIndexes[membraneSegmentSelectionIndexes[i]];
			//wcArr[2*i] = offsetCoordinate(firstSelDirection,secondSelDirection);
			//indexes[(2*i)+1] = fieldSampledDataIndexes[membraneSegmentSelectionIndexes[i]];
			//wcArr[(2*i)+1] = offsetCoordinate(secondSelDirection,firstSelDirection);
		//}
	}
	
	return new SSHelper(wcArr,indexes,getVariableType(),null);
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 3:29:48 PM)
 * @return double
 */
public double getLengthInMicrons() {
	double[] segmentLengths = getSegmentLengths();
	double length = 0.0;
	for(int i=0;i<segmentLengths.length;i+= 1){
		length+= segmentLengths[i];
	}
	return length;
}
/**
 * Insert the method's description here.
 * Creation date: (7/14/2004 2:54:03 PM)
 * @return double[]
 */
public double[] getSegmentLengths() {
	int[] membraneSegmentSelectionIndexes = getCurveSelectionInfo().getSegmentsInSelectionOrder();
	double[] segmentLengths = new double[membraneSegmentSelectionIndexes.length];
	for(int i=0;i<membraneSegmentSelectionIndexes.length;i+= 1){
		segmentLengths[i] = getCurveSelectionInfo().getCurve().getSampledCurve().getSegmentSpatialLength(membraneSegmentSelectionIndexes[i]);
	}
	return segmentLengths;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2004 12:11:37 PM)
 * @return cbit.vcell.geometry.SampledCurve
 */
public cbit.vcell.geometry.SampledCurve getSelectionSource() {
	return selectionSource;
}
}

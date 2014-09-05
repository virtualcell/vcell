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


/**
 * Insert the type's description here.
 * Creation date: (7/18/2001 2:39:54 PM)
 * @author: Frank Morgan
 */
public class SpatialSelectionContour extends SpatialSelection {
	private int[] fieldSampledDataIndexes = null;
/**
 * SpatialSelectionGeometry constructor comment.
 * @param argCurveSelectionInfo cbit.vcell.geometry.CurveSelectionInfo
 * @param argVarType cbit.vcell.simdata.VariableType
 * @param argMesh cbit.vcell.solvers.CartesianMesh
 * @param sampledDataIndexes int[]
 * @param selectionKind int
 */
public SpatialSelectionContour(cbit.vcell.geometry.CurveSelectionInfo argCurveSelectionInfo, cbit.vcell.math.VariableType argVarType, cbit.vcell.solvers.CartesianMesh argMesh, int[] sampledDataIndexes) {
	super(argCurveSelectionInfo, argVarType, argMesh);
	if (argVarType.equals(cbit.vcell.math.VariableType.CONTOUR)){
		fieldSampledDataIndexes = sampledDataIndexes;
	}else if (argVarType.equals(cbit.vcell.math.VariableType.CONTOUR_REGION)){
		fieldSampledDataIndexes = new int[sampledDataIndexes.length];
		for (int i = 0; i < sampledDataIndexes.length; i++){
			fieldSampledDataIndexes[i] = argMesh.getContourRegionIndex(sampledDataIndexes[i]);
		}
	}else{
		throw new IllegalArgumentException("unexpected value for VariableType = "+argVarType.toString());
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
	return getSampledDataIndexes()[curveSegment];
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/01 5:59:31 PM)
 * @return int[]
 */
public int[] getIndexSamples() {
	int beginSeg = getCurveSelectionInfo().getSegment();
	int endSeg = getCurveSelectionInfo().getSegmentExtended();

	int indexes[] = new int[getCurveSelectionInfo().getSegmentCount()];
	
	if (beginSeg==endSeg){
		indexes[0] = getSampledDataIndexes()[beginSeg];
		
	}else if (beginSeg<endSeg && getCurveSelectionInfo().getDirectionNegative()==false){
		//
		// from low to high in positive direction
		//
		int count = 0;
		for (int seg = beginSeg; seg <= endSeg; seg++){
			indexes[count++] = getSampledDataIndexes()[seg];
		}
		
	}else if (beginSeg>endSeg && getCurveSelectionInfo().getDirectionNegative()==true){
		//
		// from high to low in negative direction
		//
		int count = 0;
		for (int seg = beginSeg; seg >= endSeg; seg--){
			indexes[count++] = getSampledDataIndexes()[seg];
		}
		
	}else if (beginSeg<endSeg && getCurveSelectionInfo().getDirectionNegative()==true){
		//
		// from low to high in negative direction (only works for closed curves)
		//
		if (!getCurveSelectionInfo().getCurve().isClosed()){
			throw new RuntimeException("CurveSelectionInfo.getSegmentCount(), curve selection is corrupted, begin<end, neg dir, not closed");
		}
		int count = 0;
		for (int seg = beginSeg; seg >= 0; seg--){
			indexes[count++] = getSampledDataIndexes()[seg];
		}
		for (int seg = getSampledDataIndexes().length-1; seg >= endSeg; seg--){
			indexes[count++] = getSampledDataIndexes()[seg];
		}
		
	}else if (beginSeg>endSeg && getCurveSelectionInfo().getDirectionNegative()==false){
		//
		// from high to low in positive direction (only works for closed curves)
		//
		if (!getCurveSelectionInfo().getCurve().isClosed()){
			throw new RuntimeException("CurveSelectionInfo.getIndex(), curve selection is corrupted, begin>end, pos dir, not closed");
		}
		int count = 0;
		for (int seg = beginSeg; seg <= getSampledDataIndexes().length-1; seg++){
			indexes[count++] = getSampledDataIndexes()[seg];
		}
		for (int seg = 0; seg <= endSeg; seg++){
			indexes[count++] = getSampledDataIndexes()[seg];
		}
		
	}else{
		throw new RuntimeException("cannot happen");
	}

	return indexes;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 3:29:48 PM)
 * @return double
 */
public double getLengthInMicrons() {
	return getCurveSelectionInfo().getCurve().getSpatialLength();
}
/**
 * Gets the sampledDataIndexes property (int[]) value.
 * @return The sampledDataIndexes property value.
 */
public int[] getSampledDataIndexes() {
	return fieldSampledDataIndexes;
}
}

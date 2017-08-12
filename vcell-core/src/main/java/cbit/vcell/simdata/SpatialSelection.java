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

import cbit.vcell.geometry.CurveSelectionInfo;
import cbit.vcell.math.VariableType;
import cbit.vcell.solvers.CartesianMesh;
/**
 * Insert the type's description here.
 * Creation date: (2/26/2001 3:48:34 PM)
 * @author: Jim Schaff
 */
public abstract class SpatialSelection implements java.io.Serializable, org.vcell.util.Matchable {
	
	private CurveSelectionInfo curveSelectionInfo = null;
	transient private CartesianMesh mesh = null;
	private VariableType varType = null;

	//
	public class SSHelper {

		VariableType varType;
		Coordinate[]	meshCoords;
		int[]			indexes;
		double[]		lengths;
		double[]		values;
		int[]			membraneIndexesInOut;
		
		public SSHelper(Coordinate[] coords,int[] argIndexes,VariableType vt,int[] argMembraneIndexesInOut){
			if(coords.length != argIndexes.length || (argMembraneIndexesInOut != null && argIndexes.length != argMembraneIndexesInOut.length)){
				throw new IllegalArgumentException(this.getClass().getName()+" Coordinate,index and membIndexInOut arrays must be same length");
			}
			if(!(vt.equals(VariableType.POSTPROCESSING) || vt.equals(VariableType.VOLUME) || vt.equals(VariableType.VOLUME_REGION)) &&
				!(vt.equals(VariableType.MEMBRANE) || vt.equals(VariableType.MEMBRANE_REGION))){
				throw new IllegalArgumentException(this.getClass().getName()+" Unsupported VariableType="+vt.getTypeName());
			}
			varType = vt;
			meshCoords = coords;
			indexes = argIndexes;
			calculateAccumWCLengths();
			membraneIndexesInOut = argMembraneIndexesInOut;
		};
		private void calculateAccumWCLengths(){
			lengths = new double[meshCoords.length];
			double accumDist = 0.0;
			for(int i=0;i<lengths.length;i+= 1){
				if(i != 0){
					accumDist+= meshCoords[i-1].distanceTo(meshCoords[i]);
				}
				lengths[i] = accumDist;
			}
		}
		public int[] getMembraneIndexesInOut(){
			return membraneIndexesInOut;
		}
		public int[] getSampledIndexes(){
			return indexes;
		}
		public double[] getSampledValues(){
			return values;
		}
		public double[] getWorldCoordinateLengths(){
			return lengths;
		}
		public double getWorldCoordinateTotalLength(){
			return lengths[lengths.length-1];
		}
		public int getIndexCount(){
			return indexes.length;
		}
		public void initializeValues_VOLUME(double[] sourceValues){
			if(!(varType.equals(VariableType.VOLUME) || varType.equals(VariableType.POSTPROCESSING))){
				throw new IllegalArgumentException(this.getClass().getName()+".initializeValues_VOLUME Unsupported VariableType="+varType.getTypeName());
			}
			initValues(sourceValues);
		}
		public void initializeValues_VOLUMEREGION(double[] sourceValues){
			if(!varType.equals(VariableType.VOLUME_REGION)){
				throw new IllegalArgumentException(this.getClass().getName()+".initializeValues_VOLUMEREGION Unsupported VariableType="+varType.getTypeName());
			}
			initValues(sourceValues);
		}
		public void initializeValues_MEMBRANE(double[] sourceValues){
			if(!varType.equals(VariableType.MEMBRANE)){
				throw new IllegalArgumentException(this.getClass().getName()+".initializeValues_MEMBRANE Unsupported VariableType="+varType.getTypeName());
			}
			initValues(sourceValues);
		}
		public void initializeValues_MEMBRANEREGION(double[] sourceValues){
			if(!varType.equals(VariableType.MEMBRANE_REGION)){
				throw new IllegalArgumentException(this.getClass().getName()+".initializeValues_MEMBRANEREGION Unsupported VariableType="+varType.getTypeName());
			}
			initValues(sourceValues);
		}


		
		
		private void initValues(double[] sourceValues){
			values = new double[indexes.length];
			for(int i=0;i<values.length;i+= 1){
				values[i] = sourceValues[indexes[i]];
			}
		}
	};
	
/**
 * SpatialSelection constructor comment.
 */
protected SpatialSelection(CurveSelectionInfo argCurveSelectionInfo, VariableType argVarType, CartesianMesh argMesh){
	if (argCurveSelectionInfo==null || argMesh==null || argVarType==null){
		throw new IllegalArgumentException("null argument");
	}
	if (argCurveSelectionInfo.getType()==CurveSelectionInfo.TYPE_CURVE && !argCurveSelectionInfo.getCurve().isValid()){
		throw new IllegalArgumentException("curve is invalid");
	}
	
	this.curveSelectionInfo = argCurveSelectionInfo;
	this.mesh = argMesh;
	this.varType = argVarType;
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (this == obj) {
		return true;
	}
	if (obj instanceof SpatialSelection) {
		SpatialSelection ss = (SpatialSelection)obj;
		if (
			getCurveSelectionInfo().compareEqual(ss.getCurveSelectionInfo()) &&
			getVariableType().compareEqual(ss.getVariableType()) &&
			getMesh().compareEqual(ss.getMesh())
		) {
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 1:57:35 PM)
 * @return cbit.vcell.geometry.CurveSelectionInfo
 */
public cbit.vcell.geometry.CurveSelectionInfo getCurveSelectionInfo() {
	return curveSelectionInfo;
}


/**
 * Insert the method's description here.
 * Creation date: (7/18/01 4:07:37 PM)
 * @return int
 * @param u double
 */
public abstract int getIndex(double u);


/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 3:14:22 PM)
 * @return double
 */
public abstract double getLengthInMicrons();


/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 1:54:58 PM)
 * @return cbit.vcell.solvers.CartesianMesh
 */
public cbit.vcell.solvers.CartesianMesh getMesh() {
	return mesh;
}


/**
 * Insert the method's description here.
 * Creation date: (10/9/2004 12:28:14 PM)
 * @return double
 */
public double getSmallestMeshCellDimensionLength() {
	switch (getMesh().getGeometryDimension()){
		case 1: {
			double smallestMeshCellDimensionLength = 
				getMesh().getExtent().getX()/getMesh().getSizeX();
			return smallestMeshCellDimensionLength;
		}
		case 2: {
			double smallestMeshCellDimensionLength =
				Math.min(	getMesh().getExtent().getX()/getMesh().getSizeX(),
							getMesh().getExtent().getY()/getMesh().getSizeY());
			return smallestMeshCellDimensionLength;
		}
		case 3: {
			double smallestMeshCellDimensionLength =
				Math.min(
					Math.min(	getMesh().getExtent().getX()/getMesh().getSizeX(),
								getMesh().getExtent().getY()/getMesh().getSizeY()),
					getMesh().getExtent().getZ()/getMesh().getSizeZ());
			return smallestMeshCellDimensionLength;
		}
		default: {
			throw new RuntimeException("unexpected dimension for mesh '"+getMesh().getGeometryDimension());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2001 6:17:10 PM)
 * @return int[]
 * @param numSamples int
 */
public VariableType getVariableType(){
	return varType;
}


/**
 * Insert the method's description here.
 * Creation date: (2/27/2001 1:49:33 PM)
 * @return boolean
 */
public boolean isClosed() {
	return curveSelectionInfo.getCurve().isClosed();
}


/**
 * Insert the method's description here.
 * Creation date: (2/27/2001 1:47:14 PM)
 * @return boolean
 */
public boolean isPoint() {
	return
		(curveSelectionInfo.getCurve() instanceof cbit.vcell.geometry.SinglePoint);
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 1:54:58 PM)
 * @return cbit.vcell.solvers.CartesianMesh
 */
public void setMesh(CartesianMesh argMesh) {
	if(org.vcell.util.Compare.isEqual(mesh,argMesh)){
		return;
	}
	if(mesh != null){
		throw new RuntimeException(this.getClass().getName()+"  Error: Mesh is already set and cannot be changed.");
	}
	mesh = argMesh;
}


/**
 * Insert the method's description here.
 * Creation date: (2/27/2001 5:19:31 PM)
 * @return java.lang.String
 */
public String toString() {
	cbit.vcell.geometry.Curve curve = this.curveSelectionInfo.getCurve();
	if (isPoint()){
		return "Point="+curve.hashCode()+" ["+((cbit.vcell.geometry.SinglePoint)curve).getBeginningCoordinate()+"]";
	}else if(this.curveSelectionInfo.getType() == CurveSelectionInfo.TYPE_CURVE ||
			 this.curveSelectionInfo.getType() == CurveSelectionInfo.TYPE_CONTROL_POINT){
		return "Curve="+curve.hashCode()+" ["+curve.getBeginningCoordinate()+" to "+curve.getEndingCoordinate()+"]";
	}else if(this.curveSelectionInfo.getType() == CurveSelectionInfo.TYPE_SEGMENT){
		return "Curve="+curve.hashCode()+" Segments ["+this.curveSelectionInfo.getSegmentCount()+"]";
	}else if(this.curveSelectionInfo.getType() == CurveSelectionInfo.TYPE_U){
		return "Curve="+curve.hashCode()+" U ["+this.curveSelectionInfo.getU()+" to "+this.curveSelectionInfo.getUExtended()+"]";
	}else{
		return "Curve="+curve.hashCode();
	}
}
}

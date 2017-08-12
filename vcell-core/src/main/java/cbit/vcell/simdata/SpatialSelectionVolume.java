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

import java.util.Arrays;
import java.util.Vector;

import org.vcell.util.Coordinate;
import org.vcell.util.CoordinateIndex;

import cbit.vcell.geometry.SampledCurve;
import cbit.vcell.geometry.SinglePoint;
import cbit.vcell.math.VariableType;

/**
 * Insert the type's description here.
 * Creation date: (7/18/2001 2:39:21 PM)
 * @author: Frank Morgan
 */
public class SpatialSelectionVolume extends SpatialSelection {

	//
	//
	//
	//
/**
 * SpatialSelectionMesh constructor comment.
 * @param argCurveSelectionInfo cbit.vcell.geometry.CurveSelectionInfo
 * @param argVarType cbit.vcell.simdata.VariableType
 * @param argMesh cbit.vcell.solvers.CartesianMesh
 * @param sampledDataIndexes int[]
 * @param selectionKind int
 */
public SpatialSelectionVolume(cbit.vcell.geometry.CurveSelectionInfo argCurveSelectionInfo, cbit.vcell.math.VariableType argVarType, cbit.vcell.solvers.CartesianMesh argMesh) {
	super(argCurveSelectionInfo, argVarType, argMesh);
}
/**
 * Insert the method's description here.
 * Creation date: (10/4/2004 12:21:25 PM)
 * @return boolean
 * @param ci cbit.vcell.math.CoordinateIndex
 * @param ci2 cbit.vcell.math.CoordinateIndex
 */
private boolean areTouching(org.vcell.util.CoordinateIndex ci1, org.vcell.util.CoordinateIndex ci2) {
	
	int dx = Math.abs(ci1.x-ci2.x);
	int dy = Math.abs(ci1.y-ci2.y);
	int dz = Math.abs(ci1.z-ci2.z);
	
	if((dx != 0 && dx != 1) || (dy != 0 && dy != 1) || (dz != 0 && dz != 1)){
		return false;
	}

	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (10/4/2004 12:21:25 PM)
 * @return boolean
 * @param ci cbit.vcell.math.CoordinateIndex
 * @param ci2 cbit.vcell.math.CoordinateIndex
 */
private boolean areTouchingFace(org.vcell.util.CoordinateIndex ci1, org.vcell.util.CoordinateIndex ci2) {
	
	int dx = Math.abs(ci1.x-ci2.x);
	int dy = Math.abs(ci1.y-ci2.y);
	int dz = Math.abs(ci1.z-ci2.z);

	int offCount = (dx>0?1:0) + (dy>0?1:0) + (dz>0?1:0);

	return offCount == 1;
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2001 6:17:10 PM)
 * @return int[]
 * @param numSamples int
 */
private int getConvertedIndexFromCI(CoordinateIndex ci) {

	if (getVariableType().equals(VariableType.VOLUME) || getVariableType().equals(VariableType.POSTPROCESSING)){
		return getMesh().getVolumeIndex(ci);
	}else if (getVariableType().equals(VariableType.VOLUME_REGION)){
		return getMesh().getVolumeRegionIndex(ci);
	}else{
		throw new RuntimeException("SpatialSelection.getIndex(), unsupported VariableType = "+getVariableType());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2001 6:17:10 PM)
 * @return int[]
 * @param numSamples int
 */
private int getConvertedIndexFromU(double u) {
	
	Coordinate sampleCoordinate = getSamplingWorldCoordinate(u);
	return getConvertedIndexFromWC(sampleCoordinate);
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2001 6:17:10 PM)
 * @return int[]
 * @param numSamples int
 */
private int getConvertedIndexFromWC(Coordinate wc) {

	return getConvertedIndexFromCI(getCoordinateIndexFromWC(wc));

}
/**
 * Insert the method's description here.
 * Creation date: (10/5/2004 2:13:34 PM)
 * @return cbit.vcell.math.CoordinateIndex
 * @param wc cbit.vcell.geometry.Coordinate
 */
private CoordinateIndex getCoordinateIndexFromWC(Coordinate wc) {
	
	Coordinate fi = getMesh().getFractionalCoordinateIndex(wc);
	return getMesh().getCoordinateIndexFromFractionalIndex(fi);
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2001 6:17:10 PM)
 * @return int[]
 * @param numSamples int
 */
public int getIndex(double u) {

	return getConvertedIndexFromU(u);
}

private int getMeshIndexFromU(double u) {
	Coordinate wc = getSamplingWorldCoordinate(u);
	CoordinateIndex ci = getCoordinateIndexFromWC(wc);
	return getMesh().getVolumeIndex(ci);
}

/**
 * Insert the method's description here.
 * Creation date: (2/26/2001 6:17:10 PM)
 * @return int[]
 * @param numSamples int
 */
public SSHelper getIndexSamples(double begin,double end) {

	//
	// Find minimum number of points that return a
	// continuous(touch at least at corner) set of mesh indexes.
	//

	if(getCurveSelectionInfo().getCurve() instanceof SinglePoint){
		return new SSHelper(
			new Coordinate[] {getCurveSelectionInfo().getCurve().getCoordinate(0)},
			new int[] {getConvertedIndexFromWC(getCurveSelectionInfo().getCurve().getCoordinate(0))},
			getVariableType(),
			new int[] {-1});
	}
	//Try simple sampling
	SSHelper ssvHelper = null;
	if(begin == 0.0 && end == 1.0){
		try{
			ssvHelper = sampleSymmetric();
		}catch(Throwable e){
			//Do nothing, we'll try sampling below
		}
	}
	if(ssvHelper != null){
		return ssvHelper;
	}


	//
	// Find continuous mesh elements along the curve
	//

	final int SAMPLE_MULT = 10;
	// Determine reasonable sample size
	double smallestMeshCellDimension = getSmallestMeshCellDimensionLength();
	double uSpatialLength = getCurveSelectionInfo().getCurve().getSpatialLength()*(Math.abs(end-begin));
	int SSV_NUM_SAMPLES = (int)(Math.ceil(uSpatialLength/smallestMeshCellDimension)*(double)SAMPLE_MULT);
	if(SSV_NUM_SAMPLES < 2){SSV_NUM_SAMPLES = 2;}
	//
	double delta = (end-begin)/(double)SSV_NUM_SAMPLES;
	int[] uniquePoints = new int[SSV_NUM_SAMPLES];
	int uniquePointCount = 0;
	Vector<Coordinate> wcV = new Vector<Coordinate>();
	//Vector ciV = new Vector();
//	double[] cellStepArr = new double[SAMPLE_MULT*10];//big enough
	double[] cellStepArr = new double[SSV_NUM_SAMPLES];//big enough
	int cellStepCounter = 0;
	
	int lastISample = -1;
	int currISample;
	double currentU = begin;
	for(int index = 0;index < SSV_NUM_SAMPLES;index+= 1){
		
		boolean isLastLoop = index == (SSV_NUM_SAMPLES-1);
		
		if(isLastLoop){
			currISample = getMeshIndexFromU(end);
			currentU = end;
		}else{
			currISample = getMeshIndexFromU(currentU);
		}
		if((index == 0) || (lastISample != currISample)){
			uniquePoints[uniquePointCount] = getIndex(currentU);
			double midU = midpoint(cellStepArr,cellStepCounter);
			if(index == 0){midU = begin;}
			if(isLastLoop){midU = end;}
			Coordinate wc = getSamplingWorldCoordinate(midU);
			if(index == 0 || isLastLoop || (uniquePointCount > 1)){
				wcV.add(wc);
			}
			//CoordinateIndex ci = getCoordinateIndexFromWC(wc);
			//ciV.add(ci);
			uniquePointCount+= 1;
			//if(ciV.size() > 1 &&!areTouching(((CoordinateIndex)ciV.elementAt(ciV.size()-1)),((CoordinateIndex)ciV.elementAt(ciV.size()-2)))){
				////this shouldn't happen if our sampling is fine enough
				////all sampled mesh elements should be "touching"
			//}
			cellStepCounter = 0;
		}else if(isLastLoop && (wcV.size() != uniquePointCount)){
			wcV.add(getSamplingWorldCoordinate(end));
		}
		cellStepArr[cellStepCounter] = currentU;
		cellStepCounter+=1;
		currentU+= delta;
		lastISample = currISample;
	}

	// "snap" first and last element to end of selection
	//if(wcV.size() > 0){
		////CoordinateIndex snappedCI = getCoordinateIndexFromWC((Coordinate)wcV.elementAt(0));
		////wcV.set(0,getMesh().getCoordinate(snappedCI));
		//wcV.set(0,getSamplingWorldCoordinate(begin));
	//}
	//if(wcV.size() > 1){
		////CoordinateIndex snappedCI = getCoordinateIndexFromWC((Coordinate)wcV.lastElement());
		////wcV.set(wcV.size()-1,getMesh().getCoordinate(snappedCI));
		//wcV.set(wcV.size()-1,getSamplingWorldCoordinate(end));
	//}

	
	if(uniquePointCount > 0){
		return makeSSHelper(uniquePoints,uniquePointCount,wcV,true);
	}

	//This shouldn't happen
	throw new RuntimeException(this.getClass().getName()+" couldn't generate sampling");


}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2001 2:29:30 AM)
 * @return double
 */
public double getLengthInMicrons() {

	return getIndexSamples(0.0,1.0).getWorldCoordinateTotalLength();
}
/**
 * Insert the method's description here.
 * Creation date: (10/4/2004 4:53:59 PM)
 * @return cbit.vcell.geometry.Coordinate
 * @param u double
 */
private Coordinate getSamplingWorldCoordinate(double u) {
	
	return 	getCurveSelectionInfo().getCurve().getSampledCurve().coordinateFromNormalizedU(u);
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/2004 12:54:59 PM)
 * @return boolean
 */
private boolean isSymmetric() {

	if(!(getCurveSelectionInfo().getCurve() instanceof SampledCurve)){
		return false;
	}
	SampledCurve ssCurve = getCurveSelectionInfo().getCurve().getSampledCurve();
	Vector pointsV = ssCurve.getControlPointsVector();
	
	if(pointsV.size() == 1){
		return true;
	}
	
	for(int i=0;i<pointsV.size();i+= 1){
		if(i > 0){
			CoordinateIndex c1 = getMesh().getCoordinateIndexFromFractionalIndex(
				getMesh().getFractionalCoordinateIndex((Coordinate)pointsV.elementAt(i-1)));
			CoordinateIndex c2 = getMesh().getCoordinateIndexFromFractionalIndex(
				getMesh().getFractionalCoordinateIndex((Coordinate)pointsV.elementAt(i)));
			int dx = Math.abs(c1.x - c2.x);
			int dy = Math.abs(c1.y - c2.y);
			int dz = Math.abs(c1.z - c2.z);
			int symCount = 0;
			if(dx != 0 && dy != 0 && dx != dy){
				symCount+= 1;
			}
			if(dx != 0 && dz != 0 && dx != dz){
				symCount+= 1;
			}
			if(dy != 0 && dz != 0 && dy != dz){
				symCount+= 1;
			}
			if(symCount != 0){
				return false;
			}
		}
	}

	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (10/6/2004 8:09:38 AM)
 * @return cbit.vcell.geometry.Coordinate
 */
private static Coordinate lineMeshFaceIntersect3D(Coordinate mesh1,Coordinate mesh2,Coordinate cp1,Coordinate cp2) {

	//http://astronomy.swin.edu.au/~pbourke/geometry/planeline/
	
	// mesh1,mesh2 are CartesianMesh Coordinates from from
	// adjacent elements that share a face (points on the face Normal)
	//
	// cp1,cp2 are points on a line to test intersection

	
	//N is plane normal vector
	//P3 is point on plane
	//
	//P1,P2 are points on line
	//P = P1 + u(P2-P1) -- equation of line
	//
	//determine u where line intersects plane
	//u = (N dot (P3-P1))/(N dot (P2-P1));

	final double EPS = 1.0E-12; //epsilon

	cbit.vcell.render.Vect3d p1 = new cbit.vcell.render.Vect3d(cp1.getX(),cp1.getY(),cp1.getZ());
	cbit.vcell.render.Vect3d p2 = new cbit.vcell.render.Vect3d(cp2.getX(),cp2.getY(),cp2.getZ());
	
	cbit.vcell.render.Vect3d n1 = new cbit.vcell.render.Vect3d(mesh1.getX(),mesh1.getY(),mesh1.getZ());
	cbit.vcell.render.Vect3d n2 = new cbit.vcell.render.Vect3d(mesh2.getX(),mesh2.getY(),mesh2.getZ());

	cbit.vcell.render.Vect3d n = cbit.vcell.render.Vect3d.sub(n2,n1);
	cbit.vcell.render.Vect3d p3 = new cbit.vcell.render.Vect3d(n);
	p3.scale(.5);
	p3.add(n1);
	n.unit();

	cbit.vcell.render.Vect3d p3minp1 = cbit.vcell.render.Vect3d.sub(p3,p1);
	cbit.vcell.render.Vect3d p2minp1 = cbit.vcell.render.Vect3d.sub(p2,p1);

	double denominator = n.dot(p2minp1);
	//if(denominator == 0.0){
	if(Math.abs(denominator) < EPS){
		//line paralell to plane or on plane
		return null;
	}
	double numerator = n.dot(p3minp1);
	double u = numerator/denominator;

	p2minp1.scale(u);
	p1.add(p2minp1);
	Coordinate finalCoord = new Coordinate(p1.getX(),p1.getY(),p1.getZ());
	return finalCoord;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/2004 8:52:05 AM)
 */
private SSHelper makeSSHelper(int[] indexes,int indexCounter,Vector wcV,boolean bRecenter) {
	
		int[] finalIndexes = new int[indexCounter];
		System.arraycopy(indexes,0,finalIndexes,0,indexCounter);
		Coordinate[] finalWC = new Coordinate[wcV.size()];
		wcV.copyInto(finalWC);
		//
		try{
			return resampleMeshBoundaries(finalIndexes,finalWC,bRecenter);
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("Error resampling mesh boundaries");
		}
//		SSHelper ssvHelper = null;
//		try{
//			ssvHelper = resampleMeshBoundaries(finalIndexes,finalWC,bRecenter);
//		}catch(Throwable e){
//			e.printStackTrace();
//		}
//		if(ssvHelper != null){
//			return ssvHelper;
//		}
//		//
//		return new SSHelper(finalWC,finalIndexes,getVariableType(),null);
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2004 5:17:54 PM)
 * @return cbit.vcell.geometry.Coordinate
 * @param segU double[]
 */
private double midpoint(double[] segU,int count) {
	if(count < 1){
		return 0;
	}
	if(count == 1){
		return segU[0];
	}
	if((count % 2) == 1){
		return segU[count/2];
	}
	return (segU[(count/2)-1] + segU[(count/2)])/2.0;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2004 12:20:17 PM)
 * @return cbit.vcell.geometry.Coordinate
 * @param from cbit.vcell.geometry.Coordinate
 * @param to cbit.vcell.geometry.Coordinate
 */
private Coordinate offsetCoordinate(Coordinate start, Coordinate towards) {

	// Offset 1/10 the smallest mesh cell dimension length
	cbit.vcell.render.Vect3d offsetV =
		new cbit.vcell.render.Vect3d(
				(towards.getX()-start.getX()),
				(towards.getY()-start.getY()),
				(towards.getZ()-start.getZ())
			);
		
	double length = offsetV.length();
	double scale = (getSmallestMeshCellDimensionLength()/10.0)/length;
	offsetV.scale(scale);
	
	Coordinate offsetCoord = new Coordinate(start.getX()+offsetV.getX(),start.getY()+offsetV.getY(),start.getZ()+offsetV.getZ());
		
	return offsetCoord;
}
/**
 * Insert the method's description here.
 * Creation date: (10/6/2004 11:31:20 AM)
 */
private SSHelper resampleMeshBoundaries(int[] indexes,Coordinate[] wcV,boolean bRecenter) throws Exception{

	// Find where our samples cross mesh boundaries
	// add 2 points for each boundary crossing if its a membrane

	if(wcV.length < 2){
		return null;
	}

	final int MEMBRANE_BOUNDARY = -1;
	final int NON_MEMBRANE_BOUNDARY = -2;
	final int MEMB_INDEX = 0;
	final int IN_VOL = 1;
	final int OUT_VOL = 2;
	
	int newCount = 0;
	int[] newIndexes = new int[indexes.length*3];
	int[][][] newCrossingMembraneIndex = new int[newIndexes.length][4][3];
	Coordinate[] newWCV = new Coordinate[wcV.length*3];

	for(int i=1;i<wcV.length;i+= 1){
		CoordinateIndex ci1 = getCoordinateIndexFromWC(wcV[i]);
		CoordinateIndex ci2 = getCoordinateIndexFromWC(wcV[i-1]);
		CoordinateIndex ci3 = null;
		CoordinateIndex ci4 = null;
		CoordinateIndex[][] faces = null;
		if( areTouchingFace(ci1,ci2)){
			faces = new CoordinateIndex[][] {{ci1,ci2}};
		}else if(areTouching(ci1,ci2)){//must be a corner
			//determine the 4 faces that coincide with this corner
			int dx = ci2.x-ci1.x;
			int dy = ci2.y-ci1.y;
			int dz = ci2.z-ci1.z;
			if(dx == 0 && dy != 0 && dz != 0){
				ci3 = new CoordinateIndex(ci1.x,ci1.y+dy,ci1.z);
				ci4 = new CoordinateIndex(ci1.x,ci1.y,ci1.z+dz);
			}else if(dx != 0 && dy == 0 && dz != 0){
				ci3 = new CoordinateIndex(ci1.x+dx,ci1.y,ci1.z);
				ci4 = new CoordinateIndex(ci1.x,ci1.y,ci1.z+dz);
			}else if(dx != 0 && dy != 0 && dz == 0){
				ci3 = new CoordinateIndex(ci1.x,ci1.y+dy,ci1.z);
				ci4 = new CoordinateIndex(ci1.x+dx,ci1.y,ci1.z);
			}else{
				throw new Exception(this.getClass().getName()+".resampleMeshBoundaries Couldn't adjust for corners");
			}
			faces = new CoordinateIndex[][] {{ci1,ci3},{ci1,ci4},{ci2,ci3},{ci2,ci4}};
			
		}else{
			throw new Exception(this.getClass().getName()+".resampleMeshBoundaries Expecting touching elements");
		}

		//System.out.println("p1="+i+" p2="+(i-1));
		boolean[] bCrossMembraneArr = new boolean[faces.length];
		java.util.Arrays.fill(bCrossMembraneArr,false);
		int[][] crossMembraneIndexInOutArr = new int[bCrossMembraneArr.length][3];
		Coordinate[] intersectArr = new Coordinate[faces.length];
		int intersectNotNullCount = 0;
		//Find out where the line segment between volume elements intersect the face(s)
		for(int j=0;j<faces.length;j+= 1){
			crossMembraneIndexInOutArr[j][MEMB_INDEX] = -1;
			crossMembraneIndexInOutArr[j][IN_VOL] = -1;
			crossMembraneIndexInOutArr[j][OUT_VOL] = -1;
			intersectArr[j] = 
				lineMeshFaceIntersect3D(
					getMesh().getCoordinate(faces[j][0]),getMesh().getCoordinate(faces[j][1]),
					wcV[i],wcV[i-1]
					);
			intersectNotNullCount+= (intersectArr[j] != null?1:0);
			if(getMesh().getMembraneElements() != null){
				//Find out if this face is part of a Membrane
				int vi1 = getMesh().getVolumeIndex(faces[j][0]);
				int vi2 = getMesh().getVolumeIndex(faces[j][1]);
				for(int k=0;k<getMesh().getMembraneElements().length;k+= 1){
					int inside = getMesh().getMembraneElements()[k].getInsideVolumeIndex();
					int outside = getMesh().getMembraneElements()[k].getOutsideVolumeIndex();
					if((vi1 == inside && vi2 == outside) || (vi1 == outside && vi2 == inside)){
						bCrossMembraneArr[j] = true;
						crossMembraneIndexInOutArr[j][MEMB_INDEX] = k;
						crossMembraneIndexInOutArr[j][IN_VOL] = inside;
						crossMembraneIndexInOutArr[j][OUT_VOL] = outside;
						break;
					}
				}
			}
			//System.out.println("----- vi1="+vi1+" vi2="+vi2+" face="+j+" intersect="+intersectArr[j]+" bCrossMembrane="+bCrossMembraneArr[j]);
		}
		
		// Start re-sampling, temporarily add markers and WorldCoordinates at the intersections
		// we will use them later to determing the midpoint to adjust our data sample WorldCoordinates
		if(i == 1){
			newIndexes[newCount] = indexes[0];
			newWCV[newCount] = wcV[0];
			newCount+= 1;
		}
		
		if(faces.length == 1 && intersectNotNullCount == 1){
			// through face, use where it intersects and mark if has Membrane
			newIndexes[newCount] = (bCrossMembraneArr[0]?MEMBRANE_BOUNDARY:NON_MEMBRANE_BOUNDARY);
			newCrossingMembraneIndex[newCount] = crossMembraneIndexInOutArr;
			newWCV[newCount] = intersectArr[0];
			newCount+= 1;
		}else if(faces.length == 4 && intersectNotNullCount == 4){
			// Through a corner, calculate the corner Coordinate and mark if has Membrane
			Coordinate mc1 = getMesh().getCoordinate(ci1);
			Coordinate mc2 = getMesh().getCoordinate(ci2);
			Coordinate mc3 = getMesh().getCoordinate(ci3);
			Coordinate mc4 = getMesh().getCoordinate(ci4);
			Coordinate corner =
				new Coordinate(
					(mc1.getX()+mc2.getX()+mc3.getX()+mc4.getX())/4.0,
					(mc1.getY()+mc2.getY()+mc3.getY()+mc4.getY())/4.0,
					(mc1.getZ()+mc2.getZ()+mc3.getZ()+mc4.getZ())/4.0
				);
			newIndexes[newCount] = (bCrossMembraneArr[0]||bCrossMembraneArr[1]||bCrossMembraneArr[2]||bCrossMembraneArr[3]?MEMBRANE_BOUNDARY:NON_MEMBRANE_BOUNDARY);
//			newCrossingMembraneIndex[newCount] = crossMembraneIndexInOutArr;
			newWCV[newCount] = corner;
			newCount+= 1;
		}
		else{
			throw new Exception(this.getClass().getName()+" Unexpected intersection result");
		}
		
		newIndexes[newCount] = indexes[i];
		newWCV[newCount] = wcV[i];
		newCount+= 1;

	}

	// Count how many points we will finally end up with after markers are removed and
	// including the points that will be added for membrane intersections
	// also calculate the midpoints of the original data between the intersect points
	int finalCount = 2;//first and last
	// Adjust distances to center between boundary markers (inersections)
	for(int i=0;i<newCount;i+= 1){
		if(newIndexes[i] == MEMBRANE_BOUNDARY || newIndexes[i] == NON_MEMBRANE_BOUNDARY){
			if((i+2) < newCount){
				//if(bRecenter){
					//Coordinate b0 = newWCV[i];
					//Coordinate b1 = newWCV[i+2];
					//newWCV[i+1] = new Coordinate((b0.getX()+b1.getX())/2.0,(b0.getY()+b1.getY())/2.0,(b0.getZ()+b1.getZ())/2.0);
				//}else{
					newWCV[i+1] = newWCV[i+1];    //TODO: why is this here, is this really what we want? ... A = A;
				//}
				finalCount+= 1;
			}
		}
		if(newIndexes[i] == MEMBRANE_BOUNDARY){
			finalCount+= 2;
		}
	}

	// Final pass, remove markers, add additional points at membrane intersections
	int[] finalIndexes = new int[finalCount];
	int[] finalCrossingMembrane = null;
	Coordinate[] finalWC = new Coordinate[finalCount];
	finalCount = 0;
	for(int i=0;i<newCount;i+= 1){
		if(newIndexes[i] != MEMBRANE_BOUNDARY && newIndexes[i] != NON_MEMBRANE_BOUNDARY){
			// Get all original data points
			finalIndexes[finalCount] = newIndexes[i];
			finalWC[finalCount] = newWCV[i];
			finalCount+= 1;
		}else if(newIndexes[i] == MEMBRANE_BOUNDARY){
			for (int j = 0; j < newCrossingMembraneIndex[i].length; j++) {
				if( newCrossingMembraneIndex[i][j][MEMB_INDEX] != -1
					&&
					(
					(newCrossingMembraneIndex[i][j][IN_VOL] == newIndexes[i-1] && newCrossingMembraneIndex[i][j][OUT_VOL] == newIndexes[i+1])
					||
					(newCrossingMembraneIndex[i][j][OUT_VOL] == newIndexes[i-1] && newCrossingMembraneIndex[i][j][IN_VOL] == newIndexes[i+1])					
					)
				){
					if(finalCrossingMembrane == null){//make once only if needed
						finalCrossingMembrane = new int[finalIndexes.length];
						Arrays.fill(finalCrossingMembrane, -1);
					}
					finalCrossingMembrane[finalCount] = newCrossingMembraneIndex[i][j][MEMB_INDEX];
					finalCrossingMembrane[finalCount+1] = newCrossingMembraneIndex[i][j][MEMB_INDEX];
					break;
				}
			}
			// Add 2 new membrane intersection points, one for each side of the face
			finalIndexes[finalCount] = newIndexes[i-1];
			//finalWC[finalCount] = offsetCoordinate(newWCV[i],newWCV[i-1]);
			finalWC[finalCount] = newWCV[i];
			finalCount+= 1;
			
			finalIndexes[finalCount] = newIndexes[i+1];
			//finalWC[finalCount] = offsetCoordinate(newWCV[i],newWCV[i+1]);
			finalWC[finalCount] = newWCV[i];
			finalCount+= 1;
		}
		
	}

	
	return new SSHelper(finalWC,finalIndexes,getVariableType(),finalCrossingMembrane);
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/2004 7:19:49 AM)
 */
private SSHelper sampleSymmetric() throws Exception{

	//
	// Sample this whole selection from one controlpoint to the other
	// assumes u goes from 0-1 (whole selection sampled)
	// assumes sampling sections are symmetric (simple indexing). guarantees continuous mesh elements
	// all sample index locations are "snapped" to mesh element centers
	//
	if(!isSymmetric()){
		return null;
	}
	
	SampledCurve ssCurve = getCurveSelectionInfo().getCurve().getSampledCurve();
	Vector pointsV = ssCurve.getControlPointsVector();
	
	if(pointsV.size() < 2){
		return null;
	}

	final int INDEX_SPACE_INCR = 100;
	
	int[] indexes = new int[INDEX_SPACE_INCR];
	int indexCounter = 0;
	Vector snappedV = new Vector();
	CoordinateIndex lastCI = null;
	
	for(int i=0;i<pointsV.size();i+= 1){
		if(i > 0){
			CoordinateIndex c1 = getMesh().getCoordinateIndexFromFractionalIndex(
				getMesh().getFractionalCoordinateIndex((Coordinate)pointsV.elementAt(i-1)));
			CoordinateIndex c2 = getMesh().getCoordinateIndexFromFractionalIndex(
				getMesh().getFractionalCoordinateIndex((Coordinate)pointsV.elementAt(i)));

			int dx = c2.x - c1.x;
			if(dx != 0){dx/= Math.abs(dx);}
			int dy = c2.y - c1.y;
			if(dy != 0){dy/= Math.abs(dy);}
			int dz = c2.z - c1.z;
			if(dz != 0){dz/= Math.abs(dz);}
			while(true){
				if(!c1.compareEqual(lastCI)){
					if(indexCounter == indexes.length){
						//Make more space
						int[] temp = new int[indexes.length+INDEX_SPACE_INCR];
						System.arraycopy(indexes,0,temp,0,indexes.length);
						indexes = temp;
					}
					indexes[indexCounter] = getConvertedIndexFromCI(c1);
					snappedV.add(getMesh().getCoordinate(c1));
					indexCounter+= 1;
				}
				if(c1.compareEqual(c2)){
					break;
				}
				c1.x+= dx;
				c1.y+= dy;
				c1.z+= dz;
				//sanity check to prevent infinite loop, this shouldn't happen
				if(c1.x < 0 || c1.y < 0 || c1.z < 0 ||
					c1.x >= getMesh().getSizeX() || c1.y >= getMesh().getSizeY() || c1.z >= getMesh().getSizeZ()){
						throw new Exception(this.getClass().getName()+".sampleSymmetric failed");
				}
			};

			lastCI = c2;
			
		}
	}

	if(indexCounter > 0){
		return makeSSHelper(indexes,indexCounter,snappedV,false);
	}
	
	return null;

}
}

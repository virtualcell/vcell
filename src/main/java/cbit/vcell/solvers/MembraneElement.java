/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solvers;
import cbit.vcell.math.MathFormatException;
/**
 * This type was created in VisualAge.
 */
public class MembraneElement implements java.io.Serializable, org.vcell.util.Matchable {
	private int memIndex;
	private int insideIndex;
	private int outsideIndex;
	private int membraneNeighbors[] = null;
	public static final float AREA_UNDEFINED = -1.0f;
	float area = AREA_UNDEFINED;// default no meshmetrics
	float normalX;
	float normalY;
	float normalZ;
	float centroidX;
	float centroidY;
	float centroidZ;
	public static final int MAX_POSSIBLE_NEIGHBORS = 4;
	public static final int NEIGHBOR_UNDEFINED = -1;

/**
 * MembraneElement constructor comment.
 */
public MembraneElement(int argMembraneIndex, int argInsideIndex, int argOutsideIndex) {
	this.memIndex = argMembraneIndex;
	this.insideIndex = argInsideIndex;
	this.outsideIndex = argOutsideIndex;
}


/**
 * MembraneElement constructor comment.
 */
public MembraneElement(int argMembraneIndex, int argInsideIndex, int argOutsideIndex, 
						int neighbor1, int neighbor2, int neighbor3, int neighbor4,
						float argArea,float argNX,float argNY,float argNZ,
						float argCentroidX,float argCentroidY,float argCentroidZ) throws MathFormatException {
							
	this(argMembraneIndex,argInsideIndex,argOutsideIndex);
	this.area = argArea;
	this.normalX = argNX;
	this.normalY = argNY;
	this.normalZ = argNZ;
	this.centroidX = argCentroidX;
	this.centroidY = argCentroidY;
	this.centroidZ = argCentroidZ;
	setConnectivity(neighbor1,neighbor2,neighbor3,neighbor4);
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/01 1:53:16 PM)
 * @return boolean
 * @param object cbit.util.Matchable
 */
public boolean compareEqual(org.vcell.util.Matchable object) {
	if (this == object){
		return true;
	}
	if (!(object instanceof MembraneElement)){
		return false;
	}
	MembraneElement memElement = (MembraneElement)object;

	if (memIndex != memElement.memIndex){
		return false;
	}
	if (insideIndex != memElement.insideIndex){
		return false;
	}
	if (outsideIndex != memElement.outsideIndex){
		return false;
	}
	if ((membraneNeighbors!=null && memElement.membraneNeighbors==null) ||
		(membraneNeighbors==null && memElement.membraneNeighbors!=null)){
		return false;
	}
	if (membraneNeighbors!=null && memElement.membraneNeighbors!=null){
		if (membraneNeighbors.length!=memElement.membraneNeighbors.length){
			return false;
		}
		for (int i=0;i<membraneNeighbors.length;i++){
			if (membraneNeighbors[i] != memElement.membraneNeighbors[i]){
				return false;
			}
		}
	}
	if(this.area != memElement.area){
		return false;
	}
	if(this.normalX != memElement.normalX ||
		this.normalY != memElement.normalY ||
		this.normalZ != memElement.normalZ){
		return false;
	}
	
	if(this.centroidX != memElement.centroidX ||
		this.centroidY != memElement.centroidY ||
		this.centroidZ != memElement.centroidZ){
		return false;
	}
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (2/15/2006 1:54:50 PM)
 * @return java.lang.Double
 */
public float getArea() {
	return area;
}


/**
 * Insert the method's description here.
 * Creation date: (2/15/2006 1:54:50 PM)
 * @return cbit.vcell.render.Vect3d
 */
public org.vcell.util.Coordinate getCentroid() {
	return (area == AREA_UNDEFINED?null:new org.vcell.util.Coordinate(centroidX,centroidY,centroidZ));
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/01 2:25:47 PM)
 * @return int
 */
public int getInsideVolumeIndex() {
	return this.insideIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/01 2:25:47 PM)
 * @return int
 */
public int getMembraneIndex() {
	return this.memIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/01 2:25:47 PM)
 * @return int
 */
public int[] getMembraneNeighborIndexes() {
	return this.membraneNeighbors;
}


/**
 * Insert the method's description here.
 * Creation date: (2/15/2006 1:54:50 PM)
 * @return cbit.vcell.render.Vect3d
 */
public cbit.vcell.render.Vect3d getNormal() {
	return (area == AREA_UNDEFINED?null:new cbit.vcell.render.Vect3d(normalX,normalY,normalZ));
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/01 2:25:47 PM)
 * @return int
 */
public int getOutsideVolumeIndex() {
	return this.outsideIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (8/30/00 3:06:14 PM)
 * @param neighbor1 int
 * @param neighbor2 int
 * @param neighbor3 int
 * @param neighbor4 int
 */
public void setConnectivity(int neighbor1, int neighbor2, int neighbor3, int neighbor4) throws cbit.vcell.math.MathFormatException {
	this.membraneNeighbors = null;
	int numNeighbors = 0;
	if (neighbor1 != -1)
		numNeighbors++;
	if (neighbor2 != -1)
		numNeighbors++;
	if (neighbor3 != -1)
		numNeighbors++;
	if (neighbor4 != -1)
		numNeighbors++;

	if (numNeighbors > 0) {
		int neighbors[] = new int[numNeighbors];
		int count = 0;
		if (neighbor1 != -1)
			neighbors[count++] = neighbor1;
		if (neighbor2 != -1)
			neighbors[count++] = neighbor2;
		if (neighbor3 != -1)
			neighbors[count++] = neighbor3;
		if (neighbor4 != -1)
			neighbors[count++] = neighbor4;
		this.membraneNeighbors = neighbors;
	}else{
		this.membraneNeighbors = new int[0];
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/2/2001 12:05:42 PM)
 * @return java.lang.String
 */
public String toString() {
	return(	getMembraneIndex()+" "+
			getInsideVolumeIndex()+" " +
			getOutsideVolumeIndex()+" "+
			((membraneNeighbors != null && membraneNeighbors.length > 0)?membraneNeighbors[0]:-1)+" "+
			((membraneNeighbors != null && membraneNeighbors.length > 1)?membraneNeighbors[1]:-1)+" "+
			((membraneNeighbors != null && membraneNeighbors.length > 2)?membraneNeighbors[2]:-1)+" "+
			((membraneNeighbors != null && membraneNeighbors.length > 3)?membraneNeighbors[3]:-1)+
			" Area="+area+" Normal="+getNormal()+" Centroid="+getCentroid());
}
}

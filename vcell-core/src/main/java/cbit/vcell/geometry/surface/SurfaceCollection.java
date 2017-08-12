/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.surface;

import java.util.ArrayList;

import cbit.vcell.geometry.RegionImage.MembraneEdgeNeighbor;

/**
 * Insert the type's description here.
 * Creation date: (6/28/2003 12:10:26 AM)
 * @author: John Wagner
 */
public class SurfaceCollection {
	private Node[] fieldNodes = new Node[0];
	private java.util.Vector<Surface> fieldSurfaces = new java.util.Vector<Surface>();
	private ArrayList<MembraneEdgeNeighbor>[][] membraneEdgeNeighbors;

/**
 * BoundaryCollection constructor comment.
 */
public SurfaceCollection() {
	super();
}


/**
 * BoundaryCollection constructor comment.
 */
public SurfaceCollection(Surface surface) {
	super();
	addSurface(surface);
}

public void setMembraneEdgeNeighbors(ArrayList<MembraneEdgeNeighbor>[][] membraneEdgeNeighbors){
	this.membraneEdgeNeighbors = membraneEdgeNeighbors;
}
public ArrayList<MembraneEdgeNeighbor>[][] getMembraneEdgeNeighbors(){
	return membraneEdgeNeighbors;
}
/**
 * BoundaryCollection constructor comment.
 */
public void addSurface(Surface surface) {
	fieldSurfaces.add(surface);
}


/**
 * BoundaryCollection constructor comment.
 */
public void addSurfaceCollection(SurfaceCollection surfaceCollection) {
	for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++) {
		addSurface(surfaceCollection.getSurfaces(i));
	}
}


/**
 * BoundaryCollection constructor comment.
 */
public int getNodeCount() {
	return(fieldNodes.length);
}

public Surface getSurface(SurfaceGeometricRegion surfaceGeometricRegion){
	GeometricRegion[] adjacentGeometricRegions = surfaceGeometricRegion.getAdjacentGeometricRegions();
	VolumeGeometricRegion volumeRegion1 = (VolumeGeometricRegion)adjacentGeometricRegions[0];
	VolumeGeometricRegion volumeRegion2 = (VolumeGeometricRegion)adjacentGeometricRegions[1];
	
	for (Surface surface : fieldSurfaces){
		if (surface.getExteriorRegionIndex()==volumeRegion1.getRegionID() && surface.getInteriorRegionIndex()==volumeRegion2.getRegionID()){
			return surface;
		}
		if (surface.getExteriorRegionIndex()==volumeRegion2.getRegionID() && surface.getInteriorRegionIndex()==volumeRegion1.getRegionID()){
			return surface;
		}
	}
	return null;
}

/**
 * BoundaryCollection constructor comment.
 */
public Node[] getNodes() {
	return(fieldNodes);
}


/**
 * BoundaryCollection constructor comment.
 */
public Node getNodes(int i) {
	return(fieldNodes[i]);
}


/**
 * BoundaryCollection constructor comment.
 */
public int getSurfaceCount() {
	return(fieldSurfaces.size());
}


/**
 * BoundaryCollection constructor comment.
 */
public Surface getSurfaces(int i) {
	return fieldSurfaces.get(i);
}


/**
 * BoundaryCollection constructor comment.
 */
public void removeSurface(int i) {
	fieldSurfaces.remove(i);
}


/**
 * BoundaryCollection constructor comment.
 */
public void removeSurface(Surface surface) {
	fieldSurfaces.remove(surface);
}


/**
 * BoundaryCollection constructor comment.
 */
public void setNodes(Node[] nodes) {
	fieldNodes = nodes;
	for (int i=0;i<nodes.length;i++){
		nodes[i].setGlobalIndex(i);
	}
}


/**
 * BoundaryCollection constructor comment.
 */
public void setSurfaces(int i, Surface surface) {
	fieldSurfaces.set(i, surface);
}

public int getTotalPolygonCount() {
	int numPolygonCount = 0;
	for (int i = 0; i < getSurfaceCount(); i++){
		numPolygonCount += getSurfaces(i).getPolygonCount();
	}
	return numPolygonCount;
}
}

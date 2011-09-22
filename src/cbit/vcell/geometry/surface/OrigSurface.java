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

/**
 * Insert the type's description here.
 * Creation date: (6/28/2003 12:10:01 AM)
 * @author: John Wagner
 */
public class OrigSurface implements Surface, java.io.Serializable {
	private int fieldInteriorRegionIndex = 0;
	private int fieldExteriorRegionIndex = 0;
	private ArrayList<Polygon> fieldPolygons = new ArrayList<Polygon>();

	// masks are temporary and are used during algorithms.
	// the ray-tracer assigns interior/exterior masks to (1<<(2*N) and 1<<(2*N+1)) for surface N.
	private long interiorMask = 0;
	private long exteriorMask = 0;
/**
 * Boundary constructor comment.
 */
public OrigSurface(int interiorRegionIndex, int exteriorRegionIndex) {
	super();
	fieldInteriorRegionIndex = interiorRegionIndex;
	fieldExteriorRegionIndex = exteriorRegionIndex;
}
/**
 * Boundary constructor comment.
 */
public OrigSurface(int interiorRegionIndex, int exteriorRegionIndex, Polygon polygon) {
	super();
	fieldInteriorRegionIndex = interiorRegionIndex;
	fieldExteriorRegionIndex = exteriorRegionIndex;
	addPolygon(polygon);
}
public long getInteriorMask() {
	return interiorMask;
}
public long getExteriorMask() {
	return exteriorMask;
}
public void setInteriorMask(long mask) {
	this.interiorMask = mask;
}
public void setExteriorMask(long mask) {
	this.exteriorMask = mask;
}
/**
 * Boundary constructor comment.
 */
public void addPolygon(Polygon quadrilateral) {
	fieldPolygons.add(quadrilateral);
}
/**
 * Boundary constructor comment.
 */
public void addSurface(Surface surface) {
	boolean bReverse = !(surface.getExteriorRegionIndex() == fieldExteriorRegionIndex && surface.getInteriorRegionIndex() == fieldInteriorRegionIndex);

	if (bReverse) {
		surface.reverseDirection();
	}
	for (int i = 0; i < surface.getPolygonCount(); i++) {			
		addPolygon(surface.getPolygons(i));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 2:41:40 PM)
 * @return double
 */
public double getArea() {
	double area = 0.0;
	for (int i = 0; i < fieldPolygons.size(); i++){
		area += fieldPolygons.get(i).getArea();
	}
	return area;
}
/**
 * Boundary constructor comment.
 */
public int getExteriorRegionIndex() {
	return(fieldExteriorRegionIndex);
}
/**
 * Boundary constructor comment.
 */
public int getInteriorRegionIndex() {
	return(fieldInteriorRegionIndex);
}
/**
 * Boundary constructor comment.
 */
public int getPolygonCount() {
	return(fieldPolygons.size());
}
/**
 * Boundary constructor comment.
 */
public Polygon getPolygons(int i) {
	return fieldPolygons.get(i);
}
/**
 * Quadrilateral constructor comment.
 */
public void reverseDirection() {
	int regionIndex = fieldInteriorRegionIndex;
	fieldInteriorRegionIndex = fieldExteriorRegionIndex;
	fieldExteriorRegionIndex = regionIndex;
	for (int i = 0; i < getPolygonCount(); i++) {
		getPolygons(i).reverseDirection();
	}
}
}

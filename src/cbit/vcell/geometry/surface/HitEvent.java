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
import cbit.vcell.render.Vect3d;
/**
 * Insert the type's description here.
 * Creation date: (7/20/2004 12:08:18 PM)
 * @author: Jim Schaff
 */
public class HitEvent implements Comparable<HitEvent> {
	private cbit.vcell.geometry.surface.Surface surface = null;
	private cbit.vcell.geometry.surface.Polygon polygon = null;
	private double unitNormalInRayDirection;
	private double hitRayZ;
	private double centroidZ;
	private final String debugMessage;

	/**
	 * HitEvent constructor comment.
	 */
	public HitEvent(Surface argSurface, Polygon argPolygon, double argUnitNormalInRayDirection, double argRayZ, double centroidZ, String debugMessage) {
		super();
		this.surface = argSurface;
		this.polygon = argPolygon;
		this.unitNormalInRayDirection = argUnitNormalInRayDirection;
//		System.out.println("rayU="+argRayZ+", unitInRayDirection="+argUnitNormalInRayDirection);
		this.hitRayZ = argRayZ;
		this.centroidZ = centroidZ;
		this.debugMessage = debugMessage;
	}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 12:24:06 PM)
 * @return double
 */
	public double getHitRayZ() {
		return hitRayZ;
	}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 12:24:06 PM)
 * @return cbit.vcell.geometry.surface.Polygon
 */
public cbit.vcell.geometry.surface.Polygon getPolygon() {
	return polygon;
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 12:24:06 PM)
 * @return cbit.vcell.geometry.surface.Surface
 */
public cbit.vcell.geometry.surface.Surface getSurface() {
	return surface;
}

public double getUnitNormalInRayDirection(){
	return unitNormalInRayDirection;
}

public int compareTo(HitEvent o) {
	if (hitRayZ < o.hitRayZ){
		return -1;
	}else if (hitRayZ > o.hitRayZ){
		return 1;
	}else{
		return 0;
	}
}

public double getCentroidZ() {
	return centroidZ;
}

public String getDebugMessage(){
	return this.debugMessage;
}

public String toString(){
	Vect3d unitNormal = new Vect3d();
	polygon.getUnitNormal(unitNormal);
	return "HitEvent(rayZ="+hitRayZ+", centroidZ="+centroidZ+", unitNormalInRayDirection="+unitNormalInRayDirection+", polygon="+polygon.toString()+", normal="+unitNormal+", surface="+surface.toString()+", interiorMask="+surface.getInteriorMask()+", exteriorMask="+surface.getExteriorMask()+", extraMessage=\""+debugMessage+"\"";
}
}

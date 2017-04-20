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
import cbit.vcell.matrix.RationalNumber;
import cbit.vcell.render.Vect3d;

public class HitEventRational implements Comparable<HitEventRational> {
	private cbit.vcell.geometry.surface.Surface surface = null;
	private cbit.vcell.geometry.surface.Polygon polygon = null;
	// when testing with exact arithmetic only
	private RationalNumber unitNormalInRayDirection;
	private RationalNumber hitRayZ;
	private	RationalNumber centroidZ;
	private final String debugMessage;

	/**
	 * HitEvent constructor comment.
	 */
	public HitEventRational(Surface argSurface, Polygon argPolygon, RationalNumber argUnitNormalInRayDirection, RationalNumber argRayZ, RationalNumber centroidZ, String debugMessage) {
		super();
		this.surface = argSurface;
		this.polygon = argPolygon;
		this.unitNormalInRayDirection = argUnitNormalInRayDirection;
//		System.out.println("rayU="+argRayZ+", unitInRayDirection="+argUnitNormalInRayDirection);
		this.hitRayZ = argRayZ;
		this.centroidZ = centroidZ;
		this.debugMessage = debugMessage;
	}

	
	public cbit.vcell.geometry.surface.Polygon getPolygon() {
		return polygon;
	}
	
	public cbit.vcell.geometry.surface.Surface getSurface() {
		return surface;
	}
	
	public int compareTo(HitEventRational o) {
		if (hitRayZ.lt(o.hitRayZ)){
			return -1;
		}else if (hitRayZ.gt(o.hitRayZ)){
			return 1;
		}else{
			return 0;
		}
	}
	
	public String getDebugMessage(){
		return this.debugMessage;
	}
	
	public RationalNumber getHitRayZ() {
		return hitRayZ;
	}
	
	public RationalNumber getCentroidZ() {
		return centroidZ;
	}
	
	public RationalNumber getUnitNormalInRayDirection(){
		return unitNormalInRayDirection;
	}
	
	public String toString(){
		Vect3d unitNormal = new Vect3d();
		polygon.getUnitNormal(unitNormal);
		return "HitEvent(rayZ="+getHitRayZ()+", centroidZ="+getCentroidZ()+", unitNormalInRayDirection="+getUnitNormalInRayDirection()+", polygon="+polygon.toString()+", normal="+unitNormal+", surface="+surface.toString()+", interiorMask="+surface.getInteriorMask()+", exteriorMask="+surface.getExteriorMask()+", extraMessage=\""+debugMessage+"\"";
	}
}

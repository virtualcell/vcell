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

import org.vcell.util.Matchable;

import cbit.vcell.render.Vect3d;

@SuppressWarnings("serial")
public class CSGPrimitive extends CSGNode {
	// all objects fit in unit cube of (-1,-1,-1) to (1,1,1)
	public static enum PrimitiveType {
		CONE,			// (i.e. y^2+z^2 < (0.5*(x-1))^2 and x<1 and x>-1)
		CUBE,			// (i.e. x>=-1 and x<=1 and y>=-1 and y<=1 and z>=-1 and z<=1 
		CYLINDER,		// (i.e. y^2+z^2 < 1 and x<1 and x>-1)
		SPHERE,		// (i.e. x^2+y^2+z^2 <= 1)
	};
	
	private PrimitiveType type = null;

	public CSGPrimitive(String name, PrimitiveType type) {
		super(name);
		this.type = type;
	}

	public CSGPrimitive(CSGPrimitive primitive) {
		this(primitive.getName(), primitive.type);
	}

	public boolean compareEqual(Matchable obj) {
		if (!compareEqual0(obj)){
			return false;
		}
		if (!(obj instanceof CSGPrimitive)){
			return false;
		}
		CSGPrimitive csgp = (CSGPrimitive)obj;

		if ((getType().compareTo(csgp.getType())) != 0){
			return false;
		}

		return true;
	}
	
	@Override
	public CSGNode clone() {
		return new CSGPrimitive(this);
	}
	
	@Override
	public boolean isInside(Vect3d point) {
		// test if inside bounding box
		double x = point.getX();
		double y = point.getY();
		double z = point.getZ();

		if (x<-1.0){
			return false;
		}
		if (x>1.0){
			return false;
		}
		if (y<-1.0){
			return false;
		}
		if (y>1.0){
			return false;
		}
		if (z<-1.0){
			return false;
		}
		if (z>1.0){
			return false;
		}
		switch (type){
		case SPHERE: {
			double radiusSquared = point.lengthSquared();
			return radiusSquared <= 1.0;
		}
		case CYLINDER: {
			// already inside unit cube, so only test inside circle
			double radiusOfCircleSquared = y*y+z*z;
			return radiusOfCircleSquared <= 1.0;
		}
		case CONE: {
			// already inside unit cube, so only test inside circle
			double radius = 0.5*(x-1);
			return y*y+z*z < radius*radius;
		}
		case CUBE: {
			return true;
		}
		default:
			throw new RuntimeException("unknown CSG primative type");
		}
	}

	public PrimitiveType getType() {
		return type;
	}


}

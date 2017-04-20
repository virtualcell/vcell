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

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.render.Affine;
import cbit.vcell.render.Vect3d;

@SuppressWarnings("serial")
public class CSGRotation extends CSGTransformation {
	
	private Vect3d axis = null;
	private double rotationRadians = 0;
	
	public CSGRotation(String name, Vect3d axis, double rotationRadians) {
		super(name);
		this.axis = axis;
		this.rotationRadians = rotationRadians;
		updateTransform(axis, rotationRadians);
	}
	
	public CSGRotation(CSGRotation rotation) {
		super(rotation);
		this.axis = new Vect3d(rotation.axis);
		this.rotationRadians = rotation.rotationRadians;
		updateTransform(axis, rotationRadians);
	}
	
	public boolean compareEqual(Matchable obj) {
		if (!compareEqual0(obj)){
			return false;
		}
		if (!(obj instanceof CSGRotation)){
			return false;
		}
		CSGRotation csgr = (CSGRotation)obj;

		if (!(getChild().compareEqual(csgr.getChild()))){
			return false;
		}
		
		if (!(Compare.isEqual(rotationRadians, csgr.rotationRadians))){
			return false;
		}

		if (!(Compare.isEqual(axis.getX(), csgr.axis.getX()))){
			return false;
		}
		if (!(Compare.isEqual(axis.getY(), csgr.axis.getY()))){
			return false;
		}
		if (!(Compare.isEqual(axis.getZ(), csgr.axis.getZ()))){
			return false;
		}

		return true;
	}

	@Override
	public CSGNode clone() {
		return new CSGRotation(this);
	}
	
	private void updateTransform(Vect3d axis, double rotationRadians) {
		Affine forward = new Affine();
		forward.setRotate(axis, rotationRadians);
		Affine inverse = new Affine();
		inverse.setRotate(axis, -rotationRadians);
		setTransforms(forward, inverse);
	}
		
	public Vect3d getAxis() {
		return axis;
	}

	public void setAxis(Vect3d axis) {
		this.axis = axis;
		updateTransform(axis, rotationRadians);
	}

	public double getRotationRadians() {
		return rotationRadians;
	}

	public void setRotationRadians(double rotationRadians) {
		this.rotationRadians = rotationRadians;
		updateTransform(axis, rotationRadians);
	}


}

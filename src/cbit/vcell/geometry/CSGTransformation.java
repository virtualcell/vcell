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

import cbit.vcell.render.Affine;
import cbit.vcell.render.Vect3d;

@SuppressWarnings("serial")
public abstract class CSGTransformation extends CSGNode {
	private Affine forwardTransform = null;
	private Affine inverseTransform = null;
	private CSGNode child = null;
	
	public enum TransformationType {
		Homogeneous,
		Rotation,
		Scale,
		Translation,
	}
	CSGTransformation(String name, Affine forward, Affine inverse){
		super(name);
		this.forwardTransform = forward;
		this.inverseTransform = inverse;
	}
	
	CSGTransformation(String name){
		super(name);
		this.forwardTransform = new Affine();
		forwardTransform.identity();
		this.inverseTransform = new Affine();
		inverseTransform.identity();
	}
	
	CSGTransformation(CSGTransformation csgTransformation){
		this(csgTransformation.getName(), new Affine(csgTransformation.forwardTransform), new Affine(csgTransformation.inverseTransform));
		if (csgTransformation.getChild() != null) {
			setChild(csgTransformation.getChild().clone());
		}
	}
		
	public Affine getForwardTransform() {
		return forwardTransform;
	}

	public Affine getInverseTransform() {
		return inverseTransform;
	}

	public CSGNode getChild() {
		return child;
	}

	public void setChild(CSGNode child) {
		this.child = child;
	}

	void setTransforms(Affine forward, Affine inverse) {
		this.forwardTransform = forward;
		this.inverseTransform = inverse;
	}

	public boolean isInside(Vect3d point) {
		if (child == null) {
			return false;
		}
		Vect3d transformedPoint = inverseTransform.mult(point);
		return child.isInside(transformedPoint);
	}
}

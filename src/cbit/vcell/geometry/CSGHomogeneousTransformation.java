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

import cbit.vcell.render.Affine;

@SuppressWarnings("serial")
public class CSGHomogeneousTransformation extends CSGTransformation {
		
	public CSGHomogeneousTransformation(String name, Affine forward, Affine inverse){
		super(name, forward,inverse);
	}
	
	public CSGHomogeneousTransformation(CSGHomogeneousTransformation csgHomogeneousTransformation){
		super(csgHomogeneousTransformation);
		setTransforms(new Affine(csgHomogeneousTransformation.getForwardTransform()),new Affine(csgHomogeneousTransformation.getInverseTransform()));
	}

/**
 * @TODO : To compare forward and reverse affine transform or not??
 */
	public boolean compareEqual(Matchable obj) {
		if (!compareEqual0(obj)){
			return false;
		}
		if (!(obj instanceof CSGHomogeneousTransformation)){
			return false;
		}
		// CSGHomogeneousTransformation csght = (CSGHomogeneousTransformation)obj;

		return true;
	}

	@Override
	public CSGNode clone() {
		return new CSGHomogeneousTransformation(this);
	}
	
	public void setTransformations(Affine forward, Affine inverse){
		setTransforms(forward, inverse);
	}

}

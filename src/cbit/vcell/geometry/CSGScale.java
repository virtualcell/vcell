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
public class CSGScale extends CSGTransformation {

	private Vect3d scale = null;

	public CSGScale(String name, Vect3d scale) {
		super(name);
		this.scale = new Vect3d(scale);
		updateTransform();
	}

	public CSGScale(CSGScale csgScale) {
		super(csgScale);
		scale = new Vect3d(csgScale.scale);
		updateTransform();
	}

	public boolean compareEqual(Matchable obj) {
		if (!compareEqual0(obj)){
			return false;
		}
		if (!(obj instanceof CSGScale)){
			return false;
		}
		CSGScale csgs = (CSGScale)obj;

		if (!(getChild().compareEqual(csgs.getChild()))){
			return false;
		}
		
		if (!(Compare.isEqual(scale.getX(), csgs.scale.getX()))){
			return false;
		}
		if (!(Compare.isEqual(scale.getY(), csgs.scale.getY()))){
			return false;
		}
		if (!(Compare.isEqual(scale.getZ(), csgs.scale.getZ()))){
			return false;
		}

		return true;
	}

	@Override
	public CSGNode clone() {
		return new CSGScale(this);
	}

	public Vect3d getScale() {
		return scale;
	}

	public void setScale(Vect3d scale) {
		this.scale = scale;
		updateTransform();
	}
	
	private void updateTransform(){
		Affine forward = new Affine();
		forward.setScale(scale);
		Affine inverse = new Affine();
		inverse.setScale(new Vect3d(1.0/scale.getX(),1.0/scale.getY(),1.0/scale.getZ()));
		setTransforms(forward, inverse);
	}

}

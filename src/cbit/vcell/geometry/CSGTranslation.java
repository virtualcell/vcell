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
public class CSGTranslation extends CSGTransformation {
	
	public Vect3d translation = null;

	public CSGTranslation(String name, Vect3d translation){
		super(name);
		this.translation = translation;
		updateTransformation();
	}
	
	public CSGTranslation(CSGTranslation csgTranslation){
		super(csgTranslation);
		this.translation = new Vect3d(csgTranslation.getTranslation());
		updateTransformation();
	}
	
	public boolean compareEqual(Matchable obj) {
		if (!compareEqual0(obj)){
			return false;
		}
		if (!(obj instanceof CSGTranslation)){
			return false;
		}
		CSGTranslation csgs = (CSGTranslation)obj;

		if (!(getChild().compareEqual(csgs.getChild()))){
			return false;
		}
		
		if (!(Compare.isEqual(translation.getX(), csgs.translation.getX()))){
			return false;
		}
		if (!(Compare.isEqual(translation.getY(), csgs.translation.getY()))){
			return false;
		}
		if (!(Compare.isEqual(translation.getZ(), csgs.translation.getZ()))){
			return false;
		}

		return true;
	}
	
	@Override
	public CSGNode clone() {
		return new CSGTranslation(this);
	}

	public Vect3d getTranslation() {
		return translation;
	}

	public void setTranslation(Vect3d translation) {
		this.translation = translation;
	}
	
	private void updateTransformation(){
		Affine forward = new Affine();
		forward.setTranslate(translation);
		Affine inverse = new Affine();
		inverse.setTranslate(translation.uminus());
		setTransforms(forward,inverse);
	}

}

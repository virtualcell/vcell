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

import cbit.vcell.render.Vect3d;

@SuppressWarnings("serial")
public class CSGPseudoPrimitive extends CSGNode {
	private String csgObjectName = null;
	private CSGObject csgObject = null;

	public CSGPseudoPrimitive(String name, String csgObjectName){
		super(name);
		this.csgObjectName = csgObjectName;
	}
	
	public CSGPseudoPrimitive(String name, CSGObject csgObject){
		this(name, csgObject.getName());
	}
	
	public CSGPseudoPrimitive(CSGPseudoPrimitive csgPseudoPrimitive){
		this(csgPseudoPrimitive.getName(), csgPseudoPrimitive.csgObjectName);
	}
	
	public boolean compareEqual(Matchable obj) {
		if (!compareEqual0(obj)){
			return false;
		}
		if (!(obj instanceof CSGPseudoPrimitive)){
			return false;
		}
		CSGPseudoPrimitive csgpp = (CSGPseudoPrimitive)obj;

		if (!Compare.isEqual(csgObjectName, csgpp.csgObjectName)){
			return false;
		}

		if (!csgObject.compareEqual(csgpp.csgObject)) {
			return false;
		}
		
		return true;
	}

	@Override
	public CSGNode clone() {
		return new CSGPseudoPrimitive(this);
	}

	@Override
	public boolean isInside(Vect3d point) {
		return csgObject.getRoot().isInside(point);
	}

	public String getCsgObjectName() {
		return csgObjectName;
	}

	public void setCsgObjectName(String csgObjectName) {
		this.csgObjectName = csgObjectName;
	}

	public CSGObject getCsgObject() {
		return csgObject;
	}

	public void setCsgObject(CSGObject csgObject) {
		this.csgObject = csgObject;
	}
}

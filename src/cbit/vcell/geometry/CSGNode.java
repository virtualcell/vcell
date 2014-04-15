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

import java.io.Serializable;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.render.Vect3d;

@SuppressWarnings("serial")
public abstract class CSGNode implements Matchable, Serializable {
	private String name;	
	protected CSGNode(String name) {
		this.name = name;
	}
	
	public abstract boolean isInside(Vect3d point);
	public abstract CSGNode clone();

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}
	
	protected boolean compareEqual0(Matchable obj) {
		if (!(obj instanceof CSGNode)) {
			return false;
		}
		
		CSGNode csgn = (CSGNode)obj;
		if (!Compare.isEqual(name, csgn.name)){
			return false;
		}
		
		return true;
	}

}

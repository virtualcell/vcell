/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;
import cbit.vcell.geometry.surface.Polygon;
import cbit.vcell.geometry.surface.Surface;
/**
 * Insert the type's description here.
 * Creation date: (7/20/2004 12:08:18 PM)
 * @author: Jim Schaff
 */
public class HitEvent implements Comparable<HitEvent> {
	private cbit.vcell.geometry.surface.Surface surface = null;
	private cbit.vcell.geometry.surface.Polygon polygon = null;
	private boolean entering;
	private double hitRayZ;

/**
 * HitEvent constructor comment.
 */
public HitEvent(Surface argSurface, Polygon argPolygon, boolean argEntering, double argRayZ) {
	super();
	this.surface = argSurface;
	this.polygon = argPolygon;
	this.entering = argEntering;
	this.hitRayZ = argRayZ;
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


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 12:24:06 PM)
 * @return boolean
 */
public boolean isEntering() {
	return entering;
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
}

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

import java.util.Iterator;

/**
 * Insert the type's description here.
 * Creation date: (5/14/2004 4:58:39 PM)
 * @author: Jim Schaff
 */
public interface Surface extends Iterable<Polygon> {
/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:06:00 PM)
 * @param newSurface cbit.vcell.geometry.surface.Surface
 */
void addSurface(Surface newSurface);


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:06:16 PM)
 * @return double
 */
double getArea();


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:05:26 PM)
 * @return int
 */
int getExteriorRegionIndex();


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:05:11 PM)
 * @return int
 */
int getInteriorRegionIndex();

/**
 * masks are temporary and are used during algorithms.
 * the ray-tracer assigns interior/exterior masks to (1<<(2*N) and 1<<(2*N+1)) for surface N.
*/
long getInteriorMask();
long getExteriorMask();
void setInteriorMask(long mask);
void setExteriorMask(long mask);
/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 4:59:38 PM)
 * @return int
 */
int getPolygonCount();


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:01:09 PM)
 * @return cbit.vcell.geometry.surface.Polygon
 * @param index int
 */
Polygon getPolygons(int index);


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:05:40 PM)
 */
void reverseDirection();

default Iterator<Polygon> iterator( ) {
	return new SurfacePolygonIteratorAdapter(this);
	
}
}

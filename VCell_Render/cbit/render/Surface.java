package cbit.render;

import cbit.render.objects.Polygon;

/**
 * Insert the type's description here.
 * Creation date: (5/14/2004 4:58:39 PM)
 * @author: Jim Schaff
 */
public interface Surface {
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
}
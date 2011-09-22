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
/**
 * Insert the type's description here.
 * Creation date: (5/14/2004 4:54:24 PM)
 * @author: Jim Schaff
 */
public interface Polygon {
/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 2:14:34 PM)
 * @return double
 */
public double getArea();


/**
 * Quadrilateral constructor comment.
 */
public int getNodeCount();


/**
 * Quadrilateral constructor comment.
 */
public Node[] getNodes();


/**
 * Quadrilateral constructor comment.
 */
public Node getNodes(int n);


/**
 * Insert the method's description here.
 * Creation date: (7/7/2004 1:40:21 PM)
 * @param unitNormal cbit.vcell.render.Vect3d
 */
void getUnitNormal(cbit.vcell.render.Vect3d unitNormal);


/**
 * Quadrilateral constructor comment.
 */
public void reverseDirection();
}


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
 * Creation date: (6/28/2003 12:08:02 AM)
 * @author: John Wagner
 */
public class Quadrilateral extends AbstractPolygon {
	private int volIndexNeighbor1;
	private int volIndexNeighbor2;
/**
 * Quadrilateral constructor comment.
 */
public Quadrilateral(Node[] nodes, int argVolIndexNeighbor1, int argVolIndexNeighbor2) {
	super(nodes);
	volIndexNeighbor1 = argVolIndexNeighbor1;
	volIndexNeighbor2 = argVolIndexNeighbor2;
}
/**
 * Quadrilateral constructor comment.
 */
public Quadrilateral(Node n0, Node n1, Node n2, Node n3) {
	super(new Node[] {n0, n1, n2, n3});
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2004 12:46:59 PM)
 * @return int
 */
public int getVolIndexNeighbor1() {
	return volIndexNeighbor1;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2004 12:46:59 PM)
 * @return int
 */
public int getVolIndexNeighbor2() {
	return volIndexNeighbor2;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2004 12:46:59 PM)
 * @return int
 */
public void reverseDirection() {
	super.reverseDirection();
	int index = volIndexNeighbor1;
	volIndexNeighbor1 = volIndexNeighbor2;
	volIndexNeighbor2 = index;
}
}

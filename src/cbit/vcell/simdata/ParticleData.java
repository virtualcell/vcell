/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;

import org.vcell.util.Coordinate;
/**
 * This type was created in VisualAge.
 */
public class ParticleData implements java.io.Serializable {
	private Coordinate coordinate = null;
	private int state = 0;
	private int context = 0;
/**
 * This method was created in VisualAge.
 * @param coordinate cbit.vcell.geometry.Coordinate
 * @param state int
 * @param context int
 */
public ParticleData(Coordinate coordinate, int state, int context) {
	this.coordinate = coordinate;
	this.state = state;
	this.context = context;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
/*
public int getContext() {
	return context;
}
*/
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.Coordinate
 */
public Coordinate getCoordinate() {
	return coordinate;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
/*
public int getState() {
	return state;
}
*/
}

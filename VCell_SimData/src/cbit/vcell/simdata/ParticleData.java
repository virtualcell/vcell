package cbit.vcell.simdata;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.util.Coordinate;

import cbit.vcell.geometry.*;
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
public int getContext() {
	return context;
}
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
public int getState() {
	return state;
}
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.test;

/**
 * Insert the type's description here.
 * Creation date: (1/16/2003 2:24:27 PM)
 * @author: Jim Schaff
 */
public class TimeSeriesSample {
	private double fieldTime[] = null;
	private double fieldData[] = null;
/**
 * TimeSeriesSample constructor comment.
 */
public TimeSeriesSample(double time[], double data[]) {
	super();
	this.fieldTime = time;
	this.fieldData = data;
}
/**
 * Insert the method's description here.
 * Creation date: (1/16/2003 2:25:51 PM)
 * @return double[]
 */
public double[] getData() {
	return fieldData;
}
/**
 * Insert the method's description here.
 * Creation date: (1/16/2003 2:25:41 PM)
 * @return double[]
 */
public double[] getTimes() {
	return fieldTime;
}
}

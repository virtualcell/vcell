/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.document;
/**
 * Insert the type's description here.
 * Creation date: (2/22/2006 7:45:09 AM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class TSJobResultsSpaceStats extends TimeSeriesJobResults {
	
	private double[][] min;
	private double[][] max;
	private double[][] unweightedMean;
	private double[][] weightedMean = null;
	private double[] totalSpace = null;
	private double[][] unweightedSum;
	private double[][] weightedSum;

/**
 * TSJobResultsSpaceStats constructor comment.
 * @param argVariableNames java.lang.String[]
 * @param argIndices int[][]
 * @param argTimes double[]
 */
public TSJobResultsSpaceStats(
	java.lang.String[] argVariableNames,
    int[][] argIndices,
    double[] argTimes,
    double[][] argMin,
    double[][] argMax,
    double[][] argUnweightedMean,
    double[][] argWeightedMean,
    double[][] argUnweightedSum,
    double[][] argWeightedSum,
    double[] argTotalSpace) {

	super(argVariableNames, argIndices, argTimes);
    min = argMin;
    max = argMax;
    unweightedMean = argUnweightedMean;
    weightedMean = argWeightedMean;
    unweightedSum = argUnweightedSum;
    weightedSum = argWeightedSum;
    totalSpace = argTotalSpace;
}


/**
 * Insert the method's description here.
 * Creation date: (2/22/2006 8:51:31 AM)
 * @return double[][]
 */
public double[][] getMaximums() {
	return max;
}


/**
 * Insert the method's description here.
 * Creation date: (2/22/2006 8:51:31 AM)
 * @return double[][]
 */
public double[][] getMinimums() {
	return min;
}


/**
 * Insert the method's description here.
 * Creation date: (3/21/2006 1:39:03 AM)
 */
public double[] getTotalSpace() {
	return totalSpace;
}


/**
 * Insert the method's description here.
 * Creation date: (2/22/2006 8:51:31 AM)
 * @return double[][]
 */
public double[][] getUnweightedMean() {
	return unweightedMean;
}

public double[][] getUnweightedSum() {
	return unweightedSum;
}

/**
 * Insert the method's description here.
 * Creation date: (2/22/2006 8:51:31 AM)
 * @return double[][]
 */
public double[][] getWeightedMean() {
	return weightedMean;
}

public double[][] getWeightedSum() {
	return weightedSum;
}

}

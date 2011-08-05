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
 * Creation date: (2/21/2006 1:53:02 PM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class TSJobResultsTimeStats extends TimeSeriesJobResults {

	private double[][] min;
	private double[][] max;
	private double[][] unweightedMean;
	private double[][] weightedMean = null;
	private boolean valuesAreSpaceStats = false;

/**
 * TSJobResultsTimeStats constructor comment.
 * @param argVariableNames java.lang.String[]
 * @param argIndices int[][]
 * @param argTimes double[]
 */
public TSJobResultsTimeStats(
    java.lang.String[] argVariableNames,
    int[][] argIndices,
    double[] argTimes,
    double[][] argMin,
    double[][] argMax,
    double[][] argUnweightedMean) {

	//TimeStats only, no space stats
    super(argVariableNames, argIndices, argTimes);
    min = argMin;
    max = argMax;
    unweightedMean = argUnweightedMean;
    checkConsistency();
}


/**
 * TSJobResultsTimeStats constructor comment.
 * @param argVariableNames java.lang.String[]
 * @param argIndices int[][]
 * @param argTimes double[]
 */
public TSJobResultsTimeStats(
    java.lang.String[] argVariableNames,
    int[][] argIndices,
    double[] argTimes,
    double[] argMin,
    double[] argMax,
    double[] argUnweightedMean,
    double[] argWeightedMean) {

	//Time stats and space stats
    super(argVariableNames, argIndices, argTimes);
    min = makeVarNameIndexCompatible(argMin);
    max = makeVarNameIndexCompatible(argMax);
    unweightedMean = makeVarNameIndexCompatible(argUnweightedMean);
    weightedMean = makeVarNameIndexCompatible(argWeightedMean);
    valuesAreSpaceStats = true;
    checkConsistency();
}


/**
 * Insert the method's description here.
 * Creation date: (2/21/2006 3:28:23 PM)
 */
private void checkConsistency() {
	
    if(min.length != getVariableNames().length ||
	    max.length != getVariableNames().length ||
	    unweightedMean.length != getVariableNames().length ||
	    (weightedMean != null?weightedMean.length != getVariableNames().length:false)){
		    throw new IllegalArgumentException("Spatially Ungrouped TimeStats arrays must be same length as Variables array");
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (2/21/2006 2:15:48 PM)
 */
public double[][] getMaximums() {

	return max;
}


/**
 * Insert the method's description here.
 * Creation date: (2/21/2006 2:15:48 PM)
 */
public double[][] getMinimums() {

	return min;
}


/**
 * Insert the method's description here.
 * Creation date: (2/21/2006 2:15:48 PM)
 */
public double[][] getUnweightedMeans() {

	//
	//(1) isValuesAreSpaceStats() == false && isValuesAreSpaceStatsGrouped() == false
	//     getSpaceAOIGroups() will be NULL
	//     Interpret like getIndices()
	//
	//(2) isValuesAreSpaceStats() == TRUE && isValuesAreSpaceStatsGrouped() == false
	//     getSpaceAOIGroups() will be NULL
	//     Interpret double[getVariableNames().length][1] stat condensed for time and space for each variable
	//
	//(3) isValuesAreSpaceStats() == TRUE && isValuesAreSpaceStatsGrouped() == TRUE
	//     getSpaceAOIGroups() will be NON NULL
	//     Interpret double[getVariableNames().length][getSpaceAOIGroups()[varNameIndex].length] stat condensed for time and spacegroup for each variable
	
	return unweightedMean;
}


/**
 * Insert the method's description here.
 * Creation date: (2/21/2006 2:15:48 PM)
 */
public double[][] getWeightedMeans() {

	if(!isValuesAreSpaceStats()){
		throw new RuntimeException("weightedMeans are not valid for these TimeStats results because SpatialStats were not set");
	}
	return weightedMean;
}


/**
 * Insert the method's description here.
 * Creation date: (2/21/2006 3:16:06 PM)
 */
public boolean isValuesAreSpaceStats() {
	
	return valuesAreSpaceStats;
}


/**
 * Insert the method's description here.
 * Creation date: (2/22/2006 8:23:43 AM)
 */
private double[][] makeVarNameIndexCompatible(double[] values) {

	if(values.length != getVariableNames().length){
		throw new RuntimeException(this.getClass().getName()+" Can't make varNameIndexCompatible");
	}
	double[][] varNameCompatible = new double[values.length][1];
	for(int i=0;i<values.length;i+= 1){
		varNameCompatible[i][0] = values[i];
	}

	return varNameCompatible;
}
}

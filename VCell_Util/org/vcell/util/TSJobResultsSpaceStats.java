package org.vcell.util;
/**
 * Insert the type's description here.
 * Creation date: (2/22/2006 7:45:09 AM)
 * @author: Frank Morgan
 */
public class TSJobResultsSpaceStats extends TimeSeriesJobResults {
	
	private double[][] min;
	private double[][] max;
	private double[][] unweightedMean;
	private double[][] weightedMean = null;
	private double[] totalSpace = null;

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
    double[] argTotalSpace) {

	super(argVariableNames, argIndices, argTimes);
    min = argMin;
    max = argMax;
    unweightedMean = argUnweightedMean;
    weightedMean = argWeightedMean;
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


/**
 * Insert the method's description here.
 * Creation date: (2/22/2006 8:51:31 AM)
 * @return double[][]
 */
public double[][] getWeightedMean() {
	return weightedMean;
}
}
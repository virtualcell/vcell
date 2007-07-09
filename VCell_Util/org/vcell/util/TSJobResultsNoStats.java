package org.vcell.util;
/**
 * Insert the type's description here.
 * Creation date: (2/21/2006 1:43:07 PM)
 * @author: Frank Morgan
 */
public class TSJobResultsNoStats extends TimeSeriesJobResults {

	private double[][][] values;

/**
 * TSJobResultsNoStats constructor comment.
 * @param argVariableNames java.lang.String[]
 * @param argIndices int[][]
 * @param argTimes double[]
 */
public TSJobResultsNoStats(java.lang.String[] argVariableNames, int[][] argIndices, double[] argTimes,double[][][] argValues) {
	super(argVariableNames, argIndices, argTimes);
	values = argValues;
}


/**
 * Insert the method's description here.
 * Creation date: (2/21/2006 1:44:53 PM)
 */
public double[][] getTimesAndValuesForVariable(String variableName) {
	
	return values[getIndexForVarName(variableName)];
}
}
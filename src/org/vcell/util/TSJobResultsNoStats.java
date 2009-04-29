package org.vcell.util;
/**
 * Insert the type's description here.
 * Creation date: (2/21/2006 1:43:07 PM)
 * @author: Frank Morgan
 */
public class TSJobResultsNoStats extends TimeSeriesJobResults {

	//
	//values encoding: double[varNameIndex][dataIndex+1][timeIndex]
	//for each varNameIndex:
	//		double[0][0...numTimes-1] contains the times
	//		double[1...numIndexes][0...numTimes-1] contains the data values at each index for times
	//
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
	//
	//double[numIndexes+1][numTimes]
	//double[0][0...numTimes-1] contains the times
	//double[1...numIndexes][0...numTimes-1] contains the data values at each index for times
	//
	return values[getIndexForVarName(variableName)];
}
}
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
 * Creation date: (12/22/2004 7:35:26 AM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public abstract class TimeSeriesJobResults implements java.io.Serializable{

	private String[] variableNames;
	private int[][] indices;
	private double[] times;

	//public static class TSJRStats{
		//private double min;
		//private double max;
		//private double unweightedMean;

		//public TSJRStats(double argMin,double argMax,double argUnweightedMean){
			//min = argMin;
			//max = argMax;
			//unweightedMean = argUnweightedMean;
		//}
		//public double getMin(){
			//return min;
		//}
		//public double getMax(){
			//return max;
		//}
		//public double getUnweightedMean(){
			//return unweightedMean;
		//}
	//};

	//public static class TSJRStatsVolume extends TSJRStats{
		//public TSJRStatsVolume(double argMin,double argMax,double argUnweightedMean,double){
			//super(argMin,argMax,argUnweightedMean);
		//}
	//};

/**
 * TimeSeriesJobResults constructor comment.
 */
public TimeSeriesJobResults(String[] argVariableNames,int[][] argIndices,double[] argTimes) {

	variableNames = argVariableNames;
	indices = argIndices;
	times = argTimes;
}


/**
 * Insert the method's description here.
 * Creation date: (2/21/2006 2:13:39 PM)
 */
public int getIndexForVarName(String varName) {
	
	for(int i = 0;i<variableNames.length; i+= 1){
		if(variableNames[i].equals(varName)){
			return i;
		}
	}

	throw new IllegalArgumentException("Couldn't find "+varName);
	
}


/**
 * Insert the method's description here.
 * Creation date: (12/22/2004 7:49:28 AM)
 * @return int[][]
 */
public int[][] getIndices() {
	return indices;
}


/**
 * Insert the method's description here.
 * Creation date: (12/22/2004 7:49:28 AM)
 * @return double[]
 */
public double[] getTimes() {
	return times;
}


/**
 * Insert the method's description here.
 * Creation date: (12/22/2004 7:49:28 AM)
 * @return java.lang.String[]
 */
public java.lang.String[] getVariableNames() {
	return variableNames;
}
}

package cbit.util;

import java.rmi.dgc.VMID;
import java.util.BitSet;

import cbit.vcell.solvers.CartesianMesh;

/**
 * Insert the type's description here.
 * Creation date: (12/22/2004 7:34:33 AM)
 * @author: Frank Morgan
 */
public class TimeSeriesJobSpec implements java.io.Serializable{
	
	private String[] variableNames;
	private int[][] indices;// int[varNameIndex][varValIndex]
	private double startTime;
	private int step;
	private double endTime;
	private boolean calcSpaceStats = false;//Calc stats over space for each timepoint
	private boolean calcTimeStats = false;
	private BitSet[] roi;
	private VCDataJobID vcDataJobID;


/**
 * TimeSeriesSpec constructor comment.
 */
public TimeSeriesJobSpec(String[] argVariableNames,int[][] argIndices,double argStartTime,int argStep,double argEndTime,VCDataJobID argVCDataJobID) {

	variableNames = argVariableNames;
	indices = argIndices;
	startTime = argStartTime;
	step = argStep;
	endTime = argEndTime;
	this.vcDataJobID = argVCDataJobID;
}


/**
 * TimeSeriesSpec constructor comment.
 */
public TimeSeriesJobSpec(
    String[] argVariableNames,
    int[][] argIndices,
    double argStartTime,
    int argStep,
    double argEndTime,
    boolean argBCalcSpaceStats,
    boolean argBCalcTimeStats,
    VCDataJobID argVCDataJobID) {
	    
    this(argVariableNames, argIndices, argStartTime, argStep, argEndTime,argVCDataJobID);

    calcSpaceStats = argBCalcSpaceStats;
    calcTimeStats = argBCalcTimeStats;
}
public TimeSeriesJobSpec(
	    String[] argVariableNames,
	    BitSet[] argROI,
	    double argStartTime,
	    int argStep,
	    double argEndTime,
	    boolean argBCalcSpaceStats,
	    boolean argBCalcTimeStats,
	    VCDataJobID argVCDataJobID) {
		    
	    this(argVariableNames, null, argStartTime, argStep, argEndTime,argVCDataJobID);
	    roi = argROI;
	    
	    calcSpaceStats = argBCalcSpaceStats;
	    calcTimeStats = argBCalcTimeStats;
}

public int getCombinedIndicesCount(){
	int total = 0;
	for(int i=0;i<indices.length;i+= 1){
		total+= indices[i].length;
	}
	return total;
}
public void initIndices(CartesianMesh mesh){
	if(roi == null){
		return;
	}
	int meshSize = mesh.getNumVolumeElements();
	int[][] meshIndices = new int[variableNames.length][];
	for(int i=0;i<meshIndices.length;i+= 1){
//		if(roi[i].length() != meshSize){
//			throw new IllegalArgumentException("Mesh size does not match BitSet size");
//		}
		meshIndices[i] = new int[roi[i].cardinality()];
		int counter = 0;
		for(int j=0;j<meshSize;j+= 1){
			if(roi[i].get(j)){
				meshIndices[i][counter] = j;
				counter+= 1;
			}
		}
	}
	indices = meshIndices;
}
/**
 * Insert the method's description here.
 * Creation date: (12/22/2004 7:43:25 AM)
 * @return double
 */
public double getEndTime() {
	return endTime;
}


/**
 * Insert the method's description here.
 * Creation date: (12/22/2004 7:43:25 AM)
 * @return int[]
 */
public int[][] getIndices() {
	return indices;
}


/**
 * Insert the method's description here.
 * Creation date: (12/22/2004 7:43:25 AM)
 * @return double
 */
public double getStartTime() {
	return startTime;
}


/**
 * Insert the method's description here.
 * Creation date: (12/22/2004 7:43:25 AM)
 * @return int
 */
public int getStep() {
	return step;
}


/**
 * Insert the method's description here.
 * Creation date: (12/22/2004 7:43:25 AM)
 * @return java.lang.String[]
 */
public java.lang.String[] getVariableNames() {
	return variableNames;
}

/**
 * Insert the method's description here.
 * Creation date: (2/21/2006 12:46:25 PM)
 * @return boolean
 */
public boolean isCalcSpaceStats() {
	return calcSpaceStats;
}


/**
 * Insert the method's description here.
 * Creation date: (2/21/2006 12:46:25 PM)
 * @return boolean
 */
public boolean isCalcTimeStats() {
	return calcTimeStats;
}


public VCDataJobID getVcDataJobID() {
	return vcDataJobID;
}
}
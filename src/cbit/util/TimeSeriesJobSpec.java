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
	private int[][] crossingMembraneIndices;//int[varNameIndex][MembraneIndex] non-null for volume data only


/**
 * TimeSeriesSpec constructor comment.
 */
public TimeSeriesJobSpec(String[] argVariableNames,int[][] argIndices,int[][] argCrossingMembIndices,double argStartTime,int argStep,double argEndTime,VCDataJobID argVCDataJobID) {

	variableNames = argVariableNames;
	indices = argIndices;
	startTime = argStartTime;
	step = argStep;
	endTime = argEndTime;
	this.vcDataJobID = argVCDataJobID;
	crossingMembraneIndices = argCrossingMembIndices;
	
	if(argIndices != null && argVariableNames.length != argIndices.length){
		throw new IllegalArgumentException(this.getClass().getName()+" varname length must match indices length");
	}
	if(argCrossingMembIndices != null){
		boolean bError = false;
		if(argCrossingMembIndices.length == argIndices.length){
			for (int i = 0;i < argCrossingMembIndices.length; i++) {
				if(argCrossingMembIndices[i] != null && (argCrossingMembIndices[i].length != argIndices[i].length)){
					bError = true;
					break;
				}
			}
		}else{
			bError = true;
		}
		if(bError){
			throw new IllegalArgumentException(this.getClass().getName()+" non-null crossingMembraneIndices must match dataIndicies");
		}
	}
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
	    
    this(argVariableNames, argIndices,null, argStartTime, argStep, argEndTime,argVCDataJobID);
    if(!(argBCalcSpaceStats || argBCalcTimeStats)){
    	throw new IllegalArgumentException("use other constructor if no stats");
    }
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
		    
	    this(argVariableNames, null, null,argStartTime, argStep, argEndTime,argVCDataJobID);
	    roi = argROI;
	    if(!(argBCalcSpaceStats || argBCalcTimeStats)){
	    	throw new IllegalArgumentException("use other constructor if no stats");
	    }    
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

public int[][] getCrossingMembraneIndices(){
	return crossingMembraneIndices;
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
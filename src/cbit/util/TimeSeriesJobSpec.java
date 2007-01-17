package cbit.util;
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

/**
 * TimeSeriesSpec constructor comment.
 */
public TimeSeriesJobSpec(String[] argVariableNames,int[][] argIndices,double argStartTime,int argStep,double argEndTime) {

	variableNames = argVariableNames;
	indices = argIndices;
	startTime = argStartTime;
	step = argStep;
	endTime = argEndTime;
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
    boolean argBCalcTimeStats) {
	    
    this(argVariableNames, argIndices, argStartTime, argStep, argEndTime);

    calcSpaceStats = argBCalcSpaceStats;
    calcTimeStats = argBCalcTimeStats;
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
}
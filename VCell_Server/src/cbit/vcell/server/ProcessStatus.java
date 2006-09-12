package cbit.vcell.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class ProcessStatus implements java.io.Serializable {
	private int numJobsRunning = -1;
	private int numProcessors = -1;
	private float fractionFreeCPU = 0.0f;
	private long freeJavaMemoryBytes = -1;
	private long totalJavaMemoryBytes = -1;
	private long maxJavaMemoryBytes = -1;
	private long freeMemoryBytes = -1;
	private java.util.Date bootTime = null;
/**
 * ProcessStatus constructor comment.
 */
ProcessStatus(int aNumJobsRunning, int aNumProcessors, float aFractionFreeCPU, long aFreeMemoryBytes, long aFreeJavaMemoryBytes, long aTotalJavaMemoryBytes, long aMaxJavaMemoryBytes, java.util.Date aBootTime) {
	this.numJobsRunning = aNumJobsRunning;
	this.numProcessors = aNumProcessors;
	this.fractionFreeCPU = aFractionFreeCPU;
	this.freeMemoryBytes = aFreeMemoryBytes;
	this.freeJavaMemoryBytes = aFreeJavaMemoryBytes;
	this.totalJavaMemoryBytes = aTotalJavaMemoryBytes;
	this.maxJavaMemoryBytes = aMaxJavaMemoryBytes;
	this.bootTime = aBootTime;
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/01 7:04:11 PM)
 * @return long
 */
public long getAvaillableJavaMemoryBytes() {

	double JVM_MEMORY_TRUST_FACTOR = 0.8;
	long currentJVMUsage = getTotalJavaMemoryBytes()-getFreeJavaMemoryBytes();
	
	return (long)(JVM_MEMORY_TRUST_FACTOR*(getMaxJavaMemoryBytes() - currentJVMUsage));
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2003 10:24:59 AM)
 * @return java.util.Date
 */
public java.util.Date getBootTime() {
	return this.bootTime;
}
/**
 * This method was created in VisualAge.
 * @return float
 */
public float getFractionFreeCPU() {
	return fractionFreeCPU;
}
/**
 * This method was created in VisualAge.
 * @return float
 */
public long getFreeJavaMemoryBytes() {
	return freeJavaMemoryBytes;
}
/**
 * This method was created in VisualAge.
 * @return float
 */
public long getFreeMemoryBytes() {
	return freeMemoryBytes;
}
/**
 * This method was created in VisualAge.
 * @return float
 */
public long getMaxJavaMemoryBytes() {
	return maxJavaMemoryBytes;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumJobsRunning() {
	return numJobsRunning;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumProcessors() {
	return numProcessors;
}
/**
 * This method was created in VisualAge.
 * @return float
 */
public long getTotalJavaMemoryBytes() {
	return totalJavaMemoryBytes;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "ProcessStatus: "+
			getNumProcessors()+" processors, "+
			getNumJobsRunning()+" jobs running, "+
			"FreeMemory="+getFreeMemoryBytes()+" bytes, "+
			"FreeCPU="+(getFractionFreeCPU()*100.0)+"%\n"+
			"JVM Memory (free="+getFreeJavaMemoryBytes()+", total="+getTotalJavaMemoryBytes()+", max="+getMaxJavaMemoryBytes()+") bytes";
}
}

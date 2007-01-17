package cbit.vcell.messaging.admin;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class ServerPerformance extends Performance {
	
	private float fractionFreeCPU = 0.0f;
	private long freeMemoryBytes = -1;
/**
 * ProcessStatus constructor comment.
 */
public ServerPerformance() {		
}
/**
 * ProcessStatus constructor comment.
 */
public ServerPerformance(float aFractionFreeCPU, long aFreeMemoryBytes, long aFreeJavaMemoryBytes, long aTotalJavaMemoryBytes, long aMaxJavaMemoryBytes) {		
	this.fractionFreeCPU = aFractionFreeCPU;
	this.freeMemoryBytes = aFreeMemoryBytes;
	this.freeJavaMemoryBytes = aFreeJavaMemoryBytes;
	this.totalJavaMemoryBytes = aTotalJavaMemoryBytes;
	this.maxJavaMemoryBytes = aMaxJavaMemoryBytes;	
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
public long getFreeMemoryBytes() {
	return freeMemoryBytes;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public Object[] toObjects() {
	return new Object[] {		
		(new Double(getFractionFreeCPU()*100.0)),
		new Long(getFreeMemoryBytes()),
		new Long(getFreeJavaMemoryBytes()), new Long(getTotalJavaMemoryBytes()),  new Long(getMaxJavaMemoryBytes())
	};
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "ServerPerformanceStatus: "+					
			"FreeMemory="+getFreeMemoryBytes()+" bytes, "+
			"FreeCPU="+(getFractionFreeCPU()*100.0)+"%\n"+
			"JVM Memory (free="+getFreeJavaMemoryBytes()+", total="+getTotalJavaMemoryBytes()+", max="+getMaxJavaMemoryBytes()+") bytes";
}
}

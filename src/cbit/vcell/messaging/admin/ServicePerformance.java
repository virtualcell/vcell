package cbit.vcell.messaging.admin;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class ServicePerformance extends Performance {
/**
 * ProcessStatus constructor comment.
 */
public ServicePerformance() {		
}
/**
 * ProcessStatus constructor comment.
 */
public ServicePerformance(long aFreeJavaMemoryBytes, long aTotalJavaMemoryBytes, long aMaxJavaMemoryBytes) {		
	this.freeJavaMemoryBytes = aFreeJavaMemoryBytes;
	this.totalJavaMemoryBytes = aTotalJavaMemoryBytes;
	this.maxJavaMemoryBytes = aMaxJavaMemoryBytes;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public Object[] toObjects() {
	return new Object[] {		
		new Long(getFreeJavaMemoryBytes()), new Long(getTotalJavaMemoryBytes()), new Long(getMaxJavaMemoryBytes())
	};
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "ServicePerformanceStatus: "+ "JVM Memory (free="+getFreeJavaMemoryBytes() 
		+", total="+getTotalJavaMemoryBytes()+", max="+getMaxJavaMemoryBytes()+") bytes";
}
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.messaging.admin;

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

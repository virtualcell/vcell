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
 * Insert the type's description here.
 * Creation date: (8/20/2003 8:28:58 AM)
 * @author: Fei Gao
 */
public abstract class Performance implements java.io.Serializable {
	protected long freeJavaMemoryBytes = -1;
	protected long totalJavaMemoryBytes = -1;
	protected long maxJavaMemoryBytes = -1;	
/**
 * PerformanceStatus constructor comment.
 */
public Performance() {
	super();
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
public long getFreeJavaMemoryBytes() {
	return freeJavaMemoryBytes;
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
 * @return float
 */
public long getTotalJavaMemoryBytes() {
	return totalJavaMemoryBytes;
}
/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 8:47:17 AM)
 * @return java.lang.String[]
 */
public abstract Object[] toObjects();
}

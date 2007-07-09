package org.vcell.util;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class CacheStatus implements java.io.Serializable {
	private long currSize = -1;
	private long maxSize = -1;
	private int numObjects = -1;
/**
 * CacheStatus constructor comment.
 */
public CacheStatus(int numObjects, long currSize, long maxSize) {
	this.numObjects = numObjects;
	this.currSize = currSize;
	this.maxSize = maxSize;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public long getCurrSize() {
	return currSize;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public long getMaxSize() {
	return maxSize;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumObjects() {
	return numObjects;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "Cache ("+getNumObjects()+" entries): currMemSize="+getCurrSize()+" maxMemSize="+getMaxSize();
}
}

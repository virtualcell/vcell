/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;

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

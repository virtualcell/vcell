/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.sql;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.Cacheable;
import org.vcell.util.CompressionUtils;

/**
 * This type was created in VisualAge.
 */
public class DbObjectWrapper {
	private final static Logger lg = LogManager.getLogger(DbObjectWrapper.class);

	private Cacheable workingObject = null;
	private Cacheable referenceObject = null;
	private byte[] serializedObject = null;
/**
 * DbObjectWrapper constructor comment.
 *
 * to use this constructor, assumes immutability
 */
public DbObjectWrapper(Cacheable aWorkingObject) {
	this.workingObject = aWorkingObject;
	this.serializedObject = null;  // must be immutable
	this.referenceObject = aWorkingObject;
}
/**
 * DbObjectWrapper constructor comment.
 */
public DbObjectWrapper(Cacheable aWorkingObject, byte[] serializedCopy) {
	try {
		this.workingObject = aWorkingObject;
		this.serializedObject = serializedCopy; // BeanUtils.toSerialized(aWorkingObject);
		this.referenceObject = (Cacheable) CompressionUtils.fromSerialized(this.serializedObject);
	}catch (IOException e){
		lg.error(e);
		throw new RuntimeException(e.getMessage());
	}catch (ClassNotFoundException e){
		lg.error(e);
		throw new RuntimeException(e.getMessage());
	}
}
/**
 * This method was created in VisualAge.
 * @return Matchable
 */
public Cacheable getClonedCopy() {
	if (serializedObject == null){
		throw new RuntimeException("immutable object "+workingObject+" shouldn't be cloned");
	}
	try {
		return (Cacheable) CompressionUtils.fromSerialized(serializedObject);
	}catch (IOException e){
		lg.error(e);
		throw new RuntimeException(e.getMessage());
	}catch (ClassNotFoundException e){
		lg.error(e);
		throw new RuntimeException(e.getMessage());
	}
}
/**
 * This method was created in VisualAge.
 * @return Matchable
 */
public Cacheable getWorkingCopy() {
	try {
		if (!isUnchanged()){
			System.out.println("WARNING DbObjectWrapper.getWorkingCopy(), object "+workingObject+" has changed");
			workingObject = (Cacheable) CompressionUtils.fromSerialized(serializedObject);
		}
		return workingObject;
	}catch (IOException e){
		lg.error(e);
		throw new RuntimeException(e.getMessage());
	}catch (ClassNotFoundException e){
		lg.error(e);
		throw new RuntimeException(e.getMessage());
	}
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
private boolean isUnchanged() {
	if (workingObject == referenceObject){
		//
		// immutable case (don't chance an inefficient .compareEqual() implementation
		//
		return true;
	}
	return workingObject.compareEqual(referenceObject);
}
}

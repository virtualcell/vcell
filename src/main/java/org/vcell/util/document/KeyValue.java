/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.document;

import org.vcell.util.Matchable;
/**
 * This class is Immutable (must stay immutable).
 *
 * encapsuates primary keys in the database (oracle)
 */
@SuppressWarnings("serial")
public class KeyValue implements java.io.Serializable, Matchable {
	private java.math.BigDecimal value = null;
/**
 * KeyValue constructor comment.
 */
public KeyValue(String value) throws NumberFormatException {
	this.value = new java.math.BigDecimal(value);
}
/**
 * KeyValue constructor comment.
 */
public KeyValue(java.math.BigDecimal key) throws IllegalArgumentException {
	if (key==null){
		throw new IllegalArgumentException("key is null");
	}
	this.value = key;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj == this){
		return true;
	}
	
	if (obj != null && obj instanceof KeyValue){
		KeyValue kv = (KeyValue)obj;
		if (kv.value.equals(value)){
			return true;
		}
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (obj instanceof KeyValue){
		return ((KeyValue)obj).value.equals(value);
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 * @param strValue java.lang.String
 */
public static KeyValue fromString(String strValue) throws NumberFormatException {
	return new KeyValue(strValue);
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int hashCode() {
	return value.hashCode();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return value.toString();
}

public int compareTo(KeyValue other) {
	return value.compareTo(other.value);
}
}

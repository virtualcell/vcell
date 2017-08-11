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
 * Insert the type's description here.
 * Creation date: (6/10/2004 7:17:23 PM)
 * @author: Jim Schaff
 */
public class Preference implements java.io.Serializable, Matchable {
	private String key = null;
	private String value = null;

	//
	// max string lengths
	//
	// remember to keep these consistent with UserPreferenceTable column definitions
	//
	public static final int MAX_KEY_LENGTH = 128;
	public static final int MAX_VALUE_LENGTH = 4000;

/**
 * Preference constructor comment.
 */
public Preference(String argKey, String argValue) {
	if (argKey==null || argValue==null){
		throw new IllegalArgumentException("key or value is null");
	}
	if (TokenMangler.getSQLEscapedString(argKey).length() > MAX_KEY_LENGTH){
		throw new IllegalArgumentException("key ("+TokenMangler.getSQLEscapedString(argKey)+") is longer than "+MAX_KEY_LENGTH);
	}
	if (TokenMangler.getSQLEscapedString(argValue).length() > MAX_VALUE_LENGTH){
		throw new IllegalArgumentException("value ("+TokenMangler.getSQLEscapedString(argValue)+") is longer than "+MAX_VALUE_LENGTH);
	}
	this.key = argKey;
	this.value = argValue;
}

/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj instanceof Preference){
		Preference other = (Preference)obj;
		if (!other.value.equals(value)){
			return false;
		}
		if (!other.key.equals(key)){
			return false;
		}
		return true;
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:20:07 PM)
 * @return java.lang.String
 */
public java.lang.String getKey() {
	return key;
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:20:07 PM)
 * @return java.lang.String
 */
public java.lang.String getValue() {
	return value;
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:23:32 PM)
 * @return java.lang.String
 */
public String toString() {
	return "Preference['"+key+"','"+value+"']";
}
}

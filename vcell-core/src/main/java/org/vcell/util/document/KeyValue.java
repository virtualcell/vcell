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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.vcell.util.Matchable;

@SuppressWarnings("serial")


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class KeyValue implements java.io.Serializable, Matchable {
	private java.math.BigDecimal value = null;

	public static org.vcell.restclient.model.KeyValue keyValueToDTO(KeyValue kv) {
		org.vcell.restclient.model.KeyValue k = new org.vcell.restclient.model.KeyValue();
		k.setValue(kv.value);
		return k;
	}
	public static KeyValue dtoToKeyValue(org.vcell.restclient.model.KeyValue dto){
		return dto == null ? null : new KeyValue(dto.getValue());
	}


public KeyValue(String value) throws NumberFormatException {
	this.value = new java.math.BigDecimal(value);
}

public KeyValue(java.math.BigDecimal key) throws IllegalArgumentException {
	if (key==null){
		throw new IllegalArgumentException("key is null");
	}
	this.value = key;
}

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

public boolean equals(Object obj) {
	if (obj instanceof KeyValue){
		return ((KeyValue)obj).value.equals(value);
	}
	return false;
}

public static KeyValue fromString(String strValue) throws NumberFormatException {
	return new KeyValue(strValue);
}

public int hashCode() {
	return value.hashCode();
}

public String toString() {
	return value.toString();
}

public int compareTo(KeyValue other) {
	return value.compareTo(other.value);
}
}

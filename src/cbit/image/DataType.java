/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image;

/**
 * This type was created in VisualAge.
 */
public class DataType {
	private int type;

	private static int BYTE = 0;
	private static int SHORT = 1;
	private static int FLOAT = 2;
	private static int DOUBLE = 3;

	private static String names[] = { "Byte", "Short", "Float", "Double"};
/**
 * This method was created in VisualAge.
 * @param filterCategory int
 */
private DataType(int argType) {
	this.type = argType;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (obj instanceof DataType){
		DataType dt = (DataType)obj;
		if (dt.type == type){
			return true;
		}
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return cbit.image.DataType
 */
public static DataType getByte() {
	return new DataType(BYTE);
}
/**
 * This method was created in VisualAge.
 * @return cbit.image.DataType
 */
public static DataType getDouble() {
	return new DataType(DOUBLE);
}
/**
 * This method was created in VisualAge.
 * @return cbit.image.DataType
 */
public static DataType getFloat() {
	return new DataType(FLOAT);
}
/**
 * This method was created in VisualAge.
 * @return cbit.image.DataType
 */
public static DataType getShort() {
	return new DataType(SHORT);
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isByte() {
	return type==BYTE;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isDouble() {
	return type==DOUBLE;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isFloat() {
	return type==FLOAT;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isShort() {
	return type==SHORT;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return names[type];
}
}

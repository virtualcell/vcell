/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.util;

/**
 * Insert the type's description here.
 * Creation date: (10/30/2001 2:40:15 PM)
 * @author: John Wagner
 */
public class BoundingIndex {
	private int fieldLoI = 0;
	private int fieldHiI = 0;
	private int fieldLoJ = 0;
	private int fieldHiJ = 0;
	private int fieldLoK = 0;
	private int fieldHiK = 0;
/**
 * BoundingIndex constructor comment.
 */
public BoundingIndex(int loI, int hiI, int loJ, int hiJ, int loK, int hiK) {
	super();
	fieldLoI = loI;
	fieldHiI = hiI;
	fieldLoJ = loJ;
	fieldHiJ = hiJ;
	fieldLoK = loK;
	fieldHiK = hiK;
}
/**
 * Gets the hiI property (int) value.
 * @return The hiI property value.
 */
public int getHiI() {
	return fieldHiI;
}
/**
 * Gets the hiJ property (int) value.
 * @return The hiJ property value.
 */
public int getHiJ() {
	return fieldHiJ;
}
/**
 * Gets the hiK property (int) value.
 * @return The hiK property value.
 */
public int getHiK() {
	return fieldHiK;
}
/**
 * Gets the loI property (int) value.
 * @return The loI property value.
 */
public int getLoI() {
	return fieldLoI;
}
/**
 * Gets the loJ property (int) value.
 * @return The loJ property value.
 */
public int getLoJ() {
	return fieldLoJ;
}
/**
 * Gets the loK property (int) value.
 * @return The loK property value.
 */
public int getLoK() {
	return fieldLoK;
}
}

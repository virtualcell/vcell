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
 * Creation date: (10/30/2001 2:39:56 PM)
 * @author: John Wagner
 */
public class BoundingBox {
	private double fieldLoX = 0;
	private double fieldHiX = 0;
	private double fieldLoY = 0;
	private double fieldHiY = 0;
	private double fieldLoZ = 0;
	private double fieldHiZ = 0;
/**
 * BoundingBox constructor comment.
 */
public BoundingBox(double loX, double hiX, double loY, double hiY, double loZ, double hiZ) {
	super();
	fieldLoX = loX;
	fieldHiX = hiX;
	fieldLoY = loY;
	fieldHiY = hiY;
	fieldLoZ = loZ;
	fieldHiZ = hiZ;
}
/**
 * Gets the hiX property (double) value.
 * @return The hiX property value.
 */
public double getHiX() {
	return fieldHiX;
}
/**
 * Gets the hiY property (double) value.
 * @return The hiY property value.
 */
public double getHiY() {
	return fieldHiY;
}
/**
 * Gets the hiZ property (double) value.
 * @return The hiZ property value.
 */
public double getHiZ() {
	return fieldHiZ;
}
/**
 * Gets the loX property (double) value.
 * @return The loX property value.
 */
public double getLoX() {
	return fieldLoX;
}
/**
 * Gets the loY property (double) value.
 * @return The loY property value.
 */
public double getLoY() {
	return fieldLoY;
}
/**
 * Gets the loZ property (double) value.
 * @return The loZ property value.
 */
public double getLoZ() {
	return fieldLoZ;
}
}

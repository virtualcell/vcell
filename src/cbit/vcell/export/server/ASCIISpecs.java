/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.server;

import java.io.*;
/**
 * This type was created in VisualAge.
 */
public class ASCIISpecs extends FormatSpecificSpecs implements Serializable {
	private int format;
	private int dataType;
	private boolean switchRowsColumns;
/**
 * TextSpecs constructor comment.
 */
public ASCIISpecs(int format, int dataType, boolean switchRowsColumns) {
	this.format = format;
	this.dataType = dataType;
	this.switchRowsColumns = switchRowsColumns;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 1:08:44 AM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(java.lang.Object object) {
	if (object instanceof ASCIISpecs) {
		ASCIISpecs asciiSpecs = (ASCIISpecs)object;
		if (
			format == asciiSpecs.getFormat() &&
			dataType == asciiSpecs.getDataType() &&
			switchRowsColumns == asciiSpecs.getSwitchRowsColumns()
		) {
			return true;
		}
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getDataType() {
	return dataType;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getFormat() {
	return format;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public boolean getSwitchRowsColumns() {
	return switchRowsColumns;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 5:08:46 PM)
 * @return java.lang.String
 */
public java.lang.String toString() {
	return "ASCIISpecs: [format: " + format + ", dataType: " + dataType + ", switchRowsColumns: " + switchRowsColumns + "]";
}
}

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


import java.io.Serializable;
/**
 * This type was created in VisualAge.
 */
public class RasterSpecs extends FormatSpecificSpecs implements Serializable {
	private int format;
	private boolean separateHeader;

/**
 * Insert the method's description here.
 * Creation date: (4/23/2004 11:35:59 AM)
 * @param format int
 * @param separateHeader boolean
 */
public RasterSpecs(int format, boolean separateHeader) {
	this.format = format;
	this.separateHeader = separateHeader;
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 1:08:44 AM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(java.lang.Object object) {
	if (object instanceof RasterSpecs) {
		RasterSpecs rasterSpecs = (RasterSpecs)object;
		if (
			format == rasterSpecs.getFormat() &&
			separateHeader == rasterSpecs.isSeparateHeader()
		)
		return true;
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (4/23/2004 11:34:51 AM)
 * @return int
 */
public int getFormat() {
	return format;
}


/**
 * Insert the method's description here.
 * Creation date: (4/23/2004 11:34:51 AM)
 * @return boolean
 */
public boolean isSeparateHeader() {
	return separateHeader;
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 5:08:46 PM)
 * @return java.lang.String
 */
public java.lang.String toString() {
	return "RasterSpecs: [format: " + format + ", separateHeader: " + separateHeader + "]";
}
}

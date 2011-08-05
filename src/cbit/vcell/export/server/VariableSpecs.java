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
public class VariableSpecs implements Serializable {
	private String[] variableNames;
	private int modeID;
/**
 * This method was created in VisualAge.
 * @param variableNames java.lang.String[]
 * @param scaleSettings cbit.image.Range[]
 */
public VariableSpecs(String[] variableNames, int modeID) {
	this.variableNames = variableNames;
	this.modeID = modeID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 12:04:55 AM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof VariableSpecs) {
		VariableSpecs variableSpecs = (VariableSpecs)object;
		if (modeID == variableSpecs.getModeID()) {
			if (variableNames.length == variableSpecs.getVariableNames().length) {
				for (int i = 0; i < variableNames.length; i++){
					if (! variableNames[i].equals(variableSpecs.getVariableNames()[i])) {
						return false;
					}
				}
				return true;
			}
		}
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getModeID() {
	return modeID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
public String[] getVariableNames() {
	return variableNames;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:33:23 PM)
 * @return int
 */
public int hashCode() {
	return toString().hashCode();
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:23:04 PM)
 * @return java.lang.String
 */
public String toString() {
	StringBuffer buf = new StringBuffer();
	buf.append("VariableSpecs [");
	buf.append("names: ");
	if (variableNames != null) {
		buf.append("{");
		for (int i = 0; i < variableNames.length; i++){
			buf.append(variableNames[i]);
			if (i < variableNames.length - 1) buf.append(",");
		}
		buf.append("}");
	} else {
		buf.append("null");
	}
	buf.append(", modeID: " + modeID + "]");
	return buf.toString();
}
}

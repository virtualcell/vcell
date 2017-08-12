/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.rmi.event;

/**
 * Insert the type's description here.
 * Creation date: (11/12/2000 2:04:22 PM)
 * @author: IIM
 */
public class MessageSource implements java.io.Serializable {
	private String sourceClassName = null;
	private String sourceID = null;
/**
 * Insert the method's description here.
 * Creation date: (11/12/2000 2:08:18 PM)
 * @param source java.lang.Object
 * @param sourceID java.lang.String
 */
public MessageSource(Object source, String sourceID) {
	if (sourceID == null) {
		throw new IllegalArgumentException("ERROR: sourceID cannot be null");
	}
	this.sourceClassName = source.getClass().getName();
	this.sourceID = sourceID;
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2002 3:18:26 PM)
 * @return boolean
 * @param o java.lang.Object
 */
public boolean equals(Object o) {
	return (o != null && o instanceof MessageSource && toString().equals(o.toString()));
}
/**
 * Insert the method's description here.
 * Creation date: (11/12/2000 2:12:27 PM)
 * @return java.lang.String
 */
public String getSourceClassName() {
	return sourceClassName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/12/2000 2:14:14 PM)
 * @return java.lang.String
 */
public String getSourceID() {
	return sourceID;
}
/**
 * Insert the method's description here.
 * Creation date: (8/30/2002 11:48:06 AM)
 * @return int
 */
public int hashCode() {
	return toString().hashCode();
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2002 3:16:21 PM)
 * @return java.lang.String
 */
public String toString() {
	return "MessageSource: Class = " + getSourceClassName() + ", SourceID: " + getSourceID();
}
}

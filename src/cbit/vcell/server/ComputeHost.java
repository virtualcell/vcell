/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;

/**
 * Insert the type's description here.
 * Creation date: (8/28/2002 5:10:47 PM)
 * @author: Ion Moraru
 */
public class ComputeHost implements java.io.Serializable {
	public static final int ODEComputeHost = 0;
	public static final int PDEComputeHost = 1;
	private static final String[] hostTypeDescriptions = {"ODEComputeHost", "PDEComputeHost"};
	private String hostName = null;
	private int type = -1;
/**
 * Insert the method's description here.
 * Creation date: (8/28/2002 5:16:44 PM)
 * @param hostname java.lang.String
 * @param type int
 */
private ComputeHost(String hostName, int type) {
	this.hostName = hostName;
	this.type = type;
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2002 5:19:01 PM)
 * @return cbit.vcell.server.ComputeHost
 * @param hostname java.lang.String
 */
public static ComputeHost createODEComputeHost(String hostname) {
	return new ComputeHost(hostname, ComputeHost.ODEComputeHost);
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2002 5:19:01 PM)
 * @return cbit.vcell.server.ComputeHost
 * @param hostname java.lang.String
 */
public static ComputeHost createPDEComputeHost(String hostname) {
	return new ComputeHost(hostname, ComputeHost.PDEComputeHost);
}
/**
 * Compares two objects for equality. Returns a boolean that indicates
 * whether this object is equivalent to the specified object. This method
 * is used when an object is stored in a hashtable.
 * @param obj the Object to compare with
 * @return true if these Objects are equal; false otherwise.
 * @see java.util.Hashtable
 */
public boolean equals(Object obj) {
	return (obj != null && obj instanceof ComputeHost && toString().equals(obj.toString()));
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2002 5:21:29 PM)
 * @return java.lang.String
 */
public java.lang.String getHostName() {
	return hostName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2002 5:21:29 PM)
 * @return int
 */
public int getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (8/30/2002 11:53:54 AM)
 * @return int
 */
public int hashCode() {
	return toString().hashCode();
}
/**
 * Returns a String that represents the value of this object.
 * @return a string representation of the receiver
 */
public String toString() {
	return hostTypeDescriptions[getType()] + ": " + getHostName();
}
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gloworm.atoms;
/**
 * Insert the type's description here.
 * Creation date: (11/7/2005 10:18:36 PM)
 * @author: Ion Moraru
 */
public class VRNodeLocationAtom extends VRAtom {
	// supplied by constructor
	private String nodeType;
	// default values; public setters
	private int locationFlags = 0;
	private int locationData = 0;

/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 9:49:09 PM)
 * @param nodeType java.lang.String
 */
public VRNodeLocationAtom(String nodeType) {
	setNodeType(nodeType);
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 9:48:31 PM)
 * @return int
 */
public int getLocationData() {
	return locationData;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 9:48:31 PM)
 * @return int
 */
public int getLocationFlags() {
	return locationFlags;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 9:48:46 PM)
 * @return java.lang.String
 */
public java.lang.String getNodeType() {
	return nodeType;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 6:26:07 PM)
 * @return int
 */
public int getSize() {
	return 44;
}


/**
 * Insert the method's description here.
 * Creation date: (11/7/2005 10:18:36 PM)
 * @return java.lang.String
 */
public String getType() {
	return VR_NODE_LOCATION_ATOM_TYPE;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 9:48:31 PM)
 * @param newLocationData int
 */
public void setLocationData(int newLocationData) {
	locationData = newLocationData;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 9:48:31 PM)
 * @param newLocationFlags int
 */
public void setLocationFlags(int newLocationFlags) {
	locationFlags = newLocationFlags;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 9:48:46 PM)
 * @param newNodeType java.lang.String
 */
private void setNodeType(java.lang.String newNodeType) {
	nodeType = newNodeType;
}


/**
 * This method was created in VisualAge.
 * @param out java.io.DataOutputStream
 */
public void writeData(java.io.DataOutputStream out) throws java.io.IOException {
	out.writeInt(getSize());
	out.writeBytes(getType());
	out.writeInt(getAtomID());
	out.writeInt(getChildCount());
	out.writeInt(getIndex());
	out.writeShort(VR_MAJOR_VERSION);
	out.writeShort(VR_MINOR_VERSION);
	out.writeBytes(getNodeType());
	out.writeInt(getLocationFlags());
	out.writeInt(getLocationData());
	out.writeInt(VR_RESERVED);
	out.writeInt(VR_RESERVED);
}
}

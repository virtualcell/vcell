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
public class VRWorldHeaderAtom extends VRAtom {
	// default values; public setters
	private int nameAtomID = 1;
	private int defaultNodeID = 1;
	private int flags = 0; // Apple's software seems to use it; docs say otherwise

/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 8:43:54 PM)
 * @return int
 */
public int getDefaultNodeID() {
	return defaultNodeID;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 8:43:54 PM)
 * @return int
 */
public int getFlags() {
	return flags;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 6:33:32 PM)
 * @return int
 */
public int getNameAtomID() {
	return nameAtomID;
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
	return VR_WORLD_HEADER_ATOM_TYPE;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 8:43:54 PM)
 * @param newDefaultNodeID int
 */
public void setDefaultNodeID(int newDefaultNodeID) {
	defaultNodeID = newDefaultNodeID;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 8:43:54 PM)
 * @param newFlags int
 */
public void setFlags(int newFlags) {
	flags = newFlags;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 6:33:32 PM)
 * @param newNameAtomID int
 */
public void setNameAtomID(int newNameAtomID) {
	nameAtomID = newNameAtomID;
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
	out.writeInt(getNameAtomID());
	out.writeInt(getDefaultNodeID());
	out.writeInt(getFlags());
	out.writeInt(VR_RESERVED);
	out.writeInt(VR_RESERVED);
}
}

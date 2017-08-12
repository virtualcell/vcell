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
public class VRNodeHeaderAtom extends VRAtom {
	// required by constructor; private setter
	private String nodeType;

	// default values; public setters
	private int nodeID = 1;
	private int nameAtomID = 0;
	private int commentAtomID = 0;

/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 6:33:58 PM)
 * @param nodeType java.lang.String
 */
public VRNodeHeaderAtom(String nodeType) {
	setNodeType(nodeType);
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 6:33:32 PM)
 * @return int
 */
public int getCommentAtomID() {
	return commentAtomID;
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
 * Creation date: (11/8/2005 6:33:32 PM)
 * @return int
 */
public int getNodeID() {
	return nodeID;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 6:33:18 PM)
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
	return 48;
}


/**
 * Insert the method's description here.
 * Creation date: (11/7/2005 10:18:36 PM)
 * @return java.lang.String
 */
public String getType() {
	return VR_NODE_HEADER_ATOM_TYPE;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 6:33:32 PM)
 * @param newCommentAtomID int
 */
public void setCommentAtomID(int newCommentAtomID) {
	commentAtomID = newCommentAtomID;
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
 * Insert the method's description here.
 * Creation date: (11/8/2005 6:33:32 PM)
 * @param newNodeID int
 */
public void setNodeID(int newNodeID) {
	nodeID = newNodeID;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 6:33:18 PM)
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
	out.writeInt(getNodeID());
	out.writeInt(getNameAtomID());
	out.writeInt(getCommentAtomID());
	out.writeInt(VR_RESERVED);
	out.writeInt(VR_RESERVED);
}
}

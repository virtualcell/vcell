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
 * Creation date: (11/7/2005 9:57:59 PM)
 * @author: Ion Moraru
 */
public abstract class VRAtom implements AtomConstants {
	private int atomID = 1;
	private int childCount = 0;
	private int index = 0;

	public final static String VR_ATOM_CONTAINER_TYPE = "sean";
	public final static String VR_NODE_HEADER_ATOM_TYPE = "ndhd";
	public final static String VR_OBJECT_NODE_TYPE = "obje";
	public final static String VR_OBJECT_SAMPLE_ATOM_TYPE = "obji";
	public final static String VR_SAMPLE_DESCRIPTION_ENTRY_TYPE = "qtvr";
	public final static String VR_STRING_ATOM_TYPE = "vrsg";
	public final static String VR_WORLD_HEADER_ATOM_TYPE = "vrsc";
	public final static String VR_IMAGING_PARENT_ATOM_TYPE = "imgp";
	public final static String VR_NODE_PARENT_ATOM_TYPE = "vrnp";
	public final static String VR_NODE_ID_ATOM_TYPE = "vrni";
	public final static String VR_NODE_LOCATION_ATOM_TYPE = "nloc";
	public final static String VR_OBJECT_REFERENCE_TYPE = "obje";
	public final static String VR_IMAGE_REFERENCE_TYPE = "imgt";

	public final static short VR_MAJOR_VERSION = 2;
	public final static short VR_MINOR_VERSION = 0;
	public final static int VR_RESERVED = 0;

/**
 * Insert the method's description here.
 * Creation date: (11/7/2005 10:00:17 PM)
 * @return int
 */
public int getAtomID() {
	return atomID;
}


/**
 * Insert the method's description here.
 * Creation date: (11/7/2005 10:00:17 PM)
 * @return int
 */
public int getChildCount() {
	return childCount;
}


/**
 * Insert the method's description here.
 * Creation date: (11/7/2005 10:00:17 PM)
 * @return int
 */
public int getIndex() {
	return index;
}


/**
 * Insert the method's description here.
 * Creation date: (11/7/2005 10:02:00 PM)
 * @return int
 */
public abstract int getSize();


/**
 * Insert the method's description here.
 * Creation date: (11/7/2005 10:04:47 PM)
 * @return java.lang.String
 */
public abstract String getType();


/**
 * Insert the method's description here.
 * Creation date: (11/7/2005 10:00:17 PM)
 * @param newAtomID int
 */
public void setAtomID(int newAtomID) {
	atomID = newAtomID;
}


/**
 * Insert the method's description here.
 * Creation date: (11/7/2005 10:00:17 PM)
 * @param newChildCount int
 */
public void setChildCount(int newChildCount) {
	childCount = newChildCount;
}


/**
 * Insert the method's description here.
 * Creation date: (11/7/2005 10:00:17 PM)
 * @param newIndex int
 */
public void setIndex(int newIndex) {
	index = newIndex;
}


/**
 * This method was created in VisualAge.
 * @param out java.io.DataOutputStream
 */
public abstract void writeData(java.io.DataOutputStream out) throws java.io.IOException;
}

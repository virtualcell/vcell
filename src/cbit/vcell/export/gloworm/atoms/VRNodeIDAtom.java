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
public class VRNodeIDAtom extends VRAtom {
	private VRNodeLocationAtom locationAtoms[];

/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 8:57:15 PM)
 * @param idAtoms VRNodeLocationAtom[]
 */
public VRNodeIDAtom(VRNodeLocationAtom[] locationAtoms) {
	this.locationAtoms = locationAtoms;
	setChildCount(locationAtoms.length);
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 6:26:07 PM)
 * @return int
 */
public int getSize() {
	int size = 20;
	for (int i = 0; i < locationAtoms.length; i++){
		size += locationAtoms[i].getSize();
	}
	return size;
}


/**
 * Insert the method's description here.
 * Creation date: (11/7/2005 10:18:36 PM)
 * @return java.lang.String
 */
public String getType() {
	return VR_NODE_ID_ATOM_TYPE;
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
	for (int i = 0; i < locationAtoms.length; i++){
		locationAtoms[i].writeData(out);
	}
}
}

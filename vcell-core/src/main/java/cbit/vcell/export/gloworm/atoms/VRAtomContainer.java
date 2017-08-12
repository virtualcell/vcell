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
import java.io.IOException;
/**
 * Insert the type's description here.
 * Creation date: (11/7/2005 10:06:12 PM)
 * @author: Ion Moraru
 */
public class VRAtomContainer extends VRAtom {
	private VRAtom[] childAToms = new VRAtom[0];

/**
 * Insert the method's description here.
 * Creation date: (11/7/2005 10:17:12 PM)
 * @param childAtoms VRAtom[]
 */
public VRAtomContainer(VRAtom[] childAtoms) {
	this.childAToms = childAtoms;
	setChildCount(childAtoms.length);
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 5:05:28 PM)
 * @return int
 */
public int getSize() {
	int size = 32;
	for (int i = 0; i < childAToms.length; i++){
		size += childAToms[i].getSize();
	}
	return size;
}


/**
 * Insert the method's description here.
 * Creation date: (11/7/2005 10:06:12 PM)
 * @return java.lang.String
 */
public String getType() {
	return VR_ATOM_CONTAINER_TYPE;
}


/**
 * This method was created in VisualAge.
 * @param out java.io.DataOutputStream
 */
public void writeData(java.io.DataOutputStream out) throws IOException {
	out.writeInt(0);
	out.writeInt(0);
	out.writeInt(0);
	out.writeInt(getSize() - 12);
	out.writeBytes(getType());
	out.writeInt(getAtomID());
	out.writeInt(getChildCount());
	out.writeInt(getIndex());
	for (int i = 0; i < childAToms.length; i++){
		childAToms[i].writeData(out);
	}
}
}

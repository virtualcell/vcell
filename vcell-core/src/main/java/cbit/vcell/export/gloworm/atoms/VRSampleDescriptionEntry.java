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
 * Creation date: (11/8/2005 7:27:55 PM)
 * @author: Ion Moraru
 */
public class VRSampleDescriptionEntry extends SampleDescriptionEntry {
	private VRAtomContainer vrWorldContainer;

/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:34:28 PM)
 * @param vrWorldContainer VRAtomContainer
 */
public VRSampleDescriptionEntry(VRAtomContainer vrWorldContainer) {
	this.vrWorldContainer = vrWorldContainer;
	size = 16 + vrWorldContainer.getSize();
}


/**
 * This method was created in VisualAge.
 * @param out java.io.DataOutputStream
 */
public boolean writeData(java.io.DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(VRAtom.VR_SAMPLE_DESCRIPTION_ENTRY_TYPE);
		out.write(reserved);
		out.writeShort(dataReferenceIndex);
		vrWorldContainer.writeData(out);
		return true;
	} catch (java.io.IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}

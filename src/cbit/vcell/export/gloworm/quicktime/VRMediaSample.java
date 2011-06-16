/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gloworm.quicktime;

import cbit.vcell.export.gloworm.atoms.AtomConstants;
import cbit.vcell.export.gloworm.atoms.SampleDescriptionEntry;
import cbit.vcell.export.gloworm.atoms.VRAtom;
import cbit.vcell.export.gloworm.atoms.VRSampleDescriptionEntry;


/**
 * Insert the type's description here.
 * Creation date: (11/8/2005 10:06:44 PM)
 * @author: Ion Moraru
 */
public class VRMediaSample implements MediaSample {
	private VRWorld vrWorld;
	private int nodeIndex;
	private VRSampleDescriptionEntry entry;

/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 10:16:04 PM)
 */
public VRMediaSample(VRWorld vrWorld, int nodeIndex) {
	this.vrWorld = vrWorld;
	this.nodeIndex = nodeIndex;
	this.entry = new VRSampleDescriptionEntry(vrWorld.getVRWorldContainer());
}


/**
 * This method was created in VisualAge.
 * @return byte[]
 */
public byte[] getDataBytes() {
	java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
	java.io.DataOutputStream dout = new java.io.DataOutputStream(bout);
	try {
		vrWorld.getVRNodeInfoContainer(nodeIndex).writeData(dout);
	} catch (java.io.IOException exc) {
		exc.printStackTrace();
	}
	return bout.toByteArray();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public java.lang.String getDataFormat() {
	return VRAtom.VR_SAMPLE_DESCRIPTION_ENTRY_TYPE;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getDuration() {
	return vrWorld.getDuration();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public java.lang.String getMediaType() {
	return AtomConstants.MEDIA_TYPE_QTVR;
}


/**
 * This method was created in VisualAge.
 * @return SampleDescriptionEntry
 */
public SampleDescriptionEntry getSampleDescriptionEntry() {
	return entry;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getSize() {
	return vrWorld.getVRNodeInfoContainer(nodeIndex).getSize();
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isKeyFrame() {
	return false;
}
}

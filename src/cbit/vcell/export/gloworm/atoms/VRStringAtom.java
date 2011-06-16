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
 * Creation date: (11/8/2005 7:40:37 PM)
 * @author: Ion Moraru
 */
public class VRStringAtom extends VRAtom {
	// supplied
	private String theString;
	// default
	private short stringUsage = 1;

/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:46:02 PM)
 * @param argString java.lang.String
 */
public VRStringAtom(String argString) {
	theString = argString;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:40:37 PM)
 * @return int
 */
public int getSize() {
	return 25 + theString.toCharArray().length;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:45:36 PM)
 * @return short
 */
public short getStringUsage() {
	return stringUsage;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:40:37 PM)
 * @return java.lang.String
 */
public String getType() {
	return VR_STRING_ATOM_TYPE;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:45:36 PM)
 * @param newStringUsage short
 */
public void setStringUsage(short newStringUsage) {
	stringUsage = newStringUsage;
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
	out.writeShort(getStringUsage());
	out.writeShort((short)theString.toCharArray().length);
	out.writeBytes(theString);
	out.write(new byte[1]); // don't know if required, but Apple's software appends this byte
}
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.quicktime.atoms;

import cbit.vcell.export.quicktime.*;
import java.io.*;
import java.util.zip.*;
/**
 * This type was created in VisualAge.
 */
public class HandlerReference extends LeafAtom {

	public static final String type = "hdlr";
	protected String componentType;
	protected String componentSubtype;
	protected static String componentManufacturer = "appl";
	protected byte[] componentFlags = new byte[4];
	protected byte[] componentFlagsMask = new byte[4];
	protected byte[] componentName;

/**
 * This method was created in VisualAge.
 */
public HandlerReference(String cType, String cSubtype) throws DataFormatException {
	componentManufacturer = "appl";
	if (cType.equals(mediaHandler)) {
		if (cSubtype.equals(videoSubtype)) {
			componentType = cType;
			componentSubtype = cSubtype;
			componentName = new byte[26];
			componentName[0] = (byte)25;
			for (int i=1;i<26;i++) componentName[i] = "Apple Video Media Handler".getBytes()[i - 1];
		} else {
			componentType = "null";
			componentSubtype = "null";
			componentName = "null".getBytes();
			throw new DataFormatException("Component type or subtype not valid");
		}
	} else {
		if (cType.equals(dataHandler)) {
			if (cSubtype.equals(aliasSubtype)) {
				componentType = cType;
				componentSubtype = cSubtype;
				componentName = new byte[25];
				componentName[0] = (byte)24;
				for (int i=1;i<25;i++) componentName[i] = "Apple Alias Data Handler".getBytes()[i - 1];
			} else {
				componentType = "null";
				componentSubtype = "null";
				componentName = "null".getBytes();
				throw new DataFormatException("Component type or subtype not valid");
			}
		}
	}
	size = 32 + componentName.length;
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		out.writeByte(version);
		out.write(flags);
		out.writeBytes(componentType);
		out.writeBytes(componentSubtype);
		out.writeBytes(componentManufacturer);
		out.write(componentFlags);
		out.write(componentFlagsMask);
		out.write(componentName);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}

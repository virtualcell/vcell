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
	protected String componentName;

/**
 * This method was created in VisualAge.
 */
public HandlerReference(String componentType, int componentSubtype) throws DataFormatException {
	this.componentType = componentType;
	switch (componentSubtype) {
		case COMPONENT_SUBTYPE_FILE_ALIAS: 
		case COMPONENT_SUBTYPE_OBJECT: 
		case COMPONENT_SUBTYPE_QTVR: 
		case COMPONENT_SUBTYPE_VIDEO: 
		{
			this.componentSubtype = COMPONENT_SUBTYPES[componentSubtype];
			this.componentName = COMPONENT_SUBTYPE_HANDLER_NAMES[componentSubtype];
			break;
		} 
		default: {
			throw new DataFormatException("Unknown component subtype");
		}
	}
	size = 33 + componentName.toCharArray().length;
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
		out.writeBytes(componentName);
		out.writeByte(0);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}

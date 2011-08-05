/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.server;

import java.io.*;
/**
 * This type was created in VisualAge.
 */
public class ExportOutput implements Serializable {
	
	private boolean valid;
	private String dataType;
	private String simID;
	private String dataID;
	private byte[] data;
	
/**
 * This method was created in VisualAge.
 * @param dataType java.lang.String
 * @param dataID java.lang.String
 * @param data byte[]
 */
public ExportOutput(boolean valid, String dataType, String simID, String dataID, byte[] data) {
	this.valid = valid;
	this.dataType = dataType;
	this.simID = simID;
	this.dataID = dataID;
	this.data = data;
}
/**
 * This method was created in VisualAge.
 * @return byte[]
 */
public byte[] getData() {
	return data;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getDataID() {
	return dataID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getDataType() {
	return dataType;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getSimID() {
	return simID;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isValid() {
	return valid;
}
}

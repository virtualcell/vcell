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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;

import org.vcell.util.FileUtils;

import cbit.vcell.export.server.FileDataContainerManager.FileDataContainerID;

/**
 * This type was created in VisualAge.
 */
public class ExportOutput implements Serializable {
	private boolean valid;
	private String dataType;
	private String simID;
	private String dataID;
	private FileDataContainerID fileDataContainerID;
	

public ExportOutput(boolean valid, String dataType, String simID, String dataID, FileDataContainerManager fileDataContainerManager) throws IOException {
	this.valid = valid;
	this.dataType = dataType;
	this.simID = simID;
	this.dataID = dataID;
     
	//this.data = putDataIntoTempFile(data);
	this.fileDataContainerID = fileDataContainerManager.getNewFileDataContainerID();
}

public void writeDataToOutputStream(OutputStream outputStream,FileDataContainerManager fileDataContainerManager) throws FileNotFoundException, IOException{
	fileDataContainerManager.writeAndFlush(fileDataContainerID, outputStream);
}

public FileDataContainerID getFileDataContainerID(){
	return fileDataContainerID;
}

//public byte[] getData() throws IOException {
//	return FileUtils.readByteArrayFromFile(data);
//}
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

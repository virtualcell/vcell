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
//	FileInputStream fis = new FileInputStream(data.getDataFile());
//	ReadableByteChannel source = Channels.newChannel(fis);
//    WritableByteChannel target = Channels.newChannel(outputStream);
//
//    ByteBuffer buffer = ByteBuffer.allocate(16 * 4096);
//    while (source.read(buffer) != -1) {
//        buffer.flip(); // Prepare the buffer to be drained
//        while (buffer.hasRemaining()) {
//            target.write(buffer);
//        }
//        buffer.clear(); // Empty buffer to get ready for filling
//    }
//
//    source.close();
//	fis.close();
	
	FileInputStream fis = null;
	BufferedInputStream bis = null;
	try{
		File dataFile = fileDataContainerManager.getFileDataContainer(fileDataContainerID).getDataFile();
		fis = new FileInputStream(dataFile);
		bis = new BufferedInputStream(fis);
		// Copy the contents of the file to the output stream
		byte[] buffer = new byte[(int)Math.min(1048576/*2^20*/,dataFile.length())];
		int count = 0;                 
		while ((count = bis.read(buffer)) >= 0) {    
			outputStream.write(buffer, 0, count);
		}                 
		outputStream.flush();
	}finally{
		if(bis != null){try{bis.close();}catch(Exception e){e.printStackTrace();}}
		if(fis != null){try{fis.close();}catch(Exception e){e.printStackTrace();}}
	}
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

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

import org.vcell.util.FileUtils;
/**
 * This type was created in VisualAge.
 */
public class ExportOutput implements Serializable {
	
	private boolean valid;
	private String dataType;
	private String simID;
	private String dataID;
	private FileBackedDataContainer data;
	
/**
 * This method was created in VisualAge.
 * @param dataType java.lang.String
 * @param dataID java.lang.String
 * @param data byte[]
 * @throws IOException 
 */
public ExportOutput(boolean valid, String dataType, String simID, String dataID, byte[] data) throws IOException {
	this.valid = valid;
	this.dataType = dataType;
	this.simID = simID;
	this.dataID = dataID;

	this.data = new FileBackedDataContainer(data);
	
}


public ExportOutput(boolean valid, String dataType, String simID, String dataID, FileBackedDataContainer dataContainer) throws IOException {
	this.valid = valid;
	this.dataType = dataType;
	this.simID = simID;
	this.dataID = dataID;
     
	//this.data = putDataIntoTempFile(data);
	this.data = dataContainer;
}

//private File putDataIntoTempFile(byte[] dataBytes) throws IOException{
//	File tempDataFile = File.createTempFile("TestTempFile", ".tmp");
//	tempDataFile.deleteOnExit();
//	//System.out.print("Created temp file at: "+tempDataFile.getAbsolutePath()+" for "+dataBytes.length+" bytes ....");
//	FileUtils.writeByteArrayToFile(dataBytes, tempDataFile);
//	//System.out.println("Done writing.");
//	return tempDataFile;
//}

public void writeDataToOutputStream(OutputStream outputStream) throws FileNotFoundException, IOException{
	FileInputStream fis = new FileInputStream(data.getDataFile());
	ReadableByteChannel source = Channels.newChannel(fis);
    WritableByteChannel target = Channels.newChannel(outputStream);

    ByteBuffer buffer = ByteBuffer.allocate(16 * 4096);
    while (source.read(buffer) != -1) {
        buffer.flip(); // Prepare the buffer to be drained
        while (buffer.hasRemaining()) {
            target.write(buffer);
        }
        buffer.clear(); // Empty buffer to get ready for filling
    }

    source.close();
	
	
	
	
//	FileChannel fc = fis.getChannel();
//	fc.transferTo(0, data.getDataFile().length(),  outputStream);
//	fc.close();
	fis.close();
}

public FileBackedDataContainer getDataContainer(){
	return this.data;
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

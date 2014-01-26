/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.nrrd;
import java.io.*;

import cbit.vcell.export.server.FileDataContainerManager;
import cbit.vcell.export.server.FileDataContainerManager.FileDataContainerID;
public class NrrdWriter {
/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 4:26:18 PM)
 */
private static void writeArrayFieldString(FileWriter fw, String name, double[] data) throws IOException {
	if (data != null) {
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < data.length; i++){
			bf.append(data[i]);
			bf.append(" ");
		}
		fw. write(name + ": " + bf.toString().trim() + "\n");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 4:26:18 PM)
 */
private static void writeArrayFieldString(FileWriter fw, String name, int[] data) throws IOException {
	if (data != null) {
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < data.length; i++){
			bf.append(data[i]);
			bf.append(" ");
		}
		fw. write(name + ": " + bf.toString().trim() + "\n");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 4:26:18 PM)
 */
private static void writeArrayFieldString(FileWriter fw, String name, String[] data) throws IOException {
	if (data != null) {
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < data.length; i++){
			bf.append(data[i]);
			bf.append(" ");
		}
		fw. write(name + ": " + bf.toString().trim() + "\n");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 10:51:32 AM)
 * @param headerFileName java.lang.String
 * @param baseDir java.io.File
 * @param nrrdInfo cbit.vcell.export.nrrd.NrrdInfo
 * @exception java.io.IOException The exception description.
 * @exception java.io.FileNotFoundException The exception description.
 */
public static NrrdInfo writeNRRD(NrrdInfo nrrdInfo,FileDataContainerManager fileDataContainerManager) throws IOException{
	// write header first
	nrrdInfo.setHeaderFileID(fileDataContainerManager.getNewFileDataContainerID());
	FileWriter headerFileWriter = null;
	try {
		headerFileWriter = 	new FileWriter(fileDataContainerManager.getFile(nrrdInfo.getHeaderFileID()));
		headerFileWriter.write(NrrdInfo.MAGIC + "\n");
		headerFileWriter.write("endian: " + NrrdInfo.ENDIAN + "\n");
		headerFileWriter.write("# " + nrrdInfo.getMainComment() + "\n");
		headerFileWriter.write("type: " + nrrdInfo.getType() + "\n");
		headerFileWriter.write("dimension: " + nrrdInfo.getDimension() + "\n");
		headerFileWriter.write("encoding: " + nrrdInfo.getEncoding() + "\n");
		writeArrayFieldString(headerFileWriter, "sizes", nrrdInfo.getSizes());
		writeArrayFieldString(headerFileWriter, "spacings", nrrdInfo.getSpacings());
		writeArrayFieldString(headerFileWriter, "aximins", nrrdInfo.getAxismins());
		writeArrayFieldString(headerFileWriter, "axismaxs", nrrdInfo.getAxismaxs());
		writeArrayFieldString(headerFileWriter, "centers", nrrdInfo.getCenters());
		writeArrayFieldString(headerFileWriter, "labels", nrrdInfo.getLabels());
		writeArrayFieldString(headerFileWriter, "units", nrrdInfo.getUnits());
		headerFileWriter.write("min: " + nrrdInfo.getMin() + "\n");
		headerFileWriter.write("max: " + nrrdInfo.getMax() + "\n");
		headerFileWriter.write("lineskip: " + nrrdInfo.getLineskip() + "\n");
		headerFileWriter.write("byteskip: " + nrrdInfo.getByteskip() + "\n");
		if (nrrdInfo.isSeparateHeader()) {
			headerFileWriter.write("datafile: " + nrrdInfo.getCanonicalFilename(false) + "\n");
		} else {
			headerFileWriter.write("\n");
		}
	}finally {
		if(headerFileWriter != null){try{headerFileWriter.close();}catch(Exception e){e.printStackTrace();}}
	}
	// if we didn't want a detached header, append the datafile
	BufferedOutputStream headerdOut = null;
	BufferedInputStream dataIN = null;
	if (! nrrdInfo.isSeparateHeader()) {
		headerdOut = new BufferedOutputStream(new FileOutputStream(fileDataContainerManager.getFile(nrrdInfo.getHeaderFileID()), true));
		dataIN = new BufferedInputStream(new FileInputStream(fileDataContainerManager.getFile(nrrdInfo.getDataFileID())));
		byte[] bytes = new byte[65536];
		try {
			int b = dataIN.read(bytes);
			while (b != -1) {
				headerdOut.write(bytes, 0, b);
				b = dataIN.read(bytes);
			}
			headerdOut.flush();
		}finally {
			if(headerdOut != null){try{headerdOut.close();}catch(Exception e){e.printStackTrace();}}
			if(dataIN != null){try{dataIN.close();}catch(Exception e){e.printStackTrace();}}
		}
	}
	// successful write
	nrrdInfo.setHasData(true);
	return nrrdInfo;
}
}

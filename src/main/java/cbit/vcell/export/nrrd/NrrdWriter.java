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
import java.io.FileWriter;
import java.io.IOException;

import cbit.vcell.export.server.FileDataContainerManager;
public class NrrdWriter {
/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 4:26:18 PM)
 */
private static String getArrayFieldString(String name, double[] data) {
	if (data != null) {
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < data.length; i++){
			bf.append(data[i]);
			bf.append(" ");
		}
		return(name + ": " + bf.toString().trim() + "\n");
	} else {
		return "";
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 4:26:18 PM)
 */
private static String getArrayFieldString(String name, int[] data)  {
	if (data != null) {
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < data.length; i++){
			bf.append(data[i]);
			bf.append(" ");
		}
		return (name + ": " + bf.toString().trim() + "\n");
	} else {
		return "";
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 4:26:18 PM)
 */
private static String getArrayFieldString(String name, String[] data)  {
	if (data != null) {
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < data.length; i++){
			bf.append(data[i]);
			bf.append(" ");
		}
		 return (name + ": " + bf.toString().trim() + "\n");
	} else {
		return "";
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
//		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(), )
//		headerFileWriter = 	new FileWriter(fileDataContainerManager.getFile(nrrdInfo.getHeaderFileID()));
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),NrrdInfo.MAGIC + "\n");
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),"endian: " + NrrdInfo.ENDIAN + "\n");
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),"# " + nrrdInfo.getMainComment() + "\n");
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),"type: " + nrrdInfo.getType() + "\n");
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),"dimension: " + nrrdInfo.getDimension() + "\n");
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),"encoding: " + nrrdInfo.getEncoding() + "\n");
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),getArrayFieldString("sizes", nrrdInfo.getSizes()));
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),getArrayFieldString("spacings", nrrdInfo.getSpacings()));
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),getArrayFieldString("aximins", nrrdInfo.getAxismins()));
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),getArrayFieldString("axismaxs", nrrdInfo.getAxismaxs()));
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),getArrayFieldString("centers", nrrdInfo.getCenters()));
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),getArrayFieldString("labels", nrrdInfo.getLabels()));
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),getArrayFieldString("units", nrrdInfo.getUnits()));
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),"min: " + nrrdInfo.getMin() + "\n");
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),"max: " + nrrdInfo.getMax() + "\n");
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),"lineskip: " + nrrdInfo.getLineskip() + "\n");
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),"byteskip: " + nrrdInfo.getByteskip() + "\n");
		if (nrrdInfo.isSeparateHeader()) {
			fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),"datafile: " + nrrdInfo.getCanonicalFilename(false) + "\n");
		} else {
			fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),"\n");
		}
	}finally {
		if(headerFileWriter != null){try{headerFileWriter.close();}catch(Exception e){e.printStackTrace();}}
	}
	// if we didn't want a detached header, append the datafile
	//BufferedOutputStream headerdOut = null;
	//BufferedInputStream dataIN = null;
	if (! nrrdInfo.isSeparateHeader()) {
		
		fileDataContainerManager.append(nrrdInfo.getHeaderFileID(),nrrdInfo.getDataFileID());
		
//		headerdOut = new BufferedOutputStream(new FileOutputStream(fileDataContainerManager.getFile(nrrdInfo.getHeaderFileID()), true));
//		dataIN = new BufferedInputStream(new FileInputStream(fileDataContainerManager.getFile(nrrdInfo.getDataFileID())));
//		byte[] bytes = new byte[65536];
//		try {
//			int b = dataIN.read(bytes);
//			while (b != -1) {
//				headerdOut.write(bytes, 0, b);
//				b = dataIN.read(bytes);
//			}
//			headerdOut.flush();
//		}finally {
//			if(headerdOut != null){try{headerdOut.close();}catch(Exception e){e.printStackTrace();}}
//			if(dataIN != null){try{dataIN.close();}catch(Exception e){e.printStackTrace();}}
//		}
	}
	// successful write
	nrrdInfo.setHasData(true);
	return nrrdInfo;
}
}

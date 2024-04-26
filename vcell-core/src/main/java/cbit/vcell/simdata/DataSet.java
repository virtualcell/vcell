/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;

import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.SimulationData.SolverDataType;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Vector;

public class DataSet implements java.io.Serializable
{
	private final static Logger lg = LogManager.getLogger(DataSet.class);

   private Vector<DataBlock> dataBlockList;
   private FileHeader fileHeader;
   private String fileName;

DataSet() {
	dataBlockList = new Vector<DataBlock>();
	fileHeader = new FileHeader();
}


/**
 * This method was created by a SmartGuide.
 * @return double[]
 * @param varName java.lang.String
 */
public static double[] fetchSimData(String varName, File file) throws IOException {
	DataSet dataSet = new DataSet();
	dataSet.read(file, null);
	return dataSet.getData(varName, null);
}


/**
 * This method was created by a SmartGuide.
 * @return double[]
 * @param varName java.lang.String
 */
double[] getData(String varName, File zipFile) throws IOException {
	return getData(varName, zipFile, null, null);
}

double[] getData(String varName, File zipFile, Double time, SolverDataType solverDataType) throws IOException {
	double data[] = null;
	if (solverDataType == SolverDataType.MBSData)
	{
		try {
			data = MovingBoundarySimDataReader.readMBSData(fileName, dataBlockList, varName, time);
		} catch(Exception e) {
			throw new IOException(e.getMessage(), e);
		}
	}
	else
	{
		if (zipFile != null && isChombo(zipFile)) {
			try {
				data = ChomboSimDataReader.readHdf5VariableSolution(zipFile, new File(fileName).getName(), varName);
			} catch(Exception e) {
				throw new IOException(e.getMessage(), e);
			}
		}
		else
		{
			for (int i=0;i<dataBlockList.size();i++){
				DataBlock dataBlock = (DataBlock)dataBlockList.elementAt(i);
				if (varName.trim().equals(dataBlock.getVarName().trim())){
					File pdeFile = new File(fileName);
					InputStream is = null;
					long length = 0;
					org.apache.commons.compress.archivers.zip.ZipFile zipZipFile = null;
		            
					if (zipFile == null && !pdeFile.exists()) {
						throw new FileNotFoundException("file "+fileName+" does not exist");
					}
					if (zipFile != null) {
						zipZipFile = openZipFile(zipFile);
						java.util.zip.ZipEntry dataEntry = zipZipFile.getEntry(pdeFile.getName());
						length = dataEntry.getSize();
						is = zipZipFile.getInputStream((ZipArchiveEntry) dataEntry);
					} else {
						length = pdeFile.length();
						is = new FileInputStream(pdeFile);
					}
		
					// read data from zip file
					
					DataInputStream dis = null;
					try {
						BufferedInputStream bis = new BufferedInputStream(is);
						dis = new DataInputStream(bis);
						dis.skip(dataBlock.getDataOffset());
						int size = dataBlock.getSize();
						data = new double[size];
						for (int j = 0; j < size; j++) {
							data[j] = dis.readDouble();
						}
					} finally {
						try {
							if (dis != null) {
								dis.close();
							}
							if (zipZipFile != null) {
								zipZipFile.close();
							}
						} catch (Exception ex) {
							// ignore
						}
					}	
					break;
				}
			}
		}
	}
	if (data == null) {
		throw new IOException("DataSet.getData(), data not found for variable '" + varName + "'");
	}	
	return data;
}




/**
 * This method was created by a SmartGuide.
 * @return double[]
 * @param varName java.lang.String
 */
int getDataLength(String varName) throws IOException {
	for (int i=0;i<dataBlockList.size();i++){
		DataBlock dataBlock = (DataBlock)dataBlockList.elementAt(i);
		if (varName.trim().equals(dataBlock.getVarName().trim())){
			return dataBlock.getSize();
		}
	}
	throw new IOException("DataSet.getData("+varName+"), data not found");
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String[]
 */
String[] getDataNames() {
	String nameArray[] = new String[dataBlockList.size()];
	for (int i=0;i<dataBlockList.size();i++){
		nameArray[i] = ((DataBlock)dataBlockList.elementAt(i)).getVarName();
	}	
	return nameArray;
}


/**
 * This method was created by a SmartGuide.
 * @return int
 */
int getSizeX() {
	return fileHeader.sizeX;
}


/**
 * This method was created by a SmartGuide.
 * @return int
 */
int getSizeY() {
	return fileHeader.sizeY;
}


/**
 * This method was created by a SmartGuide.
 * @return int
 */
int getSizeZ() {
	return fileHeader.sizeZ;
}


/**
 * This method was created by a SmartGuide.
 * @return double[]
 * @param varName java.lang.String
 */
int getVariableTypeInteger(String varName) throws IOException {
	for (int i=0;i<dataBlockList.size();i++){
		DataBlock dataBlock = (DataBlock)dataBlockList.elementAt(i);
		if (varName.trim().equals(dataBlock.getVarName().trim())){
			return dataBlock.getVariableTypeInteger();
		}
	}
	throw new IOException("DataSet.getData("+varName+"), data not found");
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String[]
 */
int[] getVariableTypeIntegers() {
	int typeArray[] = new int[dataBlockList.size()];
	for (int i=0;i<dataBlockList.size();i++){
		typeArray[i] = ((DataBlock)dataBlockList.elementAt(i)).getVariableTypeInteger();
	}	
	return typeArray;
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2004 9:37:26 AM)
 * @return java.util.zip.ZipFile
 */
public static ZipFile openZipFile(File zipFile) throws IOException {
	for (int i = 0; i < 20; i ++) {
		try {
			return new org.apache.commons.compress.archivers.zip.ZipFile(zipFile);
		} catch (java.util.zip.ZipException ex) {			
			if (i < 19) {
				try {
					System.out.println("<ALERT> Failed reading zip file " + zipFile + ", try again");
					Thread.sleep(50);
				} catch (InterruptedException iex) {
				}
			} else {
				throw ex;
			}
		}
	}
	throw new IOException("Opening zip file " + zipFile + " failed");
}


/**
 * This method was created by a SmartGuide.
 * 
 */
void read(File file, File zipFile) throws IOException, OutOfMemoryError {
	read(file, zipFile, null);
}
	
void read(File file, File zipFile, SolverDataType solverDataType) throws IOException, OutOfMemoryError {
	
	ZipFile zipZipFile = null;
	DataInputStream dataInputStream = null;
	try{
		this.fileName = file.getPath();	
		
		if (solverDataType == SolverDataType.MBSData)
		{
			try {
				MovingBoundarySimDataReader.readMBSDataMetadata(fileName, dataBlockList);
			} catch (Exception e) {
				throw new IOException(e.getMessage(),e);
			}
		}
		else
		{
			InputStream is = null;
			long length  = 0;
			
			if (zipFile != null) {
//				System.out.println("DataSet.read() open " + zipFile + " for " + file.getName());
				zipZipFile = openZipFile(zipFile);
				java.util.zip.ZipEntry dataEntry = zipZipFile.getEntry(file.getName());
				is = zipZipFile.getInputStream((ZipArchiveEntry) dataEntry);
				length = dataEntry.getSize();
			} else {		
				if (!file.exists()){
					File compressedFile = new File(fileName+".Z");
					if (compressedFile.exists()){
						Runtime.getRuntime().exec("uncompress "+fileName+".Z");
						file = new File(fileName);
						if (!file.exists()){
							throw new IOException("file "+fileName+".Z could not be uncompressed");
						}	
					}else{
						throw new FileNotFoundException("file "+fileName+" does not exist");
					}
				}	
				System.out.println("DataSet.read() open '" + fileName + "'"); 
				is = new FileInputStream(file);
				length = file.length();
			}
		
			if(is != null && zipFile!=null && isChombo(zipFile)){
				try {
					ChomboSimDataReader.readHdf5SolutionMetaData(is, dataBlockList);
				} catch (Exception e) {
					throw new IOException(e.getMessage(),e);
				}
			}else{
				BufferedInputStream bis = new BufferedInputStream(is);
				dataInputStream = new DataInputStream(bis);
				fileHeader.read(dataInputStream);
				for (int i = 0; i < fileHeader.numBlocks; i++) {
					DataBlock dataBlock = new DataBlock();
					dataBlock.readBlockHeader(dataInputStream);
					dataBlockList.addElement(dataBlock);
				}
			}
		}
	}finally{
		if (dataInputStream != null) {
			try{dataInputStream.close();}catch(Exception e){lg.error(e.getMessage(), e);}
		}
		if (zipZipFile != null) {
			try{zipZipFile.close();}catch(Exception e){lg.error(e.getMessage(), e);}
		}
	}
}

public static boolean isChombo(File zipFile){
	return zipFile.getName().endsWith(".hdf5.zip");
}


	public static void writeNew(File file, String[] varNameArr, VariableType[] varTypeArr, org.vcell.util.ISize size, double[][] dataArr) throws IOException {
	
	FileOutputStream fos = null;
	BufferedOutputStream bos = null;
	DataOutputStream dos = null;

	try {
		fos = new FileOutputStream(file);
		bos = new BufferedOutputStream(fos);
		dos = new DataOutputStream(bos);
		
		FileHeader fileHeader = new FileHeader();
		
		fileHeader.sizeX = size.getX();
		fileHeader.sizeY = size.getY();
		fileHeader.sizeZ = size.getZ();
		fileHeader.numBlocks = varNameArr.length;
		fileHeader.firstBlockOffset = FileHeader.headerSize;
		fileHeader.writeNew(dos);

		int masterOffset = fileHeader.firstBlockOffset;
		masterOffset += DataBlock.blockSize*varNameArr.length;
		for(int i=0;i<varNameArr.length;i+= 1){
			DataBlock dataBlock = 
				DataBlock.createDataBlock(
						varNameArr[i], varTypeArr[i].getType(), dataArr[i].length, masterOffset);
			dataBlock.writeBlockHeaderNew(dos);	   
			masterOffset+= (dataArr[i].length*8);
		}
		for(int i=0;i<varNameArr.length;i+= 1){
			for (int j = 0; j < dataArr[i].length; j ++) {
				dos.writeDouble(dataArr[i][j]);
			}
		}
	} catch (Exception e) {
		if(e instanceof IOException){
			throw (IOException)e;
		}else{
			throw new RuntimeException(e.getMessage(), e);
		}
	}finally{
		try{
			try{if(dos != null){dos.close();}}
				finally{try{if(bos != null){bos.close();}}
					finally{if(fos != null){fos.close();}}}
		}catch(Exception e2){
			System.out.println("Exception: Error closing DataSet.write(...) after failure.");
		}		
	}
}

}

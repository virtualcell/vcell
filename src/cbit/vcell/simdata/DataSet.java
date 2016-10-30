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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
//import java.util.zip.ZipEntry;
import java.util.zip.ZipEntry;
//import java.util.zip.ZipFile;







import javax.swing.tree.DefaultMutableTreeNode;

import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import cbit.vcell.math.Variable;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.SimulationData.SolverDataType;
import cbit.vcell.solvers.CartesianMeshMovingBoundary.MBSDataGroup;
import cbit.vcell.solvers.CartesianMeshMovingBoundary.MSBDataAttribute;
import cbit.vcell.solvers.CartesianMeshMovingBoundary.MSBDataAttributeValue;

public class DataSet implements java.io.Serializable
{

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
			data = readMBSData(varName, time);
		} catch(Exception e) {
			throw new IOException(e.getMessage(), e);
		}
	}
	else
	{
		if (zipFile != null && isChombo(zipFile)) {
			try {
				data = readHdf5VariableSolution(zipFile, new File(fileName).getName(), varName);
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
protected static ZipFile openZipFile(File zipFile) throws IOException {
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
				readMBSDataMetadata();
			} catch (Exception e) {
				throw new IOException(e.getMessage(),e);
			}
		}
		else
		{
			InputStream is = null;
			long length  = 0;
			
			if (zipFile != null) {
				System.out.println("DataSet.read() open " + zipFile + " for " + file.getName());
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
					readHdf5SolutionMetaData(is);
				} catch (Exception e) {
					e.printStackTrace();
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
			try{dataInputStream.close();}catch(Exception e){e.printStackTrace();}
		}
		if (zipZipFile != null) {
			try{zipZipFile.close();}catch(Exception e){e.printStackTrace();}
		}
	}
}

private static boolean isChombo(File zipFile){
	return zipFile.getName().endsWith(".hdf5.zip");
}

private static File createTempHdf5File(InputStream is) throws IOException
{
	OutputStream out = null;
	try{
		File tempFile = File.createTempFile("temp", "hdf5");
		out=new FileOutputStream(tempFile);
		byte buf[] = new byte[1024];
		int len;
		while((len=is.read(buf))>0) {
			out.write(buf,0,len);
		}
		return tempFile;
	}
	finally
	{
		try {
			if (out != null) {
				out.close();
			}
		} catch (Exception ex) {
			// ignore
		}
	}
}

static File createTempHdf5File(ZipFile zipFile, String fileName) throws IOException
{
	InputStream is = null;
	try
	{
		ZipEntry dataEntry = zipFile.getEntry(fileName);
		is = zipFile.getInputStream((ZipArchiveEntry) dataEntry);		
		return createTempHdf5File(is);
	}
	finally
	{
		try
		{
			if (is != null)
			{
				is.close();
			}
		}
		catch (Exception ex)
		{
			// ignore
		}
	}
}


private static File createTempHdf5File(File zipFile, String fileName) throws IOException
{
	ZipFile zipZipFile = null;
	try
	{
		zipZipFile = openZipFile(zipFile);		
		return createTempHdf5File(zipZipFile, fileName);
	}
	finally
	{
		try
		{
			if (zipZipFile != null)
			{
				zipZipFile.close();
			}
		}
		catch (Exception ex)
		{
			// ignore
		}
	}
}

private void readHdf5SolutionMetaData(InputStream is) throws Exception
{
	File tempFile = null;
	FileFormat solFile = null;
	try{
		tempFile = createTempHdf5File(is);
		
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		solFile = fileFormat.createInstance(tempFile.getAbsolutePath(), FileFormat.READ);
		solFile.open();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)solFile.getRootNode();
		Group rootGroup = (Group)rootNode.getUserObject();
		List<HObject> solGroups = rootGroup.getMemberList();

		for (HObject memberGroup : solGroups)
		{
			if (memberGroup instanceof Group && memberGroup.getName().equals("solution"))
			{
				Group solGroup = (Group) memberGroup;
				List<HObject> memberList = solGroup.getMemberList();
				for (HObject member : memberList)
				{
					if (!(member instanceof Dataset)){
						continue;
					}
					Dataset dataset = (Dataset)member;
					String dsname = dataset.getName();
					int vt = -1;
					String domain = null;
					List<Attribute> solAttrList = dataset.getMetadata();
					for (Attribute attr : solAttrList)
					{
						String attrName = attr.getName();
						if(attrName.equals("variable type")){
							Object obj = attr.getValue();
							vt = ((int[])obj)[0];
						} else if (attrName.equals("domain")) {
							Object obj = attr.getValue();
							domain = ((String[])obj)[0];
						}
					}
					long[] dims = dataset.getDims();
					String varName = domain == null ? dsname : domain + Variable.COMBINED_IDENTIFIER_SEPARATOR + dsname;
					dataBlockList.addElement(DataBlock.createDataBlock(varName, vt, (int) dims[0], 0));
				}
				break;
			}
		}
	} finally {
		try {
			if (solFile != null) {
				solFile.close();
			}
			if (tempFile != null) {
				if (!tempFile.delete()) {
					System.err.println("couldn't delete temp file " + tempFile);
				}
			}
		} catch(Exception e) {
			// ignore
		}
	}
}


static double[] readHdf5VariableSolution(File zipfile, String fileName, String varName) throws Exception{
	
	File tempFile = null;
	FileFormat solFile = null;
	try{
		tempFile = createTempHdf5File(zipfile, fileName);
		
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		solFile = fileFormat.createInstance(tempFile.getAbsolutePath(), FileFormat.READ);
		solFile.open();
		if (varName != null)
		{
			String varPath = Hdf5Utils.getVarSolutionPath(varName);
			HObject solObj = FileFormat.findObject(solFile, varPath);
			if (solObj instanceof Dataset)
			{
				Dataset dataset = (Dataset)solObj;
				return (double[]) dataset.read();
			}
		}
	} finally {
		try {
			if (solFile != null) {
				solFile.close();
			}
			if (tempFile != null) {
				if (!tempFile.delete()) {
					System.err.println("couldn't delete temp file " + tempFile.getAbsolutePath());
				}
			}
		} catch(Exception e) {
			// ignore
		}
	}
	return null;
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
		e.printStackTrace();
		if(e instanceof IOException){
			throw (IOException)e;
		}else{
			throw new RuntimeException(e.getMessage());
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

	static double[] readChomboExtrapolatedValues(String varName, File pdeFile, File zipFile) throws IOException {
		double[] data = null;
		if (zipFile != null && isChombo(zipFile)) {
			File tempFile = null;
			FileFormat solFile = null;
			try{
				tempFile = createTempHdf5File(zipFile, pdeFile.getName());
				
				FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
				solFile = fileFormat.createInstance(tempFile.getAbsolutePath(), FileFormat.READ);
				solFile.open();
				data = readChomboExtrapolatedValues(varName, solFile);
			} catch(Exception e) {
				throw new IOException(e.getMessage(), e);
			} finally {
				try {
					if (solFile != null) {
						solFile.close();
					}
					if (tempFile != null) {
						if (!tempFile.delete()) {
							System.err.println("couldn't delete temp file " + tempFile.getAbsolutePath());
						}
					}
				} catch(Exception e) {
					// ignore
				}
			}
		}
		return data;
	}
	
	static double[] readChomboExtrapolatedValues(String varName, FileFormat solFile) throws Exception {
		double data[] = null;
		if (varName != null)
		{
			String varPath = Hdf5Utils.getVolVarExtrapolatedValuesPath(varName);
			HObject solObj = FileFormat.findObject(solFile, varPath);
			if (solObj == null)
			{
				throw new IOException("Extrapolated values for variable '" + varName + "' does not exist in the results.");
			}
			if (solObj instanceof Dataset)
			{
				Dataset dataset = (Dataset)solObj;
				return (double[]) dataset.read();
			}
		}
		return data;
	}
	
	private void readMBSDataMetadata() throws Exception
	{
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		FileFormat solFile = null;
		try {
			solFile = fileFormat.createInstance(fileName, FileFormat.READ);
			solFile.open();
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)solFile.getRootNode();
			Group rootGroup = (Group)rootNode.getUserObject();
			Group solutionGroup = null;
			for (HObject member : rootGroup.getMemberList())
			{
				String memberName = member.getName();
				if (member instanceof Group)
				{
					MBSDataGroup group = MBSDataGroup.valueOf(memberName);
					if (group == MBSDataGroup.Solution)
					{
						solutionGroup = (Group) member;
						break;
					}
				}
			}
			if (solutionGroup == null)
			{
				throw new Exception("Group " + MBSDataGroup.Solution + " not found");
			}
			
			// find any timeGroup
			Group timeGroup = null;
			for (HObject member : solutionGroup.getMemberList())
			{
				String memberName = member.getName();
				if (member instanceof Group && memberName.startsWith("time"))
				{
					timeGroup = (Group) member;
					break;
				}
			}
			
			if (timeGroup == null)
			{
				throw new Exception("No time group found");
			}
			
			// find all the datasets in that time group
			for (HObject member : timeGroup.getMemberList())
			{
				if (member instanceof Dataset)
				{
					List<Attribute> solAttrList = member.getMetadata();
					int size = 0;
					String varName = null;
					VariableType varType = null;
					for (Attribute attr : solAttrList)
					{
						String attrName = attr.getName();
						Object attrValue = attr.getValue();
						if(attrName.equals(MSBDataAttribute.name.name()))
						{
							varName = ((String[]) attrValue)[0];
						}
						else if (attrName.equals(MSBDataAttribute.size.name()))
						{
							size = ((int[]) attrValue)[0];
						}
						else if (attrName.equals(MSBDataAttribute.type.name()))
						{
							String vt = ((String[]) attrValue)[0];
							if (vt.equals(MSBDataAttributeValue.Point.name()))
							{
								varType = VariableType.POINT_VARIABLE;
							}
							else if (vt.equals(MSBDataAttributeValue.Volume.name()))
							{
								varType = VariableType.VOLUME;
							}
							else if (vt.equals(MSBDataAttributeValue.PointSubDomain.name()))
							{
								// Position for PointSubdomain
							}
						}
					}
					if (varType == VariableType.VOLUME)
					{
						// only display volume
						dataBlockList.addElement(DataBlock.createDataBlock(varName, varType.getType(), size, 0));
					}
					if (varType == VariableType.POINT_VARIABLE)
					{
						// only display volume
						dataBlockList.addElement(DataBlock.createDataBlock(varName, varType.getType(), size, 0));
					}
					
				}
			}
		}
		finally
		{
				if (solFile != null)
				{
					try {
						solFile.close();
					} catch (Exception e) {
						// ignore
					}
				}
		}
	}
	
	private double[] readMBSData(String varName, Double time) throws Exception {
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		FileFormat solFile = null;
		double[] data = null;
		try {
			solFile = fileFormat.createInstance(fileName, FileFormat.READ);
			solFile.open();
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)solFile.getRootNode();
			Group rootGroup = (Group)rootNode.getUserObject();
			Group solutionGroup = null;
			for (HObject member : rootGroup.getMemberList())
			{
				String memberName = member.getName();
				if (member instanceof Group)
				{
					MBSDataGroup group = MBSDataGroup.valueOf(memberName);
					if (group == MBSDataGroup.Solution)
					{
						solutionGroup = (Group) member;
						break;
					}
				}
			}
			if (solutionGroup == null)
			{
				throw new Exception("Group " + MBSDataGroup.Solution + " not found");
			}
			
			int varIndex = -1;
			int size = 0;
			for (int i = 0; i < dataBlockList.size(); ++ i)
			{
				DataBlock dataBlock = dataBlockList.get(i);
				if (dataBlock.getVarName().equals(varName))
				{
					varIndex = i;
					size = dataBlock.getSize();
					break;
				}
			}
			
			if (varIndex == -1)
			{
				throw new Exception("Variable " + varName + " not found");
			}
			
			// find time group for that time
			Group timeGroup = null;
			for (HObject member : solutionGroup.getMemberList())
			{
				if (member instanceof Group)
				{
					Group group = (Group)member;
					List<Attribute> dsAttrList = group.getMetadata();
					Attribute timeAttribute = null;
					for (Attribute attr : dsAttrList)
					{
						if (attr.getName().equals(MSBDataAttribute.time.name()))
						{
							timeAttribute = attr;
							break;
						}
					}
					if (timeAttribute != null)
					{
						double t = ((double[]) timeAttribute.getValue())[0];
						if (Math.abs(t - time) < 1e-8)
						{
							timeGroup = group;
							break;
						}
					}
				}
			}
			
			if (timeGroup == null)
			{
				throw new Exception("No time group found for time=" + time);
			}
			
			// find variable dataset
			Dataset varDataset = null;
			for (HObject member : timeGroup.getMemberList())
			{
				if (member instanceof Dataset)
				{
					List<Attribute> dsAttrList = member.getMetadata();
					String var = null;
					for (Attribute attr : dsAttrList)
					{
						if (attr.getName().equals(MSBDataAttribute.name.name()))
						{
							var = ((String[]) attr.getValue())[0];
							break;
						}
					}
					if (var != null && var.equals(varName))
					{
						varDataset = (Dataset) member;
						break;
					}
				}
			}
			if (varDataset == null)
			{
				throw new Exception("Data for Variable " + varName + " at time " + time + " not found");
			}
			
			data = new double[size];
			System.arraycopy((double[])varDataset.getData(), 0, data, 0, size);
			return data;
		}
		finally
		{
				if (solFile != null)
				{
					try {
						solFile.close();
					} catch (Exception e) {
						// ignore
					}
				}
		}		
	}
}

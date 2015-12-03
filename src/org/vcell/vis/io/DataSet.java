/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.vis.io;
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
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;

import javax.swing.tree.DefaultMutableTreeNode;

import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
//import java.util.zip.ZipFile;
import org.apache.commons.compress.archivers.zip.ZipFile;

import cbit.vcell.math.Variable;
import cbit.vcell.math.VariableType;

public class DataSet implements java.io.Serializable
{

   private Vector<DataBlock> dataBlockList;
   private FileHeader fileHeader;
   private String fileName;

   public DataSet() {
	   dataBlockList = new Vector<DataBlock>();
	   fileHeader = new FileHeader();
   }


	public static double[] fetchSimData(String varName, File file) throws IOException {
		DataSet dataSet = new DataSet();
		dataSet.read(file, null);
		return dataSet.getData(varName, null);
	}
	
	public double[] getData(String varName, File zipFile) throws IOException {
		double data[] = null;
		if (zipFile != null && isChombo(zipFile)) {
			throw new RuntimeException("chombo files not read with "+getClass().getName()+", should use Chombo utilities");
		}
		else
		{
			for (int i=0;i<dataBlockList.size();i++){
				DataBlock dataBlock = (DataBlock)dataBlockList.elementAt(i);
				if (varName.trim().equals(dataBlock.getVarName().trim())){
					File pdeFile = new File(fileName);
					InputStream is = null;
					long length = 0;
					ZipFile zipZipFile = null;
		
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
		if (data == null) {
			throw new IOException("DataSet.getData(), data not found for variable '" + varName + "'");
		}	
		return data;
	}
	
	/**
	 * This method was created by a SmartGuide.
	 * @return java.lang.String[]
	 */
	public String[] getDataNames() {
		String nameArray[] = new String[dataBlockList.size()];
		for (int i=0;i<dataBlockList.size();i++){
			nameArray[i] = ((DataBlock)dataBlockList.elementAt(i)).getVarName();
		}	
		return nameArray;
	}
	
	
	private int getVariableTypeInteger(String varName) throws IOException {
		for (int i=0;i<dataBlockList.size();i++){
			DataBlock dataBlock = (DataBlock)dataBlockList.elementAt(i);
			if (varName.trim().equals(dataBlock.getVarName().trim())){
				return dataBlock.getVariableTypeInteger();
			}
		}
		throw new IOException("DataSet.getData("+varName+"), data not found");
	}
	
	private int[] getVariableTypeIntegers() {
		int typeArray[] = new int[dataBlockList.size()];
		for (int i=0;i<dataBlockList.size();i++){
			typeArray[i] = ((DataBlock)dataBlockList.elementAt(i)).getVariableTypeInteger();
		}	
		return typeArray;
	}
	
	private static ZipFile openZipFile(File zipFile) throws IOException {
		for (int i = 0; i < 20; i ++) {
			try {
				return new ZipFile(zipFile);
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
	public void read(File file, File zipFile) throws IOException, OutOfMemoryError {
		
		ZipFile zipZipFile = null;
		DataInputStream dataInputStream = null;
		try{
			this.fileName = file.getPath();	
			
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
		}finally{
			if (dataInputStream != null) {
				try{dataInputStream.close();}catch(Exception e){e.printStackTrace();}
			}
			if (zipZipFile != null) {
				try{zipZipFile.close();}catch(Exception e){e.printStackTrace();}
			}
		}
	}
	
	private boolean isChombo(File zipFile){
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
			Group solGroup = (Group)rootGroup.getMemberList().get(0);
	
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
}

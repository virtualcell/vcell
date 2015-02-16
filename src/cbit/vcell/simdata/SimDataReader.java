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
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import java.util.zip.ZipEntry;

import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.HObject;

import org.vcell.util.DataAccessException;

import cbit.vcell.math.InsideVariable;
import cbit.vcell.math.OutsideVariable;


/**
 * Insert the type's description here.
 * Creation date: (10/24/2004 3:33:16 PM)
 * @author: Frank Morgan
 */
public class SimDataReader{

	private static final int INDEX_ORIGINAL_POSITION = 0;
	private static final int INDEX_VALUE = 1;
	
	private double[] times;
	private boolean[] wantsThisTime;
	private String[] zipFilenNames;
	private String[] simDataFileNames;
	private String[] varNames;
	private int[][] varIndexes;
	private int masterTimeIndex = 0;
	private long masterStreamIndex = 0;
	private org.apache.commons.compress.archivers.zip.ZipFile currentZipFile = null;
	private String currentZipFileName = null;
	private DataInputStream dis = null;
	long[][][] sortedVarIndexes = null;

	private int[][][] reMapper;
	private boolean isChombo = false;

	private ReorderVarIndexes rvi = new ReorderVarIndexes();
	private class ReorderVarIndexes implements java.util.Comparator<long[]>{
		public int compare(long[] o1, long[] o2){
			long[] temp1 = (long[])o1;
			long[] temp2 = (long[])o2;
			return (int)(temp1[1]-temp2[1]);
		}
	};

/**
 * Insert the method's description here.
 * Creation date: (10/24/2004 3:34:31 PM)
 */
public SimDataReader(boolean[] argWantsThisTime,double[] argTimes,String[] argZipFileNames,String[] argSimDataFileNames,
	String[] argVarNames,int[][] argVarIndexes, boolean isChombo){

	// ZipFiles must be stored uncompressed,unencrypted
	// argVarNames can be in any order and with duplicate names
	// argVarIndexes can be in any order and with duplicates
	
	if(argTimes.length != argWantsThisTime.length || (argZipFileNames != null && argTimes.length != argZipFileNames.length) || argTimes.length != argSimDataFileNames.length){
		throw new RuntimeException("WantsTime,Times,ZipFileNames and SimDataNames must be same length");
	}
	
	if(argVarNames.length != argVarIndexes.length){
		throw new RuntimeException("VarNames and VarIndexs must be same length");
	}
	
	times = argTimes;
	wantsThisTime = argWantsThisTime;
	zipFilenNames = argZipFileNames;
	simDataFileNames = argSimDataFileNames;
	this.isChombo = isChombo;

	if (isChombo)
	{
		// no need to order for chombo dataset
		varNames = argVarNames;
		varIndexes = argVarIndexes;
	}
	else
	{
		order(argVarNames,argVarIndexes);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/27/2004 1:48:40 PM)
 */
public void close(){

	String error = "";
	if(currentZipFile != null){
		try{
			currentZipFile.close();
			currentZipFile = null;
			currentZipFileName = null;
		}catch(IOException e){
			error+= "Error closing zipfile "+currentZipFileName.toString()+"\n"+(e.getMessage() != null?e.getMessage():e.getClass().getName());
		}
	}
	
	if(dis != null){
		try{
			dis.close();
			dis = null;
		}catch(IOException e){
			error+="Error closing dataInputStream\n"+(error.length() != 0?"\n":"")+(e.getMessage() != null?e.getMessage():e.getClass().getName());
		}
	}
	if(error.length() != 0){
		throw new RuntimeException(error);
	}
}

private void getNextDataAtCurrentTimeChombo(double[][] returnValues)  throws Exception {
	if (zipFilenNames == null || zipFilenNames[masterTimeIndex] == null) {
		return;
	}
	if (currentZipFile == null || !currentZipFileName.equals(zipFilenNames[masterTimeIndex])) {
		close();
		currentZipFile = new ZipFile(zipFilenNames[masterTimeIndex]);
		currentZipFileName=zipFilenNames[masterTimeIndex];
	}
	File tempFile = null;
	FileFormat solFile = null;
	try {
		tempFile = DataSet.createTempHdf5File(currentZipFile, simDataFileNames[masterTimeIndex]);
		
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		solFile = fileFormat.createInstance(tempFile.getAbsolutePath(), FileFormat.READ);
		solFile.open();
	
		for(int k = 0; k < varNames.length; ++ k) {
			try {
				boolean bExtrapolatedValue = false;
				String varName = varNames[k];
				if (varName.endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX))
				{
					bExtrapolatedValue = true;
					varName = varName.substring(0, varName.lastIndexOf(InsideVariable.INSIDE_VARIABLE_SUFFIX));
				}
				else if (varName.endsWith(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX))
				{
					bExtrapolatedValue = true;
					varName = varName.substring(0, varName.lastIndexOf(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX));
				}
				double[] sol = null;
				if (bExtrapolatedValue)
				{
					sol = DataSet.readChomboExtrapolatedValues(varName, solFile);
				}
				else
				{
					String varPath = Hdf5Utils.getVarSolutionPath(varNames[k]);
					HObject solObj = FileFormat.findObject(solFile, varPath);
					if (solObj instanceof Dataset) {
						Dataset dataset = (Dataset)solObj;
						sol = (double[]) dataset.read();
					}
				}
				if (sol != null)
				{
					for(int l = 0;l < varIndexes[k].length; ++ l) {
						int idx = varIndexes[k][l];					
						double val =  sol[idx];					
						returnValues[k][l] = val;
					}
				}
			} catch (Exception e) {
				e.printStackTrace(System.out);
				throw new DataAccessException(e.getMessage(), e);
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
	
	++ masterTimeIndex;
	if (masterTimeIndex >= times.length) {
		close();
	}
}

/**
 * Insert the method's description here.
 * Creation date: (10/26/2004 10:18:50 AM)
 */
public void getNextDataAtCurrentTime(double[][] returnValues) throws IOException, DataAccessException {
	if (isChombo) {
		try {
			getNextDataAtCurrentTimeChombo(returnValues);
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new DataAccessException(e.getMessage(), e);
		}
	} else {
		getNextDataAtCurrentTime0(returnValues);
	}
}

private void getNextDataAtCurrentTime0(double[][] returnValues) throws IOException,DataAccessException{
	
	if(masterTimeIndex >= times.length){
		throw new RuntimeException("No More Time Data");
	}

//	long currentTimeIndex = masterTimeIndex;

//long beginTime = System.currentTimeMillis();

	try{

		//Setup the proper DataInputStream
		String currentSimDataFileName = simDataFileNames[masterTimeIndex];
		if(zipFilenNames != null && zipFilenNames[masterTimeIndex] != null){
			if(currentZipFile == null || !currentZipFileName.equals(zipFilenNames[masterTimeIndex])){
				if(currentZipFile != null){
					currentZipFile.close();
					currentZipFileName=null;
				}
				currentZipFile = new org.apache.commons.compress.archivers.zip.ZipFile(zipFilenNames[masterTimeIndex]);
				currentZipFileName=zipFilenNames[masterTimeIndex];
			}
			if(dis != null){dis.close();dis = null;}
			ZipEntry ze = currentZipFile.getEntry(currentSimDataFileName);
			if(wantsThisTime[masterTimeIndex]){
				InputStream is = currentZipFile.getInputStream((ZipArchiveEntry) ze);
				//java.io.BufferedInputStream bis = new java.io.BufferedInputStream(is);
				dis = new java.io.DataInputStream(is);
			}
		}else if(wantsThisTime[masterTimeIndex]){
			File file = new File(currentSimDataFileName);
			FileInputStream fis = new java.io.FileInputStream(file);
			dis = new java.io.DataInputStream(new java.io.BufferedInputStream(fis));
		}

//long entryTime = System.currentTimeMillis();
//System.out.println("Time for Entry="+(entryTime-beginTime));

		if(wantsThisTime[masterTimeIndex]){
			masterStreamIndex = 0;
			// Read the SimDataHeader and SimdataBlock Info
			FileHeader fileHeader = readFileHeader(dis);//This will fail if mis-aligned in stream
//System.out.println(currentSimDataFileName+" numBlocks="+fileHeader.numBlocks);
			DataBlock[] dataBlockList = new DataBlock[fileHeader.numBlocks];
			for (int j = 0; j < fileHeader.numBlocks; j++) {
				DataBlock dataBlock = new DataBlock();
				incrementStreamCounts(dataBlock.readBlockHeader(dis));//This will fail if mis-aligned in stream
//System.out.println("varName="+dataBlock.getVarName()+" Offset="+dataBlock.getDataOffset()+" type="+dataBlock.getVariableTypeInteger());
				dataBlockList[j] = dataBlock;
			}
			
//long blockTime = System.currentTimeMillis();
//System.out.println("Time for Blocks="+(blockTime-entryTime));

			// Beginning of All SimData(Blocks) (All doubles) check DataBlocks in order (Always move forward in stream)
			int variableFoundCount = 0;
			for(int j=0;j<dataBlockList.length;j+= 1){
				boolean isBlockIndexAligned = false;
				// See if current block is a variable we want
				for(int k=0;k<varNames.length;k+= 1){
					if(varNames[k].equals(dataBlockList[j].getVarName())){
						variableFoundCount+= 1;
						// Align to beginning of current block
						if(!isBlockIndexAligned){
							skip(dis,dataBlockList[j].getDataOffset()-masterStreamIndex);
							isBlockIndexAligned = true;
						}
						double val = 0;
						for(int l=0;l<varIndexes[k].length;l+= 1){
							long skip = (sortedVarIndexes[k][l][INDEX_VALUE] - (l>0?sortedVarIndexes[k][l-1][INDEX_VALUE]:0));
							// If current data offset was same as last, re-use val
							if((l==0) || (skip != 0)){
								// Align to desired data index
								skip(dis,(l==0?skip:skip-1)*8);
								val = dis.readDouble();
								incrementStreamCounts(8);
							}
							// Store value in original order
							int unSortedVarIndex = (int)sortedVarIndexes[k][l][INDEX_ORIGINAL_POSITION];
							int reMappedVarNameIndex =  reMapper[k][unSortedVarIndex][0];
							int reMappedVarIndexIndex = reMapper[k][unSortedVarIndex][1];
//System.out.println("k="+k+" l="+l+" uvi="+unSortedVarIndex+" rvni="+reMappedVarNameIndex+" rmvii="+reMappedVarIndexIndex);
							returnValues[reMappedVarNameIndex][reMappedVarIndexIndex] = val;
//System.out.println("var="+varNames[k]+" sortindex="+l+
//" unsortindex="+sortedVarIndexes[k][l][INDEX_ORIGINAL_POSITION]+" index="+sortedVarIndexes[k][l][INDEX_VALUE]+" val="+val);
						}
					}
				}
			}
			if(variableFoundCount != varNames.length){
				throw new DataAccessException(this.getClass().getName()+".getNextDataAtCurrentTime: At least 1 variable name was not found in Datablock list");
			}
//long dataTime = System.currentTimeMillis();
//System.out.println("Time for data="+(dataTime-blockTime)+"\n");

		}

		if(dis != null){dis.close();dis = null;}
		
		masterTimeIndex+= 1;
		
		if(masterTimeIndex >= times.length){
			close();
		}
		
//long totalTime = System.currentTimeMillis();
//System.out.println("Total Time="+(totalTime-beginTime));
		
	}catch(Throwable e){
		close();
		throw new RuntimeException("Error reading SimData: "+(e.getMessage() != null?e.getMessage():e.getClass().getName()));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/27/2004 6:43:22 PM)
 * @return boolean
 */
public boolean hasMoreData(){
	
	boolean bHasMoreData = (masterTimeIndex < times.length);
	if(!bHasMoreData){
		close();
	}
	return bHasMoreData;
}


/**
 * Insert the method's description here.
 * Creation date: (10/26/2004 10:52:49 AM)
 * @param incr long
 */
private void incrementStreamCounts(long incr) {

	masterStreamIndex+= incr;
	//currentZipFileOffset+= incr;
	//currentSimDataOffset+= incr;
	//currentDoublesOffset+= incr;
//System.out.println("before="+(currentSimDataOffset-incr)+" delta="+incr+" after="+currentSimDataOffset);

}


/**
 * Insert the method's description here.
 * Creation date: (11/7/2004 10:26:07 AM)
 * @param argVarNames java.lang.String[]
 * @param argVarIndexes int[][]
 */
private void order(String[] argVarNames, int[][] argVarIndexes) {
	
	//Collect equal varNames indexes
	//keep record of their original order
	String[] compactVarNames = new String[0];
	int[][] compactIndexes = new int[0][];
	for(int i=0;i<argVarNames.length;i+= 1){
			int compactVarNameIndex = -1;
			for(int j=0;j<compactVarNames.length;j+= 1){
				if(compactVarNames[j].equals(argVarNames[i])){
					compactVarNameIndex = j;
					break;
				}
			}
			if(compactVarNameIndex == -1){
				String[] temp = new String[compactVarNames.length+1];
				System.arraycopy(compactVarNames,0,temp,0,compactVarNames.length);
				temp[temp.length-1] = argVarNames[i];
				compactVarNames = temp;
				compactVarNameIndex = compactVarNames.length-1;
			}
			//
			if(compactIndexes.length == compactVarNameIndex){
				int[][] temp = new int[compactIndexes.length+1][];
				for(int j=0;j<compactIndexes.length;j+= 1){
					temp[j] = compactIndexes[j];
				}
				compactIndexes = temp;
			}
			int[] temp = null;
			if(compactIndexes[compactVarNameIndex] == null){
				temp = argVarIndexes[i];
			}else{
				temp = new int[compactIndexes[compactVarNameIndex].length+argVarIndexes[i].length];
				System.arraycopy(compactIndexes[compactVarNameIndex],0,temp,0,compactIndexes[compactVarNameIndex].length);
				System.arraycopy(argVarIndexes[i],0,temp,compactIndexes[compactVarNameIndex].length,argVarIndexes[i].length);
			}
			compactIndexes[compactVarNameIndex] = temp;

			int[][][]tempreMapper = new int[compactVarNames.length][0][2];
			for(int j=0;j<compactVarNames.length;j+= 1){
				tempreMapper[j] = new int[compactIndexes[j].length][2];
				for(int k=0;k<compactIndexes[j].length;k+= 1){
					boolean isNew = (j == compactVarNameIndex) && (k >= (compactIndexes[j].length-argVarIndexes[i].length));
					if(!isNew){
						tempreMapper[j][k][0] = reMapper[j][k][0];
					}else{
						tempreMapper[j][k][0] = i;
					}
					//
					if(!isNew){
						tempreMapper[j][k][1] = reMapper[j][k][1];
					}else{
						tempreMapper[j][k][1] = k - (compactIndexes[j].length-argVarIndexes[i].length);
					}
					
				}
			}
			reMapper = tempreMapper;
			
	}
	
	varNames = compactVarNames;
	varIndexes = compactIndexes;

	// Sort indexes so only need to jump forward in SimData stream
	// keep record of their original order
	sortedVarIndexes = new long[varIndexes.length][][];
	for(int i=0;i<varIndexes.length;i+= 1){
		sortedVarIndexes[i] = new long[varIndexes[i].length][2];
		for(int j=0;j<varIndexes[i].length;j+= 1){
			sortedVarIndexes[i][j][INDEX_ORIGINAL_POSITION] = j;
			sortedVarIndexes[i][j][INDEX_VALUE] = varIndexes[i][j];
		}
		Arrays.sort(sortedVarIndexes[i],rvi);
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (10/26/2004 10:50:57 AM)
 * @return cbit.vcell.simdata.FileHeader
 * @param dis java.io.DataInputStream
 */
private FileHeader readFileHeader(DataInputStream dis) throws IOException{
	FileHeader fileHeader = new FileHeader();
	incrementStreamCounts(fileHeader.read(dis));//reads 44 bytes
	return fileHeader;
}


/**
 * Insert the method's description here.
 * Creation date: (10/25/2004 8:24:27 AM)
 * @param skipCount int
 */
private void skip(InputStream is,long skipCount) throws IOException{
	if(skipCount == 0){
		return;
	}
	
	int total = 0;
	int cur = 0;

	while ((total<skipCount) && ((cur = (int) is.skip(skipCount-total)) > 0)) {
	    total += cur;
	}

	incrementStreamCounts(total);
}
}

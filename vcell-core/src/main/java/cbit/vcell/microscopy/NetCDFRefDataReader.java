/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy;

import java.io.IOException;

import ucar.ma2.ArrayDouble;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 * This class basicly reads the NetCDF file  and outputs
 * arries of dimentions, variables and attributes. netcdf-2.2.18.jar 
 * is provided by Unidata as basic APIs for reading and writing NetCDF files.
 * @author Tracy LI
 * Feb 18, 2010
 */

public class NetCDFRefDataReader {
	
	private NetcdfFile ncfile = null;
	private String filename = null;
	/**
	 * The constructor, which opens and .nc file. and output required arraies.
	 * @param String filename: the NetCDF file name.
	 */
	public NetCDFRefDataReader(String fname) throws IOException {
		filename = fname;
		try {
			ncfile = NetcdfFile.open(filename);
		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
		    throw new IOException("Cannot open file:"+filename +"!"+ ioe.getMessage());
		} 
	}
	/**
	 * To close the NetCDFReader
	 */
	public void close() throws IOException{
		if (null != ncfile) try {
	      ncfile.close();
	    } catch (IOException ioe) {
	    	ioe.printStackTrace(System.out);
		    throw new IOException("Cannot close file:"+filename+"!");
	    }
	}
	
	public int getNumTimes()
	{
		ucar.nc2.Dimension dim = ncfile.findDimension("timeDim");
		return dim.getLength();
	}
	
	public int getNumVolumes()
	{
		ucar.nc2.Dimension dim = ncfile.findDimension("volumeDim");
		return dim.getLength();
	}
	
	public int getNumRegions()
	{
		ucar.nc2.Dimension dim = ncfile.findDimension("regionDim");
		return dim.getLength();
	}
	
	public Variable getTime()
	{
		return ncfile.findVariable("t");
	}
	
	public double[] getTimePoints() throws IOException
	{
		if((this != null) && (getTime() != null))
		{
			Variable time = getTime();
			int[] shape = time.getShape();
			double[] timePoints = new double[shape[0]];
			ArrayDouble.D1 data = null;
			try{
				data = (ArrayDouble.D1)time.read();
			}catch(IOException ioe){
				ioe.printStackTrace(System.err);
				throw new IOException("Can not read time points from the model.");
			}
			for(int i=0; i<shape[0]; i++)
			{
				timePoints[i] = data.get(i);
			}
			return timePoints;
		}
		return null;
	}
	
	//regaion variable "fluor_primary_mobile_Convolved"
	//contains two dimensional data, first dimension is time points
	//second dimension is number of regions
	//for reference simulation the regions are 8 rings(stored in colume1 to colume8)
	//plus one more regions(everything beyond 8 rings) stored in colume0.
	public Variable getRegionVariable()
	{
		return ncfile.findVariable("fluor_primary_mobile_Convolved");
	}
	
	public double[] getRegionVar_one(int roiIdx) throws IOException 
	{
		if((this != null) && (getRegionVariable() != null))
		{
			Variable regionVars = getRegionVariable();
			int[] shape = regionVars.getShape();
			ArrayDouble.D2 data = null;
			try{
				data = (ArrayDouble.D2)regionVars.read();
			}catch(Exception e){
				e.printStackTrace(System.err);
				throw new IOException("Can not read species' names from the model.");
			}
			int numTimePoints = shape[0];
			double[] values = new double[numTimePoints];
			for(int i=0; i<numTimePoints; i++)
			{
				values[i]= data.get(i,roiIdx);
			}
//			
//			for(int i=0;i<values.length;i++)
//			{
//				System.out.println(values[i]+"\t");
//			}
			return values;
		}
		return null;
	}
	
	public double[][] getRegionVar() throws IOException 
	{
		if((this != null) && (getRegionVariable() != null))
		{
			Variable regionVars = getRegionVariable();
			int[] shape = regionVars.getShape();
			@SuppressWarnings("unused")
			ArrayDouble.D2 data = null;
			try{
				data = (ArrayDouble.D2)regionVars.read();
			}catch(Exception e){
				e.printStackTrace(System.err);
				throw new IOException("Can not read species' names from the model.");
			}
			int numRois = shape[1];
			int numTimePoints = shape[0];
			double[][] values = new double[numRois][numTimePoints];
			for(int i=0; i<numRois; i++)
			{
				values[i]= getRegionVar_one(i);
			}
//			
//			for(int i=0;i<values.length;i++)
//			{
//				System.out.println(values[i]+"\t");
//			}
			return values;
		}
		return null;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
//		NetCDFRefDataReader ncEva = new NetCDFRefDataReader();
		try
		{
//			ncEva.setNetCDFTarget("C:/trial_10_EM.nc");
		}catch (Exception e) {e.printStackTrace(System.err);}
		
		try
		{
//			ArrayDouble.D2 data = ((ArrayDouble.D2)(ncEva.getTimeSeriesData(0)));
//			int[] shape = data.getShape();
//			for(int i=0;i<shape[1];i++)
//			{
//				double val = data.get(shape[0]-1,i);
//				System.out.println("\t"+val);
//			}
			
//				Array arr_t = ncReader.getReactionStochCoeff().read();
//				if(arr_t instanceof ArrayInt.D2)
//					System.out.println("array type is int with rank 2");
//				
//				System.out.println("array_t rank is"+arr_t.getRank());
//				int[] shape = arr_t.getShape();
//				for(int j=0; j<shape.length; j++) 
//				 System.out.println("array_t shape is"+shape[j]yu7);
//				Array arr = ncReader.getTimeSeriesData(0);
//				if(arr instanceof ArrayDouble)
//					System.out.println("array type is double");
//				if(arr instanceof ArrayFloat)
//					System.out.println("array type is double");
//				if(arr instanceof ArrayDouble.D2)
//					System.out.println("array type is double with rank 2. ");
//				if(arr instanceof ArrayDouble.D3)
//					System.out.println("array type is double with rank 3. ");
//				System.out.println("array rank is "+arr.getRank());
			//NCdump.printArray(arr,"State",System.out,null);
		}catch (Exception e){e.printStackTrace(System.err);}
//		if(ncEva.getNetCDFReader() != null)
//		{
//			try{
//				ncEva.getNetCDFReader().close();
//			}catch(IOException ioe)
//			{
//				ioe.printStackTrace(System.err);
//			}
//		}
	}
}

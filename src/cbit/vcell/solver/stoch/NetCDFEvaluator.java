/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.stoch;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.InvalidRangeException;

/**
 * To get trajetory or the histogram from results which is in a NetCDF format.
 * It is referenced by HybridSolver.java. However, there is a main function attached for testing purpose.
 * Usage: 2 steps. 1) In main function, put file name in ncEva.setNetCDFTarget.
 * 2)Set number of save points (in one run) in ncEva.printHistogram or getTimeSeriesData with specified trial no.
 * The results' distribution will be displayed through console.
 * @author Tracy LI
 * Created June, 2007
 * @version 1.0
 */
public class NetCDFEvaluator {

	NetCDFReader ncreader = null;
	public NetCDFEvaluator() {
		 
	}
	/**
	 * Get NetCDF reader.
	 */
	public NetCDFReader getNetCDFReader()
	{
		return ncreader;
	}
	/**
	 * create a NetCDFReader from a given NetCDF file.
	 * @param String, file name.
	 */
	public void setNetCDFTarget(String fn)
	{
		try 
		{
			ncreader = new NetCDFReader(fn);
		}catch(IOException ioe)
		{
			ioe.printStackTrace(System.out );
			//DialogUtils.showErrorDialog("Can not open NetCDF file:"+fn+"!");
			throw new RuntimeException("Can not open NetCDF file:"+fn+"!");
		} 
		
	}
	/**
	 * Get time series data based on a specific trial number.
	 * The state variable has 3 dimensions. The time series data removes the first dimension
	 * containing only time and species in a specific trial.
	 */
	public Array getTimeSeriesData (int trialNo) throws IOException,InvalidRangeException
	{
		if((ncreader != null) && (ncreader.getState() != null) && (trialNo > 0) && (trialNo <= ncreader.getNumTrials()))
		{
			String readIdx = (trialNo-1)+":"+(trialNo-1)+":"+"1,"+"0:"+(ncreader.getNumTimePoints()-1)+":1,"+"0:"+(ncreader.getNumSpecies()-1)+":1";
			System.out.println(readIdx);
			try{
				Array result = ncreader.getState().read(readIdx).reduce();
				return result;
			}catch(IOException e)
			{
				e.printStackTrace(System.err);
			    throw new IOException("Unable to read variable "+ncreader.getState().getName()+"!");
			}
		}
		return null;
	}
	/**
	 * Get Data series based on a specific time point over trials.
	 */
	public Array getDataOverTrials (int timePointNo) throws IOException,InvalidRangeException
	{
		if((ncreader != null) && (ncreader.getState() != null) && (timePointNo>=0) && (timePointNo < ncreader.getNumTimePoints()))
		{
			String readIdx = 0+":"+(ncreader.getNumTrials()-1)+":"+"1,"+timePointNo+":"+timePointNo+":1,"+"0:"+(ncreader.getNumSpecies()-1)+":1";
			System.out.println(readIdx);
			try{
				Array result = ncreader.getState().read(readIdx).reduce();
				return result;
			}catch(IOException e)
			{
				e.printStackTrace(System.err);
			    throw new IOException("Unable to read variable "+ncreader.getState().getName()+"!");
			}
		}
		return null;
	}
	public void printAllHistograms(int timePointNo) throws IOException
	{
		if(ncreader != null)
		{
			try
			{
				//get all species' names
				String[] names = ncreader.getSpeciesNames_val();
				//get the data for all species at a specific time point
				ArrayDouble data = (ArrayDouble)getDataOverTrials(timePointNo);
				//shape[0]:num of trial, shape[1]: num of species
				int[] shape = data.getShape();
                
				if(shape.length == 1) //one species
				{
					ArrayDouble.D1 temData = (ArrayDouble.D1)data;
					System.out.println(names[0]+":");
					//get one specie's values at a specific time point over trials
					double[] val = new double[shape[0]];
					for(int j=0;j<shape[0];j++)
					{
						val[j] = temData.get(j);
						//System.out.println("\t"+val[j]);
					}
					Point2D[] histogram = generateHistogram(val);
					for(int k=0;k<histogram.length;k++)
					{
						System.out.println(histogram[k].getX()+"\t"+histogram[k].getY());
					}
				}
				
				if(shape.length == 2) //more than one species
				{
					ArrayDouble.D2 temData = (ArrayDouble.D2)data;
					for(int i=0;i<shape[1];i++)//go through species one by one
					{
						System.out.println(names[i]+":");
						//get one specie's values at a specific time point over trials
						double[] val = new double[shape[0]];
						for(int j=0;j<shape[0];j++)
						{
							val[j] = temData.get(j,i);
							//System.out.println("\t"+val[j]);
						}
						Point2D[] histogram = generateHistogram(val);
						for(int k=0;k<histogram.length;k++)
						{
							System.out.println(histogram[k].getX()+"\t"+histogram[k].getY());
						}
					}
				}
			}catch(Exception e)
			{
				e.printStackTrace(System.err);
				throw new IOException("Can not get species' names from model.");
			}
		}
	}
	
	/*
	 * To get a hash table with keys as possible results for a specific variable after certain time period
	 * and the values as the frequency. It is sorted ascendantly.
	 */
	private Point2D[] generateHistogram(double[] rawData)
	{
		Hashtable<Integer,Integer> temp = new Hashtable<Integer,Integer>();
		//sum the results for a specific variable after multiple trials.
		for(int i=0;i<rawData.length;i++)
		{
			int val = ((int)Math.round(rawData[i]));
			if(temp.get(new Integer(val))!= null)
			{
				int v = temp.get(new Integer(val)).intValue();
				temp.put(new Integer(val), new Integer(v+1));
			}
			else temp.put(new Integer(val), new Integer(1));
		}
		//sort the hashtable ascendantly and also calculate the frequency in terms of percentage.
		Vector keys = new Vector(temp.keySet());
		Collections.sort(keys);
		Point2D[] result = new Point2D[keys.size()];
		for (int i=0; i<keys.size(); i++)
		{
	        Integer key = (Integer)keys.elementAt(i);
	        Double valperc = new Double(((double)temp.get(key).intValue())/((double)rawData.length));
	        result[i] = new Point2D.Double(key,valperc);
	    }
		return result;
	}
	/**
	 * @param args
	 */
	//need to put specific NETCDF file name and last time point number(e.g No.50 means there are total 51 time points) for printAllHistogram().
	public static void main(String[] args) {
		NetCDFEvaluator ncEva = new NetCDFEvaluator();
		try
		{
			ncEva.setNetCDFTarget("C:/sim1.nc");
		}catch (Exception e) {e.printStackTrace(System.err);}
		
//		try
//		{
//			ArrayDouble.D2 data = ((ArrayDouble.D2)(ncEva.getTimeSeriesData(0)));
//			int[] shape = data.getShape();
//			for(int i=0;i<shape[1];i++)
//			{
//				double val = data.get(shape[0]-1,i);
//				System.out.println("\t"+val);
//			}
//		}catch (Exception e){e.printStackTrace(System.err);}
//		
//		try
//		{
//			ArrayDouble.D2 data = ((ArrayDouble.D2)(ncEva.getDataOverTrials(10)));
//			int[] shape = data.getShape();
//			for(int i=0;i<shape[0];i++)
//			{
//				for(int j=0;j<shape[1];j++)
//				{
//					double val = data.get(i,j);
//					System.out.println("\t"+val);
//				}
//				System.out.println("\n");
//			}
//		}catch (Exception e){e.printStackTrace(System.err);}
		try{
			ncEva.printAllHistograms(20);
		}catch(IOException e)
		{
			e.printStackTrace(System.err);
			throw new RuntimeException(e.getMessage());
		}
		if(ncEva.getNetCDFReader() != null)
		{
			try{
				ncEva.getNetCDFReader().close();
			}catch(IOException ioe)
			{
				ioe.printStackTrace(System.err);
			}
		}

	}

}

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

import java.io.IOException;

import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 * This class basicly reads the NetCDF file  and outputs
 * arries of dimentions, variables and attributes. netcdf-2.2.18.jar 
 * is provided by Unidata as basic APIs for reading and writing NetCDF files.
 * @author Tracy LI
 * April 30, 2007
 * @version 1.0
 */

public class NetCDFReader {
	
	private NetcdfFile ncfile = null;
	private String filename = null;
	/**
	 * The constructor, which opens and .nc file. and output required arraies.
	 * @param String filename: the NetCDF file name.
	 */
	public NetCDFReader(String fname) throws IOException {
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
	/**
	 * Get number of trials in the model file.
	 * @return int, number of trials
	 */
	public int getNumTrials()
	{
		ucar.nc2.Dimension dim = ncfile.findDimension("NumTrials");
		return dim.getLength();
	}
	
	public int[] getTrialNumbers()
	{
		int totalNum = getNumTrials();
		int[] result=new int[totalNum];
		for(int i=1; i< totalNum+1; i++)
			result[i-1]=i;
		return result;
	}
	/**
	 * Get number of Species in the model file.
	 * @return int, number of species
	 */
	public int getNumSpecies()
	{
		ucar.nc2.Dimension dim = ncfile.findDimension("NumSpecies");
		return dim.getLength();
	}
	/**
	 * Get number of Reactions in the model file.
	 * @return int, number of reactions
	 */
	public int getNumReactions()
	{
		ucar.nc2.Dimension dim = ncfile.findDimension("NumReactions");
		return dim.getLength();
	}
	/**
	 * Get number of models currently in the NetCDF file.
	 * @return int, number of models.
	 */
	public int getNumModels_Dimension()
	{
		ucar.nc2.Dimension dim = ncfile.findDimension("NumModels");
		return dim.getLength();
	}
	/**
	 * Get number of maximum number of species/kinetic parameters in each rate law (default is 6).
	 * @return int, maximum number of parameters
	 */
	public int getNumMaxDepList()
	{
		ucar.nc2.Dimension dim = ncfile.findDimension("NumMaxDepList");
		return dim.getLength();
	}
	/**
	 * Get number of maximum number of species/stoichiometric coefficients in each reaction (default is 25)
	 * Used as dimension in Reaction_StoichCoeff(stoichiometries for species in a specific reaction), also
	 * used in Reaction_StoichSpecies(species involoved in a reaction, represented by their indices(starts from 1)
	 * in Species_names)
	 * @return int, mmaximum number of species/stoichiometric coefficients
	 */
	public int getNumMaxStoichList()
	{
		ucar.nc2.Dimension dim = ncfile.findDimension("NumMaxStoichList");
		return dim.getLength();
	}
	/**
	 * Get number of time points in the model file.
	 * @return int. time points
	 */
	public int getNumTimePoints()
	{
		ucar.nc2.Dimension dim = ncfile.findDimension("NumTimePoints");
		return dim.getLength();
	}
	/**
	 * Get state, which is actually the simulation results. The results can be represented as
	 * 3 dimensional array State(NumTrials, NumTimePoints, NumSpecies).
	 */
	public Variable getState()
	{
		return ncfile.findVariable("State");
	}
	/**
	 * Get starting time. 0 dimension.
	 * @return double, starting time.
	 */
	public double getStartTime() throws IOException
	{
		Variable v = ncfile.findVariable("TStart");
		Double val = 0.0;
		try
		{
			val = v.readScalarDouble();
		}catch (IOException ioe)
		{
			ioe.printStackTrace(System.err);
			throw new IOException("Cannot get proper starting time from the model.");
		}
		return val;
	}
	/**
	 * Get ending time. 0 dimenstion
	 * @return double, ending time.
	 */
	public double getEndTime() throws IOException
	{
		Variable v = ncfile.findVariable("TEnd");
		Double val = 0.0;
		try
		{
			val = v.readScalarDouble();
		}catch (IOException ioe)
		{
			ioe.printStackTrace(System.err);
			throw new IOException("Cannot get proper ending time from the model.");
		}
		return val;
	}
	/**
	 * Get save period, which is the time interval for saving the data. 0 dimension.
	 * @return double, save period.
	 */
	public double getSavePeriod() throws IOException
	{
		Variable v = ncfile.findVariable("SaveTime");
		Double val = 0.0;
		try
		{
			val = v.readScalarDouble();
		}catch (IOException ioe)
		{
			ioe.printStackTrace(System.err);
			throw new IOException("Cannot get proper save period from the model.");
		}
		return val;
	}
	/**
	 * Get volume.
	 * @return double, volume.
	 */
	public double getVolume() throws IOException
	{
		Variable v = ncfile.findVariable("Volume");
		Double val = 0.0;
		try
		{
			val = v.readScalarDouble();
		}catch (IOException ioe)
		{
			ioe.printStackTrace(System.err);
			throw new IOException("Cannot get proper volume from the model.");
		}
		return val;
	}
	/**
	 * Get cell growth time, which is the average amount of time between cell divisions.
	 * @return double, cell growth time.
	 */
	public double getCellGrowthTime() throws IOException
	{
		Variable v = ncfile.findVariable("CellGrowthTime");
		Double val = 0.0;
		try
		{
			val = v.readScalarDouble();
		}catch (IOException ioe)
		{
			ioe.printStackTrace(System.err);
			throw new IOException("Cannot get proper cell growth time from the model.");
		}
		return val;
	}
	/**
	 * Get cell growth time SD, which is the standard deviation of the time between cell divisions.
	 * @return double, standard deviation of cell growth time.
	 */
	public double getCellGrowthTimeSD() throws IOException
	{
		Variable v = ncfile.findVariable("CellGrowthTimeSD");
		Double val = 0.0;
		try
		{
			val = v.readScalarDouble();
		}catch (IOException ioe)
		{
			ioe.printStackTrace(System.err);
			throw new IOException("Cannot get proper standard deviation of the time between cell divisions from the model.");
		}
		return val;
	}
	/**
	 * Get experiment time. 
	 * ExpType = 1; This indicates that this NetCDF file is a 'Single model' file. 
	 * ExpType = 2; This indicates that this NetCDF file is a 'Multi-model' file.
	 * @return int, experiment type.
	 */
	public int getExperimentType() throws IOException
	{
		Variable v = ncfile.findVariable("ExpType");
		int val = 0;
		try
		{
			val = v.readScalarInt();
		}catch (IOException ioe)
		{
			ioe.printStackTrace(System.err);
			throw new IOException("Cannot get proper experiment type from the model.");
		}
		return val;
	}
	/**
	 * Get last trial.
	 * @return int. last trial.
	 */
	public int getLastTrial() throws IOException
	{
		Variable v = ncfile.findVariable("LastTrial");
		int val = 0;
		try
		{
			val = v.readScalarInt();
		}catch (IOException ioe)
		{
			ioe.printStackTrace(System.err);
			throw new IOException("Cannot get proper last trial number from the model.");
		}
		return val;
	}
	/**
	 * Get last model.
	 * @return int. last model.
	 */
	public int getLastModel() throws IOException
	{
		Variable v = ncfile.findVariable("LastModel");
		int val = 0;
		try
		{
			val = v.readScalarInt();
		}catch (IOException ioe)
		{
			ioe.printStackTrace(System.err);
			throw new IOException("Cannot get proper last model number from the file.");
		}
		return val;
	}
	/**
	 * Get max number of models.
	 * @return int. max number of models.
	 */
	public int getMaxNumModels() throws IOException
	{
		Variable v = ncfile.findVariable("MaxNumModels");
		int val = 0;
		try
		{
			val = v.readScalarInt();
		}catch (IOException ioe)
		{
			ioe.printStackTrace(System.err);
			throw new IOException("Cannot get proper max number of models from the file.");
		}
		return val;
	}
	/**
	 * Get Number of Models.
	 * @return int. number of models.
	 */
	public int getNumModels_Variable() throws IOException
	{
		Variable v = ncfile.findVariable("NumModels");
		int val = 0;
		try
		{
			val = v.readScalarInt();
		}catch (IOException ioe)
		{
			ioe.printStackTrace(System.err);
			throw new IOException("Cannot get proper variable of number of models from the file.");
		}
		return val;
	}
	public Variable getSpeciesNames()
	{
		return ncfile.findVariable("Species_names");
	}
	/**
	 * Get the real string of species' names. The species' names are stored
	 * in a 2 dimentional structure. First dimension is the number of speceis
	 * and the second dimension is the allowed string length of the names(default=25). 
	 * @return String[], the list of the species' names in the model.
	 */
	public String[] getSpeciesNames_val() throws IOException 
	{
		if((this != null) && (getSpeciesNames() != null))
		{
			Variable snames = getSpeciesNames();
			int[] shape = snames.getShape();
			String[] result = new String[shape[0]];
			ArrayChar.D2 data = null;
			try{
				data = (ArrayChar.D2)snames.read();
			}catch(Exception e){
				e.printStackTrace(System.err);
				throw new IOException("Can not read species' names from the model.");
			}
			for(int i=0;i<shape[0];i++)
			{
				char[] name = new char[shape[1]];
				for(int j=0;j<shape[1];j++)
				{
					name[j]= data.get(i,j);
				}
				result[i]= new String(name).trim();
			}
//			
//			for(int i=0;i<result.length;i++)
//			{
//				System.out.println(result[i]+"\t");
//			}
			return result;
		}
		return null;
	}
	public Variable getTime()
	{
		return ncfile.findVariable("Time");
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
	public Variable getReactionStochCoeff()
	{
		return ncfile.findVariable("Reaction_StoichCoeff");
	}
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		NetCDFEvaluator ncEva = new NetCDFEvaluator();
		try
		{
			ncEva.setNetCDFTarget("C:/trial_10_EM.nc");
		}catch (Exception e) {e.printStackTrace(System.err);}
		
		try
		{
			ArrayDouble.D2 data = ((ArrayDouble.D2)(ncEva.getTimeSeriesData(0)));
			int[] shape = data.getShape();
			for(int i=0;i<shape[1];i++)
			{
				double val = data.get(shape[0]-1,i);
				System.out.println("\t"+val);
			}
			
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

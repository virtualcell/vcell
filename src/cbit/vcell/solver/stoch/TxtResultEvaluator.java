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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;
/**
 * To evaluate the distribution of results from a text file.
 * In main function, use evaluator.getResults to get the distribution.
 * File name and total trials number are required as input paras.
 * Users can get distribution from console.
 * @author Tracy LI
 *
 */
public class TxtResultEvaluator {

	double[][] results = null;
	String[] names = null;
	public TxtResultEvaluator() {
		 
	}
	
	public void getResults(String filename, int numTrials)
	{
		 
		File fin=new File(filename);
		FileReader fr = null;
		try{
			fr=new java.io.FileReader(fin);
		}catch(FileNotFoundException e)
		{
			e.printStackTrace(System.err);
			throw new RuntimeException(e.getMessage());
		}
		BufferedReader br=new BufferedReader(fr);
		String str = null;
		try{
			str = br.readLine();
			//System.out.println("first line:" + str);
			//save the names
			String[] temp = str.split(":");
			names = new String[temp.length-1];
			for(int i=1;i<temp.length;i++)
			{
				names[i-1]=temp[i];
			}
			int idx = 0;
			results = new double[numTrials][temp.length-1];
			//save datas
			while((str = br.readLine())!=null)
			{
				//System.out.println("data:"+str);
				temp = str.split("\t");
				for(int j=0; j<names.length; j++)
				{
					results[idx][j] = Double.parseDouble(temp[j+1].trim());
					//System.out.print("\t"+results[idx][j]);
				}
				//System.out.println();
				idx++;
			}
		}catch(IOException e)
		{
			e.printStackTrace(System.err);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public void printAllHistograms()
	{
		if(names != null && results != null)
		{
			for(int i=0; i<names.length; i++)
			{
				System.out.println(names[i]+":");
				int trials = results.length;
				// get one specie's values over trials
				double[] val = new double[trials];
				for(int j=0;j<trials;j++)
				{
					val[j] = results[j][i];
					//System.out.println("\t"+val[j]);
				}
				Point2D[] histogram = generateHistogram(val);
				for(int k=0;k<histogram.length;k++)
				{
					System.out.println(histogram[k].getX()+"\t"+histogram[k].getY());
				}
			}
		}
	}
	
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
	public static void main(String[] args) {
		TxtResultEvaluator evaluator = new TxtResultEvaluator();
		evaluator.getResults("C:/Hy3S_2/TestCases/HMM/VCell_SSA/trial30000_out.txt",30000 );
		evaluator.printAllHistograms();
	}

}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

/*
 * The class is made to calculate simple statistics for an array of doubles.
 * The statistics include Mean, Median, mode(s), Min, Max, and Standard Deviation.
 * author: Tracy Li
 * version: 1.1
 */
public class DescriptiveStatistics 
{
	public static final String MEAN_NAME = "Mean";
	public static final String STANDARD_DEVIATION_NAME = "Standard Deviation";
	public static final String MEDIAN_NAME = "Median";
	public static final String MODE_NAME = "Mode";
	public static final String MIN_NAME = "Minimum";
	public static final String MAX_NAME = "Maximum";
	private double min = 0;
	private double max = 0;
	private double mean = 0;
	private double median = 0;
	private Hashtable<Double,Integer> mode = null;
	private double standardDeviation = 0;
	

	public static DescriptiveStatistics CreateBasicStatistics(double[] array)
	{
		DescriptiveStatistics stat = new DescriptiveStatistics();
		if(array != null && array.length > 0)
		{
			Arrays.sort(array);
			//min, max, median
			stat.setMin(array[0]);
			stat.setMax(array[array.length - 1]);
			if((array.length % 2) != 0 )
			{
				stat.setMedian(array[(array.length/2)]);
			}
			else
			{
				stat.setMedian((array[(array.length/2)-1]+ array[array.length/2])/2);
			}
			//mean
			double average = 0;
			for(double d : array)
			{
				average = average + d;
				
			}
			average = (average*1.0)/array.length;
			stat.setMean(average);
			//mode
			Hashtable<Double,Integer> modeHash = new Hashtable<Double, Integer>();
			int currentModeCount = 1;
			int maxModeCount = 1;
			modeHash.put(array[0], maxModeCount);
			for(int i=1; i<array.length; i++)
			{
				if(array[i] == array[i-1])
				{
					currentModeCount ++;
				}
				else
				{
					if(currentModeCount == maxModeCount)
					{
						modeHash.put(array[i-1],currentModeCount);
					}
					else if(currentModeCount > maxModeCount)
					{
						modeHash.clear();
						modeHash.put(array[i-1], currentModeCount);
						maxModeCount = currentModeCount;
					}
					currentModeCount = 1;
				}
			}
			//for the last element in the array
			if(currentModeCount == maxModeCount)
			{
				modeHash.put(array[array.length-1],currentModeCount);
			}
			else if(currentModeCount > maxModeCount)
			{
				modeHash.clear();
				modeHash.put(array[array.length-1], currentModeCount);
				maxModeCount = currentModeCount;
			}
			stat.setMode(modeHash);
			//standard deviation
			double std = 0;
			if(array.length >1)
			{
				for( double d: array)
				{
					std = std + ( Math.pow( ( d - average ), 2) / (array.length - 1) );
				}
				std = Math.sqrt( std );
			}
			stat.setStandardDeviation(std);

		}
		return stat;
	}
	
	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getMean() {
		return mean;
	}

	public void setMean(double average) {
		this.mean = average;
	}

	public double getMedian() {
		return median;
	}

	public void setMedian(double median) {
		this.median = median;
	}

	public Hashtable<Double,Integer> getMode() {
		return mode;
	}

	public void setMode(Hashtable<Double,Integer> mode) {
		this.mode = mode;
	}
	
	public double getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation(double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

	public static void main(String[] args)
	{
		//try mode
		double[] test = new double[]{1,1,1,3,6,7,9,9,9};
		DescriptiveStatistics ds = CreateBasicStatistics(test);
		Hashtable<Double,Integer> testHash = ds.getMode();
		Enumeration<Double> enu = testHash.keys();
		while(enu.hasMoreElements())
		{
			Double d = enu.nextElement();
			System.out.println(((Double)d).doubleValue() +"   :"+testHash.get(d));
			
		}
	}
}

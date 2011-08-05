/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization;

import org.vcell.util.NumberUtils;
/*
 * The class defines a confidence interval. A CI should have a lowerbound
 * and a upperbound. Whether the two bounds are included or not should also be defined.
 * author: Tracy Li
 * version: 1.1
 */
public class ConfidenceInterval 
{
	//Confidence levels constants 
	public static final int NUM_CONFIDENCE_LEVELS = 4;
	public static final int IDX_DELTA_ALPHA_80 = 0;
	public static final int IDX_DELTA_ALPHA_90 = 1;
	public static final int IDX_DELTA_ALPHA_95 = 2;
	public static final int IDX_DELTA_ALPHA_99 = 3;
	public static final String[] CONFIDENCE_LEVEL_NAME = new String[]{"80% confidence", "90%confidence", "95%confidence", "99%confidence"};
	public static final double[] DELTA_ALPHA_VALUE = new double[]{1.642, 2.706, 3.841, 6.635};
	
	double lowerBound = Double.NEGATIVE_INFINITY;
	boolean bLowerBoundOpen = true;
	double upperBound = Double.POSITIVE_INFINITY;
	boolean bUpperBoundOpen = true;
	
	public ConfidenceInterval(double lowerBound, boolean bLowerBoundOpen, double upperBound, boolean bUpperBoundOpen)
	{
		this.lowerBound = lowerBound;
		this.bLowerBoundOpen = bLowerBoundOpen;
		this.upperBound = upperBound;
		this.bUpperBoundOpen = bUpperBoundOpen;
	}
	
	public double getLowerBound() {
		return lowerBound;
	}

	public boolean isLowerBoundOpen() {
		return bLowerBoundOpen;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public boolean isUpperBoundOpen() {
		return bUpperBoundOpen;
	}
	
	public String toString()
	{
		String result = "";
		if(bLowerBoundOpen)//close lower bound
		{
			result = result + "(";
		}
		else
		{
			result = result + "[";
		}
		if(lowerBound == Double.NEGATIVE_INFINITY)
		{
			result = result + "-\u221E, ";
		}
		else
		{
			result = result + NumberUtils.formatNumber(lowerBound, 5) + ", ";
		}
		if(upperBound == Double.POSITIVE_INFINITY)
		{
			result = result + "+\u221E";
		}
		else
		{
			result = result + NumberUtils.formatNumber(upperBound, 5);
		}
		if(bUpperBoundOpen)//close upper bound
		{
			result = result + ")";
		}
		else
		{
			result = result + "]";
		}
		return result; 
	}
}

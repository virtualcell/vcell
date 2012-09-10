/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.opt;

import java.io.Serializable;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;
/**
 * Assign different weights for data at different time.
 * Can be used for SimpleReferenceData, SpatialReferenceData
 * @author Tracy Li
 */
@SuppressWarnings("serial")
public class TimeWeights implements Weights, Serializable {

	double[] weights = null;
	
	public TimeWeights(double[] argTimeWeights)
	{
		this.weights = argTimeWeights;
	}
	     
	public TimeWeights(TimeWeights argTimeWeights)
	{
		this.weights = argTimeWeights.getWeightData().clone();
	}
	
	public int getNumWeights()
	{
		return weights.length;
	}

	public double getWeightByTimeIdx(int timeIndex)
	{
		return weights[timeIndex];
	}
	
	public double[] getWeightData()
	{
		return weights;
	}
	
	public boolean compareEqual(Matchable obj) {
		if(obj != null && obj instanceof TimeWeights)
		{
			TimeWeights otherTimeWeights = (TimeWeights)obj;
			if(getWeightData() != null && otherTimeWeights.getWeightData() != null)
			{
				return Compare.isEqual(getWeightData(), otherTimeWeights.getWeightData());
			}
		}
		return false;
	}
		
}

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

@SuppressWarnings("serial")
public class ElementWeights implements Weights, Serializable {
	double[][] weights = null;
	//first dimension is number of rows, second dimension is number of columns
	public ElementWeights(double[][] argElementWeights)
	{
		this.weights = argElementWeights;
	}
	
	public ElementWeights(ElementWeights argElementWeights)
	{
		double[][] argWeightData = argElementWeights.getWeightData();
		this.weights = new double[argWeightData.length][];
		for(int i=0; i<argWeightData.length; i++)
		{
			weights[i] = argWeightData[i].clone();
		}
	}
	
	public int getNumWeights() {
		if(weights != null && weights.length > 0 && weights[0] != null)
		{
			return weights.length * weights[0].length;
		}
		return 0;
	}

	public double getWeight(int timeIndex, int varIndex) {
		return weights[timeIndex][varIndex];
	}

	public double[][] getWeightData()
	{
		return weights;
	}
	
	public boolean compareEqual(Matchable obj) {
		if(obj == null || !(obj instanceof ElementWeights) || weights == null || ((ElementWeights)obj).getWeightData() == null)
		{
			return false;
		}
		ElementWeights otherEleWeights = (ElementWeights)obj;
		if(weights.length != otherEleWeights.getWeightData().length)
		{
			return false;
		}
		for (int i = 0; i < weights.length; i++){
			double[] thisData = weights[i];
			double[] otherData = otherEleWeights.getWeightData()[i];
			if (!Compare.isEqual(thisData,otherData)){
				return false;
			}
		}
		return true;
	}
	
}
